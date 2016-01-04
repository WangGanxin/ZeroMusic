package com.ganxin.zeromusic.common.http.volleyImage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * 本地缓存读写 Implementation of DiskLruCache by Jake Wharton modified from
 * http://stackoverflow
 * .com/questions/10185898/using-disklrucache-in-android-4-0-
 * does-not-provide-for-opencache-method
 */
public class DiskLruImageCache implements ImageCache {

	private DiskLruCache mDiskCache;
	private CompressFormat mCompressFormat = CompressFormat.JPEG;
	private static int IO_BUFFER_SIZE = 15 * 1024;
	private int mCompressQuality = 50;
	private static final int APP_VERSION = 1;
	private static final int VALUE_COUNT = 1;

	public DiskLruImageCache(Context context, String uniqueName,
			int diskCacheSize, CompressFormat compressFormat, int quality) {
		try {
			final File diskCacheDir = getDiskCacheDir(context, uniqueName);
			mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION,
					VALUE_COUNT, diskCacheSize);
			mCompressFormat = compressFormat;
			mCompressQuality = quality;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor)
			throws IOException, FileNotFoundException {
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(editor.newOutputStream(0),
					IO_BUFFER_SIZE);
			return bitmap.compress(mCompressFormat, mCompressQuality, out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	private File getDiskCacheDir(Context context, String uniqueName) {

		final String cachePath = context.getCacheDir().getPath();
		return new File(cachePath + File.separator + uniqueName);
	}

	@Override
	public void putBitmap(String url, Bitmap data) {

		DiskLruCache.Editor editor = null;
		try {
			editor = mDiskCache.edit(createKey(url));
			if (editor == null) {
				return;
			}

			if (writeBitmapToFile(data, editor)) {
				mDiskCache.flush();
				editor.commit();

			} else {
				editor.abort();

			}
		} catch (IOException e) {

			try {
				if (editor != null) {
					editor.abort();
				}
			} catch (IOException ignored) {
			}
		}

	}

	@Override
	public Bitmap getBitmap(String url) {

		Bitmap bitmap = null;
		DiskLruCache.Snapshot snapshot = null;
		try {

			snapshot = mDiskCache.get(createKey(url));
			if (snapshot == null) {
				return null;
			}
			final InputStream in = snapshot.getInputStream(0);
			if (in != null) {
				final BufferedInputStream buffIn = new BufferedInputStream(in,
						IO_BUFFER_SIZE);
				bitmap = BitmapFactory.decodeStream(buffIn);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (snapshot != null) {
				snapshot.close();
			}
		}

		return bitmap;

	}

	public boolean containsKey(String key) {

		boolean contained = false;
		DiskLruCache.Snapshot snapshot = null;
		try {
			snapshot = mDiskCache.get(key);
			contained = snapshot != null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (snapshot != null) {
				snapshot.close();
			}
		}

		return contained;

	}

	public void clearCache() {
		try {
			mDiskCache.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getCacheFolder() {
		return mDiskCache.getDirectory();
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

}
