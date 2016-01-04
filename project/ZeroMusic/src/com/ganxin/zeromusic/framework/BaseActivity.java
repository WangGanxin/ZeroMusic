package com.ganxin.zeromusic.framework;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.ganxin.zeromusic.common.manager.TimingManager;
import com.ganxin.zeromusic.common.util.AppUtil;
import com.ganxin.zeromusic.common.util.LogHelper;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @Description Activity基类
 * @author ganxin
 * @date 2015-1-6
 * @email ganxinvip@163.com
 */
public class BaseActivity extends FragmentActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		AppUtil.addActivity(this);
		LogHelper.logD(this.getClass().getSimpleName() + "----------onCreat");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		LogHelper.logD(this.getClass().getSimpleName() + "----------onStart");
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		LogHelper.logD(this.getClass().getSimpleName() + "----------onRestart");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		LogHelper.logD(this.getClass().getSimpleName() + "----------onPause");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		LogHelper.logD(this.getClass().getSimpleName() + "----------onStop");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		LogHelper.logD(this.getClass().getSimpleName() + "----------onResume");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//每次退出应用重置定时器
		TimingManager.getInstance().resetTimingClose(this);
		LogHelper.logD(this.getClass().getSimpleName() + "----------onDestroy");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}
