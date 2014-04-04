package com.example.daysjourney.user;

import com.example.daysjourney.R;
import com.example.daysjourney.R.id;
import com.example.daysjourney.R.layout;
import com.example.daysjourney.user.SignInActivity;
import com.example.daysjourney.user.SignUpActivity;
import com.example.daysjourney.user.UserPageActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * Activity for the user path page.
 * User can set home information and 
 * the places where they want to go.
 * TODO: When a new place is added, the 
 * new place information should be shown,
 * and the sequence problem also should be solved.
 *
 */
public class UserPathActivity extends FragmentActivity{


	private class ButtonClickHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int buttonId = v.getId();
			Intent intent = null;
			switch (buttonId) {
			case R.id.user_path_home_button_top:
				Toast.makeText(getApplicationContext(), "top", Toast.LENGTH_SHORT).show();
				intent = new Intent(UserPathActivity.this, RegisterHomeActivity.class);
				startActivity(intent);
				break;
			case R.id.user_path_home_button_bottom:
				Toast.makeText(getApplicationContext(), "bottom", Toast.LENGTH_SHORT).show();
				intent = new Intent(UserPathActivity.this, RegisterHomeActivity.class);
				startActivity(intent);
				break;
			case R.id.user_path_add_place_button:
				Toast.makeText(getApplicationContext(), "add", Toast.LENGTH_SHORT).show();
				intent = new Intent(UserPathActivity.this, RegisterDestinationActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_path);
		
		Button userPathHomeButtonTop = (Button) this.findViewById(R.id.user_path_home_button_top);
		Button userPathHomeButtonBottom = (Button) this.findViewById(R.id.user_path_home_button_bottom);
		
		Button userPathAddPlaceButton = (Button) this.findViewById(R.id.user_path_add_place_button);
		
		userPathHomeButtonTop.setOnClickListener(new ButtonClickHandler());
		userPathHomeButtonBottom.setOnClickListener(new ButtonClickHandler());
		userPathAddPlaceButton.setOnClickListener(new ButtonClickHandler());

	}


}















