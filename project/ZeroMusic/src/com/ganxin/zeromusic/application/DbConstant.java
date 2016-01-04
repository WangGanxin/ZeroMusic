package com.ganxin.zeromusic.application;

/**
 * 
 * @Description 数据库常量类
 * @author ganxin
 * @date Sep 20, 2014
 * @email ganxinvip@163.com
 */
public class DbConstant {
	// 数据库名称
	public static final String DB_NAME = "ZeroMusic";
	// 数据库版本
	public final static int DB_VERSION = 1;
	// 数据库表名
	public static final String TABLE_LOCALMUSIC = "local_music";
	public static final String TABLE_FAVORITES = "favorites";
	public static final String TABLE_DOWNLOAD = "download";
	public static final String TABLE_ARTIST = "artist";
	public static final String TABLE_TIMING_OPEN = "timing";
	public static final String TABLE_HISTORY = "history";
	
	// 相应表的列
	public static final String LOCAL_ID = "_id";
	public static final String LOCAL_TITLE = "title";
	public static final String LOCAL_ARTIST = "artist";
	public static final String LOCAL_ALBUM = "album";
	public static final String LOCAL_PATH = "path";
	public static final String LOCAL_DURATION = "duration";
	public static final String LOCAL_FILE_SIZE = "file_size";
	public static final String LOCAL_LRC_TITLE = "lrc_title";
	
	public static final String ARTIST_ID = "_id";
	public static final String ARTIST_LOCAL_ARTIST = "local_artist";

	public static final String FAVORITES_ID = "_id";
	public static final String FAVORITES_LOCAL_ID = "local_id";
	
	public static final String DOWNLOAD_ID = "_id";
	public static final String DOWNLOAD_LOCAL_ID = "local_id";
	
	public static final String TIMING_OPEN_ID = "_id";
	public static final String TIMING_OPEN_TIME = "time";
	public static final String TIMING_OPEN_MON = "monday";
	public static final String TIMING_OPEN_TUE = "tuesday";
	public static final String TIMING_OPEN_WED = "wednesday";
	public static final String TIMING_OPEN_THU = "thursday";
	public static final String TIMING_OPEN_FRI = "friday";
	public static final String TIMING_OPEN_SAT = "saturday";
	public static final String TIMING_OPEN_SUN = "sunday";
	public static final String TIMING_OPEN_STATUS = "status";
	
	public static final String HISTORY_ID = "_id";
	public static final String HISTORY_TITLE = "title";
	public static final String HISTORY_ARTIST = "artist";
	public static final String HISTORY_ALBUM = "album";
	public static final String HISTORY_PATH = "path";
	public static final String HISTORY_DURATION = "duration";
	public static final String HISTORY_FILE_SIZE = "file_size";
	public static final String HISTORY_LRC_TITLE = "lrc_title";
}
