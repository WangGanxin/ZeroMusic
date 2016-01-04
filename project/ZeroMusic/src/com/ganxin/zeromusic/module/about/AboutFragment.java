package com.ganxin.zeromusic.module.about;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ganxin.zeromusic.common.manager.ShareManager;
import com.ganxin.zeromusic.common.util.NetWorkHelper;
import com.ganxin.zeromusic.common.util.ToastHelper;
import com.ganxin.zeromusic.module.about.detail.AboutDetailActivity;
import com.ganxin.zeromusic.view.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * 
 * @Description 关于页面
 * @author ganxin
 * @date Mar 13, 2015
 * @email ganxinvip@163.com
 */
public class AboutFragment extends Fragment {

	private View parentView;
	private Dialog dialog;
	private TextView versionNameTv;
	private ListView listView;
	private AboutListViewAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> arrayList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		parentView = inflater
				.inflate(R.layout.fragment_about, container, false);
		initUI();
		initData();
		return parentView;
	}

	private void initUI() {
		// TODO Auto-generated method stub
		versionNameTv = (TextView) parentView
				.findViewById(R.id.about_app_version);
		listView = (ListView) parentView.findViewById(R.id.about_listview);
		
		listView.setOnItemClickListener(new AboutItemClickListener());
	}

	private void initData() {
		// TODO Auto-generated method stub
		mAdapter = new AboutListViewAdapter(getActivity(),getInitListData());
		listView.setAdapter(mAdapter);
		
		setVersionName();		
		setVersionTip();

		ShareManager.getInstance().init(getActivity());
		ShareManager.getInstance().addWechat(getActivity());
		ShareManager.getInstance().addQQfriend(getActivity());
		ShareManager.getInstance().addQQZone(getActivity());
		ShareManager.getInstance().addSinaWeibo(getActivity());
	}

	/**
	 * 设置版本名称
	 */
	private void setVersionName() {
		try {
			PackageManager manager = getActivity().getPackageManager();
			PackageInfo info = manager.getPackageInfo(getActivity()
					.getPackageName(), 0);
			String version = info.versionName;
			versionNameTv.setText("Version : "+version);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建数据源
	 * 
	 * @return
	 */
	private ArrayList<HashMap<String, Object>> getInitListData() {
		arrayList = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> tempHashMap1 = new HashMap<String, Object>();
		HashMap<String, Object> tempHashMap2 = new HashMap<String, Object>();
		HashMap<String, Object> tempHashMap3 = new HashMap<String, Object>();

		tempHashMap1.put("text",getResources().getString(R.string.about_update_check));
		tempHashMap1.put("icon",false);
		
		tempHashMap2.put("text",getResources().getString(R.string.about_recommend_friend));
		tempHashMap2.put("icon",false);
		
		tempHashMap3.put("text",getResources().getString(R.string.about_zero_music));
		tempHashMap3.put("icon",false);

		arrayList.add(tempHashMap1);
		arrayList.add(tempHashMap2);
		arrayList.add(tempHashMap3);

		return arrayList;
	}
	
	/**
	 * 是否提示版本更新的小红点
	 */
	private void setVersionTip(){
		UmengUpdateAgent.setUpdateAutoPopup(false);		
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
		    @Override
		    public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
		        switch (updateStatus) {
		        case UpdateStatus.Yes: // has update
					if(arrayList!=null&&arrayList.size()>0){
				        arrayList.get(0).put("icon",true);
				        mAdapter.setData(arrayList);
				    }
		            break;
		        case UpdateStatus.No: // has no update
		            break;
		        case UpdateStatus.NoneWifi: // none wifi
		            break;
		        case UpdateStatus.Timeout: // time out
		            break;
		        }
		    }
		});		
		UmengUpdateAgent.forceUpdate(getActivity());
	}
	
	/**
	 * 更新检测处理
	 */
	private void processChcekVersion(){
		if(NetWorkHelper.isConnected(getActivity())){
			showLoadingDialog();
			UmengUpdateAgent.setUpdateAutoPopup(false);
			
			UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			    @Override
			    public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
			    	closeLoadingDialog();
			        switch (updateStatus) {
			        case UpdateStatus.Yes: // has update
			            UmengUpdateAgent.showUpdateDialog(getActivity(), updateInfo);
			            break;
			        case UpdateStatus.No: // has no update
			            ToastHelper.show(getActivity(), R.string.about_current_was_last_version);
			            break;
			        case UpdateStatus.NoneWifi: // none wifi
			            break;
			        case UpdateStatus.Timeout: // time out
			            break;
			        default:
			        	ToastHelper.show(getActivity(), R.string.about_current_was_last_version);
			            break;
			        }
			    }
			});
			
			UmengUpdateAgent.forceUpdate(getActivity());
		}
		else{
			 ToastHelper.show(getActivity(), R.string.network_is_not_connected);
		}
	}
	
	/**
	 * 显示检测loading
	 */
	private void showLoadingDialog() {
		dialog = new Dialog(getActivity(), R.style.loadingDialog);
		dialog.setContentView(R.layout.dialog_loading_music);
		dialog.setCanceledOnTouchOutside(true);

		// 以下更改对话框的大小
		Window dialogWindow = dialog.getWindow();
		WindowManager winManager = (WindowManager)getActivity().getSystemService("window");
		Display display = winManager.getDefaultDisplay(); // 获取屏幕的宽、高
		Point size = new Point();
		display.getSize(size);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		lp.width = (int) (size.x * 0.7); // 宽度设置为屏幕的0.7
		dialogWindow.setAttributes(lp);

		TextView msg=(TextView) dialog.findViewById(R.id.dialog_loading_text);
		msg.setText(R.string.about_checking_version);
		
		// 开始旋转动画
		ImageView img = (ImageView) dialog
				.findViewById(R.id.dialog_loading_img);
		RotateAnimation anim = new RotateAnimation(0, 360,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		anim.setDuration(1000);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(-1);
		img.startAnimation(anim);

		dialog.show();
	}

	/**
	 * 关闭检测loading
	 */
	private void closeLoadingDialog() {
		if(dialog!=null){
			dialog.dismiss();
		}
	}
	
	/**
	 * 
	 * @Description listview点击监听
	 * @author ganxin
	 * @date Nov 8, 2015
	 * @email ganxinvip@163.com
	 */
	private class AboutItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			switch (position) {
			case 0:
				processChcekVersion();
				break;
			case 1:
				ShareManager.getInstance().popUmengShareDialog(getActivity());
				break;
			case 2:
				Intent intent=new Intent();
				intent.setClass(getActivity(), AboutDetailActivity.class);
				getActivity().startActivity(intent);
				break;
			default:
				break;
			}
		}		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {		
		super.onActivityResult(requestCode, resultCode, data);		
		ShareManager.getInstance().setOnActivityResult(requestCode, resultCode, data);
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
	
	
}
