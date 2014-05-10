package com.example.daysjourney.core;

import android.app.Application;
import android.util.Log;

public class App extends Application {
	public static final int SERVER_TEST = 0;
	public static final int SERVER_PRODUCTION = 1;
	public static final int SERVER_TARGET = SERVER_TEST;
	
    public static final String TAG = "DaysJourney";
	
	//TODO Think whether there are more shared things or not.
	
	public static void log(String msg) {
		log(TAG,msg);
	}
	
	public static void log(String tag, String msg) {
		Log.d(tag, msg);
	}
}
