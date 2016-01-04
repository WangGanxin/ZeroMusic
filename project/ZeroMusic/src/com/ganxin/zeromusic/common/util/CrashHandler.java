package com.ganxin.zeromusic.common.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import com.umeng.analytics.MobclickAgent;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * 处理应用程序中出现的异常
 * 
 * @author lifuqiang
 *
 */

public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";
	/**
	 * 是否开启日志输出,在Debug状态下开启, 在Release状态下关闭以提示程序性能
	 * */
	public static final boolean DEBUG = false;
	/** 系统默认的UncaughtException处理类 */
	private UncaughtExceptionHandler mDefaultHandler;
	/** CrashHandler实例 */
	private static CrashHandler INSTANCE;
	/** 程序的Context对象 */
	private Context mContext;

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CrashHandler();
		}
		return INSTANCE;
	}

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 * 
	 * @param ctx
	 */
	public void init(Context ctx) {
		mContext = ctx;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			System.out.println("系统处理异常");
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			// Sleep一会后结束程序
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				Log.e(TAG, "Error : ", e);
			}
			System.out.println("自定义处理异常信息");
			System.exit(0);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		final String msg = ex.getLocalizedMessage();
		System.out.println("程序错误:" + msg + "...." + ex.getMessage());

		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "抱歉，应用出现错误:" + msg, Toast.LENGTH_LONG)
						.show();
				Looper.loop();
			}

		}.start();
		
		// 退出应用-- activity和service
		exitApp(mContext);
		
		return true;
	}

	private void exitApp(final Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		// 结束所有服务
		List<ActivityManager.RunningServiceInfo> runningServices = manager
				.getRunningServices(100);
		for (ActivityManager.RunningServiceInfo runningService : runningServices) {
			if (runningService.process.startsWith(context.getPackageName())) {
				Intent intent = new Intent();
				intent.setComponent(runningService.service);
				context.stopService(intent);
			}
		}

		// 结束进程
		new Thread() {
			@Override
			public void run() {
				while (getRunningActivityNumber(context) > 0) {
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				MobclickAgent.onKillProcess(context);
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}.start();

	}

	/**
	 * 获取当前应用程序被打开的 Activity 界面数
	 *
	 * @param context
	 *            上下文
	 * @return Activity 个数
	 */
	private int getRunningActivityNumber(Context context) {
		int num = 0;
		try {
			ActivityManager manager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningTaskInfo> runningTasks = manager
					.getRunningTasks(100);
			String packageName = context.getPackageName();
			for (ActivityManager.RunningTaskInfo taskInfo : runningTasks) {
				if (packageName.equals(taskInfo.baseActivity.getPackageName())) {
					num += taskInfo.numActivities;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return num;
	}
}
