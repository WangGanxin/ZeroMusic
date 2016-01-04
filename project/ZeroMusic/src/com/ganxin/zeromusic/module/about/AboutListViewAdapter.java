package com.ganxin.zeromusic.module.about;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ganxin.zeromusic.view.R;

/**
 * 
 * @Description 关于适配器
 * @author ganxin
 * @date Nov 8, 2015
 * @email ganxinvip@163.com
 */
public class AboutListViewAdapter extends BaseAdapter{

	private Context context;
	private List<HashMap<String, Object>> item=new ArrayList<HashMap<String,Object>>();
	
	public AboutListViewAdapter(Context ctx,List<HashMap<String, Object>> list){
		this.context=ctx;
		this.item=list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return item!=null?item.size():0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return item!=null?item.get(position):null;
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
			convertView=LayoutInflater.from(context).inflate(R.layout.about_listview_item,parent,false);
			viewHolder=new ViewHolder();
			viewHolder.text=(TextView) convertView.findViewById(R.id.about_listview_item_text);
			viewHolder.icon=(ImageView) convertView.findViewById(R.id.about_listview_item_icon);
			
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		
		if(item.get(position)!=null){
			String text=(String) item.get(position).get("text");
			boolean iconFlag=(Boolean) item.get(position).get("icon");

			viewHolder.text.setText(text);
			
			if(iconFlag){
				viewHolder.icon.setVisibility(View.VISIBLE);
			}
			else{
				viewHolder.icon.setVisibility(View.GONE);
			}
		}
		
		return convertView;
	}

	public void setData(List<HashMap<String, Object>> list){
		this.item=list;
		notifyDataSetChanged();
	}
	
	private class ViewHolder{
		TextView text;
		ImageView icon;
	}
}
