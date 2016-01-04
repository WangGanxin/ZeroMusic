package com.ganxin.zeromusic.module.timing;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ganxin.zeromusic.common.bean.TimingBean;
import com.ganxin.zeromusic.common.db.MusicDBHelper;
import com.ganxin.zeromusic.common.manager.TimingManager;
import com.ganxin.zeromusic.common.util.ToastHelper;
import com.ganxin.zeromusic.view.R;

/**
 * 
 * @Description 定时起床音乐的适配器
 * @author ganxin
 * @date Sep 20, 2015
 * @email ganxinvip@163.com
 */
public class TimingListviewAdapter extends BaseAdapter{

	private Context context;
	private List<TimingBean> list=new ArrayList<TimingBean>();
	private MusicDBHelper dbHelper;
	
	public TimingListviewAdapter(Context ctx){
		this.context=ctx;
		dbHelper=new MusicDBHelper(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list!=null?list.size():0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list!=null?list.get(position):null;
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
			convertView=LayoutInflater.from(context).inflate(R.layout.timing_listview_item, null,false);
			
			viewHolder.time=(TextView) convertView.findViewById(R.id.timing_list_item_time);
			viewHolder.week=(TextView) convertView.findViewById(R.id.timing_list_item_week);
			viewHolder.delBtn=(ImageView) convertView.findViewById(R.id.timing_list_item_del);
			viewHolder.switchBtn=(CheckBox) convertView.findViewById(R.id.timing_list_item_switch);
			
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		
		final TimingBean bean=list.get(position);
		
		if(bean!=null){
			viewHolder.time.setText(bean.getTime());
			viewHolder.week.setText(getWeekString(bean));
			
			if(bean.getStatus().equals("OPEN")){
				viewHolder.switchBtn.setChecked(true);
			}
			else{
				viewHolder.switchBtn.setChecked(false);
			}
									
			viewHolder.delBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(dbHelper!=null){
						int i=dbHelper.delTimingFromID(bean.getId());
						
						if(i>0){
							list.remove(position);
							TimingManager.getInstance().cancelTimingOpen(context, bean);
							notifyDataSetChanged();
						}
						else{
							ToastHelper.show(context,R.string.timing_open_del_error);
						}
					}
					else{
						ToastHelper.show(context,R.string.timing_open_del_error);
					}
					
				}
			});
			
			viewHolder.switchBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked){
						int i=dbHelper.updateTimingStaus(bean.getId(),"OPEN");
						
						if(i>0){
							list.get(position).setStatus("OPEN");
							TimingManager.getInstance().addTimingOpen(context, bean);
							notifyDataSetChanged();
						}
						else{
							ToastHelper.show(context,R.string.timing_open_open_error);
						}
					}
					else{
						int i=dbHelper.updateTimingStaus(bean.getId(),"CLOSE");
						
						if(i>0){
							list.get(position).setStatus("CLOSE");
							TimingManager.getInstance().cancelTimingOpen(context, bean);
							notifyDataSetChanged();
						}
						else{
							ToastHelper.show(context,R.string.timing_open_close_error);
						}
					}
				}
			});
		}
		
		return convertView;
	}
	
	public void setData(List<TimingBean> list){
		this.list=list;
		notifyDataSetChanged();
	}
	
	private String getWeekString(TimingBean bean){
		String week="";
		
		if(bean.getMonday()!=null){
			week+=bean.getMonday()+" ";
		}
		
		if(bean.getTuesday()!=null){
			week+=bean.getTuesday()+" ";
		}
		
		if(bean.getWednesday()!=null){
			week+=bean.getWednesday()+" ";
		}
		
		if(bean.getThursday()!=null){
			week+=bean.getThursday()+" ";
		}
		
		if(bean.getFriday()!=null){
			week+=bean.getFriday()+" ";
		}
		
		if(bean.getSaturday()!=null){
			week+=bean.getSaturday()+" ";
		}
		
		if(bean.getSunday()!=null){
			week+=bean.getSunday()+" ";
		}
		
		return week;
	}
	
	private class ViewHolder{
		TextView time,week;
		ImageView delBtn;
		CheckBox switchBtn;
	}
}
