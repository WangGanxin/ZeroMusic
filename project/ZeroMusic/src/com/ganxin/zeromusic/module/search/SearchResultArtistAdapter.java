package com.ganxin.zeromusic.module.search;

import java.util.ArrayList;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ganxin.zeromusic.common.bean.QueryMusicBean.Artist;
import com.ganxin.zeromusic.view.R;

/**
 * 
 * @Description 搜索结果中歌手的适配器
 * @author ganxin
 * @date Apr 10, 2015
 * @email ganxinvip@163.com
 */
public class SearchResultArtistAdapter extends BaseAdapter{
	
	private Context context; //上下文
	private ArrayList<Artist> list; //数据源
	private String keyword;  //关键字
	
	public SearchResultArtistAdapter(Context context,ArrayList<Artist> list,String keyword){
		this.context=context;
		this.list=list;
		this.keyword=keyword;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.search_artist_listview_item,null);			
			viewHolder.artistName=(TextView) convertView.findViewById(R.id.search_artist_listview_item_tv);

			convertView.setTag(viewHolder);
			
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		
		//设置数据
		String artist=list.get(position).getArtistname();
		
		if(artist.contains(keyword)){
			SpannableString styledText = new SpannableString(artist); 
			
			styledText.setSpan(new TextAppearanceSpan(context, R.style.keywordStyle),
					artist.indexOf(keyword),artist.indexOf(keyword)+keyword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
			
			viewHolder.artistName.setText(styledText,TextView.BufferType.SPANNABLE);
		}
		else {
			viewHolder.artistName.setText(artist);
		}
		
		return convertView;
	}
	
	private class ViewHolder{
		TextView artistName; 
	}
}
