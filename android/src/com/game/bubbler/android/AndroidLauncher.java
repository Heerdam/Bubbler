package com.game.bubbler.android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.game.bubbler.Game;
import com.game.bubbler.Game.Ads;
import com.game.bubbler.Game.IActivityRequestHandler;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;

public class AndroidLauncher extends AndroidApplication implements IActivityRequestHandler{

	protected static AdView adViewTop;

	protected static final String BANNER_AD_UNIT_ID = "ca-app-pub-1979630875854799/8034576666"; 
	
	protected static Context context;

	protected static Handler handler = new Handler(){
	       
		@Override
		public void handleMessage(Message msg){
			Ads temp = (Ads) msg.obj;
			switch(temp){
				case show:
					adViewTop.setVisibility(View.VISIBLE);
				break;
				case hide:
					adViewTop.setVisibility(View.INVISIBLE);
				break;
				case destroy:
					adViewTop.destroy();
				break;
				case help:
				    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.crossart.ch/current-projects/bubbler/bubbler-help/"))); 
				break;
		    }
		}
		
	};

	@Override 
	public void onCreate (Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        context = getContext();
	        RelativeLayout layout = new RelativeLayout(this);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	        
	        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
			config.useWakelock = true;
			config.useCompass = true;
			layout.addView(initializeForView(new Game(this), config));
	        
	        Builder adRequest = new AdRequest.Builder();
	        //adRequest.addTestDevice("CED838BBC57766D946AF7333E904FB19");
	        adRequest.tagForChildDirectedTreatment(true);
	         
	        adViewTop = new AdView(this);
	        adViewTop.setAdUnitId(BANNER_AD_UNIT_ID);
	        adViewTop.setBackgroundColor(151515);
	        adViewTop.setVisibility(View.VISIBLE);
	        adViewTop.setAdSize(AdSize.SMART_BANNER);
	       
	        RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	        adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

	        layout.addView(adViewTop, adParams);

	        adViewTop.loadAd(adRequest.build());

	        setContentView(layout);
	       
	}

	@Override
	public void showAds(Ads action){
		Message temp = new Message();
		temp.obj = action;
		handler.sendMessage(temp);
	}
}
