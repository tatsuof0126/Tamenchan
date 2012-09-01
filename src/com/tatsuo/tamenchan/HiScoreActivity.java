package com.tatsuo.tamenchan;

import com.tatsuo.tamenchan.domain.HiScore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HiScoreActivity extends Activity {
	
	private static final String BUTTON_MENU  = "menu";	
	private static final String BUTTON_CLEAR = "clear";	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hiscore);
        
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
