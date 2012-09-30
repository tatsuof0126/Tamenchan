package com.tatsuo.tamenchan.domain;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;


public class TwitterUser {
	
	String oauthToken;
	String oauthTokenSecret;
	
	private static final String FILE_PROFILE_IMAGE = "profile.jpg";
	
	private TwitterUser(String oauthToken, String oauthTokenSecret){
		this.oauthToken = oauthToken;
		this.oauthTokenSecret = oauthTokenSecret;		
	}
	
	public void tweet(String tweetString) throws TwitterException {    	
    	Twitter twitter = getTwitter();
    	twitter.updateStatus(tweetString);
	}
	
	public Bitmap getProfileImage(Activity activity) {
		Bitmap bitmap = null;
		
		try {
			FileInputStream fis = activity.openFileInput(FILE_PROFILE_IMAGE);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (IOException ioe){
			ioe.printStackTrace();
		}
		
		return bitmap;
	}
	
	private Twitter getTwitter() {
    	ConfigurationBuilder builder = new ConfigurationBuilder();
    	builder.setOAuthConsumerKey(TamenchanDefine.CONSUMER_KEY);
    	builder.setOAuthConsumerSecret(TamenchanDefine.CONSUMER_SECRET);
    	builder.setOAuthAccessToken(oauthToken);
    	builder.setOAuthAccessTokenSecret(oauthTokenSecret);

    	Configuration config = builder.build();
    	
    	Twitter twitter = new TwitterFactory(config).getInstance();
		
    	return twitter;
	}
	
	
	public static TwitterUser getTwitterUser(Activity activity){
		TwitterUser twitterUser = null;
		
    	String oauthToken       = TamenchanSetting.getOauthToken(activity);
    	String oauthTokenSecret = TamenchanSetting.getOauthTokenSecret(activity);
    	
    	if(oauthToken != null && "".equals(oauthToken) == false &&
    			oauthTokenSecret != null && "".equals(oauthTokenSecret)  == false ){
        	twitterUser = new TwitterUser(oauthToken, oauthTokenSecret);
    	}
    	
		return twitterUser;
	}
	
	public static RequestToken getRequestToken(){
		RequestToken requestToken = null;
		
		OAuthAuthorization twitterOauth = getOAuthAuthorization();
        try {
        	requestToken = twitterOauth.getOAuthRequestToken(TamenchanDefine.CALLBACK_URL);
        } catch (TwitterException e) {
        	e.printStackTrace();
        }
        
        return requestToken;
	}
	
	public static TwitterUser makeTwitterUser(Activity activity, RequestToken requestToken, Intent intent){
		TwitterUser twitterUser = null;
		
    	if(intent == null){
    		return null;
    	}
    	
		String oauth_verifier = intent.getStringExtra(TamenchanDefine.PARAM_OAUTH_VERIFIER);
		if(oauth_verifier == null || "".equals(oauth_verifier)){
			return null;
		}
    	
	    AccessToken accessToken = null;
    	try {
    		accessToken = getOAuthAuthorization().getOAuthAccessToken(requestToken, oauth_verifier);
    	} catch (TwitterException te){
    		te.printStackTrace();
    	}
    	
    	if(accessToken == null){
    		return null;
    	}
    	
    	String oauthToken       = accessToken.getToken();
    	String oauthTokenSecret = accessToken.getTokenSecret();
    	
    	twitterUser = new TwitterUser(oauthToken, oauthTokenSecret);
    	twitterUser.saveTwitterInfo(activity);
    	
		return twitterUser;
	}
	
	public void saveTwitterInfo(Activity activity){
    	TamenchanSetting.setOauthToken(activity, oauthToken);
    	TamenchanSetting.setOauthTokenSecret(activity, oauthTokenSecret);
    	
		Twitter twitter = getTwitter();
		
		User user = null;
		try	{
			user = twitter.verifyCredentials();
			String screenName = user.getScreenName();
			TamenchanSetting.setTwitterScreenName(activity, screenName);
			
			URL profileImageURL = user.getProfileImageURL();
			Bitmap profileImage = null;
			try {  
				// BitmapFactory.decodeStreamでビットマップを作成。  
				profileImage = BitmapFactory.decodeStream(profileImageURL.openConnection().getInputStream());
				
				if (profileImage != null) {
					FileOutputStream fos = activity.openFileOutput(FILE_PROFILE_IMAGE, Activity.MODE_PRIVATE);
					profileImage.compress(CompressFormat.JPEG, 100, fos);
					fos.close();
				}
			} catch (IOException ioe){
				ioe.printStackTrace();
			}

		} catch (TwitterException te){
			te.printStackTrace();
		}
	}	
	
	private static OAuthAuthorization getOAuthAuthorization(){
		OAuthAuthorization twitterOauth = null;
		
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(TamenchanDefine.CONSUMER_KEY);
        builder.setOAuthConsumerSecret(TamenchanDefine.CONSUMER_SECRET);
        
        Configuration configuration = builder.build();
 
        twitterOauth = new OAuthAuthorization(configuration);
        twitterOauth.setOAuthAccessToken(null);

        return twitterOauth;
	}

}
