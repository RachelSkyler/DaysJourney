package com.example.daysjourney.user;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.daysjourney.R;
import com.example.daysjourney.core.AccountManager;
import com.example.daysjourney.entity.User;
import com.example.daysjourney.network.APIResponseHandler;
import com.example.daysjourney.network.HttpUtil;
import com.example.daysjourney.network.URLSource;
import com.loopj.android.http.RequestParams;

/**
 * Activity for user sign up.
 * Check whether all the required information are filled.
 * If not, show error icon to the user.
 * If sign up succeeds, go to the destination registering page for home registration
 */
public class SignUpActivity extends Activity implements View.OnClickListener {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * Used for checking whether the user exists already.
	 * Will delete it after connecting with server.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"foo@example.com:hello", "bar@example.com:world" };
	
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	//private UserLoginTask mAuthTask = null;

	// Values for email password and password confirmation at the time of the sign up attempt
	private EditText mUsernameView;
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mPasswordConfirmView;
	private View mSignUpFormView;
	private View mSignUpStatusView;
	private TextView mSignUpStatusMessageView;
	
	private Button mBtnSignUp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE); Hide title bar.
		setContentView(R.layout.activity_sign_up);

		// Set up the sign up form
		initResources();
		initEvents(); 
	}
	
	private void initResources() {
		mUsernameView = (EditText) findViewById(R.id.username);
		mEmailView = (EditText) findViewById(R.id.email);
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordConfirmView = (EditText) findViewById(R.id.password_confirm);
		
		mSignUpFormView = findViewById(R.id.sign_up_form);
		mSignUpStatusView = findViewById(R.id.sign_up_status);
		mSignUpStatusMessageView = (TextView) findViewById(R.id.sign_up_status_message);
		
		mBtnSignUp = (Button)findViewById(R.id.sign_up_button);
	}
	
	private void initEvents() {
		mBtnSignUp.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptSignUp();
					}
				});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
	}
	
	private String getEmail() {
		return mEmailView.getText().toString();
	}
	
	private String getPassword() {
		return mPasswordView.getText().toString();
	}
	
	private String getPasswordConfirm(){
		return mPasswordConfirmView.getText().toString();
	}
	
	private String getUsername(){
		return mUsernameView.getText().toString();
	}
	
	private boolean isEmailFormFilled() {
		return !(getEmail().equals(""));
	}
	
	private boolean isPasswordFormFilled() {
		return !(getPassword().equals(""));
	}
	
	private boolean isPasswordConfirmFormFilled() {
		return !(getPasswordConfirm().equals(""));
	}
	
	private boolean isPasswordConfirmCorrected() {
		return getPassword().equals(getPasswordConfirm());
	}
	
	private boolean isValidEmail() {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches();
	}
	
	private boolean isValidPassword() {
	    return getPassword().length() >= 4  && getPassword().length() <= 128;
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		// Reset errors
		mEmailView.setError(null);
		mPasswordView.setError(null);
		mPasswordConfirmView.setError(null);
		
		View focusView = null;
		boolean cancel = false;
		
		switch(view.getId()) {
		case R.id.sign_up_button:
			if(!isEmailFormFilled()) {
				mEmailView.setError(getString(R.string.error_field_required));
				focusView = mEmailView;
				cancel = true;
			} else if(!isPasswordFormFilled()) {
				mPasswordView.setError(getString(R.string.error_field_required));
				focusView = mPasswordView;
				cancel = true;
			} else if(!isPasswordConfirmFormFilled()) {
				mPasswordConfirmView.setError(getString(R.string.error_field_required));
				focusView = mPasswordConfirmView;
				cancel = true;
			} else if(!isValidEmail()) {
				mEmailView.setError(getString(R.string.error_invalid_email));
				focusView = mEmailView;
				cancel = true;
			} else if(!isValidPassword()) {
				mPasswordConfirmView.setError(getString(R.string.error_invalid_password));
				focusView = mPasswordConfirmView;
				cancel = true;
			} else {
				attemptSignUp();
			}
			
			if (cancel) {
				// There was an error; don't attempt sign up and focus the first
				// form field with an error.
				focusView.requestFocus();
			}
		}
	}
	
	
	/**
	 * Attempts to sign up the account specified by the sign up form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual sign up attempt is made.
	 */
	public void attemptSignUp() {
		String url = URLSource.SIGN_UP;

		// Store values at the time of the sign up attempt
		RequestParams params = new RequestParams();
		params.put(User.EMAIL, getEmail());
		params.put(User.PASSWORD, getPassword());
		params.put(User.USER_NAME, getUsername());
	
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/json");
		headers.put("Content-type", "application/json");
		
		HttpUtil.post(url, headers, params, new APIResponseHandler(SignUpActivity.this){

			@Override
			public void onFinish() {
				super.onFinish();
				// Show a progress spinner, and kick off a background task to
				// perform the user login attempt.
				mSignUpStatusMessageView.setText(R.string.sign_up_progress);
				showProgress(true);
				//TODO: if view loading layout is made, then implement this method.
				//showLoading(); 
			}

			@Override
			public void onStart() {
				super.onStart();
				//hideLoading();
				
			}

			@Override
			public void onSuccess(JSONObject response) {
				setResult(Activity.RESULT_OK);
				AccountManager.getInstance().signIn(SignUpActivity.this, User.build(response));
				UserPageActivity.isSignedIn = true;
				showProgress(false);
				finish();
			}
			
		}); 
	}

	/**
	 * Shows the progress UI and hides the sign up form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mSignUpStatusView.setVisibility(View.VISIBLE);
			mSignUpStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mSignUpStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mSignUpFormView.setVisibility(View.VISIBLE);
			mSignUpFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mSignUpFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components
			mSignUpStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
}
