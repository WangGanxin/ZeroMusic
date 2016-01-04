package com.ganxin.zeromusic.module.home.artist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ganxin.zeromusic.application.DbConstant;
import com.ganxin.zeromusic.common.db.MusicDBHelper;
import com.ganxin.zeromusic.common.util.ToastHelper;
import com.ganxin.zeromusic.framework.BaseActivity;
import com.ganxin.zeromusic.view.R;

/**
 * 
 * @Description 歌手选择界面
 * @author ganxin
 * @date Sep 27, 2014
 * @email ganxinvip@163.com
 */
public class ArtistSelectActivity extends BaseActivity {
	
	// 歌手显示的listview
	private ListView lvMusic;
	// 无歌曲时显示的图片
	private ImageView nothingImg;
	//界面标题栏的标题
	private TextView title,rightDiviler;
	// 标题栏的返回按钮、扫描按钮
	private ImageButton backBtn,scanBtn;
	// 数据库帮助类
	private MusicDBHelper localDbHelper;
	// 查询本地数据库得到的cursor
	private Cursor curArtist;
	// 适配器adapter
	private SimpleAdapter adapter;
	// 该参数一是用于适配器的数据源，二是便于传参数给ArtistMusicActivity
	private ArrayList<Map<String, String>> data;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_music);
		setButtonClick();
		initListView();
		setListViewClick();
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
		title.setText(R.string.artist);
		rightDiviler.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 刚进入界面时，查询本地数据库得到数据给listview
	 */
	private void initListView(){
		nothingImg=(ImageView) findViewById(R.id.local_nothing_img);
		lvMusic=(ListView) findViewById(R.id.local_listview);
		localDbHelper=new MusicDBHelper(ArtistSelectActivity.this);
		
		new Thread() {
			public void run() {
				curArtist = localDbHelper.queryArtistByID();
				handler.sendMessage(handler.obtainMessage(0, curArtist));
			};
		}.start();		
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
			if (curArtist.getCount() != 0){
				curArtist.moveToFirst();
				data = getListFromArtist(curArtist);
				adapter = new SimpleAdapter(ArtistSelectActivity.this, data,
						R.layout.artist_list_item,
						new String[] { "artist" },
						new int[] { R.id.artist_select_tv });
				lvMusic.setAdapter(adapter);
				// 关闭cursor和数据库
				curArtist.close();
				localDbHelper.close();
			}
			else{
				nothingImg.setVisibility(View.VISIBLE);
				lvMusic.setVisibility(View.GONE);
				ToastHelper.show(ArtistSelectActivity.this, R.string.please_scan_local_music);
			}
		}		
	};
	
    /**
     * 将数据库中查询到的cursor转换为list
     * @param curLocal
     * @return ArrayList<Map<String, String>>
     */
	private ArrayList<Map<String, String>> getListFromArtist(Cursor curLocal){
		if (curLocal.getCount() != 0){
			curLocal.moveToFirst();
			data = new ArrayList<Map<String, String>>();
			do{
				
				Map<String, String> map = new HashMap<String, String>();
				map.put("artist", curLocal.getString(curLocal
						.getColumnIndex(DbConstant.ARTIST_LOCAL_ARTIST)));
				data.add(map);
				curLocal.moveToNext();
			}
			while (!curLocal.isAfterLast());
			return data;
		}		
		return null;
	}
	
	/**
	 * 设置listview的点击监听事件
	 */
	private void setListViewClick(){
		lvMusic.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ArtistSelectActivity.this,
						ArtistMusicActivity.class);
				intent.putExtra("artist",
						data.get(position).get("artist"));
				startActivity(intent);
			}			
		});
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// 关闭cursor和数据库
		curArtist.close();
		localDbHelper.close();
		super.onDestroy();
	}
}
