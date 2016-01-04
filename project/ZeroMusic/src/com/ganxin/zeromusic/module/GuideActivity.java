package com.ganxin.zeromusic.module;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ganxin.zeromusic.application.AppConstant;
import com.ganxin.zeromusic.common.util.DensityHelper;
import com.ganxin.zeromusic.common.util.SharPreferHelper;
import com.ganxin.zeromusic.framework.BaseActivity;
import com.ganxin.zeromusic.view.R;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * 
 * @Description 引导页
 * @author ganxin
 * @date Oct 27, 2015
 * @email ganxinvip@163.com
 */
public class GuideActivity extends BaseActivity{

	private Context context;
	private GuideAdapter mAdapter;
	private ViewPager mPager;
	private CirclePageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		context=this;
		initView();
		initData();
	}

	private void initView() {
		mPager=(ViewPager) findViewById(R.id.guide_viewpager);
		mIndicator=(CirclePageIndicator) findViewById(R.id.guide_indicator);		
	}
	
	private void initData() {
		mAdapter = new GuideAdapter();
		mPager.setAdapter(mAdapter);
				
		mIndicator.setRadius(DensityHelper.dip2px(context,4));
		mIndicator.setViewPager(mPager);

	}
		
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}



	/**
	 * 
	 * @Description 引导页适配器
	 * @author ganxin
	 * @date Nov 23, 2015
	 * @email ganxinvip@163.com
	 */
	private class GuideAdapter extends PagerAdapter{

		private final int[] imgIdAddress = { R.drawable.guide_1,R.drawable.guide_2,R.drawable.guide_3,R.drawable.guide_4};
		
		@Override
		public int getCount() {
			return imgIdAddress.length;
		}
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0==arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			View view = container.findViewWithTag(position);
			if(view==null){
				if (position < imgIdAddress.length - 1){
                    ImageView imgView = new ImageView(GuideActivity.this);
                    imgView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    imgView.setBackground(getResources().getDrawable(imgIdAddress[position]));
                    view = imgView;
				}
				else{
                    final ImageView imgView = new ImageView(GuideActivity.this);
                    imgView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    imgView.setBackground(getResources().getDrawable(imgIdAddress[position]));
                    imgView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							imgView.setClickable(false);
							new Handler().postDelayed(new Runnable() {
								
								@Override
								public void run() {
									Intent intent = new Intent(GuideActivity.this, MenuActivity.class);
									startActivity(intent);
									
						    		SharPreferHelper.setConfig(context, AppConstant.SHARPREFER_FILENAME,
						    				AppConstant.GUIDE_SHOW,true);
						    		
									finish();
								}
							},1500);
						}
					});
                    view = imgView;
				}
                view.setTag(position);
                container.addView(view);
			}			
			return view;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
            if (object instanceof View) {
                View view = (View) object;
                Object tag = view.getTag();
                if (tag instanceof Integer && (Integer) tag == position) {
                    container.removeView(view);
                }
            }
		}
	}
}
