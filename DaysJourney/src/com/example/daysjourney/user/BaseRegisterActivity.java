package com.example.daysjourney.user;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class BaseRegisterActivity extends Activity {
	protected GoogleMap mMap;
	protected SensorManager mSensorMngr;
	
	
	private static final String TAG = "RegisterActivityLog";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMap.setMyLocationEnabled(false);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mMap.setMyLocationEnabled(false);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMap.setMyLocationEnabled(false);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMap.setMyLocationEnabled(true);
	}

	protected void showCurrentLocation(Double latitude, Double longitude){
		LatLng curPoint = new LatLng(latitude, longitude);
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	}
	
	protected class GPSListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Double latitude = location.getLatitude();
			Double longitude = location.getLongitude();
			String msg = "Your Current Location \nLatitude: " + latitude
					+ ", Longitude: " + longitude;
			Log.i(TAG, msg);
			// RegisterHomeActivity.this.showToastMsg(msg);
			showCurrentLocation(latitude, longitude);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		
		@Override
		public void onProviderEnabled(String provider) {}
		
		@Override
		public void onProviderDisabled(String provider) {}
	}

	protected void showToastMsg(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}
}
