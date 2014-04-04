package com.example.daysjourney.user;

import com.example.daysjourney.R;
import com.example.daysjourney.R.id;
import com.example.daysjourney.R.layout;
import com.example.daysjourney.R.menu;
import com.example.daysjourney.R.string;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity for user sign up.
 * Check whether all the required information are filled.
 * If not, show error icon to the user.
 * If sign up succeeds, go to the destination registering page for home registration
 */
public class SignUpActivity extends Activity {
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
	private UserLoginTask mAuthTask = null;

	// Values for email password and password confirmation at the time of the sign up attempt
	private String mEmail;
	private String mPassword;
	private String mPasswordConfirm;

	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mPasswordConfirmView;
	private View mSignUpFormView;
	private View mSignUpStatusView;
	private TextView mSignUpStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sign_up);

		// Set up the sign up form
		mEmailView = (EditText) findViewById(R.id.email);
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordConfirmView = (EditText) findViewById(R.id.password_confirm);
		mPasswordConfirmView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.sign_up || id == EditorInfo.IME_NULL) {
							attemptSignUp();
							return true;
						}
						return false;
					}
				});

		mSignUpFormView = findViewById(R.id.sign_up_form);
		mSignUpStatusView = findViewById(R.id.sign_up_status);
		mSignUpStatusMessageView = (TextView) findViewById(R.id.sign_up_status_message);

		Button btn = (Button)findViewById(R.id.sign_up_button);
		System.out.println(btn == null);
		findViewById(R.id.sign_up_button).setOnClickListener(
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

	/**
	 * Attempts to sign up the account specified by the sign up form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual sign up attempt is made.
	 */
	public void attemptSignUp() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors
		mEmailView.setError(null);
		mPasswordView.setError(null);
		mPasswordConfirmView.setError(null);

		// Store values at the time of the sign up attempt
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mPasswordConfirm = mPasswordConfirmView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid email address
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}
		
		// Check for a valid password
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}
		
		// Check for a valid password confirmation
		if(TextUtils.isEmpty(mPasswordConfirm)){
			mPasswordConfirmView.setError(getString(R.string.error_field_required));
			focusView = mPasswordConfirmView;
			cancel = true;
		}else if(mPasswordConfirm.length() < 4){
			mPasswordConfirmView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordConfirmView;
			cancel = true;
		}else if(!TextUtils.equals(mPassword, mPasswordConfirm)){
			mPasswordConfirmView.setError(getString(R.string.error_incorrect_password_confirmation));
			focusView = mPasswordConfirmView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt sign up and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mSignUpStatusMessageView.setText(R.string.sign_up_progress);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
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

	/**
	 * Represents an asynchronous sign up task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			
			// TODO: attempt authentication against a network service
			try {
				// Simulate network access
				// Communicate with our server here
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}
			
			// TODO: should check whether the email has already been signed up before
			// In our project, we will get the information in JSON format
			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(mEmail)) {
					// Account email exists, return true if the password matches
					return pieces[1].equals(mPassword);
				}
			}

			// TODO: register the new account here
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				// TODO: Both sign UP and sign IN are supposed to be completed
				// automatically and go to the user destination registration page
				// TRANSACTION is needed 
				UserPageActivity.isSignedIn = true;
				Intent intent = new Intent(SignUpActivity.this, RegisterHomeActivity.class);
				intent.putExtra("email", mEmail);
				startActivity(intent);
				finish();
			} else {
				mPasswordView
						.setError(getString(R.string.error_sign_up_failed));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
