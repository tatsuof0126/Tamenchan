package com.tatsuo.tamenchan;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tatsuo.tamenchan.domain.TamenchanScore;
import com.tatsuo.tamenchan.domain.TamenchanSetting;
import com.tatsuo.tamenchan.domain.Tehai;
import com.tatsuo.tamenchan.domain.TenpaiChecker;
import com.tatsuo.tamenchan.view.TimerView;

public class GameMainActivity extends Activity {
	private TamenchanScore tamenchanScore = null;
	
	private Tehai tehai = null;
	private int remainingTime = 0;
	private boolean select[] = new boolean[10];
	private boolean judged = false;
	private boolean questionStandBy = false;
	private boolean questionShowing = false;
	
	private Handler handler = new Handler();
    Timer tehaiOpenTimer = null;
    Timer timer = null;
	
	private static final int MAX_QUESTION = 5;
	
	private static int[] MACHI_ARRAY = new int[100];
	// レア待ちと見なす待ち数
	private static final int RARE_MACHI = 7; 
	
	private static int[] BONUS_SCORE = new int[10];
	
	private static final int[] haiImageResourceId = new int[10];
	private static final int[] haiImageId = new int[10];
	private static final int[] tehaiImageId = new int[14];
	
	private static final int ALPHA_SELECTED = 255;
	private static final int ALPHA_NO_SELECTED = 100;
	
	private static final String BUTTON_JUDGE = "judge";
	private static final String BUTTON_MENU  = "menu";
	
	// インテントのキー
	static final String KEY_SCORE = "score";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamemain);
        
        // ゲーム初期化
        tamenchanScore = new TamenchanScore();
        
        // 問題初期化
        makeQuestion();
        
        // リスナーをセット
        for(int i=1;i<=9;i++){
        	ImageView haiImage = (ImageView)findViewById(haiImageId[i]);
        	haiImage.setTag(Integer.valueOf(i));
        	haiImage.setOnClickListener(new HaiClickListener());
        }        
        
        Button judgeButton = (Button)findViewById(R.id.judge);
        judgeButton.setTag(BUTTON_JUDGE);
        judgeButton.setOnClickListener(new ButtonClickListener());
        
        Button menuButton = (Button)findViewById(R.id.menu);
        menuButton.setTag(BUTTON_MENU);
        menuButton.setOnClickListener(new ButtonClickListener());
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	
		if(tehaiOpenTimer != null){
			tehaiOpenTimer.cancel();
			tehaiOpenTimer.purge();
		}
		if(timer != null){
			timer.cancel();
			timer.purge();
		}    	
    }
    
    class HaiClickListener implements OnClickListener {
    	public void onClick(View v){
    		if(judged == true || questionShowing == false){return;}
    		
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
    		if(BUTTON_JUDGE.equals(v.getTag()) && questionShowing == true && judged == false){
    	    	TenpaiChecker checker = new TenpaiChecker();
        		boolean[] machi = checker.checkMachihai(tehai);
    			boolean result = judge(machi, select);
    			
    			String titleStr = "";
    			String messageStr = "";
    			if(result == true){
    				int score = (int)((remainingTime+1000-1) / 1000);
    				
    				int machiNum = 0;
    				for(int i=0;i<machi.length;i++){
    					if(machi[i] == true){machiNum++;}
    				}
    				int bonus = BONUS_SCORE[machiNum];
    				
    				tamenchanScore.setScore(tamenchanScore.getScore()+score+bonus);
    				
    				titleStr = "ためんちゃん！";
       				messageStr = "正解です！  "+score+"点獲得";
       				if(bonus != 0){
       					messageStr += "\nためんちゃんボーナス！ +"+bonus+"点";
       				}       				
    			} else {
    				titleStr = "だめじゃん．．．";
       				messageStr = "正しくは 「"+makeMachiStr(machi)+"」です\n"
       					+"あなたの回答 「"+makeMachiStr(select)+"」";
    			}

    	    	judged = true;
    			
    	    	showAnswer(titleStr, messageStr);
    	    	
    		} else if (BUTTON_MENU.equals(v.getTag())){
    			finish();    			
    		}
    	}
    }
    
    private void makeQuestion(){
    	tamenchanScore.setQuestion(tamenchanScore.getQuestion()+1);
    	judged = false;
    	remainingTime = 20000;
    	
    	TextView headerText = (TextView)findViewById(R.id.header);
    	headerText.setText("Question："+tamenchanScore.getQuestion() + " / " + MAX_QUESTION 
    			+"     Score："+tamenchanScore.getScore());
    	
    	questionStandBy = false;
    	
        // 別スレッドで問題を作成
        new Thread() {
			@Override
			public void run() {
				setTehai();
		        questionStandBy = true;
			}
		}.start();
        
        // 手牌を裏向きで表示
		questionShowing = false;
		for(int i=1;i<=13;i++){
        	ImageView imageView = (ImageView)findViewById(tehaiImageId[i]);
        	imageView.setImageResource(haiImageResourceId[0]);
        }

        // 回答部分を初期化して表示
    	initChoice();
        for(int i=1;i<=9;i++){
        	ImageView haiImage = (ImageView)findViewById(haiImageId[i]);
        	haiImage.setAlpha(ALPHA_NO_SELECTED);
        }
        
        // 残り時間も初期化して表示
        showTimer();
                
        // １問目かつ初回プレイだったらチュートリアルを表示、そうでない場合は問題を表示
        if(tamenchanScore.getQuestion() == 1 && isInitialPlay() == true){
        	showTutorial();
        } else {
        	showTehai();
        }
		
    }
    
    private void nextQuestion(){
    	if(tamenchanScore.getQuestion() < MAX_QUESTION){
    		// まだ問題が残っている場合は次の問題へ
    		makeQuestion();
    	} else {
    		// 問題が終了したら次の画面に遷移する
    		Intent intent = new Intent(this, ResultActivity.class);
    		intent.putExtra(KEY_SCORE, tamenchanScore);
    		startActivity(intent);
    		finish();
    	}
    }
    
    private void setTehai(){
    	tehai = new Tehai();
    	TenpaiChecker checker = new TenpaiChecker();
    	
    	// 何面待ちの問題にするかを決定
    	int questionMachiNum = MACHI_ARRAY[(int)(Math.random()*100)];
    	
		int haipaiCount = 0;
    	while(true){
    		haipaiCount++;
    		
    		tehai.haipai();
    		boolean[] machi = checker.checkMachihai(tehai);
    	
    		int machiNum = 0;
    		for(int i=0;i<machi.length;i++){
    			if(machi[i] == true){
    				machiNum++;
    			}
    		}
    		
    		// 予定の待ち数 または レア待ちになったら、それを問題にする
    		// または10回以上配牌を繰り返して、予定の待ち数にならなかったら
    		// １つ以上待ちがあるものを問題にする
    		if(machiNum == questionMachiNum || machiNum >= RARE_MACHI
    			|| (haipaiCount >= 10 && machiNum > 0) ){
    			
    	    	Log.i("MACHI -> ",questionMachiNum+" : "+machiNum+" : "+haipaiCount);

    	    	// 配牌回数が10回以下の場合は時間稼ぎ
    	    	for(int i=haipaiCount;i<=10;i++){
    	    		checker.checkMachihai(tehai);
    	    	}
    			break;
    		}
    	}
    }
    
    private void showTehai(){
    	// ２秒後に手牌を表向きに表示
    	tehaiOpenTimer = new Timer();
    	tehaiOpenTimer.schedule(new TimerTask(){
        	public void run(){
        		handler.post(new Runnable() {
					@Override
					public void run() {
						// 問題の準備ができてなければ待つ
						while(questionStandBy == false){}
						
				        // 問題の手牌を表示
				        int[] hai = tehai.getTehai();
				        int num = 1;
				        for(int i=1;i<hai.length;i++){
				        	for(int j=0;j<hai[i];j++){
				            	ImageView imageView = (ImageView)findViewById(tehaiImageId[num]);
				            	imageView.setImageResource(haiImageResourceId[i]);
				            	num++;
				        	}
				        }
						questionShowing = true;

						// タイマーをスタート
						startTimer();
					}
        		});
        	}
        }, 2000);
    }
    
    private void startTimer(){
    	timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {							
			@Override
			public void run() {
        		handler.post(new Runnable() {
					@Override
					public void run() {
						if(judged == false){
							remainingTime-=200;
							showTimer();
							if(remainingTime <= 0){
								judged = true;
								TenpaiChecker checker = new TenpaiChecker();
								boolean[] machi = checker.checkMachihai(tehai);
								showAnswer("時間切れ！" , "正解は「"+makeMachiStr(machi)+"」です");
							}
						}
					}
        		});
			}
		}, 200, 200);
    }
    
    private void showTimer(){
    	TimerView timerView = (TimerView)findViewById(R.id.timer);
    	timerView.updateTimerView(remainingTime);
    }
    
    private void showAnswer(String titleStr, String messageStr){
    	tehaiOpenTimer.cancel();
    	tehaiOpenTimer.purge();
    	timer.cancel();
    	timer.purge();
    	
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
    }
    
    private void showTutorial(){
		AlertDialog.Builder dialog
			= new AlertDialog.Builder(GameMainActivity.this);

		dialog.setTitle("ためんちゃんの遊び方");
		dialog.setMessage("上に表示される手牌の待ち牌を選択して「判定」ボタンを押してください。");
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showTehai();
			}
		});
		dialog.show();
    }

    private void initChoice(){
    	for(int i=0;i<select.length;i++){
    		select[i] = false;
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
    
    private boolean isInitialPlay(){
    	boolean initPlay = TamenchanSetting.isInitialPlay(this);
    	
    	if(initPlay == true){
    		TamenchanSetting.setInitialPlay(this, false);
    	}
    	
    	return initPlay;
    }
    
	{
		// 問題の待ち数の確率表（１つが１％）
		MACHI_ARRAY = new int[]{
				0,0,0,1,1,1,1,1,1,1,
				1,1,1,1,1,1,1,1,1,1,
				1,1,1,1,1,2,2,2,2,2,
				2,2,2,2,2,2,2,2,2,2,
				2,2,2,2,2,2,2,2,2,2,
				3,3,3,3,3,3,3,3,3,3,
				3,3,3,3,3,3,3,3,3,3,
				3,3,3,3,3,4,4,4,4,4,
				4,4,4,4,4,4,4,4,4,4,
				5,5,5,5,5,5,5,6,6,6};
		
		BONUS_SCORE = new int[]{0,0,0,3,5,7,10,10,15,20};
		
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
