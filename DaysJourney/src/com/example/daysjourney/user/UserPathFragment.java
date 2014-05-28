package com.example.daysjourney.user;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daysjourney.R;
import com.example.daysjourney.core.AccountManager;
import com.example.daysjourney.core.PathManager;
import com.example.daysjourney.entity.Path;
import com.example.daysjourney.network.APIResponseHandler;
import com.example.daysjourney.network.HttpUtil;
import com.example.daysjourney.network.URLSource;
import com.example.daysjourney.util.UrlSource;

/**
 * Activity for the user path page. User can set home information and the places
 * where they want to go. TODO: When a new place is added, the new place
 * information should be shown, and the sequence problem also should be solved.
 * 
 */
@SuppressLint("ValidFragment") 
public class UserPathFragment extends Fragment {

	private Button userPathHomeButtonTop;
	private Button userPathAddPlaceBotton;
	private Button userPathHomeButtonBottom;
	private TextView todayDate;
	
	private static final int REGISTER_HOME = 1;
	private static final int REGISTER_DESTINATION = 2;

	
	public static UserPathFragment newInstance(int position) {
		UserPathFragment frg = new UserPathFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		frg.setArguments(bundle);
		return frg;
	}
	
	private void checkPath() {
		// 등록되지 않았거나 오늘날짜의 Path 가 아니라면 새로 만들어~!
		// 등록이 DB에는 되어 있다면? 그럼 로그인을 하면 getPath
		if (!PathManager.getInstance().isRegisteredPath(getActivity()) || 
				!PathManager.getInstance().isTodayPath(getActivity())) {
			createPath();
		} 
	}

	private void createPath() {
		String url = String.format(URLSource.PATHS_CREATE, getUserId());
		
        HttpUtil.post(url, null, null, new APIResponseHandler(getActivity()) {
        	
            @Override
            public void onSuccess(JSONObject response) {
                PathManager.getInstance().registerPath(getActivity(), Path.build(response));
            }
        });
	}
	
	private String getUserId() {
		return AccountManager.getInstance().getUserId(getActivity());
	}
	
	private void dispatchRegisterHome(Intent intent) {
		//TODO home 정보를 가져와서 입력. 아니면 Flag 만 넘겨서...
		intent = new Intent(getActivity(), RegisterHomeActivity.class);
		intent.putExtra("prev", "user_path" );
		startActivityForResult(intent, REGISTER_HOME);
	}
	
	private void dispatchRegisterDestination(Intent intent) {
		intent = new Intent(getActivity(),RegisterDestinationActivity.class);
		startActivityForResult(intent, REGISTER_DESTINATION);
	}
	
	private class ButtonClickHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int buttonId = v.getId();
			Intent intent = null;
			switch (buttonId) {
			case R.id.user_path_home_button_top:
				dispatchRegisterHome(intent);
				break;
			case R.id.user_path_home_button_bottom:
				dispatchRegisterHome(intent);
				break;
			case R.id.user_path_add_place_button:
				dispatchRegisterDestination(intent);
				break;
			default:
				break;
			}

		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == getActivity().RESULT_OK) {
			switch (requestCode) {
			case REGISTER_HOME:
				
				break;

			case REGISTER_DESTINATION:
				
				break;

			default:
				break;
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_user_path, container,
				false);
		initResources(view);
		initEvents();
		setDate();
		return view;
	}
	private void setDate() {
		Time now = new Time();
		now.setToNow();
		
		//todayDate.setText(text);
	}
	private void initResources(View view) {
		userPathHomeButtonTop = (Button) view
				.findViewById(R.id.user_path_home_button_top);
		userPathAddPlaceBotton = (Button) view
				.findViewById(R.id.user_path_add_place_button);
		userPathHomeButtonBottom = (Button) view
				.findViewById(R.id.user_path_home_button_bottom);
		todayDate = (TextView) view.findViewById(R.id.today_date_text);
	}
	
	private void initEvents() {
		userPathHomeButtonTop.setOnClickListener(new ButtonClickHandler());
		userPathAddPlaceBotton.setOnClickListener(new ButtonClickHandler());
		userPathHomeButtonBottom.setOnClickListener(new ButtonClickHandler());
	}
	
	private void showToastMsg(String msg){
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

}
