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

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daysjourney.R;
import com.example.daysjourney.graph.Line;
import com.example.daysjourney.graph.LineGraph;
import com.example.daysjourney.graph.LinePoint;
import com.example.daysjourney.network.IpSubnet;
import com.example.daysjourney.util.UrlSource;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Activity for the user path page. User can set home information and the places
 * where they want to go. TODO: When a new place is added, the new place
 * information should be shown, and the sequence problem also should be solved.
 * 
 */
public class EnvironmentInsideInfoFragment extends Fragment {
	// The constructor of the static fragment class
	// with the position number defined in integer
	public static EnvironmentInsideInfoFragment newInstance(int position) {
		EnvironmentInsideInfoFragment frg = new EnvironmentInsideInfoFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		frg.setArguments(bundle);
		return frg;
	}

	// Fetch continuous values
	public static final int MSG_CONT = 100;
	private static final String LOG = "EnvironmentInsideInfoFragment";

	TextView insideTemp, insideBrightness, insideResult;
	Button insideSingleButton, insideContinuousButton, insideConnectButton;
	ProgressBar insideBusyProgressBar;
	LineGraph insideGraph;
	Line lineTemp, lineBrightness;

	boolean bOnline = false; // whether we are connected to Wifi or other
								// networks
	boolean bAutoscan = false; // whether scan for Arduino automatically
	boolean bNetworking = false; // whether networking is available
	boolean bConnected = false; // whether Arduino is connected
	boolean bFromPref = false; // whether there is some information from
								// preferences
	boolean bContinuous = false; // whether fetch values continuously
	boolean bIsHwMatched = false;

	String exactUrl = null; // example http://192.168.0.2/sensorData
	String subnetUrl = null; // example http://192.168.0.*/sensorData
	String subnetIPString = null;
	String pingUrl = null; // currently scanned address
	int iScanTimeout, iContInterval; // time out for scanning Arduino IP & time
										// interval for fetching values
	int pingFrom, pingTo; // the starting and ending ping numbers (last unit in
							// IP address)
	int px; // points for displaying line graph

	SharedPreferences prefs = null;
	AsyncHttpClient netClient = null;
	Message msg = null;

	View view;

	// Popup view
	PopupWindow popup;
	View popView;
	EditText idEt;
	EditText pwEt;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_environment_inside_info,
				container, false);

		// setup
		getViews();
		bOnline = isNetworkPresent();
		netClient = new AsyncHttpClient();
		prefs = getActivity().getSharedPreferences("prefs", 0);
		setupVars();

		insideSingleButton.setEnabled(false);
		insideContinuousButton.setEnabled(false);
		if (bAutoscan) {
			insideConnectButton.setText(R.string.text_inside_scan_button);
			insideResult.setText("press to scan\n" + subnetUrl);
		} else {
			insideConnectButton.setText(R.string.text_inside_connect_button);
			insideResult.setText("press to connect to\n" + exactUrl);
		}

		lineTemp = new Line();
		lineTemp.setColor(Color.parseColor("#99ccff"));
		lineTemp.setStrokeWidth(4);
		lineBrightness = new Line();
		lineBrightness.setColor(Color.parseColor("#ffcc99"));
		lineBrightness.setStrokeWidth(4);

		insideGraph.addLine(lineTemp);
		insideGraph.addLine(lineBrightness);

		insideGraph.setRangeY(0, 100);
		insideGraph.addPoint(0, new LinePoint(0, 0));
		insideGraph.addPoint(1, new LinePoint(0, 100));
		px = 1;

		/*
		 * Connect button clicked
		 */
		insideConnectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ((!bNetworking) & (bAutoscan)) {
					bNetworking = true;
					pingFrom = 1;
					pingTo = 100;
					insideBusyProgressBar.setVisibility(View.VISIBLE);
					insideConnectButton
							.setText(R.string.text_inside_stop_button);
					scanNet();
				} else if (!bNetworking) {
					insideBusyProgressBar.setVisibility(View.VISIBLE);
					insideConnectButton.setEnabled(false);

					// Show popup window
					// Check ID and password in DaysJourney
					showPopupWindow();
					checkHardwareAuthen();
					if (bIsHwMatched)
						connectUrl();
				} else {
					bNetworking = false;
				}
			}
		});
		/*
		 * Single button clicked
		 */
		insideSingleButton.setOnClickListener(new OnClickListener() {

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
		insideContinuousButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (bConnected & !bContinuous) {
					bContinuous = true;
					insideContinuousButton
							.setText(R.string.text_inside_stop_button);
					insideSingleButton.setEnabled(false);
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

	private class HardwareAuthenTask extends
			AsyncTask<List<NameValuePair>, Void, Boolean> {
		String url = new UrlSource().getUrlRoot() + "hardwareauthen.iotweb";

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (bIsHwMatched) {
				popView.findViewById(R.id.frame_popup_window).setVisibility(
						View.INVISIBLE);
				connectUrl();
			} else {
				showToastMsg("The hardware ID and password do not match the IP address.\nTry again please.");
				idEt.setText("");
				pwEt.setText("");
				insideBusyProgressBar.setVisibility(View.INVISIBLE);
				insideConnectButton
						.setText(R.string.text_inside_connect_button);
			}

		}

		@Override
		protected Boolean doInBackground(List<NameValuePair>... params) {
			// TODO Auto-generated method stub
			StringBuilder str = new StringBuilder();
			boolean flag = false;
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
						flag = true;
						bIsHwMatched = true;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return flag;
		}

	}

	protected void fetchValues() {
		// TODO Auto-generated method stub
		/*
		 * AoA Data connection
		 */
		/*if (netClient == null)
			return;
		netClient.setTimeout(2000);
		insideSingleButton.setEnabled(false);
		netClient.get(exactUrl, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				insideSingleButton.setEnabled(true);
				insideResult.setText("ERROR fetching values");
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
					insideTemp.setText(String.format("%.1f", cTemp));
					insideBrightness.setText(String.format("%02d", brightness));
					insideGraph.shiftPoint(0, new LinePoint(px, cTemp), 50);
					insideGraph.shiftPoint(1,
							new LinePoint(px, brightness / 10), 50);
					insideResult.setText("ACQUISITION n. " + px);
					px++;
					insideSingleButton.setEnabled(true);
				} catch (Exception e) {
				}
			}

		});*/
		
		/*
		 * Data connection through ioedata
		 */
		if (netClient == null)
			return;
		netClient.setTimeout(2000);
		insideSingleButton.setEnabled(false);
		
		String requestUrl = new UrlSource().getUrlRoot() + "datarequest.iotweb";
		RequestParams params = new RequestParams();
		params.put("ip", subnetIPString);
		params.put("id", "abc");
		params.put("method", "single");
		netClient.post(requestUrl, params, new AsyncHttpResponseHandler(){

			@Override
			public void onSuccess(String content) {
				// TODO Auto-generated method stub
				
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
				insideResult.setText("ERROR fetching values");
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
					insideTemp.setText(String.format("%.1f", cTemp));
					insideBrightness.setText(String.format("%02d", brightness));
					insideGraph.shiftPoint(0, new LinePoint(px, cTemp), 50);
					insideGraph.shiftPoint(1,
							new LinePoint(px, brightness / 10), 50);
					insideResult.setText("ACQUISITION n. " + px);
					px++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}

	private void showPopupWindow() {
		popup = new PopupWindow(getActivity());
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popView = inflater.inflate(R.layout.frame_popup_window, null);
		popup.setContentView(popView);
		popup.setWindowLayoutMode(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		popup.setTouchable(true);
		popup.setFocusable(true);
		popup.setBackgroundDrawable(new BitmapDrawable());
		popup.showAtLocation(view, Gravity.CENTER, 40, 40);

		TextView ipTextView = (TextView) popView
				.findViewById(R.id.ip_address_popup_window);
		ipTextView.setText(subnetIPString);
	}

	private boolean checkHardwareAuthen() {
		boolean isIdPwMatch = false;
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

				HardwareAuthenTask asyncTask = new HardwareAuthenTask();
				asyncTask.execute(params);
			}
		});

		return isIdPwMatch;
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
				insideResult.setText("CONNECTED Arduino at\n" + exactUrl);
				insideSingleButton.setEnabled(true);
				insideContinuousButton.setEnabled(true);
				// indeterminate progress bar
				insideBusyProgressBar.setVisibility(View.INVISIBLE);
			}

			// callback called on failure : return
			@Override
			public void onFailure(Throwable e, String response) {
				// result
				insideResult.setText("NOT FOUND at address\n " + exactUrl);
				// indeterminate progress bar
				insideBusyProgressBar.setVisibility(View.INVISIBLE);
				insideConnectButton.setEnabled(true);
			}
		});
	}
	

	protected void scanNet() {
		// TODO Auto-generated method stub
		pingUrl = subnetUrl.replace("*", Integer.toString(pingFrom));
		insideConnectButton.setText(R.string.text_inside_stop_button);
		if (netClient == null)
			return;
		netClient.setTimeout(iScanTimeout);
		netClient.get(pingUrl, new AsyncHttpResponseHandler() {

			// callback called on success : url found
			@Override
			public void onSuccess(String response) {
				// result
				exactUrl = pingUrl;
				subnetIPString = exactUrl.substring(
						exactUrl.indexOf("http://") + 7,
						exactUrl.indexOf("/sensorData"));
				showToastMsg("코넥트 성공!~~~" + exactUrl + "\n" + subnetIPString);
				insideResult.setText("FOUND Arduino at\n" + exactUrl);
				// set preferences and status
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("prefkey_autoscan", false);
				editor.putString("prefkey_network_address_inside", exactUrl);
				editor.commit();
				bAutoscan = false;
				bNetworking = false;
				// button and indeterminate progress bar
				insideConnectButton
						.setText(R.string.text_inside_connect_button);
				insideBusyProgressBar.setVisibility(View.INVISIBLE);
			}

			// callback called on failure : continue scanning
			@Override
			public void onFailure(Throwable e, String response) {
				insideResult.setText("SCANNING subnet address\n " + pingUrl);
				if (pingFrom < pingTo) {
					pingFrom++;
					if (bNetworking)
						scanNet();
					else {
						// result
						insideResult.setText("press to scan\n" + subnetUrl);
						// button and indeterminate progress bar
						if (bAutoscan)
							insideConnectButton
									.setText(R.string.text_inside_scan_button);
						else
							insideConnectButton
									.setText(R.string.text_inside_connect_button);
						insideBusyProgressBar.setVisibility(View.INVISIBLE);
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
					insideContinuousButton
							.setText(R.string.text_inside_continuous_button);
					insideSingleButton.setEnabled(true);
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
				insideConnectButton.setText(R.string.text_inside_scan_button);
			else
				insideConnectButton
						.setText(R.string.text_inside_connect_button);
		}
	}

	private void showToastMsg(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

	private void getViews() {
		insideGraph = (LineGraph) view.findViewById(R.id.insideGraph);
		insideSingleButton = (Button) view
				.findViewById(R.id.inside_single_button);
		insideContinuousButton = (Button) view
				.findViewById(R.id.inside_continuous_button);
		insideConnectButton = (Button) view
				.findViewById(R.id.inside_connect_button);
		insideResult = (TextView) view.findViewById(R.id.inside_result);
		insideTemp = (TextView) view.findViewById(R.id.inside_temp);
		insideBrightness = (TextView) view.findViewById(R.id.inside_brightness);
		insideBusyProgressBar = (ProgressBar) view
				.findViewById(R.id.inside_busy_progressbar);
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
		exactUrl = prefs.getString("prefkey_network_address_inside", "");
		iScanTimeout = Integer.parseInt(prefs.getString("prefkey_scan_timeout",
				"1000"));
		iContInterval = Integer.parseInt(prefs.getString(
				"prefkey_cont_interval", "1000"));
		bAutoscan = prefs.getBoolean("prefkey_autoscan_inside", true);
		if (bOnline) {
			// //////////////////////////////
			subnetUrl = IpSubnet.getIpSubnet().getSubnet()
					+ "/sensorData/insideHome/all";
		}

	}

}
