package com.ganxin.zeromusic.module.timing.open;

import java.util.List;

import net.simonvt.numberpicker.NumberPicker;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.ganxin.zeromusic.common.bean.TimingBean;
import com.ganxin.zeromusic.common.db.MusicDBHelper;
import com.ganxin.zeromusic.common.manager.TimingManager;
import com.ganxin.zeromusic.common.util.ToastHelper;
import com.ganxin.zeromusic.framework.BaseActivity;
import com.ganxin.zeromusic.view.R;

/**
 * 
 * @Description 定时起床音乐的设置界面
 * @author ganxin
 * @date Apr 20, 2015
 * @email ganxinvip@163.com
 */
public class TimingOpenActivity extends BaseActivity{

	private Context mContext;
	private ImageButton backBtn,saveBtn;
	private NumberPicker hoursPicker,minutesPicker;
	private ListView weekListView;
	private TimingOpenListviewApapter mAdapter;
	
	private MusicDBHelper dbHelper;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timing_open);
		mContext=this;
		initUI();
		initEvent();
		initData();
	}
	
	private void initUI() {
		// TODO Auto-generated method stub
		backBtn=(ImageButton) findViewById(R.id.timing_open_actionbar_back);
		saveBtn=(ImageButton) findViewById(R.id.timing_open_actionbar_scan);
		hoursPicker=(NumberPicker) findViewById(R.id.timing_open_hours);
		minutesPicker=(NumberPicker) findViewById(R.id.timing_open_minutes);
		weekListView=(ListView) findViewById(R.id.timing_open_week_listview);		
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		backBtn.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
	}

	private void initData() {
		// TODO Auto-generated method stub
		hoursPicker.setMaxValue(23);
		hoursPicker.setMinValue(00);
		hoursPicker.getChildAt(0).setFocusable(false);
		hoursPicker.setFocusable(false);
		hoursPicker.setFocusableInTouchMode(true);
		
		minutesPicker.setMaxValue(59);
		minutesPicker.setMinValue(00);
		minutesPicker.getChildAt(0).setFocusable(false);
		minutesPicker.setFocusable(false);
		minutesPicker.setFocusableInTouchMode(true);
		
		dbHelper=new MusicDBHelper(mContext);
		
		mAdapter=new TimingOpenListviewApapter(mContext);
		weekListView.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.timing_open_actionbar_back:
			finish();
			break;
		case R.id.timing_open_actionbar_scan:
			if(mAdapter!=null){
				List<Integer> list=mAdapter.getCheckList();
				if(list.size()>0){
					inserToTable(list);
				}
				else{
					ToastHelper.makeText(this,R.string.timing_open_please_select_day,Toast.LENGTH_SHORT).show();
				}
			}
			break;
		default:
			break;
		}
	}
	
	private void inserToTable(List<Integer> list){
		if(dbHelper!=null){
			TimingBean timingBean=new TimingBean();

			String hours="";
			String minutes="";
			if(hoursPicker.getValue()<10){
				hours="0"+hoursPicker.getValue();
			}
			else{
				hours=""+hoursPicker.getValue();
			}
			
			if(minutesPicker.getValue()<10){
				minutes="0"+minutesPicker.getValue();
			}
			else{
				minutes=""+minutesPicker.getValue();
			}
			
			timingBean.setTime(hours+":"+minutes);			
			timingBean.setStatus("OPEN");
			for(int i=0;i<list.size();i++){
				switch (list.get(i)) {
				case 0:
					timingBean.setMonday("一");
					break;
				case 1:
					timingBean.setTuesday("二");
					break;
				case 2:
					timingBean.setWednesday("三");
					break;
				case 3:
					timingBean.setThursday("四");
					break;
				case 4:
					timingBean.setFriday("五");
					break;
				case 5:
					timingBean.setSaturday("六");
					break;
				case 6:
					timingBean.setSunday("日");
					break;					
				default:
					break;
				}
			}
			
			if(compareTimingTable(timingBean)){
				ToastHelper.makeText(this,R.string.timing_open_has_exist_item,Toast.LENGTH_SHORT).show();
			}
			else{
				Long i=dbHelper.inserTiming(timingBean);
				
				if(i>0){
					TimingManager.getInstance().addTimingOpen(mContext,timingBean);
					finish();
				}
				else{
					ToastHelper.makeText(this,R.string.timing_open_unknow_error,Toast.LENGTH_SHORT).show();
					finish();
				}	
			}		
		}
		else{
			ToastHelper.makeText(this,R.string.timing_open_set_date_error,Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 比较定时开启的表中，是否已经包含该选项
	 * @param bean
	 * @return
	 */
	private boolean compareTimingTable(TimingBean bean){
		if(dbHelper!=null){
			List<TimingBean> list=dbHelper.queryTimingByID();
			
			if(list!=null&&list.size()>0){
				boolean flag=false;
				for(TimingBean tb: list){					
					if(tb.getTime().equals(bean.getTime())){
						boolean flag1=false;
						boolean flag2=false;
						boolean flag3=false;
						boolean flag4=false;
						boolean flag5=false;
						boolean flag6=false;
						boolean flag7=false;
						
						if((tb.getMonday()!=null&&bean.getMonday()!=null)||
								(tb.getMonday()==null&&bean.getMonday()==null)){
							flag1=true;
						}
						if((tb.getTuesday()!=null&&bean.getTuesday()!=null)||
								(tb.getTuesday()==null&&bean.getTuesday()==null)){
							flag2=true;
						}
						if((tb.getWednesday()!=null&&bean.getWednesday()!=null)||
								(tb.getWednesday()==null&&bean.getWednesday()==null)){
							flag3=true;
						}
						if((tb.getThursday()!=null&&bean.getThursday()!=null)||
								(tb.getThursday()==null&&bean.getThursday()==null)){
							flag4=true;
						}
						if((tb.getFriday()!=null&&bean.getFriday()!=null)||
								(tb.getFriday()==null&&bean.getFriday()==null)){
							flag5=true;
						}
						if((tb.getSaturday()!=null&&bean.getSaturday()!=null)||
								(tb.getSaturday()==null&&bean.getSaturday()==null)){
							flag6=true;
						}
						if((tb.getSunday()!=null&&bean.getSunday()!=null)||
								(tb.getSunday()==null&&bean.getSunday()==null)){
							flag7=true;
						}

						if(flag1||flag2||flag3||flag4||flag5||flag6||flag7){
							flag=true;
							break;	
						}				   
					}					
				}
				return flag;
			}
			else {		
				return false;				
			}
		}
		else{
			   return true;
		}		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(dbHelper!=null){
			dbHelper=null;
		}
		super.onDestroy();
	}

}
