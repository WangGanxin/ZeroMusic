package com.ganxin.zeromusic.service;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import com.ganxin.zeromusic.application.AppConstant;
import com.ganxin.zeromusic.application.PlayerConstant;
import com.ganxin.zeromusic.application.ZeroMusicApplication;
import com.ganxin.zeromusic.common.bean.MusicBean;
import com.ganxin.zeromusic.common.db.MusicDBHelper;
import com.ganxin.zeromusic.common.listener.OnBufferingUpdateListener;
import com.ganxin.zeromusic.common.listener.OnModeChangeListener;
import com.ganxin.zeromusic.common.listener.OnPlayerStateChangeListener;
import com.ganxin.zeromusic.common.listener.OnSeekChangeListener;
import com.ganxin.zeromusic.common.listener.OnSettingsChangedListenr;
import com.ganxin.zeromusic.common.manager.SettingManager;
import com.ganxin.zeromusic.common.util.HeadsetUtil;
import com.ganxin.zeromusic.common.util.HeadsetUtil.IHeadsetState;
import com.ganxin.zeromusic.common.util.LogHelper;
import com.ganxin.zeromusic.common.util.PlayerHelper;
import com.ganxin.zeromusic.receiver.PlayerReceiver;
import com.ganxin.zeromusic.view.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @Description 音乐播放的后台服务
 * @author ganxin
 * @date 2014-12-10
 * @email ganxinvip@163.com
 */
public class PlayerService extends Service implements Runnable,OnSettingsChangedListenr{
	// 音乐的广播接收者
	private PlayerReceiver receiver;
	// 播放歌曲帮助类
	private static PlayerHelper playerHelper;
	private MusicDBHelper dbHelper;
	// 常驻线程是否运行
	private static Boolean isRun = true;
	// 状态栏通知管理
	private NotificationManager notificationManager;
	// 状态栏通知
	private Notification notification;
	private String notifiTitle;
	private String notifiTickerText;
	private String notifiArtist;
	private static final int NOTIFI_ID = 110;
	private static boolean NOTIFI_FLAG=false;

	// 分别表示播放状态是否改变，进度条是否改变，播放模式是否改变
	public static boolean stateChange, seekChange, modeChange;
	// 点击的是哪个列表
	public static String where;
	// 当前播放列表
	public static List<MusicBean> serviceMusicList;
	// 当前播放歌曲位置
	public static int servicePosition = 0;
	// 当前音乐播放状态，默认为等待
	public static int state = PlayerConstant.STATE_WAIT;
	// 当前音乐循环模式，默认为顺序
	public static int mode = PlayerConstant.MODE_ORDER;
	// 当前歌曲播放进度
	private static int progress = 0;
	// 当前歌曲进度条最大值
	private static int max = 0;
	// 当前播放的时间
	private static String timePosition = "0:00";
	// 当前歌曲播放的时长
	private static String duration = "0:00";

	// 用一个List保存 客户注册的监听----用于回调更新客户的ui，状态改变、seekbar进度改变的监听
	private static List<OnPlayerStateChangeListener> stateListenerList = new ArrayList<OnPlayerStateChangeListener>();
	private static List<OnSeekChangeListener> seekListenerList = new ArrayList<OnSeekChangeListener>();
	private static List<OnModeChangeListener> modeListenerList = new ArrayList<OnModeChangeListener>();
	private static List<WeakReference<OnBufferingUpdateListener>> bufferingUpdateListenerList=new ArrayList<WeakReference<OnBufferingUpdateListener>>();
	
	//耳机插拔监听
	private IHeadsetState headSetStateListener;

	// handler匿名内部类，用于监听器遍历回调
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// 对List中的所有监听器遍历回调，根据what值判断回调哪个监听器
			switch (msg.what) {
			case 0:
				for (OnPlayerStateChangeListener playerListener : stateListenerList)
					playerListener.onStateChange(state, mode, serviceMusicList,
							servicePosition);					
				break;
			case 1:
                for(OnSeekChangeListener seekChangeListener : seekListenerList)
    				seekChangeListener.onSeekChange(progress, max, timePosition, duration);                	
				break;
			case 2:
                for(OnModeChangeListener modeListener:modeListenerList)
                	modeListener.onModeChange(mode);
				break;
			default:
				break;
			}
		}

	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		// 注册广播，并添加Action
		receiver = new PlayerReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(PlayerReceiver.ACTION_PLAY_ITEM); // 播放歌曲
		filter.addAction(PlayerReceiver.ACTION_BACKUP_ZERO); // 应用返回前台
		filter.addAction(PlayerReceiver.ACTION_PLAY_BUTTON); // 播放和暂停键
		filter.addAction(PlayerReceiver.ACTION_PLAY_PREVIOUS); // 上一首
		filter.addAction(PlayerReceiver.ACTION_PLAY_NEXT); // 下一首
		filter.addAction(PlayerReceiver.ACTION_PLAY_MODE); // 播放模式
		filter.addAction(PlayerReceiver.ACTION_SEEKBAR); // 进度条
		filter.addAction(PlayerReceiver.ACTION_UPDATE_LIST); // 更新list

		registerReceiver(receiver, filter);

		LogHelper.logD("service=======start");

		playerHelper = PlayerHelper.getInstance();
		dbHelper=new MusicDBHelper(this);

		// 开启常驻线程
		new Thread(this).start();

		initHeadSet();
		return super.onStartCommand(intent, flags, startId);
	}

	private void initHeadSet() {
		// TODO Auto-generated method stub		
		//注册监听
		SettingManager.getInstance().addSettingsChangedListener(this,AppConstant.SETTING_HEADSET_OPEN);
		
		boolean headSetFlag=SettingManager.getInstance().getLineOutPauseCheck(this);
		if(headSetFlag){
			headSetStateListener=new IHeadsetState() {
				
				@Override
				public void headsetPull(String name, boolean microphone) {
					// TODO Auto-generated method stub
					if(playerHelper.isPlaying()){
						Intent intent=new Intent(PlayerReceiver.ACTION_PLAY_BUTTON);
						sendBroadcast(intent);
					}
				}
				
				@Override
				public void headsetPlug(String name, boolean microphone) {}
			};
			
			HeadsetUtil.registHeadsetStateListener(this, headSetStateListener);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
		if(headSetStateListener!=null){
			HeadsetUtil.unregistHeadsetStateListener(this,headSetStateListener);
		}
		
		// 已解决--这句话在没有播放歌曲时就关闭应用程序会造成NullException，届时需要放在其他位置
		if(NOTIFI_FLAG)
		   notificationManager.cancel(NOTIFI_ID);
		
		//added 2015.11.07 退出时能关闭通知栏消息
		if(notificationManager!=null){
			notificationManager.cancelAll();
		}
		
		unregisterReceiver(receiver);
		
		MobclickAgent.onKillProcess(this);
		
		System.exit(0);

		LogHelper.logD("service=======destroy");
		super.onDestroy();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		LogHelper.logD("thread------------runing");

		// 常驻线程，死循环
		while (isRun) {
			// 如果播放状态发生改变，player播放类执行不同的方法

			if (stateChange) {
				switch (state) {
				case PlayerConstant.STATE_WAIT:
					break;
				case PlayerConstant.STATE_PLAY:
					LogHelper.logD("播放歌曲路径----》"
							+ serviceMusicList.get(servicePosition).getPath());
					if (where.equals("local")) {
						playerHelper.play(serviceMusicList.get(servicePosition)
								.getPath());
						
						List<MusicBean> list=dbHelper.queryHistoryByID();
						if(list!=null&&list.size()>0){
							dbHelper.updateHistoryTable(serviceMusicList.get(servicePosition));
						}
						else{
							dbHelper.inserHistory(serviceMusicList.get(servicePosition));
						}
					}
					else if(where.equals("online")){
						String url=serviceMusicList.get(servicePosition)
								.getPath();
						playerHelper.playOnline(ZeroMusicApplication.getContext(), Uri.parse(url));
					}

					// 播放状态要移动seekbar
					seekChange = true;
					break;
				case PlayerConstant.STATE_PAUSE:
					playerHelper.pause();
					break;
				case PlayerConstant.STATE_CONTINUE:
					playerHelper.continuePlay();
					// 播放状态要动seekbar
					seekChange = true;
					break;
				case PlayerConstant.STATE_STOP:
					playerHelper.stop();
					break;
				default:
					break;
				}

				// state改变为false
				stateChange = false;
				// 向handler发送一条消息，通知handler执行回调函数
				handler.sendEmptyMessage(0);

				// 显示通知
				notifiTitle = serviceMusicList.get(servicePosition).getTitle();
				notifiArtist = serviceMusicList.get(servicePosition)
						.getArtist();
				notifiTickerText = "正在播放：";

				showNotification(notifiTitle, notifiArtist, notifiTickerText);

			}

			if (playerHelper.isPlaying()) {
				seekChange = true;
			} else {
				seekChange = false;
			}
			
			//如果进度条发生了改变，执行以下
			if (seekChange) {
				// 得到当前播放时间，int，毫秒单位，也是进度条的当前进度
				progress = playerHelper.getPlayCurrentTime();
				// 得到歌曲播放总时长，为进度条的最大值
				max = playerHelper.getPlayDuration();
				// 当前播放时间改变单位为分
				float floatTime = (float) progress / 1000.0f / 60.0f;
				
				// 当前播放时间转换为字符类型
				String timeStr = Float.toString(floatTime);
				// 根据小数点切分
				String timeSub[] = timeStr.split("\\.");
				// 初始值为0.0，在后边补0
				if (timeSub[1].length() < 2) {
					timeSub[1] = timeSub[1] + "0";
				} else {
					// 截取小数点后两位
					timeSub[1] = timeSub[1].substring(0, 2);
				}
				
				float sec=Float.parseFloat(timeSub[1])*0.6f;
				String secStr="";
				if(sec<10.0){
					String secSub[] = String.valueOf(sec).split("\\.");
					secStr="0"+secSub[0];
				}
				else{
					String secSub[] = String.valueOf(sec).split("\\.");
					secStr=secSub[0];
				}
				
				// 拼接得到当前播放时间，用于UI界面显示
				timePosition = timeSub[0] + ":" + secStr;
				// seekChange改回false
				seekChange = false;
				try {
					// 等1s发送消息,即每秒刷新时间进度
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 发送相应消息给handler
				handler.sendEmptyMessage(1);
			}

			// 如果歌曲播放模式改变，发送消息给handler，modeChange改回false
			if (modeChange) {
				handler.sendEmptyMessage(2);
				modeChange = false;
			}
		}
	}

	public static void playerSeekToTime(int porgress,int state){
		if(state==PlayerConstant.STATE_PLAY||state==PlayerConstant.STATE_CONTINUE)
		    playerHelper.seekToMusicAndPlay(porgress);
		else
			playerHelper.seekToMusic(porgress);
	}
	
	@SuppressWarnings("deprecation")
	private void showNotification(String title, String artist, String tickerText) {
		// 1、得到通知管理器
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// 2、设置通知的点击事件 (点击通知后，跳转到当前正在运行的界面)
		Intent intent = new Intent(PlayerReceiver.ACTION_BACKUP_ZERO);
		PendingIntent contentIntent = PendingIntent.getBroadcast(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 3、自定义的通知视图
		RemoteViews remoteViews = new RemoteViews(getPackageName(),
				R.layout.notification);
		remoteViews.setTextViewText(R.id.notifi_artist, artist);
		remoteViews.setTextViewText(R.id.notifi_song, title);

		// 4、构建通知
		// API 11添加的方法
		Notification.Builder builder = new Notification.Builder(this)
				.setContent(remoteViews).setAutoCancel(false)
				.setTicker(tickerText + title).setWhen(0)
				.setSmallIcon(R.drawable.ic_launcher_26).setOngoing(true)
				.setContentIntent(contentIntent);

		// notification = builder.build(); // API 16添加创建notification的方法
		// （为了兼容4.0以上版本，放弃使用该方法）
		notification = builder.getNotification(); // API 11添加的方法

		// 5、发送通知
		notificationManager.notify(NOTIFI_ID, notification);
		// 6、修改通知已发送的标识
		NOTIFI_FLAG=true;
		LogHelper.logD("-------------已经发送啦");
	}

	/**
	 * 向service注册一个监听器，用于监听播放状态的改变
	 * 
	 * @param listener
	 *            监听器对象
	 */
	public static void registerStateChangeListener(
			OnPlayerStateChangeListener listener) {
		listener.onStateChange(state, mode, serviceMusicList, servicePosition);
		stateListenerList.add(listener);
	}
	
	/**
	 * 解除之前注册的播放状态改变监听器
	 * @param listener 监听器对象
	 */
	public static void removeStateChangeListener(OnPlayerStateChangeListener listener){
		stateListenerList.remove(listener);
	}
	
	/**
	 * 向service注册一个监听器，用于播放模式的改变
	 * 
	 * @param listener
	 *            监听器对象
	 */
	public static void registerModeChangeListener(
			OnModeChangeListener modeListener){
		modeListener.onModeChange(mode);
		modeListenerList.add(modeListener);
	}
	
	/**
	 * 解除之前注册的播放模式改变监听器
	 * @param listener 监听器对象
	 */
	public static void removeModeChangeListener(OnModeChangeListener modeListener){
		modeListenerList.remove(modeListener);
	}
	
	/**
	 * 向service注册一个监听器，用于进度条的改变
	 * @param seekListener 监听器对象
	 */
	public static void registerSeekChangeListener(OnSeekChangeListener seekListener) {
		seekListener.onSeekChange(progress, max, timePosition, duration);
		seekListenerList.add(seekListener);
	}
	
	/**
	 * 解除之前注册的进度条监听器
	 * @param listener 监听器对象
	 */
	public static void removeSeekChangeListener(OnSeekChangeListener seekListener){
		seekListenerList.remove(seekListener);
	}
		
	public static void addBufferingUpdateListener(OnBufferingUpdateListener listener){
		synchronized (listener) {
			boolean isExist=false;
			for(WeakReference<OnBufferingUpdateListener> refListener:bufferingUpdateListenerList){
				if(refListener.get()!=null&&refListener.get()==listener){
					isExist=true;
					break;
				}
			}
			if(!isExist){
				bufferingUpdateListenerList.add(new WeakReference<OnBufferingUpdateListener>(listener));
			}
		}
	}
	
	public static void removeBufferingUpdateListener(OnBufferingUpdateListener listener){
		synchronized (listener) {
			for(WeakReference<OnBufferingUpdateListener> refListener: bufferingUpdateListenerList){
				if(refListener.get()!=null&&refListener.get()==listener){
					bufferingUpdateListenerList.remove(refListener);
					break;
				}
			}
		}
	}
	
	public static void notifyBufferingUpdateListener(int percent){
		synchronized (bufferingUpdateListenerList) {
			List<WeakReference<OnBufferingUpdateListener>> emptyList=new ArrayList<WeakReference<OnBufferingUpdateListener>>();
			
			for(WeakReference<OnBufferingUpdateListener> refListener: bufferingUpdateListenerList){
				OnBufferingUpdateListener listener=refListener.get();
				if(listener!=null){
					listener.onBufferingUpdate(percent);
				}else{
					emptyList.add(refListener);
				}
			}
			
			if(emptyList.size()>0){
				bufferingUpdateListenerList.remove(emptyList);
			}
		}
	}

	@Override
	public void settingsChanged(String type, Object newVal) {
		// TODO Auto-generated method stub
		if (AppConstant.SETTING_HEADSET_OPEN.equals(type)) {
			if(newVal instanceof Boolean){
				boolean headSetFlag=(Boolean) newVal;
				
				if(headSetFlag){
					if(headSetStateListener==null){
						headSetStateListener=new IHeadsetState() {
							
							@Override
							public void headsetPull(String name, boolean microphone) {
								// TODO Auto-generated method stub
								if(playerHelper.isPlaying()){
									Intent intent=new Intent(PlayerReceiver.ACTION_PLAY_BUTTON);
									sendBroadcast(intent);
								}
							}
							
							@Override
							public void headsetPlug(String name, boolean microphone) {	}
						};
					}
										
					HeadsetUtil.registHeadsetStateListener(this, headSetStateListener);
				}
				else{
					if(headSetStateListener!=null){
						HeadsetUtil.unregistHeadsetStateListener(this,headSetStateListener);
					}
				}
			}
		}
	}
}
