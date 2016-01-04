package com.ganxin.zeromusic.module.home.play;

import grd.lks.oew.br.AdSize;
import grd.lks.oew.br.AdView;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ganxin.zeromusic.application.AppConstant;
import com.ganxin.zeromusic.application.PlayerConstant;
import com.ganxin.zeromusic.common.bean.MusicBean;
import com.ganxin.zeromusic.common.listener.OnBufferingUpdateListener;
import com.ganxin.zeromusic.common.listener.OnModeChangeListener;
import com.ganxin.zeromusic.common.listener.OnPlayerStateChangeListener;
import com.ganxin.zeromusic.common.listener.OnSeekChangeListener;
import com.ganxin.zeromusic.common.manager.SettingManager;
import com.ganxin.zeromusic.common.util.LogHelper;
import com.ganxin.zeromusic.common.util.SharPreferHelper;
import com.ganxin.zeromusic.common.util.ToastHelper;
import com.ganxin.zeromusic.framework.BaseActivity;
import com.ganxin.zeromusic.module.MenuActivity;
import com.ganxin.zeromusic.module.home.play.lyric.LyricFragment;
import com.ganxin.zeromusic.module.home.play.picture.PictureFragment;
import com.ganxin.zeromusic.receiver.PlayerReceiver;
import com.ganxin.zeromusic.service.PlayerService;
import com.ganxin.zeromusic.view.R;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * 
 * @Description 播放显示界面
 * @author ganxin
 * @date Sep 19, 2014
 * @email ganxinvip@163.com
 */
public class PlayActivity extends BaseActivity {
	private Context context;
	// viewpager+fragment，实现左右滑动效果
	private CirclesFragmentAdapter mAdapter;
	private ViewPager mPager;
	private CirclePageIndicator mIndicator;

	// actionbar 中的后退、播放列表按钮
	private ImageButton backBtn, listBtn;
	// 图片和歌词的layout
	private LinearLayout contentLayout;
	// 当前播放歌曲的列表
	private ListView playQueue;
	// 播放列表
	private ArrayList<MusicBean> musicList;
	// actionbar 中的歌曲名、歌手
	private TextView songTv, artistTv;
	// 播放器下方的播放模式、上一首、下一首、播放、音量模式按钮
	private ImageButton playeModeBtn, preBtn, playBtn, nextBtn, volumeBtn;
	// 进度条中播放位置的时间、总时间
	private TextView timePosition, duration;
	// 播放进度条
	private SeekBar musicSeekBar;
	// 当前歌曲的播放状态
	private int playerState;

	// 回调接口更新UI---播放状态、播放模式、进度条
	private OnPlayerStateChangeListener stateChangeListener;
	private OnModeChangeListener modeChangeListener;
	private OnSeekChangeListener seekChangeListener;
	private OnBufferingUpdateListener bufferingUpdateListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context=this;
		setContentView(R.layout.activity_player_lyric);

		initUI();
		initYoumiAd();
		initData();
		setSeekBar();
		setListViewClick();
	}

	private void initUI() {
		// TODO Auto-generated method stub
		backBtn = (ImageButton) findViewById(R.id.player_actionbar_back);
		listBtn = (ImageButton) findViewById(R.id.player_actionbar_list);

		contentLayout = (LinearLayout) findViewById(R.id.player_layout_content);
		playQueue = (ListView) findViewById(R.id.player_listview);

		songTv = (TextView) findViewById(R.id.player_actionbar_song);
		artistTv = (TextView) findViewById(R.id.player_actionbar_artist);

		playeModeBtn = (ImageButton) findViewById(R.id.home_player_mode);
		preBtn = (ImageButton) findViewById(R.id.home_player_pre);
		playBtn = (ImageButton) findViewById(R.id.home_player_play);
		nextBtn = (ImageButton) findViewById(R.id.home_player_next);
		volumeBtn = (ImageButton) findViewById(R.id.home_player_volume);

		timePosition = (TextView) findViewById(R.id.player_seekbar_time);
		duration = (TextView) findViewById(R.id.player_seekbar_duration);
		musicSeekBar = (SeekBar) findViewById(R.id.player_seekbar);

		backBtn.setOnClickListener(this);
		listBtn.setOnClickListener(this);

		songTv.setOnClickListener(this);
		artistTv.setOnClickListener(this);

		playeModeBtn.setOnClickListener(this);
		preBtn.setOnClickListener(this);
		playBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		volumeBtn.setOnClickListener(this);

	}

	//有米广告配置
	private void initYoumiAd(){
		boolean adFlag=SettingManager.getInstance().getBannerAdvertisermentCheck(this);
		boolean openFlag=SharPreferHelper.getBooleanConfig(context, AppConstant.SHARPREFER_FILENAME,
				AppConstant.LOCAL_SHOW_ADVIEW,false); 
		if(adFlag&&openFlag){
			// 实例化广告条
			AdView adView = new AdView(this,AdSize.FIT_SCREEN);
			// 获取要嵌入广告条的布局
			LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
			// 将广告条加入到布局中
			adLayout.addView(adView);
		}
	}
	
	private void initData() {
		// TODO Auto-generated method stub
		// 设置viewpagerfragment
		mAdapter = new CirclesFragmentAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.home_play_viewpager);
		mPager.setAdapter(mAdapter);

		mIndicator = (CirclePageIndicator) findViewById(R.id.home_play_indicator);
		mIndicator.setViewPager(mPager);

		// 检查音量模式设置
		checkVolumeMode();

		stateChangeListener = new OnPlayerStateChangeListener() {

			@Override
			public void onStateChange(int state, int mode,
					List<MusicBean> musicList, int position) {
				// TODO Auto-generated method stub
				playerState=state;
				
				if (musicList != null) {
					// 设置播放队列
					PlayQueueAdapter adapter = new PlayQueueAdapter(
							PlayActivity.this, (ArrayList<MusicBean>) musicList);
					adapter.setPlayMusicTitle(musicList.get(position)
							.getTitle());
					adapter.setPlayMusicArtist(musicList.get(position)
							.getArtist());
					playQueue.setAdapter(adapter);

					// 设置播放列表的数据
					PlayActivity.this.musicList = (ArrayList<MusicBean>) musicList;

					// 显示当前播放歌曲信息
					songTv.setText(musicList.get(position).getTitle());
					artistTv.setText(musicList.get(position).getArtist());
					// 设置当前播放歌曲长度
					long l = musicList.get(position).getDuration();
					float longF; 
					if(l<1000){
						 longF = (float) l / 60.0f;
					}
					else{
						 longF = (float) l / 1000.0f / 60.0f;
					}
					String longStr = Float.toString(longF);
					String dur[] = longStr.split("\\.");
					float sec = Float.parseFloat("0." + dur[1]) * 60.0f;
					String secStr = "";
					if (sec < 10.0) {
						String secSub[] = String.valueOf(sec).split("\\.");
						secStr = "0" + secSub[0];
					} else {
						String secSub[] = String.valueOf(sec).split("\\.");
						secStr = secSub[0].substring(0, 2);
					}
					duration.setText(dur[0] + ":" + secStr);

					// 播放按钮图标的改变
					switch (state) {
					case PlayerConstant.STATE_PLAY:
						playBtn.setImageResource(R.drawable.player_pause);
						break;
					case PlayerConstant.STATE_CONTINUE:
						playBtn.setImageResource(R.drawable.player_pause);
						break;
					case PlayerConstant.STATE_PAUSE:
						playBtn.setImageResource(R.drawable.player_play);
						break;
					case PlayerConstant.STATE_STOP:
						playBtn.setImageResource(R.drawable.player_play);
						break;
					default:
						break;
					}
				}

			}
		};

		modeChangeListener = new OnModeChangeListener() {

			@Override
			public void onModeChange(int mode) {
				// TODO Auto-generated method stub
				switch (mode) {
				case PlayerConstant.MODE_SINGLE:
					playeModeBtn
							.setImageResource(R.drawable.player_mode_single);
					ToastHelper.show(PlayActivity.this,
							R.string.playmode_for_single);
					break;
				case PlayerConstant.MODE_LOOP:
					playeModeBtn.setImageResource(R.drawable.player_mode_loop);
					ToastHelper.show(PlayActivity.this,
							R.string.playmode_for_loop);
					break;
				case PlayerConstant.MODE_RANDOM:
					playeModeBtn
							.setImageResource(R.drawable.player_mode_random);
					ToastHelper.show(PlayActivity.this,
							R.string.playmode_for_random);
					break;
				case PlayerConstant.MODE_ORDER:
					playeModeBtn.setImageResource(R.drawable.player_mode_order);
					ToastHelper.show(PlayActivity.this,
							R.string.playmode_for_order);
					break;
				default:
					break;
				}
			}
		};

		seekChangeListener = new OnSeekChangeListener() {

			@Override
			public void onSeekChange(int progress, int max, String time,
					String duration) {
				// TODO Auto-generated method stub
				musicSeekBar.setMax(max);
				musicSeekBar.setProgress(progress);
				timePosition.setText(time);
			}
		};
		
		bufferingUpdateListener=new OnBufferingUpdateListener() {
			
			@Override
			public void onBufferingUpdate(int percent) {
				// TODO Auto-generated method stub				
				musicSeekBar.setSecondaryProgress((percent*musicSeekBar.getMax())/100);
			}
		};
		
		PlayerService.addBufferingUpdateListener(bufferingUpdateListener);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.player_actionbar_back:
			finish();
			break;
		case R.id.player_actionbar_list:
			switchContentToList();
			break;
		case R.id.home_player_mode:
			Intent intentMode = new Intent();
			intentMode.setAction(PlayerReceiver.ACTION_PLAY_MODE);
			sendBroadcast(intentMode);
			break;
		case R.id.home_player_pre:
			Intent intentPre = new Intent();
			intentPre.setAction(PlayerReceiver.ACTION_PLAY_PREVIOUS);
			sendBroadcast(intentPre);
			break;
		case R.id.home_player_play:
			Intent intentPlay = new Intent();
			intentPlay.setAction(PlayerReceiver.ACTION_PLAY_BUTTON);
			sendBroadcast(intentPlay);
			break;
		case R.id.home_player_next:
			Intent intentNext = new Intent();
			intentNext.setAction(PlayerReceiver.ACTION_PLAY_NEXT);
			sendBroadcast(intentNext);
			break;
		case R.id.home_player_volume:
			chageVolumeMode();
			break;
		default:
			break;
		}

	}

	/**
	 * 设置进度条的滑动时的监听
	 */
	private void setSeekBar() {
		// TODO Auto-generated method stub
		musicSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				// 进度条改变，发送广播，回调改变播放时间
				Intent intent = new Intent(PlayerReceiver.ACTION_SEEKBAR);
				intent.putExtra(PlayerConstant.SEEKBAR_PROGRESS,
						seekBar.getProgress());
				intent.putExtra(PlayerConstant.PLAYER_STATE, playerState);
				sendBroadcast(intent);
			}

		});
	}

	private void setListViewClick() {
		// TODO Auto-generated method stub
		playQueue.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				int position = arg2;
				Intent intent = new Intent();
				intent.setAction(PlayerReceiver.ACTION_PLAY_ITEM);
				intent.putParcelableArrayListExtra(PlayerConstant.PLAYER_LIST,
						musicList);
				intent.putExtra(PlayerConstant.PLAYER_POSITION, position);
				intent.putExtra(PlayerConstant.PLAYER_WHERE, "local");
				sendBroadcast(intent);
			}
		});
	}

	/**
	 * 更改音量模式
	 * 
	 * @param displayToast
	 *            是否显示toast
	 */
	private void chageVolumeMode() {
		boolean volumeMode = SharPreferHelper.readBoolean(this,
				AppConstant.VOLUME_MODE_KEY, true);

		if (volumeMode) {
			LogHelper.logD("222---静音状态");
			volumeBtn.setImageResource(R.drawable.player_volume_mute);

			// 设置静音
			MenuActivity.audioManager.setStreamMute(AudioManager.STREAM_MUSIC,
					true);

			SharPreferHelper.writeBoolean(this,
					AppConstant.VOLUME_MODE_KEY, false);
			SharPreferHelper.writeBoolean(this,
					AppConstant.VOLUME_IS_MUTE, true);
			ToastHelper.show(this, R.string.set_mute_mode);
		} else {
			LogHelper.logD("222---非静音");
			volumeBtn.setImageResource(R.drawable.player_volume);

			// 取消静音
			MenuActivity.audioManager.setStreamMute(AudioManager.STREAM_MUSIC,
					false);

			SharPreferHelper.writeBoolean(this,
					AppConstant.VOLUME_MODE_KEY, true);
			SharPreferHelper.writeBoolean(this,
					AppConstant.VOLUME_IS_MUTE, false);
			ToastHelper.show(this, R.string.cancel_mute_mode);
		}
	}

	/**
	 * 检查音量模式
	 */
	private void checkVolumeMode() {
		boolean isMute = SharPreferHelper.readBoolean(this,
				AppConstant.VOLUME_IS_MUTE, false);
		if (isMute)
			volumeBtn.setImageResource(R.drawable.player_volume_mute);
		else
			volumeBtn.setImageResource(R.drawable.player_volume);
	}

	/**
	 * 切换切面至播放列表
	 */
	private void switchContentToList() {
		// TODO Auto-generated method stub
		if (contentLayout.getVisibility() == View.VISIBLE) {
			contentLayout.setVisibility(View.GONE);
			playQueue.setVisibility(View.VISIBLE);
		} else {
			contentLayout.setVisibility(View.VISIBLE);
			playQueue.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// 注册播放状态改变、播放模式、进度条的监听器
		PlayerService.registerStateChangeListener(stateChangeListener);
		PlayerService.registerModeChangeListener(modeChangeListener);
		PlayerService.registerSeekChangeListener(seekChangeListener);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		// 解除注册状态改变、播放模式、进度条的监听器
		PlayerService.removeStateChangeListener(stateChangeListener);
		PlayerService.removeModeChangeListener(modeChangeListener);
		PlayerService.removeSeekChangeListener(seekChangeListener);

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(bufferingUpdateListener!=null){
			PlayerService.removeBufferingUpdateListener(bufferingUpdateListener);
		}
		super.onDestroy();
	}



	/**
	 * 
	 * @Description viewpagerfragment的适配器
	 * @author ganxin
	 * @date Mar 16, 2015
	 * @email ganxinvip@163.com
	 */
	private class CirclesFragmentAdapter extends FragmentPagerAdapter {

		public CirclesFragmentAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			Fragment fragment = null;
			switch (arg0) {
			case 0:
				fragment = new PictureFragment();
				break;
			case 1:
				fragment = new LyricFragment();
				break;
			default:
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}
	}
}
