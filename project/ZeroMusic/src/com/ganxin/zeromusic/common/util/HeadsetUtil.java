package com.ganxin.zeromusic.common.util;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

/**
 * 耳机相关工具类
 */
public class HeadsetUtil {

	/**
	 * 耳机插拔事件的广播接收器
	 */
	private static final BroadcastReceiver headsetStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent
					.getAction())) {
				boolean isplug = intent.getIntExtra("state", 0) == HEADSET_STATE_PLUG;
				String name = intent.getStringExtra("name");
				boolean microphone = intent.getIntExtra("microphone", 0) == 1;
				for (IHeadsetState headsetState : headsetStateListener) {
					if (isplug) {
						headsetState.headsetPlug(name, microphone);
					} else {
						headsetState.headsetPull(name, microphone);
					}
				}
			}
		}
	};

	/**
	 * 保存所有已注册的耳机插拔事件监听器
	 */
	private static final List<IHeadsetState> headsetStateListener = new ArrayList<IHeadsetState>();
	private static final Object lock = new Object();
	/**
	 * 插入耳机对应的状态值
	 */
	private static final int HEADSET_STATE_PLUG = 1;

	/**
	 * 是否已注册耳机插拔事件广播接收器
	 */
	private static boolean isRegistHeadsetStateReceiver = false;

	/**
	 * 注册对耳机插拔事件的监听
	 *
	 * @param context
	 *            上下文
	 * @param listener
	 *            监听器
	 */
	public static void registHeadsetStateListener(Context context,
			IHeadsetState listener) {
		synchronized (lock) {
			if (listener != null) {
				headsetStateListener.add(listener);
				if (!isRegistHeadsetStateReceiver
						&& !headsetStateListener.isEmpty()) {
					context.getApplicationContext().registerReceiver(
							headsetStateReceiver,
							new IntentFilter(
									AudioManager.ACTION_AUDIO_BECOMING_NOISY));
					isRegistHeadsetStateReceiver = true;
				}
			}
		}
	}

	/**
	 * 取消已注册的耳机插拔事件广播接收器
	 *
	 * @param context
	 *            上下文
	 * @param listener
	 *            监听器
	 */
	public static void unregistHeadsetStateListener(Context context,
			IHeadsetState listener) {
		synchronized (lock) {
			if (headsetStateListener.contains(listener)) {
				headsetStateListener.remove(listener);
			}
			if (isRegistHeadsetStateReceiver && headsetStateListener.isEmpty()) {
				context.getApplicationContext().unregisterReceiver(
						headsetStateReceiver);
				isRegistHeadsetStateReceiver = false;
			}
		}
	}

	/**
	 * 耳机插拔状态监听
	 */
	public interface IHeadsetState {
		/**
		 * 耳机插入时调用
		 *
		 * @param name
		 *            耳机名称
		 * @param microphone
		 *            是否具有麦克风
		 */
		void headsetPlug(String name, boolean microphone);

		/**
		 * 耳机拔出时调用
		 *
		 * @param name
		 *            耳机名称
		 * @param microphone
		 *            是否具有麦克风
		 */
		void headsetPull(String name, boolean microphone);
	}
}
