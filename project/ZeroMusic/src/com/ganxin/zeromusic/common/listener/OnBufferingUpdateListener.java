package com.ganxin.zeromusic.common.listener;
/**
 * 
 * @Description 播放在线歌曲 缓冲更新的监听器
 * @author ganxin
 * @date Sep 13, 2015
 * @email ganxinvip@163.com
 */
public interface OnBufferingUpdateListener {
	/**
	 * 缓冲更新回调
	 * @param percent 缓冲的百分比
	 */
	public void onBufferingUpdate( int percent);
}
