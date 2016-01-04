package com.ganxin.zeromusic.common.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ganxin.zeromusic.application.DbConstant;
import com.ganxin.zeromusic.common.bean.MusicBean;
import com.ganxin.zeromusic.common.bean.TimingBean;

/**
 * 
 * @Description 数据库帮助类
 * @author ganxin
 * @date Sep 20, 2014
 * @email ganxinvip@163.com
 */
public class MusicDBHelper extends SQLiteOpenHelper{
	
	private SQLiteDatabase db = this.getWritableDatabase();
	
	public MusicDBHelper(Context context) {
		super(context, DbConstant.DB_NAME, null, DbConstant.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//分别建立数据库表：本地歌曲、歌手、下载、我喜欢
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DbConstant.TABLE_LOCALMUSIC
				+ " (" + DbConstant.LOCAL_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + DbConstant.LOCAL_TITLE
				+ " TEXT UNIQUE NOT NULL," + DbConstant.LOCAL_ARTIST + " TEXT,"
				+ DbConstant.LOCAL_ALBUM + " TEXT," + DbConstant.LOCAL_PATH
				+ " TEXT NOT NULL," + DbConstant.LOCAL_DURATION
				+ " LONG NOT NULL," + DbConstant.LOCAL_FILE_SIZE
				+ " LONG NOT NULL," + DbConstant.LOCAL_LRC_TITLE + " TEXT"+");");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DbConstant.TABLE_ARTIST + " ("
				+ DbConstant.ARTIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DbConstant.ARTIST_LOCAL_ARTIST + " TEXT UNIQUE NOT NULL);");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DbConstant.TABLE_FAVORITES
				+ " (" + DbConstant.FAVORITES_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DbConstant.FAVORITES_LOCAL_ID + " INTEGER UNIQUE NOT NULL);");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DbConstant.TABLE_DOWNLOAD
				+ " (" + DbConstant.DOWNLOAD_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DbConstant.DOWNLOAD_LOCAL_ID + " INTEGER UNIQUE NOT NULL);");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DbConstant.TABLE_TIMING_OPEN
				+ " (" + DbConstant.TIMING_OPEN_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DbConstant.TIMING_OPEN_TIME+ " TEXT," 
				+ DbConstant.TIMING_OPEN_MON + " TEXT,"
				+ DbConstant.TIMING_OPEN_TUE + " TEXT," 
				+ DbConstant.TIMING_OPEN_WED + " TEXT," 
				+ DbConstant.TIMING_OPEN_THU + " TEXT," 
				+ DbConstant.TIMING_OPEN_FRI + " TEXT," 
				+ DbConstant.TIMING_OPEN_SAT + " TEXT," 
				+ DbConstant.TIMING_OPEN_SUN + " TEXT," 
				+ DbConstant.TIMING_OPEN_STATUS+ " TEXT"+");");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DbConstant.TABLE_HISTORY
				+ " (" + DbConstant.HISTORY_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + DbConstant.HISTORY_TITLE
				+ " TEXT UNIQUE NOT NULL," + DbConstant.HISTORY_ARTIST + " TEXT,"
				+ DbConstant.HISTORY_ALBUM + " TEXT," + DbConstant.HISTORY_PATH
				+ " TEXT NOT NULL," + DbConstant.HISTORY_DURATION
				+ " LONG NOT NULL," + DbConstant.HISTORY_FILE_SIZE
				+ " LONG NOT NULL," + DbConstant.HISTORY_LRC_TITLE + " TEXT"+");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + DbConstant.TABLE_LOCALMUSIC);
		db.execSQL("DROP TABLE IF EXISTS " + DbConstant.TABLE_FAVORITES);
		db.execSQL("DROP TABLE IF EXISTS " + DbConstant.TABLE_DOWNLOAD);
		db.execSQL("DROP TABLE IF EXISTS " + DbConstant.TABLE_ARTIST);
		db.execSQL("DROP TABLE IF EXISTS " + DbConstant.TABLE_TIMING_OPEN);
		db.execSQL("DROP TABLE IF EXISTS " + DbConstant.TABLE_HISTORY);
		onCreate(db);		
	}

	/**
	 * 清空本地音乐表，并重新创建
	 */
	public void clearLocalMusicTable(){
		db.execSQL("DROP TABLE IF EXISTS " + DbConstant.TABLE_LOCALMUSIC);
		onCreate(db);
	}
		
	/**
	 * 插入本地音乐列表
	 * @param music 音乐实体对象
	 * @return Long 插入的行id
	 */
	public Long insertLocal(MusicBean music){		
		ContentValues values = new ContentValues();
		values.put(DbConstant.LOCAL_TITLE, music.getTitle());
		values.put(DbConstant.LOCAL_ARTIST, music.getArtist());
		values.put(DbConstant.LOCAL_PATH, music.getPath());
		values.put(DbConstant.LOCAL_DURATION, music.getDuration());
		values.put(DbConstant.LOCAL_FILE_SIZE, music.getSize());
		values.put(DbConstant.LOCAL_LRC_TITLE, music.getLyric_file_name());
		Long id = db.insert(DbConstant.TABLE_LOCALMUSIC, null, values);
		return id;	
	}
	
	/**
	 * 插入歌手列表
	 * @param music 音乐实体对象
	 * @return Long 插入的行id
	 */
	public Long insertArtist(MusicBean music){
		ContentValues values = new ContentValues();
		values.put(DbConstant.ARTIST_LOCAL_ARTIST, music.getArtist());
		Long i = db.insert(DbConstant.TABLE_ARTIST, null, values);
		return i;
	}
	
	/**
	 * 插入我喜欢列表
	 * @param music 音乐实体对象
	 * @return  Long 插入的行id
	 */
	public Long insertFav(MusicBean music){
		ContentValues values = new ContentValues();
		values.put(DbConstant.FAVORITES_LOCAL_ID, music.getId());
		Long i = db.insert(DbConstant.TABLE_FAVORITES, null, values);
		return i;		
	}
	
	/**
	 * 插入下载表
	 * @param music 音乐实体对象
	 * @return  Long 插入的行id
	 */
	public Long insertDownLoad(MusicBean music){
		ContentValues values = new ContentValues();
		values.put(DbConstant.DOWNLOAD_LOCAL_ID, music.getId());
		Long i = db.insert(DbConstant.TABLE_DOWNLOAD, null, values);
		return i;		
	}
	
	/**
	 * 插入定时开启表
	 * @param timing 定时设置的实体对象
	 * @return
	 */
	public Long inserTiming(TimingBean timing){
		ContentValues values = new ContentValues();
		values.put(DbConstant.TIMING_OPEN_MON, timing.getMonday());
		values.put(DbConstant.TIMING_OPEN_TUE, timing.getTuesday());
		values.put(DbConstant.TIMING_OPEN_WED, timing.getWednesday());
		values.put(DbConstant.TIMING_OPEN_THU, timing.getThursday());
		values.put(DbConstant.TIMING_OPEN_FRI, timing.getFriday());
		values.put(DbConstant.TIMING_OPEN_SAT, timing.getSaturday());
		values.put(DbConstant.TIMING_OPEN_SUN, timing.getSunday());
		values.put(DbConstant.TIMING_OPEN_STATUS, timing.getStatus());
		values.put(DbConstant.TIMING_OPEN_TIME, timing.getTime());
		Long i = db.insert(DbConstant.TABLE_TIMING_OPEN, null, values);
		return i;
	}
	
	/**
	 * 插入收听历史表
	 * @param music
	 * @return
	 */
	public Long inserHistory(MusicBean music){
		ContentValues values = new ContentValues();
		values.put(DbConstant.HISTORY_TITLE, music.getTitle());
		values.put(DbConstant.HISTORY_ARTIST, music.getArtist());
		values.put(DbConstant.HISTORY_PATH, music.getPath());
		values.put(DbConstant.HISTORY_DURATION, music.getDuration());
		values.put(DbConstant.HISTORY_FILE_SIZE, music.getSize());
		values.put(DbConstant.HISTORY_LRC_TITLE, music.getLyric_file_name());
		Long id = db.insert(DbConstant.TABLE_HISTORY, null, values);
		return id;	
	}
		
	/**
	 * 查询本地音乐数据库
	 * @return Cursor 返回本地音乐的数据库结果
	 */
	public Cursor queryLocalMusicByID(){
		Cursor cur = db.query(DbConstant.TABLE_LOCALMUSIC, null, null, null, null,
				null, DbConstant.LOCAL_ID + " asc");
		return cur;		
	}
	
	/**
	 * 根据 歌手名 查询本地数据库，得到歌曲信息
	 * @param artist 歌手名
	 * @return  Cursor 查询数据库返回的cursor
	 */
	public Cursor queryLocalByArtist(String artist){
		String selection = DbConstant.LOCAL_ARTIST + "=?";
		String selectionArgs[] = { artist };
		Cursor cur = db.query(DbConstant.TABLE_LOCALMUSIC, null, selection,
				selectionArgs, null, null, DbConstant.LOCAL_ID + " asc");
		return cur;
	}
	
	
	/**
	 * 查询歌手列表
	 * @return Cursor 返回歌手列表的数据库结果
	 */
	public Cursor queryArtistByID(){
		Cursor cur = db.query(DbConstant.TABLE_ARTIST, null, null, null, null,
				null, DbConstant.ARTIST_ID + " asc");
		return cur;
	}
	
	/**
	 * 查询 我喜欢 列表
	 * @return Cursor 返回我喜欢列表的数据库结果
	 */
	public Cursor queryFavByID() {
		Cursor cur = db.query(DbConstant.TABLE_FAVORITES, null, null, null, null,
				null, DbConstant.LOCAL_ID + " asc");
		return cur;
	}
	
	/**
	 * 根据 我喜欢  里收藏的id查询本地数据库，得到歌曲信息
	 * @return Cursor 返回查询本地数据库的结果
	 */
	public Cursor queryFavFromLocal(){
		Cursor idCursor = db.query(DbConstant.TABLE_FAVORITES, null, null, null,
				null, null, DbConstant.FAVORITES_LOCAL_ID + " asc");
		
		String selection =DbConstant.LOCAL_ID + " in (?)";
		String selectionArgs[] = new String[idCursor.getCount()];
		
		if (idCursor.getCount() != 0){
			idCursor.moveToFirst();
			int i = 0;
			StringBuffer param=new StringBuffer("?");
			do{
				param.append(",?");
				selectionArgs[i] = String.valueOf(idCursor.getInt(idCursor
						.getColumnIndex(DbConstant.FAVORITES_LOCAL_ID)));				
				i++;
				idCursor.moveToNext();				
			}
			while(!idCursor.isAfterLast());

			//重新修改参数？的个数
			selection=selection.replace("?",param.toString());
		}			
		Cursor cur = db.query(DbConstant.TABLE_LOCALMUSIC, null, selection,
				selectionArgs, null, null, DbConstant.LOCAL_ID + " asc");

		return cur;
	}

	/**
	 * 根据本地ID 查询 下载表 的ID
	 * @param localID
	 * @return
	 */
	public int queryDownloadByLocalID(int localId) {
		String selection = DbConstant.DOWNLOAD_LOCAL_ID + "=?";
		String selectionArgs[] = { localId+"" };
		Cursor cur = db.query(DbConstant.TABLE_DOWNLOAD, null, selection,
				selectionArgs, null, null, DbConstant.DOWNLOAD_ID + " asc");
		
		if(cur.moveToFirst()){
			return cur.getInt(cur.getColumnIndex(DbConstant.DOWNLOAD_ID));
		}
		else {
			return -1;
		}
	}
	
	/**
	 * 查询 下载 列表
	 * @return Cursor 返回下载列表的数据库结果
	 */
	public Cursor queryDownloadByID() {
		Cursor cur = db.query(DbConstant.TABLE_DOWNLOAD, null, null, null, null,
				null, DbConstant.DOWNLOAD_ID+ " asc");
		return cur;
	}
	
	/**
	 * 根据 下载表  里的local id查询本地数据库，得到歌曲信息
	 * @return Cursor 返回查询本地数据库的结果
	 */
	public Cursor queryDownloadFromLocal(){
		Cursor idCursor = db.query(DbConstant.TABLE_DOWNLOAD, null, null, null,
				null, null, DbConstant.DOWNLOAD_LOCAL_ID + " asc");
		
		String selection =DbConstant.LOCAL_ID + " in (?)";
		String selectionArgs[] = new String[idCursor.getCount()];
		
		if (idCursor.getCount() != 0){
			idCursor.moveToFirst();
			int i = 0;
			StringBuffer param=new StringBuffer("?");
			do{
				param.append(",?");
				selectionArgs[i] = String.valueOf(idCursor.getInt(idCursor
						.getColumnIndex(DbConstant.DOWNLOAD_LOCAL_ID)));				
				i++;
				idCursor.moveToNext();				
			}
			while(!idCursor.isAfterLast());

			//重新修改参数？的个数
			selection=selection.replace("?",param.toString());
		}			
		Cursor cur = db.query(DbConstant.TABLE_LOCALMUSIC, null, selection,
				selectionArgs, null, null, DbConstant.LOCAL_ID + " asc");

		return cur;
	}
	
	/**
	 * 查询定时开启表的所有数据，返回list对象
	 * @return
	 */
	public List<TimingBean> queryTimingByID(){
		List<TimingBean> list=new ArrayList<TimingBean>();
		Cursor cur = db.query(DbConstant.TABLE_TIMING_OPEN, null, null, null, null,
				null, DbConstant.TIMING_OPEN_ID + " asc");
		if(cur!=null&&cur.getCount()>0){
			cur.moveToFirst();
			do{
				TimingBean timing = new TimingBean();
				timing.setId(cur.getInt(cur.getColumnIndex(DbConstant.TIMING_OPEN_ID)));
				timing.setMonday(cur.getString(cur.getColumnIndex(DbConstant.TIMING_OPEN_MON)));
				timing.setTuesday(cur.getString(cur.getColumnIndex(DbConstant.TIMING_OPEN_TUE)));
				timing.setWednesday(cur.getString(cur.getColumnIndex(DbConstant.TIMING_OPEN_WED)));
				timing.setThursday(cur.getString(cur.getColumnIndex(DbConstant.TIMING_OPEN_THU)));
				timing.setFriday(cur.getString(cur.getColumnIndex(DbConstant.TIMING_OPEN_FRI)));
				timing.setSaturday(cur.getString(cur.getColumnIndex(DbConstant.TIMING_OPEN_SAT)));
				timing.setSunday(cur.getString(cur.getColumnIndex(DbConstant.TIMING_OPEN_SUN)));
				timing.setStatus(cur.getString(cur.getColumnIndex(DbConstant.TIMING_OPEN_STATUS)));
				timing.setTime(cur.getString(cur.getColumnIndex(DbConstant.TIMING_OPEN_TIME)));
				
				list.add(timing);
				cur.moveToNext();				
			}
			while(!cur.isAfterLast());
		}
		return list;
	}
	
	/**
	 * 查询历史表的所有数据，返回list对象
	 * @return
	 */
	public ArrayList<MusicBean> queryHistoryByID(){
		Cursor cur = db.query(DbConstant.TABLE_HISTORY, null, null, null, null,
				null, DbConstant.HISTORY_ID + " asc");
		ArrayList<MusicBean> musicList=new ArrayList<MusicBean>();
		if(cur!=null&&cur.getCount()>0){
			cur.moveToFirst();
			do{
				MusicBean musicBean= new MusicBean();
				musicBean.setId(cur.getInt(cur.getColumnIndex(DbConstant.HISTORY_ID)));
				musicBean.setTitle(cur.getString(cur.getColumnIndex(DbConstant.HISTORY_TITLE)));
				musicBean.setArtist(cur.getString(cur.getColumnIndex(DbConstant.HISTORY_ARTIST)));
				musicBean.setDuration(cur.getLong(cur.getColumnIndex(DbConstant.HISTORY_DURATION)));
				musicBean.setSize(cur.getLong(cur.getColumnIndex(DbConstant.HISTORY_FILE_SIZE)));
				musicBean.setLyric_file_name(cur.getString(cur.getColumnIndex(DbConstant.HISTORY_LRC_TITLE)));
				musicBean.setPath(cur.getString(cur.getColumnIndex(DbConstant.HISTORY_PATH)));
				
				musicList.add(musicBean);
				cur.moveToNext();				
			}
			while(!cur.isAfterLast());
		}
		return musicList;	
	}
	
	/**
	 * 根据指定的ID，更新本地音乐表的album字段
	 * @param localID 本地音乐的ID
	 * @param url 专辑封面图的URL
	 * @return affectedRow 受影响的行数
	 */
	public int updateLocalAlbum(int localID,String url){
		
		ContentValues contentValues = new ContentValues();
		contentValues.put(DbConstant.LOCAL_ALBUM, url);
		
		int affectedRow=db.update(DbConstant.TABLE_LOCALMUSIC, contentValues,DbConstant.LOCAL_ID + "=?",
				new String[] {String.valueOf(localID)});
		
		return affectedRow;
	}
	
	/**
	 * 根据指定ID，更新定时开启表的status字段
	 * @param timingID 表Id
	 * @param status 更新的状态值
	 * @return
	 */
	public int updateTimingStaus(int timingID,String status){
		ContentValues contentValues = new ContentValues();
		contentValues.put(DbConstant.TIMING_OPEN_STATUS, status);
		
		int affectedRow=db.update(DbConstant.TABLE_TIMING_OPEN, contentValues,DbConstant.TIMING_OPEN_ID + "=?",
				new String[] {String.valueOf(timingID)});
		
		return affectedRow;		
	}
	
	/**
	 * 更新history的第一行数据
	 * @param bean
	 * @return
	 */
	public int updateHistoryTable(MusicBean bean){
		ContentValues contentValues = new ContentValues();
		contentValues.put(DbConstant.HISTORY_TITLE, bean.getTitle());
		contentValues.put(DbConstant.HISTORY_ARTIST, bean.getArtist());
		contentValues.put(DbConstant.HISTORY_DURATION, bean.getDuration());
		contentValues.put(DbConstant.HISTORY_FILE_SIZE, bean.getSize());
		contentValues.put(DbConstant.HISTORY_LRC_TITLE, bean.getLyric_file_name());
		contentValues.put(DbConstant.HISTORY_PATH, bean.getPath());
		
		int affectedRow=db.update(DbConstant.TABLE_HISTORY, contentValues,DbConstant.HISTORY_ID + "=?",
				new String[] {"0"});
		
		return affectedRow;			
	}
	
	/**
	 * 删除本地音乐列表对应id的歌曲
	 * @param musicId 歌曲id
	 * @return i 受影响的行数
	 */
	public int delLocal(int musicId){
		int i = db.delete(DbConstant.TABLE_LOCALMUSIC, DbConstant.LOCAL_ID + "=?",
				new String[] {String.valueOf(musicId)});
		return i;		
	}
	
	/**
	 * 根据 歌手名称 删除歌手列表对应的歌手(没有该歌手的歌曲时才删除)
	 * @param musicId
	 * @return i受影响的行数
	 */
	public int delArtist(String artist){
		Cursor cur =queryLocalByArtist(artist);	
		if(cur.getCount()<=0){
			
			int i = db.delete(DbConstant.TABLE_ARTIST, DbConstant.ARTIST_LOCAL_ARTIST
					+ "=?", new String[] {artist});
			cur.close();
			return i;	
		}
		else{
			cur.close();
			return 0;
		}
	}
	
	/**
	 * 删除我喜欢列表对应id的歌曲
	 * @param musicId 歌曲id
	 * @return i 受影响的行数
	 */
	public int delFav(int musicId){
		int i = db.delete(DbConstant.TABLE_FAVORITES, DbConstant.FAVORITES_LOCAL_ID
				+ "=?", new String[] {String.valueOf(musicId)});
		return i;	
	}
	
	/**
	 * 删除下载列表对应local id的歌曲
	 * @param localId 歌曲id
	 * @return i 受影响的行数
	 */
	public int delDownloadFromLocalID(int localId){
		int i = db.delete(DbConstant.TABLE_DOWNLOAD, DbConstant.DOWNLOAD_LOCAL_ID
				+ "=?", new String[] {String.valueOf(localId)});
		return i;	
	}
	
	/**
	 * 删除下载列表对应 id的歌曲
	 * @param musicId 歌曲id
	 * @return i 受影响的行数
	 */
	public int delDownloadFromID(int musicId){
		int i = db.delete(DbConstant.TABLE_DOWNLOAD, DbConstant.DOWNLOAD_ID
				+ "=?", new String[] {String.valueOf(musicId)});
		return i;	
	}
	
	/**
	 * 删除定时开启表对应ID的对象 
	 * @param timingID
	 * @return
	 */
	public int delTimingFromID(int timingID){
		int i = db.delete(DbConstant.TABLE_TIMING_OPEN, DbConstant.TIMING_OPEN_ID
				+ "=?", new String[] {String.valueOf(timingID)});
		return i;			
	}
	
	/**
	 * 清空下载表，并重新创建
	 */
	public void removewDownloadTable(){
		db.execSQL("DROP TABLE IF EXISTS " + DbConstant.TABLE_DOWNLOAD);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DbConstant.TABLE_DOWNLOAD
				+ " (" + DbConstant.DOWNLOAD_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DbConstant.DOWNLOAD_LOCAL_ID + " INTEGER UNIQUE NOT NULL);");
	}
		
	/**
	 * 	根据查询本地音乐列表得到的cursor 来获取歌曲信息集合
	 * @param curLocal 本地音乐列表查询到的cursor
	 * @return ArrayList<MusicInfo> 歌曲实体类集合
	 */
	public ArrayList<MusicBean> getMusicListFromCursor(Cursor curLocal){
		if (curLocal.getCount() != 0){
			curLocal.moveToFirst();
			ArrayList<MusicBean> musicList = new ArrayList<MusicBean>();
			
			do{
				MusicBean music = new MusicBean();
				music.setId(curLocal.getInt(curLocal
						.getColumnIndex(DbConstant.LOCAL_ID)));
				music.setTitle(curLocal.getString(curLocal
						.getColumnIndex(DbConstant.LOCAL_TITLE)));
				music.setArtist(curLocal.getString(curLocal
						.getColumnIndex(DbConstant.LOCAL_ARTIST)));
				music.setPath(curLocal.getString(curLocal
						.getColumnIndex(DbConstant.LOCAL_PATH)));
				music.setDuration(curLocal.getLong(curLocal
						.getColumnIndex(DbConstant.LOCAL_DURATION)));
				music.setSize(curLocal.getLong(curLocal
						.getColumnIndex(DbConstant.LOCAL_FILE_SIZE)));
				music.setLyric_file_name(curLocal.getString(curLocal
						.getColumnIndex(DbConstant.LOCAL_LRC_TITLE)));
				music.setAlbum(curLocal.getString(curLocal
						.getColumnIndex(DbConstant.LOCAL_ALBUM)));
				musicList.add(music);
				curLocal.moveToNext();				
			}
			while(!curLocal.isAfterLast());
			
			return musicList;
		}
		else
			return null;	
	}
}
