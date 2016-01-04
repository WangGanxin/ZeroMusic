package com.ganxin.zeromusic.service;

import java.util.List;

import com.ganxin.zeromusic.common.manager.DownloadManager;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * 
 * @Description 下载歌曲服务
 * @author ganxin
 * @date Aug 23, 2015
 * @email ganxinvip@163.com
 */
public class DownloadService extends Service {
	private static DownloadManager DOWNLOAD_MANAGER;

	public DownloadService() {
		super();
	}

	public static DownloadManager getDownloadManager(Context appContext) {
		if (!DownloadService.isServiceRunning(appContext)) {
			Intent downloadSvr = new Intent(
					"com.ganxin.zeromusic.downloadservice.action");
			appContext.startService(downloadSvr);
		}
		if (DownloadService.DOWNLOAD_MANAGER == null) {
			DownloadService.DOWNLOAD_MANAGER = new DownloadManager(appContext);
		}
		return DOWNLOAD_MANAGER;
	}

	public static boolean isServiceRunning(Context context) {
		boolean isRunning = false;

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		if (serviceList == null || serviceList.size() == 0) {
			return false;
		}

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(
					DownloadService.class.getName())) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (DOWNLOAD_MANAGER != null) {
			try {
				DOWNLOAD_MANAGER.stopAllDownload();
				DOWNLOAD_MANAGER.backupDownloadInfoList();
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
		}
		super.onDestroy();
	}

}
