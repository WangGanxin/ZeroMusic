package com.ganxin.zeromusic.common.manager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.ganxin.zeromusic.common.bean.DownloadInfo;
import com.ganxin.zeromusic.common.listener.OnDownloadFinishListener;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.converter.ColumnConverter;
import com.lidroid.xutils.db.converter.ColumnConverterFactory;
import com.lidroid.xutils.db.sqlite.ColumnDbType;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;

/**
 * 
 * @Description 下载管理者
 * @author ganxin
 * @date Aug 23, 2015
 * @email ganxinvip@163.com
 */
public class DownloadManager {
	private List<DownloadInfo> downloadInfoList;
	private int maxDownloadThread = 1;

	private Context mContext;
	private DbUtils db;

	private static List<WeakReference<OnDownloadFinishListener>> downloadFinishListenerList = new ArrayList<WeakReference<OnDownloadFinishListener>>();

	public DownloadManager(Context appContext) {
		ColumnConverterFactory.registerColumnConverter(HttpHandler.State.class,
				new HttpHandlerStateConverter());
		mContext = appContext;
		db = DbUtils.create(mContext);
		try {
			downloadInfoList = db.findAll(Selector.from(DownloadInfo.class));
		} catch (DbException e) {
			LogUtils.e(e.getMessage(), e);
		}
		if (downloadInfoList == null) {
			downloadInfoList = new ArrayList<DownloadInfo>();
		}
	}

	public List<DownloadInfo> getDownloadInfoList() {
		return downloadInfoList;
	}

	public int getDownloadInfoListCount() {
		return downloadInfoList.size();
	}

	public DownloadInfo getDownloadInfo(int index) {
		return downloadInfoList.get(index);
	}

	public int getMaxDownloadThread() {
		return maxDownloadThread;
	}

	public void setMaxDownloadThread(int maxDownloadThread) {
		this.maxDownloadThread = maxDownloadThread;
	}

	public void addNewDownload(Boolean isLrcFile, String url, String fileName,
			String target, String artist, String lyric_file_name, Long size,
			Long duration, boolean autoResume, boolean autoRename,
			final RequestCallBack<File> callback) throws DbException {
		final DownloadInfo downloadInfo = new DownloadInfo();
		downloadInfo.setLrcFile(isLrcFile);
		downloadInfo.setDownloadUrl(url);
		downloadInfo.setAutoRename(autoRename);
		downloadInfo.setAutoResume(autoResume);
		downloadInfo.setFileName(fileName);
		downloadInfo.setFileSavePath(target);
		downloadInfo.setArtist(artist);
		downloadInfo.setLyric_file_name(lyric_file_name);
		downloadInfo.setSize(size);
		downloadInfo.setDuration(duration);

		HttpUtils http = new HttpUtils();
		http.configRequestThreadPoolSize(maxDownloadThread);
		HttpHandler<File> handler = http.download(url, target, autoResume,
				autoRename, new ManagerCallBack(downloadInfo, callback));
		downloadInfo.setHandler(handler);
		downloadInfo.setState(handler.getState());
		downloadInfoList.add(downloadInfo);
		db.saveBindingId(downloadInfo);
	}

	public void resumeDownload(int index, final RequestCallBack<File> callback)
			throws DbException {
		final DownloadInfo downloadInfo = downloadInfoList.get(index);
		resumeDownload(downloadInfo, callback);
	}

	public void resumeDownload(DownloadInfo downloadInfo,
			final RequestCallBack<File> callback) throws DbException {
		HttpUtils http = new HttpUtils();
		http.configRequestThreadPoolSize(maxDownloadThread);
		HttpHandler<File> handler = http.download(
				downloadInfo.getDownloadUrl(), downloadInfo.getFileSavePath(),
				downloadInfo.isAutoResume(), downloadInfo.isAutoRename(),
				new ManagerCallBack(downloadInfo, callback));
		downloadInfo.setHandler(handler);
		downloadInfo.setState(handler.getState());
		db.saveOrUpdate(downloadInfo);
	}

	public void removeDownload(int index) throws DbException {
		DownloadInfo downloadInfo = downloadInfoList.get(index);
		removeDownload(downloadInfo);
	}

	public void removeDownload(DownloadInfo downloadInfo) throws DbException {
		HttpHandler<File> handler = downloadInfo.getHandler();
		if (handler != null && !handler.isCancelled()) {
			handler.cancel();
		}
		downloadInfoList.remove(downloadInfo);
		db.delete(downloadInfo);
	}

	public void stopDownload(int index) throws DbException {
		DownloadInfo downloadInfo = downloadInfoList.get(index);
		stopDownload(downloadInfo);
	}

	public void stopDownload(DownloadInfo downloadInfo) throws DbException {
		HttpHandler<File> handler = downloadInfo.getHandler();
		if (handler != null && !handler.isCancelled()) {
			handler.cancel();
		} else {
			downloadInfo.setState(HttpHandler.State.CANCELLED);
		}
		db.saveOrUpdate(downloadInfo);
	}

	public void stopAllDownload() throws DbException {
		for (DownloadInfo downloadInfo : downloadInfoList) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null && !handler.isCancelled()) {
				handler.cancel();
			} else {
				downloadInfo.setState(HttpHandler.State.CANCELLED);
			}
		}
		db.saveOrUpdateAll(downloadInfoList);
	}

	public void backupDownloadInfoList() throws DbException {
		for (DownloadInfo downloadInfo : downloadInfoList) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
		}
		db.saveOrUpdateAll(downloadInfoList);
	}

	public void addDownloadFinishListener(OnDownloadFinishListener listener){
		synchronized (listener) {
			boolean isExist=false;
			for(WeakReference<OnDownloadFinishListener> refListener: downloadFinishListenerList){
				if(refListener.get()!=null&&refListener.get()==listener){
					isExist=true;
					break;
				}
			}
			
			if(!isExist){
				downloadFinishListenerList.add(new WeakReference<OnDownloadFinishListener>(listener));
			}
		}
	}
	
	public void removeDownloadFinishListener(OnDownloadFinishListener listener) {
		synchronized (listener) {
			for(WeakReference<OnDownloadFinishListener> refListener: downloadFinishListenerList){
				if(refListener.get()!=null&&refListener.get()==listener){
					downloadFinishListenerList.remove(refListener);
					break;
				}
			}
		}
	}
	
	public void notifyDownloadFinish() {
		synchronized (downloadFinishListenerList) {
			List<WeakReference<OnDownloadFinishListener>> emptyList=new ArrayList<WeakReference<OnDownloadFinishListener>>();
			
			for(WeakReference<OnDownloadFinishListener> refListener: downloadFinishListenerList){
				OnDownloadFinishListener listener=refListener.get();
				if(listener!=null){
					listener.onFinish();
				}else{
					emptyList.add(refListener);
				}
			}
			
			if(emptyList.size()>0){
				downloadFinishListenerList.remove(emptyList);
			}
		}
	}

	/**
	 * 
	 * @Description httphandler的状态转换器
	 * @author ganxin
	 * @date Aug 23, 2015
	 * @email ganxinvip@163.com
	 */
	private class HttpHandlerStateConverter implements
			ColumnConverter<HttpHandler.State> {

		@Override
		public HttpHandler.State getFieldValue(Cursor cursor, int index) {
			return HttpHandler.State.valueOf(cursor.getInt(index));
		}

		@Override
		public HttpHandler.State getFieldValue(String fieldStringValue) {
			if (fieldStringValue == null)
				return null;
			return HttpHandler.State.valueOf(fieldStringValue);
		}

		@Override
		public Object fieldValue2ColumnValue(HttpHandler.State fieldValue) {
			return fieldValue.value();
		}

		@Override
		public ColumnDbType getColumnDbType() {
			return ColumnDbType.INTEGER;
		}
	}

	/**
	 * 
	 * @Description 请求回调管理类
	 * @author ganxin
	 * @date Aug 23, 2015
	 * @email ganxinvip@163.com
	 */
	public class ManagerCallBack extends RequestCallBack<File> {
		private DownloadInfo downloadInfo;
		private RequestCallBack<File> baseCallBack;

		public RequestCallBack<File> getBaseCallBack() {
			return baseCallBack;
		}

		public void setBaseCallBack(RequestCallBack<File> baseCallBack) {
			this.baseCallBack = baseCallBack;
		}

		private ManagerCallBack(DownloadInfo downloadInfo,
				RequestCallBack<File> baseCallBack) {
			this.baseCallBack = baseCallBack;
			this.downloadInfo = downloadInfo;
		}

		@Override
		public Object getUserTag() {
			if (baseCallBack == null)
				return null;
			return baseCallBack.getUserTag();
		}

		@Override
		public void setUserTag(Object userTag) {
			if (baseCallBack == null)
				return;
			baseCallBack.setUserTag(userTag);
		}

		@Override
		public void onStart() {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			try {
				db.saveOrUpdate(downloadInfo);
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onStart();
			}
		}

		@Override
		public void onCancelled() {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			try {
				db.saveOrUpdate(downloadInfo);
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onCancelled();
			}
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			downloadInfo.setFileLength(total);
			downloadInfo.setProgress(current);
			try {
				db.saveOrUpdate(downloadInfo);
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onLoading(total, current, isUploading);
			}
		}

		@Override
		public void onSuccess(ResponseInfo<File> responseInfo) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			try {
				db.saveOrUpdate(downloadInfo);
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onSuccess(responseInfo);
			}
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			try {
				db.saveOrUpdate(downloadInfo);
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onFailure(error, msg);
			}
		}
	}
}
