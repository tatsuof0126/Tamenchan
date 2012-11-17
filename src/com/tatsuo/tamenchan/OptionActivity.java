package com.tatsuo.tamenchan;

import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tatsuo.tamenchan.domain.TamenchanDefine;
import com.tatsuo.tamenchan.domain.TamenchanSetting;
import com.tatsuo.tamenchan.domain.TwitterUser;

public class OptionActivity extends Activity {
	
	private static final String BUTTON_SET_GAMELEVEL      = "setgamelevel";
	private static final String BUTTON_SET_HAITYPE        = "sethaitype";
	private static final String BUTTON_CONNECT_TWITTER    = "connecttwitter";
	private static final String BUTTON_DISCONNECT_TWITTER = "disconnecttwitter";
	private static final String BUTTON_ABOUTAPP           = "aboutapp";
	private static final String BUTTON_MENU               = "menu";
	
	private static final int[] HAI_TYPE_IMAGE = new int[3];
	
	static {
		HAI_TYPE_IMAGE[0] = R.drawable.m1;
		HAI_TYPE_IMAGE[1] = R.drawable.p1;
		HAI_TYPE_IMAGE[2] = R.drawable.s1;
	}
	
    int gameLevel = TamenchanDefine.GAMELEVEL_MIDDLE;
	int haiType = TamenchanDefine.HAITYPE_MANZU;
    
	private static final int TWITTER_AUTHENTICATION = 1;
	private RequestToken requestToken = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option);
        
        showGameLevel();
        showHaiType();
        
        Button setgamelevelButton = (Button)findViewById(R.id.setgamelevel);
        setgamelevelButton.setTag(BUTTON_SET_GAMELEVEL);
        setgamelevelButton.setOnClickListener(new ButtonClickListener());
        
        Button sethaitypeButton = (Button)findViewById(R.id.sethaitype);
        sethaitypeButton.setTag(BUTTON_SET_HAITYPE);
        sethaitypeButton.setOnClickListener(new ButtonClickListener());
        
        Button aboutappButton = (Button)findViewById(R.id.aboutapp);
        aboutappButton.setTag(BUTTON_ABOUTAPP);
        aboutappButton.setOnClickListener(new ButtonClickListener());

        Button menuButton = (Button)findViewById(R.id.menu);
        menuButton.setTag(BUTTON_MENU);
        menuButton.setOnClickListener(new ButtonClickListener());
        
        Button twitterDisconnectButton = (Button)findViewById(R.id.disconnecttwitter);
        twitterDisconnectButton.setTag(BUTTON_DISCONNECT_TWITTER);
        twitterDisconnectButton.setOnClickListener(new ButtonClickListener());
        
        Button twitterButton = (Button)findViewById(R.id.connecttwitter);
        twitterButton.setTag(BUTTON_CONNECT_TWITTER);
        twitterButton.setOnClickListener(new ButtonClickListener());
        
        TwitterUser twitterUser = TwitterUser.getTwitterUser(this);
        ImageView imageView = (ImageView)findViewById(R.id.twittericon);
        TextView textView = (TextView)findViewById(R.id.twittername);
        
        if(twitterUser != null){
        	Bitmap bitmap = twitterUser.getProfileImage(this);
            imageView.setImageBitmap(bitmap);
            
            String twitterScreenName = TamenchanSetting.getTwitterScreenName(this);
            textView.setText("  "+twitterScreenName+"    ");
            
        	// Twitter情報がある場合はTwitter連携ボタンを非表示
            twitterButton.setVisibility(View.GONE);
        } else {
        	// Twitter情報がない場合はTwitter情報表示部分を非表示
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            twitterDisconnectButton.setVisibility(View.GONE);
        }
        
    }
    
    private void showGameLevel(){
        gameLevel = TamenchanSetting.getGameLevel(this);
        TextView gameLevelText = (TextView)findViewById(R.id.gamelevel);
        gameLevelText.setText(TamenchanDefine.GAME_LEVEL[gameLevel]);
    }
    
    private void showHaiType(){
        haiType = TamenchanSetting.getHaiType(this);
        
        ImageView haiTypeImage = (ImageView)findViewById(R.id.haitypeimage);
        haiTypeImage.setImageResource(HAI_TYPE_IMAGE[haiType]);
        
        TextView haiTypeText = (TextView)findViewById(R.id.haitype);
        haiTypeText.setText(TamenchanDefine.HAI_TYPE[haiType]);
    }
    

    class ButtonClickListener implements OnClickListener {
    	public void onClick(View v){
    		if(BUTTON_SET_GAMELEVEL.equals(v.getTag())){
    			showSelectGameLevelDialog();
    		} else if (BUTTON_SET_HAITYPE.equals(v.getTag())){
    			showSelectHaiTypeDialog();
    		} else if(BUTTON_CONNECT_TWITTER.equals(v.getTag())){
    	    	requestToken = TwitterUser.getRequestToken();
    	    	
    	        Intent intent = new Intent(OptionActivity.this, TwitterLoginActivity.class);
    	        intent.putExtra(TwitterLoginActivity.KEY_AUTH_URL, requestToken.getAuthorizationURL()); 
    	        startActivityForResult(intent, TWITTER_AUTHENTICATION);
    		} else if(BUTTON_DISCONNECT_TWITTER.equals(v.getTag())){
    			TamenchanSetting.setOauthToken(OptionActivity.this, "");
    			TamenchanSetting.setOauthTokenSecret(OptionActivity.this, "");
    			
    	        Button twitterButton = (Button)findViewById(R.id.connecttwitter);
    	        Button twitterDisconnectButton = (Button)findViewById(R.id.disconnecttwitter);
    	        ImageView imageView = (ImageView)findViewById(R.id.twittericon);
    	        TextView textView = (TextView)findViewById(R.id.twittername);
    	        
    	        twitterButton.setVisibility(View.VISIBLE);
    	        twitterDisconnectButton.setVisibility(View.GONE);
    	        imageView.setVisibility(View.GONE);
    	        textView.setVisibility(View.GONE);
    		} else if(BUTTON_ABOUTAPP.equals(v.getTag())){
    			Intent intent = new Intent(OptionActivity.this, AboutAppActivity.class);
    			startActivity(intent);    			
    		} else if(BUTTON_MENU.equals(v.getTag())){
    			finish();
    		}
    	}
    }
    
    private void showSelectGameLevelDialog(){
    	AlertDialog.Builder dialog
    		= new AlertDialog.Builder(OptionActivity.this);
    	dialog.setTitle("レベルを選んでください");
    	dialog.setSingleChoiceItems(TamenchanDefine.GAME_LEVEL, gameLevel,
    		new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which == TamenchanDefine.GAMELEVEL_HIGH){
						boolean highLevelFlag = TamenchanSetting.getHighLevelFlag(OptionActivity.this);
						
						if(highLevelFlag == false){
							dialog.dismiss();
							AlertDialog.Builder infoDialog
								= new AlertDialog.Builder(OptionActivity.this);
							infoDialog.setMessage("上級は、中級で"+TamenchanDefine.QUALIFYING_SCORE+"点以上のスコアを出すと選択できるようになります。");
							infoDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {}
							});
							infoDialog.show();
							return;
						}
					}
					
					TamenchanSetting.setGameLevel(OptionActivity.this, which);
					showGameLevel();
					dialog.dismiss();
				}
			});
    	dialog.show();
    }
    
    private void showSelectHaiTypeDialog(){
    	AlertDialog.Builder dialog
    		= new AlertDialog.Builder(OptionActivity.this);
    	dialog.setTitle("牌の種類を選んでください");
    	dialog.setSingleChoiceItems(TamenchanDefine.HAI_TYPE, haiType,
    		new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					TamenchanSetting.setHaiType(OptionActivity.this, which);
					showHaiType();
					dialog.dismiss();
				}
			});
    	dialog.show();
    }
    
    @Override
    protected void onActivityResult(int requestCode,
    		int resultCode, Intent intent) {
    	// 戻りの情報でTwitter情報を構築＆保存
    	TwitterUser twitterUser = TwitterUser.makeTwitterUser(this, requestToken, intent);
    	
    	if(twitterUser == null){
    		return;
    	}
    	
    	// Twitter情報が取得できた場合は画面に表示
        Button twitterButton = (Button)findViewById(R.id.connecttwitter);
        Button twitterDisconnectButton = (Button)findViewById(R.id.disconnecttwitter);
        ImageView imageView = (ImageView)findViewById(R.id.twittericon);
        TextView textView = (TextView)findViewById(R.id.twittername);

    	Bitmap bitmap = twitterUser.getProfileImage(this);
        imageView.setImageBitmap(bitmap);
        
        String twitterScreenName = TamenchanSetting.getTwitterScreenName(this);
        textView.setText("  "+twitterScreenName+"    ");
        
        twitterButton.setVisibility(View.GONE);
        twitterDisconnectButton.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
    }
}
