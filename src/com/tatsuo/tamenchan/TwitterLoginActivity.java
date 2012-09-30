package com.tatsuo.tamenchan;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tatsuo.tamenchan.domain.TamenchanDefine;

public class TwitterLoginActivity extends Activity {
	
	static final String KEY_AUTH_URL = "auth_url";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitterwebview);

		WebView webView = (WebView)findViewById(R.id.twitterwebview);
		webView.setWebViewClient(new TwitterWebViewClient());
		
		//auth_urlでTwitterの認証画面を呼び出し
		String authUrl = getIntent().getStringExtra(KEY_AUTH_URL);
		
		Log.i("auth_url", authUrl);
		
		webView.loadUrl(authUrl);
	}

	private class TwitterWebViewClient extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			
			Log.i("callback url",url);

			if (url != null && url.startsWith(TamenchanDefine.CALLBACK_URL)) {
				
				HashMap<String, String> hashMap = convertUrlParameters2HashMap(url);
				
				String oauthToken = hashMap.get(TamenchanDefine.PARAM_OAUTH_TOKEN);
				String oauthVerifier = hashMap.get(TamenchanDefine.PARAM_OAUTH_VERIFIER);
				
				Intent intent = getIntent();
				intent.putExtra(TamenchanDefine.PARAM_OAUTH_TOKEN, oauthToken);
				intent.putExtra(TamenchanDefine.PARAM_OAUTH_VERIFIER, oauthVerifier);
				
				setResult(Activity.RESULT_OK, intent);
				
				finish();
			}
		}
	}
	
	private HashMap<String, String> convertUrlParameters2HashMap(String url){
		HashMap<String, String> hashMap = new HashMap<String, String>();
		
		String[] splitedUrl = url.split("\\?");
		
		if(splitedUrl.length == 1){
			return hashMap;
		}
		
		String[] urlParameters = splitedUrl[1].split("&");
		
		for(int i=0;i<urlParameters.length;i++){
			String[] params = urlParameters[i].split("=");
			
			if(params.length >= 2){
				hashMap.put(params[0], params[1]);
			}
		}
		
		return hashMap;
	}

}
