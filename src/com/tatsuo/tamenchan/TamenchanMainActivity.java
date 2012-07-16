package com.tatsuo.tamenchan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TamenchanMainActivity extends Activity {
    /** Called when the activity is first created. */
	
	private static final String GAMESTART = "gamestart";
	private static final String OPTION = "option";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button startButton = (Button)findViewById(R.id.gamestart);
        startButton.setTag(GAMESTART);
        startButton.setOnClickListener(new ButtonClickListener());
        
        Button optionButton = (Button)findViewById(R.id.option);
        optionButton.setTag(OPTION);
        optionButton.setOnClickListener(new ButtonClickListener());
    }
    
    class ButtonClickListener implements OnClickListener {
    	public void onClick(View v){
    		if(GAMESTART.equals(v.getTag())){
    			Intent intent = new Intent(TamenchanMainActivity.this, GameMainActivity.class);
    			startActivity(intent);
    		} else if (OPTION.equals(v.getTag())){
    			Intent intent = new Intent(TamenchanMainActivity.this, OptionActivity.class);
    			startActivity(intent);
    		}
    	}
    }
    
}