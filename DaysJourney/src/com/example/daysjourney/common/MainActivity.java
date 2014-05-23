package com.example.daysjourney.common;

import com.example.daysjourney.R;
import com.example.daysjourney.R.id;
import com.example.daysjourney.R.layout;
import com.example.daysjourney.entity.User;
import com.example.daysjourney.user.RegisterHomeActivity;
import com.example.daysjourney.user.SignInActivity;
import com.example.daysjourney.user.SignUpActivity;
import com.example.daysjourney.user.UserPageActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Activity for the main home page.
 * With two buttons for going to both sign up and sign in pages.
 * If user is signed in, this page will be skipped.
 * @author RachelSkyler
 *
 */
public class MainActivity extends Activity implements View.OnClickListener{
	public static final int REQUEST_SIGN_IN = 0;
	public static final int REQUEST_SIGN_UP = 1;
	public static final int REQUEST_REGISTER_HOME = 2;
	public static final int REQUEST_USER_PAGE = 3;
	
	private Button mSignUpBtn;
	private Button mSignInBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// If user is signed in, do not show this page
		if(UserPageActivity.isSignedIn == true)
			finish();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_home);
		
		initResources();
		initEvents();	
	}
	
	private void initResources(){
		mSignUpBtn = (Button) this.findViewById(R.id.sign_up_page_button);
		mSignInBtn = (Button) this.findViewById(R.id.sign_in_page_button);

	}
	
	private void initEvents(){
		mSignUpBtn.setOnClickListener(this);
		mSignInBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.sign_in_page_button:
			dispatchSignIn();
			break;

		case R.id.sign_up_page_button:
			dispatchSignUp();
			break;
		}
	}

	private void dispatchSignUp() {
		Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
		startActivityForResult(intent, REQUEST_SIGN_UP);
	}

	private void dispatchSignIn() {
		Intent intent = new Intent(MainActivity.this, SignInActivity.class);
		startActivityForResult(intent, REQUEST_SIGN_IN);
	}
	
	private void dispatchRegisterHome() {
		Intent intent = new Intent(MainActivity.this, RegisterHomeActivity.class);
		//intent.putExtra("email", getEmail());
		startActivityForResult(intent, REQUEST_REGISTER_HOME);
	}
	
	private void dispatchUserPage() {
		Intent intent = new Intent(MainActivity.this, UserPageActivity.class);
		startActivityForResult(intent, REQUEST_USER_PAGE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_SIGN_IN) {
			if (resultCode == Activity.RESULT_OK) {
				dispatchUserPage();
				this.finish();
			}
		} else if (requestCode == REQUEST_SIGN_UP) {
			if (resultCode == Activity.RESULT_OK) {
				dispatchRegisterHome();
			}
		} else if (requestCode == REQUEST_REGISTER_HOME) {
			if (resultCode == Activity.RESULT_OK) {
				dispatchUserPage();
			}
		}
	}
	
	
}
