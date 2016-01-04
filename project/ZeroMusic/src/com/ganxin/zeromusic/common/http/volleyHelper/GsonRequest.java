package com.ganxin.zeromusic.common.http.volleyHelper;

import java.io.UnsupportedEncodingException;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.ganxin.zeromusic.common.util.LogHelper;
import com.google.gson.Gson;

/**
 * 
 * @Description 采用Volley与gson库结合实现的Http请求类
 * @author ganxin
 * @date 2014-12-12
 * @email ganxinvip@163.com
 * @param <T> 泛型模型
 */
public class GsonRequest<T> extends Request<T> {
	/**
	 * gson库
	 */
	private Gson mGson;
	/**
	 * 泛型模型类
	 */
	private Class<T> mClass;
	/**
	 * 请求成功监听
	 */
	private final Listener<T> mListener;

	/**
	 * 基于Volley并结合Jackson库的网络请求,GET请求方式的构造
	 * 
	 * @param url
	 *            请求地址
	 * @param clazz
	 *            需要返回的泛型模型
	 * @param listener
	 *            请求成功监听
	 * @param errorListener
	 *            请求错误监听
	 */
	public GsonRequest(String url, Class<T> clazz, Listener<T> listener,
			ErrorListener errorListener) {
		super(Method.GET, url, errorListener);
		// TODO Auto-generated constructor stub
		mGson = new Gson();
		mClass = clazz;
		mListener = listener;
	}

	@Override
	protected void deliverResponse(T response) {
		// TODO Auto-generated method stub
		mListener.onResponse(response); // 返回新的监听结果
	}

	/**
	 * 服务器返回数据封装成bean对象
	 */
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		// TODO Auto-generated method stub
		try {
			String jsonStr = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			
			LogHelper.logD(jsonStr);
			
			return Response.success(mGson.fromJson(jsonStr, mClass),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.error(new ParseError(e));
		}
	}
}
