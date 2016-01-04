package com.ganxin.zeromusic.common.http.volleyHelper;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * 
 * @Description Volley网络通信框架工具类
 * @author ganxin
 * @date 2014-12-12
 * @email ganxinvip@163.com
 */
public class VolleyHelper {
	/**
	 * 单例对象实例
	 */
	private static RequestQueue mRequestQueue;

	/**
	 * 初始化网络请求队列
	 * 
	 * @param context 当前环境				
	 * @return mRequestQueue 请求队列
	 */
	public static RequestQueue init(Context context) {
		
		if (mRequestQueue == null){
			mRequestQueue = Volley.newRequestQueue(context);
		}		
		return mRequestQueue;
	}
	
	/**
	 * 得到请求队列的实例
	 * @return
	 */
	public static RequestQueue getRequestQueue(){
		return mRequestQueue;
	}

	/**
	 * 开始指定标签的单一请求
	 * @param request 请求类型
	 */
	public static void startRequest(Request<?> request) {
		mRequestQueue.add(request);
		startAllRequest();
	}
	
    /**
     * 增加指定标签的请求，但是不开始执行
     * @param request 请求类型
     */
	public static void addRequest(Request<?> request) {
		mRequestQueue.add(request);
	}
	
    /**
     * 开始队列中的所有请求
     */
	public static void startAllRequest() {
		mRequestQueue.start();
	}
	
	/**
	 * 终止请求队列
	 */
	public static void stopRequestQueue() {
		mRequestQueue.stop();
	}
}
