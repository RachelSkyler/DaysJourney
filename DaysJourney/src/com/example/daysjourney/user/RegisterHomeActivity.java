package com.example.daysjourney.user;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daysjourney.R;
import com.example.daysjourney.map.GooglePlacesVO;
import com.example.daysjourney.map.SearchPlaceActivity;
import com.example.daysjourney.util.IpSubnet;
import com.example.daysjourney.util.UrlSource;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Activity for the home registration page. This page comes right after a user's
 * successful sign up. For insurance, we first check whether there is one query
 * result in DESTINATION table in DB, which shows that the user has already got
 * one destination whose 'is_home' is 1 (true). If not, which means we have made
 * sure that the user does not yet have registered home, we ask him/her to
 * register a home location and the arduinos.
 */
public class RegisterHomeActivity extends Activity {

	/**
	 * Member variables used for destination map in this activity.
	 */

	private GoogleMap mHomeMap;
	private SensorManager mSensorMngr;
	private LocationManager mLocationMngr;
	private GPSListener mGpsListener;

	private TextView myHomeLocationTextView;
	private Button searchInsideBtn;
	private Button searchOutsideBtn;
	private Button mSearchLocationButton;
	
	// Popup view
	private PopupWindow popup;
	private View popView;
	private EditText idEt;
	private EditText pwEt;
	
	private static final String TAG = "RegisterHomeActivityLog";
	private String subnetIPString;
	private boolean bIsRegisterSucceeded;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_home);

		RegisterHomeActivity.this.startLocationService();

		initResource();
		initEvent();
	}
	
	@SuppressLint("NewApi") 
	private void initResource(){
		mSensorMngr = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		mHomeMap = ((MapFragment) this.getFragmentManager().findFragmentById(R.id.home_map)).getMap();
		mSearchLocationButton = (Button) this.findViewById(R.id.search_home_location_button);
		myHomeLocationTextView = (TextView) this
				.findViewById(R.id.text_my_home_location);
	}
	
	private void initEvent() {
		mSearchLocationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Go to the page for select a place
				Intent intent = new Intent(RegisterHomeActivity.this,
						SearchPlaceActivity.class);
				startActivityForResult(intent, 1);
			}
		});

		searchInsideBtn = (Button) this
				.findViewById(R.id.search_my_inside_arduino_button);
		searchOutsideBtn = (Button) this
				.findViewById(R.id.search_my_outside_arduino_button);
		searchInsideBtn.setOnClickListener(new SearchArduinoButtonHandler());
		searchOutsideBtn.setOnClickListener(new SearchArduinoButtonHandler());
	}

	private class SearchArduinoButtonHandler implements OnClickListener {

		int pingFrom = 1;
		int pingTo = 100;

		boolean bOnline;

		String subnetUrl;
		String exactUrl;
		String pingUrl;

		AsyncHttpClient netClient;

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			bOnline = isNetworkPresent();
			Log.d(TAG, "" + bOnline);
			netClient = new AsyncHttpClient();

			switch (v.getId()) {
			case R.id.search_my_inside_arduino_button:
				if (bOnline)
					subnetUrl = IpSubnet.getIpSubnet().getSubnet()
							+ "/sensorData/insideHome/all";
				searchOutsideBtn.setEnabled(false);
				scanNet(v);
				break;
			case R.id.search_my_outside_arduino_button:
				if (bOnline)
					subnetUrl = IpSubnet.getIpSubnet().getSubnet()
							+ "/sensorData/outsideHome/all";
				searchInsideBtn.setEnabled(false);
				scanNet(v);
				break;
			default:
				break;
			}
		}

		private void scanNet(final View view) {
			view.setEnabled(false);

			pingUrl = subnetUrl.replace("*", Integer.toString(pingFrom));
			subnetIPString = pingUrl.substring(pingUrl.indexOf("http://") + 7,
					pingUrl.indexOf("/sensorData"));
			netClient.setTimeout(1000);
			netClient.get(pingUrl, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable error, String content) {
					// TODO Auto-generated method stub
					((Button) view).setText(subnetIPString);
					if (pingFrom < pingTo) {
						pingFrom++;
						scanNet(view);
					}

				}

				@Override
				public void onSuccess(String content) {
					// TODO Auto-generated method stub
					exactUrl = pingUrl;
					showToastMsg("코넥트 성공~~~" + exactUrl + "\n" + subnetIPString);
					((Button) view).setText(subnetIPString);
					switch (view.getId()) {
					case R.id.search_my_inside_arduino_button:
						searchOutsideBtn.setEnabled(true);
						break;
					case R.id.search_my_outside_arduino_button:
						searchInsideBtn.setEnabled(true);
						break;

					default:
						break;
					}
					view.setEnabled(true);
					view.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showPopupWindow();
							registerHardware();
						}
					});
				}

			});
		}

	}

	private void showPopupWindow() {
		popup = new PopupWindow(this);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popView = inflater.inflate(R.layout.frame_popup_window, null);
		popup.setContentView(popView);
		popup.setWindowLayoutMode(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		popup.setTouchable(true);
		popup.setFocusable(true);
		popup.setBackgroundDrawable(new BitmapDrawable());
		popup.showAtLocation(popView, Gravity.CENTER, 40, 40);

		TextView ipTextView = (TextView) popView
				.findViewById(R.id.ip_address_popup_window);
		ipTextView.setText(subnetIPString);
	}

	private boolean registerHardware() {
		idEt = (EditText) popView.findViewById(R.id.arduino_id_popup_window);
		pwEt = (EditText) popView
				.findViewById(R.id.arduino_password_popup_window);
		Button sendBtn = (Button) popView
				.findViewById(R.id.send_button_popup_window);

		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String id = idEt.getText().toString();
				String pw = pwEt.getText().toString();

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("hwip", subnetIPString));
				params.add(new BasicNameValuePair("hwid", id));
				params.add(new BasicNameValuePair("hwpw", pw));

				AsyncRegisterHardwareTask registerHwTask = new AsyncRegisterHardwareTask();
				registerHwTask.execute(params);

			}
		});
		return bIsRegisterSucceeded;
	}

	private class AsyncRegisterHardwareTask extends
			AsyncTask<List<NameValuePair>, Void, Boolean> {
		String url = new UrlSource().getUrlRoot() + "hardwareauthen.iotweb";

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (bIsRegisterSucceeded) {
				popView.findViewById(R.id.frame_popup_window).setVisibility(
						View.INVISIBLE);
				showToastMsg("Arduino registration succeeded!");
			} else {
				showToastMsg("Arduino registration failed!");
			}
		}

		@Override
		protected Boolean doInBackground(List<NameValuePair>... params) {
			// TODO Auto-generated method stub
			StringBuilder str = new StringBuilder();
			HttpPost post = new HttpPost(url);
			HttpClient client = new DefaultHttpClient();

			try {
				post.setEntity(new UrlEncodedFormEntity(params[0]));
				HttpResponse res;
				res = client.execute(post);
				StatusLine status = res.getStatusLine();
				int statusCode = status.getStatusCode();
				Log.d("RESPONSECODE", statusCode + "");
				if (statusCode == 200) {
					HttpEntity entity = res.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String lv = "";
					while ((lv = reader.readLine()) != null) {
						str.append(lv);
					}
					Log.d("RESPONSE", str.toString());
					if (str.toString().equals("true")) {
						bIsRegisterSucceeded = true;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return bIsRegisterSucceeded;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				Bundle bundle = data.getBundleExtra("placeInfo");
				GooglePlacesVO placesVO = (GooglePlacesVO) bundle
						.getSerializable("placesVO");
				String desc = placesVO.getDescription();
				this.myHomeLocationTextView.setText(desc.substring(0,
						desc.indexOf(",")));
				this.showCurrentLocation(placesVO.getLatitude(),
						placesVO.getLongitude());
				break;

			default:
				break;
			}
		}
	}

	private void startLocationService() {
		// TODO Auto-generated method stub
		mLocationMngr = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		mGpsListener = new GPSListener();
		long minTime = 10000;
		float minDistance = 0;

		mLocationMngr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				minTime, minDistance, mGpsListener);
		mLocationMngr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				minTime, minDistance, mGpsListener);

		try {
			Location lastLocation = mLocationMngr
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (lastLocation != null) {
				Double latitude = lastLocation.getLatitude();
				Double longitude = lastLocation.getLongitude();
				String msg = "Your Current Location \nLatitude: " + latitude
						+ ", Longitude: " + longitude;
				Log.i(TAG, msg);
				// this.showToastMsg(msg);
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
		this.mLocationMngr.removeUpdates(mGpsListener);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.mLocationMngr.removeUpdates(mGpsListener);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.mHomeMap.setMyLocationEnabled(true);
	}

	private boolean isNetworkPresent() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeWifi = cm.getActiveNetworkInfo();
		return ((activeWifi != null) && (activeWifi.isConnected()));
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
}
