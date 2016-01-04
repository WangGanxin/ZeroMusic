package com.ganxin.zeromusic.module;

import grd.lks.oew.AdManager;
import grd.lks.oew.onlineconfig.OnlineConfigCallBack;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ganxin.zeromusic.application.AppConstant;
import com.ganxin.zeromusic.application.PlayerConstant;
import com.ganxin.zeromusic.common.bean.MusicBean;
import com.ganxin.zeromusic.common.db.MusicDBHelper;
import com.ganxin.zeromusic.common.listener.OnPlayerStateChangeListener;
import com.ganxin.zeromusic.common.manager.ShareManager;
import com.ganxin.zeromusic.common.util.LogHelper;
import com.ganxin.zeromusic.common.util.SharPreferHelper;
import com.ganxin.zeromusic.common.util.ToastHelper;
import com.ganxin.zeromusic.common.widget.residemenu.ResideMenu;
import com.ganxin.zeromusic.common.widget.residemenu.ResideMenuItem;
import com.ganxin.zeromusic.framework.BaseActivity;
import com.ganxin.zeromusic.module.about.AboutFragment;
import com.ganxin.zeromusic.module.home.HomeFragment;
import com.ganxin.zeromusic.module.search.SearchFragment;
import com.ganxin.zeromusic.module.setting.SettingFragment;
import com.ganxin.zeromusic.module.timing.TimingFragment;
import com.ganxin.zeromusic.receiver.PlayerReceiver;
import com.ganxin.zeromusic.service.PlayerService;
import com.ganxin.zeromusic.view.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * 
 * @Description 侧滑菜单的主界面
 * @author ganxin
 * @date Sep 18, 2014
 * @email ganxinvip@163.com
 */
public class MenuActivity extends BaseActivity{
	private Context mContext;
	private ResideMenu resideMenu;	
	private ResideMenuItem itemHome;
	private ResideMenuItem itemSearch;
	private ResideMenuItem itemTiming;
	private ResideMenuItem itemSetting;
	private ResideMenuItem itemAbout;
	private ResideMenuItem itemExit;
	
	private TextView musicTitle; //顶部标题
	private Intent playerService; //用于开启服务
	
	public static AudioManager audioManager;
	
	// 回调接口更新UI---播放状态
	private OnPlayerStateChangeListener stateChangeListener;
		
	private MusicDBHelper dbHelper;
	private Boolean operation=false;
	
	private Handler mHandler;
	private static final int CHECK_VERSION=10;
	private static final int CHECK_SHOW_ADVIEW=11;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext=this;
		setContentView(R.layout.activity_menu);
		setMarquee();
		setMenuItem();
		changeFragment(new HomeFragment());
		initHandler();
		ininData();
		initAutoStart();
	}

	/**
	 * 设置界面标题的走马灯效果
	 */
	private void setMarquee(){
		musicTitle=(TextView) findViewById(R.id.title_bar_music);
		musicTitle.setSelected(true);
	}
	
	/**
	 * 设置界面的菜单项及相关的监听器
	 */
	private void setMenuItem() {
		// TODO Auto-generated method stub	
		//添加resideMenu到当前的Activity
		resideMenu=new ResideMenu(this);
		resideMenu.setBackground(R.drawable.menu_background);
		resideMenu.attachToActivity(this);
		
		//创建resideMenu的菜单项
        itemHome=new ResideMenuItem(this, R.drawable.icon_home, R.string.menu_home);
        itemSearch=new ResideMenuItem(this, R.drawable.icon_search, R.string.menu_search);
        itemTiming=new ResideMenuItem(this, R.drawable.icon_timing, R.string.menu_timing);
        itemSetting=new ResideMenuItem(this, R.drawable.icon_settings, R.string.menu_setting);
        itemAbout=new ResideMenuItem(this, R.drawable.icon_about, R.string.menu_about);
        itemExit=new ResideMenuItem(this, R.drawable.icon_exit, R.string.menu_exit);
		
        //添加到resideMenu中
        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemSearch, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemTiming, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemSetting, ResideMenu.DIRECTION_LEFT);
        
        resideMenu.addMenuItem(itemAbout, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemExit, ResideMenu.DIRECTION_RIGHT);
        
        //添加监听器
        itemHome.setOnClickListener(this);
        itemSearch.setOnClickListener(this);
        itemTiming.setOnClickListener(this);
        itemSetting.setOnClickListener(this);
        itemAbout.setOnClickListener(this);
        itemExit.setOnClickListener(this);
        
        //点击标题栏的图标按钮弹出resideMenu
        findViewById(R.id.title_bar_left_menu).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
			}     	
        });
     
        findViewById(R.id.title_bar_right_menu).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
			}     	
        });        
        
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		//手势滑动开启/关闭菜单
		return resideMenu.dispatchTouchEvent(ev);
	}
	
    private void changeFragment(Fragment targetFragment){
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    
	private void initHandler() {
		// TODO Auto-generated method stub
		if(mHandler==null){
			mHandler=new Handler(){
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					switch (msg.what) {
					case CHECK_VERSION:
						UmengUpdateAgent.update(mContext);
						break;
					case CHECK_SHOW_ADVIEW:
						checkShowAdView();
						break;
					default:
						break;
					}
				}				
			};
		}
	}
	
    private void ininData(){		
		operation=getIntent().getBooleanExtra(AppConstant.TIMING_OPEN_AUTO,false);
		
		dbHelper=new MusicDBHelper(this);
		
		//启动 playerService
		playerService = new Intent(this, PlayerService.class);
		startService(playerService);
				
		//获取音频管理器实例
		audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
		
		//实例化状态监听器
		stateChangeListener=new OnPlayerStateChangeListener() {
			
			@Override
			public void onStateChange(int state, int mode, List<MusicBean> musicList,
					int position) {
				// TODO Auto-generated method stub
				if(musicList!=null){
					musicTitle.setText(musicList.get(position).getTitle()+
							"-"+musicList.get(position).getArtist());
				}

			}
		};
		
		if(mHandler!=null){
			mHandler.sendEmptyMessageDelayed(CHECK_VERSION, 2500);
			mHandler.sendEmptyMessageDelayed(CHECK_SHOW_ADVIEW, 1500);
		}
    }
    
    private void initAutoStart(){
    	if(operation){
    		operation=false;
    		if(dbHelper!=null){
    			final ArrayList<MusicBean> list=dbHelper.queryHistoryByID();    			
    			if(list!=null&&list.size()>0){
    	    		ToastHelper.makeText(this,R.string.app_auto_starting,Toast.LENGTH_LONG).show();
    	    		new Handler().postDelayed(new Runnable() {
    					
    					@Override
    					public void run() {
    						// TODO Auto-generated method stub
    						Intent intent = new Intent();
    						intent.setAction(PlayerReceiver.ACTION_PLAY_ITEM);
    						intent.putParcelableArrayListExtra(PlayerConstant.PLAYER_LIST,list);
    						intent.putExtra(PlayerConstant.PLAYER_WHERE, "local");
    						intent.putExtra(PlayerConstant.PLAYER_POSITION,0);

    						sendBroadcast(intent);
    					}
    				},5000);
    			}
    		}
    	}
    }
    
    private void checkShowAdView(){
		// TODO Auto-generated method stub
		AdManager.getInstance(mContext).asyncGetOnlineConfig(AppConstant.SHOW_AD_SWITCH,new OnlineConfigCallBack() {
			
			@Override
			public void onGetOnlineConfigSuccessful(String key, String value) {

				if(value!=null&&value.equalsIgnoreCase("true")){
					SharPreferHelper.setConfig(mContext,AppConstant.SHARPREFER_FILENAME,
							AppConstant.LOCAL_SHOW_ADVIEW,true);
				}
				else{
					SharPreferHelper.setConfig(mContext,AppConstant.SHARPREFER_FILENAME,
							AppConstant.LOCAL_SHOW_ADVIEW,false);
				}
			}
			
			@Override
			public void onGetOnlineConfigFailed(String arg0) {
				// TODO Auto-generated method stub
				SharPreferHelper.setConfig(mContext,AppConstant.SHARPREFER_FILENAME,
						AppConstant.LOCAL_SHOW_ADVIEW,false);
			}
		});    	
    }
    
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
        if (view == itemHome){
            changeFragment(new HomeFragment());
            MobclickAgent.onEvent(mContext,"home");
        }else if (view == itemSearch){
            changeFragment(new SearchFragment());
            MobclickAgent.onEvent(mContext,"search");
        }else if (view == itemTiming){
            changeFragment(new TimingFragment());
            MobclickAgent.onEvent(mContext,"timing");
        }else if (view == itemSetting){
            changeFragment(new SettingFragment());
            MobclickAgent.onEvent(mContext,"setting");
        }else if(view == itemAbout){
        	changeFragment(new AboutFragment());
        	MobclickAgent.onEvent(mContext,"about");
        }else if(view == itemExit){
        	//System.exit(0);
        	MobclickAgent.onEvent(mContext,"exit");
        	finish();
        }
        
        resideMenu.closeMenu();
	}
	
	// What good method is to access resideMenu？
	public ResideMenu getResideMenu() {
		return resideMenu;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_MENU)
			resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
		
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 重写按下back键方法，程序切换到后台而不被destroy
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent= new Intent(Intent.ACTION_MAIN);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        intent.addCategory(Intent.CATEGORY_HOME);  
        startActivity(intent);
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		ShareManager.getInstance().setOnActivityResult(arg0,arg1,arg2);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// 注册播放状态改变、播放模式、进度条的监听器
		PlayerService.registerStateChangeListener(stateChangeListener);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		// 解除注册状态改变、播放模式、进度条的监听器
		PlayerService.removeStateChangeListener(stateChangeListener);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(mHandler!=null){
			mHandler=null;
		}
		stopService(playerService);
		LogHelper.logD("stop service---ok???");
		super.onDestroy();
	}	
}
