package com.tatsuo.tamenchan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TamenchanMainActivity extends Activity {
    /** Called when the activity is first created. */
	
	private static final String BUTTON_GAMESTART = "gamestart";
	private static final String BUTTON_HISCORE   = "hiscore";
	private static final String BUTTON_OPTION    = "option";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button startButton = (Button)findViewById(R.id.gamestartbutton);
        startButton.setTag(BUTTON_GAMESTART);
        startButton.setOnClickListener(new ButtonClickListener());
        
        Button hiscoreButton = (Button)findViewById(R.id.hiscorebutton);
        hiscoreButton.setTag(BUTTON_HISCORE);
        hiscoreButton.setOnClickListener(new ButtonClickListener());
        
        Button optionButton = (Button)findViewById(R.id.optionbutton);
        optionButton.setTag(BUTTON_OPTION);
        optionButton.setOnClickListener(new ButtonClickListener());
    }
    
    class ButtonClickListener implements OnClickListener {
    	public void onClick(View v){
    		if(BUTTON_GAMESTART.equals(v.getTag())){
    			Intent intent = new Intent(TamenchanMainActivity.this, GameMainActivity.class);
    			startActivity(intent);
    		} else if (BUTTON_HISCORE.equals(v.getTag())){
    			Intent intent = new Intent(TamenchanMainActivity.this, HiScoreTabActivity.class);
    			startActivity(intent);
    		} else if (BUTTON_OPTION.equals(v.getTag())){
    			Intent intent = new Intent(TamenchanMainActivity.this, OptionActivity.class);
    			startActivity(intent);
    		}
    	}
    }
    
}