package com.ganxin.zeromusic.module.home.artist;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.ganxin.zeromusic.application.PlayerConstant;
import com.ganxin.zeromusic.common.bean.MusicBean;
import com.ganxin.zeromusic.common.db.MusicDBHelper;
import com.ganxin.zeromusic.common.listener.OnPlayerStateChangeListener;
import com.ganxin.zeromusic.framework.BaseActivity;
import com.ganxin.zeromusic.module.home.local.LocalMusicListAdapter;
import com.ganxin.zeromusic.receiver.PlayerReceiver;
import com.ganxin.zeromusic.service.PlayerService;
import com.ganxin.zeromusic.view.R;

/**
 * 
 * @Description 歌手音乐界面
 * @author ganxin
 * @date Mar 13, 2015
 * @email ganxinvip@163.com
 */
public class ArtistMusicActivity extends BaseActivity{
	// 传参过来的歌手名
	private String artist_title;
	//界面标题栏的标题
	private TextView title,rightDiviler;
	// 标题栏的返回按钮、扫描按钮
	private ImageButton backBtn,scanBtn;
	// 歌曲显示的listview
	private ListView lvMusic;
	// 数据库帮助类
    private MusicDBHelper localDbHelper;
	// 查询本地数据库得到的cursor
	private Cursor cur;
	// 本地歌曲适配器adapter
	private LocalMusicListAdapter adapter;
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
		
		// 接到intent传来的参数，歌手名
		Intent intent = getIntent();
		artist_title = intent.getStringExtra("artist");
		
		setButtonClick();
		initListView();
		setListViewClick();
		initData();
	}

	/**
	 * 界面按钮的初始化操作
	 */
	private void setButtonClick(){
		backBtn=(ImageButton) findViewById(R.id.local_actionbar_back);
		scanBtn=(ImageButton) findViewById(R.id.local_actionbar_scan);
		title = (TextView) findViewById(R.id.local_actionbar_title);
		rightDiviler = (TextView) findViewById(R.id.local_actionbar_right_diviler);
		
		backBtn.setOnClickListener(this);
		scanBtn.setVisibility(View.INVISIBLE);
		title.setText(artist_title);
		rightDiviler.setVisibility(View.INVISIBLE);
	}
		
	/**
	 * 刚进入界面时，查询本地数据库得到数据给listview
	 */
	private void initListView(){
		lvMusic = (ListView) findViewById(R.id.local_listview);
		new Thread() {
			public void run() {
				localDbHelper = new MusicDBHelper(ArtistMusicActivity.this);
				cur = localDbHelper.queryLocalByArtist(artist_title);
				handler.sendMessage(handler.obtainMessage(0, cur));
			};
		}.start();
	}
	
	/**
	 * 设置listView监听事件
	 */
	private void setListViewClick() {
		// TODO Auto-generated method stub
		lvMusic.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Cursor cursor=adapter.getCursor();
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
					if(adapter!=null)
					{
						adapter.setPlayMusicTitle(musicList.get(position).getTitle());
						adapter.setPlayMusicArtist(musicList.get(position).getArtist());	
						adapter.notifyDataSetChanged();
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
		default:
			break;
		}		
	}
	
	//匿名内部类 该handler用于listview的显示
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			cur = (Cursor) msg.obj;
			adapter = new LocalMusicListAdapter(ArtistMusicActivity.this, cur);
			lvMusic.setAdapter(adapter);
			// 注册状态改变监听事件
			PlayerService.registerStateChangeListener(stateChangeListener);
		}		
	};
	
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
		cur.close();
		localDbHelper.close();
		super.onDestroy();
	}
}
