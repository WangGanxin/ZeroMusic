package com.ganxin.zeromusic.module;

import grd.lks.oew.st.SplashView;
import grd.lks.oew.st.SpotDialogListener;
import grd.lks.oew.st.SpotManager;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ganxin.zeromusic.application.AppConstant;
import com.ganxin.zeromusic.common.util.LogHelper;
import com.ganxin.zeromusic.common.util.NetWorkHelper;
import com.ganxin.zeromusic.common.util.SharPreferHelper;
import com.ganxin.zeromusic.framework.BaseActivity;
import com.ganxin.zeromusic.view.R;

/**
 * 
 * @Description 应用启动欢迎界面
 * @author ganxin
 * @date Sep 18, 2014
 * @email ganxinvip@163.com
 */
public class LoadingActivity extends BaseActivity {

	private Context context;
	private ImageView imageViewNormal;
	private RelativeLayout mainLayout,adsLayout;
	private Animation animation;
	
	private SplashView splashView;
	private View splash;
	
	private int loadingNormalCount;
	private boolean hasShowGudie;
	private boolean openAdViewFlag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		setContentView(R.layout.activity_loading);			
		initView();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		imageViewNormal=(ImageView)this.findViewById(R.id.imageView);		
		mainLayout=(RelativeLayout) this.findViewById(R.id.loading_main_layout);
		adsLayout=(RelativeLayout) this.findViewById(R.id.loading_ads_layout);
	}
	
    private void initData() {
	// TODO Auto-generated method stub 
    	loadingNormalCount=SharPreferHelper.getIntConfig(context,AppConstant.SHARPREFER_FILENAME,
    			AppConstant.LOADING_NORMAL_COUNT,0);
	
    	hasShowGudie=SharPreferHelper.getBooleanConfig(context,AppConstant.SHARPREFER_FILENAME,
    			AppConstant.GUIDE_SHOW,false);
    	
    	openAdViewFlag=SharPreferHelper.getBooleanConfig(context,AppConstant.SHARPREFER_FILENAME,
    			AppConstant.LOCAL_SHOW_ADVIEW,false);
    	
    	if(!hasShowGudie){
    		showGuide();
    	}
    	else{
        	if(loadingNormalCount<AppConstant.LOADING_NORMAL_Max||!NetWorkHelper.isConnected(this)||!openAdViewFlag){
        		showNormalAnimation();
        	}
        	else{
        		showAds();
        	}
    	}
    }
    
    /**
     * 显示引导页
     */
    private void showGuide(){
		Intent intent = new Intent(LoadingActivity.this, GuideActivity.class);
		startActivity(intent);
		finish();
    }
    
    /***
     * 显示正常动画
     */
    private void showNormalAnimation(){
    	imageViewNormal.setVisibility(View.VISIBLE);
    	adsLayout.setVisibility(View.GONE);
    	
		animation=new AlphaAnimation(0.2f,1.0f); //实例化animation对象
		animation.setDuration(1500);             //设置动画持续时间
		imageViewNormal.startAnimation(animation);     //关联到iamgeView控件并启动
		
		//设置AnimationListener监听器
		animation.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				//创建定时器
				Timer timer = new Timer();
				//创建定时任务
				TimerTask task = new TimerTask() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Intent intent = new Intent(LoadingActivity.this, MenuActivity.class);
						startActivity(intent);
						finish();						
					}
				};
				timer.schedule(task, 4000);		
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {}
			
		});
    }
    
    private void showAds(){
    	mainLayout.setBackground(getResources().getDrawable(R.drawable.loading_bg));    		
    	imageViewNormal.setVisibility(View.GONE);

		// 第二个参数传入目标activity，或者传入null，改为setIntent传入跳转的intent
		splashView = new SplashView(context, null);
		// 设置是否显示倒数
		splashView.setShowReciprocal(true);
		// 隐藏关闭按钮
		splashView.hideCloseBtn(true);

		Intent intent = new Intent(context,MenuActivity.class);
		splashView.setIntent(intent);
		splashView.setIsJumpTargetWhenFail(true);		
		splash = splashView.getSplashView();

		adsLayout.setVisibility(View.GONE);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
		params.addRule(RelativeLayout.ABOVE, R.id.loading_cut_line);
		adsLayout.addView(splash,params);
		
		SpotManager.getInstance(context).showSplashSpotAds(context, splashView, new SpotDialogListener() {
			
			@Override
			public void onSpotClosed() {
				// TODO Auto-generated method stub
				LogHelper.logD("广告-------------展示关闭");
			}
			
			@Override
			public void onShowSuccess() {
				// TODO Auto-generated method stub
				adsLayout.setVisibility(View.VISIBLE);
				adsLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pic_enter_anim_alpha));
				LogHelper.logD("广告-------------展示成功");
			}
			
			@Override
			public void onShowFailed() {
				// TODO Auto-generated method stub
				LogHelper.logD("广告-------------展示失败");
			}

			@Override
			public void onSpotClick(boolean arg0) {
				// TODO Auto-generated method stub
				LogHelper.logD("广告-------------插屏点击");
			}
		});			
    }
	
	// 请务必加上词句，否则进入网页广告后无法进去原sdk
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 10045) {
			Intent intent = new Intent(context, MenuActivity.class);
			startActivity(intent);
			finish();
		}
	}
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		//按下"返回"按键，退出应用程序
		if(keyCode==KeyEvent.KEYCODE_BACK)
			System.exit(0);
				
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_launcher, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		SharPreferHelper.setConfig(context, AppConstant.SHARPREFER_FILENAME,
				AppConstant.LOADING_NORMAL_COUNT,loadingNormalCount+1);
		super.onDestroy();
	}		
}
