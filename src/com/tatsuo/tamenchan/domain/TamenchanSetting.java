package com.tatsuo.tamenchan.domain;

import java.util.UUID;

import android.app.Activity;
import android.content.SharedPreferences;

public class TamenchanSetting {
	
	public static final String SETTING_PREF_NAME = "Setting";
	
	private static final String KEY_INITPLAY  = "initplay";
	private static final String KEY_DEVICE_ID = "deviceId";
	
	private static final String KEY_GAMELEVEL = "gamelevel";
	private static final String KEY_HIGHLEVELFLAG = "highlevelflag";
	private static final String KEY_HAITYPE   = "haitype";

	private static final String KEY_OAUTH_TOKEN         = "oauthToken";
	private static final String KEY_OAUTH_TOKEN_SECRET  = "oauthTokenSecret";
	private static final String KEY_TWITTER_SCREEN_NAME = "screenname";
	
	public static String getDeviceId(Activity activity){
		SharedPreferences preferences =	activity.getSharedPreferences(SETTING_PREF_NAME, Activity.MODE_PRIVATE);		
		String deviceId = preferences.getString(KEY_DEVICE_ID, "");
		
		if("".equals(deviceId)){
			deviceId = UUID.randomUUID().toString();
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(KEY_DEVICE_ID, deviceId);
			editor.commit();
		}
		
		return deviceId;
	}
	
	public static void clearDeviceId(Activity activity){
		SharedPreferences preferences =	activity.getSharedPreferences(SETTING_PREF_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(KEY_DEVICE_ID, "");
		editor.commit();
	}
	
	public static boolean isInitialPlay(Activity activity){
		SharedPreferences preferences =	activity.getSharedPreferences(SETTING_PREF_NAME, Activity.MODE_PRIVATE);
    	return preferences.getBoolean(KEY_INITPLAY, true);
	}

	public static void setInitialPlay(Activity activity, boolean initialPlay){
		SharedPreferences preferences =	activity.getSharedPreferences(SETTING_PREF_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(KEY_INITPLAY, initialPlay);
		editor.commit();		
	}
	
	public static int getGameLevel(Activity activity){
		SharedPreferences preferences =	activity.getSharedPreferences(SETTING_PREF_NAME, Activity.MODE_PRIVATE);
    	return preferences.getInt(KEY_GAMELEVEL, TamenchanDefine.GAMELEVEL_MIDDLE);
	}
	
	public static void setGameLevel(Activity activity, int gameLevel){
		SharedPreferences preferences =	activity.getSharedPreferences(SETTING_PREF_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(KEY_GAMELEVEL, gameLevel);
		editor.commit();		
	}
	
	public static int getHaiType(Activity activity){
		SharedPreferences preferences =	activity.getSharedPreferences(SETTING_PREF_NAME, Activity.MODE_PRIVATE);
    	return preferences.getInt(KEY_HAITYPE, 0);
	}
	
	public static void setHaiType(Activity activity, int haiType){
		SharedPreferences preferences =	activity.getSharedPreferences(SETTING_PREF_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(KEY_HAITYPE, haiType);
		editor.commit();
	}
	
	public static boolean getHighLevelFlag(Activity activity){
		SharedPreferences preferences =	activity.getSharedPreferences(SETTING_PREF_NAME, Activity.MODE_PRIVATE);
    	return preferences.getBoolean(KEY_HIGHLEVELFLAG, false);
	}

	public static void setHighLevelFlag(Activity activity, boolean highLevelFlag){
		SharedPreferences preferences =	activity.getSharedPreferences(SETTING_PREF_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(KEY_HIGHLEVELFLAG, highLevelFlag);
		editor.commit();		
	}
	
	public static String getOauthToken(Activity activity){
		SharedPreferences preferences =	activity.getSharedPreferences(SETTING_PREF_NAME, Activity.MODE_PRIVATE);
    	return preferences.getString(KEY_OAUTH_TOKEN, "");
	}
	
	public static void setOauthToken(Activity activity, String oauthToken){
		SharedPreferences preferences =	activity.getSharedPreferences(SETTING_PREF_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(KEY_OAUTH_TOKEN, oauthToken);
		editor.commit();
	}
	
	public static String getOauthTokenSecret(Activity activity){
		SharedPreferences preferences =	activity.getSharedPreferences(SETTING_PREF_NAME, Activity.MODE_PRIVATE);
    	return preferences.getString(KEY_OAUTH_TOKEN_SECRET, "");
	}
	
	public static void setOauthTokenSecret(Activity activity, String oauthTokenSecret){
		SharedPreferences preferences =	activity.getSharedPreferences(SETTING_PREF_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(KEY_OAUTH_TOKEN_SECRET, oauthTokenSecret);
		editor.commit();
	}
	
	public static String getTwitterScreenName(Activity activity){
		SharedPreferences preferences =	activity.getSharedPreferences(SETTING_PREF_NAME, Activity.MODE_PRIVATE);
    	return preferences.getString(KEY_TWITTER_SCREEN_NAME, "");
	}
	
	public static void setTwitterScreenName(Activity activity, String screenName){
		SharedPreferences preferences =	activity.getSharedPreferences(SETTING_PREF_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(KEY_TWITTER_SCREEN_NAME, screenName);
		editor.commit();
	}
	
}
