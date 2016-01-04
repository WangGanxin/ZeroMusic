package test;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.NetworkImageView;
import com.ganxin.zeromusic.common.bean.ImageURLBean;
import com.ganxin.zeromusic.common.bean.QueryMusicBean;
import com.ganxin.zeromusic.common.bean.QueryMusicBean.Song;
import com.ganxin.zeromusic.common.http.volleyHelper.HttpAPI;
import com.ganxin.zeromusic.common.http.volleyHelper.RequestParams;
import com.ganxin.zeromusic.common.http.volleyHelper.VolleyHelper;
import com.ganxin.zeromusic.common.http.volleyImage.ImageCacheManager;
import com.ganxin.zeromusic.common.util.LogHelper;
import com.ganxin.zeromusic.view.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		
//		RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());   
//		StringRequest stringRequest = new StringRequest("http://www.baidu.com",  
//                new Response.Listener<String>() {  
//                    @Override  
//                    public void onResponse(String response) {  
//                    	LogHelper.logD("response----"+response); 
//                    }  
//                }, new Response.ErrorListener() {  
//                    @Override  
//                    public void onErrorResponse(VolleyError error) {  
//                        
//                    }  
//                }){
//			
//		};
//		
//		mQueue.add(stringRequest);
		
		//网易api
//		String api="http://s.music.163.com/search/get/?src=loft&type=1&filterDj=true&limit=10&" +
//				"s=%E5%90%8E%E4%BC%9A%E6%97%A0%E6%9C%9F&offect=0";
		
		//网友提供整理的百度api
//		String api2="http://musicapi.sinaapp.com/bdmusic/batch.v3?format=json&id=10263290";
//		
//		String api3="http://tingapi.ting.baidu.com/v1/restserver/ting?from=webapp_music&format=json&method=baidu.ting.song.downWeb" +
//				"&songid=10263290&bit=128&_t=1413017198449";
//				
//		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(api2, null,  
//		        new Response.Listener<JSONObject>() {  
//		            @Override  
//		            public void onResponse(JSONObject response) {
//		            	LogHelper.logD(response.toString());
//		            }  
//		        }, new Response.ErrorListener() {  
//		            @Override  
//		            public void onErrorResponse(VolleyError error) {  
//		            	LogHelper.logD(error.getMessage());
//		            }  
//		        });
//		mQueue.add(jsonObjectRequest);
//		mQueue.start();
		
//		VolleyHelper.init(this);
		
		HashMap<String, String> params=RequestParams.queryMusicParams("张");
		
		HttpAPI.createAndStartGetMusicRequest(params, QueryMusicBean.class,new Listener<QueryMusicBean>(){
					@Override
					public void onResponse(QueryMusicBean arg0) {
						// TODO Auto-generated method stub
						ArrayList<Song> list=arg0.getSong();
						for(Song s:list)
						LogHelper.logD("---------"+s.getArtistname());
					}			
		}, new ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				LogHelper.logD("ERROR----"+arg0.getMessage());
			}			
		});
			
//		ImageLoader imageLoader=new ImageLoader(VolleyHelper.getRequestQueue(),new ImageCache(){
//
//			@Override
//			public Bitmap getBitmap(String arg0) {
//				// TODO Auto-generated method stub
//				return null;
//			}
//
//			@Override
//			public void putBitmap(String arg0, Bitmap arg1) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});
		
//		ImageCacheManager.init(this);
//				
//		NetworkImageView imageView=(NetworkImageView) findViewById(R.id.networkImg);
//		String url="http://g.hiphotos.baidu.com/image/pic/item/500fd9f9d72a605993a94c142a34349b033bba07.jpg";
//		imageView.setImageUrl(url, ImageCacheManager.getInstance().getImageLoader());
		
//		HashMap<String, String> params=RequestParams.getNetworkImage("蔡卓音");
//		HttpAPI.createAndStartGetImageRequest(params,ImageURLBean.class,new Listener<ImageURLBean>(){
//
//			@Override
//			public void onResponse(ImageURLBean arg0) {
//				// TODO Auto-generated method stub
//				LogHelper.logD("测试图片----》"+arg0.getData().size());
//			}
//			
//		}, new ErrorListener(){
//
//			@Override
//			public void onErrorResponse(VolleyError arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});

	}

}
