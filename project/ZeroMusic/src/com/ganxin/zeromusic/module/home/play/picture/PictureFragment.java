package com.ganxin.zeromusic.module.home.play.picture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;
import com.ganxin.zeromusic.application.AppConstant;
import com.ganxin.zeromusic.application.PlayerConstant;
import com.ganxin.zeromusic.common.bean.MusicBean;
import com.ganxin.zeromusic.common.db.MusicDBHelper;
import com.ganxin.zeromusic.common.http.volleyHelper.RequestParams;
import com.ganxin.zeromusic.common.http.volleyImage.ImageCacheManager;
import com.ganxin.zeromusic.common.listener.OnPlayerStateChangeListener;
import com.ganxin.zeromusic.common.manager.SettingManager;
import com.ganxin.zeromusic.common.util.LogHelper;
import com.ganxin.zeromusic.common.util.NetWorkHelper;
import com.ganxin.zeromusic.common.util.ToastHelper;
import com.ganxin.zeromusic.receiver.PlayerReceiver;
import com.ganxin.zeromusic.service.PlayerService;
import com.ganxin.zeromusic.view.R;

/**
 * 
 * @Description 专辑封面图片界面
 * @author ganxin
 * @date Mar 13, 2015
 * @email ganxinvip@163.com
 */
public class PictureFragment extends Fragment {
	private View parentView;
	
	// 定义sensor管理器, 注册加速度监听器用
	private SensorManager mSensorManager;
	// 设备震动
	private Vibrator vibrator;
	// 显示歌曲的专辑图像
	private NetworkImageView networkImageview;
	// 默认显示的专辑图像
	private ImageView defaultImageView;
	// 等待的进度条
	private ImageView loadingImg;
	// 灰色背景的布局
	private RelativeLayout loadingLayout;
	//播放歌曲的列表
	private List<MusicBean> list;
	//播放歌曲的位置
	private int pos;
	// 数据库帮助类
	private MusicDBHelper dbHelper;
	
	// 回调函数更新UI
	private OnPlayerStateChangeListener stateChangeListener;
	
	private Handler mHandler;
	private final static int UI_SHOW_LOADING=0;
	private final static int UI_CLOSE_LOADING=1;
	private final static int UI_SHOW_TOAST=2;
	private final static int UI_SHOW_IMAGE=3;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		parentView = inflater.inflate(R.layout.home_play_picture, container,false);
		initHandler();
		initData();
		return parentView;
	}

	private void initHandler() {
		// TODO Auto-generated method stub
		if(mHandler==null){
			mHandler=new Handler(){
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					switch (msg.what) {
					case UI_SHOW_LOADING:
						showLoadingAnimation();
						break;
					case UI_CLOSE_LOADING:
						closeLoadingAnimation();
						networkImageview.setErrorImageResId(R.drawable.home_play_picture_default);
						break;
					case UI_SHOW_TOAST:
						ToastHelper.show(getActivity(), R.string.network_is_not_connected);
						break;
					case UI_SHOW_IMAGE:
						@SuppressWarnings("unchecked")
						List<String> urlList=(List<String>) msg.obj;
						if(urlList!=null&&urlList.size()>0){
							setNetworkImage(urlList);
						}
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
		
		networkImageview=(NetworkImageView) parentView.findViewById(R.id.home_play_network_picture);
		defaultImageView=(ImageView) parentView.findViewById(R.id.home_play_pic_default);
		loadingImg=(ImageView) parentView.findViewById(R.id.home_play_loading_img);
		loadingLayout=(RelativeLayout) parentView.findViewById(R.id.home_play_loading_layout);
		
		// 数据库帮助类对象
		dbHelper=new MusicDBHelper(getActivity());
		// 获取传感器管理服务
		mSensorManager = (SensorManager) getActivity().getSystemService(Service.SENSOR_SERVICE);
		// 获取设备震动的服务
		vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
		
		stateChangeListener=new OnPlayerStateChangeListener() {
			
			@Override
			public void onStateChange(int state, int mode, List<MusicBean> musicList,
					int position) {
				// TODO Auto-generated method stub
				list=musicList;
				pos=position;
				if(musicList!=null){
					//获取歌词路径
					String album = musicList.get(position).getAlbum();
					LogHelper.logD("album  is---->"+album);
					if(album!=null&&album.contains("http://")){
						defaultImageView.setVisibility(View.GONE);
						networkImageview.setVisibility(View.VISIBLE);
						networkImageview.setImageUrl(album, ImageCacheManager.getInstance().getImageLoader());
						networkImageview.setErrorImageResId(R.drawable.home_play_picture_default);
					}																	  
					else{
						defaultImageView.setVisibility(View.VISIBLE);
						networkImageview.setVisibility(View.GONE);
					}
				}
				else{
					defaultImageView.setVisibility(View.VISIBLE);
					networkImageview.setVisibility(View.GONE);
				}		
				
			}
		};
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// 注册播放状态改变的监听器
		PlayerService.registerStateChangeListener(stateChangeListener);
		
		// 注册加速度传感器
		mSensorManager.registerListener(sensorEventListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				// 还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，
				// 根据不同应用，需要的反应速率不同，具体根据实际情况设定
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		// 解除注册状态改变的监听器
		PlayerService.removeStateChangeListener(stateChangeListener);		
		// 解除注册加速度的传感器的监听
		mSensorManager.unregisterListener(sensorEventListener);
	}
	
	/**
	 * 获取歌曲的图片的url(来源于：有道图片搜索引擎)
	 */
	private void getYouDaoImageURL(){
		if(NetWorkHelper.isConnected(getActivity())){
			if(mHandler!=null){
				mHandler.sendEmptyMessage(UI_SHOW_LOADING);
			}
			String musicName=list.get(pos).getTitle();
			String artistName=list.get(pos).getArtist();
			
			LogHelper.logD("music----->"+musicName);
			LogHelper.logD("artist----->"+artistName);
			
			String url="";
			if(artistName.equalsIgnoreCase("unknown")){
				url=RequestParams.getYouDaoRequestUrl(musicName);
			}
			else{
				url=RequestParams.getYouDaoRequestUrl(artistName);
			}
						
			try {
				Document doc = Jsoup.connect(url).get();
				
				if(doc!=null){
					Elements list = doc.getElementsByClass("imgthumb"); 
					
					if(list!=null&&list.size()>0){
						List<String> urlList=new ArrayList<String>();
						
						int size=list.size()>20?20:list.size();
						LogHelper.logD("list size---->"+list.size());
						for(int i=0;i<size;i++){
							 urlList.add(list.get(i).attr("src"));							 
						}
						if(mHandler!= null){
							mHandler.obtainMessage(UI_SHOW_IMAGE, urlList).sendToTarget();
						}
					}
					else{
						if(mHandler!=null){
							mHandler.sendEmptyMessage(UI_CLOSE_LOADING);
						}						
					}
				}
				else{
					if(mHandler!=null){
						mHandler.sendEmptyMessage(UI_CLOSE_LOADING);
					}				
				}
			} catch (IOException e) {
				e.printStackTrace();
				if(mHandler!=null){
					mHandler.sendEmptyMessage(UI_CLOSE_LOADING);
				}
			}
		}
		else {
			if(mHandler!=null){
				mHandler.sendEmptyMessage(UI_SHOW_TOAST);
			}
		}		
	}
	
	/**
	 * 设置网络图片
	 * @param image
	 */
	private void setNetworkImage(List<String> urlList){
		int randomPosition=(int)(Math.random()*urlList.size()-1);
		String url=urlList.get(randomPosition);
					
		//如果取得url为空，或者不以http开头，或为gif图片类型，重新取值
		if(url==null||!url.startsWith("http:")||url.endsWith(".gif")){
			randomPosition=(int)(Math.random()*urlList.size()-1);
			url=urlList.get(randomPosition);
		}
		
		LogHelper.logD("url------>"+url+"-----pos--"+randomPosition);
		
		ImageLoader imageLoader=ImageCacheManager.getInstance().getImageLoader();
		ImageContainer iContainer=imageLoader.get(url,new ImageListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				if(mHandler!=null){
					mHandler.sendEmptyMessage(UI_CLOSE_LOADING);
				}	
			}
			
			@Override
			public void onResponse(ImageContainer arg0, boolean arg1) {
				// TODO Auto-generated method stub					
				if(!arg1){
					LogHelper.logD("新的图片！！！！--------");
					defaultImageView.setVisibility(View.GONE);
				    networkImageview.setVisibility(View.VISIBLE);
				    networkImageview.setImageUrl(arg0.getRequestUrl(),ImageCacheManager.getInstance().getImageLoader());
				    dbHelper.updateLocalAlbum(list.get(pos).getId(),arg0.getRequestUrl());
				    updateServiceListBroadCast(pos,arg0.getRequestUrl());
				    closeLoadingAnimation();
				}
			}
		});
		
		if(iContainer.getBitmap()!=null){
			LogHelper.logD("icontener------>缓存的图片！");			
			defaultImageView.setVisibility(View.GONE);
		    networkImageview.setVisibility(View.VISIBLE);
		    networkImageview.setImageUrl(iContainer.getRequestUrl(),ImageCacheManager.getInstance().getImageLoader());
		    dbHelper.updateLocalAlbum(list.get(pos).getId(),iContainer.getRequestUrl());
		    updateServiceListBroadCast(pos,iContainer.getRequestUrl());
		    closeLoadingAnimation();
		}		
	}
		
	/**
	 * 显示切换图片时的加载动画
	 */
	private void showLoadingAnimation(){
		//显示灰色背景
		loadingLayout.setVisibility(View.VISIBLE);
		//开始旋转动画
		RotateAnimation anim = new RotateAnimation(0, 360,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		anim.setDuration(1500);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(-1);	
		loadingImg.startAnimation(anim);
	}
	
	/**
	 * 关闭切换动画
	 */
	private void closeLoadingAnimation(){
		loadingLayout.setVisibility(View.GONE);
	}
	
	/**
	 * 更新后台服务中播放的歌曲队列的广播
	 * @param position 位置
	 * @param album 专辑地址URL
	 */
	private void updateServiceListBroadCast(int position,String album){
		Intent intent=new Intent();
		intent.setAction(PlayerReceiver.ACTION_UPDATE_LIST);
		intent.putExtra(PlayerConstant.PLAYER_POSITION, position);
		intent.putExtra(PlayerConstant.PLAYER_LIST_ALBUM, album);
		getActivity().sendBroadcast(intent);
	}

	/**
	 * 传感器的监听对象
	 */
	private SensorEventListener sensorEventListener = new SensorEventListener() {

		// 上次检测时间
		private long lastUpdateTime;

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			int sensorType = event.sensor.getType();
			// values[0]:X轴，values[1]：Y轴，values[2]：Z轴
			float[] values = event.values;

			if (sensorType == Sensor.TYPE_ACCELEROMETER) {
				
				// 现在检测时间
				long currentUpdateTime = System.currentTimeMillis();
				// 两次检测的时间间隔
				long timeInterval = currentUpdateTime - lastUpdateTime;
				if (timeInterval < 800)
					return;

				// 现在的时间变成last时间
				lastUpdateTime = currentUpdateTime;
				
				int strength=14;
				int type=SettingManager.getInstance().getSensorType(getActivity());
				switch (type) {
				case AppConstant.SENSOR_TYPE_HIGH:
					strength=11;
					break;
				case AppConstant.SENSOR_TYPE_MIDDLE:
					strength=14;
					break;
				case AppConstant.SENSOR_TYPE_LOWER:
					strength=16;
					break;
				default:
					break;
				}
				// 正常情况下，任意轴数值最大就在9.8~10之间，只有在突然摇动手机的时候，瞬时加速度才会突然增大或减少
				// 监听任一轴的加速度大于14即可(可能每个设备的灵敏度不一样！)				
				if ((Math.abs(values[0]) > strength || Math.abs(values[1]) > strength || Math.abs(values[2]) > strength)) {
					if(list!=null){					
						new NetWorkThread().start();
						// 摇动手机后，再伴随震动提示
						vibrator.vibrate(new long[] { 300, 200, 300, 200 }, -1);	
					}			
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

	};
		
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		if(mHandler!=null){
			mHandler=null;
		}
		
		super.onDestroyView();
	}

	/**
	 * 
	 * @Description 网络请求线程
	 * @author ganxin
	 * @date Nov 4, 2015
	 * @email ganxinvip@163.com
	 */
	public class NetWorkThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getYouDaoImageURL();
		}		
	}
}
