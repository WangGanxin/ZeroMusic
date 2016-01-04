package com.ganxin.zeromusic.common.util;

import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 
 * @Description 网络工具类
 * @author ganxin
 * @date 2014-12-15
 * @email ganxinvip@163.com
 */
public class NetWorkHelper {
	
	/**
	 * 获取连接管理器实例
	 * 
	 * @param context
	 *            当前环境
	 * @return 连接管理器实例
	 */
	public static ConnectivityManager getConnectivityManager(Context context) {
		Object obj = context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (obj instanceof ConnectivityManager) {
			return (ConnectivityManager) obj;
		}
		return null;
	}
	
	/**
	 * 获取正在使用的网络的信息
	 * 
	 * @param context
	 *            当前环境
	 * @return 当前正在使用的网络的信息
	 */
	public static NetworkInfo getActivieNetworkInfo(Context context) {
		ConnectivityManager connectivityManager = getConnectivityManager(context);
		if (connectivityManager != null) {
			return connectivityManager.getActiveNetworkInfo();
		}
		return null;
	}
	
	/**
	 * 检测当前网络是否已连接
	 * 
	 * @param context
	 *            当前环境
	 * @return 如果已连接返回true
	 */
	public static boolean isConnected(Context context){
		NetworkInfo networkInfo = getActivieNetworkInfo(context);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}
	
	/**
	 * 判断当前网络状态是否是wifi
	 * 
	 * @param context
	 *            当前环境
	 * @return 如果当前网络环境是wifi返回true
	 */
	public static boolean isWifi(Context context) {
		NetworkInfo networkInfo = getActivieNetworkInfo(context);
		if (networkInfo != null) {
			if ("wifi".equals(networkInfo.getTypeName()
					.toLowerCase(Locale.CHINESE))) {
				return true;
			}
		}
		return false;
	}
}
