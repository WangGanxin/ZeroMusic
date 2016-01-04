package com.ganxin.zeromusic.module.search;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ganxin.zeromusic.application.AppConstant;
import com.ganxin.zeromusic.view.R;

/**
 * 
 * @Description 歌曲搜索页面的排行榜的gridview的适配器
 * @author ganxin
 * @date Apr 9, 2015
 * @email ganxinvip@163.com
 */
public class SearchGridViewAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<HashMap<String,Object>> list;
	
	public SearchGridViewAdapter(Context context,ArrayList<HashMap<String,Object>> list) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.list=list;
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
		ViewHoder viewHoder;
		
		if(convertView==null){
			viewHoder=new ViewHoder();
			convertView=LayoutInflater.from(context).inflate(R.layout.search_gridview_item,null);
			
			viewHoder.textView=(TextView) convertView.findViewById(R.id.search_gridview_item_textview);
			viewHoder.imageView=(ImageView) convertView.findViewById(R.id.search_gridview_item_icon);
			
			convertView.setTag(viewHoder);
		}
		else{
			viewHoder=(ViewHoder) convertView.getTag();
		}
		
		int nameID=(Integer)list.get(position).get(AppConstant.RANKING_LIST_NAME);
		int resID=(Integer)list.get(position).get(AppConstant.RANKING_LIST_RESID);
		
		viewHoder.imageView.setBackgroundResource(resID);
		viewHoder.textView.setText(nameID);
		
		return convertView;
	}
	
	private class ViewHoder {
		TextView textView;
		ImageView imageView;
	}
}
