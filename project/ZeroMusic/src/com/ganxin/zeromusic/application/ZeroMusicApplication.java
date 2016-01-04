package com.ganxin.zeromusic.application;

import grd.lks.oew.AdManager;
import android.app.Application;
import android.content.Context;

import com.ganxin.zeromusic.common.http.volleyHelper.VolleyHelper;
import com.ganxin.zeromusic.common.http.volleyImage.ImageCacheManager;
import com.ganxin.zeromusic.common.util.CrashHandler;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @Description 全局application
 * @author ganxin
 * @date Mar 13, 2015
 * @email ganxinvip@163.com
 */
public class ZeroMusicApplication extends Application {
	/**
	 * 全局环境
	 */
	private static Context mContext;
	
    private static ZeroMusicApplication instance;

    public static ZeroMusicApplication getInstence() {
        return instance;
    }
    
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();		
		instance=this;
		
		ZeroMusicApplication.mContext = getApplicationContext();
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
		//初始化volley网络请求队列
		VolleyHelper.init(this);
		//初始化volley框架的图片缓存设置
		ImageCacheManager.init(this);
		//异常处理handler 初始化
		CrashHandler.getInstance().init(this);
				
		//有米广告初始化
        AdManager.getInstance(this).init("f626638ffe01f992", "44a854557e72f862");
        AdManager.getInstance(this).setEnableDebugLog(false);
        
        //友盟统计数据 加密设置
        AnalyticsConfig.enableEncrypt(true);/** 设置是否对日志信息进行加密, 默认false(不加密). */
        MobclickAgent.setDebugMode(false); //是否开启友盟集成测试
	}

	public static Context getContext() {
		return ZeroMusicApplication.mContext;
	}	
}
