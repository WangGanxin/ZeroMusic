package com.ganxin.zeromusic.common.listener;

/**
 * 
 * @Description 设置改变监听器
 * @author ganxin
 * @date Oct 31, 2015
 * @email ganxinvip@163.com
 */
public interface OnSettingsChangedListenr {
	/**
	 * 
	 * @param type 类型
	 * @param newVal 新的value
	 */
	void settingsChanged(String type, Object newVal);
}
