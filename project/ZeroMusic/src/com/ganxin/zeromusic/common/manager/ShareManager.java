package com.ganxin.zeromusic.common.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.ganxin.zeromusic.application.AppConstant;
import com.ganxin.zeromusic.view.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

/**
 * 
 * @Description 分享管理类
 * @author ganxin
 * @date Nov 21, 2015
 * @email ganxinvip@163.com
 */
public class ShareManager {
	//饿汉单例模式
	private ShareManager(){};
	private static final ShareManager instance=new ShareManager();
	
	public static ShareManager getInstance() {
		return instance;
	}
	
	// 首先在您的Activity中添加如下成员变量
	private  static final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	
	/**
	 * 初始化分享内容
	 * @param ctx
	 */
	public void init(Context ctx){		
		// 设置分享内容
		mController.setShareContent(ctx.getResources().getString(R.string.about_share_content));
		// 设置分享图片, 参数2为图片的url地址
		mController.setShareMedia(new UMImage(ctx,AppConstant.APP_SHARE_IMAGE));
	}
	
	/**
	 * 弹出分享面板
	 * @param ctx
	 */
	public void popUmengShareDialog(Activity ctx){
		try {
			//去除腾讯微博
			mController.getConfig().removePlatform( SHARE_MEDIA.TENCENT);
			// 是否只有已登录用户才能打开分享选择页
			mController.openShare(ctx, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置SSO登录的回调
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void setOnActivityResult(int requestCode, int resultCode, Intent data){		
		/**使用SSO授权必须添加如下代码 */
	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}
	
	/**
	 * 添加分享到微信/朋友圈
	 */
	public void addWechat(Context ctx){
		//微信的appID 和 appSecret
		String appID = "wx7978a8db5c5d1076";
		String appSecret = "d4624c36b6795d1d99dcf0547af5443d";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(ctx,appID,appSecret);
		wxHandler.addToSocialSDK();
		// 添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(ctx,appID,appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.setTargetUrl(AppConstant.APP_SHARE_URL);
		wxCircleHandler.setTitle(ctx.getResources().getString(R.string.about_share_title));
		wxCircleHandler.addToSocialSDK();
	}
	
	/**
	 * 添加分享到QQ好友
	 * @param activity
	 */
	public void addQQfriend(Activity activity){
		//参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		String appId="1104896533";
		String appKey="SRomGel83bg92Qb1";
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity,appId,appKey);
		qqSsoHandler.setTargetUrl(AppConstant.APP_SHARE_URL);
		qqSsoHandler.setTitle(activity.getResources().getString(R.string.about_share_title));
		qqSsoHandler.addToSocialSDK();  
	}
	
	/**
	 * 添加分享到QQ空间
	 * @param activity
	 */
	public void addQQZone(Activity activity){
		//参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		String appId="1104896533";
		String appKey="SRomGel83bg92Qb1";
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity,appId,appKey);
		qZoneSsoHandler.setTargetUrl(AppConstant.APP_SHARE_URL);
		qZoneSsoHandler.addToSocialSDK();
	}
	
	/**
	 * 添加分享到新浪微博
	 */
	public void addSinaWeibo(Context ctx){
		//设置新浪SSO handler
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		
		SinaShareContent sinaContent=new SinaShareContent();
		sinaContent.setShareContent(ctx.getResources().getString(R.string.about_share_content));
		sinaContent.setTitle(ctx.getResources().getString(R.string.about_share_title));
		sinaContent.setTargetUrl(AppConstant.APP_SHARE_URL);
		sinaContent.setShareImage(new UMImage(ctx,AppConstant.APP_SHARE_IMAGE));
		mController.setShareMedia(sinaContent);
	}
}
