package com.example.daysjourney.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.daysjourney.R;
import com.example.daysjourney.map.SearchPlaceActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Activity for the home registration page.
 * This page comes right after a user's successful sign up.
 * For insurance, we first check whether there is one query result in DESTINATION table in DB,
 * which shows that the user has already got one destination whose 'is_home' is 1 (true).
 * If not, which means we have made sure that the user does not yet have registered home,
 * we ask him/her to register a home location and the arduinos.
 */
public class RegisterHomeActivity extends Activity {

	/**
	 * Member variables used for destination map in this activity.
	 */
	GoogleMap mHomeMap;
	SensorManager mSensorMngr;
	
	private static final String TAG = "RegisterHomeActivityLog";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_home);
		
		this.mSensorMngr = (SensorManager) this
				.getSystemService(Context.SENSOR_SERVICE);

		this.mHomeMap = ((MapFragment) this.getFragmentManager()
				.findFragmentById(R.id.home_map)).getMap();


		RegisterHomeActivity.this.startLocationService();

		Button searchLocationButton = (Button) this
				.findViewById(R.id.search_home_location_button);
		searchLocationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Go to the page for select a place
				Intent intent = new Intent(RegisterHomeActivity.this, SearchPlaceActivity.class);
				startActivity(intent);
			}
		});
	}

	private void startLocationService() {
		// TODO Auto-generated method stub
		LocationManager locationMngr = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		GPSListener gpsListener = new GPSListener();
		long minTime = 10000;
		float minDistance = 0;

		locationMngr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				minTime, minDistance, gpsListener);
		locationMngr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				minTime, minDistance, gpsListener);
		
		try{
			Location lastLocation = locationMngr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(lastLocation != null){
				Double latitude = lastLocation.getLatitude();
				Double longitude = lastLocation.getLongitude();
				String msg = "Your Current Location \nLatitude: "+latitude+", Longitude: "+longitude;
				Log.i(TAG, msg);
				//this.showToastMsg(msg);
				this.showCurrentLocation(latitude, longitude);
			}
		}catch(Exception e){
			String msg = "Failed to get current location. Please try later.";
			Log.e(TAG, msg);
			this.showToastMsg(msg);
		}

	}

	private void showCurrentLocation(Double latitude, Double longitude){
		LatLng curPoint = new LatLng(latitude, longitude);
		mHomeMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
		mHomeMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	}

	
	private class GPSListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Double latitude = location.getLatitude();
			Double longitude = location.getLongitude();
			String msg = "Your Current Location \nLatitude: "+latitude+", Longitude: "+longitude;
			Log.i(TAG, msg);
			//RegisterHomeActivity.this.showToastMsg(msg);
			
			showCurrentLocation(latitude, longitude);
		}
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

	}

	private void showToastMsg(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		this.mHomeMap.setMyLocationEnabled(false);
	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		this.mHomeMap.setMyLocationEnabled(false);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.mHomeMap.setMyLocationEnabled(false);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.mHomeMap.setMyLocationEnabled(true);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	

	private final SensorEventListener mListener=new SensorEventListener() {
		private int iOrientation=-1;
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};




}
