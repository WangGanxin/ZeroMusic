package com.ganxin.zeromusic.module.search.rankinglist;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.ganxin.zeromusic.application.AppConstant;
import com.ganxin.zeromusic.common.bean.BillListBean;
import com.ganxin.zeromusic.common.bean.BillListBean.billboard;
import com.ganxin.zeromusic.common.bean.BillListBean.song_list;
import com.ganxin.zeromusic.common.http.volleyHelper.HttpAPI;
import com.ganxin.zeromusic.common.http.volleyHelper.RequestParams;
import com.ganxin.zeromusic.common.util.NetWorkHelper;
import com.ganxin.zeromusic.common.util.ToastHelper;
import com.ganxin.zeromusic.framework.BaseActivity;
import com.ganxin.zeromusic.view.R;

/**
 * 
 * @Description 排行榜的歌曲详情界面
 * @author ganxin
 * @date Apr 10, 2015
 * @email ganxinvip@163.com
 */
public class RankingListActivity extends BaseActivity{

	// 界面标题栏的标题
	private TextView title,rightDiviler;
	// 标题栏的返回按钮、右侧按钮
	private ImageButton backBtn,rightBtn;
	// 无歌曲时显示的图片
	private ImageView nothingImg;
	// 排行榜歌曲显示的listview
	private ListView rankingListview;
	// 排行榜歌曲显示的适配器
	private RankingListViewAdapter rankingListViewAdapter;
	// 歌曲列表的数据源
	private ArrayList<song_list> songList;
	// 榜单的内容描述信息对象
	private billboard bill;
	// 加载显示的对话框
	private Dialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_music);
		initUI();
		initData();
	}

	private void initUI() {
		// TODO Auto-generated method stub
		title=(TextView) findViewById(R.id.local_actionbar_title);
		rightDiviler=(TextView) findViewById(R.id.local_actionbar_right_diviler);
		backBtn=(ImageButton) findViewById(R.id.local_actionbar_back);
		rightBtn=(ImageButton) findViewById(R.id.local_actionbar_scan);
		
		rankingListview=(ListView) findViewById(R.id.local_listview);
		nothingImg=(ImageView) findViewById(R.id.local_nothing_img);

		rightDiviler.setVisibility(View.GONE);
		rightBtn.setVisibility(View.GONE);
		backBtn.setOnClickListener(this);
	}

	private void initData() {
		// TODO Auto-generated method stub
		int type=getIntent().getIntExtra(AppConstant.RANKING_LIST_TYPE,0);
		initTitle(type);
		showLoadingDialog();
		getRankingListMusic(type);
	}

	private void initTitle(int type){
		switch (type) {
		case 1:
			title.setText("新歌榜 Top30");
			break;
		case 2:
			title.setText("热歌榜 Top30");
			break;
		case 21:
			title.setText("欧美榜 Top30");
			break;
		case 22:
			title.setText("经典老歌榜 Top30");
			break;
		case 23:
			title.setText("情歌对唱榜 Top30");
			break;
		case 24:
			title.setText("影视金曲榜 Top30");
			break;
		default:
			title.setText("排行榜");
			break;
		}
	}
	
	private void getRankingListMusic(int type){
		if(NetWorkHelper.isConnected(this)){
			HashMap<String, String> params =RequestParams.getMusicListParams(String.valueOf(type), 30, 0);
			
			HttpAPI.createAndStartGetMusicRequest(params, BillListBean.class,new Listener<BillListBean>() {

				@Override
				public void onResponse(BillListBean arg0) {
					// TODO Auto-generated method stub
					closeLoadingDialog();
					if(arg0.getBillboard()!=null&&arg0.getSong_list()!=null&&arg0.getSong_list().size()>0){
						songList=arg0.getSong_list();
						bill=arg0.getBillboard();
						handler.sendMessage(handler.obtainMessage(2));
					}
					else{
						handler.sendMessage(handler.obtainMessage(1));
					}
				}
			}, new ErrorListener(){

				@Override
				public void onErrorResponse(VolleyError arg0) {
					// TODO Auto-generated method stub
					handler.sendMessage(handler.obtainMessage(1));
				}
				
			});
		}
		else
			handler.sendMessage(handler.obtainMessage(0));
	}
	
	private void initListview(){
		if(bill!=null){
			 LayoutInflater infla = LayoutInflater.from(this);   
		     View headView = infla.inflate(R.layout.search_rankinglist_headview, null);
		     
		     TextView updateDate=(TextView) headView.findViewById(R.id.search_rankinglist_headview_update_date);
		     TextView description=(TextView) headView.findViewById(R.id.search_rankinglist_headview_description);
		     
		     updateDate.setText("更新日期 : "+bill.getUpdate_date());
		     description.setText(bill.getComment());
		     
		     rankingListview.addHeaderView(headView, null, true);
		}
		
		if(songList!=null){
			rankingListViewAdapter=new RankingListViewAdapter(this, songList);
			rankingListview.setAdapter(rankingListViewAdapter);
		}
		
	}
	
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			//没有网络
			case 0:
				closeLoadingDialog();
				nothingImg.setImageResource(R.drawable.error_offline);
				nothingImg.setVisibility(View.VISIBLE);				
				ToastHelper.show(RankingListActivity.this,R.string.network_is_not_connected);
				break;
			//错误响应或返回结果为空
			case 1:
				closeLoadingDialog();
				nothingImg.setImageResource(R.drawable.error_search_noresult);
				nothingImg.setVisibility(View.VISIBLE);				
				ToastHelper.show(RankingListActivity.this,R.string.search_rankinglist_no_result);
				break;
			//有结果
			case 2:
				initListview();
				break;
			default:
				break;
			}
		}
		
	};
	
	/**
	 * 显示加载进度对话框
	 */
	private void showLoadingDialog(){
		dialog = new Dialog(this,R.style.loadingDialog);
		dialog.setContentView(R.layout.dialog_loading_music);
		dialog.setCanceledOnTouchOutside(false);

		//以下更改对话框的大小
		Window dialogWindow = dialog.getWindow();
		WindowManager winManager = (WindowManager)getSystemService("window");
		Display display = winManager.getDefaultDisplay(); //获取屏幕的宽、高
		Point size = new Point();
		display.getSize(size);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes(); //获取对话框当前的参数值
		lp.width = (int) (size.x * 0.7); //宽度设置为屏幕的0.7
		dialogWindow.setAttributes(lp);
		
		// 开始旋转动画
		ImageView img=(ImageView) dialog.findViewById(R.id.dialog_loading_img);
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
	private void closeLoadingDialog(){
		dialog.dismiss();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.local_actionbar_back:
			finish();
			break;

		default:
			break;
		}
	}
}
