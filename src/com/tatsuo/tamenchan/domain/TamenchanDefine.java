package com.tatsuo.tamenchan.domain;

public class TamenchanDefine {
	// ハイスコア登録サーバー
	public static final String SERVER_URI = "http://tamenchanserver.herokuapp.com/";
//	public static final String SERVER_URI = "http://192.168.11.6:3000/";
	
	// ハイスコア登録パス
	public static final String HISCORELIST_PATH = "hiscorelist";

	// Twitter関連	
    // Application key
    public static final String CONSUMER_KEY = "dXEL6STjVsalD1gXK1sYfA";
    
    // Application secret code
    public static final String CONSUMER_SECRET = "IDpMYl2x6ZkTBBwDxzEeRJF385doyWdcxuj4id0CSo";
    
    // Twitterアプリ承認時にコールバックされるURL
    public static final String CALLBACK_URL = "http://tamenchanserver.herokuapp.com/twittercallback";
    
    // Twitterアプリ承認時にコールバックされるURLの認証トークンパラメーター情報
    public static final String PARAM_OAUTH_TOKEN = "oauth_token";
    
    // Twitterアプリ承認時にコールバックされるURLの認証立証パラメーター情報
    public static final String PARAM_OAUTH_VERIFIER = "oauth_verifier";
    
    // 認証後パラメータOAuth token参照キー
    public static final String SUB_KEY_OAUTH_TOKEN = "oauth_token";
    
    // 認証後パラメータOAuth token secret参照キー
    public static final String SUB_KEY_OAUTH_TOKEN_SECRET = "oauth_token_secret";	
	
}
