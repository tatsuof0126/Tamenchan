package com.tatsuo.tamenchan.domain;

import android.content.SharedPreferences;

public class HiScore {
	private String name;
	private int score;
	private long date;
	
	public static final String HISCORE_PREF_NAME = "HiScore";
	private static final String KEY_NAME  = "name";
	private static final String KEY_SCORE = "score";
	private static final String KEY_DATE  = "date";
	
	private static final int HISCORE_LENGTH = 5;
	
	public static final String DEFAULT_NAME = "No Name";
	public static final long   DEFAULT_DATE = 0L;
	
	public HiScore(){
	}
	
	public HiScore(String name, int score, long date){
		this.name = name;
		this.score = score;
		this.date = date;
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
	
	public static HiScore[] readHiScore(SharedPreferences preferences){
		HiScore[] hiScore = new HiScore[HISCORE_LENGTH];
		
		for(int i=0;i<HISCORE_LENGTH;i++){
			String name = preferences.getString(KEY_NAME+i, DEFAULT_NAME);
			int score = preferences.getInt(KEY_SCORE+i, (HISCORE_LENGTH-i)*(25/HISCORE_LENGTH));
			long date = preferences.getLong(KEY_DATE+i, DEFAULT_DATE);
			
			hiScore[i] = new HiScore();
			hiScore[i].setName(name);
			hiScore[i].setScore(score);
			hiScore[i].setDate(date);
		}
		
		return hiScore;
	}
	
	public static void writeHiScore(SharedPreferences preferences, HiScore[] hiScore){
		SharedPreferences.Editor editor = preferences.edit();
		
		for(int i=0;i<hiScore.length;i++){
			editor.putString(KEY_NAME+i, hiScore[i].getName());
			editor.putInt(KEY_SCORE+i, hiScore[i].getScore());
			editor.putLong(KEY_DATE+i, hiScore[i].getDate());
		}
		
		editor.commit();
	}

	public static void clearHiScore(SharedPreferences preferences){
		HiScore[] hiScore = new HiScore[HISCORE_LENGTH];
		
		for(int i=0;i<hiScore.length;i++){
			hiScore[i] = new HiScore();			
			hiScore[i].setName(DEFAULT_NAME);
			hiScore[i].setScore((HISCORE_LENGTH-i)*(25/HISCORE_LENGTH));
			hiScore[i].setDate(DEFAULT_DATE);
		}
		
		writeHiScore(preferences, hiScore);
	}
	
}
