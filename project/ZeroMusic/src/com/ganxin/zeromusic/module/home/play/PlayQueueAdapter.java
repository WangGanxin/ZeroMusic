package com.ganxin.zeromusic.module.home.play;

import java.util.ArrayList;

import com.ganxin.zeromusic.common.bean.MusicBean;
import com.ganxin.zeromusic.view.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * @Description 播放队列适配器
 * @author ganxin
 * @date Mar 27, 2015
 * @email ganxinvip@163.com
 */
public class PlayQueueAdapter extends BaseAdapter{
	//上下文
	private Context context;
	//数据源
	private ArrayList<MusicBean> musicList;
	// 正在播放的歌曲名称
	private String playMusicTitle="";
	// 正在播放的歌曲演唱者
	private String playMusicArtist="";
	
	public PlayQueueAdapter(Context context, ArrayList<MusicBean> musicList) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.musicList=musicList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return musicList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return musicList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.home_play_musiclist_item, null);
			viewHolder.number=(TextView) convertView.findViewById(R.id.player_listitem_number);
			viewHolder.title=(TextView) convertView.findViewById(R.id.player_listitem_song_text);
			viewHolder.artist=(TextView) convertView.findViewById(R.id.player_listitem_artist_text);
			
			convertView.setTag(viewHolder);
			
		}
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if((position+1)<10)
		    viewHolder.number.setText("0"+(position+1));
		else 			
			viewHolder.number.setText((position+1)+"");
		
		String musicTitle=musicList.get(position).getTitle();
		String musicArtist=musicList.get(position).getArtist();
		
		viewHolder.title.setText(musicTitle);
		viewHolder.artist.setText(musicArtist);
		
		if (musicTitle.equals(playMusicTitle) && musicArtist.equals(playMusicArtist)) {
			viewHolder.number.setTextColor(context.getResources().getColor(R.color.green));
			viewHolder.title.setTextColor(context.getResources().getColor(R.color.green));
			viewHolder.artist.setTextColor(context.getResources().getColor(R.color.green));
			
		} else {
			viewHolder.number.setTextColor(context.getResources().getColor(R.color.darkgrey));
			viewHolder.title.setTextColor(context.getResources().getColor(R.color.black));
			viewHolder.artist.setTextColor(context.getResources().getColor(R.color.darkgrey));
		}
		
		return convertView;
	}
		
	public void setPlayMusicTitle(String playMusicTitle) {
		this.playMusicTitle = playMusicTitle;
	}

	public void setPlayMusicArtist(String playMusicArtist) {
		this.playMusicArtist = playMusicArtist;
	}

	private class ViewHolder {
		TextView number;
		TextView title;
		TextView artist;
	}

}
