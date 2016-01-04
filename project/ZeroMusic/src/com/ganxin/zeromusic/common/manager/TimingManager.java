package com.ganxin.zeromusic.common.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.ganxin.zeromusic.application.AppConstant;
import com.ganxin.zeromusic.common.bean.TimingBean;
import com.ganxin.zeromusic.common.util.AppUtil;
import com.ganxin.zeromusic.common.util.LogHelper;
import com.ganxin.zeromusic.common.util.SharPreferHelper;
import com.ganxin.zeromusic.receiver.PlayerReceiver;

/**
 * 
 * @Description 定时管理类
 * @author ganxin
 * @date Sep 20, 2015
 * @email ganxinvip@163.com
 */
public class TimingManager {
	private static TimingManager instance;

	private WakeLock wakeLock;
	private Handler sleepTimeLastHandler;
	private static long sleepTimeLast = 0;
	private static int sleepTimeType = -1;
	
	private static String INTENT_EXTRA="extra";
	
	public static TimingManager getInstance() {
		if (instance == null) {
			instance = new TimingManager();
		}
		return instance;
	}
	
	public boolean readTimingClose(Context ctx){
		return SharPreferHelper.readBoolean(ctx,AppConstant.TIMING_CLOSE_KEY,false);
	}
	
	public int readTimingCloseTime(Context ctx){
		return SharPreferHelper.readInt(ctx,AppConstant.TIMING_CLOSE_TIME,1);
	}
	
	public void setTimingClose(Context ctx,Boolean val){
		SharPreferHelper.writeBoolean(ctx,AppConstant.TIMING_CLOSE_KEY, val);
	}
	
	public void setTimingCloseTime(Context ctx,int val){
		SharPreferHelper.wirteInt(ctx, AppConstant.TIMING_CLOSE_TIME, val);
	}
	
	public void resetTimingClose(Context ctx){
		clearSleepTime();
		SharPreferHelper.writeBoolean(ctx,AppConstant.TIMING_CLOSE_KEY,false);
		SharPreferHelper.wirteInt(ctx, AppConstant.TIMING_CLOSE_TIME,1);
	}
	
	public void setSleepTime(Context context, long time) {
		if (time > 0) {
			acquireWakeLock(context);
			sleepTimeLastUpdate();
			sleepTimeLast = time / 1000;
			sleepTimeType=0;
			sleepTimeLastHandler.sendEmptyMessage(0);
		} else {
			releaseWakeLock();
			sleepTimeLast = 0;
		}
	}
	
	private void clearSleepTime(){
		releaseWakeLock();
		sleepTimeLast = 0;
		sleepTimeType=-1;
	}
	
	private void sleepTimeLastUpdate() {
		if (sleepTimeLastHandler != null) {
			sleepTimeLastHandler.removeMessages(0);
			sleepTimeLastHandler = null;
		}
		sleepTimeLastHandler = new Handler(Looper.myLooper()) {

			@Override
			public void handleMessage(Message msg) {
				if (sleepTimeLast > 0 && sleepTimeType != -1) {
					sleepTimeLast--;
					sleepTimeLastHandler.sendEmptyMessageDelayed(0, 1000);
					if (sleepTimeLast == 0) {
						releaseWakeLock();
						AppUtil.exitAllActivity();
					}
				}
			}
		};
	}
	
	/**
	 * 获取电源管理锁，占用CPU使不休眠
	 */
	private void acquireWakeLock(Context context) {
		if (wakeLock == null) {
			PowerManager pm = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this
					.getClass().getCanonicalName());
			wakeLock.acquire();
		}
	}

	/**
	 * 释放电源管理锁，使不占用cpu
	 */
	private void releaseWakeLock() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
	}
	
	//-----------------------------定时启动-------------------------------
	public void addTimingOpen(Context ctx,TimingBean bean){
		        
        AlarmManager am = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE); 
        
        if(bean.getMonday()!=null){        	
        	PendingIntent sender=PendingIntent.getBroadcast(ctx, 0,getDayOfIntent(1,bean.getTime()), 
        			PendingIntent.FLAG_UPDATE_CURRENT);
        	
            Calendar calendar = Calendar.getInstance();
            String time []=bean.getTime().split(":");
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
            calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
            calendar.set(Calendar.MINUTE,Integer.parseInt(time[1]));
            calendar.set(Calendar.SECOND,1);
            calendar.set(Calendar.MILLISECOND, 0);
            
            am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),0,sender);
            LogHelper.logD("-------------------1");
        }
        if(bean.getTuesday()!=null){
        	PendingIntent sender=PendingIntent.getBroadcast(ctx, 0,getDayOfIntent(2,bean.getTime()), 
        			PendingIntent.FLAG_UPDATE_CURRENT);
        	
            Calendar calendar = Calendar.getInstance();
            String time []=bean.getTime().split(":");
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.TUESDAY);
            calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
            calendar.set(Calendar.MINUTE,Integer.parseInt(time[1]));
            calendar.set(Calendar.SECOND,1);
            calendar.set(Calendar.MILLISECOND, 0);

            am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),0,sender);
            LogHelper.logD("-------------------2");
        }
        if(bean.getWednesday()!=null){
        	PendingIntent sender=PendingIntent.getBroadcast(ctx, 0,getDayOfIntent(3,bean.getTime()), 
        			PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            String time []=bean.getTime().split(":");
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.WEDNESDAY);
            calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
            calendar.set(Calendar.MINUTE,Integer.parseInt(time[1]));
            calendar.set(Calendar.SECOND,1);
            calendar.set(Calendar.MILLISECOND, 0);
            
            am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),0,sender);
            LogHelper.logD("-------------------3");
        }
        if(bean.getThursday()!=null){
        	PendingIntent sender=PendingIntent.getBroadcast(ctx, 0,getDayOfIntent(4,bean.getTime()), 
        			PendingIntent.FLAG_UPDATE_CURRENT);
        	
            Calendar calendar = Calendar.getInstance();
            String time []=bean.getTime().split(":");
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.TUESDAY);
            calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
            calendar.set(Calendar.MINUTE,Integer.parseInt(time[1]));
            calendar.set(Calendar.SECOND,1);
            calendar.set(Calendar.MILLISECOND, 0);
            
            am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),0,sender);
            LogHelper.logD("-------------------4");
        }
        if(bean.getFriday()!=null){
        	PendingIntent sender=PendingIntent.getBroadcast(ctx, 0,getDayOfIntent(5,bean.getTime()), 
        			PendingIntent.FLAG_UPDATE_CURRENT);
        	
            Calendar calendar = Calendar.getInstance();
            String time []=bean.getTime().split(":");
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
            calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
            calendar.set(Calendar.MINUTE,Integer.parseInt(time[1]));
            calendar.set(Calendar.SECOND,1);
            calendar.set(Calendar.MILLISECOND, 0);
            
            am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),0,sender);
            LogHelper.logD("-------------------5");
        }
        if(bean.getSaturday()!=null){
        	PendingIntent sender=PendingIntent.getBroadcast(ctx, 0,getDayOfIntent(6,bean.getTime()), 
        			PendingIntent.FLAG_UPDATE_CURRENT);
        	
            Calendar calendar = Calendar.getInstance();
            String time []=bean.getTime().split(":");
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
            calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
            calendar.set(Calendar.MINUTE,Integer.parseInt(time[1]));
            calendar.set(Calendar.SECOND,1);
            calendar.set(Calendar.MILLISECOND, 0);
            
            am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),0,sender);
            LogHelper.logD("-------------------6");
        }
        if(bean.getSunday()!=null){
        	PendingIntent sender=PendingIntent.getBroadcast(ctx, 0,getDayOfIntent(7,bean.getTime()), 
        			PendingIntent.FLAG_UPDATE_CURRENT);
        	
            Calendar calendar = Calendar.getInstance();
            String time []=bean.getTime().split(":");

            calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
            calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
            calendar.set(Calendar.MINUTE,Integer.parseInt(time[1]));
            calendar.set(Calendar.SECOND, 1);
            calendar.set(Calendar.MILLISECOND, 0);
            
            am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),0,sender);
            LogHelper.logD("-------------------7");
        }	
	}
	
	public void cancelTimingOpen(Context ctx,TimingBean bean){
		List<PendingIntent> senderList=getPendingIntentsList(ctx, bean);
        AlarmManager am = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE); 
        
        if(senderList!=null&&senderList.size()>0){
        	for(int i=0;i<senderList.size();i++){
        		am.cancel(senderList.get(i));
        		 LogHelper.logD("cancel----------"+i);
        	}
        }
	}
	
	/**
	 * 获取定时跳转的intent
	 * @param ctx
	 * @return
	 */
	private List<Intent> getMyIntent(Context ctx,TimingBean bean){
		List<Intent> list=new ArrayList<Intent>();
		
        if(bean.getMonday()!=null){
    		Intent intent = new Intent();
    		intent.setAction(PlayerReceiver.ACTION_AUTO_START);
    		intent.putExtra(INTENT_EXTRA,"1:"+bean.getTime());
    		list.add(intent);
        }
        if(bean.getTuesday()!=null){
    		Intent intent = new Intent();
    		intent.setAction(PlayerReceiver.ACTION_AUTO_START);
    		intent.putExtra(INTENT_EXTRA,"2:"+bean.getTime());
    		list.add(intent);
        }
        if(bean.getWednesday()!=null){
    		Intent intent = new Intent();
    		intent.setAction(PlayerReceiver.ACTION_AUTO_START);
    		intent.putExtra(INTENT_EXTRA,"3:"+bean.getTime());
    		list.add(intent);
        }
        if(bean.getThursday()!=null){
    		Intent intent = new Intent();
    		intent.setAction(PlayerReceiver.ACTION_AUTO_START);
    		intent.putExtra(INTENT_EXTRA,"4:"+bean.getTime());
    		list.add(intent);
        }
        if(bean.getFriday()!=null){
    		Intent intent = new Intent();
    		intent.setAction(PlayerReceiver.ACTION_AUTO_START);
    		intent.putExtra(INTENT_EXTRA,"5:"+bean.getTime());
    		list.add(intent);
        }
        if(bean.getSaturday()!=null){
    		Intent intent = new Intent();
    		intent.setAction(PlayerReceiver.ACTION_AUTO_START);
    		intent.putExtra(INTENT_EXTRA,"6:"+bean.getTime());
    		list.add(intent);
        }
        if(bean.getSunday()!=null){
    		Intent intent = new Intent();
    		intent.setAction(PlayerReceiver.ACTION_AUTO_START);
    		intent.putExtra(INTENT_EXTRA,"7:"+bean.getTime());
    		list.add(intent);
        }
		return list;
	}
	
	private Intent getDayOfIntent(int day,String time){
		Intent intent = new Intent();
		intent.setAction(PlayerReceiver.ACTION_AUTO_START);
		intent.putExtra(INTENT_EXTRA,day+":"+time);
		return intent;
	}
	
	/**
	 * 获取pendingIntent
	 * @param ctx
	 * @param bean
	 * @return
	 */
	private List<PendingIntent> getPendingIntentsList(Context ctx,TimingBean bean){
		List<PendingIntent> pendingIntentList=new ArrayList<PendingIntent>();
		
		List<Intent> intentList=getMyIntent(ctx, bean);
		if(intentList!=null&&intentList.size()>0){
			for(int i=0;i<intentList.size();i++){
		        PendingIntent sender =PendingIntent.getBroadcast(ctx, 0,
		        		intentList.get(i), PendingIntent.FLAG_UPDATE_CURRENT);
		        pendingIntentList.add(sender);
			}
		}			
		return pendingIntentList;
	}
}
