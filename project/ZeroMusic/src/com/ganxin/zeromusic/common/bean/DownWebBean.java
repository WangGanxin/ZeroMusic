package com.ganxin.zeromusic.common.bean;

import java.util.ArrayList;

/**
 * 
 * @Description 下载歌曲实体类
 * @author ganxin
 * @date Aug 16, 2015
 * @email ganxinvip@163.com
 */
public class DownWebBean extends BaseResponseBean {
	private ArrayList<BitRate> bitrate; // 歌曲比特率列表
	private SongInfo songinfo; // 歌曲信息

	public ArrayList<BitRate> getBitrate() {
		return bitrate;
	}

	public void setBitrate(ArrayList<BitRate> bitrate) {
		this.bitrate = bitrate;
	}

	public SongInfo getSonginfo() {
		return songinfo;
	}

	public void setSonginfo(SongInfo songinfo) {
		this.songinfo = songinfo;
	}

	/**
	 * 
	 * @Description 下载歌曲--歌曲比特率信息实体
	 * @author ganxin
	 * @date Aug 16, 2015
	 * @email ganxinvip@163.com
	 */
	public class BitRate {
		private int file_bitrate; // 文件比特率
		private String file_link; // 文件下载链接
		private String file_extension; // 文件拓展名
		private int file_duration; // 文件时长
		private int file_size; // 文件大小
		private String show_link; //显示的文件下载链接

		public int getFile_bitrate() {
			return file_bitrate;
		}

		public void setFile_bitrate(int file_bitrate) {
			this.file_bitrate = file_bitrate;
		}

		public String getFile_link() {
			return file_link;
		}

		public void setFile_link(String file_link) {
			this.file_link = file_link;
		}

		public String getFile_extension() {
			return file_extension;
		}

		public void setFile_extension(String file_extension) {
			this.file_extension = file_extension;
		}

		public int getFile_duration() {
			return file_duration;
		}

		public void setFile_duration(int file_duration) {
			this.file_duration = file_duration;
		}

		public int getFile_size() {
			return file_size;
		}

		public void setFile_size(int file_size) {
			this.file_size = file_size;
		}

		public String getShow_link() {
			return show_link;
		}

		public void setShow_link(String show_link) {
			this.show_link = show_link;
		}

	}

	/**
	 * 
	 * @Description 下载歌曲--歌曲信息实体
	 * @author ganxin
	 * @date Aug 16, 2015
	 * @email ganxinvip@163.com
	 */
	public class SongInfo {
		private String title; // 歌名
		private String author; // 歌手
		private int song_id; // 歌曲ID
		private String lrclink; // 歌词地址

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

		public int getSong_id() {
			return song_id;
		}

		public void setSong_id(int song_id) {
			this.song_id = song_id;
		}

		public String getLrclink() {
			return lrclink;
		}

		public void setLrclink(String lrclink) {
			this.lrclink = lrclink;
		}

	}

}
