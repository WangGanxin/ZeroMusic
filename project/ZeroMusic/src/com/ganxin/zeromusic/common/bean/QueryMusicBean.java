package com.ganxin.zeromusic.common.bean;

import java.util.ArrayList;

/**
 * 
 * @Description 搜索歌曲接口实体
 * @author ganxin
 * @date 2014-12-15
 * @email ganxinvip@163.com
 */
public class QueryMusicBean extends BaseResponseBean {
	private String order; //排序方式

	private ArrayList<Song> song; //歌曲列表
	
	private ArrayList<Artist> artist; //歌手列表
	
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public ArrayList<Song> getSong() {
		return song;
	}

	public void setSong(ArrayList<Song> song) {
		this.song = song;
	}

	public ArrayList<Artist> getArtist() {
		return artist;
	}

	public void setArtist(ArrayList<Artist> artist) {
		this.artist = artist;
	}

	/**
	 * 
	 * @Description 查询结果返回的歌曲实体类
	 * @author ganxin
	 * @date 2014-12-15
	 * @email ganxinvip@163.com
	 */
	public class Song{
		private int songid; //歌曲ID
		private String encrypted_songid; //加密的歌曲ID
		private String songname; //歌名
		private String artistname; //歌手
		
		public int getSongid() {
			return songid;
		}
		public void setSongid(int songid) {
			this.songid = songid;
		}
		public String getEncrypted_songid() {
			return encrypted_songid;
		}
		public void setEncrypted_songid(String encrypted_songid) {
			this.encrypted_songid = encrypted_songid;
		}
		public String getSongname() {
			return songname;
		}
		public void setSongname(String songname) {
			this.songname = songname;
		}
		public String getArtistname() {
			return artistname;
		}
		public void setArtistname(String artistname) {
			this.artistname = artistname;
		}		
	}
	
	/**
	 * 
	 * @Description 查询结果返回的歌手实体类
	 * @author ganxin
	 * @date Apr 10, 2015
	 * @email ganxinvip@163.com
	 */
	public class Artist{
		private int artistid; //歌手ID
		private String artistname; //歌手名称
		
		public int getArtistid() {
			return artistid;
		}
		public void setArtistid(int artistid) {
			this.artistid = artistid;
		}
		public String getArtistname() {
			return artistname;
		}
		public void setArtistname(String artistname) {
			this.artistname = artistname;
		}		
	}
}
