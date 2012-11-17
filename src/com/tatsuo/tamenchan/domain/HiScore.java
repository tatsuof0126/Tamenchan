package com.tatsuo.tamenchan.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.content.SharedPreferences;

public class HiScore {
	private String name;
	private int score;
	private long date;
	private int registeredId;
	
	private static final String HISCORE_PREF_NAME = "HiScore";
	
	private static final String[] KEY_NAME  = {"name0","name","name2"};
	private static final String[] KEY_SCORE = {"score0","score","score2"};
	private static final String[] KEY_DATE  = {"date0","date","date2"};
	private static final String[] KEY_REGISTERED_ID  = {"registeredId0","registeredId","registeredId2"};
	
	private static final int HISCORE_LENGTH = 5;
	
	public static final String  DEFAULT_NAME = "No Name";
	public static final long    DEFAULT_DATE = 0L;
	public static final int[][] DEFAULT_SCORE = {{25,20,15,10,5},{25,20,15,10,5},{10,8,6,4,2}};
	public static final int     DEFAULT_REGISTERED_ID = -999;
	
	public HiScore(){
		this.registeredId = DEFAULT_REGISTERED_ID;
	}
	
	public HiScore(String name, int score, long date){
		this.name = name;
		this.score = score;
		this.date = date;
		this.registeredId = DEFAULT_REGISTERED_ID;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public long getDate(){
		return date;
	}
	public void setDate(long date){
		this.date = date;
	}
	public int getRegisteredId() {
		return registeredId;
	}
	public void setRegisteredId(int registeredId) {
		this.registeredId = registeredId;
	}

	public String getDateStr(){
		String dateStr = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		
		// UTC時刻で来るのでタイムゾーンをUTCにセット
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		dateStr = dateFormat.format(new Date(date));
		
		return dateStr;
	}
	
	public static HiScore[] readHiScore(Activity activity){
		int gamelevel = TamenchanSetting.getGameLevel(activity);		
		return readHiScore(activity, gamelevel);
	}
	
	public static HiScore[] readHiScore(Activity activity, int gamelevel){
		SharedPreferences preferences =	activity.getSharedPreferences(HISCORE_PREF_NAME, Activity.MODE_PRIVATE);
		
		HiScore[] hiScore = new HiScore[HISCORE_LENGTH];
		
		for(int i=0;i<HISCORE_LENGTH;i++){
			String name = preferences.getString(KEY_NAME[gamelevel]+i, DEFAULT_NAME);
			int score = preferences.getInt(KEY_SCORE[gamelevel]+i, DEFAULT_SCORE[gamelevel][i]);
			long date = preferences.getLong(KEY_DATE[gamelevel]+i, DEFAULT_DATE);
			int registeredId = preferences.getInt(KEY_REGISTERED_ID[gamelevel]+i, DEFAULT_REGISTERED_ID);
			
			hiScore[i] = new HiScore();
			hiScore[i].setName(name);
			hiScore[i].setScore(score);
			hiScore[i].setDate(date);
			hiScore[i].setRegisteredId(registeredId);
		}
		
		return hiScore;
	}
	
	public static HiScore[] readAllHiScore(Activity activity){
		HiScore[] allHiScore = new HiScore[HISCORE_LENGTH*TamenchanDefine.GAME_LEVEL.length];
		
		for(int i=0;i<TamenchanDefine.GAME_LEVEL.length;i++){
			HiScore[] hiScore = readHiScore(activity, i);
			System.arraycopy(hiScore, 0, allHiScore, i*HISCORE_LENGTH, HISCORE_LENGTH);			
		}
		
		return allHiScore;
	}
	
	public static void writeHiScore(Activity activity, HiScore[] hiScore){
		int gamelevel = TamenchanSetting.getGameLevel(activity);		
		
		SharedPreferences preferences =	activity.getSharedPreferences(HISCORE_PREF_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		
		for(int i=0;i<hiScore.length;i++){
			editor.putString(KEY_NAME[gamelevel]+i, hiScore[i].getName());
			editor.putInt(KEY_SCORE[gamelevel]+i, hiScore[i].getScore());
			editor.putLong(KEY_DATE[gamelevel]+i, hiScore[i].getDate());
			editor.putInt(KEY_REGISTERED_ID[gamelevel]+i, hiScore[i].getRegisteredId());
		}
		
		editor.commit();
	}

	public static void clearHiScore(Activity activity){
		int gamelevel = TamenchanSetting.getGameLevel(activity);		

		HiScore[] hiScore = new HiScore[HISCORE_LENGTH];
		
		for(int i=0;i<hiScore.length;i++){
			hiScore[i] = new HiScore();
			hiScore[i].setName(DEFAULT_NAME);
			hiScore[i].setScore(DEFAULT_SCORE[gamelevel][i]);
			hiScore[i].setDate(DEFAULT_DATE);
			hiScore[i].setRegisteredId(DEFAULT_REGISTERED_ID);
		}
		
		writeHiScore(activity, hiScore);
	}
	
}
