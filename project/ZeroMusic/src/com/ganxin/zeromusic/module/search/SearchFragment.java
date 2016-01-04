package com.ganxin.zeromusic.module.search;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.ganxin.zeromusic.application.AppConstant;
import com.ganxin.zeromusic.common.bean.GetSongListBean;
import com.ganxin.zeromusic.common.bean.GetSongListBean.songlist;
import com.ganxin.zeromusic.common.bean.QueryMusicBean;
import com.ganxin.zeromusic.common.bean.QueryMusicBean.Artist;
import com.ganxin.zeromusic.common.bean.QueryMusicBean.Song;
import com.ganxin.zeromusic.common.http.volleyHelper.HttpAPI;
import com.ganxin.zeromusic.common.http.volleyHelper.RequestParams;
import com.ganxin.zeromusic.common.util.NetWorkHelper;
import com.ganxin.zeromusic.common.util.ToastHelper;
import com.ganxin.zeromusic.common.widget.gridview.DisabledScrollGridView;
import com.ganxin.zeromusic.common.widget.listview.DisabledScrollListView;
import com.ganxin.zeromusic.module.search.rankinglist.RankingListActivity;
import com.ganxin.zeromusic.view.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @Description 歌曲搜索界面
 * @author ganxin
 * @date Mar 13, 2015
 * @email ganxinvip@163.com
 */
public class SearchFragment extends Fragment implements OnClickListener {

	private View parentView; // 当前view
	private EditText searchEditText; // 搜索框
	private ImageButton searchImgBtn; // 搜索按钮
	private LinearLayout loadingLayout; // 加载时显示的布局
	private ImageView loadingImgView; // 加载时显示的选择动画
	private ImageView noResultImg; //没有结果显示的图片
	
	private DisabledScrollGridView gridview; // 排行榜的表格展示
	private SearchGridViewAdapter gridViewAdapter; //排行榜的适配器
	
	private DisabledScrollListView listviewSong; // 搜索结果的歌曲展示
	private SearchResultMusicAdapter musicAdapter; //搜索结果的歌曲适配器
	
	private DisabledScrollListView listviewArtist; // 搜索结果的歌手展示
	private SearchResultArtistAdapter artistAdapter; //搜索结果的歌手适配器
	
	private TextView resultLine; //返回结果用作歌手和歌曲的分割线
	
	private ArrayList<Song> musicList; //搜索结果返回的歌曲信息
	private ArrayList<Artist> artistList; //搜索结果返回的歌手信息
	
	private ArrayList<songlist> songtList; //根据歌手名称搜索 返回的歌曲信息
	private SearchResultSongListAdapter songListAdapter; //根据歌手名称搜索 返回的适配器

	// 榜单名称对应资源ID
	private int[] rankingListName = { R.string.rankinglist_newsong,
			R.string.rankinglist_hotsong, R.string.rankinglist_foreignsong,
			R.string.rankinglist_oldsong, R.string.rankinglist_lovesong,
			R.string.rankinglist_filmsong };

	// 榜单对应的图标对应资源ID
	private int[] rankingListIcon = { R.drawable.icon_newsong,
			R.drawable.icon_hotsong, R.drawable.icon_foreignsong,
			R.drawable.icon_oldsong, R.drawable.icon_lovesong,
			R.drawable.icon_filmsong };
	
	// 榜单对应的请求参数
	private int [] rankingListType={1,2,21,22,23,24};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		parentView = inflater.inflate(R.layout.fragment_search, container,
				false);
		initUI();
		initData();
		return parentView;
	}

	private void initUI() {
		// TODO Auto-generated method stub
		searchEditText = (EditText) parentView
				.findViewById(R.id.search_content_edittext);
		searchImgBtn = (ImageButton) parentView
				.findViewById(R.id.search_imgbtn);

		loadingLayout = (LinearLayout) parentView
				.findViewById(R.id.search_loading_layout);
		loadingImgView = (ImageView) parentView
				.findViewById(R.id.search_loading_img);
		
		noResultImg=(ImageView) parentView.findViewById(R.id.search_no_result_img);

		gridview = (DisabledScrollGridView) parentView
				.findViewById(R.id.search_rankinglist_gridview);
		listviewSong=(DisabledScrollListView) parentView.findViewById(R.id.search_result_song_listview);
		listviewArtist=(DisabledScrollListView) parentView.findViewById(R.id.search_result_artist_listview);
		resultLine=(TextView) parentView.findViewById(R.id.search_result_line);
		
		searchImgBtn.setOnClickListener(this);

	}

	private void initData() {
		// TODO Auto-generated method stub
		gridViewAdapter = new SearchGridViewAdapter(getActivity(),
				getDataResource());
		gridview.setAdapter(gridViewAdapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(getActivity(),RankingListActivity.class);
				intent.putExtra(AppConstant.RANKING_LIST_TYPE,rankingListType[position]);
				startActivity(intent);
			}
		});

		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (s.length() > 0)
					searchImgBtn.setBackgroundResource(R.drawable.icon_music_search_selected);
				else
					searchImgBtn.setBackgroundResource(R.drawable.icon_music_search_normal);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.search_imgbtn:
			searchMusic();
			break;
		default:
			break;
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			// 没有网络
			case 0:
				closeLoadingAnimation();
				noResultImg.setVisibility(View.VISIBLE);
				ToastHelper.show(getActivity(),R.string.network_is_not_connected);
				break;
			// 发生错误响应或无结果
			case 1:
				closeLoadingAnimation();
				noResultImg.setBackgroundResource(R.drawable.error_search_noresult);
				noResultImg.setVisibility(View.VISIBLE);
				ToastHelper.show(getActivity(),R.string.search_no_result);
				break;
			// 关键字查询返回的结果
			case 2:
				closeLoadingAnimation();
				setSearchResultAdapter();
				break;
			// 点击歌手查询返回的结果
			case 3:
				closeLoadingAnimation();
				setSongListAdapter();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 搜索歌曲
	 */
	private void searchMusic() {
		// TODO Auto-generated method stub
		String keywords = searchEditText.getText().toString();
		if (keywords.length() > 0) {
			showLoadingAnimation();
			gridview.setVisibility(View.GONE);
			listviewSong.setVisibility(View.GONE);
			listviewArtist.setVisibility(View.GONE);
			resultLine.setVisibility(View.GONE);
			
			HashMap<String, String> params = RequestParams
					.queryMusicParams(keywords);

			if (NetWorkHelper.isConnected(getActivity())) {
				HttpAPI.createAndStartGetMusicRequest(params,
						QueryMusicBean.class, new Listener<QueryMusicBean>() {
							@Override
							public void onResponse(QueryMusicBean arg0) {
								// TODO Auto-generated method stub
								if((arg0.getArtist()!=null&&arg0.getArtist().size()>0)||
										(arg0.getSong()!=null&&arg0.getSong().size()>0)){
									musicList=arg0.getSong();
									artistList=arg0.getArtist();
									handler.sendMessage(handler.obtainMessage(2));
								}
								else {
									handler.sendMessage(handler.obtainMessage(1));
								}								   
							}
						}, new ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError arg0) {
								// TODO Auto-generated method stub
								handler.sendMessage(handler.obtainMessage(1));
							}
						});
			} else
				handler.sendMessage(handler.obtainMessage(0));
		}

	}
	
    /**
     * 根据歌手ID，搜索歌曲
     * @param id
     */
	private void getMusicFromArtistID(int id){
		String keywords = searchEditText.getText().toString();
		if (keywords.length() > 0) {
			showLoadingAnimation();
			gridview.setVisibility(View.GONE);
			
			HashMap<String, String> params =RequestParams.queryMusicFromArtistIdParams(id);

			if (NetWorkHelper.isConnected(getActivity())) {
				HttpAPI.createAndStartGetMusicRequest(params,
						GetSongListBean.class, new Listener<GetSongListBean>() {
							@Override
							public void onResponse(GetSongListBean arg0) {
								// TODO Auto-generated method stub
								if(arg0.getSonglist()!=null&&arg0.getSonglist().size()>0){
									songtList=arg0.getSonglist();
									handler.sendMessage(handler.obtainMessage(3));
								}
								else {
									handler.sendMessage(handler.obtainMessage(1));
								}								   
							}
						}, new ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError arg0) {
								// TODO Auto-generated method stub
								handler.sendMessage(handler.obtainMessage(1));
							}
						});
			} else
				handler.sendMessage(handler.obtainMessage(0));
		}
	}
	
	/**
	 * 设置返回结果要显示的适配器
	 */
	private void setSearchResultAdapter(){
		String keywords = searchEditText.getText().toString();
        if(musicList!=null){
        	musicAdapter=new SearchResultMusicAdapter(getActivity(), musicList, keywords);
        	listviewSong.setAdapter(musicAdapter);
        	listviewSong.setVisibility(View.VISIBLE);
        }
        
		if(artistList!=null){
			artistAdapter=new SearchResultArtistAdapter(getActivity(), artistList, keywords);
			listviewArtist.setAdapter(artistAdapter);
			listviewArtist.setVisibility(View.VISIBLE);
			resultLine.setVisibility(View.VISIBLE);
			
			listviewArtist.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					String name=artistList.get(position).getArtistname();
					int artistID=artistList.get(position).getArtistid();					
					searchEditText.setText(name);
					
					listviewArtist.setVisibility(View.GONE);
					listviewSong.setVisibility(View.GONE);
					resultLine.setVisibility(View.GONE);
					noResultImg.setVisibility(View.GONE);
					
					getMusicFromArtistID(artistID);
				}
			});
		}
	}
	
	private void setSongListAdapter(){
		String keywords = searchEditText.getText().toString();
	    if(songtList!=null){
	    	songListAdapter=new SearchResultSongListAdapter(getActivity(), songtList, keywords);
	        listviewSong.setAdapter(songListAdapter);
	        listviewSong.setVisibility(View.VISIBLE);
	    }
	}

	// 显示loading的动画
	private void showLoadingAnimation() {
		// 显示布局
		loadingLayout.setVisibility(View.VISIBLE);
		// 开始旋转动画
		RotateAnimation anim = new RotateAnimation(0, 360,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		anim.setDuration(1000);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(-1);
		loadingImgView.startAnimation(anim);
	}

	// 关闭loading的动画
	private void closeLoadingAnimation() {
		loadingLayout.setVisibility(View.GONE);
	}

	/**
	 * 获取排行榜类别的数据源
	 * 
	 * @return
	 */
	private ArrayList<HashMap<String, Object>> getDataResource() {
		ArrayList<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 6; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(AppConstant.RANKING_LIST_NAME, rankingListName[i]);
			map.put(AppConstant.RANKING_LIST_RESID, rankingListIcon[i]);
			dataSource.add(map);
		}

		return dataSource;
	}

	@Override
	public void onResume() {
		super.onResume();		
		MobclickAgent.onPageStart(getClass().getSimpleName());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getSimpleName());
	}
		
}
