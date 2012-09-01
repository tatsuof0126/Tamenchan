package com.tatsuo.tamenchan;

import java.util.Date;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.tatsuo.tamenchan.domain.HiScore;
import com.tatsuo.tamenchan.domain.TamenchanScore;

public class ResultActivity extends Activity {
	
	private static final String BUTTON_MENU  = "menu";
	
	private static final int RANKING_OUTSIDE = 999;
	private static final int MY_NAME_EDITTEXT = 999;
	
	HiScore[] hiScore = null;
	int myRank = RANKING_OUTSIDE;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        
        TamenchanScore score = (TamenchanScore)getIntent().getSerializableExtra(GameMainActivity.KEY_SCORE);
        
        TextView resultText = (TextView)findViewById(R.id.resultmessage);
        resultText.setText(" 得点は "+score.getScore()+" 点でした");
        
        hiScore = readHiScore();
        myRank = checkHiScore(hiScore, score);
        
    	TextView textView = (TextView)findViewById(R.id.rankinmessage);
        if(myRank != RANKING_OUTSIDE){
        	textView.setText("  "+(myRank+1)+"位になりました。\n  名前を入力してください。"); 
        } else {
        	// ランクインしなかったときはランクインメッセージのTextViewを削除
        	((ViewGroup)textView.getParent()).removeView(textView);
        }
        
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
        
        Button menuButton = (Button)findViewById(R.id.menu);
        menuButton.setTag(BUTTON_MENU);
        menuButton.setOnClickListener(new ButtonClickListener());
        if(myRank != RANKING_OUTSIDE){
        	menuButton.setText(" 保存して戻る ");
        }
    }

    class ButtonClickListener implements OnClickListener {
    	public void onClick(View v){
    		if(BUTTON_MENU.equals(v.getTag())){
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
    				writeHiScore(hiScore);
    			}
    			
    			finish();
    		}
    	}
    }
    
    private HiScore[] readHiScore(){
    	SharedPreferences preferences
    		= getSharedPreferences(HiScore.HISCORE_PREF_NAME, MODE_PRIVATE);
    	
    	return HiScore.readHiScore(preferences);
    }
    
    private void writeHiScore(HiScore[] hiScore){
    	SharedPreferences preferences
    		= getSharedPreferences(HiScore.HISCORE_PREF_NAME, MODE_PRIVATE);
    	
    	HiScore.writeHiScore(preferences, hiScore);    	
    }
    
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
