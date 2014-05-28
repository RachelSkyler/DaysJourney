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
import com.example.daysjourney.entity.Destination;
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
	Button mRegisterFinishButton;
	
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
		mRegisterFinishButton = (Button) this.findViewById(R.id.finish_register_button);
	}
	
	private void initEvents() {
		mSearchLocationButton.setOnClickListener(this);
		mRegisterFinishButton.setOnClickListener(this);
	}
	
	/**
	 * 1. Destination 이 없다면 생성. 
	 * 2. Destination 이 존재한다면 업데이트 
	 * 
	 * 판단은 User Path 에서. 
	 * Fragment 에서 Path 정보를 가져와야 해. 
	 * 여기서 Path 정보란 Destination 의 이름 목록도 가지고 있어야 할 수 도 
	 * 
	 * location name을 description 을 parsing 해서 저장하자. 
	 * 
	 */
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.search_location_button:
			dispatchSearchPlace();
			break;
		case R.id.finish_register_button:
			quitActivity();
			break;
			
		}
		
	}
	
	/**
	 * 이전에 Destination을 등록한 적이 있는 지. 
	 * 1. Destination 등록한 적 이 있는 지를 판별 = > Destination 의 종류는 많을 꺼야
	 * UserPathFragment 에서 Destination Array를 얻은 다음에 순서대로 누르면 index를 넘겨주기? 
	 * 아니면 해당 인덱스에 맞는 destination을 넘겨주기. 
	 * 어떤게 리소스가 가장 적을 지 생각 하면 
	 * 항상 destination을 만들 때마다 Path에 포함되어 있는 destination의 숫자를 기록해놓고
	 * 순서에 맞는 버튼을 누를 때 해당 index를 넘겨주고 RegisterDestinationActivity에서 getDestination을 
	 * 호출하고 있다면 reset 없다면 새로 생성. 
	 * 
	 * @return
	 */
	private boolean checkPrev() {
		return false;
	}
	
	private void quitActivity() {
		setResult(Activity.RESULT_OK);
		finish();
	}
	
	private void dispatchSearchPlace() {
		Intent intent = new Intent(RegisterDestinationActivity.this,
				SearchPlaceActivity.class);
		intent.putExtra("is_home", false);
		if (!(mDestination == null))
			intent.putExtra(Destination.DESTINATION_ID, mDestination.getDestinationId());
		startActivityForResult(intent, SEARCH_PLACE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode ==RESULT_OK){
			switch (requestCode) {
			case SEARCH_PLACE:
				resetDestinationInfo(mDestination);
				break;

			default:
				break;
			}
		}
	}
	
	private void resetDestinationInfo(Destination destination) {
		myLocationTextView.setText(destination.getDescription().substring(0,
				destination.getDescription().indexOf(",")));
		showCurrentLocation(destination.getLatitude(),
				destination.getLongitude());
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
