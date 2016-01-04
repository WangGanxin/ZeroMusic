package com.ganxin.zeromusic.common.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

/**
 * 
 * @Description app全局工具类
 * @author ganxin
 * @date Sep 27, 2015
 * @email ganxinvip@163.com
 */
public class AppUtil {
	private static List<Activity> activityList = new ArrayList<Activity>();

	/**
	 * 添加Activity到容器中 @param activity
	 */
	public static void addActivity(Activity activity) {
		activityList.add(activity);
	}

	/**
	 * 从容器中删除Activity @param activity
	 */
	public static void removeActivity(Activity activity) {
		activityList.remove(activity);
	}

	/**
	 * 遍历所有Activity并finish
	 */
	public static void exitAllActivity() {
		if (activityList != null && activityList.size() > 0) {
			LogHelper.logD("activtiy list size--->"+activityList.size());
			try {
				for (Activity activity : activityList) {
					if (activity != null) {
						activity.finish();
					}
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
	}
}
