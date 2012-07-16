package com.tatsuo.tamenchan;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.tatsuo.tamenchan.domain.TamenchanScore;

public class ResultActivity extends Activity {
	
	private static final String BUTTON_MENU  = "menu";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        
        TamenchanScore score = (TamenchanScore)getIntent().getSerializableExtra(GameMainActivity.KEY_SCORE);
        
        TextView resultText = (TextView)findViewById(R.id.result);
        resultText.setText(" 得点は "+score.getScore()+" 点でした。");
        
        Button menuButton = (Button)findViewById(R.id.menu);
        menuButton.setTag(BUTTON_MENU);
        menuButton.setOnClickListener(new ButtonClickListener());
    }

    class ButtonClickListener implements OnClickListener {
    	public void onClick(View v){
    		if(BUTTON_MENU.equals(v.getTag())){
    			finish();
    		}
    	}
    }

}
