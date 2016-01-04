package com.ganxin.zeromusic.common.http.volleyImage;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.ganxin.zeromusic.common.http.volleyHelper.VolleyHelper;

/**
 * Implementation of volley's ImageCache interface. This manager tracks the
 * application image loader and cache.
 * 
 * Volley recommends an L1 non-blocking cache which is the default MEMORY
 * CacheType.
 * 
 * @author Trey Robinson
 * 
 */
public class ImageCacheManager {

	/**
	 * 单例异步锁，用于维持线程安全
	 */
	private static final Object SYNC_LOCK = new Object();

	/**
	 * Volley recommends in-memory L1 cache but both a disk and memory cache are
	 * provided. Volley includes a L2 disk cache out of the box but you can
	 * technically use a disk cache as an L1 cache provided you can live with
	 * potential i/o blocking.
	 * 
	 */
	public enum CacheType {
		DISK, MEMORY // 本地,内存
	}

	private static ImageCacheManager mInstance;

	/**
	 * Volley image loader
	 */
	private ImageLoader mImageLoader;

	/**
	 * Image cache implementation
	 */
	private ImageCache mImageCache;
	
	private static final String DISK_CACHE_FOLDER ="zero"; 
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 12; // 12MB

	/**
	 * @return instance of the cache manager
	 */
	public static ImageCacheManager getInstance() {
		if (mInstance == null) {
			// 加了一层锁用于维持线程安全
			synchronized (SYNC_LOCK) {
				if (mInstance == null) {
					mInstance = new ImageCacheManager();
				}
			}
		}
		return mInstance;
	}

	/**
	 * Initializer for the manager. Must be called prior to use.
	 * 
	 * @param context
	 *            application context
	 * @param uniqueName
	 *            name for the cache location
	 * @param cacheSize
	 *            max size for the cache
	 * @param compressFormat
	 *            file type compression format.
	 * @param quality
	 */
	// 我修改这个初始化的方法，使其变为静态方法，增加了整体代码的规范程度，但是通用性下降了
	public static void init(Context context) {
		getInstance().init(context,DISK_CACHE_FOLDER,
				DISK_CACHE_SIZE, CompressFormat.PNG, 100, CacheType.DISK);
	}

	/**
	 * Initializer for the manager. Must be called prior to use.
	 * 
	 * @param context
	 *            application context
	 * @param uniqueName
	 *            name for the cache location
	 * @param cacheSize
	 *            max size for the cache
	 * @param compressFormat
	 *            file type compression format.
	 * @param quality
	 */
	public void init(Context context, String uniqueName, int cacheSize,
			CompressFormat compressFormat, int quality, CacheType type) {
		switch (type) {
		case DISK:
			mImageCache = new DiskLruImageCache(context, uniqueName, cacheSize,
					compressFormat, quality);
			break;
		case MEMORY:
			mImageCache = new BitmapLruImageCache(cacheSize);
		default:
			mImageCache = new BitmapLruImageCache(cacheSize);
			break;
		}

		mImageLoader = new ImageLoader(VolleyHelper.getRequestQueue(),
				mImageCache);
	}

	public Bitmap getBitmap(String url) {
		try {
			return mImageCache.getBitmap(createKey(url));
		} catch (NullPointerException e) {
			throw new IllegalStateException("Disk Cache Not initialized");
		}
	}

	public void putBitmap(String url, Bitmap bitmap) {
		try {
			mImageCache.putBitmap(createKey(url), bitmap);
		} catch (NullPointerException e) {
			throw new IllegalStateException("Disk Cache Not initialized");
		}
	}

	/**
	 * Executes and image load
	 * 
	 * @param url
	 *            location of image
	 * @param listener
	 *            Listener for completion
	 */
	public void getImage(String url, ImageListener listener) {
		mImageLoader.get(url, listener);
	}

	/**
	 * Executes and image load
	 * 
	 * @param url
	 *            location of image
	 * @param listener
	 *            Listener for completion
	 */
	public void getImage(String url, ImageListener listener, int w, int h) {
		mImageLoader.get(url, listener, w, h);
	}

	/**
	 * @return instance of the image loader
	 */
	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	/**
	 * Creates a unique cache key based on a url value
	 * 
	 * @param url
	 *            url to be used in key creation
	 * @return cache key value
	 */
	private String createKey(String url) {
		return String.valueOf(url.hashCode());
	}
	
	public File getCacheFolder(){
		if(mImageCache instanceof DiskLruImageCache){
			DiskLruImageCache diskLruImageCache=(DiskLruImageCache) mImageCache;			
			return diskLruImageCache.getCacheFolder();
		}
		else{
			return null;
		}
	}
	
	public void cleanCache(){
		if(mImageCache instanceof DiskLruImageCache){
			DiskLruImageCache diskLruImageCache=(DiskLruImageCache) mImageCache;			
			diskLruImageCache.clearCache();
		}
	}
}
