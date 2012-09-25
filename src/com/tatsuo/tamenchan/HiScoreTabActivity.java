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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.tatsuo.tamenchan.domain.HiScore;
import com.tatsuo.tamenchan.domain.RegisteredHiScore;
import com.tatsuo.tamenchan.domain.TamenchanDefine;
import com.tatsuo.tamenchan.domain.TamenchanSetting;

public class HiScoreTabActivity extends TabActivity {
	
	private static final String TAB1 = "tab1";
	private static final String TAB2 = "tab2";
	
	private static final String BUTTON_MENU   = "menu";
	private static final String BUTTON_CLEAR  = "clear";
	private static final String BUTTON_POST   = "post";
	private static final String BUTTON_RELOAD = "reloadhiscore";
	
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
		
        showLocalHiScore();
        
        Button clearButton = (Button)findViewById(R.id.clearhiscore);
        clearButton.setTag(BUTTON_CLEAR);
        clearButton.setOnClickListener(new ButtonClickListener());
        
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
		
	}

    class ButtonClickListener implements OnClickListener {
    	public void onClick(View v){
    		if(BUTTON_MENU.equals(v.getTag())){
    			finish();
    		} else if(BUTTON_CLEAR.equals(v.getTag())){
    			AlertDialog.Builder dialog
    				= new AlertDialog.Builder(HiScoreTabActivity.this);
    			dialog.setTitle("ハイスコアのクリア");
    			dialog.setMessage("ハイスコアをクリアします。よろしいですか？");
    			dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
		    			clearHiScore();
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
//			HttpUriRequest request = new HttpGet("http://192.168.11.4:3000/hiscorelist");
	    	HttpUriRequest request = new HttpGet(TamenchanDefine.SERVER_URI + TamenchanDefine.HISCORELIST_PATH);
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
//	    	HttpPost request = new HttpPost("http://192.168.11.4:3000/hiscorelist");
	    	HttpPost request = new HttpPost(TamenchanDefine.SERVER_URI + TamenchanDefine.HISCORELIST_PATH);
	    	HttpResponse response = null;
	    	HttpEntity entity = null;
	    	String jsonStr = null;
	        
	    	try {
	    		HiScore[] hiScore = readHiScore();
	    		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    		
	    		for(int i=0;i<hiScore.length;i++){
	    			if(hiScore[i].getRegisteredId() != HiScore.DEFAULT_REGISTERED_ID){
	    				params.add(new BasicNameValuePair("id"+i, Integer.toString(hiScore[i].getRegisteredId())));
	    			}
		    		params.add(new BasicNameValuePair("devid"+i, getDeviceId()));
		    		params.add(new BasicNameValuePair("name"+i, hiScore[i].getName()));
		    		params.add(new BasicNameValuePair("score"+i, Integer.toString(hiScore[i].getScore())));
		    		params.add(new BasicNameValuePair("date"+i, hiScore[i].getDateStr()));
	    		}
	    		
	    		request.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
	    		
	    		response = client.execute(request);
	    		
		    	if(response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
		    		entity = response.getEntity();
		    		jsonStr = EntityUtils.toString(entity);
	    			
		    		Log.i("JSON", jsonStr);
		    		
		    		registeredHiScore = parseRegisteredHiScore(jsonStr);
		    		
		    		for(int i=0;i<hiScore.length;i++){
		    			hiScore[i].setRegisteredId(registeredHiScore[i].getRegisteredId());
		    		}
		    		writeScore(hiScore);
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
	    		int topRank = 999;
	    		int rankingNum = 0;
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
//	    		resultDialog.setTitle("");
	    		String messageText = "";
	    		if(rankingNum != 0){
	    			messageText = "登録されました。\nみんなのハイスコアに "
	    					+rankingNum+"件 ランクインしています。\n最高位は "+topRank+"位 です。";
	    		} else {
	    			messageText = "登録しました。\nみんなのハイスコアへのランクインはありません。";
	    		}
    			resultDialog.setMessage(messageText);
	    		resultDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    			@Override
	    			public void onClick(DialogInterface dialog, int which) {
	    			}
	    		});
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
	    	
			// getTabHost().setCurrentTabByTag(TAB2);
	    }
    }
    
    private void showLocalHiScore(){
        HiScore[] hiScore = readHiScore();

        TableLayout tableLayout = (TableLayout)findViewById(R.id.hiscorelist);

        // すでに順位リストが設定されている場合それを削除（削除後の再表示のため）
        int num = tableLayout.getChildCount();
        if(num >= 2){
        	tableLayout.removeViews(1, num-1);
        }
        
        for(int i=0;i<hiScore.length;i++){
        	TableRow tableRow = new TableRow(this);
        	
        	TextView rankView = new TextView(this);
        	rankView.setText((i+1)+"位");
        	rankView.setGravity(Gravity.CENTER);

        	TextView nameView = new TextView(this);
        	nameView.setText(hiScore[i].getName());        	
        	
        	TextView scoreView = new TextView(this);
        	scoreView.setText(hiScore[i].getScore()+"点");
        	scoreView.setGravity(Gravity.CENTER);
        	
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
        String deviceId = getDeviceId();
        
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
    
    private void writeScore(HiScore[] hiScore){
    	SharedPreferences preferences
    		= getSharedPreferences(HiScore.HISCORE_PREF_NAME, MODE_PRIVATE);
    	
    	HiScore.writeHiScore(preferences, hiScore);
    }
    
    
    private HiScore[] readHiScore(){
    	SharedPreferences preferences
    		= getSharedPreferences(HiScore.HISCORE_PREF_NAME, MODE_PRIVATE);
    	
    	return HiScore.readHiScore(preferences);
    }
    
    private void clearHiScore(){
    	SharedPreferences preferences
			= getSharedPreferences(HiScore.HISCORE_PREF_NAME, MODE_PRIVATE);
	
    	HiScore.clearHiScore(preferences);
    }
    
    private String getDeviceId(){
    	SharedPreferences preferences
			= getSharedPreferences(TamenchanSetting.SETTING_PREF_NAME, MODE_PRIVATE);

    	return TamenchanSetting.getDeviceId(preferences);
    }
    
}
