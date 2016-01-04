package com.ganxin.zeromusic.common.http.volleyHelper;

import java.util.Map;
import java.util.Set;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.ganxin.zeromusic.common.util.LogHelper;

/**
 * 
 * @Description http接口请求类
 * @author ganxin
 * @date 2014-12-13
 * @email ganxinvip@163.com
 */
public class HttpAPI {
	/**
	 * 百度音乐API
	 */
	private static final String BAIDU_MUSIC_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting";
	/**
	 * 百度图片API
	 */
	private static final String BAIDU_IMAGE_URL = "http://image.baidu.com/i";

	/**
	 * 默认的请求重试策略类
	 */
	private static DefaultRetryPolicy retryPolicy;

	/**
	 * 创建Get请求到请求队列并开始--获取音乐
	 * 
	 * @param params
	 *            请求参数map
	 * @param clazz
	 *            返回的JSON字符串需要解析成的bean类
	 * @param listener
	 *            访问成功监听
	 * @param errorListener
	 *            访问失败监听
	 */
	public static <T> void createAndStartGetMusicRequest(Map<String, String> params,
			Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
		GsonRequest<T> request;

		if (params == null || params.size() <= 0) {
			request = new GsonRequest<T>(BAIDU_MUSIC_URL, clazz, listener,
					errorListener);
		} else {
			StringBuffer buffer = new StringBuffer();
			Set<String> keys = params.keySet();
			for (String key : keys) {
				String value = params.get(key);
				buffer.append(key + "=" + value);
				buffer.append("&");
			}
			String paramsString = buffer.toString();
			paramsString = "?"
					+ paramsString.substring(0, paramsString.length() - 1);

			request = new GsonRequest<T>(BAIDU_MUSIC_URL + paramsString, clazz,
					listener, errorListener);

			LogHelper.logD(BAIDU_MUSIC_URL + paramsString);
		}

		// 设置请求超时10s,最大重试次数1,尝试backoff次数 1.0
		retryPolicy = new DefaultRetryPolicy(10000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

		request.setRetryPolicy(retryPolicy);
		VolleyHelper.addRequest(request);
		startAllRequest();
	}

	/**
	 * 创建Get请求到请求队列并开始--获取图片
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数map
	 * @param clazz
	 *            返回的JSON字符串需要解析成的bean类
	 * @param listener
	 *            访问成功监听
	 * @param errorListener
	 *            访问失败监听
	 */
	@Deprecated
	public static <T> void createAndStartGetImageRequest(Map<String, String> params, 
			Class<T> clazz, Listener<T> listener,ErrorListener errorListener) {
		GsonRequest<T> request;

		if (params == null || params.size() <= 0) {
			request = new GsonRequest<T>(BAIDU_IMAGE_URL, clazz, listener,
					errorListener);
		} else {
			StringBuffer buffer = new StringBuffer();
			Set<String> keys = params.keySet();
			for (String key : keys) {
				String value = params.get(key);
				buffer.append(key + "=" + value);
				buffer.append("&");
			}
			String paramsString = buffer.toString();
			paramsString = "?"
					+ paramsString.substring(0, paramsString.length() - 1);

			request = new GsonRequest<T>(BAIDU_IMAGE_URL + paramsString, clazz,
					listener, errorListener);

			LogHelper.logD(BAIDU_IMAGE_URL + paramsString);
		}

		// 设置请求超时10s,最大重试次数1,尝试backoff次数 1.0
		retryPolicy = new DefaultRetryPolicy(10000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

		request.setRetryPolicy(retryPolicy);
		VolleyHelper.addRequest(request);
		startAllRequest();
	}

	/**
	 * 开始请求队列中的所有请求
	 */
	public static void startAllRequest() {
		VolleyHelper.startAllRequest();
	}

	/**
	 * 终止请求队列（除非有必要，否则一般情况下不要调用的这个方法）
	 */
	public static void stopRequestQueue() {
		VolleyHelper.stopRequestQueue();
	}
}
