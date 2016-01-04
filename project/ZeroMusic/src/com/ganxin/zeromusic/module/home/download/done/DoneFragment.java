package com.ganxin.zeromusic.module.home.download.done;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.ganxin.zeromusic.application.PlayerConstant;
import com.ganxin.zeromusic.common.bean.MusicBean;
import com.ganxin.zeromusic.common.db.MusicDBHelper;
import com.ganxin.zeromusic.common.listener.OnDownloadFinishListener;
import com.ganxin.zeromusic.common.listener.OnPlayerStateChangeListener;
import com.ganxin.zeromusic.common.manager.DownloadManager;
import com.ganxin.zeromusic.common.util.LogHelper;
import com.ganxin.zeromusic.common.util.ToastHelper;
import com.ganxin.zeromusic.receiver.PlayerReceiver;
import com.ganxin.zeromusic.service.DownloadService;
import com.ganxin.zeromusic.service.PlayerService;
import com.ganxin.zeromusic.view.R;

/**
 * 
 * @Description 已下载界面
 * @author ganxin
 * @date Apr 12, 2015
 * @email ganxinvip@163.com
 */
public class DoneFragment extends Fragment {

	private View parentView;
	private ImageView nothingImageView;
	private ListView mListView;
	private DoneListAdapter mAdapter;
	private MusicDBHelper dbHelper;
	private Cursor curDownload;
	// 播放音乐的队列
	private ArrayList<MusicBean> musicList;
	// 播放状态改变监听器
	private OnPlayerStateChangeListener stateChangeListener;
	// 播放状态flag,用于判断是否第一次进入
	private Boolean stateFlag = false;
	
	private DownloadManager downloadManager;
	private OnDownloadFinishListener downLoadFinishListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		parentView = inflater.inflate(R.layout.home_download_done, container,
				false);
		initUI();
		initData();
		return parentView;
	}

	private void initUI() {
		// TODO Auto-generated method stub
		mListView = (ListView) parentView.findViewById(R.id.done_listview);
		nothingImageView = (ImageView) parentView
				.findViewById(R.id.done_nothing_img);
	}

	private void initData() {
		// TODO Auto-generated method stub
		dbHelper = new MusicDBHelper(getActivity());
		downloadManager=DownloadService.getDownloadManager(getActivity());
		downLoadFinishListener=new OnDownloadFinishListener() {
			
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				handler.postDelayed(updateListRunnable,100);
			}
		};
		
		downloadManager.addDownloadFinishListener(downLoadFinishListener);

		new Thread() {
			public void run() {
				curDownload = dbHelper.queryDownloadFromLocal();
				handler.sendMessage(handler.obtainMessage(0, curDownload));
			};
		}.start();
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Cursor cursor=mAdapter.getCursor();
				if(cursor!=null){
					musicList =dbHelper.getMusicListFromCursor(cursor);
					
					// 点击item传一个musicList和当前点击位置给service
					Intent intent = new Intent();
					intent.setAction(PlayerReceiver.ACTION_PLAY_ITEM);
					intent.putParcelableArrayListExtra(PlayerConstant.PLAYER_LIST,musicList);
					intent.putExtra(PlayerConstant.PLAYER_WHERE, "local");
					intent.putExtra(PlayerConstant.PLAYER_POSITION, position);
					
					// 发送相应广播给service
					getActivity().sendBroadcast(intent);
				}
			}
		});
		
		stateChangeListener=new OnPlayerStateChangeListener() {
			
			@Override
			public void onStateChange(int state, int mode, List<MusicBean> musicList,
					int position) {
				// TODO Auto-generated method stub
				if(musicList!=null){
					if(mAdapter!=null)
					{
						mAdapter.setPlayMusicTitle(musicList.get(position).getTitle());
						mAdapter.setPlayMusicArtist(musicList.get(position).getArtist());						
						mAdapter.notifyDataSetChanged();
					}
				}
			}
		};
	}
	
	private Runnable updateListRunnable=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			curDownload = dbHelper.queryDownloadFromLocal();
			handler.sendMessage(handler.obtainMessage(0, curDownload));
		}
	};

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// 根据what判断进行不同的操作
			switch (msg.what) {
			case 0:
				curDownload = (Cursor) msg.obj;
				if (curDownload.getCount()!= 0){
					nothingImageView.setVisibility(View.GONE);
					mListView.setVisibility(View.VISIBLE);
					mAdapter = new DoneListAdapter(getActivity(),curDownload);
					mListView.setAdapter(mAdapter);	
				}
				else{
					nothingImageView.setVisibility(View.VISIBLE);
					mListView.setVisibility(View.GONE);
					ToastHelper.show(getActivity(),R.string.done_not_music_msg);
				}
				break;
			default:
				break;
			}
			
			if (mAdapter!=null) {
				// 注册状态改变监听事件
				PlayerService.registerStateChangeListener(stateChangeListener);
			}
		}
	};

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (stateFlag) {
			// 注册状态改变监听事件
			PlayerService.registerStateChangeListener(stateChangeListener);
		}
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 解除注册状态改变监听事件
		PlayerService.removeStateChangeListener(stateChangeListener);
		stateFlag = true;
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		// 关闭cursor和数据库
		curDownload.close();
		dbHelper.close();
		
		if(downLoadFinishListener!=null){
			downloadManager.removeDownloadFinishListener(downLoadFinishListener);
		}
		super.onDestroyView();
	}
	
	public void notifiyDataChange(){
		if(handler!=null){
			LogHelper.logD("------222");
			handler.postDelayed(updateListRunnable,100);
		}
	}
}
