package com.ganxin.zeromusic.common.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @Description 歌曲信息实体类
 * @author ganxin
 * @date Sep 20, 2014
 * @email ganxinvip@163.com
 */
public class MusicBean implements Parcelable{
	
	private int id;
	private String title;
	private String artist;
	private String path;
	private Long duration;
	private Long size;
	private String lyric_file_name;
	private String album;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public String getLyric_file_name() {
		return lyric_file_name;
	}
	public void setLyric_file_name(String lyric_file_name) {
		this.lyric_file_name = lyric_file_name;
	}	
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(this.id);
		dest.writeString(this.title);
		dest.writeString(this.artist);
		dest.writeString(this.path);
		dest.writeLong(this.duration);
		dest.writeLong(this.size);;
		dest.writeString(this.lyric_file_name);
		dest.writeString(this.album);
	}
	
	//实现Parcelable接口必须创建的内部对象
	public static final Parcelable.Creator<MusicBean> CREATOR = new Parcelable.Creator<MusicBean>(){

		@Override
		public MusicBean createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			MusicBean music = new MusicBean();
			music.setId(source.readInt());
			music.setTitle(source.readString());
			music.setArtist(source.readString());
			music.setPath(source.readString());
			music.setDuration(source.readLong());
			music.setSize(source.readLong());
			music.setLyric_file_name(source.readString());
			music.setAlbum(source.readString());
			
			return music;
		}

		@Override
		public MusicBean[] newArray(int size) {
			// TODO Auto-generated method stub
			return new MusicBean[size];
		}		
	};
		
}
