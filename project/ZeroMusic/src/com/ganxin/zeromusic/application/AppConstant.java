package com.ganxin.zeromusic.application;

import android.os.Environment;

/**
 * 
 * @Description 全局配置常量类
 * @author ganxin
 * @date 2014-12-8
 * @email ganxinvip@163.com
 */
public class AppConstant {	
	public static final boolean DEBUG = false; //是否开启调式模式
	
	public static final String SHARPREFER_FILENAME = "zeromusic"; //SharedPreference的文件名
	public static final String VOLUME_MODE_KEY = "volume_mode_key"; //静音模式的key
	public static final String VOLUME_IS_MUTE = "volume_is_mute"; //检查是否静音的key
	
	public static final String RANKING_LIST_TYPE="ranking_list_type"; //排行榜的类型，用于参数
	public static final String RANKING_LIST_NAME="ranking_list_name"; //排行榜的名称，用于参数
	public static final String RANKING_LIST_RESID="ranking_list_resID"; //排行榜的图片资源ID，用于参数
	
	public static final String SDCARD_SAVE_PATH=Environment.getExternalStorageDirectory()
			                               .getPath()+"/Zero/music/download/"; //SD卡存储路径	
	public static final String TIMING_CLOSE_KEY="timing_close_key"; //定时关闭的key
	public static final String TIMING_CLOSE_TIME="timing_close_time"; //定时关闭的时间
	public static final String TIMING_OPEN_AUTO="timing_open_auto"; //是否自动启动
	
	/**
	 * 是否拔出耳机自动暂停	
	 */
	public static final String SETTING_HEADSET_OPEN="setting_headset_open";
	/**
	 * 是否展示横幅广告条
	 */
	public static final String SETTING_SHOW_BANNER_AD="setting_show_banner_ad";
	/**
	 * 甩动手机力度类型
	 */
	public static final String SETTING_SENSOR_TYPE="setting_sensor_type";
	
	public static final int SENSOR_TYPE_HIGH=0;	//灵敏
	public static final int SENSOR_TYPE_MIDDLE=1; //适中
	public static final int SENSOR_TYPE_LOWER=2; //迟缓
	
	public static final String LOADING_NORMAL_COUNT="loading_normal_count"; //正常显示loading计数
	public static final int LOADING_NORMAL_Max=2; //正常显示loading图的最大次数
	
	public static final String HOME_ONLINE_KEY="RecommendToady"; //主页头部的在线参数key
	public static final String SHOW_AD_SWITCH="ShowAdSwitch"; //在线参数key:是否开启广告
	
	public static final String APP_SHARE_URL="http://zeromusic.kuaizhan.com"; //分享链接
	public static final String APP_SHARE_IMAGE="http://ww1.sinaimg.cn/mw690/c62c3492gw1ey9lniwwhdj204v04vaa4.jpg"; //分享图标
	
	public static final String GUIDE_SHOW="guide_show"; //是否显示过引导页
	public static final String LOCAL_SHOW_ADVIEW="local_show_adview"; //是否显示广告条
}
