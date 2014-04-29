package com.example.daysjourney.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
public class EnvironmentInfoFragment extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_environment_info, container, false);
		
		return view;
	}
	
	public static EnvironmentInfoFragment newInstance(int position){
		EnvironmentInfoFragment frg = new EnvironmentInfoFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		frg.setArguments(bundle);
		return frg;
	}


}















