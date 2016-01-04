package com.ganxin.zeromusic.module.home.favor;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
 * @Description 我喜欢音乐界面
 * @author ganxin
 * @date Oct 2, 2014
 * @email ganxinvip@163.com
 */
public class FavorMusicActivity extends BaseActivity {
	
	// 歌手显示的listview
	private ListView favorMusic;
	// 无歌曲时显示的图片
	private ImageView nothingImg;
	// 界面标题栏的标题
	private TextView title,rightDiviler;
	// 标题栏的返回按钮、删除按钮
	private ImageButton backBtn,scanBtn;
	// 我喜欢的适配器adapter
	private FavorMusicListAdapter favorMusicAdapter;
	// 数据库帮助类
	private MusicDBHelper dbHelper;
	// 查询数据库的cursor，自定义adapter的数据源
	private Cursor curFav;
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
	 * 界面按钮的初始化操作
	 */
	private void setButtonClick(){
		backBtn=(ImageButton) findViewById(R.id.local_actionbar_back);
		scanBtn=(ImageButton) findViewById(R.id.local_actionbar_scan);
		title = (TextView) findViewById(R.id.local_actionbar_title);
		rightDiviler = (TextView) findViewById(R.id.local_actionbar_right_diviler);
		
		backBtn.setOnClickListener(this);
		scanBtn.setVisibility(View.INVISIBLE);
		title.setText(R.string.ilike);
		rightDiviler.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 刚进入界面时，查询本地数据库得到数据给listview
	 */
	private void initListView(){
		nothingImg = (ImageView) findViewById(R.id.local_nothing_img);
		favorMusic = (ListView) findViewById(R.id.local_listview);
		dbHelper = new MusicDBHelper(FavorMusicActivity.this);
		
		new Thread() {
			public void run() {
				curFav = dbHelper.queryFavFromLocal();
				handler.sendMessage(handler.obtainMessage(0, curFav));
			};
		}.start();		
	}
	
	/**
	 * 设置listView监听事件
	 */
	private void setListViewClick() {
		// TODO Auto-generated method stub
		favorMusic.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//将musicList放在这里赋值，解决在取消喜欢歌曲时，再次播放歌曲不一致的bug
				Cursor cursor=favorMusicAdapter.getCursor();
				musicList =dbHelper.getMusicListFromCursor(cursor);
				
				// 点击item传一个musicList和当前点击位置给service
				Intent intent = new Intent();
				intent.setAction(PlayerReceiver.ACTION_PLAY_ITEM);
				intent.putParcelableArrayListExtra(PlayerConstant.PLAYER_LIST,musicList);
				intent.putExtra(PlayerConstant.PLAYER_WHERE, "local");
				intent.putExtra(PlayerConstant.PLAYER_POSITION, position-1);
				
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
					if(favorMusicAdapter!=null)
					{
						favorMusicAdapter.setPlayMusicTitle(musicList.get(position).getTitle());
						favorMusicAdapter.setPlayMusicArtist(musicList.get(position).getArtist());						
						favorMusicAdapter.notifyDataSetChanged();
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
			curFav = (Cursor) msg.obj;
			if (curFav.getCount() != 0){
				nothingImg.setVisibility(View.GONE);
				favorMusic.setVisibility(View.VISIBLE);				
				View headView = getLayoutInflater().inflate(
						R.layout.favor_music_headview, null);
				favorMusicAdapter=new FavorMusicListAdapter(FavorMusicActivity.this, curFav);

				favorMusic.addHeaderView(headView);
				favorMusic.setAdapter(favorMusicAdapter);
				
				if (favorMusicAdapter!=null) {
					// 注册状态改变监听事件
					PlayerService.registerStateChangeListener(stateChangeListener);
				}								
			}
			else{
				nothingImg.setImageResource(R.drawable.error_nosimilar_playlist);
				nothingImg.setVisibility(View.VISIBLE);
				favorMusic.setVisibility(View.GONE);
				ToastHelper.show(FavorMusicActivity.this,R.string.favor_not_add_music);
			}
			
			
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
		curFav.close();
		dbHelper.close();
		super.onDestroy();
	}
}
