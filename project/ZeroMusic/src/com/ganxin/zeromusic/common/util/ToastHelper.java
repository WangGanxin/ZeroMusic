package com.ganxin.zeromusic.common.util;

import com.ganxin.zeromusic.view.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @Description 自定义Toast类
 * @author ganxin
 * @date Sep 23, 2014
 * @email ganxinvip@163.com
 */
public class ToastHelper extends Toast{

	//private static ToastShow toast = null;
	
	public ToastHelper(Context context) {
		super(context);
	}

	/**
	 * 重写makeText方法1
	 * @param context 上下文
	 * @param resId  字符串资源Id
	 * @param duration  显示时长
	 * @return
	 */
	public static Toast makeText(Context context,int resId, int duration) {  
        Toast result = new Toast(context);  
          
        //获取LayoutInflater对象  
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   
        //由layout文件创建一个View对象  
        View layout = inflater.inflate(R.layout.newtoast_layout, null);           
        //TextView对象   
        TextView textView = (TextView) layout.findViewById(R.id.newtoast_text);  
        
        textView.setText(resId);  
          
        result.setView(layout);  
        result.setGravity(Gravity.CENTER, 0, 0);  
        result.setDuration(duration);  
          
        return result;  
    }
	
	/**
	 * 重写makeText方法2
	 * @param context 上下文
	 * @param msg  显示的信息
	 * @param duration  显示时长
	 * @return
	 */
	public static Toast makeText(Context context,String msg, int duration) {  
        Toast result = new Toast(context);  
          
        //获取LayoutInflater对象  
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   
        //由layout文件创建一个View对象  
        View layout = inflater.inflate(R.layout.newtoast_layout, null);
             layout.setBackgroundColor(
            		 context.getResources()
            		 .getColor(R.color.fullblue));
             
        //TextView对象   
        TextView textView = (TextView) layout.findViewById(R.id.newtoast_text);  
        
        textView.setText(msg);  
          
        result.setView(layout);  
        result.setGravity(Gravity.CENTER, 0, 0);  
        result.setDuration(duration);  
          
        return result;  
    }
	
	/**
	 * 自定义的show方法1
	 * @param context  上下文
	 * @param resId  字符串资源的id
	 */
	public static void show(Context context, int resId){
		   makeText(context,resId, LENGTH_SHORT).show();;
	}
	
	/**
	 * 自定义的show方法2
	 * @param context 上下文
	 * @param msg 要显示的信息
	 */
	public static void show(Context context,String msg){
		   makeText(context,msg, LENGTH_LONG).show();;
	}
}
