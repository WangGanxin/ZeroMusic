package com.ganxin.zeromusic.module.home.local;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ganxin.zeromusic.application.DbConstant;
import com.ganxin.zeromusic.common.bean.MusicBean;
import com.ganxin.zeromusic.common.db.MusicDBHelper;
import com.ganxin.zeromusic.common.util.ToastHelper;
import com.ganxin.zeromusic.view.R;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 
 * @Description 本地音乐的自定义适配器
 * @author ganxin
 * @date Sep 23, 2014
 * @email ganxinvip@163.com
 */
public class LocalMusicListAdapter extends BaseAdapter{

	//上下文
	private Context context;
	// 数据源
	private Cursor cursor;
	// 用于判断checkbox
	private List<Boolean> popCheckStatus;
	// 正在播放的歌曲名称
	private String playMusicTitle="";
	// 正在播放的歌曲演唱者
	private String playMusicArtist="";
	
	private class ViewHolder {
		TextView title;
		TextView artist;
		CheckBox popCheck;
		LinearLayout popdown;
		Button favor, detail, ringtone, delete;
		ImageView indicator;
	}
	
	/**
	 * 构造方法
	 * @param context  上下文
	 * @param cursor 数据源
	 */
	public LocalMusicListAdapter(Context context, Cursor cursor) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.cursor = cursor;		
		
		if (cursor != null) {
			popCheckStatus = new ArrayList<Boolean>();
			for (int i = 0; i < cursor.getCount(); i++) {
				popCheckStatus.add(false);
			}
		}		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return cursor.moveToPosition(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		
		if(convertView==null){
			holder = new ViewHolder();
			
			convertView = LayoutInflater.from(context).inflate(
					R.layout.local_music_listview_item, null);
			
			holder.title = (TextView) convertView
					.findViewById(R.id.localmusic_listitem_song_text);
			holder.artist = (TextView) convertView
					.findViewById(R.id.localmusic_listitem_artist_text);
			holder.popCheck = (CheckBox) convertView
					.findViewById(R.id.localmusic_listitem_action);
			holder.popdown = (LinearLayout) convertView
					.findViewById(R.id.local_popdown);
			holder.favor = (Button) convertView
					.findViewById(R.id.local_popdown_favor);
			holder.detail = (Button) convertView
					.findViewById(R.id.local_popdown_detail);
			holder.ringtone = (Button) convertView
					.findViewById(R.id.local_popdown_ringtone);
			holder.delete = (Button) convertView
					.findViewById(R.id.local_popdown_del);
			holder.indicator = (ImageView) convertView
					.findViewById(R.id.localmusic_listitem_indicator);
			convertView.setTag(holder);			
			
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		cursor.moveToPosition(position);
		
        String musicTitle=cursor.getString(cursor
				.getColumnIndex(DbConstant.LOCAL_TITLE));
        String musicArtist=cursor.getString(cursor
				.getColumnIndex(DbConstant.LOCAL_ARTIST));
        
		holder.title.setText(musicTitle);
		holder.artist.setText(musicArtist);
		holder.popdown.setVisibility(View.GONE);
		
		if (musicTitle.equals(playMusicTitle) && musicArtist.equals(playMusicArtist)) {
			holder.indicator.setVisibility(View.VISIBLE);
		} else {
			holder.indicator.setVisibility(View.INVISIBLE);
		}
		
		if (popCheckStatus.get(position)) {
			holder.popdown.setVisibility(View.VISIBLE);
		} 
		else{
			holder.popdown.setVisibility(View.GONE);
		}		
		
		
		holder.popCheck.setTag(position);
		holder.favor.setTag(position);
		holder.detail.setTag(position);
		holder.ringtone.setTag(position);
		holder.delete.setTag(position);
		holder.popCheck.setChecked(popCheckStatus.get(position));
		
		// ckeckbox为选中时，显示popdown
		holder.popCheck.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				popCheckStatus.set((Integer) buttonView.getTag(),
						isChecked);
				
				if (isChecked){
					holder.popdown.setVisibility(View.VISIBLE);
				}
				else{
					holder.popdown.setVisibility(View.GONE);
				}			
			}			
		});
		
		// 设置我喜欢按钮点击事件
		holder.favor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cursor.moveToPosition((Integer) v.getTag());
				MusicBean music = new MusicBean();
				music.setId(cursor.getInt(cursor.getColumnIndex(DbConstant.LOCAL_ID)));
				MusicDBHelper dbHelper = new MusicDBHelper(context);
				// 插入我喜欢列表的是本地音乐列表的id
				Long i = dbHelper.insertFav(music);
				if(i>0)
					ToastHelper.show(context,R.string.favor_add_success);
				else
					ToastHelper.show(context,R.string.favor_add_repeat);			
				//dbHelper.close();
			}			
		});
		
		//设置歌曲信息显示按钮
		holder.detail.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MusicDBHelper dbHelper = new MusicDBHelper(context);
				ArrayList<MusicBean> musicList=dbHelper
						.getMusicListFromCursor(cursor);
				MusicBean music = musicList.get((Integer) v.getTag());
				String title = music.getTitle();
				String artist = music.getArtist();
				String size = music.getSize() / 1024.0f / 1024.0f + "";
				       size = size.substring(0, 3) + "M";
				       
				long l = music.getDuration();
				float longF; 
				if(l<1000){
					 longF = (float) l / 60.0f;
				}
				else{
					 longF = (float) l / 1000.0f / 60.0f;
				}
				
				String longStr = Float.toString(longF);
				String dur[] = longStr.split("\\.");
				float sec = Float.parseFloat("0." + dur[1]) * 60.0f;
				String secStr = "";
				
				if (sec < 10.0) {
					String secSub[] = String.valueOf(sec).split("\\.");
					secStr = "0" + secSub[0];
				} else {
					String secSub[] = String.valueOf(sec).split("\\.");
					secStr = secSub[0].substring(0, 2);
				}								
				String duration = dur[0] + ":" + secStr;
												
				String path = music.getPath();
				ToastHelper.show(context,"歌名: "+title+"\n"+
				                       "歌手: "+artist+"\n"+
						               "大小: "+size+"\n"+
				                       "时长: "+duration+"\n"+
						               "路径: "+path);
			}			
		});
		
		//设置铃声按钮监听事件
		holder.ringtone.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int position= (Integer) v.getTag();
				MusicDBHelper helper=new MusicDBHelper(context);
				ArrayList<MusicBean> musicList=helper.getMusicListFromCursor(cursor);
				MusicBean music=musicList.get(position);
				
				String path=music.getPath();
				File musicFile=new File(path);
				
				setMyRingtone(musicFile);				
			}
		});
		
		//删除按钮，弹出对话框
		holder.delete.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int position= (Integer) v.getTag();
				delDialog(position);
			}
		});
		
		return convertView;
	}
	
	/**
	 * 设置手机铃声
	 * @param musicFile 音乐文件
	 */
	private void setMyRingtone(File musicFile){
		ContentValues values = new ContentValues();
		
		values.put(MediaStore.MediaColumns.DATA, musicFile.getAbsolutePath());
		values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
		values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
		values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
		values.put(MediaStore.Audio.Media.IS_ALARM, false);
		values.put(MediaStore.Audio.Media.IS_MUSIC, false);
		
		Uri uri = MediaStore.Audio.Media.getContentUriForPath(musicFile
				.getAbsolutePath());
		Uri newUri = context.getContentResolver().insert(uri, values);
		
		RingtoneManager.setActualDefaultRingtoneUri(context,
				RingtoneManager.TYPE_RINGTONE, newUri);
		ToastHelper.show(context,R.string.ringtone_add_success);
	}
	
	/**
	 * 删除对话框
	 * @param position 要删除歌曲的位置
	 */
	private void delDialog(int position){
		 final int p=position;
		 final Dialog customDialog= new Dialog(context,R.style.customDialog);
		 customDialog.setContentView(R.layout.dialog_del_music);
		 customDialog.setCanceledOnTouchOutside(false);

		 //以下更改对话框的大小
		 Window dialogWindow = customDialog.getWindow();
		 WindowManager winManager = (WindowManager) context.getSystemService("window");
		 Display display = winManager.getDefaultDisplay(); //获取屏幕的宽、高
		 Point size = new Point();
		 display.getSize(size);
		 WindowManager.LayoutParams lp = dialogWindow.getAttributes(); //获取对话框当前的参数值
		 lp.width = (int) (size.x * 0.9); //宽度设置为屏幕的0.9
		 dialogWindow.setAttributes(lp);
		 
		 Button btnConfirm=(Button) dialogWindow.findViewById(R.id.dialog_btn_confirm);
		 Button btnCancel=(Button) dialogWindow.findViewById(R.id.dialog_btn_cancel);
		 final CheckBox option=(CheckBox) dialogWindow.findViewById(R.id.diaolg_other_option);
		 
		 //确认按钮
		 btnConfirm.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MusicDBHelper dBHelper = new MusicDBHelper(context);
				cursor.moveToPosition(p);
				int musicId=cursor.getInt(cursor.
						getColumnIndex(DbConstant.LOCAL_ID));
				
				String artist=cursor.getString(cursor.getColumnIndex(DbConstant.LOCAL_ARTIST));
				
				String musicPath=cursor.getString(cursor.
						getColumnIndex(DbConstant.LOCAL_PATH));
				String lrcPath=musicPath.replace(".mp3", ".lrc");
				
				//同时删除文件
				if(option.isChecked()){					
					int i= dBHelper.delLocal(musicId);
					       dBHelper.delFav(musicId);
					       dBHelper.delArtist(artist);
					       dBHelper.delDownloadFromLocalID(musicId);
					File musicFile=new File(musicPath);
					File lrcFile=new File(lrcPath);
					boolean musicFlag=false;
					boolean lrcFlag=false;
					if(musicFile.isFile()||lrcFile.isFile()){
						musicFlag=musicFile.delete();
						lrcFlag=lrcFile.delete();
					}
                    
					customDialog.dismiss();
					if(i>0&&(musicFlag||lrcFlag))
						ToastHelper.show(context,R.string.del_music_success);
					else
						ToastHelper.show(context,R.string.del_music_failed);
						
				}
				//只删除本地数据库
				else{
					customDialog.dismiss();
					int i= dBHelper.delLocal(musicId);
					       dBHelper.delFav(musicId);
					       dBHelper.delArtist(artist);
					       dBHelper.delDownloadFromLocalID(musicId);
					if(i>0)
						ToastHelper.show(context,R.string.del_music_success);						
					else
						ToastHelper.show(context,R.string.del_music_failed);										
				}
				
				//以下操作重新刷新listview
				cursor = dBHelper.queryLocalMusicByID();
				if (cursor != null) {
					popCheckStatus = new ArrayList<Boolean>();
					for (int j = 0; j < cursor.getCount(); j++) {
						popCheckStatus.add(false);
					}
				}
				notifyDataSetChanged();
			}
		});
		 
		 //取消按钮
		 btnCancel.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				customDialog.dismiss();
			}
		});		 
		 
		customDialog.show();
	}

	public Cursor getCursor() {
		return cursor;
	}

	public void setPlayMusicTitle(String playMusicTitle) {
		this.playMusicTitle = playMusicTitle;
	}

	public void setPlayMusicArtist(String playMusicArtist) {
		this.playMusicArtist = playMusicArtist;
	}
		
}
