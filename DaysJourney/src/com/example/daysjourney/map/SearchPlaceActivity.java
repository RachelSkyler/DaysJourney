package com.example.daysjourney.map;

import com.example.daysjourney.R;
import com.example.daysjourney.util.SystemUiHider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class SearchPlaceActivity extends Activity {

	/**
	 * Member variables used for destination map in this activity.
	 */
	GoogleMap mHomeMap;
	SensorManager mSensorMngr;
	boolean mCompassEnabled;
	CompassView mCompassView;

	private static final String TAG = "SearchPlaceActivityLog";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_place);

		this.mSensorMngr = (SensorManager) this
				.getSystemService(Context.SENSOR_SERVICE);

		this.mHomeMap = ((MapFragment) this.getFragmentManager()
				.findFragmentById(R.id.fullscreen_map)).getMap();

		boolean sideBottom = true;
		this.mCompassView = new CompassView(this, sideBottom);
		this.mCompassView.setVisibility(View.VISIBLE);
		this.mCompassEnabled = true;

		RelativeLayout fullscreenMapLayout = (RelativeLayout) this
				.findViewById(R.id.fullscreen_map_layout);
		final RelativeLayout.LayoutParams compassParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		compassParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		compassParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		fullscreenMapLayout.addView(mCompassView, compassParams);

		SearchPlaceActivity.this.startLocationService();

		Button fullscreenMapSearchButton = (Button) this
				.findViewById(R.id.fullscreen_map_search_button);
		fullscreenMapSearchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Go to the page for select a place
				SearchPlaceActivity.this.showToastMsg("Search a Place for Me~");
			}
		});

	}

	private void startLocationService() {
		// TODO Auto-generated method stub
		LocationManager locationMngr = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		GPSListener gpsListener = new GPSListener();
		long minTime = 5000;
		float minDistance = 0;

		locationMngr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				minTime, minDistance, gpsListener);
		locationMngr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				minTime, minDistance, gpsListener);

		try {
			Location lastLocation = locationMngr
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (lastLocation != null) {
				Double latitude = lastLocation.getLatitude();
				Double longitude = lastLocation.getLongitude();
				String msg = "Your Current Location \nLatitude: " + latitude
						+ ", Longitude: " + longitude;
				Log.i(TAG, msg);
				this.showToastMsg(msg);
				this.showCurrentLocation(latitude, longitude);
			}
		} catch (Exception e) {
			String msg = "Failed to get current location. Please try later.";
			Log.e(TAG, msg);
			this.showToastMsg(msg);
		}

	}

	private void showCurrentLocation(Double latitude, Double longitude) {
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
			String msg = "Your Current Location \nLatitude: " + latitude
					+ ", Longitude: " + longitude;
			Log.i(TAG, msg);
			SearchPlaceActivity.this.showToastMsg(msg);

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
		this.mCompassEnabled = false;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.mHomeMap.setMyLocationEnabled(true);
		if (this.mCompassEnabled) {
			this.mSensorMngr.registerListener(mListener,
					this.mSensorMngr.getDefaultSensor(Sensor.TYPE_ORIENTATION),
					this.mSensorMngr.SENSOR_DELAY_UI);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	private final SensorEventListener mListener = new SensorEventListener() {
		private int iOrientation = -1;

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			if (this.iOrientation < 0) {
				this.iOrientation = ((WindowManager) SearchPlaceActivity.this
						.getSystemService(Context.WINDOW_SERVICE))
						.getDefaultDisplay().getOrientation();
				mCompassView.setAzimuth(event.values[0] + 90 * iOrientation);
				mCompassView.invalidate();
				Log.i(TAG, "New Azimuth --> " + event.values[0] + 90
						* iOrientation);
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}
	};

}
