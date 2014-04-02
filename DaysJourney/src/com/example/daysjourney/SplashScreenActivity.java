package com.example.daysjourney;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.os.Build;
/**
 * Activity for the intro page of the application.
 * The intro page will last for 3 seconds and go to the home page 
 * (log in or sign up) directly.
 * @author ajou
 *
 */
public class SplashScreenActivity extends ActionBarActivity {
	
	Thread mSplashThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash_screen);

		// Set the full screen for 3 seconds and go directly to the main home page
		this.mSplashThread = new Thread(){

			@Override
			public void run() {
				// Let's wait for 3 seconds
				super.run();
				try{
					synchronized (this) {
						wait(3000);
					}
				}catch(Exception e){
					
				}
				finish();
				
				Intent intent = new Intent();
				intent.setClass(SplashScreenActivity.this, MainActivity.class);
				startActivity(intent);
			}
		};
		
		this.mSplashThread.start();
		
	}

}
