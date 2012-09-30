package com.tatsuo.tamenchan;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutAppActivity extends Activity {

	private static final String BUTTON_OPTION_MENU  = "optionmenu";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutapp);
        
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
            String versionStr = getString(R.string.version_string, packageInfo.versionName);
            
            TextView versionTextView = (TextView)findViewById(R.id.version);
            versionTextView.setText(versionStr);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        
        Button menuButton = (Button)findViewById(R.id.optionmenu);
        menuButton.setTag(BUTTON_OPTION_MENU);
        menuButton.setOnClickListener(new ButtonClickListener());
    }

    class ButtonClickListener implements OnClickListener {
    	public void onClick(View v){
    		if(BUTTON_OPTION_MENU.equals(v.getTag())){
    			finish();
    		}
    	}
    }
	
}
