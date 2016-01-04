package com.ganxin.zeromusic.common.listener;

import java.util.List;

import com.ganxin.zeromusic.common.bean.MusicBean;

/**
 * 
 * @Description 播放控制器的监听接口
 * @author ganxin
 * @date Mar 21, 2015
 * @email ganxinvip@163.com
 */
public interface OnPlayerStateChangeListener {

	/**
	 * 
	 * @param state
	 *            状态
	 * @param mode
	 *            播放模式
	 * @param musicList
	 *            播放列表
	 * @param position
	 *            位置
	 */
	public void onStateChange(int state, int mode, List<MusicBean> musicList,
			int position);
}
