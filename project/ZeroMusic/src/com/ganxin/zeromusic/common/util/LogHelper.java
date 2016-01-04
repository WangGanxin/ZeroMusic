package com.ganxin.zeromusic.common.util;

import com.ganxin.zeromusic.application.AppConstant;

import android.util.Log;

/**
 * 
 * @Description 日志打印工具类
 * @author ganxin
 * @date 2014-12-8
 * @email ganxinvip@163.com
 */
public class LogHelper {
	public static boolean DEBUG = AppConstant.DEBUG;
	public static String TAG="ZeroMusic";
	
	/**
	 * 调试
	 * @param msg 内容
	 */
	public static void logD(String msg){
		if (DEBUG) {
			Log.i(TAG, msg);
		}	
	}
	
	/**
	 * 错误
	 * @param msg 内容
	 */
	public static void logE(String msg){
		if (DEBUG) {
			Log.e(TAG,msg);
		}
	}
	
	/**
	 * 状态
	 * @param cls 类
	 * @param msg 内容
	 */
	public static void state(Class<?> cls,String msg){
		if (DEBUG) {
			Log.i(TAG,"State : "+cls+"------"+msg);
		}
	}
}
