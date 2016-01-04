package com.ganxin.zeromusic.module.home.download;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ganxin.zeromusic.common.bean.DownloadInfo;
import com.ganxin.zeromusic.common.db.MusicDBHelper;
import com.ganxin.zeromusic.common.manager.DownloadManager;
import com.ganxin.zeromusic.common.util.ToastHelper;
import com.ganxin.zeromusic.framework.BaseActivity;
import com.ganxin.zeromusic.module.home.download.doing.DoingFragment;
import com.ganxin.zeromusic.module.home.download.done.DoneFragment;
import com.ganxin.zeromusic.service.DownloadService;
import com.ganxin.zeromusic.view.R;
import com.lidroid.xutils.exception.DbException;

/**
 * 
 * @Description 下载界面
 * @author ganxin
 * @date Mar 13, 2015
 * @email ganxinvip@163.com
 */
public class DownloadActivity extends BaseActivity {
	// 界面标题栏的标题
	private TextView title;
	// 标题栏的返回按钮、删除按钮
	private ImageButton backBtn, delBtn;

	private ViewPager viewpager;
	private FragmentPagerAdapter mAdapter;
	private DoneFragment doneFragment;
	private DoingFragment doingFragment;
	
	private List<Fragment> fragmentList;
	// 已下载和正在下载的文字控件
	private TextView doneTv, doingTv;
	private ImageView tabLine;
	// 屏幕宽度
	private int screenWidth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_download);
		initUI();
		initData();
	}

	private void initUI() {
		// TODO Auto-generated method stub
		backBtn = (ImageButton) findViewById(R.id.home_download_actionbar_back);
		delBtn = (ImageButton) findViewById(R.id.home_download_actionbar_del);
		title = (TextView) findViewById(R.id.home_download_actionbar_title);

		backBtn.setOnClickListener(this);
		delBtn.setImageResource(R.drawable.actionbar_del);
		title.setText(R.string.download_manager);

		viewpager = (ViewPager) findViewById(R.id.home_download_viewpager);
		doneTv = (TextView) findViewById(R.id.home_download_tab_done_text);
		doingTv = (TextView) findViewById(R.id.home_download_tab_doing_text);
		tabLine = (ImageView) findViewById(R.id.home_download_tab_line);

		doneTv.setOnClickListener(this);
		doingTv.setOnClickListener(this);
		delBtn.setOnClickListener(this);
	}

	private void initData() {
		// TODO Auto-generated method stub
		fragmentList = new ArrayList<Fragment>();
		doneFragment = new DoneFragment();
		doingFragment = new DoingFragment();

		fragmentList.add(doneFragment);
		fragmentList.add(doingFragment);

		Display display = getWindow().getWindowManager().getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		screenWidth = (int) (dm.widthPixels / 2);

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return fragmentList.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				// TODO Auto-generated method stub
				return fragmentList.get(arg0);
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				// TODO Auto-generated method stub
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabLine
						.getLayoutParams();
				lp.width = screenWidth;
				tabLine.setLayoutParams(lp);
				return super.instantiateItem(container, position);
			}

		};

		viewpager.setAdapter(mAdapter);
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				resetFontColor();

				switch (position) {
				case 0:
					doneTv.setTextColor(getResources().getColor(R.color.green));
					break;
				case 1:
					doingTv.setTextColor(getResources().getColor(R.color.green));
					break;
				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// TODO Auto-generated method stub
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabLine
						.getLayoutParams();
				lp.leftMargin = position * screenWidth
						+ (int) (positionOffset * screenWidth);
				lp.width = screenWidth;
				tabLine.setLayoutParams(lp);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * 重置tab字体的颜色
	 */
	private void resetFontColor() {
		doneTv.setTextColor(getResources().getColor(R.color.black));
		doingTv.setTextColor(getResources().getColor(R.color.black));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.home_download_actionbar_back:
			this.finish();
			break;
		case R.id.home_download_actionbar_del:
			if (viewpager != null) {
				if (viewpager.getCurrentItem() == 0) {
					showReslutDialog(this,0);
				} else if (viewpager.getCurrentItem() == 1) {
					showReslutDialog(this,1);
				}
			}
			break;
		case R.id.home_download_tab_done_text:
			viewpager.setCurrentItem(0);
			doneTv.setTextColor(getResources().getColor(R.color.green));
			break;
		case R.id.home_download_tab_doing_text:
			viewpager.setCurrentItem(1);
			doingTv.setTextColor(getResources().getColor(R.color.green));
			break;
		default:
			break;
		}
	}

	/**
	 * 删除对话框
	 * 
	 * @param type
	 *            0-删除下载的歌曲；1-删除正在下载的任务
	 */
	private void showReslutDialog(final Context ct,int type) {
		final Dialog resultDialog = new Dialog(this, R.style.loadingDialog);
		resultDialog.setContentView(R.layout.dialog_select_music);
		resultDialog.setCanceledOnTouchOutside(true);

		// 以下更改对话框的大小
		Window dialogWindow = resultDialog.getWindow();
		WindowManager winManager = (WindowManager) this
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

		//删除歌曲
		if(type==0){
			// 设置标题
			title.setText(R.string.del_music_title_tip);
			content.setText(R.string.del_music_content_delall_download_music);

			// 设置监听
			leftLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub					
					MusicDBHelper dbHelper = new MusicDBHelper(ct);
					Cursor cursor=dbHelper.queryDownloadByID();
					if(cursor!=null&&cursor.getCount()>0){
						dbHelper.removewDownloadTable();
						doneFragment.notifiyDataChange();
						ToastHelper.show(ct, R.string.del_music_content_delall_success);
					}
					else{
						ToastHelper.show(ct, R.string.del_music_content_delall_download_no_music);
					}
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
		}
		else { //删除下载任务
			// 设置标题
			title.setText(R.string.del_music_title_tip);
			content.setText(R.string.del_music_content_delall_download_task);

			// 设置监听
			leftLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					DownloadManager downloadManager=DownloadService.getDownloadManager(ct);
					List<DownloadInfo> list=downloadManager.getDownloadInfoList();
					if(list!=null&&list.size()>0){
						for(int i=0;i<list.size();i++){
							try {
								downloadManager.removeDownload(list.get(i));
							} catch (DbException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						ToastHelper.show(ct,R.string.del_music_content_delall_success);
					}
					else{
						ToastHelper.show(ct,R.string.del_music_content_delall_download_no_task);
					}					
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
		}

		resultDialog.show();
	}
}
