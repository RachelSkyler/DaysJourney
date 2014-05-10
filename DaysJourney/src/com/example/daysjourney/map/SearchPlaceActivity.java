package com.example.daysjourney.map;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.daysjourney.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

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

	AutoCompleteTextView mAutoPlace;
	ArrayAdapter<String> mAdapter;

	private static final String TAG = "SearchPlaceActivityLog";
	private static final String API_KEY = "AIzaSyAPPleBgtffPbcRSUkfKu6V2DuV2cJJ5-4";
	private static final String GOOGLE_PLACES_AUTO_ROOT = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=";
	private static final String GOOGLE_PLACES_DETAILS_ROOT = "https://maps.googleapis.com/maps/api/place/details/json?reference=";
	private static final String GOOGLE_PLACES_API_OPTIONS = "&sensor=false&key="+API_KEY;
	
	// Values from Google API JSON that is returned
	GooglePlacesVO placesVO;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_place);

		this.mSensorMngr = (SensorManager) this
				.getSystemService(Context.SENSOR_SERVICE);

		this.mHomeMap = ((MapFragment) this.getFragmentManager()
				.findFragmentById(R.id.fullscreen_map)).getMap();

		this.mAutoPlace = (AutoCompleteTextView) this
				.findViewById(R.id.fullscreen_map_search_place_input);
		this.mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line);
		this.mAdapter.setNotifyOnChange(true);
		this.mAutoPlace.setAdapter(mAdapter);
		this.mAutoPlace.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (count % 3 == 1) {
					mAdapter.clear();
					AsyncGetPlaces asyncTask = new AsyncGetPlaces();
					asyncTask.execute(mAutoPlace.getText().toString());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		Button confirmBtn = (Button) this.findViewById(R.id.fullscreen_map_confirm_button);
		confirmBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//return to previous activity
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("placesVO", placesVO);
				intent.putExtra("placeInfo", bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
		SearchPlaceActivity.this.startLocationService();
		
	}

	private class AsyncGetPlaces extends
			AsyncTask<String, Void, ArrayList<GooglePlacesVO>> {

		@Override
		protected ArrayList<GooglePlacesVO> doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.d(TAG, "doInBackground " + params[0]);
			ArrayList<GooglePlacesVO> predictionList = new ArrayList<GooglePlacesVO>();
			try {

				URL googlePlaces = new java.net.URL(GOOGLE_PLACES_AUTO_ROOT
						+ URLEncoder.encode(params[0].toString(), "UTF-8")
						+ GOOGLE_PLACES_API_OPTIONS);
				// Server Key: AIzaSyAPPleBgtffPbcRSUkfKu6V2DuV2cJJ5-4

				URLConnection connection = googlePlaces.openConnection();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));

				String line;
				StringBuffer strBuff = new StringBuffer();
				// Take Google's JSON and turn it into a huge long String
				while ((line = br.readLine()) != null) {
					strBuff.append(line);
				}

				System.out.println(strBuff.toString());
				JSONObject jsonObj = new JSONObject(strBuff.toString());
				JSONArray jsonArr = new JSONArray(
						jsonObj.getString("predictions"));
				for (int i = 0; i < jsonArr.length(); i++) {
					JSONObject obj = (JSONObject) jsonArr.get(i);
					
					GooglePlacesVO plVO = new GooglePlacesVO();
					plVO.setDescription(obj.getString("description"));
					plVO.setId(obj.getString("id"));
					plVO.setReference(obj.getString("reference"));
					
					predictionList.add(plVO);
				}

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, predictionList.toString());
			return predictionList;
		}

		@Override
		protected void onPostExecute(final ArrayList<GooglePlacesVO> result) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onPostExecute : " + result.size());
			super.onPostExecute(result);
			mAdapter = new ArrayAdapter<String>(getBaseContext(),
					android.R.layout.simple_dropdown_item_1line);
			mAdapter.setNotifyOnChange(true);
			mAutoPlace.setAdapter(mAdapter);

			for (GooglePlacesVO googleObj : result) {
				mAdapter.add(googleObj.getDescription());
				mAdapter.notifyDataSetChanged();
			}
			
			mAutoPlace.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					placesVO = result.get(position);
					String reference = placesVO.getReference();
					
					AsyncGetPlacesDetails detailsAsyncTask = new AsyncGetPlacesDetails();
					detailsAsyncTask.execute(reference);
					
					Log.e(TAG, placesVO.getDescription()+" "+placesVO.getReference());
				}
			});
			Log.d(TAG,
					"onPostExecute : autoCompleteAdapter" + mAdapter.getCount());
		}

	}

	private class AsyncGetPlacesDetails extends AsyncTask<String, JSONObject, GooglePlacesVO>{

		@Override
		protected void onPostExecute(GooglePlacesVO result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mHomeMap.setMyLocationEnabled(false);
		}

		@Override
		protected void onProgressUpdate(JSONObject... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			try {
				showCurrentLocation(values[0].getDouble("lat"), values[0].getDouble("lng"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		protected GooglePlacesVO doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.d(TAG, params[0]);
			
			try {
				URL googlePlacesDetails = new java.net.URL(
						GOOGLE_PLACES_DETAILS_ROOT
								+ URLEncoder.encode(params[0].toString(),
										"UTF-8")+GOOGLE_PLACES_API_OPTIONS);
				
				URLConnection connection = googlePlacesDetails.openConnection();
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				String line;
				StringBuffer strBuff = new StringBuffer();
				while((line = br.readLine()) != null){
					strBuff.append(line);
				}
				
				System.out.println("DETAILS"+strBuff.toString());
				
				JSONObject jsonObj = new JSONObject(strBuff.toString());
				JSONObject locObj = jsonObj.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
				placesVO.setLatitude(locObj.getDouble("lat"));
				placesVO.setLongitude(locObj.getDouble("lng"));
				
				publishProgress(locObj);
				
				Log.e(TAG+"_DETAILS", locObj.toString());
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return placesVO;
		}
		
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
			// SearchPlaceActivity.this.showToastMsg(msg);

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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.mHomeMap.setMyLocationEnabled(true);
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
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

}
