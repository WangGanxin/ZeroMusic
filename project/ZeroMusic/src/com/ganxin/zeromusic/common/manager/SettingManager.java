package com.ganxin.zeromusic.common.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.ganxin.zeromusic.application.AppConstant;
import com.ganxin.zeromusic.common.http.volleyImage.ImageCacheManager;
import com.ganxin.zeromusic.common.listener.OnSettingsChangedListenr;
import com.ganxin.zeromusic.common.listener.OnSharedataCommitListener;
import com.ganxin.zeromusic.common.util.DataCleanHelper;
import com.ganxin.zeromusic.common.util.SharPreferHelper;

/**
 * 
 * @Description 设置管理类
 * @author ganxin
 * @date Sep 20, 2015
 * @email ganxinvip@163.com
 */
public class SettingManager implements OnSharedataCommitListener{

	//饿汉单例模式
	private SettingManager(){};
	private static final SettingManager instance=new SettingManager();
	
	public static SettingManager getInstance() {
		return instance;
	}
			
	private HashMap<String, List<OnSettingsChangedListenr>> listeners = 
			new HashMap<String, List<OnSettingsChangedListenr>>();
	
	/**
	 * 
	 * @param listener 监听对象
	 * @param type 
	 *        类型
	 *        <li>{@link AppConstant#SETTING_HEADSET_OPEN} 是否拔出耳机暂停歌曲
	 *        <li>{@link AppConstant#SETTING_SHOW_BANNER_AD} 是否展示广告条
	 */
	public void addSettingsChangedListener(OnSettingsChangedListenr listener,
			String... type){
		if (listener == null || type == null)
			return;
		for (int i = 0; i < type.length; i++) {
			List<OnSettingsChangedListenr> list = listeners.get(type[i]);
			if (list == null) {
				list = new ArrayList<OnSettingsChangedListenr>();
			}
			list.add(listener);
			listeners.put(type[i], list);
		}
	}
	
	private void notifySettingsChanged(String type, Object newVal) {
		List<OnSettingsChangedListenr> list = listeners.get(type);
		if (list != null) {
			for (OnSettingsChangedListenr scl : list) {
				scl.settingsChanged(type, newVal);
			}
		}
	}
	
	@Override
	public void onSharedataCommit(String configKey, Object configValue) {
		// TODO Auto-generated method stub
		notifySettingsChanged(configKey, configValue);
	}	
	
	/**
	 * 设置耳机拔出暂停
	 * @param context
	 * @param value
	 */
	public void setLineOutPauseCheck(Context context, boolean value) {
		SharPreferHelper.setConfig(context, AppConstant.SHARPREFER_FILENAME,
				AppConstant.SETTING_HEADSET_OPEN,value,this);
	}

	/**
	 * 获取耳机拔出暂停
	 * @param context
	 * @return
	 */
	public boolean getLineOutPauseCheck(Context context) {
		return SharPreferHelper.getBooleanConfig(context, AppConstant.SHARPREFER_FILENAME,
				AppConstant.SETTING_HEADSET_OPEN,false);
	}
	
	/**
	 * 设置展示广告条
	 * @param context
	 * @param value
	 */
	public void setBannerAdvertisermentCheck(Context context, boolean value){
		SharPreferHelper.setConfig(context, AppConstant.SHARPREFER_FILENAME,
				AppConstant.SETTING_SHOW_BANNER_AD,value,this);		
	}
	
	/**
	 * 获取展示广告条
	 * @param context
	 * @return
	 */
	public boolean getBannerAdvertisermentCheck(Context context){
		return SharPreferHelper.getBooleanConfig(context, AppConstant.SHARPREFER_FILENAME,
				AppConstant.SETTING_SHOW_BANNER_AD,true);		
	}
	
	/**
	 * 设置甩动手机切换封面的力度类型
	 * @param context
	 * @param value
	 */
	public void setSensorType(Context context, int value){
		SharPreferHelper.setConfig(context, AppConstant.SHARPREFER_FILENAME, 
				AppConstant.SETTING_SENSOR_TYPE, value);
	}
	
	/**
	 * 获取甩动手机切换封面的力度类型
	 * @param context
	 * @return
	 */
	public int getSensorType(Context context){
		return SharPreferHelper.getIntConfig(context, AppConstant.SHARPREFER_FILENAME,
				AppConstant.SETTING_SENSOR_TYPE,AppConstant.SENSOR_TYPE_MIDDLE);
	}
	
	/**
	 * 获取缓存大小
	 * @return
	 */
	public String getCacheSize(){
		try {
			return DataCleanHelper.getCacheSize(ImageCacheManager.getInstance().getCacheFolder());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 清除缓存（仅清除了图片）
	 */
	public void cleanImageCache(){
		DataCleanHelper.deleteFilesByDirectory(ImageCacheManager.getInstance().getCacheFolder());
	}
}
