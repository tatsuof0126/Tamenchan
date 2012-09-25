package com.tatsuo.tamenchan.domain;

import java.util.UUID;

import android.content.SharedPreferences;

public class TamenchanSetting {
	
	public static final String SETTING_PREF_NAME = "Setting";
	
	private static final String KEY_INITPLAY  = "initplay";
	private static final String KEY_DEVICE_ID = "deviceId";

	public static String getDeviceId(SharedPreferences preferences){
		String deviceId = preferences.getString(KEY_DEVICE_ID, "");
		
		if("".equals(deviceId)){
			deviceId = UUID.randomUUID().toString();
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(KEY_DEVICE_ID, deviceId);
			editor.commit();
		}
		
		return deviceId;
	}
	
	public static boolean isInitialPlay(SharedPreferences preferences){
    	return preferences.getBoolean(KEY_INITPLAY, true);
	}

	public static void setInitialPlay(SharedPreferences preferences, boolean initialPlay){
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(KEY_INITPLAY, initialPlay);
		editor.commit();		
	}
	
}
