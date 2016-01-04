package com.ganxin.zeromusic.common.bean;

import java.io.File;

import com.lidroid.xutils.db.annotation.Transient;
import com.lidroid.xutils.http.HttpHandler;

/**
 * 
 * @Description 下载信息实体类
 * @author ganxin
 * @date Aug 23, 2015
 * @email ganxinvip@163.com
 */
public class DownloadInfo {

	public DownloadInfo() {
	}

	private long id;

	@Transient
	private HttpHandler<File> handler;

	private HttpHandler.State state;

	private String downloadUrl;

	private String fileName;

	private String fileSavePath;

	private long progress;

	private long fileLength;

	private boolean autoResume;

	private boolean autoRename;
	
	private boolean isLrcFile;

	private String lyric_file_name;

	private Long size;

	private Long duration;

	private String artist;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public HttpHandler<File> getHandler() {
		return handler;
	}

	public void setHandler(HttpHandler<File> handler) {
		this.handler = handler;
	}

	public HttpHandler.State getState() {
		return state;
	}

	public void setState(HttpHandler.State state) {
		this.state = state;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSavePath() {
		return fileSavePath;
	}

	public void setFileSavePath(String fileSavePath) {
		this.fileSavePath = fileSavePath;
	}

	public long getProgress() {
		return progress;
	}

	public void setProgress(long progress) {
		this.progress = progress;
	}

	public long getFileLength() {
		return fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public boolean isAutoResume() {
		return autoResume;
	}

	public void setAutoResume(boolean autoResume) {
		this.autoResume = autoResume;
	}

	public boolean isAutoRename() {
		return autoRename;
	}

	public void setAutoRename(boolean autoRename) {
		this.autoRename = autoRename;
	}

	public String getLyric_file_name() {
		return lyric_file_name;
	}

	public void setLyric_file_name(String lyric_file_name) {
		this.lyric_file_name = lyric_file_name;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public boolean isLrcFile() {
		return isLrcFile;
	}

	public void setLrcFile(boolean isLrcFile) {
		this.isLrcFile = isLrcFile;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DownloadInfo))
			return false;

		DownloadInfo that = (DownloadInfo) o;

		if (id != that.id)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}
}
