package com.ganxin.zeromusic.common.listener;

/**
 * 
 * @Description 播放模式监听接口
 * @author ganxin
 * @date Mar 21, 2015
 * @email ganxinvip@163.com
 */
public interface OnModeChangeListener {
	/**
	 * 播放模式改变的回调
	 * @param mode  更改的模式
	 */
	public void onModeChange(int mode);
}
