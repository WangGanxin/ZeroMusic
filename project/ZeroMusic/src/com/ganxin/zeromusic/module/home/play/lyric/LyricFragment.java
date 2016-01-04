package com.ganxin.zeromusic.module.home.play.lyric;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ganxin.zeromusic.common.bean.MusicBean;
import com.ganxin.zeromusic.common.listener.OnPlayerStateChangeListener;
import com.ganxin.zeromusic.common.listener.OnSeekChangeListener;
import com.ganxin.zeromusic.common.util.StringHelper;
import com.ganxin.zeromusic.common.widget.lyric.DefaultLrcBuilder;
import com.ganxin.zeromusic.common.widget.lyric.LrcRow;
import com.ganxin.zeromusic.common.widget.lyric.LrcView;
import com.ganxin.zeromusic.service.PlayerService;
import com.ganxin.zeromusic.view.R;

/**
 * 
 * @Description 歌词显示界面
 * @author ganxin
 * @date Mar 13, 2015
 * @email ganxinvip@163.com
 */
public class LyricFragment extends Fragment{
	private View parentView;
	
	//歌词显示控件
	private LrcView lrcView;
	// 回调函数更新UI
	private OnPlayerStateChangeListener stateChangeListener;
	private OnSeekChangeListener seekChangeListener;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		parentView = inflater.inflate(R.layout.home_play_lyric, container, false);
		initUI();
        initData();
		return parentView;
	}

	private void initUI() {
		// TODO Auto-generated method stub
		lrcView=(LrcView) parentView.findViewById(R.id.player_lyric_view);
	}

	private void initData() {
		// TODO Auto-generated method stub
		stateChangeListener=new OnPlayerStateChangeListener() {
			
			@Override
			public void onStateChange(int state, int mode, List<MusicBean> musicList,
					int position) {
				// TODO Auto-generated method stub
				if(musicList!=null){
					try {
						//获取歌词路径
						String lrcName = musicList.get(position).getLyric_file_name();
						String path=musicList.get(position).getPath();					
						String florder=StringHelper.getFileFolderPath(path);
						//获取歌词字符串
						String lrcStr = getFromLrcFile(florder+lrcName);
						// 解析歌词
						DefaultLrcBuilder builder = new DefaultLrcBuilder();
						List<LrcRow> rows = builder.getLrcRows(lrcStr);
						// 设置歌词
						lrcView.setLrc(rows);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		seekChangeListener=new OnSeekChangeListener() {
			
			@Override
			public void onSeekChange(int progress, int max, String time, String duration) {
				// TODO Auto-generated method stub
			    lrcView.seekLrcToTime(progress);
			}
		};
	}

	// lrc字符串拼接
	@SuppressWarnings("resource")
	private String getFromLrcFile(String lrcPath) {
		// TODO Auto-generated method stub

		try {
			InputStreamReader inputReader = new InputStreamReader(
					new FileInputStream(lrcPath));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			String Result = "";
			while ((line = bufReader.readLine()) != null) {
				if (line.trim().equals(""))
					continue;
				Result += line + "\r\n";
			}
			return Result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// 注册播放状态改变、进度条的监听器
		PlayerService.registerStateChangeListener(stateChangeListener);
		PlayerService.registerSeekChangeListener(seekChangeListener);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		// 解除注册状态改变、进度条的监听器
		PlayerService.removeStateChangeListener(stateChangeListener);
		PlayerService.removeSeekChangeListener(seekChangeListener);
	}	
	
}
