package com.tatsuo.tamenchan;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.tatsuo.tamenchan.domain.HiScore;
import com.tatsuo.tamenchan.domain.TamenchanScore;

public class ResultActivity extends Activity {
	
	private static final String BUTTON_SAVE  = "save";
	private static final String BUTTON_SAVE_AND_TWEET  = "saveandtweet";
	
	private static final int RANKING_OUTSIDE = 999;
	private static final int MY_NAME_EDITTEXT = 999;

	// インテントのキー
	static final String KEY_RANK = "rank";
	static final String KEY_MODE = "mode";
	
	static final String MODE_TWEET = "tweet";
	
	private HiScore[] hiScore = null;
	private int myRank = RANKING_OUTSIDE;

/*	
    private OAuthAuthorization twitterOauth;
    private RequestToken requestToken;	
    private AccessToken accessToken;
*/
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        
        TamenchanScore tamenchanScore = (TamenchanScore)getIntent().getSerializableExtra(GameMainActivity.KEY_SCORE);
        
        hiScore = HiScore.readHiScore(this);
        myRank = checkHiScore(hiScore, tamenchanScore);
        
        if(myRank == RANKING_OUTSIDE){
        	// ハイスコアにならなかった場合はハイスコア画面にそのまま遷移
    		Intent intent = new Intent(this, HiScoreTabActivity.class);
    		intent.putExtra(GameMainActivity.KEY_SCORE, tamenchanScore);
    		startActivity(intent);
    		finish();
    		return;
        }
        
        TextView resultText = (TextView)findViewById(R.id.resultmessage);
        resultText.setText(" 得点は "+tamenchanScore.getScore()+" 点でした");
        
    	TextView textView = (TextView)findViewById(R.id.rankinmessage);
        textView.setText("  "+(myRank+1)+"位になりました。\n  名前を入力してください。");
        
        TableLayout tableLayout = (TableLayout)findViewById(R.id.resulthiscorelist);
        for(int i=0;i<hiScore.length;i++){
        	TableRow tableRow = new TableRow(this);
        	
        	TextView rankView = new TextView(this);
        	rankView.setText((i+1)+"位");
        	rankView.setGravity(Gravity.CENTER);

        	TextView nameView = new TextView(this);
        	if(i == myRank){
        		nameView = new EditText(this);
        		nameView.setId(MY_NAME_EDITTEXT);
        	} else {
        		nameView = new TextView(this);
        	}
        	nameView.setText(hiScore[i].getName());        	
        	
        	TextView scoreView = new TextView(this);
        	scoreView.setText(hiScore[i].getScore()+"点");
        	scoreView.setGravity(Gravity.CENTER);
        	
        	tableRow.addView(rankView);        	
        	tableRow.addView(nameView);        	
        	tableRow.addView(scoreView);
        	
        	tableLayout.addView(tableRow);
        }
        
        Button saveTweetButton = (Button)findViewById(R.id.saveandtweet);
        saveTweetButton.setTag(BUTTON_SAVE_AND_TWEET);
        saveTweetButton.setOnClickListener(new ButtonClickListener());

        Button saveButton = (Button)findViewById(R.id.save);
        saveButton.setTag(BUTTON_SAVE);
        saveButton.setOnClickListener(new ButtonClickListener());
    }
    
    class ButtonClickListener implements OnClickListener {
    	public void onClick(View v){
    		if(BUTTON_SAVE.equals(v.getTag()) || BUTTON_SAVE_AND_TWEET.equals(v.getTag())){
				// 入力された内容でハイスコアを書き換え
    			if(myRank != RANKING_OUTSIDE){
    				EditText editText = (EditText)findViewById(MY_NAME_EDITTEXT);
    				String myName = editText.getText().toString();
    				
    				if(myName == null || "".equals(myName)){
    					return;
    				}
    				if(myName.length() > 10){
    					Toast.makeText(ResultActivity.this, "名前は10文字以内で入力してください", Toast.LENGTH_SHORT).show();
    					return;
    				}
    				
        			hiScore[myRank].setName(myName);    				
    				HiScore.writeHiScore(ResultActivity.this, hiScore);
    			}
    		}
    		
    		if(BUTTON_SAVE.equals(v.getTag())){
        		Intent intent = new Intent(ResultActivity.this, HiScoreTabActivity.class);
        		intent.putExtra(KEY_RANK, myRank);
        		startActivity(intent);
        		finish();
    		} else if (BUTTON_SAVE_AND_TWEET.equals(v.getTag())){
        		Intent intent = new Intent(ResultActivity.this, HiScoreTabActivity.class);
        		intent.putExtra(KEY_RANK, myRank);
        		intent.putExtra(KEY_MODE, MODE_TWEET);
        		startActivity(intent);
        		finish();
/* 
 	         	ConfigurationBuilder builder = new ConfigurationBuilder();

    	        builder.setOAuthConsumerKey(TamenchanDefine.CONSUMER_KEY);
    	        builder.setOAuthConsumerSecret(TamenchanDefine.CONSUMER_SECRET);
    	        
    	        Configuration configuration = builder.build();
    	 
    	        twitterOauth = new OAuthAuthorization(configuration);
    	        twitterOauth.setOAuthAccessToken(null);
    	        
    	        try {
    	        	requestToken = twitterOauth.getOAuthRequestToken(TamenchanDefine.CALLBACK_URL);
    	        } catch (TwitterException e) {
    	        	e.printStackTrace();
    	        }
    	 
    	        Intent intent = new Intent(ResultActivity.this, TwitterLoginActivity.class);
    	        intent.putExtra("auth_url", requestToken.getAuthorizationURL());    	 
    	        startActivityForResult(intent, 1);
*/
    		}
    	}
    }
   
/*    
    @Override
    protected void onActivityResult(int requestCode,
    		int resultCode, Intent intent) {
    	try{
    		accessToken = twitterOauth.getOAuthAccessToken(
    				requestToken, intent.getExtras().getString(TamenchanDefine.PARAM_OAUTH_VERIFIER));
    	} catch (TwitterException te){
    		te.printStackTrace();
    	}
    	
    	Log.i(TamenchanDefine.SUB_KEY_OAUTH_TOKEN, accessToken.getToken());
    	Log.i(TamenchanDefine.SUB_KEY_OAUTH_TOKEN_SECRET, accessToken.getTokenSecret());
    	
    	String oauthToken       = accessToken.getToken();
    	String oauthTokenSecret = accessToken.getTokenSecret();
    	
    	ConfigurationBuilder builder = new ConfigurationBuilder();
    	builder.setOAuthConsumerKey(TamenchanDefine.CONSUMER_KEY);
    	builder.setOAuthConsumerSecret(TamenchanDefine.CONSUMER_SECRET);
    	builder.setOAuthAccessToken(oauthToken);
    	builder.setOAuthAccessTokenSecret(oauthTokenSecret);

    	Configuration config = builder.build();
    	
    	Twitter twitter = new TwitterFactory(config).getInstance();

    	try {
    		twitter.updateStatus("Tweet from Tamenchan App");
    	} catch (TwitterException te){
    		te.printStackTrace();
    	}

    	
    	finish();
    }
*/    
    private int checkHiScore(HiScore[] hiScore, TamenchanScore score){
    	int rank = RANKING_OUTSIDE;
    	
    	int num = 0;
    	while(num < hiScore.length){
    		if(hiScore[num].getScore() <= score.getScore()){
    			// デフォルトの名前を一番最近入力したハイスコアにする
    			String defaultName = "";
    			long latestDate = -1L;
    			for(int i=0;i<hiScore.length;i++){
    				if(hiScore[i].getDate() > latestDate){
    					defaultName = hiScore[i].getName();
    					latestDate = hiScore[i].getDate();
    				}
    			}
    			
    			// 今までのランキングを１つずつ落とす
    			for(int i=hiScore.length-1;i>=num+1;i--){
    				hiScore[i] = hiScore[i-1];
    			}
    			hiScore[num] = new HiScore(defaultName, score.getScore(), new Date().getTime());
    			rank = num;
    			break;
    		}
    		num++;
    	}
    	
    	return rank;
    }

}
