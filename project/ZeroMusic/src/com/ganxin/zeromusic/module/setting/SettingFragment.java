package com.ganxin.zeromusic.module.setting;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ganxin.zeromusic.application.AppConstant;
import com.ganxin.zeromusic.common.manager.SettingManager;
import com.ganxin.zeromusic.common.util.SharPreferHelper;
import com.ganxin.zeromusic.common.widget.checkbox.IconCheckBox;
import com.ganxin.zeromusic.view.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @Description 设置页面
 * @author ganxin
 * @date Mar 13, 2015
 * @email ganxinvip@163.com
 */
public class SettingFragment extends Fragment implements OnClickListener,OnCheckedChangeListener{

	private View rootView;
	private Context context;
	
	private CheckBox headSetBtn,adertisementBtn;
	private RelativeLayout musicAlbumLayout,cleanCacheLayout;
	private TextView sensorTypeTv,cacheSizeTv;
	
	private RelativeLayout advertisementLayout;
	private View advertisementLine;
	
	private SettingManager settingManager;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_setting, container, false);
		context=getActivity();
		initView();
		initData();
		return rootView;
	}

	private void initView() {
		// TODO Auto-generated method stub
		headSetBtn=(CheckBox) rootView.findViewById(R.id.setting_headset_btn);
		adertisementBtn=(CheckBox) rootView.findViewById(R.id.setting_advertisement_btn);
		
		musicAlbumLayout=(RelativeLayout) rootView.findViewById(R.id.setting_music_album_layout);
		cleanCacheLayout=(RelativeLayout) rootView.findViewById(R.id.setting_clean_cache_layout);
		
		sensorTypeTv=(TextView) rootView.findViewById(R.id.setting_music_album_strength);
		cacheSizeTv=(TextView) rootView.findViewById(R.id.setting_clean_cache_size_textview);
		
		advertisementLayout=(RelativeLayout) rootView.findViewById(R.id.setting_advertisement_layout);
		advertisementLine=rootView.findViewById(R.id.setting_advertisement_split_line);
		
		headSetBtn.setOnCheckedChangeListener(this);
		adertisementBtn.setOnCheckedChangeListener(this);
		
		musicAlbumLayout.setOnClickListener(this);
		cleanCacheLayout.setOnClickListener(this);
	}
	
	private void initData() {
		// TODO Auto-generated method stub
		settingManager=SettingManager.getInstance();

		boolean openFlag=SharPreferHelper.getBooleanConfig(context, AppConstant.SHARPREFER_FILENAME,
				AppConstant.LOCAL_SHOW_ADVIEW,false);
		
		if(!openFlag){
			advertisementLayout.setVisibility(View.GONE);			
			advertisementLine.setVisibility(View.GONE);
		}
				
		headSetBtn.setChecked(settingManager.getLineOutPauseCheck(context));
		adertisementBtn.setChecked(settingManager.getBannerAdvertisermentCheck(context));
		
		int type=settingManager.getSensorType(context);
		switch (type) {
		case AppConstant.SENSOR_TYPE_HIGH:
			sensorTypeTv.setText(R.string.setting_change_pic_strength1);
			break;
		case AppConstant.SENSOR_TYPE_MIDDLE:
			sensorTypeTv.setText(R.string.setting_change_pic_strength2);
			break;
		case AppConstant.SENSOR_TYPE_LOWER:
			sensorTypeTv.setText(R.string.setting_change_pic_strength3);
			break;
		default:
			break;
		}

		String size=settingManager.getCacheSize();
		if(size!=null&&size.length()>0){
			cacheSizeTv.setText(size);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.setting_music_album_layout:
			showSensorDiaolg();
			break;
		case R.id.setting_clean_cache_layout:
			showCleanCacheDialog();
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.setting_headset_btn:
			if(isChecked){
				headSetBtn.setChecked(true);
				settingManager.setLineOutPauseCheck(context,true);
			}else{
				headSetBtn.setChecked(false);
				settingManager.setLineOutPauseCheck(context,false);
			}
			break;
		case R.id.setting_advertisement_btn:
			if(isChecked){
				adertisementBtn.setChecked(true);
				settingManager.setBannerAdvertisermentCheck(context, true);
			}
			else{
				adertisementBtn.setChecked(false);
				settingManager.setBannerAdvertisermentCheck(context, false);
			}
			break;
		default:
			break;
		}
	}

	private void showSensorDiaolg(){
		final Dialog sensorDialog = new Dialog(context,
				R.style.loadingDialog);
		sensorDialog.setContentView(R.layout.dialog_select_sensor_type);
		sensorDialog.setCanceledOnTouchOutside(false);

		// 以下更改对话框的大小
		Window dialogWindow = sensorDialog.getWindow();
		WindowManager winManager = (WindowManager) context
				.getSystemService("window");
		Display display = winManager.getDefaultDisplay(); // 获取屏幕的宽、高
		Point size = new Point();
		display.getSize(size);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		lp.width = (int) (size.x * 0.9); // 宽度设置为屏幕的0.9
		dialogWindow.setAttributes(lp);
		
		LinearLayout layout1=(LinearLayout) sensorDialog.findViewById(R.id.setting_sensor_layout1);
		LinearLayout layout2=(LinearLayout) sensorDialog.findViewById(R.id.setting_sensor_layout2);
		LinearLayout layout3=(LinearLayout) sensorDialog.findViewById(R.id.setting_sensor_layout3);
		
		final IconCheckBox checkBox1=(IconCheckBox) sensorDialog.findViewById(R.id.setting_sensor_checkbox1);
		final IconCheckBox checkBox2=(IconCheckBox) sensorDialog.findViewById(R.id.setting_sensor_checkbox2);
		final IconCheckBox checkBox3=(IconCheckBox) sensorDialog.findViewById(R.id.setting_sensor_checkbox3);
		
		LinearLayout leftLayout=(LinearLayout) sensorDialog.findViewById(R.id.dialog_select_leftLayout);
		LinearLayout rightLayout=(LinearLayout) sensorDialog.findViewById(R.id.dialog_select_rightLayout);
		
		int type=settingManager.getSensorType(context);
		switch (type) {
		case AppConstant.SENSOR_TYPE_HIGH:
			checkBox1.setVisibility(View.VISIBLE);
			checkBox1.setChecking(true);
			break;
		case AppConstant.SENSOR_TYPE_MIDDLE:
			checkBox2.setVisibility(View.VISIBLE);
			checkBox2.setChecking(true);
			break;
		case AppConstant.SENSOR_TYPE_LOWER:
			checkBox3.setVisibility(View.VISIBLE);
			checkBox3.setChecking(true);
			break;
		default:
			break;
		}
		
		layout1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkBox1.setVisibility(View.VISIBLE);
				checkBox2.setVisibility(View.GONE);
				checkBox3.setVisibility(View.GONE);
				
				checkBox1.setChecking(true);
				checkBox2.setChecking(false);
				checkBox3.setChecking(false);
			}
		});
		
		layout2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkBox1.setVisibility(View.GONE);
				checkBox2.setVisibility(View.VISIBLE);
				checkBox3.setVisibility(View.GONE);
				
				checkBox1.setChecking(false);
				checkBox2.setChecking(true);
				checkBox3.setChecking(false);		
			}
		});
		
		layout3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkBox1.setVisibility(View.GONE);
				checkBox2.setVisibility(View.GONE);
				checkBox3.setVisibility(View.VISIBLE);
				
				checkBox1.setChecking(false);
				checkBox2.setChecking(false);
				checkBox3.setChecking(true);		
			}
		});
		
		leftLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				if(checkBox1.isChecked()){
					settingManager.setSensorType(context, AppConstant.SENSOR_TYPE_HIGH);
					sensorTypeTv.setText(R.string.setting_change_pic_strength1);
				}
				
				if(checkBox2.isChecked()){
					settingManager.setSensorType(context, AppConstant.SENSOR_TYPE_MIDDLE);
					sensorTypeTv.setText(R.string.setting_change_pic_strength2);
				}
				
				if(checkBox3.isChecked()){
					settingManager.setSensorType(context, AppConstant.SENSOR_TYPE_LOWER);
					sensorTypeTv.setText(R.string.setting_change_pic_strength3);
				}
				
				sensorDialog.dismiss();
			}
		});
		
		rightLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sensorDialog.dismiss();
			}
		});
		
		sensorDialog.show();
	}
	
	private void showCleanCacheDialog(){
		final Dialog cleanDialog = new Dialog(context,
				R.style.loadingDialog);
		cleanDialog.setContentView(R.layout.dialog_select_music);
		cleanDialog.setCanceledOnTouchOutside(false);

		// 以下更改对话框的大小
		Window dialogWindow = cleanDialog.getWindow();
		WindowManager winManager = (WindowManager) context
				.getSystemService("window");
		Display display = winManager.getDefaultDisplay(); // 获取屏幕的宽、高
		Point size = new Point();
		display.getSize(size);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		lp.width = (int) (size.x * 0.9); // 宽度设置为屏幕的0.9
		dialogWindow.setAttributes(lp);

		// 获取控件
		TextView title = (TextView) cleanDialog
				.findViewById(R.id.dialog_select_music_title);
		TextView content = (TextView) cleanDialog
				.findViewById(R.id.dialog_select_music_content);
		LinearLayout leftLayout = (LinearLayout) cleanDialog
				.findViewById(R.id.dialog_select_music_leftLayout);
		LinearLayout rightLayout = (LinearLayout) cleanDialog
				.findViewById(R.id.dialog_select_music_rightLayout);

		title.setText(R.string.del_music_title_tip);
		
		content.setText(R.string.setting_clear_cache_msg);
		
		leftLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				settingManager.cleanImageCache();;
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String size=settingManager.getCacheSize();
						if(size!=null&&size.length()>0){
							cacheSizeTv.setText(size);
						}
					}
				},800);
				
				cleanDialog.dismiss();
			}
		});
		
		rightLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cleanDialog.dismiss();
			}
		});
		
		cleanDialog.show();
	}
		
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getClass().getSimpleName());
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getSimpleName()); 
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}
}
