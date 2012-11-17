package com.tatsuo.tamenchan;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tatsuo.mahjonglib.Tehai;
import com.tatsuo.mahjonglib.TenpaiChecker;
import com.tatsuo.tamenchan.domain.TamenchanDefine;
import com.tatsuo.tamenchan.domain.TamenchanScore;
import com.tatsuo.tamenchan.domain.TamenchanSetting;
import com.tatsuo.tamenchan.view.TimerView;

public class GameMainActivity extends Activity {
	private TamenchanScore tamenchanScore = null;
	
	private Tehai tehai = null;
	private int remainingTime = 0;
	private boolean select[] = new boolean[10];
	private boolean judged = false;
	private boolean questionStandBy = false;
	private boolean questionShowing = false;
	private int gameLevel = TamenchanDefine.GAMELEVEL_LOW;
	private int haiType = 0;
	
	private Handler handler = new Handler();
    Timer tehaiOpenTimer = null;
    Timer timer = null;
	
	private static final int MAX_QUESTION = 5;
	
	private static int[] MACHI_ARRAY = new int[100];
	// レア待ちと見なす待ち数
	private static final int RARE_MACHI = 7; 
	
	private static int[] BONUS_SCORE = new int[10];
	
	private static final int[] haiImageId = new int[10];
	private static final int[] tehaiImageId = new int[14];
	private static final int[][][] haiImageResourceId = new int[3][10][2];
	private static final int[][] kotsuhaiImageResourceId = new int[8][2];
	private static final int urahaiImageResourceId = R.drawable.bk;
	
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
        
        gameLevel = TamenchanSetting.getGameLevel(this);
        haiType = TamenchanSetting.getHaiType(this);
        setHaiImageResource();
        
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
    
    private void setHaiImageResource(){
    	for(int i=1;i<=9;i++){
    		ImageView haiImage = (ImageView)findViewById(haiImageId[i]);
    		haiImage.setImageResource(haiImageResourceId[haiType][i][0]);
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
    	
    	int gamelevel = TamenchanSetting.getGameLevel(this);
    	TextView headerText = (TextView)findViewById(R.id.header);
    	headerText.setText("<"+TamenchanDefine.GAME_LEVEL[gamelevel]+">"
    			+"  問題："+tamenchanScore.getQuestion() + " / " + MAX_QUESTION 
    			+"    得点："+tamenchanScore.getScore());
    	
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
        	imageView.setImageResource(urahaiImageResourceId);
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
    		
    		int[] presethai = new int[10];
    		// 初級の場合は字牌の暗刻を一つ加える。
    		if(gameLevel == TamenchanDefine.GAMELEVEL_LOW){
        		presethai[0] = 3;
        		haiImageResourceId[haiType][0] = kotsuhaiImageResourceId[(int)(Math.random()*7)+1];
    		}
    		
    		tehai.haipai(presethai);
    		
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
    			
//    	    	Log.i("MACHI -> ",questionMachiNum+" : "+machiNum+" : "+haipaiCount);

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
						
				        // Listに数をセット
				        int[] hai = tehai.getTehai();
				        ArrayList<Integer> haiList = new ArrayList<Integer>();
				        for(int i=1;i<hai.length;i++){
				        	for(int j=0;j<hai[i];j++){
				        		haiList.add(Integer.valueOf(i));
				        	}
				        }
				        for(int i=0;i<hai[0];i++){
			        		haiList.add(Integer.valueOf(0));
				        }
				        
				        // 問題の手牌を表示
				        if(gameLevel != TamenchanDefine.GAMELEVEL_HIGH){
				        	// 初級/中級の場合
				        	for(int i=0;i<haiList.size();i++){
				        		int haiNum = haiList.get(i).intValue();
				        		ImageView imageView = (ImageView)findViewById(tehaiImageId[i+1]);
				        		imageView.setImageResource(haiImageResourceId[haiType][haiNum][0]);
				        	}
				        } else {
				        	// 上級の場合（理牌なし）
				        	for(int i=0;i<13;i++){
				        		int num = (int)(Math.random()*haiList.size());
				        		int haiNum = haiList.remove(num).intValue();
				        		
				        		// int side = (int)(Math.random()*2);
				        		int side = 0; // 上下逆はさすがに難しすぎ＆画像がずれるので、当面は上下逆にはしない。
				        		
				        		ImageView imageView = (ImageView)findViewById(tehaiImageId[i+1]);
				        		imageView.setImageResource(haiImageResourceId[haiType][haiNum][side]);
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
		
		haiImageResourceId[0][0][0] = 0;
		haiImageResourceId[0][1][0] = R.drawable.m1;
		haiImageResourceId[0][2][0] = R.drawable.m2;
		haiImageResourceId[0][3][0] = R.drawable.m3;
		haiImageResourceId[0][4][0] = R.drawable.m4;
		haiImageResourceId[0][5][0] = R.drawable.m5;
		haiImageResourceId[0][6][0] = R.drawable.m6;
		haiImageResourceId[0][7][0] = R.drawable.m7;
		haiImageResourceId[0][8][0] = R.drawable.m8;
		haiImageResourceId[0][9][0] = R.drawable.m9;
		haiImageResourceId[0][0][1] = 0;
		haiImageResourceId[0][1][1] = R.drawable.m1u;
		haiImageResourceId[0][2][1] = R.drawable.m2u;
		haiImageResourceId[0][3][1] = R.drawable.m3u;
		haiImageResourceId[0][4][1] = R.drawable.m4u;
		haiImageResourceId[0][5][1] = R.drawable.m5u;
		haiImageResourceId[0][6][1] = R.drawable.m6u;
		haiImageResourceId[0][7][1] = R.drawable.m7u;
		haiImageResourceId[0][8][1] = R.drawable.m8u;
		haiImageResourceId[0][9][1] = R.drawable.m9u;
		haiImageResourceId[1][0][0] = 0;
		haiImageResourceId[1][1][0] = R.drawable.p1;
		haiImageResourceId[1][2][0] = R.drawable.p2;
		haiImageResourceId[1][3][0] = R.drawable.p3;
		haiImageResourceId[1][4][0] = R.drawable.p4;
		haiImageResourceId[1][5][0] = R.drawable.p5;
		haiImageResourceId[1][6][0] = R.drawable.p6;
		haiImageResourceId[1][7][0] = R.drawable.p7;
		haiImageResourceId[1][8][0] = R.drawable.p8;
		haiImageResourceId[1][9][0] = R.drawable.p9;
		haiImageResourceId[1][0][1] = 0;
		haiImageResourceId[1][1][1] = R.drawable.p1u;
		haiImageResourceId[1][2][1] = R.drawable.p2u;
		haiImageResourceId[1][3][1] = R.drawable.p3u;
		haiImageResourceId[1][4][1] = R.drawable.p4u;
		haiImageResourceId[1][5][1] = R.drawable.p5u;
		haiImageResourceId[1][6][1] = R.drawable.p6u;
		haiImageResourceId[1][7][1] = R.drawable.p7u;
		haiImageResourceId[1][8][1] = R.drawable.p8u;
		haiImageResourceId[1][9][1] = R.drawable.p9u;
		haiImageResourceId[2][0][0] = 0;
		haiImageResourceId[2][1][0] = R.drawable.s1;
		haiImageResourceId[2][2][0] = R.drawable.s2;
		haiImageResourceId[2][3][0] = R.drawable.s3;
		haiImageResourceId[2][4][0] = R.drawable.s4;
		haiImageResourceId[2][5][0] = R.drawable.s5;
		haiImageResourceId[2][6][0] = R.drawable.s6;
		haiImageResourceId[2][7][0] = R.drawable.s7;
		haiImageResourceId[2][8][0] = R.drawable.s8;
		haiImageResourceId[2][9][0] = R.drawable.s9;
		haiImageResourceId[2][0][1] = 0;
		haiImageResourceId[2][1][1] = R.drawable.s1u;
		haiImageResourceId[2][2][1] = R.drawable.s2u;
		haiImageResourceId[2][3][1] = R.drawable.s3u;
		haiImageResourceId[2][4][1] = R.drawable.s4u;
		haiImageResourceId[2][5][1] = R.drawable.s5u;
		haiImageResourceId[2][6][1] = R.drawable.s6u;
		haiImageResourceId[2][7][1] = R.drawable.s7u;
		haiImageResourceId[2][8][1] = R.drawable.s8u;
		haiImageResourceId[2][9][1] = R.drawable.s9u;

		kotsuhaiImageResourceId[0][0] = 0;
		kotsuhaiImageResourceId[1][0] = R.drawable.j1;
		kotsuhaiImageResourceId[2][0] = R.drawable.j2;
		kotsuhaiImageResourceId[3][0] = R.drawable.j3;
		kotsuhaiImageResourceId[4][0] = R.drawable.j4;
		kotsuhaiImageResourceId[5][0] = R.drawable.j5;
		kotsuhaiImageResourceId[6][0] = R.drawable.j6;
		kotsuhaiImageResourceId[7][0] = R.drawable.j7;
		kotsuhaiImageResourceId[0][1] = 0;
		kotsuhaiImageResourceId[1][1] = R.drawable.j1u;
		kotsuhaiImageResourceId[2][1] = R.drawable.j2u;
		kotsuhaiImageResourceId[3][1] = R.drawable.j3u;
		kotsuhaiImageResourceId[4][1] = R.drawable.j4u;
		kotsuhaiImageResourceId[5][1] = R.drawable.j5u;
		kotsuhaiImageResourceId[6][1] = R.drawable.j6u;
		kotsuhaiImageResourceId[7][1] = R.drawable.j7u;
	}
	
}
