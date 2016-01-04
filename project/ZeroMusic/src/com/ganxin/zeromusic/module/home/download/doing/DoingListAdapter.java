package com.ganxin.zeromusic.module.home.download.doing;

import java.io.File;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ganxin.numberprogressbar.NumberProgressBar;
import com.ganxin.zeromusic.application.ZeroMusicApplication;
import com.ganxin.zeromusic.common.bean.DownloadInfo;
import com.ganxin.zeromusic.common.bean.MusicBean;
import com.ganxin.zeromusic.common.db.MusicDBHelper;
import com.ganxin.zeromusic.common.manager.DownloadManager;
import com.ganxin.zeromusic.common.util.LogHelper;
import com.ganxin.zeromusic.common.util.ToastHelper;
import com.ganxin.zeromusic.service.DownloadService;
import com.ganxin.zeromusic.view.R;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;

/**
 * 
 * @Description 正在下载界面 适配器
 * @author ganxin
 * @date Aug 23, 2015
 * @email ganxinvip@163.com
 */
public class DoingListAdapter extends BaseAdapter{
    private  Context mContext;
    private  LayoutInflater mInflater;
    private DownloadManager downloadManager;
    
    public DoingListAdapter(Context context){
    	this.mContext=context;
    	 mInflater = LayoutInflater.from(mContext);
    	 downloadManager = DownloadService.getDownloadManager(mContext);
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return downloadManager!=null?downloadManager.getDownloadInfoListCount():0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return downloadManager!=null?downloadManager.getDownloadInfo(position):null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DownloadItemViewHolder holder;
		DownloadInfo downloadInfo = downloadManager.getDownloadInfo(position);
		LogHelper.logD("getview downloadinfo---"+downloadInfo.getDownloadUrl()+"---"+downloadInfo.getFileName());
		if(convertView==null){
			convertView = mInflater.inflate(R.layout.home_download_doing_item, null);
			
            holder = new DownloadItemViewHolder(mContext,downloadInfo);
            holder.itemLayout=(RelativeLayout) convertView.findViewById(R.id.doing_item_layout);
            holder.state=(ImageView) convertView.findViewById(R.id.doing_item_state);
            holder.title=(TextView) convertView.findViewById(R.id.doing_item_title);
            holder.status=(TextView) convertView.findViewById(R.id.doing_item_status_textview);
            holder.delLayout=(RelativeLayout) convertView.findViewById(R.id.doing_item_del_btn);
            holder.progressBar=(NumberProgressBar) convertView.findViewById(R.id.doing_item_progressbar);
            
            holder.initListener();
            
            convertView.setTag(holder);
            holder.refresh();
		}
		else{
            holder = (DownloadItemViewHolder) convertView.getTag();
            holder.update(downloadInfo);
		}
		
        HttpHandler<File> handler = downloadInfo.getHandler();
        if (handler != null) {
            RequestCallBack callBack = handler.getRequestCallBack();
            if (callBack instanceof DownloadManager.ManagerCallBack) {
                DownloadManager.ManagerCallBack managerCallBack = (DownloadManager.ManagerCallBack) callBack;
                if (managerCallBack.getBaseCallBack() == null) {
                    managerCallBack.setBaseCallBack(new DownloadRequestCallBack());
                }
            }
            callBack.setUserTag(new WeakReference<DownloadItemViewHolder>(holder));
        }
		
		return convertView;
	}

	private class DownloadItemViewHolder{
		private RelativeLayout itemLayout;
		private ImageView state;
		private TextView title;
		private TextView status;
		private NumberProgressBar progressBar;
		private RelativeLayout delLayout;

		private DownloadInfo downloadInfo;
		private MusicDBHelper dbHelper;
		
        public DownloadItemViewHolder(Context ct,DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
            dbHelper=new MusicDBHelper(ct);
        }
        
        public void initListener(){
            this.delLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					remove();
				}
			});
            
            this.itemLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					stop();
				}
			});
        }
        
        public void update(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
            refresh();
        }
        
        public void refresh() {
        	title.setText(downloadInfo.getFileName());
            if (downloadInfo.getFileLength() > 0) {            	
                progressBar.setProgress((int) (downloadInfo.getProgress() * 100 / downloadInfo.getFileLength()));
            } else {
                progressBar.setProgress(0);
            }

            HttpHandler.State downState = downloadInfo.getState();
            switch (downState) {
                case WAITING:
                	status.setText(R.string.doing_item_state_waiting);
                	status.setVisibility(View.VISIBLE);
                	progressBar.setVisibility(View.GONE);
                    state.setImageResource(R.drawable.icon_download_waitting);
                    state.clearAnimation();
                    break;
                case STARTED:
                	status.setText(R.string.doing_item_state_started);
                	status.setVisibility(View.VISIBLE);
                	progressBar.setVisibility(View.GONE);
                    state.setImageResource(R.drawable.icon_download_ruing);
                    state.clearAnimation();
                    break;
                case LOADING:
                	status.setVisibility(View.GONE);
                	progressBar.setVisibility(View.VISIBLE);
                    state.setImageResource(R.drawable.icon_download_ruing);
            		RotateAnimation anim = new RotateAnimation(0, 360,
            				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            		anim.setDuration(800);
            		anim.setInterpolator(new LinearInterpolator());
            		anim.setRepeatCount(-1);
            		state.startAnimation(anim);
                    break;
                case CANCELLED:
                	status.setText(R.string.doing_item_state_canceled);
                	status.setVisibility(View.VISIBLE);
                	progressBar.setVisibility(View.GONE);
                	state.setImageResource(R.drawable.icon_download_pause);
                	state.clearAnimation();
                    break;
                case SUCCESS:
                	status.setVisibility(View.GONE);
                	ToastHelper.makeText(ZeroMusicApplication.getContext(), downloadInfo.getFileName()+" 下载成功",Toast.LENGTH_SHORT);
                	insertDB(downloadInfo);
                	remove();
                    break;
                case FAILURE:
                	status.setText(R.string.doing_item_state_failure);
                	status.setVisibility(View.VISIBLE);
                	progressBar.setVisibility(View.GONE);
                	state.setImageResource(R.drawable.icon_download_failure);
                	state.clearAnimation();
                    break;
                default:
                    break;
            }
        }
        
        public void stop() {
            HttpHandler.State state = downloadInfo.getState();
            switch (state) {
                case WAITING:
                case STARTED:
                case LOADING:
                    try {
                        downloadManager.stopDownload(downloadInfo);
                    } catch (DbException e) {
                        LogUtils.e(e.getMessage(), e);
                    }
                    break;
                case CANCELLED:
                case FAILURE:
                    try {
                        downloadManager.resumeDownload(downloadInfo, new DownloadRequestCallBack());
                    } catch (DbException e) {
                        LogUtils.e(e.getMessage(), e);
                    }
                    notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
        
        public void remove() {
            try {
                downloadManager.removeDownload(downloadInfo);
                notifyDataSetChanged();
            } catch (DbException e) {
                LogUtils.e(e.getMessage(), e);
            }
        }
        
        private void insertDB(DownloadInfo info){
        	if(dbHelper!=null&&info!=null){
        		
        		if(!info.isLrcFile()){
            		MusicBean bean=new MusicBean();
            		String fileName=info.getFileName();
            		String title=fileName.subSequence(0, fileName.lastIndexOf(".")).toString();
            		
            		bean.setTitle(title);
            		bean.setArtist(info.getArtist());
            		bean.setDuration(info.getDuration());
            		bean.setLyric_file_name(info.getLyric_file_name());
            		bean.setPath(info.getFileSavePath());
            		bean.setSize(info.getSize());
            		
            		//插入本地歌曲表
            		Long id=dbHelper.insertLocal(bean);
            		
            		//插入下载列表
            		MusicBean downBean=new MusicBean();
            		downBean.setId( new Long(id).intValue());
            		dbHelper.insertDownLoad(downBean);
            		
            		//插入歌手表
            		MusicBean artistBean=new MusicBean();
            		artistBean.setArtist(info.getArtist());
            		dbHelper.insertArtist(artistBean);
            		
            		//通知更新
            		downloadManager.notifyDownloadFinish();
        		}
        	}
        }
	}
	
    private class DownloadRequestCallBack extends RequestCallBack<File> {
    	
        @SuppressWarnings("unchecked")
		private void refreshListItem() {
            if (userTag == null) return;
            WeakReference<DownloadItemViewHolder> tag = (WeakReference<DownloadItemViewHolder>) userTag;
            DownloadItemViewHolder holder = tag.get();
            if (holder != null) {
                holder.refresh();
            }
        }

        @Override
        public void onStart() {
            refreshListItem();
        }

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            refreshListItem();
        }

        @Override
        public void onSuccess(ResponseInfo<File> responseInfo) {
            refreshListItem();
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            refreshListItem();
        }

        @Override
        public void onCancelled() {
            refreshListItem();
        }
    }
}
