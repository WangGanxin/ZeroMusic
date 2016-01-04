package com.ganxin.zeromusic.receiver;

import java.util.List;
import java.util.Random;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ganxin.zeromusic.application.AppConstant;
import com.ganxin.zeromusic.application.PlayerConstant;
import com.ganxin.zeromusic.common.util.LogHelper;
import com.ganxin.zeromusic.module.MenuActivity;
import com.ganxin.zeromusic.service.PlayerService;

/**
 * 
 * @Description 音乐广播接收者，用于接收客户发来的播放音乐的广播
 * @author ganxin
 * @date 2014-12-10
 * @email ganxinvip@163.com
 */
public class PlayerReceiver extends BroadcastReceiver {
	// 点击播放列表时的action
	public static final String ACTION_PLAY_ITEM = "com.ganxin.zeromusic.service.ACTION_PLAY_ITEM";
	// 点击通知后跳转到当前应用的action
	public static final String ACTION_BACKUP_ZERO = "com.ganxin.zeromusic.service.ACTION_BACKUP_ZERO";
	// 点击了播放/暂停键的时候发这个action
	public static final String ACTION_PLAY_BUTTON = "com.ganxin.zeromusic.service.ACTION_PLAY_BUTTON";
	// 上一首action
	public static final String ACTION_PLAY_PREVIOUS = "com.ganxin.zeromusic.service.ACTION_PLAY_PREVIOUS";
	// 下一首action
	public static final String ACTION_PLAY_NEXT = "com.ganxin.zeromusic.service.ACTION_PLAY_NEXT";
	// 播放模式的action
	public static final String ACTION_PLAY_MODE = "com.ganxin.zeromusic.service.ACTION_PLAY_MODE";
	// seekbar进度更改的action
	public static final String ACTION_SEEKBAR = "com.ganxin.zeromusic.service.ACTION_SEEKBAR";
	// 更新PlayerService的播放歌曲队列
	public static final String ACTION_UPDATE_LIST = "com.ganxin.zeromusic.service.ACTION_UPDATE_LIST";
	// 自动启动音乐action
	public static final String ACTION_AUTO_START = "com.ganxin.zeromusic.AUTO_START";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		LogHelper.logD("ACTION==============" + action);

		// 如果收到的是点击播放列表时发送的广播
		if (action.equals(ACTION_PLAY_ITEM)) {
			PlayerService.where = intent
					.getStringExtra(PlayerConstant.PLAYER_WHERE);
			LogHelper.logD("where=============" + PlayerService.where);
			// 得到当前页面传过来的播放列表
			PlayerService.serviceMusicList = intent
					.getParcelableArrayListExtra(PlayerConstant.PLAYER_LIST);
			// 得到当前页面点击的item的position
			PlayerService.servicePosition = intent.getIntExtra(
					PlayerConstant.PLAYER_POSITION, 0);
			// state改变为play，播放歌曲
			PlayerService.state = PlayerConstant.STATE_PLAY;
			// 如果state改变
			PlayerService.stateChange = true;
		}

		// 如果收到的是返回应用的广播
		if (action.equals(ACTION_BACKUP_ZERO)) {
			LogHelper.logD("=============进来了吗？？？？？");
			// 获取ActivityManager
			ActivityManager mAm = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			// 获得当前运行的task
			List<ActivityManager.RunningTaskInfo> taskList = mAm
					.getRunningTasks(100);
			for (ActivityManager.RunningTaskInfo rti : taskList) {

				// 找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
				if (rti.topActivity.getPackageName().equals(
						context.getPackageName())) {
					try {
						Intent resultIntent = new Intent(context,
								Class.forName(rti.topActivity.getClassName()));
						resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
								| Intent.FLAG_ACTIVITY_SINGLE_TOP);
						context.startActivity(resultIntent);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					return;
				}
			}

			// 若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainactivity
			Intent resultIntent = new Intent(context, MenuActivity.class);
			resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			context.startActivity(resultIntent);
		}
		
		// 如果接收的是点击暂停/播放键时的广播
		if(action.equals(ACTION_PLAY_BUTTON)){
			if (PlayerService.serviceMusicList!= null) {
				
				// 根据当前状态点击后，进行相应状态改变
				switch (PlayerService.state) {
				case PlayerConstant.STATE_PLAY:
				case PlayerConstant.STATE_CONTINUE:
					PlayerService.state  = PlayerConstant.STATE_PAUSE;
					break;
				case PlayerConstant.STATE_PAUSE:
					PlayerService.state  = PlayerConstant.STATE_CONTINUE;
					break;
				case PlayerConstant.STATE_STOP:
					PlayerService.state  = PlayerConstant.STATE_PLAY;
					break;
				}
				// state改变
				PlayerService.stateChange = true;
			}
		}
		
		// 如果接收的是上一首广播
		if(action.equals(ACTION_PLAY_PREVIOUS)){
			if (PlayerService.serviceMusicList != null) {
				// 点击上一首按钮，如果当前位置为0，退回歌曲列表最后一首
				if (PlayerService.servicePosition == 0) {
					PlayerService.servicePosition = PlayerService.serviceMusicList.size() - 1;
				} else {
					PlayerService.servicePosition--;
				}
				// state改变
				PlayerService.state = PlayerConstant.STATE_PLAY;
				PlayerService.stateChange = true;
			}
		}
		
		// 如果接收的是下一首广播
		if(action.equals(ACTION_PLAY_NEXT)){
			if (PlayerService.serviceMusicList != null) {
				// 点击下一首，根据播放模式不同，下一首位置不同
				switch (PlayerService.mode) {
				case PlayerConstant.MODE_SINGLE:
					PlayerService.state = PlayerConstant.STATE_PLAY;
					break;
				case PlayerConstant.MODE_LOOP:
					if (PlayerService.servicePosition == PlayerService.serviceMusicList.size() - 1) {
						PlayerService.servicePosition = 0;
					} else {
						PlayerService.servicePosition++;
					}
					PlayerService.state = PlayerConstant.STATE_PLAY;
					break;
				case PlayerConstant.MODE_RANDOM:
					Random random = new Random();
					int p = PlayerService.servicePosition;
					while (true) {
						PlayerService.servicePosition = random.nextInt(PlayerService.serviceMusicList
								.size());

						if (p != PlayerService.servicePosition) {
							PlayerService.state = PlayerConstant.STATE_PLAY;
							break;
						}
					}
					break;
				case PlayerConstant.MODE_ORDER:
					if (PlayerService.servicePosition == PlayerService.serviceMusicList.size() - 1) {
						PlayerService.state = PlayerConstant.STATE_STOP;
					} else {
						PlayerService.servicePosition++;
						PlayerService.state = PlayerConstant.STATE_PLAY;
					}
					break;
				}
				// state改变
				PlayerService.stateChange = true;
			}
		}
		
		//播放模式的广播
		if(action.equals(ACTION_PLAY_MODE)){
			LogHelper.logD("更改播放模式，当前播放模式为"+PlayerService.mode);
			switch (PlayerService.mode) {
			// 根据当前mode，做出mode的更改
			case PlayerConstant.MODE_SINGLE:
				PlayerService.mode = PlayerConstant.MODE_ORDER;
				break;
			case PlayerConstant.MODE_LOOP:
				PlayerService.mode = PlayerConstant.MODE_RANDOM;
				break;
			case PlayerConstant.MODE_RANDOM:
				PlayerService.mode = PlayerConstant.MODE_SINGLE;
				break;
			case PlayerConstant.MODE_ORDER:
				PlayerService.mode = PlayerConstant.MODE_LOOP;
				break;
			default:
				break;
			}
			// 播放模式改变
			PlayerService.modeChange=true;
		}
		
		// seekbar发送的广播
		if(action.equals(ACTION_SEEKBAR)){
			// 得到传过来的当前进度条进度，更改歌曲播放位置
			int progress = intent.getIntExtra(PlayerConstant.SEEKBAR_PROGRESS,0);
			int state=intent.getIntExtra(PlayerConstant.PLAYER_STATE, 0);
			PlayerService.playerSeekToTime(progress,state);
			// 进度条改变
			PlayerService.seekChange = true;
		}
		
		//更新list的广播
		if(action.equals(ACTION_UPDATE_LIST)){
			LogHelper.logD("-------》修改了LIST");
			//得到参数
			int position=intent.getIntExtra(PlayerConstant.PLAYER_POSITION, 0);
			String album=intent.getStringExtra(PlayerConstant.PLAYER_LIST_ALBUM);
			//修改指定的值
			PlayerService.serviceMusicList.get(position).setAlbum(album);
		}
		
		if(action.contains(ACTION_AUTO_START)){
			Intent i=new Intent();
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra(AppConstant.TIMING_OPEN_AUTO,true);
			i.setClass(context,MenuActivity.class);
			context.startActivity(i);
		}
	}
}
