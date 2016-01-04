package com.ganxin.zeromusic.common.listener;

/**
 * 
 * @Description 进度条监听接口
 * @author ganxin
 * @date Mar 16, 2015
 * @email ganxinvip@163.com
 */
public interface OnSeekChangeListener {
	/**
	 * 
	 * @param progress
	 *            播放的进度
	 * @param max
	 *            最大值
	 * @param time
	 *            当前播放的时间
	 * @param duration
	 *            总时间
	 */
	public void onSeekChange(int progress, int max, String time, String duration);

}
