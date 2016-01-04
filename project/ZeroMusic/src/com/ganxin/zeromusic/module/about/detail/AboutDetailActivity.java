package com.ganxin.zeromusic.module.about.detail;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.ganxin.zeromusic.framework.BaseActivity;
import com.ganxin.zeromusic.view.R;

/**
 * 
 * @Description 关于零听的详情页面
 * @author ganxin
 * @date Apr 20, 2015
 * @email ganxinvip@163.com
 */
public class AboutDetailActivity extends BaseActivity{

	private ImageButton backBtn;
	private LinearLayout progressView;
	private WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_detatil);
		initData();
	}

	private void initData() {
		webView=(WebView) findViewById(R.id.about_detail_webview);
		progressView=(LinearLayout) findViewById(R.id.web_view_progress);
		backBtn=(ImageButton) findViewById(R.id.about_detail_actionbar_back);
		
	    webView.loadUrl("file:///android_asset/about.html");
		WebSettings webSetting = webView.getSettings();
		webSetting.setJavaScriptEnabled(true);
		webSetting.setBuiltInZoomControls(false);
		webSetting.setSupportZoom(false);
		webSetting.setLoadsImagesAutomatically(true);
		webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
	    
	    webView.setWebViewClient(new WebViewClient(){
	    	@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    		view.loadUrl(url);
				return false;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				progressView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				progressView.setVisibility(View.GONE);
			}
	    });
	    
	    backBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(webView.canGoBack()){
					webView.goBack();
				}
				else{
					finish();
				}
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
