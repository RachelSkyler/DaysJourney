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
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daysjourney.R;
import com.example.daysjourney.common.MainActivity;
import com.example.daysjourney.core.AccountManager;
import com.example.daysjourney.core.PathManager;
import com.example.daysjourney.entity.Destination;
import com.example.daysjourney.entity.Path;
import com.example.daysjourney.entity.User;
import com.example.daysjourney.map.GooglePlacesVO;
import com.example.daysjourney.map.SearchPlaceActivity;
import com.example.daysjourney.network.APIResponseHandler;
import com.example.daysjourney.network.HttpUtil;
import com.example.daysjourney.network.IpSubnet;
import com.example.daysjourney.network.URLSource;
import com.example.daysjourney.util.UrlSource;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Activity for the home registration page. This page comes right after a user's
 * successful sign up. For insurance, we first check whether there is one query
 * result in DESTINATION table in DB, which shows that the user has already got
 * one destination whose 'is_home' is 1 (true). If not, which means we have made
 * sure that the user does not yet have registered home, we ask him/her to
 * register a home location and the arduinos.
 */
public class RegisterHomeActivity extends BaseRegisterActivity {

	/**
	 * Member variables used for destination map in this activity.
	 */
	private LocationManager mLocationMngr;
	private GPSListener mGpsListener;

	private TextView myHomeLocationTextView;
	private Button mSearchInsideBtn;
	private Button mSearchOutsideBtn;
	private Button mSearchLocationButton;
	private Button mFinishRegisterBtn;
	
	// Popup view
	private PopupWindow popup;
	private View popView;
	private EditText idEt;
	private EditText pwEt;
	
	private static final String TAG = "RegisterHomeActivityLog";
	private static final String USER_PATH = "user_path";
	private String subnetIPString;
	private boolean bIsRegisterSucceeded;
	private boolean bPathIsRegistered;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register_home);
		
		initResource();
		initEvent();
		
		bPathIsRegistered = PathManager.getInstance().isRegisteredPath(RegisterHomeActivity.this);
		
		System.out.println("Today's path is registered: " + bPathIsRegistered);
		if (bPathIsRegistered) {
			getHomeInfo();
		} else {
			RegisterHomeActivity.this.startLocationService();
		}
	}
	
	private boolean checkPrev() {
		boolean result = false;
		Intent intent = this.getIntent();
		String prev =  intent.getExtras().getString("prev");
		
		if (prev.equals(USER_PATH)) {
			result = true;
		}
		
		return result;
	}
	
	@SuppressLint("NewApi") 
	private void initResource(){
		mSensorMngr = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		mMap = ((MapFragment) this.getFragmentManager().findFragmentById(R.id.home_map)).getMap();
		mSearchLocationButton = (Button) this.findViewById(R.id.search_home_location_button);
		myHomeLocationTextView = (TextView) this.findViewById(R.id.text_my_home_location);
		mSearchInsideBtn = (Button) this.findViewById(R.id.search_my_inside_arduino_button);
		mSearchOutsideBtn = (Button) this.findViewById(R.id.search_my_outside_arduino_button);
		mFinishRegisterBtn = (Button) this.findViewById(R.id.finish_register_home_button);
	}
	
	private void initEvent() {
		mSearchLocationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!bPathIsRegistered)
					createPath();
					
				dispatchSearchPlace();	
			}
		});
		
		mFinishRegisterBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!checkPrev()) {
					createDestination(mDestination);
				} else if(mDestination.getDestinationId() != null) {
					updateDestination(mDestination);
				}
				// TODO error handling
				quitActivity();
			}
		});
		mSearchInsideBtn.setOnClickListener(new SearchArduinoButtonHandler());
		mSearchOutsideBtn.setOnClickListener(new SearchArduinoButtonHandler());
	}
	
	private String getUserId() {
		return AccountManager.getInstance().getUserId(RegisterHomeActivity.this);
	}
	
	private void createPath() {
		String url = String.format(URLSource.PATHS_CREATE, getUserId());
		
        HttpUtil.post(url, null, null, new APIResponseHandler(RegisterHomeActivity.this) {
        	
            @Override
            public void onSuccess(JSONObject response) {
                PathManager.getInstance().registerPath(RegisterHomeActivity.this, Path.build(response));
            }
        });
	}
	
	private void quitActivity() {
		setResult(Activity.RESULT_OK);
		finish();
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
				showCurrentLocation(latitude, longitude);
			}
		} catch (Exception e) {
			String msg = "Failed to get current location. Please try later.";
			Log.e(TAG, msg);
			showToastMsg(msg);
		}

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			resetDestinationInfo(mDestination);
		}
	}
	
	
	private void resetDestinationInfo(Destination destination) {
		myHomeLocationTextView.setText(destination.getDescription().substring(0,
				destination.getDescription().indexOf(",")));
		showCurrentLocation(destination.getLatitude(),
				destination.getLongitude());
	}
	
	
	private void getHomeInfo() {
		String url = String.format(URLSource.HOME_INFO,AccountManager.getInstance().getUserId(this));
		
		HttpUtil.get(url, null, null, new APIResponseHandler(RegisterHomeActivity.this) {

			@Override
			public void onSuccess(JSONObject response) {
				super.onSuccess(response);
				mDestination = Destination.build(response);
				resetDestinationInfo(mDestination);
			}
		});
	}
	
	private void dispatchSearchPlace() {
		Intent intent = new Intent(RegisterHomeActivity.this,
				SearchPlaceActivity.class);
		intent.putExtra("is_home", true);
		if (!(mDestination == null))
			intent.putExtra(Destination.DESTINATION_ID, mDestination.getDestinationId());
		startActivityForResult(intent, SEARCH_PLACE);
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
				mSearchOutsideBtn.setEnabled(false);
				scanNet(v);
				break;
			case R.id.search_my_outside_arduino_button:
				if (bOnline)
					subnetUrl = IpSubnet.getIpSubnet().getSubnet()
							+ "/sensorData/outsideHome/all";
				mSearchInsideBtn.setEnabled(false);
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
					((Button) view).setText(subnetIPString);
					if (pingFrom < pingTo) {
						pingFrom++;
						scanNet(view);
					}

				}

				@Override
				public void onSuccess(String content) {
					exactUrl = pingUrl;
					showToastMsg("코넥트 성공~~~" + exactUrl + "\n" + subnetIPString);
					((Button) view).setText(subnetIPString);
					switch (view.getId()) {
					case R.id.search_my_inside_arduino_button:
						mSearchOutsideBtn.setEnabled(true);
						break;
					case R.id.search_my_outside_arduino_button:
						mSearchInsideBtn.setEnabled(true);
						break;

					default:
						break;
					}
					view.setEnabled(true);
					view.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
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
				e.printStackTrace();
			}
			return bIsRegisterSucceeded;
		}

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
