package com.ganxin.zeromusic.common.bean;

import java.util.ArrayList;

/**
 * 
 * @Description 根据歌手ID获取歌曲的实体类
 * @author ganxin
 * @date Apr 11, 2015
 * @email ganxinvip@163.com
 */
public class GetSongListBean extends BaseResponseBean {
	private String songnums; // 歌曲数量
	private int havemore; // 是否有更多歌曲
	private ArrayList<songlist> songlist; // 歌曲队列

	public String getSongnums() {
		return songnums;
	}

	public void setSongnums(String songnums) {
		this.songnums = songnums;
	}

	public int getHavemore() {
		return havemore;
	}

	public void setHavemore(int havemore) {
		this.havemore = havemore;
	}

	public ArrayList<songlist> getSonglist() {
		return songlist;
	}

	public void setSonglist(ArrayList<songlist> songlist) {
		this.songlist = songlist;
	}

	/**
	 * 
	 * @Description 歌曲列表类
	 * @author ganxin
	 * @date Apr 11, 2015
	 * @email ganxinvip@163.com
	 */
	public class songlist {
		private String title; // 歌名
		private String author; // 歌手
		private String song_id; // 歌曲ID

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public String getSong_id() {
			return song_id;
		}

		public void setSong_id(String song_id) {
			this.song_id = song_id;
		}
	}
}
