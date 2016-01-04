package com.ganxin.zeromusic.module.timing.open;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ganxin.zeromusic.common.widget.checkbox.IconCheckBox;
import com.ganxin.zeromusic.common.widget.checkbox.IconCheckBox.CheckedChangedListener;
import com.ganxin.zeromusic.view.R;

/**
 * 
 * @Description 定时起床音乐的选项listview适配器
 * @author ganxin
 * @date Sep 15, 2015
 * @email ganxinvip@163.com
 */
public class TimingOpenListviewApapter extends BaseAdapter {
    private Context context;
    private String[] weeks;
    private List<Integer> checkList=new ArrayList<Integer>();
    private List<Boolean> flagList=new ArrayList<Boolean>();
    
	public TimingOpenListviewApapter(Context context) {
           this.context=context;
           
           String [] weeks={context.getResources().getString(R.string.timing_open_monday),
           		context.getResources().getString(R.string.timing_open_tuesday),
        		context.getResources().getString(R.string.timing_open_wednesday),
        		context.getResources().getString(R.string.timing_open_thursday),
        		context.getResources().getString(R.string.timing_open_friday),
        		context.getResources().getString(R.string.timing_open_saturday),
        		context.getResources().getString(R.string.timing_open_sunday)};
           
           this.weeks=weeks;
           
           for(int i=0;i<weeks.length;i++){
        	   flagList.add(false);
           }
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return weeks.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return weeks[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder=null;		
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.timing_open_listview_item,null, false);
			viewHolder.itemLayout=(RelativeLayout) convertView.findViewById(R.id.timing_open_list_item_layout);
			viewHolder.checkbox=(IconCheckBox) convertView.findViewById(R.id.timing_open_list_item_checkbox);
			viewHolder.title=(TextView) convertView.findViewById(R.id.timing_open_list_item_title);
			
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder=(ViewHolder) convertView.getTag();
		}

		viewHolder.title.setText(weeks[position]);
		
		viewHolder.checkbox.setChecking(flagList.get(position));
		
		viewHolder.itemLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					if(flagList.get(position)){
						flagList.set(position, false);
					}
					else{						
						flagList.set(position,true);
					}
					
					notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		viewHolder.checkbox.setOnCheckedChangedListener(new CheckedChangedListener() {
			
			@Override
			public void checkedChanged(IconCheckBox checkBox, boolean isChecked) {
				// TODO Auto-generated method stub
				try {
					if(isChecked){
						flagList.set(position,true);
					}
					else{
						flagList.set(position,false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});	
		return convertView;
	}
	
	public List<Integer> getCheckList() {
		checkList.clear();
		for(int i=0;i<flagList.size();i++){
			if(flagList.get(i)){
				checkList.add(i);
			}			
		}
		return checkList;
	}

	private class ViewHolder{
		RelativeLayout itemLayout;
		TextView title;
		IconCheckBox checkbox;
	}	
}
