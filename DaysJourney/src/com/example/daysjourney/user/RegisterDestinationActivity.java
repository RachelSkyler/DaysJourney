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
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daysjourney.R;
import com.example.daysjourney.common.MainActivity;
import com.example.daysjourney.map.GooglePlacesVO;
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
public class RegisterDestinationActivity extends BaseRegisterActivity implements View.OnClickListener {

	/**
	 * Member variables used for destination map in this activity.
	 */
	Button mSearchLocationButton;
	TextView myLocationTextView;

	private static final String TAG = "RegisterDestinationActivityLog";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register_destination);
		RegisterDestinationActivity.this.startLocationService();
		
		initResources();
		initEvents();
	}
	
	@SuppressLint("NewApi") 
	private void initResources() {
		myLocationTextView = (TextView) this.findViewById(R.id.text_my_location);
		mSensorMngr = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		mMap = ((MapFragment) this.getFragmentManager().findFragmentById(R.id.destination_map)).getMap();
		
		RelativeLayout destinationMapLayout = (RelativeLayout) this.findViewById(R.id.destination_map_layout);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode ==RESULT_OK){
			switch (requestCode) {
			case 1:
				Bundle bundle = data.getBundleExtra("placeInfo");
				GooglePlacesVO placesVO = (GooglePlacesVO) bundle.getSerializable("placesVO");
				String desc = placesVO.getDescription();
				this.myLocationTextView.setText(desc.substring(0, desc.indexOf(",")));
				this.showCurrentLocation(placesVO.getLatitude(), placesVO.getLongitude());
				break;

			default:
				break;
			}
		}
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
