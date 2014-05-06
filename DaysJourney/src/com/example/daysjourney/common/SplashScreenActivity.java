package com.example.daysjourney.common;

import com.example.daysjourney.R;
import com.example.daysjourney.R.layout;
import com.example.daysjourney.user.UserPageActivity;

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
 * The intro page will last for 3 seconds and go to the home page directly.
 */
public class SplashScreenActivity extends ActionBarActivity {
	/**
	 * Thread to count for 3 seconds.
	 */
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
						wait(1000);
					}
				}catch(Exception e){
					
				}
				finish();
				
				Intent intent = new Intent();
				// Go to the user path page, not the main page directly
				// After getting to the main page, 
				// whether the user is signed in or not will be checked then
				// After that, whether we should go to the main page to sign in or sign up
				// will be determined
				intent.setClass(SplashScreenActivity.this, UserPageActivity.class);
				startActivity(intent);
			}
		};
		
		this.mSplashThread.start();
		
	}

}
