package com.ganxin.zeromusic.common.listener;

/**
 * 
 * @Description sharedata的设置监听器
 * @author ganxin
 * @date Oct 31, 2015
 * @email ganxinvip@163.com
 */
public interface OnSharedataCommitListener {
	/***
	 * 设置保存成功后触发
	 * @param configKey    设置键
	 * @param configValue  设置键值
	 */
	void onSharedataCommit(String configKey,Object configValue);
}
