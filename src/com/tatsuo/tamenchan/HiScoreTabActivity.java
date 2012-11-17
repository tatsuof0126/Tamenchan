package com.tatsuo.tamenchan;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.tatsuo.tamenchan.domain.HiScore;
import com.tatsuo.tamenchan.domain.RegisteredHiScore;
import com.tatsuo.tamenchan.domain.TamenchanDefine;
import com.tatsuo.tamenchan.domain.TamenchanScore;
import com.tatsuo.tamenchan.domain.TamenchanSetting;
import com.tatsuo.tamenchan.domain.TwitterUser;

public class HiScoreTabActivity extends TabActivity {
	
	private static final String TAB1 = "tab1";
	private static final String TAB2 = "tab2";
	
	private static final String BUTTON_MENU     = "menu";
	private static final String BUTTON_CONTINUE = "continue";
	private static final String BUTTON_CLEAR    = "clear";
	private static final String BUTTON_POST     = "post";
	private static final String BUTTON_RELOAD   = "reloadhiscore";
	
	private static final int TWEET_REGISTERED_HISCORE = 1;
	private static final int TWEET_LOCAL_HISCORE      = 2;
	
	private static final int RANKING_OUTSIDE = 999;
	
	private int myRank;
	private int topRank;
	private int rankingNum;
	
    private RequestToken requestToken;	
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TabHost tabHost = getTabHost();
		
		LayoutInflater.from(this).inflate(R.layout.hiscoretab,
					tabHost.getTabContentView(), true);
		
		TabSpec tab1 = tabHost.newTabSpec(TAB1);
		tab1.setIndicator("私のハイスコア", 
				getResources().getDrawable(android.R.drawable.ic_menu_myplaces));
		tab1.setContent(R.id.hiscoretab1);
		
		TabSpec tab2 = tabHost.newTabSpec(TAB2);
		tab2.setIndicator("みんなのハイスコア", 
				getResources().getDrawable(android.R.drawable.ic_menu_mapmode));
		tab2.setContent(R.id.hiscoretab2);
		
		tabHost.addTab(tab1);
		tabHost.addTab(tab2);

		tabHost.setCurrentTab(0);
		tabHost.setOnTabChangedListener(new TabChangeListener());
		
		// タイトルを設定
		int gamelevel = TamenchanSetting.getGameLevel(this);
		TextView myHiscoreView = (TextView)findViewById(R.id.myhiscoretitle);
		myHiscoreView.setText("私のハイスコア <"+TamenchanDefine.GAME_LEVEL[gamelevel]+">");
		TextView ourHiscoreView = (TextView)findViewById(R.id.ourhiscoretitle);
		ourHiscoreView.setText("みんなのハイスコア <"+TamenchanDefine.GAME_LEVEL[gamelevel]+">");
		
		// スコア・メッセージ表示部の表示を決定
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.hiscoretab1);
        TableLayout tableLayout = (TableLayout)findViewById(R.id.resultmessagelayout);
        TextView resultText = (TextView)findViewById(R.id.resultscore);
    	
        TamenchanScore tamenchanScore = (TamenchanScore)getIntent().getSerializableExtra(GameMainActivity.KEY_SCORE);
        myRank = getIntent().getIntExtra(ResultActivity.KEY_RANK, RANKING_OUTSIDE);
        
        if(tamenchanScore != null){
    		// ゲームスコアの情報がある場合は、ゲーム画面から直接来ているので、
        	// スコアを表示
        	resultText.setText(" 得点は "+tamenchanScore.getScore()+" 点でした");
        } else if(myRank != RANKING_OUTSIDE){
    		// 順位の情報がある場合は、ハイスコア登録画面から来ているので、
        	// 登録完了メッセージを表示
        	resultText.setText(" 登録されました");
        } else {
    		// ゲームスコアの情報がない場合は、メインメニューから来ているので、
        	// スコア・メッセージ表示部分を削除
        	linearLayout.removeView(tableLayout);
        }
		
        showLocalHiScore(myRank);
        
        Button continueButton = (Button)findViewById(R.id.continuegame);
        Button clearButton = (Button)findViewById(R.id.clearhiscore);
        if(continueButton != null){
        	continueButton.setTag(BUTTON_CONTINUE);
        	continueButton.setOnClickListener(new ButtonClickListener());
        	// 続けるボタンがある場合は、クリアボタンを非表示にする
        	clearButton.setVisibility(View.GONE);
        } else {
            clearButton.setTag(BUTTON_CLEAR);
            clearButton.setOnClickListener(new ButtonClickListener());
        }
        
        Button postButton = (Button)findViewById(R.id.postbutton);
        postButton.setTag(BUTTON_POST);
        postButton.setOnClickListener(new ButtonClickListener());        
		
        Button menuButton = (Button)findViewById(R.id.menubutton);
        menuButton.setTag(BUTTON_MENU);
        menuButton.setOnClickListener(new ButtonClickListener());        
		
        Button reloadButton = (Button)findViewById(R.id.reloadhiscore);
        reloadButton.setTag(BUTTON_RELOAD);
        reloadButton.setOnClickListener(new ButtonClickListener());        
		
        Button menuButton2 = (Button)findViewById(R.id.menubutton2);
        menuButton2.setTag(BUTTON_MENU);
        menuButton2.setOnClickListener(new ButtonClickListener());        
		
        // ハイスコア登録画面からの遷移で、Tweet指定がある場合はTweet処理に移る
        String mode = getIntent().getStringExtra(ResultActivity.KEY_MODE);
        if(ResultActivity.MODE_TWEET.equals(mode)){
        	tweetLocalHiScore();        	
        }
        
	}

    class ButtonClickListener implements OnClickListener {
    	public void onClick(View v){
    		if(BUTTON_MENU.equals(v.getTag())){
    			finish();
    		} else if(BUTTON_CONTINUE.equals(v.getTag())){
        		Intent intent = new Intent(HiScoreTabActivity.this, GameMainActivity.class);
        		startActivity(intent);
        		finish();
    		} else if(BUTTON_CLEAR.equals(v.getTag())){
    			AlertDialog.Builder dialog
    				= new AlertDialog.Builder(HiScoreTabActivity.this);
    			dialog.setTitle("ハイスコアのクリア");
    			dialog.setMessage("ハイスコアをクリアします。よろしいですか？");
    			dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
		    			HiScore.clearHiScore(HiScoreTabActivity.this);
		    			// ハイスコアをクリアしたらデバイスIDも初期化
		    			TamenchanSetting.clearDeviceId(HiScoreTabActivity.this);
		    	        showLocalHiScore();
					}
				});
    			dialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
    			dialog.show();
    		} else if(BUTTON_POST.equals(v.getTag())){
    			PostHiScoreTask postHiScoreTask = new PostHiScoreTask();
    			postHiScoreTask.execute();
    		} else if(BUTTON_RELOAD.equals(v.getTag())){
    			GetHiScoreTask getHiScoreTask = new GetHiScoreTask();
    			getHiScoreTask.execute();
    		}
    	}
    }
    
    class TabChangeListener implements OnTabChangeListener {
    	public void onTabChanged(String tab) {
    		if(TAB2.equals(tab)){
    			GetHiScoreTask getHiScoreTask = new GetHiScoreTask();
    			getHiScoreTask.execute();
    		}
        }
    }
    
    class GetHiScoreTask extends AsyncTask<Void, Void, RegisteredHiScore[]> {
    	ProgressDialog dialog = null;
    	
    	@Override
    	protected void onPreExecute(){
            dialog = new ProgressDialog(HiScoreTabActivity.this);
            dialog.setIndeterminate(true);
            dialog.setMessage("通信中...");
            dialog.show();
    	}
    	
		@Override
		protected RegisteredHiScore[] doInBackground(Void... args) {
			RegisteredHiScore[] registeredHiScore = null;
			
	    	HttpClient client = new DefaultHttpClient();
	    	HttpUriRequest request = new HttpGet(TamenchanDefine.SERVER_URI + TamenchanDefine.HISCORELIST_PATH 
		    	+ "?gamelevel=" + TamenchanSetting.getGameLevel(HiScoreTabActivity.this));
	    	HttpResponse response = null;
	    	HttpEntity entity = null;
	    	String jsonStr = null;
	        
	    	try {
	    		response = client.execute(request);
	    		
		    	if(response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
		    		entity = response.getEntity();
		    		jsonStr = EntityUtils.toString(entity);
	    			
		    		registeredHiScore = parseRegisteredHiScore(jsonStr);
		    	}
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	} finally {
    	        try {
    	        	if(entity != null){
    	        		entity.consumeContent();
    	        	}
    	        }
    	        catch (IOException e) {
    	    		e.printStackTrace();
    	        }
	    	}
	    	
	    	client.getConnectionManager().shutdown();

			return registeredHiScore;
		}
		
	    @Override  
	    protected void onPostExecute(RegisteredHiScore[] registeredHiScore) {
	    	dialog.dismiss();
	    	showRegisteredHiScore(registeredHiScore);
	    }
    }
    
    class PostHiScoreTask extends AsyncTask<Void, Void, RegisteredHiScore[]> {
    	ProgressDialog dialog = null;
    	
    	@Override
    	protected void onPreExecute(){
            dialog = new ProgressDialog(HiScoreTabActivity.this);
            dialog.setIndeterminate(true);
            dialog.setMessage("通信中...");
            dialog.show();
    	}
    	
		@Override
		protected RegisteredHiScore[] doInBackground(Void... args) {
			RegisteredHiScore[] registeredHiScore = null;

	    	HttpClient client = new DefaultHttpClient();
	    	HttpPost request = new HttpPost(TamenchanDefine.SERVER_URI + TamenchanDefine.HISCORELIST_PATH
	    		+ "?gamelevel=" + TamenchanSetting.getGameLevel(HiScoreTabActivity.this));
	    	HttpResponse response = null;
	    	HttpEntity entity = null;
	    	String jsonStr = null;
	        
	    	try {
	    		HiScore[] hiScore = HiScore.readHiScore(HiScoreTabActivity.this);
	    		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    		
	    		for(int i=0;i<hiScore.length;i++){
	    			if(hiScore[i].getRegisteredId() != HiScore.DEFAULT_REGISTERED_ID){
	    				params.add(new BasicNameValuePair("id"+i, Integer.toString(hiScore[i].getRegisteredId())));
	    			}
		    		params.add(new BasicNameValuePair("devid"+i, TamenchanSetting.getDeviceId(HiScoreTabActivity.this)));
		    		params.add(new BasicNameValuePair("name"+i, hiScore[i].getName()));
		    		params.add(new BasicNameValuePair("score"+i, Integer.toString(hiScore[i].getScore())));
		    		params.add(new BasicNameValuePair("date"+i, hiScore[i].getDateStr()));
	    		}
	    		
	    		request.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
	    		
	    		response = client.execute(request);
	    		
		    	if(response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
		    		entity = response.getEntity();
		    		jsonStr = EntityUtils.toString(entity);
	    			
//		    		Log.i("JSON", jsonStr);
		    		
		    		registeredHiScore = parseRegisteredHiScore(jsonStr);
		    		
		    		for(int i=0;i<hiScore.length;i++){
		    			hiScore[i].setRegisteredId(registeredHiScore[i].getRegisteredId());
		    		}
		    		HiScore.writeHiScore(HiScoreTabActivity.this, hiScore);
		    	}
	    	} catch (Exception e){
	    		e.printStackTrace();
	    		registeredHiScore = null;
	    	}
			
			return registeredHiScore;
		}
		
	    @Override  
	    protected void onPostExecute(RegisteredHiScore[] registeredHiScore) {
	    	dialog.dismiss();
	    	
	    	if(registeredHiScore != null){
	    		topRank = 999;
	    		rankingNum = 0;
	    		for(int i=0;i<registeredHiScore.length;i++){
	    			int rank = registeredHiScore[i].getRank();
	    			if(rank <= 20){
	    				rankingNum++;
	    				if(topRank > rank){
	    					topRank = rank;
	    				}
	    			}	    			
	    		}
	    		
	    		AlertDialog.Builder resultDialog
	    			= new AlertDialog.Builder(HiScoreTabActivity.this);
	    		String messageText = "";
	    		if(rankingNum != 0){
	    			messageText = "登録されました。\nみんなのハイスコアに "
	    					+rankingNum+"件 ランクインしています。\n最高位は "+topRank+"位 です。";
	    		} else {
	    			messageText = "登録しました。\nみんなのハイスコアへのランクインはありません。";
	    		}
    			resultDialog.setMessage(messageText);
	    		resultDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
	    			@Override
	    			public void onClick(DialogInterface dialog, int which) {
	    			}
	    		});
	    		if(rankingNum >= 1){
	    			resultDialog.setPositiveButton("つぶやく", new DialogInterface.OnClickListener() {
	    				@Override
	    				public void onClick(DialogInterface dialog, int which) {
	    					tweetRegisteredHiScore();	    				
	    				}
	    			});
	    		}
	    		resultDialog.show();
	    	} else {
		    	AlertDialog.Builder resultDialog
		    		= new AlertDialog.Builder(HiScoreTabActivity.this);
		    	resultDialog.setMessage("登録に失敗しました。\nしばらくしてから再度お試しください。");
		    	resultDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    		@Override
		    		public void onClick(DialogInterface dialog, int which) {}
		    	});
	    		resultDialog.show();	    	
	    	}
	    }
    }
    
    private void tweetRegisteredHiScore(){
    	TwitterUser twitterUser = TwitterUser.getTwitterUser(this);
    	
		if(twitterUser != null){
			// Twitter認証があるとき
			String tweetString = makeTweetString(TWEET_REGISTERED_HISCORE);
			showTweetDialog(twitterUser, tweetString);
		} else {
			// Twitter認証情報がないとき
			authentication(TWEET_REGISTERED_HISCORE);
		}
    }
    
    private void tweetLocalHiScore(){
    	TwitterUser twitterUser = TwitterUser.getTwitterUser(this);
    	    	
		if(twitterUser != null){
			// Twitter認証があるとき
			String tweetString = makeTweetString(TWEET_LOCAL_HISCORE);
			showTweetDialog(twitterUser, tweetString);
		} else {
			// Twitter認証情報がないとき
			authentication(TWEET_LOCAL_HISCORE);
		}
    }
    
    private void showTweetDialog(final TwitterUser twitterUser, String tweetString){
		final EditText tweetEditText = new EditText(this);
		tweetEditText.setText(tweetString);
		
		AlertDialog.Builder dialog
			= new AlertDialog.Builder(HiScoreTabActivity.this);
		dialog.setTitle("Twitterにつぶやく");
		dialog.setView(tweetEditText);
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
		    		twitterUser.tweet(tweetEditText.getText().toString());
		    		Toast.makeText(HiScoreTabActivity.this, "つぶやきました", Toast.LENGTH_LONG).show();
		    	} catch (TwitterException te){
		    		te.printStackTrace();
		    	}
			}
		});
		dialog.show();
    }
    
    private void authentication(int tweetmode){
    	requestToken = TwitterUser.getRequestToken();
 
        Intent intent = new Intent(this, TwitterLoginActivity.class);
        intent.putExtra(TwitterLoginActivity.KEY_AUTH_URL, requestToken.getAuthorizationURL());    	 
        startActivityForResult(intent, tweetmode);
    }

    @Override
    protected void onActivityResult(int requestCode,
    		int resultCode, Intent intent) {
    	TwitterUser twitterUser = TwitterUser.makeTwitterUser(this, requestToken, intent);
    	
    	if(twitterUser == null){
    		return;
    	}
    	
    	String tweetString = makeTweetString(requestCode);
    	showTweetDialog(twitterUser, tweetString);
    }
    
    private String makeTweetString(int tweetmode){
    	int gamelevel = TamenchanSetting.getGameLevel(this);
    	HiScore[] hiScore = HiScore.readHiScore(this);
    	
    	if(tweetmode == TWEET_LOCAL_HISCORE){
    		return hiScore[myRank].getName()+"さんの得点<"+TamenchanDefine.GAME_LEVEL[gamelevel]+">は"
    			+hiScore[myRank].getScore()+"点でした。 #ためんちゃん http://bit.ly/ST1EfJ";    		
    	} else {
    		return "みんなのハイスコア<"+TamenchanDefine.GAME_LEVEL[gamelevel]+">に"
    			+rankingNum+"件ランクインしています。最高位は"
				+topRank+"位("+hiScore[0].getScore()+"点)です。 #ためんちゃん http://bit.ly/ST1EfJ";
    	}
    }
    
    private void showLocalHiScore(){
    	showLocalHiScore(RANKING_OUTSIDE);
    }
    
    private void showLocalHiScore(int myRank){
        HiScore[] hiScore = HiScore.readHiScore(this);

        TableLayout tableLayout = (TableLayout)findViewById(R.id.hiscorelist);

        // すでに順位リストが設定されている場合それを削除（削除後の再表示のため）
        int num = tableLayout.getChildCount();
        if(num >= 2){
        	tableLayout.removeViews(1, num-1);
        }
        
        for(int i=0;i<hiScore.length;i++){
        	// 今回ハイスコア登録された順位の場合はマゼンタで表示
        	int textColor = Color.WHITE;
        	if(i == myRank){
        		textColor = Color.MAGENTA;
        	}
        	
        	TableRow tableRow = new TableRow(this);
        	
        	TextView rankView = new TextView(this);
        	rankView.setText((i+1)+"位");
        	rankView.setGravity(Gravity.CENTER);
        	rankView.setTextColor(textColor);

        	TextView nameView = new TextView(this);
        	nameView.setText(hiScore[i].getName());
        	nameView.setTextColor(textColor);
        	
        	TextView scoreView = new TextView(this);
        	scoreView.setText(hiScore[i].getScore()+"点");
        	scoreView.setGravity(Gravity.CENTER);
        	scoreView.setTextColor(textColor);
        	
        	tableRow.addView(rankView);
        	tableRow.addView(nameView); 	
        	tableRow.addView(scoreView);
        	
        	tableLayout.addView(tableRow);
        }
    }
    
    private void showRegisteredHiScore(RegisteredHiScore[] registeredHiScore){
    	
    	if(registeredHiScore == null){
    		return;
    	}
    	
        TableLayout tableLayout = (TableLayout)findViewById(R.id.registeredhiscorelist);
        
        // 順位リストをクリア（削除後の再表示のため）
    	tableLayout.removeAllViews();
    	
    	// 幅を合わせるためヘッダーのTextViewを取得
    	TextView rankHeader = (TextView)findViewById(R.id.headerrank);
    	TextView nameHeader = (TextView)findViewById(R.id.headername);
    	TextView scoreHeader = (TextView)findViewById(R.id.headerscore);
      
        // 自分のデバイスIDを取得
        String deviceId = TamenchanSetting.getDeviceId(this);
        
        for(int i=0;i<registeredHiScore.length;i++){
        	int textColor = Color.WHITE;

        	// ハイスコアが自分のデバイスで出したスコアだったら色を変える
            if(deviceId.equals(registeredHiScore[i].getDevId())){
        		textColor = Color.CYAN;
        	}        	
        	
        	TableRow tableRow = new TableRow(this);
        	
        	TextView rankView = new TextView(this);
        	rankView.setText((i+1)+"位");
        	rankView.setGravity(Gravity.CENTER);
        	rankView.setTextColor(textColor);
        	rankView.setWidth(rankHeader.getWidth());

        	TextView nameView = new TextView(this);
        	nameView.setText(registeredHiScore[i].getName());
        	nameView.setTextColor(textColor);
        	nameView.setWidth(nameHeader.getWidth());
        	
        	TextView scoreView = new TextView(this);
        	scoreView.setText(registeredHiScore[i].getScore()+"点");
        	scoreView.setGravity(Gravity.CENTER);
        	scoreView.setTextColor(textColor);
        	scoreView.setWidth(scoreHeader.getWidth());
        	
        	tableRow.addView(rankView);        	
        	tableRow.addView(nameView);        	
        	tableRow.addView(scoreView);
        	
        	tableLayout.addView(tableRow);
        }
    }
    
    private RegisteredHiScore[] parseRegisteredHiScore(String jsonStr){
    	RegisteredHiScore[] registeredHiScore = null;
    	
    	try {
    		JSONArray jsonArray = new JSONArray(jsonStr);
		
    		registeredHiScore = new RegisteredHiScore[jsonArray.length()];
    		
    		for(int i=0;i<jsonArray.length();i++){
    			JSONObject jsonObj = jsonArray.getJSONObject(i);
    			registeredHiScore[i] = new RegisteredHiScore();
    			registeredHiScore[i].setRegisteredId(jsonObj.getInt("id"));
    			registeredHiScore[i].setName(jsonObj.getString("name"));
    			registeredHiScore[i].setDevId(jsonObj.getString("devid"));
    			registeredHiScore[i].setRank(jsonObj.getInt("rank"));
    			registeredHiScore[i].setScore(jsonObj.getInt("score"));
    			registeredHiScore[i].setAchievedDate(jsonObj.getString("achieved_date"));	    				
    		}
    	} catch (JSONException je){
    		je.printStackTrace();
    	}
    	
		return registeredHiScore;
    }
    
}
