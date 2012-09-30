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
	
	private static final String KEY_NAME  = "name";
	private static final String KEY_SCORE = "score";
	private static final String KEY_DATE  = "date";
	private static final String KEY_REGISTERED_ID  = "registeredId";
	
	private static final int HISCORE_LENGTH = 5;
	
	public static final String DEFAULT_NAME = "No Name";
	public static final long   DEFAULT_DATE = 0L;
	public static final int    DEFAULT_REGISTERED_ID = -999;
	
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
		SharedPreferences preferences =	activity.getSharedPreferences(HISCORE_PREF_NAME, Activity.MODE_PRIVATE);
		
		HiScore[] hiScore = new HiScore[HISCORE_LENGTH];
		
		for(int i=0;i<HISCORE_LENGTH;i++){
			String name = preferences.getString(KEY_NAME+i, DEFAULT_NAME);
			int score = preferences.getInt(KEY_SCORE+i, (HISCORE_LENGTH-i)*(25/HISCORE_LENGTH));
			long date = preferences.getLong(KEY_DATE+i, DEFAULT_DATE);
			int registeredId = preferences.getInt(KEY_REGISTERED_ID+i, DEFAULT_REGISTERED_ID);
			
			hiScore[i] = new HiScore();
			hiScore[i].setName(name);
			hiScore[i].setScore(score);
			hiScore[i].setDate(date);
			hiScore[i].setRegisteredId(registeredId);
		}
		
		return hiScore;
	}
	
	public static void writeHiScore(Activity activity, HiScore[] hiScore){
		SharedPreferences preferences =	activity.getSharedPreferences(HISCORE_PREF_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		
		for(int i=0;i<hiScore.length;i++){
			editor.putString(KEY_NAME+i, hiScore[i].getName());
			editor.putInt(KEY_SCORE+i, hiScore[i].getScore());
			editor.putLong(KEY_DATE+i, hiScore[i].getDate());
			editor.putInt(KEY_REGISTERED_ID+i, hiScore[i].getRegisteredId());
		}
		
		editor.commit();
	}

	public static void clearHiScore(Activity activity){
		HiScore[] hiScore = new HiScore[HISCORE_LENGTH];
		
		for(int i=0;i<hiScore.length;i++){
			hiScore[i] = new HiScore();
			hiScore[i].setName(DEFAULT_NAME);
			hiScore[i].setScore((HISCORE_LENGTH-i)*(25/HISCORE_LENGTH));
			hiScore[i].setDate(DEFAULT_DATE);
			hiScore[i].setRegisteredId(DEFAULT_REGISTERED_ID);
		}
		
		writeHiScore(activity, hiScore);
	}
	
}
