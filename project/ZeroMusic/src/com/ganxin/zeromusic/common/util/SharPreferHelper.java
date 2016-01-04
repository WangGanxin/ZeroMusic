package com.ganxin.zeromusic.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.ganxin.zeromusic.application.AppConstant;
import com.ganxin.zeromusic.application.ZeroMusicApplication;
import com.ganxin.zeromusic.common.listener.OnSharedataCommitListener;

/**
 * 
 * @Description SharPreference工具类
 * @author ganxin
 * @date 2014-12-8
 * @email ganxinvip@163.com
 */
public class SharPreferHelper {
	/**
	 * 写入布尔型数据
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public static void writeBoolean(Context context, String key, Boolean value) {
		SharedPreferences preference = context.getSharedPreferences(
				AppConstant.SHARPREFER_FILENAME, Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * 读取布尔型数据
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            键
	 * @param defValue
	 *            默认的值，如果不存在则以此值返回
	 * @return boolean 布尔数据
	 */
	public static boolean readBoolean(Context context, String key,
			boolean defValue) {
		SharedPreferences preference = context.getSharedPreferences(
				AppConstant.SHARPREFER_FILENAME, Context.MODE_PRIVATE);
		return preference.getBoolean(key, defValue);
	}

	/**
	 * 写入整型数据
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public static void wirteInt(Context context, String key, int value) {
		SharedPreferences preference = context.getSharedPreferences(
				AppConstant.SHARPREFER_FILENAME, Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putInt(key, value);
		editor.commit();
	}
		
	/**
	 * 读取整型数据
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            键
	 * @param defValue
	 *            默认值
	 * @return int 整型数据
	 */
	public static int readInt(Context context, String key, int defValue) {
		SharedPreferences preference = context.getSharedPreferences(
				AppConstant.SHARPREFER_FILENAME, Context.MODE_PRIVATE);
		return preference.getInt(key, defValue);
	}

	//-----------------------------added 2015.10.31---------------------	
	//改造该帮助类，使其具有更好的安全性与扩展性
	
	/**
	 * 
	 * @Description
	 * @author ganxin
	 * @date Oct 31, 2015
	 * @email ganxinvip@163.com
	 */
	private static class SetConfigRunnable implements Runnable{
        private Context context;
        private String configName;
        private String configKey;
        private Object configValue;
        private OnSharedataCommitListener commit;
        
        public SetConfigRunnable(Context context, String configName, String configKey, Object configValue,OnSharedataCommitListener commit) {
            this.configKey = configKey;
            this.configName = configName;
            this.configValue = configValue;
            this.context = context;
            this.commit=commit;
        }

        @Override
        public void run() {
            if (context == null)
                context = ZeroMusicApplication.getInstence();
            if (TextUtils.isEmpty(configName) || TextUtils.isEmpty(configKey)) {
                return;
            }

            SharedPreferences.Editor sharedata = context.getSharedPreferences(configName, 0).edit();
            if (configValue instanceof Integer) {
                sharedata.putInt(configKey, (Integer) configValue);
            } else if (configValue instanceof String) {
                sharedata.putString(configKey, (String) configValue);
            } else if (configValue instanceof Boolean) {
                sharedata.putBoolean(configKey, (Boolean) configValue);
            } else if (configValue instanceof Float) {
                sharedata.putFloat(configKey, (Float) configValue);
            } else if (configValue instanceof Long) {
                sharedata.putLong(configKey, (Long) configValue);
            }

            try {
                sharedata.commit();
                if(this.commit!=null){
                	this.commit.onSharedataCommit(configKey,configValue);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }	
	}
	
	public static void setConfig(Context context, String configName, String configKey, Object configValue) {
		setConfig(context, configName, configKey, configValue,null);
    }
    public static void setConfig(Context context, String configName, String configKey, Object configValue,OnSharedataCommitListener commit) {
        new Thread(new SetConfigRunnable(context, configName, configKey, configValue,commit)).start();
    }
    
    public static void removeConfig(Context context, String cfgName, String cfgKey) {
    	if (context == null){
    		context = ZeroMusicApplication.getInstence().getApplicationContext();
    	}
    	try {
    		SharedPreferences shareData = context.getSharedPreferences(cfgName, 0);
    		shareData.edit().remove(cfgKey);
    		shareData.edit().commit();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static int getIntConfig(Context context, String cfgName, String cfgKey, int defVal) {
        if (context == null)
            context = ZeroMusicApplication.getInstence();
        SharedPreferences shareData = context.getSharedPreferences(cfgName,Context.MODE_PRIVATE);
        return shareData.getInt(cfgKey, defVal);
    }

    public static boolean getBooleanConfig(Context context, String cfgName, String cfgKey, boolean defVal) {
        if (context == null)
            context = ZeroMusicApplication.getInstence();
        SharedPreferences shareData = context.getSharedPreferences(cfgName,Context.MODE_PRIVATE);
        return shareData.getBoolean(cfgKey, defVal);
    }

    public static long getLongConfig(Context context, String cfgName, String cfgKey, Long defVal) {
        if (context == null)
            context = ZeroMusicApplication.getInstence();
        SharedPreferences shareData = context.getSharedPreferences(cfgName,Context.MODE_PRIVATE);
        return shareData.getLong(cfgKey, defVal);
    }

    public static String getStringConfig(Context context, String cfgName, String cfgKey, String defVal) {
        if (context == null)
            context = ZeroMusicApplication.getInstence();
        SharedPreferences shareData = context.getSharedPreferences(cfgName,Context.MODE_PRIVATE);
        return shareData.getString(cfgKey, defVal);
    }
}
