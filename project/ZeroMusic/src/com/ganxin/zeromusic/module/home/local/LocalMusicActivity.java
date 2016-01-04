package com.ganxin.zeromusic.module.home.local;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.ganxin.zeromusic.application.PlayerConstant;
import com.ganxin.zeromusic.common.bean.MusicBean;
import com.ganxin.zeromusic.common.db.MusicDBHelper;
import com.ganxin.zeromusic.common.listener.OnPlayerStateChangeListener;
import com.ganxin.zeromusic.common.util.ToastHelper;
import com.ganxin.zeromusic.framework.BaseActivity;
import com.ganxin.zeromusic.receiver.PlayerReceiver;
import com.ganxin.zeromusic.service.PlayerService;
import com.ganxin.zeromusic.view.R;

/**
 * 
 * @Description 本地音乐的列表界面
 * @author ganxin
 * @date Sep 18, 2014
 * @email ganxinvip@163.com
 */
public class LocalMusicActivity extends BaseActivity {

	//标题栏的返回按钮、扫描按钮
	private ImageButton backBtn,scanBtn;
	// 歌曲显示的listview
	private ListView lvMusic;
	// 无歌曲时显示的图片
	private ImageView nothingImg;
	// 数据库帮助类
	private MusicDBHelper localDbHelper;
	// 查询本地数据库得到的cursor
	private Cursor curLocal;
	// 本地歌曲适配器adapter
	private LocalMusicListAdapter lvMusicAdapter;
	// 正在扫描对话框
	private Dialog scanDialog;
	// 扫描线程
	private ScanThread scanThread;	
	// 播放音乐的队列
	private ArrayList<MusicBean> musicList;
	// 播放状态改变监听器
	private OnPlayerStateChangeListener stateChangeListener;
	// 播放状态flag,用于判断是否第一次进入
	private Boolean stateFlag = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_music);		
		setButtonClick();
		initListView();
		setListViewClick();
		initData();
	}

	/**
	 * 界面各个按钮的初始化操作
	 */
	private void setButtonClick(){
		backBtn=(ImageButton) findViewById(R.id.local_actionbar_back);
		scanBtn=(ImageButton) findViewById(R.id.local_actionbar_scan);
		
		backBtn.setOnClickListener(this);
		scanBtn.setOnClickListener(this);
	}
	
	/**
	 * 刚进入界面时，查询本地数据库得到数据给listview
	 */
	private void initListView(){
		nothingImg=(ImageView) findViewById(R.id.local_nothing_img);
		lvMusic=(ListView) findViewById(R.id.local_listview);
		localDbHelper=new MusicDBHelper(LocalMusicActivity.this);
		
		new Thread() {
			public void run() {
				curLocal = localDbHelper.queryLocalMusicByID();
				handler.sendMessage(handler.obtainMessage(2, curLocal));
			};
		}.start();
	}

	/**
	 * 设置listView监听事件
	 */
	private void setListViewClick(){
		lvMusic.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Cursor cursor=lvMusicAdapter.getCursor();
				musicList =localDbHelper.getMusicListFromCursor(cursor);
				
				// 点击item传一个musicList和当前点击位置给service
				Intent intent = new Intent();
				intent.setAction(PlayerReceiver.ACTION_PLAY_ITEM);
				intent.putParcelableArrayListExtra(PlayerConstant.PLAYER_LIST,musicList);
				intent.putExtra(PlayerConstant.PLAYER_WHERE, "local");
				intent.putExtra(PlayerConstant.PLAYER_POSITION, position);
				
				// 发送相应广播给service
				sendBroadcast(intent);
			}
		});
	}
	
	private void initData() {
		// TODO Auto-generated method stub
		stateChangeListener=new OnPlayerStateChangeListener() {
			
			@Override
			public void onStateChange(int state, int mode, List<MusicBean> musicList,
					int position) {
				// TODO Auto-generated method stub
				if(musicList!=null){
					if(lvMusicAdapter!=null)
					{
						lvMusicAdapter.setPlayMusicTitle(musicList.get(position).getTitle());
						lvMusicAdapter.setPlayMusicArtist(musicList.get(position).getArtist());						
						lvMusicAdapter.notifyDataSetChanged();
					}
				}
			}
		};
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.local_actionbar_back:
			this.finish();
			break;
		case R.id.local_actionbar_scan:
			nothingImg.setVisibility(View.GONE);
			lvMusic.setVisibility(View.VISIBLE);
			scanDialog();
			//开启扫描线程
			scanThread = new ScanThread();
			scanThread.start();
			break;
		default:
			break;
		}
	}
	
	//匿名内部类 该handler用于listview的显示
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// 根据what判断进行不同的操作
			switch(msg.what){
				case 0:
					break;
				case 1:
					curLocal = (Cursor) msg.obj;
					
					//关闭扫描对话框
					scanDialog.dismiss();
					
					if (curLocal.getCount() != 0){
						nothingImg.setVisibility(View.GONE);
						lvMusic.setVisibility(View.VISIBLE);
						lvMusicAdapter = new LocalMusicListAdapter(LocalMusicActivity.this,
								curLocal);
						lvMusic.setAdapter(lvMusicAdapter);
					}
					else{
						nothingImg.setVisibility(View.VISIBLE);
						lvMusic.setVisibility(View.GONE);
					}					
					break;
				case 2:
					curLocal = (Cursor) msg.obj;
					if (curLocal.getCount()!= 0){
						nothingImg.setVisibility(View.GONE);
						lvMusic.setVisibility(View.VISIBLE);
						lvMusicAdapter = new LocalMusicListAdapter(LocalMusicActivity.this,curLocal);
						lvMusic.setAdapter(lvMusicAdapter);	
					}
					else{
						nothingImg.setVisibility(View.VISIBLE);
						lvMusic.setVisibility(View.GONE);
						ToastHelper.show(LocalMusicActivity.this,R.string.please_scan_local_music);
					}
					break;
				case 3:
					scanDialog.dismiss();
					nothingImg.setVisibility(View.VISIBLE);
					lvMusic.setVisibility(View.GONE);
					ToastHelper.show(LocalMusicActivity.this,R.string.music_not_exist);
					break;
				case 4:
					scanDialog.dismiss();
					nothingImg.setVisibility(View.VISIBLE);
					lvMusic.setVisibility(View.GONE);
					ToastHelper.show(LocalMusicActivity.this,R.string.sdcard_not_exist);
					break;
			}
			
			if (lvMusicAdapter!=null) {
				// 注册状态改变监听事件
				PlayerService.registerStateChangeListener(stateChangeListener);
			}
		}		
	};
	
	private void scanDialog(){
		Builder builder = new AlertDialog.Builder(LocalMusicActivity.this);
		builder.setCancelable(true);
		
		View dialogView = LayoutInflater.from(LocalMusicActivity.this).inflate(
				R.layout.scan_dialog, null);
		builder.setView(dialogView);
		
		RotateAnimation anim = new RotateAnimation(0, 360,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		anim.setDuration(1000);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(-1);
		ImageView dialogImg = (ImageView) dialogView.findViewById(R.id.scan_img);		
		dialogImg.startAnimation(anim);
		
		builder.setNeutralButton(R.string.cancel,new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				handler.sendMessage(handler.obtainMessage(3));
			}
		});
		scanDialog = builder.create();
		scanDialog.show();
	}
	
	private class ScanThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				
				ContentResolver conRes = getContentResolver();
				Cursor cur = conRes.query(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
						null, 
						MediaStore.Audio.Media.DURATION + ">?", 
						new String[] { "20000" }, 
						MediaStore.Audio.Media.TITLE+" asc");			
				
				int titleIndex = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
				int artistIndex = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
				int pathIndex = cur.getColumnIndex(MediaStore.Audio.Media.DATA);
				int durationIndex = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
				int sizeIndex = cur.getColumnIndex(MediaStore.Audio.Media.SIZE);
				int fileNameIndex = cur.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
				
				localDbHelper = new MusicDBHelper(LocalMusicActivity.this);
				//modified by ganxin 2015.09.05 扫描时不再清除本地表，防止下载表的数据也同时被清除
				//localDbHelper.clearLocalMusicTable();
				
				if(cur.getCount()!=0){
					cur.moveToFirst();
					do{
						File musicFile=new File(cur.getString(pathIndex));						
						if(musicFile.exists()){
							MusicBean music=new MusicBean();
						    music.setTitle(cur.getString(titleIndex));
							music.setArtist(cur.getString(artistIndex));
							music.setPath(cur.getString(pathIndex));
							music.setDuration(cur.getLong(durationIndex));
							music.setSize(cur.getLong(sizeIndex));					
							String fileName = cur.getString(fileNameIndex);
							String lrcName = fileName.replace(".mp3", ".lrc");
							music.setLyric_file_name(lrcName);
												
							localDbHelper.insertLocal(music);
							localDbHelper.insertArtist(music);
							cur.moveToNext();	
						}
						else
							cur.moveToNext();
					}
					while(!cur.isAfterLast());
					curLocal = localDbHelper.queryLocalMusicByID();
					handler.sendMessage(handler.obtainMessage(1, curLocal));
				}
				else{
					handler.sendMessage(handler.obtainMessage(3));
				}
			}
			else{
				handler.sendMessage(handler.obtainMessage(4));
			}
			
		}
	}	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (stateFlag) {
			// 注册状态改变监听事件
			PlayerService.registerStateChangeListener(stateChangeListener);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 解除注册状态改变监听事件
		PlayerService.removeStateChangeListener(stateChangeListener);
		stateFlag = true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// 关闭cursor和数据库
		curLocal.close();
		localDbHelper.close();
		super.onDestroy();
	}
		
}
