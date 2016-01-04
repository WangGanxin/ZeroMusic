package com.ganxin.zeromusic.module.timing;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.appyvet.rangebar.RangeBar;
import com.appyvet.rangebar.RangeBar.OnRangeBarChangeListener;
import com.ganxin.zeromusic.common.bean.TimingBean;
import com.ganxin.zeromusic.common.db.MusicDBHelper;
import com.ganxin.zeromusic.common.manager.TimingManager;
import com.ganxin.zeromusic.common.util.ToastHelper;
import com.ganxin.zeromusic.common.widget.residemenu.ResideMenu;
import com.ganxin.zeromusic.module.MenuActivity;
import com.ganxin.zeromusic.module.timing.open.TimingOpenActivity;
import com.ganxin.zeromusic.view.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @Description 定时音乐界面
 * @author ganxin
 * @date Mar 13, 2015
 * @email ganxinvip@163.com
 */
public class TimingFragment extends Fragment implements OnClickListener,OnRangeBarChangeListener{

	private View parentView;
	private ResideMenu resideMenu;
	private RangeBar rangeBar;
	private ImageButton addBtn;
	private ToggleButton toggleBtn;
	private ListView listview;
	private TimingListviewAdapter adapter;
	
	private MusicDBHelper dbHelper;
	private Handler mHandler;
	
	private boolean firstInitRangeBar=true; //用于判断是否是第一次
	private boolean firstMoveFlag=true; //用于判断是否是第一次
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		parentView = inflater.inflate(R.layout.fragment_timing, container, false);
		initUI();
		initEvent();
		initHandler();
		initData();
		return parentView;
	}

	private void initUI() {
		// TODO Auto-generated method stub
		rangeBar=(RangeBar) parentView.findViewById(R.id.timing_close_rangebar);
		addBtn=(ImageButton) parentView.findViewById(R.id.timing_add_btn);
		toggleBtn=(ToggleButton) parentView.findViewById(R.id.timing_close_btn);
		listview=(ListView) parentView.findViewById(R.id.timing_listview);
	}
	
	private void initEvent() {
		// TODO Auto-generated method stub
		addBtn.setOnClickListener(this);
		toggleBtn.setOnClickListener(this);
		rangeBar.setOnRangeBarChangeListener(this);
	}
	
	private void initHandler(){
		if(mHandler==null){
			mHandler=new Handler(){
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
					switch (msg.what) {
					case 0:
						queryTimingOpenData();
						break;

					default:
						break;
					}
				}				
			};
		}
	}

	private void initData() {
		// TODO Auto-generated method stub        
		initToggleBtn();
		//添加手势操作忽略的视图
        MenuActivity parentActivity = (MenuActivity) getActivity();
        resideMenu = parentActivity.getResideMenu();
        resideMenu.addIgnoredView(rangeBar);
        
        dbHelper=new MusicDBHelper(parentActivity);
        
        adapter=new TimingListviewAdapter(getActivity());
        listview.setAdapter(adapter);
        
        firstInitRangeBar=true;
        
        if(mHandler!= null){
        	mHandler.sendEmptyMessage(0);
        }
	}
		
	private void initToggleBtn(){
		boolean flag=TimingManager.getInstance().readTimingClose(getActivity());		
		if(flag){
		
			toggleBtn.setChecked(true);						
			final int sleepTime=TimingManager.getInstance().readTimingCloseTime(getActivity());
			new Handler().postDelayed(new Runnable() {				
				@Override
				public void run() {
					rangeBar.setRangePinsByValue(1, sleepTime);
				}
			},100);

			firstMoveFlag=false;
		}
		else{
			toggleBtn.setChecked(false);
			if(rangeBar.getRightIndex()==89){
				firstMoveFlag=true;
			}
		}
	}
	
	private void toggleBtnClick(){		
		if(toggleBtn.isChecked()){			
			long sleepTime=0;
			if(firstMoveFlag){
				sleepTime=rangeBar.getLeftIndex()+1;
			}
			else{
				sleepTime=rangeBar.getRightIndex()+1;
			}
			TimingManager.getInstance().setTimingClose(getActivity(), true);
			TimingManager.getInstance().setTimingCloseTime(getActivity(),(int)sleepTime);
			TimingManager.getInstance().setSleepTime(getActivity(), Long.valueOf(sleepTime)*1000*60);
			ToastHelper.makeText(getActivity(), "应用将在 "+sleepTime+" 分钟后关闭",Toast.LENGTH_SHORT).show();
		}
		else{			
			TimingManager.getInstance().resetTimingClose(getActivity());
		}
	}
	
	private void queryTimingOpenData(){
		if(dbHelper!=null){
			List<TimingBean> list=dbHelper.queryTimingByID();
			if(list!=null&&list.size()>0){
				adapter.setData(list);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.timing_add_btn:
			Intent intent=new Intent();
			intent.setClass(getActivity(), TimingOpenActivity.class);
			getActivity().startActivity(intent);
			break;
		case R.id.timing_close_btn:
			toggleBtnClick();
			break;
		default:
			break;
		}
	}

	@Override
	public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
			int rightPinIndex, String leftPinValue, String rightPinValue) {
		try {
			if(toggleBtn.isChecked()&&!firstInitRangeBar){
				TimingManager.getInstance().setTimingClose(getActivity(), true);
				TimingManager.getInstance().setTimingCloseTime(getActivity(),(int)rightPinIndex+1);
				TimingManager.getInstance().setSleepTime(getActivity(), Long.valueOf(rightPinIndex+1)*1000*60);
				ToastHelper.makeText(getActivity(), "应用将在 "+(rightPinIndex+1)+" 分钟后关闭",Toast.LENGTH_SHORT).show();
			}
			
			if(firstInitRangeBar){
				firstInitRangeBar=false;			
			}
			else{
				firstInitRangeBar=false;
				firstMoveFlag=false;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		if(mHandler!=null){
			mHandler.sendEmptyMessage(0);
		}
		super.onResume();
		MobclickAgent.onPageStart(getClass().getSimpleName());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getSimpleName());
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		if(dbHelper!=null){
			dbHelper=null;
		}
		
		if(mHandler!=null){
			mHandler=null;
		}
		super.onDestroyView();
	}
	
}
