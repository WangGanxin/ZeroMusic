package com.ganxin.zeromusic.module.search;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.ganxin.zeromusic.application.AppConstant;
import com.ganxin.zeromusic.application.PlayerConstant;
import com.ganxin.zeromusic.common.bean.DownWebBean;
import com.ganxin.zeromusic.common.bean.DownWebBean.BitRate;
import com.ganxin.zeromusic.common.bean.DownWebBean.SongInfo;
import com.ganxin.zeromusic.common.bean.GetSongListBean.songlist;
import com.ganxin.zeromusic.common.bean.MusicBean;
import com.ganxin.zeromusic.common.http.volleyHelper.HttpAPI;
import com.ganxin.zeromusic.common.http.volleyHelper.RequestParams;
import com.ganxin.zeromusic.common.manager.DownloadManager;
import com.ganxin.zeromusic.common.util.FileHelper;
import com.ganxin.zeromusic.common.util.NetWorkHelper;
import com.ganxin.zeromusic.common.util.ToastHelper;
import com.ganxin.zeromusic.receiver.PlayerReceiver;
import com.ganxin.zeromusic.service.DownloadService;
import com.ganxin.zeromusic.view.R;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;

/**
 * 
 * @Description 根据歌名搜索歌曲返回结果的适配器
 * @author ganxin
 * @date Apr 11, 2015
 * @email ganxinvip@163.com
 */
public class SearchResultSongListAdapter  extends BaseAdapter{

	private Context context; //上下文
	private ArrayList<songlist> list; //数据源
	private String keyword;  //关键字
	private Dialog dialog;
	private DownloadManager downloadManager;
	
	public SearchResultSongListAdapter(Context context,ArrayList<songlist> list,String keyword){
		this.context=context;
		this.list=list;
		this.keyword=keyword;
		this.downloadManager=DownloadService.getDownloadManager(context);
	}
		
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.search_music_listview_item,null);
			
			viewHolder.musicName=(TextView) convertView.findViewById(R.id.search_song_listview_item_song);
			viewHolder.musicArtist=(TextView) convertView.findViewById(R.id.search_song_listview_item_artist);
			viewHolder.downloadBtn=(ImageButton) convertView.findViewById(R.id.search_song_listview_item_download);
			viewHolder.layout=(RelativeLayout) convertView.findViewById(R.id.search_song_listview_item_layout);
			
			convertView.setTag(viewHolder);
			
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		
		//设置数据
		String author=list.get(position).getAuthor();
		
		if(author.contains(keyword)){
			SpannableString styledText = new SpannableString(author); 
			
			styledText.setSpan(new TextAppearanceSpan(context, R.style.keywordStyle),
					author.indexOf(keyword),author.indexOf(keyword)+keyword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
			
			viewHolder.musicArtist.setText(styledText,TextView.BufferType.SPANNABLE);
		}
		else {
			viewHolder.musicArtist.setText(author);
		}
		
		viewHolder.musicName.setText(list.get(position).getTitle());
		
		//下载按钮监听
		viewHolder.downloadBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (NetWorkHelper.isConnected(context)) {
					showLoadingDialog();
					HashMap<String, String> params = RequestParams
							.downMusic(Integer.valueOf(list.get(position).getSong_id()));
					HttpAPI.createAndStartGetMusicRequest(params,
							DownWebBean.class, new Listener<DownWebBean>() {

								@Override
								public void onResponse(DownWebBean arg0) {
									// TODO Auto-generated method stub
									closeLoadingDialog();
									if (arg0 != null
											&& arg0.getBitrate() != null
											&& arg0.getBitrate().size() > 0) {
										BitRate bitRate = getBetterBitRate(arg0
												.getBitrate());
										SongInfo info = arg0.getSonginfo();
										showReslutDialog(bitRate, info);
									} else {
										ToastHelper.show(context,
												R.string.get_downmusic_error);
									}
								}

							}, new ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError arg0) {
									// TODO Auto-generated method stub
									closeLoadingDialog();
									ToastHelper.show(context,
											R.string.get_downmusic_error);
								}

							});
				} else {
					ToastHelper
							.show(context, R.string.network_is_not_connected);
				}
			}
		});
		
		//item点击监听
		viewHolder.layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (NetWorkHelper.isConnected(context)) {
					showLoadingDialog();
					HashMap<String, String> params = RequestParams
							.downMusic(Integer.valueOf(list.get(position).getSong_id()));
					HttpAPI.createAndStartGetMusicRequest(params,
							DownWebBean.class, new Listener<DownWebBean>() {

								@Override
								public void onResponse(DownWebBean arg0) {
									// TODO Auto-generated method stub
									closeLoadingDialog();
									if (arg0 != null
											&& arg0.getBitrate() != null
											&& arg0.getBitrate().size() > 0) {
										BitRate bitRate = getBetterBitRate(arg0
												.getBitrate());
										SongInfo info = arg0.getSonginfo();
										
										playOnlineMusic(bitRate,info);
									} else {
										ToastHelper.show(context,
												R.string.get_downmusic_error);
									}
								}

							}, new ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError arg0) {
									// TODO Auto-generated method stub
									closeLoadingDialog();
									ToastHelper.show(context,
											R.string.get_downmusic_error);
								}

							});
				} else {
					ToastHelper
							.show(context, R.string.network_is_not_connected);
				}
			}
		});
		
		return convertView;
	}
	
	/**
	 * 播放在线歌曲
	 * @param bitRate 
	 * @param info
	 */
	private void playOnlineMusic(BitRate bitRate, SongInfo info){
		if(bitRate!=null&&info!=null){
			// 点击item传一个musicList和当前点击位置给service
			ArrayList<MusicBean> musicList=new ArrayList<MusicBean>();
			MusicBean bean=new MusicBean();
			bean.setArtist(info.getAuthor());
			bean.setTitle(info.getTitle());
			bean.setDuration((long)bitRate.getFile_duration());
			bean.setSize((long)bitRate.getFile_size());
			bean.setPath(bitRate.getFile_link().length()>0?bitRate.getFile_link():bitRate.getShow_link());
			musicList.add(bean);
			
			Intent intent = new Intent();
			intent.setAction(PlayerReceiver.ACTION_PLAY_ITEM);
			intent.putParcelableArrayListExtra(PlayerConstant.PLAYER_LIST,musicList);
			intent.putExtra(PlayerConstant.PLAYER_WHERE, "online");
			intent.putExtra(PlayerConstant.PLAYER_POSITION,0);
			
			// 发送相应广播给service
			context.sendBroadcast(intent);
		}
		else{
			ToastHelper.show(context,R.string.get_downmusic_url_error);
		}

	}
	
	/**
	 * 开始下载
	 * @param bitRate
	 * @param info
	 */
	private void startDownload(BitRate bitRate, SongInfo info){
        String savePath =AppConstant.SDCARD_SAVE_PATH;
        String fileName =info.getTitle()+"."+bitRate.getFile_extension();
        String url=bitRate.getFile_link().length()>0?bitRate.getFile_link():bitRate.getShow_link();
        
        String lrcUrl=info.getLrclink();
        String lrcName="";
        String lrcStr="";
        
        if(lrcUrl.length()>0){        	
        	lrcStr=lrcUrl.substring(lrcUrl.lastIndexOf("/")+1,lrcUrl.length());
        	
			try {
				lrcName = URLDecoder.decode(lrcStr, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        if(!FileHelper.isExist(savePath)){
        	FileHelper.createFolder(savePath, FileHelper.MODE_COVER);
        }
        
        
        try {
        	//下载歌曲
            downloadManager.addNewDownload(false,url,
                    fileName,
                    savePath+fileName,                    
                    info.getAuthor(),
                    lrcName,
                    (long)bitRate.getFile_size(),
                    (long)bitRate.getFile_duration(),
                    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                    false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                    null);
            
            //下载歌词
            if(lrcUrl.length()>0){
            	downloadManager.addNewDownload(true,lrcUrl,
                        lrcName,savePath+lrcName,                    
                        null,null,null,null,
                        true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                        false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                        null);
            }
        } catch (DbException e) {
            LogUtils.e(e.getMessage(), e);
        }
	}
	
	/**
	 * 显示结果对话框
	 */
	private void showReslutDialog(final BitRate bitRate, final SongInfo info) {
		if (bitRate != null && info != null) {
			final Dialog resultDialog = new Dialog(context,
					R.style.loadingDialog);
			resultDialog.setContentView(R.layout.dialog_select_music);
			resultDialog.setCanceledOnTouchOutside(true);

			// 以下更改对话框的大小
			Window dialogWindow = resultDialog.getWindow();
			WindowManager winManager = (WindowManager) context
					.getSystemService("window");
			Display display = winManager.getDefaultDisplay(); // 获取屏幕的宽、高
			Point size = new Point();
			display.getSize(size);
			WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
			lp.width = (int) (size.x * 0.7); // 宽度设置为屏幕的0.7
			dialogWindow.setAttributes(lp);

			// 获取控件
			TextView title = (TextView) resultDialog
					.findViewById(R.id.dialog_select_music_title);
			TextView content = (TextView) resultDialog
					.findViewById(R.id.dialog_select_music_content);
			LinearLayout leftLayout = (LinearLayout) resultDialog
					.findViewById(R.id.dialog_select_music_leftLayout);
			LinearLayout rightLayout = (LinearLayout) resultDialog
					.findViewById(R.id.dialog_select_music_rightLayout);

			title.setText(R.string.get_downmusic_tips);

			float fileSize = (float) bitRate.getFile_size() / (float) 1024
					/ (float) 1024;
			DecimalFormat decimalFormat = new DecimalFormat(".00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
			content.setText(info.getTitle() + "." + bitRate.getFile_extension()
					+ "  (" + decimalFormat.format(fileSize) + "M)");

			Drawable qualityIcon=null;
			if(bitRate.getFile_bitrate()>320){
				qualityIcon=context.getResources().getDrawable(R.drawable.icon_sq);
				qualityIcon.setBounds(0, 0, qualityIcon.getMinimumWidth(), qualityIcon.getMinimumHeight());  
				content.setCompoundDrawables(qualityIcon, null,null, null); 
			}
			else if(bitRate.getFile_bitrate()>=256){
				qualityIcon=context.getResources().getDrawable(R.drawable.icon_hq);
				qualityIcon.setBounds(0, 0, qualityIcon.getMinimumWidth(), qualityIcon.getMinimumHeight());  
				content.setCompoundDrawables(qualityIcon, null,null, null); 
			} 
						
			// 设置监听
			leftLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					startDownload(bitRate,info);
					ToastHelper.show(context,R.string.dialog_select_music_start_downloading);
					resultDialog.dismiss();
				}
			});

			rightLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					resultDialog.dismiss();
				}
			});

			resultDialog.show();
		}
		else{
			ToastHelper.show(context,R.string.get_downmusic_error);
		}
	}
	
	/**
	 * 获取最合适的下载链接
	 * 
	 * @param list
	 * @return
	 */
	private BitRate getBetterBitRate(ArrayList<BitRate> list) {
		BitRate bitRate = null;
		if (list != null && list.size() > 0) {
			if (NetWorkHelper.isConnected(context)) {

				// 剔除没有下载连接的数据
				ArrayList<BitRate> collectionList = new ArrayList<DownWebBean.BitRate>();
				for (int i = 0; i < list.size(); i++) {
					String linkString = list.get(i).getFile_link();
					String showLinkString=list.get(i).getShow_link();
					if (linkString != null && (linkString.startsWith("http://")||showLinkString.startsWith("http://"))) {
						collectionList.add(list.get(i));
					}
				}

				Collections.sort(collectionList, new Comparator<BitRate>() {

					@Override
					public int compare(BitRate lhs, BitRate rhs) {
						// TODO Auto-generated method stub
						Integer firstInteger = lhs.getFile_bitrate();
						Integer secondInteger = rhs.getFile_bitrate();
						return secondInteger.compareTo(firstInteger);
					}
				});

				if (collectionList.size() == 1) {
					bitRate = collectionList.get(0);
				} else if (collectionList.size() >= 2) {
					if(NetWorkHelper.isWifi(context)){
						bitRate = collectionList.get(0);
					}
					else{
						for (int i = 0; i < collectionList.size(); i++) {
							if (collectionList.get(i).getFile_bitrate() <= 320) {
								bitRate = collectionList.get(i);
								break;
							}
						}
					}
				}
			}
		}
		return bitRate;
	}
	
	/**
	 * 显示加载进度对话框
	 */
	private void showLoadingDialog() {
		dialog = new Dialog(context, R.style.loadingDialog);
		dialog.setContentView(R.layout.dialog_loading_music);
		dialog.setCanceledOnTouchOutside(false);

		// 以下更改对话框的大小
		Window dialogWindow = dialog.getWindow();
		WindowManager winManager = (WindowManager) context
				.getSystemService("window");
		Display display = winManager.getDefaultDisplay(); // 获取屏幕的宽、高
		Point size = new Point();
		display.getSize(size);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		lp.width = (int) (size.x * 0.7); // 宽度设置为屏幕的0.7
		dialogWindow.setAttributes(lp);

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
	 * 关闭加载进度的对话框
	 */
	private void closeLoadingDialog() {
		dialog.dismiss();
	}
	
	private class ViewHolder{
		TextView musicName; 
		TextView musicArtist;
		ImageButton downloadBtn;
		RelativeLayout layout;
	}
}
