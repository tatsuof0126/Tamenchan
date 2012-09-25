package com.tatsuo.tamenchan;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.tatsuo.tamenchan.domain.HiScore;

public class HiScoreActivity extends Activity {
	
	private static final String BUTTON_MENU  = "menu";	
	private static final String BUTTON_CLEAR = "clear";	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hiscore);
        
        getHiScoreList();
        
        showHiScore();
        
        Button clearButton = (Button)findViewById(R.id.clearhiscore);
        clearButton.setTag(BUTTON_CLEAR);
        clearButton.setOnClickListener(new ButtonClickListener());
        
        Button menuButton = (Button)findViewById(R.id.menubutton);
        menuButton.setTag(BUTTON_MENU);
        menuButton.setOnClickListener(new ButtonClickListener());        
    }
	
    class ButtonClickListener implements OnClickListener {
    	public void onClick(View v){
    		if(BUTTON_MENU.equals(v.getTag())){
    			finish();
    		} else if(BUTTON_CLEAR.equals(v.getTag())){
    			AlertDialog.Builder dialog
    				= new AlertDialog.Builder(HiScoreActivity.this);
    			dialog.setTitle("ハイスコアのクリア");
    			dialog.setMessage("ハイスコアをクリアします。よろしいですか？");
    			dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
		    			clearHiScore();
		    	        showHiScore();
					}
				});
    			dialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
    			dialog.show();
    		}
    	}
    }
    
    private void getHiScoreList(){
    	HttpClient client = new DefaultHttpClient();
    	HttpUriRequest request = new HttpGet("http://192.168.11.4:3000/hiscorelist");
    	HttpResponse response = null;
        
    	try {
    	    response = client.execute(request);
    	}
    	catch (ClientProtocolException e) {
    		e.printStackTrace();
    	}
    	catch (IOException e){
    		e.printStackTrace();
    	}
    	
    	String json = null;
    	
    	if(response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
    		HttpEntity entity = response.getEntity();
    		try {
    			json = EntityUtils.toString(entity);
    		} catch (ParseException e) {
        		e.printStackTrace();
    	    }
    	    catch (IOException e) {
        		e.printStackTrace();
    	    }
    	    finally {
    	        try {
    	            entity.consumeContent();
    	        }
    	        catch (IOException e) {
    	    		e.printStackTrace();
    	        }
    	    }
    		
    	    Log.i("response",json);
    		
    	}
    	
    	client.getConnectionManager().shutdown();
    	
    	try{
    		JSONArray jsonArray = new JSONArray(json);
    		
    		Log.i("length",""+jsonArray.length());
    		for(int i=0;i<jsonArray.length();i++){
    			JSONObject jsonObj = jsonArray.getJSONObject(i);
    			String name = (String)jsonObj.get("name");
    			Log.i("name"+i, name);
    		}
    		
    		
    	} catch (JSONException e){
    		e.printStackTrace();
    	}
    	
    	
    	
    }
    
    
    
    

    private void showHiScore(){
        HiScore[] hiScore = readHiScore();

        TableLayout tableLayout = (TableLayout)findViewById(R.id.hiscorelist);

        // すでに順位リストが設定されている場合それを削除
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
    
}
