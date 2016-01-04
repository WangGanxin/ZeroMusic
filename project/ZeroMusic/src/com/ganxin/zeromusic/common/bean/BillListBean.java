package com.ganxin.zeromusic.common.bean;

import java.util.ArrayList;

/**
 * 
 * @Description 获取榜单内容的实体类
 * @author ganxin
 * @date Apr 12, 2015
 * @email ganxinvip@163.com
 */
public class BillListBean extends BaseResponseBean {

	private billboard billboard; // 榜单的描述
	private ArrayList<song_list> song_list; // 歌曲列表

	public billboard getBillboard() {
		return billboard;
	}

	public void setBillboard(billboard billboard) {
		this.billboard = billboard;
	}

	public ArrayList<song_list> getSong_list() {
		return song_list;
	}

	public void setSong_list(ArrayList<song_list> song_list) {
		this.song_list = song_list;
	}

	/**
	 * 
	 * @Description 榜单描述的实体类
	 * @author ganxin
	 * @date Apr 12, 2015
	 * @email ganxinvip@163.com
	 */
	public class billboard {
		private String billboard_type; // 榜单类型
		private String update_date; // 更新日期
		private String name; // 榜单名称
		private String comment; // 榜单描述

		public String getBillboard_type() {
			return billboard_type;
		}

		public void setBillboard_type(String billboard_type) {
			this.billboard_type = billboard_type;
		}

		public String getUpdate_date() {
			return update_date;
		}

		public void setUpdate_date(String update_date) {
			this.update_date = update_date;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}
	}

	/**
	 * 
	 * @Description 歌曲列表实体类
	 * @author ganxin
	 * @date Apr 12, 2015
	 * @email ganxinvip@163.com
	 */
	public class song_list {
		private String song_id; // 歌曲ID
		private String title; // 歌名
		private String author; // 演唱者

		public String getSong_id() {
			return song_id;
		}

		public void setSong_id(String song_id) {
			this.song_id = song_id;
		}

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

	}
}
