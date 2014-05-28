package com.example.daysjourney.user;

import org.json.JSONObject;

import com.example.daysjourney.core.PathManager;
import com.example.daysjourney.entity.Destination;
import com.example.daysjourney.entity.Path;
import com.example.daysjourney.network.APIResponseHandler;
import com.example.daysjourney.network.HttpUtil;
import com.example.daysjourney.network.URLSource;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Intent;
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
	protected Destination mDestination;

	
	protected static final int SEARCH_PLACE = 1;

	
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case SEARCH_PLACE:
				Bundle bundle = data.getBundleExtra("placeInfo");
				mDestination = (Destination) bundle
						.getSerializable("destination");
				break;

			default:
				break;
			}
		}
	}
	
	protected void createDestination(Destination destination) {
		if(destination == null) return;
		
		String url = String.format(URLSource.DESTINATIONS, PathManager.getInstance().getPathId(BaseRegisterActivity.this));
		
		RequestParams params = new RequestParams();
		params.put(Destination.HOME, destination.getHome());
	    params.put(Destination.DESCRIPTION, destination.getDescription());
	    params.put(Destination.REFERENCE, destination.getReference());
	    params.put(Destination.LATITUDE, String.valueOf(destination.getLatitude()));
	    params.put(Destination.LONGITUDE, String.valueOf(destination.getLongitude()));
		
        HttpUtil.post(url, null, params, new APIResponseHandler(BaseRegisterActivity.this) {
        	
            @Override
            public void onSuccess(JSONObject response) {
				//return to previous activity
            }
        });
		
	}
	
	protected void updateDestination(Destination destination) {
		String url = String.format(URLSource.HOME_UPDATE, destination.getDestinationId());
		
		RequestParams params = new RequestParams();
		params.put(Destination.HOME, destination.getHome());
	    params.put(Destination.DESCRIPTION, destination.getDescription());
	    params.put(Destination.REFERENCE, destination.getReference());
	    params.put(Destination.LATITUDE, String.valueOf(destination.getLatitude()));
	    params.put(Destination.LONGITUDE, String.valueOf(destination.getLongitude()));
		
		HttpUtil.put(url, null, params, new APIResponseHandler(BaseRegisterActivity.this));
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
