package com.ganxin.zeromusic.module.home.download.doing;

import com.ganxin.zeromusic.common.manager.DownloadManager;
import com.ganxin.zeromusic.service.DownloadService;
import com.ganxin.zeromusic.view.R;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * 
 * @Description 正在下载界面
 * @author ganxin
 * @date Apr 12, 2015
 * @email ganxinvip@163.com
 */
public class DoingFragment extends Fragment{
	
	private View parentView;
	private ListView mListView;
	private DoingListAdapter madapter;
	private DownloadManager downloadManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		parentView = inflater.inflate(R.layout.home_download_doing, container, false);
		initUI();
        initData();
		return parentView;
	}

	private void initUI() {
		// TODO Auto-generated method stub
		mListView=(ListView) parentView.findViewById(R.id.doing_listview);
	}

	private void initData() {
		// TODO Auto-generated method stub
		downloadManager=DownloadService.getDownloadManager(getActivity());
		madapter = new DoingListAdapter(getActivity());
		mListView.setAdapter(madapter);
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		madapter.notifyDataSetChanged();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
        try {
            if (madapter != null && downloadManager != null) {
                downloadManager.backupDownloadInfoList();
            }
        } catch (DbException e) {
            LogUtils.e(e.getMessage(), e);
        }
		super.onDestroy();
	}
}
