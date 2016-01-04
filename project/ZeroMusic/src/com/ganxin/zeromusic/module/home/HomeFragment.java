package com.ganxin.zeromusic.module.home;

import grd.lks.oew.AdManager;
import grd.lks.oew.onlineconfig.OnlineConfigCallBack;

import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.ganxin.zeromusic.application.AppConstant;
import com.ganxin.zeromusic.application.PlayerConstant;
import com.ganxin.zeromusic.common.bean.MusicBean;
import com.ganxin.zeromusic.common.db.MusicDBHelper;
import com.ganxin.zeromusic.common.http.volleyImage.ImageCacheManager;
import com.ganxin.zeromusic.common.listener.OnPlayerStateChangeListener;
import com.ganxin.zeromusic.common.util.LogHelper;
import com.ganxin.zeromusic.common.util.SharPreferHelper;
import com.ganxin.zeromusic.common.util.ToastHelper;
import com.ganxin.zeromusic.module.MenuActivity;
import com.ganxin.zeromusic.module.home.artist.ArtistSelectActivity;
import com.ganxin.zeromusic.module.home.download.DownloadActivity;
import com.ganxin.zeromusic.module.home.favor.FavorMusicActivity;
import com.ganxin.zeromusic.module.home.local.LocalMusicActivity;
import com.ganxin.zeromusic.module.home.play.PlayActivity;
import com.ganxin.zeromusic.receiver.PlayerReceiver;
import com.ganxin.zeromusic.service.PlayerService;
import com.ganxin.zeromusic.view.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @Description 我的零听主界面
 * @author ganxin
 * @date Oct 2, 2014
 * @email ganxinvip@163.com
 */
public class HomeFragment extends Fragment implements OnClickListener {

	private View parentView;
	// 我的零听界面点击跳转view
	private LinearLayout localMusic, artist, download, favor;
	//本地头部图片
	private ImageView localHeadView;
	//网络头部图片
	private NetworkImageView onlineHeadView;
	// 显示歌曲数目的控件
	private TextView localMusic_Text, artist_text, download_text, favor_text;
	// 播放器的三个按钮：显示歌词界面、播放、音量控制
	private ImageButton lyricBtn, playerBtn, volumeBtn;
	// 播放状态改变UI的监听器
	private OnPlayerStateChangeListener stateChangeListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		parentView = inflater.inflate(R.layout.fragment_home, container, false);
		setButtonClick();
		setMusicAmount();
		initData();
		initOnlineParams();
		return parentView;
	}

	/**
	 * 界面各个按钮的初始化操作
	 */
	private void setButtonClick() {

		lyricBtn = (ImageButton) parentView.findViewById(R.id.home_to_lyric);
		playerBtn = (ImageButton) parentView
				.findViewById(R.id.home_player_play);
		volumeBtn = (ImageButton) parentView
				.findViewById(R.id.home_player_volume);

		localMusic = (LinearLayout) parentView
				.findViewById(R.id.home_local_music_item);
		artist = (LinearLayout) parentView.findViewById(R.id.home_artist_item);
		download = (LinearLayout) parentView
				.findViewById(R.id.home_download_item);
		favor = (LinearLayout) parentView.findViewById(R.id.home_favor_item);
		
		localHeadView=(ImageView) parentView.findViewById(R.id.home_head);
		onlineHeadView=(NetworkImageView) parentView.findViewById(R.id.home_recommend_today_pic);

		localMusic.setOnClickListener(this);
		artist.setOnClickListener(this);
		download.setOnClickListener(this);
		favor.setOnClickListener(this);

		lyricBtn.setOnClickListener(this);
		playerBtn.setOnClickListener(this);
		volumeBtn.setOnClickListener(this);
	}

	/**
	 * 设置界面各类歌曲的数目
	 */
	private void setMusicAmount() {
		localMusic_Text = (TextView) parentView
				.findViewById(R.id.home_local_music_text);
		artist_text = (TextView) parentView.findViewById(R.id.home_artist_text);
		download_text = (TextView) parentView
				.findViewById(R.id.home_download_text);
		favor_text = (TextView) parentView.findViewById(R.id.home_favor_text);
		download_text = (TextView) parentView.findViewById(R.id.home_download_text);
		

		// 设置各类歌曲的数目
		int[] musicAmount = getMusicAmountData();
		localMusic_Text.setText(musicAmount[0] + "首");
		artist_text.setText(musicAmount[1] + "个歌手");
		favor_text.setText(musicAmount[2] + "首歌");
		download_text.setText(musicAmount[3]+"首");

	}

	private void initData() {
		// 设置初始音量模式
		SharPreferHelper.writeBoolean(getActivity(),
				AppConstant.VOLUME_MODE_KEY, true);
		
		stateChangeListener=new OnPlayerStateChangeListener() {
			
			@Override
			public void onStateChange(int state, int mode, List<MusicBean> musicList,
					int position) {
				// TODO Auto-generated method stub
				switch (state) {
				case PlayerConstant.STATE_PLAY:
					playerBtn.setImageResource(R.drawable.player_pause);
					break;
				case PlayerConstant.STATE_CONTINUE:
					playerBtn.setImageResource(R.drawable.player_pause);
					break;
				case PlayerConstant.STATE_PAUSE:
					playerBtn.setImageResource(R.drawable.player_play);
					break;
				case PlayerConstant.STATE_STOP:
					playerBtn.setImageResource(R.drawable.player_play);
					break;
				default:
					break;
				}
			}
		};
	}
	
	private void initOnlineParams() {
		// TODO Auto-generated method stub
		AdManager.getInstance(getActivity()).asyncGetOnlineConfig(AppConstant.HOME_ONLINE_KEY,new OnlineConfigCallBack() {
			
			@Override
			public void onGetOnlineConfigSuccessful(String key, String value) {

				if(value!=null&&value.startsWith("http")&&!value.endsWith(".gif")){
					onlineHeadView.setDefaultImageResId(R.drawable.default_cover);
					onlineHeadView.setErrorImageResId(R.drawable.default_cover);
					onlineHeadView.setImageUrl(value,ImageCacheManager.getInstance().getImageLoader());;
					onlineHeadView.setVisibility(View.VISIBLE);
					onlineHeadView.startAnimation(AnimationUtils.loadAnimation(getActivity(),android.R.anim.fade_in));
					localHeadView.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void onGetOnlineConfigFailed(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.home_to_lyric:
			changeView(PlayActivity.class);
			break;
		case R.id.home_player_play:
			// 发送广播给service
			Intent intent = new Intent();
			intent.setAction(PlayerReceiver.ACTION_PLAY_BUTTON);
			getActivity().sendBroadcast(intent);
			break;
		case R.id.home_player_volume:
			chageVolumeMode();
			break;
		case R.id.home_local_music_item:
			changeView(LocalMusicActivity.class);
			break;
		case R.id.home_artist_item:
			changeView(ArtistSelectActivity.class);
			break;
		case R.id.home_download_item:
			changeView(DownloadActivity.class);
			break;
		case R.id.home_favor_item:
			changeView(FavorMusicActivity.class);
			break;
		default:
			break;
		}
	}

	/**
	 * 更改操作界面
	 * 
	 * @param target
	 *            目标界面
	 */
	private void changeView(Class<?> target) {
		Intent intent = new Intent(getActivity(), target);
		getActivity().startActivity(intent);
	}

	/**
	 * 更改音量模式
	 * 
	 */
	private void chageVolumeMode() {
		boolean volumeMode = SharPreferHelper.readBoolean(getActivity(),
				AppConstant.VOLUME_MODE_KEY, true);

		if (volumeMode) {
			LogHelper.logD("111---静音状态");
			volumeBtn.setImageResource(R.drawable.player_volume_mute);

			// 设置静音
			MenuActivity.audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);

			SharPreferHelper.writeBoolean(getActivity(),
					AppConstant.VOLUME_MODE_KEY, false);
			SharPreferHelper.writeBoolean(getActivity(),
					AppConstant.VOLUME_IS_MUTE, true);
			ToastHelper.show(getActivity(), R.string.set_mute_mode);
		} else {
			LogHelper.logD("111---非静音");
			volumeBtn.setImageResource(R.drawable.player_volume);

			// 取消静音
			MenuActivity.audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);

			SharPreferHelper.writeBoolean(getActivity(),
					AppConstant.VOLUME_MODE_KEY, true);
			SharPreferHelper.writeBoolean(getActivity(),
					AppConstant.VOLUME_IS_MUTE, false);
			ToastHelper.show(getActivity(), R.string.cancel_mute_mode);
		}
	}

	/**
	 * 检查音量模式
	 */
	private void checkVolumeMode() {
		boolean isMute = SharPreferHelper.readBoolean(getActivity(),
				AppConstant.VOLUME_IS_MUTE, false);
		if (isMute)
			volumeBtn.setImageResource(R.drawable.player_volume_mute);
		else
			volumeBtn.setImageResource(R.drawable.player_volume);
	}

	/**
	 * 获取分类的歌曲的数量
	 * 
	 * @return musicAmount 以数组形式存放各类歌曲数量
	 */
	private int[] getMusicAmountData() {
		MusicDBHelper dbHelper = new MusicDBHelper(getActivity());
		Cursor curLocal = dbHelper.queryLocalMusicByID();
		Cursor curArtist = dbHelper.queryArtistByID();
		Cursor curFavor = dbHelper.queryFavByID();
		Cursor curDownload=dbHelper.queryDownloadByID();

		int[] musicAmount = new int[] { curLocal.getCount(),
				curArtist.getCount(), curFavor.getCount() ,curDownload.getCount()};

		curLocal.close();
		curArtist.close();
		curFavor.close();
		curDownload.close();
		dbHelper.close();

		return musicAmount;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub		
		super.onResume();
		
		MobclickAgent.onPageStart(getClass().getSimpleName());

		setMusicAmount();
		checkVolumeMode();
		
		// 注册播放状态改变的监听器
		PlayerService.registerStateChangeListener(stateChangeListener);

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub		
		super.onPause();
		
		MobclickAgent.onPageEnd(getClass().getSimpleName()); 
		 
		// 解除注册状态改变监听器
		PlayerService.removeStateChangeListener(stateChangeListener);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
        //退出应用时重置静音标识
		SharPreferHelper.writeBoolean(getActivity(),AppConstant.VOLUME_IS_MUTE,false);
	}
		
}
