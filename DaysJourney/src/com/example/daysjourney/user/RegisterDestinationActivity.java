package com.example.daysjourney.user;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.daysjourney.R;
import com.example.daysjourney.common.MainActivity;
import com.example.daysjourney.map.SearchPlaceActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Activity for the destination registration page. For the first goal of us,
 * Arduinos will only be equipped at home, not all destinations. Therefore, this
 * page will only get the user's email first, and get input of a location from
 * the user, then put it to the PATH table.
 */
public class RegisterDestinationActivity extends Activity implements View.OnClickListener {

	/**
	 * Member variables used for destination map in this activity.
	 */
	GoogleMap mDestinationMap;
	SensorManager mSensorMngr;
	
	Button mSearchLocationButton;

	private static final String TAG = "RegisterDestinationActivityLog";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_destination);
		RegisterDestinationActivity.this.startLocationService();
		
		initResources();
		initEvents();
	}
	
	@SuppressLint("NewApi") 
	private void initResources() {
		RelativeLayout destinationMapLayout = (RelativeLayout) this
				.findViewById(R.id.destination_map_layout);
		this.mSensorMngr = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		this.mDestinationMap = ((MapFragment) this.getFragmentManager().findFragmentById(R.id.destination_map)).getMap();
		
		mSearchLocationButton = (Button) this.findViewById(R.id.search_location_button);
	}
	
	private void initEvents() {
		mSearchLocationButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(RegisterDestinationActivity.this, SearchPlaceActivity.class);
		startActivity(intent);
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
				String msg = "Your Current Location \nLatitude: "+lastLocation.getLatitude()+", Longitude: "+lastLocation.getLongitude();
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
		mDestinationMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
		mDestinationMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	}
	
	private class GPSListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Double latitude = location.getLatitude();
			Double longitude = location.getLongitude();
			String msg = "Your Current Location \nLatitude: "+latitude+", Longitude: "+longitude;
			Log.i(TAG, msg);
			//RegisterDestinationActivity.this.showToastMsg(msg);
			
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
		this.mDestinationMap.setMyLocationEnabled(false);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		this.mDestinationMap.setMyLocationEnabled(false);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.mDestinationMap.setMyLocationEnabled(false);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.mDestinationMap.setMyLocationEnabled(true);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register_destination, menu);
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
}
