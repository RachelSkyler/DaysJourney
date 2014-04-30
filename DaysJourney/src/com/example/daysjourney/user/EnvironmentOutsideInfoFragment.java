package com.example.daysjourney.user;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daysjourney.R;
import com.example.daysjourney.graph.Line;
import com.example.daysjourney.graph.LineGraph;
import com.example.daysjourney.graph.LinePoint;
import com.example.daysjourney.util.IpSubnet;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Activity for the user path page. User can set home information and the places
 * where they want to go. TODO: When a new place is added, the new place
 * information should be shown, and the sequence problem also should be solved.
 * 
 */
public class EnvironmentOutsideInfoFragment extends Fragment {
	// The constructor of the static fragment class
	// with the position number defined in integer
	public static EnvironmentOutsideInfoFragment newInstance(int position) {
		EnvironmentOutsideInfoFragment frg = new EnvironmentOutsideInfoFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		frg.setArguments(bundle);
		return frg;
	}

	// Fetch continuous values
	public static final int MSG_CONT = 100;
	private static final String LOG = "EnvironmentOutsideInfoFragment";

	TextView outsideTemp, outsideBrightness, outsideResult;
	Button outsideSingleButton, outsideContinuousButton, outsideConnectButton;
	ProgressBar outsideBusyProgressBar;
	LineGraph outsideGraph;
	Line lineTemp, lineBrightness;

	boolean bOnline = false; // whether we are connected to Wifi or other networks
	boolean bAutoscan = false; // whether scan for Arduino automatically
	boolean bNetworking = false; // whether networking is available
	boolean bConnected = false; // whether Arduino is connected
	boolean bFromPref = false; // whether there is some information from preferences
	boolean bContinuous = false; // whether fetch values continuously
	String exactUrl = null; // example http://192.168.0.2/sensorData
	String subnetUrl = null; // example http://192.168.0.*/sensorData
	String pingUrl = null; // currently scanned address
	int iScanTimeout, iContInterval; // time out for scanning Arduino IP & time interval for fetching values
	int pingFrom, pingTo; // the starting and ending ping numbers (last unit in IP address)
	int px; // points for displaying line graph

	SharedPreferences prefs = null;
	AsyncHttpClient netClient = null;
	Message msg = null;

	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_environment_outside_info,
				container, false);

		// setup
		getViews();
		bOnline = isNetworkPresent();
		netClient = new AsyncHttpClient();
		prefs = getActivity().getSharedPreferences("prefs", 0);
		setupVars();

		outsideSingleButton.setEnabled(false);
		outsideContinuousButton.setEnabled(false);
		if (bAutoscan) {
			outsideConnectButton.setText(R.string.text_outside_scan_button);
			outsideResult.setText("press to scan\n" + subnetUrl);
		} else {
			outsideConnectButton.setText(R.string.text_outside_connect_button);
			outsideResult.setText("press to connect to\n" + exactUrl);
		}

		lineTemp = new Line();
		lineTemp.setColor(Color.parseColor("#99ccff"));
		lineTemp.setStrokeWidth(4);
		lineBrightness = new Line();
		lineBrightness.setColor(Color.parseColor("#ffcc99"));
		lineBrightness.setStrokeWidth(4);

		outsideGraph.addLine(lineTemp);
		outsideGraph.addLine(lineBrightness);

		outsideGraph.setRangeY(0, 100);
		outsideGraph.addPoint(0, new LinePoint(0, 0));
		outsideGraph.addPoint(1, new LinePoint(0, 100));
		px = 1;

		/*
		 * Connect button clicked
		 */
		outsideConnectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ((!bNetworking) & (bAutoscan)) {
					bNetworking = true;
					pingFrom = 1;
					pingTo = 100;
					outsideBusyProgressBar.setVisibility(View.VISIBLE);
					outsideConnectButton
							.setText(R.string.text_outside_stop_button);
					scanNet();
				} else if (!bNetworking) {
					outsideBusyProgressBar.setVisibility(View.VISIBLE);
					outsideConnectButton.setEnabled(false);
					connectUrl();
				} else {
					bNetworking = false;
				}
			}
		});
		/*
		 * Single button clicked
		 */
		outsideSingleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (bConnected)
					fetchValues();
			}
		});

		/*
		 * Continuous button clicked
		 */
		outsideContinuousButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (bConnected & !bContinuous) {
					bContinuous = true;
					outsideContinuousButton
							.setText(R.string.text_outside_stop_button);
					outsideSingleButton.setEnabled(false);
					msg = new Message();
					msg.what = MSG_CONT;
					msg.arg1 = iContInterval;
					timingHandler.sendMessageDelayed(msg, msg.arg1);
				} else if (bContinuous) {
					bContinuous = false;
				}
			}
		});

		return view;
	}

	protected void fetchValues() {
		// TODO Auto-generated method stub
		if (netClient == null)
			return;
		netClient.setTimeout(2000);
		outsideSingleButton.setEnabled(false);
		netClient.get(exactUrl, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				outsideSingleButton.setEnabled(true);
				outsideResult.setText("ERROR fetching values");
			}

			@Override
			public void onSuccess(String response) {
				// TODO Auto-generated method stub
				super.onSuccess(response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					Log.i("FETCH", jsonObject.toString());
					float cTemp = (float) jsonObject.getDouble("ctemp");
					int brightness = (int) jsonObject.getInt("brightness");
					outsideTemp.setText(String.format("%.1f", cTemp));
					outsideBrightness.setText(String.format("%02d", brightness));
					outsideGraph.shiftPoint(0, new LinePoint(px, cTemp), 50);
					outsideGraph.shiftPoint(1, new LinePoint(px,
							brightness / 10), 50);
					outsideResult.setText("ACQUISITION n. " + px);
					px++;
					outsideSingleButton.setEnabled(true);
				} catch (Exception e) {
				}
			}

		});
	}

	protected void fetchContValues() {
		// TODO Auto-generated method stub
		if (netClient == null)
			return;
		netClient.setTimeout(iContInterval - 200);
		netClient.get(exactUrl, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable e, String response) {
				outsideResult.setText("ERROR fetching values");
			}

			@Override
			public void onSuccess(String response) {
				// TODO Auto-generated method stub
				super.onSuccess(response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					Log.i("FETCHCONT", jsonObject.toString());
					float cTemp = (float) jsonObject.getDouble("ctemp");
					int brightness = (int) jsonObject.getInt("brightness");
					outsideTemp.setText(String.format("%.1f", cTemp));
					outsideBrightness.setText(String.format("%02d", brightness));
					outsideGraph.shiftPoint(0, new LinePoint(px, cTemp), 50);
					outsideGraph.shiftPoint(1, new LinePoint(px,
							brightness / 10), 50);
					outsideResult.setText("ACQUISITION n. " + px);
					px++;
				} catch (Exception e) {
				}
			}

		});
	}

	protected void connectUrl() {
		// TODO Auto-generated method stub
		if (netClient == null)
			return;
		netClient.setTimeout(100000);
		netClient.get(exactUrl, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				// result
				bConnected = true;
				outsideResult.setText("CONNECTED Arduino at\n" + exactUrl);
				outsideSingleButton.setEnabled(true);
				outsideContinuousButton.setEnabled(true);
				// indeterminate progress bar
				outsideBusyProgressBar.setVisibility(View.INVISIBLE);
			}

			// callback called on failure : return
			@Override
			public void onFailure(Throwable e, String response) {
				// result
				outsideResult.setText("NOT FOUND at address\n " + exactUrl);
				// indeterminate progress bar
				outsideBusyProgressBar.setVisibility(View.INVISIBLE);
				outsideConnectButton.setEnabled(true);
			}
		});
	}

	protected void scanNet() {
		// TODO Auto-generated method stub
		showToastMsg("SCANNET "+pingFrom);
		pingUrl = subnetUrl.replace("*", Integer.toString(pingFrom));
		outsideConnectButton.setText(R.string.text_outside_stop_button);
		if(netClient == null) 
			return;
		netClient.setTimeout(iScanTimeout);
		netClient.get(pingUrl, new AsyncHttpResponseHandler() {

			// callback called on success : url found
			@Override
			public void onSuccess(String response) {
				// result
				exactUrl = pingUrl;
				showToastMsg("코넥트 성공!~~~"+exactUrl);
				outsideResult.setText("FOUND Arduino at\n" + exactUrl);
				// set preferences and status
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("prefkey_autoscan", false);
				editor.putString("prefkey_network_address", exactUrl);
				editor.commit();
				bAutoscan = false;
				bNetworking = false;
				// button and indeterminate progress bar
				outsideConnectButton.setText(R.string.text_outside_connect_button);
				outsideBusyProgressBar.setVisibility(View.INVISIBLE);
			}

			// callback called on failure : continue scanning
			@Override
			public void onFailure(Throwable e, String response) {
				showToastMsg("SCANNET FAILURE");
				outsideResult.setText("SCANNING subnet address\n " + pingUrl);
				if (pingFrom < pingTo) {
					pingFrom++;
					if (bNetworking)
						scanNet();
					else {
						// result
						outsideResult.setText("press to scan\n" + subnetUrl);
						// button and indeterminate progress bar
						if (bAutoscan)
							outsideConnectButton.setText(R.string.text_outside_scan_button);
						else
							outsideConnectButton.setText(R.string.text_outside_connect_button);
						outsideBusyProgressBar.setVisibility(View.INVISIBLE);
					}
				}
			}

		});
	}

	Handler timingHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_CONT:
				if (bContinuous) {
					fetchContValues();
					msg = timingHandler.obtainMessage(MSG_CONT, iContInterval,
							0);
					timingHandler.sendMessageDelayed(msg, msg.arg1);
				} else {
					outsideContinuousButton
							.setText(R.string.text_outside_continuous_button);
					outsideSingleButton.setEnabled(true);
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	// called when the activity resumes
	@Override
	public void onResume() {
		super.onResume();
		// update vars when returning from preferences
		if (bFromPref) {
			bFromPref = false;
			setupVars();
			if (bAutoscan)
				outsideConnectButton.setText(R.string.text_outside_scan_button);
			else
				outsideConnectButton
						.setText(R.string.text_outside_connect_button);
		}
	}
	
	private void showToastMsg(String msg){
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

	private void getViews() {
		outsideGraph = (LineGraph) view.findViewById(R.id.outsideGraph);
		outsideSingleButton = (Button) view
				.findViewById(R.id.outside_single_button);
		outsideContinuousButton = (Button) view
				.findViewById(R.id.outside_continuous_button);
		outsideConnectButton = (Button) view
				.findViewById(R.id.outside_connect_button);
		outsideResult = (TextView) view.findViewById(R.id.outside_result);
		outsideTemp = (TextView) view.findViewById(R.id.outside_temp);
		outsideBrightness = (TextView) view.findViewById(R.id.outside_brightness);
		outsideBusyProgressBar = (ProgressBar) view
				.findViewById(R.id.outside_busy_progressbar);
	}

	private boolean isNetworkPresent() {
		ConnectivityManager cm = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeWifi = cm.getActiveNetworkInfo();
		return ((activeWifi != null) && (activeWifi.isConnected()));
	}

	private void setupVars() {
		if (prefs == null)
			return;
		exactUrl = prefs.getString("prefkey_network_address", "");
		iScanTimeout = Integer.parseInt(prefs.getString("prefkey_scan_timeout",
				"1000"));
		iContInterval = Integer.parseInt(prefs.getString(
				"prefkey_cont_interval", "1000"));
		bAutoscan = prefs.getBoolean("prefkey_autoscan", true);
		if (bOnline) {
			// //////////////////////////////
			subnetUrl = IpSubnet.getIpSubnet().getSubnet() + "/sensorData/outsideHome/all";
		}

	}

}
