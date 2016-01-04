package com.ganxin.zeromusic.common.http.volleyHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * 
 * @Description 请求参数类
 * @author ganxin
 * @date 2014-12-13
 * @email ganxinvip@163.com
 */
public class RequestParams {

	/**
	 * 公共的参数
	 * 
	 * @return params map类型的参数
	 */
	private static HashMap<String, String> commonParams() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("from", "webapp_music");
		params.put("format", "json");

		return params;
	}

	/**
	 * 搜索歌曲，默认method=baidu.ting.search.catalogSug
	 * 
	 * @param keyword
	 *            关键字
	 * @return params map类型的参数
	 */
	public static HashMap<String, String> queryMusicParams(String keyword) {
		HashMap<String, String> params = commonParams();
		try {
			String encodeStr = URLEncoder.encode(keyword, "utf-8");
			params.put("query", encodeStr);
			params.put("method", "baidu.ting.search.catalogSug");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return params;
	}

	/**
	 * 根据歌手ID，查询歌曲，，默认method=baidu.ting.artist.getSongList
	 * 
	 * @param artistID
	 *            歌手ID
	 * @return params map类型的参数
	 */
	public static HashMap<String, String> queryMusicFromArtistIdParams(
			int artistID) {
		HashMap<String, String> params = commonParams();

		params.put("tinguid", String.valueOf(artistID));
		params.put("limits", String.valueOf(30));
		params.put("use_cluster", String.valueOf(1));
		params.put("order", String.valueOf(2));
		params.put("method", "baidu.ting.artist.getSongList");

		return params;
	}

	/**
	 * 根据歌曲ID，下载歌曲（192kbps） 默认method=baidu.ting.song.downWeb
	 * 
	 * @param songID
	 *            歌曲ID
	 * @return
	 */
	public static HashMap<String, String> downMusic(int songID) {
		HashMap<String, String> params = commonParams();

		params.put("songid", String.valueOf(songID));
		//该参数可选，暂不使用
		//params.put("bit", String.valueOf(192));
		params.put("_t", String.valueOf(System.currentTimeMillis()));
		params.put("method", "baidu.ting.song.downWeb");

		return params;
	}

	/**
	 * 获取歌曲列表，默认method=baidu.ting.billboard.billList
	 * 
	 * @param type
	 *            类型，1：新歌榜，2：热歌榜，21：欧美金曲榜,22：经典老歌榜,23：情歌对唱榜,24：影视金曲榜
	 * @param size
	 *            返回条目数量
	 * @param offset
	 *            获取偏移
	 * @return params map类型的参数
	 */
	public static HashMap<String, String> getMusicListParams(String type,
			int size, int offset) {
		HashMap<String, String> params = commonParams();
		params.put("type", type);
		params.put("size", String.valueOf(size));
		params.put("offset", String.valueOf(offset));
		params.put("method", "baidu.ting.billboard.billList");

		return params;
	}

	/**
	 * 获取网络图片的url
	 * 
	 * @param musicName
	 *            歌曲名称
	 * @return params map类型的参数
	 */
	@Deprecated
	public static HashMap<String, String> getNetworkImage(String musicName) {

		HashMap<String, String> params = new HashMap<String, String>();

		try {
			params.put("tn", "baiduimagejson");
			params.put("ie", "utf-8");
			params.put("ic", "0");
			params.put("rn", "15");
			params.put("pn", "1");
			params.put("word", URLEncoder.encode(musicName, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return params;
	}
	
	/**
	 * 获取有道图片的请求URL
	 * @param keyword
	 * @return
	 */
	public static String getYouDaoRequestUrl(String keyword){
		String URL="http://image.youdao.com/search?keyfrom=web.index&q=";
		String word="";
		try {
			word = URLEncoder.encode(keyword, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return URL+word;
	}
}
