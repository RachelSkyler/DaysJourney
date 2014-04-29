package com.example.daysjourney.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.daysjourney.R;

/**
 * Activity for the user path page.
 * User can set home information and 
 * the places where they want to go.
 * TODO: When a new place is added, the 
 * new place information should be shown,
 * and the sequence problem also should be solved.
 *
 */
public class UserPathFragment extends Fragment{
	
	Button userPathHomeButtonTop;
	Button userPathAddPlaceBotton;
	Button userPathHomeButtonBottom;

	private class ButtonClickHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int buttonId = v.getId();
			Intent intent = null;
			switch (buttonId) {
			case R.id.user_path_home_button_top:
				intent = new Intent(getActivity(), RegisterHomeActivity.class);
				break;
			case R.id.user_path_home_button_bottom:
				intent = new Intent(getActivity(), RegisterHomeActivity.class);
				break;
			case R.id.user_path_add_place_button:
				intent = new Intent(getActivity(), RegisterDestinationActivity.class);
				break;
			default:
				break;
			}
			startActivity(intent);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_user_path, container, false);
		
		userPathHomeButtonTop = (Button) view.findViewById(R.id.user_path_home_button_top);
		userPathAddPlaceBotton = (Button) view.findViewById(R.id.user_path_add_place_button);
		userPathHomeButtonBottom = (Button) view.findViewById(R.id.user_path_home_button_bottom);
		
		userPathHomeButtonTop.setOnClickListener(new ButtonClickHandler());
		userPathAddPlaceBotton.setOnClickListener(new ButtonClickHandler());
		userPathHomeButtonBottom.setOnClickListener(new ButtonClickHandler());
		
		return view;
	}
	
	public static UserPathFragment newInstance(int position){
		UserPathFragment frg = new UserPathFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		frg.setArguments(bundle);
		return frg;
	}


}















