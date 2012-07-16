package com.tatsuo.tamenchan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tatsuo.tamenchan.domain.TamenchanScore;
import com.tatsuo.tamenchan.domain.Tehai;
import com.tatsuo.tamenchan.domain.TenpaiChecker;

public class GameMainActivity extends Activity {
	private TamenchanScore score = null;
	
	private Tehai tehai = null;
	private boolean select[] = new boolean[10];
	private boolean judged = false;
	
	private static final int MAX_QUESTION = 5;
	
	private static final int[] haiImageResourceId = new int[10];
	private static final int[] haiImageId = new int[10];
	private static final int[] tehaiImageId = new int[14];
	
	private static final int ALPHA_SELECTED = 255;
	private static final int ALPHA_NO_SELECTED = 100;
	private static final String BUTTON_JUDGE = "judge";
	private static final String BUTTON_MENU  = "menu";
	
	public static final String KEY_SCORE = "score";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamemain);
        
        // ゲーム初期化
        score = new TamenchanScore();
        
        // 問題初期化
        makeQuestion();
        
        // ビューの固定部分
        for(int i=1;i<=9;i++){
        	ImageView haiImage = (ImageView)findViewById(haiImageId[i]);
        	haiImage.setTag(new Integer(i));
        	haiImage.setOnClickListener(new HaiClickListener());
        }        
        
        Button judgeButton = (Button)findViewById(R.id.judge);
        judgeButton.setTag(BUTTON_JUDGE);
        judgeButton.setOnClickListener(new ButtonClickListener());
        
        Button menuButton = (Button)findViewById(R.id.menu);
        menuButton.setTag(BUTTON_MENU);
        menuButton.setOnClickListener(new ButtonClickListener());
    }
    
    class HaiClickListener implements OnClickListener {
    	public void onClick(View v){
    		if(judged == true){return;}
    		
    		int haiNum = ((Integer)v.getTag()).intValue();
        	ImageView haiImage = (ImageView)findViewById(haiImageId[haiNum]);
        	
        	if(select[haiNum] == true){
        		select[haiNum] = false;
            	haiImage.setAlpha(ALPHA_NO_SELECTED);
        	} else {
        		select[haiNum] = true;
            	haiImage.setAlpha(ALPHA_SELECTED);
        	}
    	}
    }
        
    class ButtonClickListener implements OnClickListener {
    	public void onClick(View v){
    		if(BUTTON_JUDGE.equals(v.getTag())){
    			Log.i("BUTTON",BUTTON_JUDGE);
    			
    	    	TenpaiChecker checker = new TenpaiChecker();
        		boolean[] machi = checker.checkMachihai(tehai);
    			boolean result = judge(machi, select);
    			
    			String titleStr = "";
    			String messageStr = "";
    			if(result == true){
    				if(judged == false){
    					score.setScore(score.getScore()+1);
    				}
    				titleStr = "ためんちゃん！";
       				messageStr = "正解です";
    			} else {
    				titleStr = "だめじゃん！";
       				messageStr = "正しくは 「"+makeMachiStr(machi)+"」です\n"
       				+"あなたの回答 「"+makeMachiStr(select)+"」";
    			}

    	    	judged = true;
    			
    			AlertDialog.Builder dialog
    				= new AlertDialog.Builder(GameMainActivity.this);
    			
    			dialog.setTitle(titleStr);
    			dialog.setMessage(messageStr);
    			dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						nextQuestion();
					}
				});
    			dialog.show();
    		} else if (BUTTON_MENU.equals(v.getTag())){
    			finish();    			
    		}
    	}
    }
    
    private boolean judge(boolean[] machi, boolean[] select){
    	for(int i=0;i<machi.length;i++){
    		if(machi[i] != select[i]){
    			return false;
    		}
    	}    	
    	return true;
    }
    
    private void nextQuestion(){
    	if(score.getQuestion() < MAX_QUESTION){
    		makeQuestion();
    	} else {
    		Intent intent = new Intent(this, ResultActivity.class);
    		intent.putExtra(KEY_SCORE, score);
    		startActivity(intent);
    		finish();
    	}
    }
    
    private void makeQuestion(){
    	score.setQuestion(score.getQuestion()+1);
    	judged = false;
    	
    	TextView headerText = (TextView)findViewById(R.id.header);
    	headerText.setText("Question："+score.getQuestion() + " / " + MAX_QUESTION 
    			+"     Score："+score.getScore());
    	
    	initChoice();
        for(int i=1;i<=9;i++){
        	ImageView haiImage = (ImageView)findViewById(haiImageId[i]);
        	haiImage.setAlpha(ALPHA_NO_SELECTED);
        }
               
        setTehai();    	
        int[] hai = tehai.getTehai();
        int num = 1;
        for(int i=1;i<hai.length;i++){
        	for(int j=0;j<hai[i];j++){
            	ImageView imageView = (ImageView)findViewById(tehaiImageId[num]);
            	imageView.setImageResource(haiImageResourceId[i]);
            	num++;
        	}
        }
    }
    
    private void setTehai(){
    	tehai = new Tehai();
    	TenpaiChecker checker = new TenpaiChecker();
    	
    	while(true){
    		tehai.haipai();
    		boolean[] machi = checker.checkMachihai(tehai);
    	
    		int num = 0;
    		for(int i=0;i<machi.length;i++){
    			if(machi[i] == true){
    				num++;
    			}
    		}
    		
    		if(num > 1){
    			break;
    		}
    	}
    }
    
    private void initChoice(){
    	for(int i=0;i<select.length;i++){
    		select[i] = false;
    	}
    }
    
    private String makeMachiStr(boolean[] machi){
		String machiStr = "";
		for(int i=0;i<machi.length;i++){
			if(machi[i] == true){
				if("".equals(machiStr)){
					machiStr += ""+i;
				} else {
					machiStr += ","+i;
				}
			}
		}
		
		if("".equals(machiStr)){
			machiStr += "待ちなし";			
		} else {
			machiStr += " 待ち";
		}		
    	
		return machiStr;
    }
    
	{
		tehaiImageId[0] = 0;
		tehaiImageId[1] = R.id.tehai1;
		tehaiImageId[2] = R.id.tehai2;
		tehaiImageId[3] = R.id.tehai3;
		tehaiImageId[4] = R.id.tehai4;
		tehaiImageId[5] = R.id.tehai5;
		tehaiImageId[6] = R.id.tehai6;
		tehaiImageId[7] = R.id.tehai7;
		tehaiImageId[8] = R.id.tehai8;
		tehaiImageId[9] = R.id.tehai9;
		tehaiImageId[10] = R.id.tehai10;
		tehaiImageId[11] = R.id.tehai11;
		tehaiImageId[12] = R.id.tehai12;
		tehaiImageId[13] = R.id.tehai13;		
		
		haiImageId[0] = 0;
		haiImageId[1] = R.id.hai1;
		haiImageId[2] = R.id.hai2;
		haiImageId[3] = R.id.hai3;
		haiImageId[4] = R.id.hai4;
		haiImageId[5] = R.id.hai5;
		haiImageId[6] = R.id.hai6;
		haiImageId[7] = R.id.hai7;
		haiImageId[8] = R.id.hai8;
		haiImageId[9] = R.id.hai9;
		
		haiImageResourceId[0] = R.drawable.bk;
		haiImageResourceId[1] = R.drawable.m1;
		haiImageResourceId[2] = R.drawable.m2;
		haiImageResourceId[3] = R.drawable.m3;
		haiImageResourceId[4] = R.drawable.m4;
		haiImageResourceId[5] = R.drawable.m5;
		haiImageResourceId[6] = R.drawable.m6;
		haiImageResourceId[7] = R.drawable.m7;
		haiImageResourceId[8] = R.drawable.m8;
		haiImageResourceId[9] = R.drawable.m9;
	}
	
}
