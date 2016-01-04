package com.ganxin.zeromusic.common.util;

import java.io.IOException;
import java.util.Random;

import com.ganxin.zeromusic.application.PlayerConstant;
import com.ganxin.zeromusic.service.PlayerService;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;

/**
 * 
 * @Description 播放歌曲帮助类,单例模式
 * @author ganxin
 * @date 2014-12-10
 * @email ganxinvip@163.com
 */
public class PlayerHelper{
	private static PlayerHelper playerHelper;
	private static MediaPlayer myMedia;
	
	private PlayerHelper(){}

	public synchronized static PlayerHelper getInstance(){
		if (playerHelper == null) {
			playerHelper = new PlayerHelper();
		}
		if (myMedia == null) {
			myMedia = new MediaPlayer();
		}
		return playerHelper;
	}
	
	/**
	 * 播放函数
	 * @param musicpath 音乐路径
	 */
	public void play(String musicpath){
		try {
			myMedia.reset();
			myMedia.setAudioStreamType(AudioManager.STREAM_MUSIC);
			myMedia.setDataSource(musicpath);
			myMedia.prepare();
			myMedia.start();
			LogHelper.logD("isplaying-------->"+myMedia.isPlaying());
			myMedia.setOnCompletionListener(new OnCompletionListener(){

				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					// 歌曲播放完毕，根据播放模式选择下一首播放歌曲的position
					// 播放模式在service中存放
					// 歌曲播放列表和位置都在service中，在这直接更改service中的position和state
					switch (PlayerService.mode) {
					//随机播放
					case PlayerConstant.MODE_RANDOM:
						Random random = new Random();
						int p = PlayerService.servicePosition;
						while (true) {
							PlayerService.servicePosition = random
									.nextInt(PlayerService.serviceMusicList
											.size());
							if (p != PlayerService.servicePosition) {
								PlayerService.state = PlayerConstant.STATE_PLAY;
								break;
							}
						}
						break;
					//单曲循环
					case PlayerConstant.MODE_SINGLE:
						myMedia.setLooping(true);
						break;
					//顺序播放	
					case PlayerConstant.MODE_ORDER:
						if (PlayerService.servicePosition == PlayerService.serviceMusicList
						      .size() - 1) {
					       PlayerService.state = PlayerConstant.STATE_STOP;
				        } else {
					      PlayerService.servicePosition++;
					      PlayerService.state = PlayerConstant.STATE_PLAY;
				        }
						break;
					//循环播放	
					case PlayerConstant.MODE_LOOP:
						if (PlayerService.servicePosition == PlayerService.serviceMusicList
						    .size() - 1) {
					        PlayerService.servicePosition = 0;
				        } else {
					         PlayerService.servicePosition++;
				        }
				         PlayerService.state = PlayerConstant.STATE_PLAY;
						break;
					default:
						break;
					}
					PlayerService.stateChange = true;
				}
				
			});
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 播放在线的歌曲
	 * @param context 上下文
	 * @param uri 歌曲uri
	 */
	public void playOnline(Context context, Uri uri){
		myMedia.reset();
		myMedia.setAudioStreamType(AudioManager.STREAM_MUSIC);
		myMedia.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
			
			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				// TODO Auto-generated method stub
				PlayerService.notifyBufferingUpdateListener(percent);
			}
		});
		try {
			myMedia.setDataSource(context, uri);
			myMedia.prepare();
			myMedia.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    /**
     * 暂停
     */
	public void pause(){
		myMedia.pause();
	}

	/**
	 * 继续播放
	 */
	public void continuePlay(){
		myMedia.start();
	}
	
	/**
	 * 停止
	 */
	public void stop(){
		myMedia.stop();
	}
	
	/**
	 * 释放
	 */
	public void release(){
		myMedia.release();
	}
	
	/**
	 * 获取歌曲当前播放位置
	 * @return int 歌曲位置
	 */
	public int getPlayCurrentTime() {
		return myMedia.getCurrentPosition();
	}
	
	/**
	 * 获取歌曲时长
	 * @return int 歌曲时长
	 */
	public int getPlayDuration() {
		return myMedia.getDuration();
	}
	
	/**
	 * 按移动的指定的位置播放
	 * @param seek 指定的位置
	 */
	public void seekToMusicAndPlay(int seek) {
		myMedia.seekTo(seek);
		myMedia.start();
	}
	
	/**
	 * 按移动的指定的位置
	 * @param seek 指定的位置
	 */
	public void seekToMusic(int seek) {
		myMedia.seekTo(seek);
	}
	
	/**
	 * 判断当前是否在播放
	 * @return boolean
	 */
	public Boolean isPlaying(){
		return myMedia.isPlaying();
	}
}
