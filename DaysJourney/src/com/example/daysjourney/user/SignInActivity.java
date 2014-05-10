package com.example.daysjourney.user;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.daysjourney.R;
import com.example.daysjourney.common.MainActivity;
import com.example.daysjourney.core.AccountManager;
import com.example.daysjourney.entity.User;
import com.example.daysjourney.network.APIResponseHandler;
import com.example.daysjourney.network.HttpUtil;
import com.example.daysjourney.network.URL;
import com.example.daysjourney.util.UrlSource;
import com.loopj.android.http.RequestParams;

/**
 * Activity for user sign in page. Check whether all the required information
 * are filled. If not, show error icon to the user. If sign in succeeds, change
 * the static variable 'isSignedIn' in UserPathActivity to TRUE.
 */
public class SignInActivity extends Activity implements View.OnClickListener{
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sign_in);
		
		initResources();
		initEvents();
	}
	
	private void initResources() {
		mEmailView = (EditText) findViewById(R.id.email);
		mPasswordView = (EditText) findViewById(R.id.password);
		
		mLoginFormView = findViewById(R.id.sign_in_form);
		mLoginStatusView = findViewById(R.id.sign_in_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.sign_in_status_message);
	}
	
	private void initEvents(){
		findViewById(R.id.sign_in_button).setOnClickListener(this);
	}
	

	@Override
	public void onClick(View v) {
		// Reset errors
		mEmailView.setError(null);
		mPasswordView.setError(null);
		
		boolean cancel = false;
		View focusView = null;

		if (!isPasswordFormFilled()) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}
		if (!isEmailFormFilled()) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!isValidEmail()) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and start a background task to
			// perform the user login attempt
			mLoginStatusMessageView.setText(R.string.sign_in_progress);
			//mAuthTask = new UserLoginTask();
			//mAuthTask.execute((Void) null);
			
			
		}
		attemptLogin();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.sign_in, menu);
		return true;
	}
	
	private String getPassword() {
		return mPasswordView.getText().toString();
	}
	
	private String getEmail() {
		return mEmailView.getText().toString();
	}
	
	private boolean isValidEmail() {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches();
	}
	
	private boolean isEmailFormFilled() {
		return !getEmail().equals("");
	}
	
	private boolean isPasswordFormFilled() {
		return !getPassword().equals("");
	}
	/**
	 * Attempts to sign in the account specified by the login form. If there are
	 * form errors (invalid email, missing fields, etc.), errors are presented
	 * and no actual login attempt is made.
	 */
	public void attemptLogin() {
		String url = URL.SIGN_IN;
		// Store values at the time of the login attempt
		RequestParams params = new RequestParams();
	    params.put(User.EMAIL, getEmail());
	    params.put(User.PASSWORD, getPassword());
	    
	    Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/json");
		headers.put("Content-type", "application/json");
		
	    HttpUtil.post(url, null, params, new APIResponseHandler(SignInActivity.this) {

            @Override
            public void onStart() {
                super.onStart();
                showProgress(true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                showProgress(false);
            }

            @Override
            public void onSuccess(JSONObject response) {
                AccountManager.getInstance().signIn(SignInActivity.this, User.build(response));

                UserPageActivity.isSignedIn = true;
				Intent intent = new Intent(SignInActivity.this, UserPageActivity.class);
				startActivity(intent);
				setResult(RESULT_OK, null);
				finish();
            }
        });
	}

	/**
	 * Represents an asynchronous login task used to authenticate the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		String url = new UrlSource().getUrlRoot() + "test.iotweb";
		
		@Override
		protected Boolean doInBackground(Void... params) {
			StringBuilder strBuilder=new StringBuilder();
			
			HttpPost httpPost = new HttpPost(url);
			Log.i("signin", httpPost.getURI().toString());
			HttpClient client = new DefaultHttpClient();
			httpPost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");
			
			// TODO: attempt authentication against a network service
			try {
				// Simulate network access
				// Communicate with our server here
				// Check the ID & Password
				
				System.out.println(123);
				HttpResponse response=client.execute(httpPost);
				System.out.println(456);
				StatusLine statusLine=response.getStatusLine();
				int status=statusLine.getStatusCode();
				Log.d("DEBUG", "status="+status);
				
				if(status==200){
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String lv = "";
					while ((lv = reader.readLine()) != null) {
						strBuilder.append(lv);
					}
				}else {
					String lv="4";
					strBuilder.append(lv);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("ERROR",e.toString());
			}
			

			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				// If sign in succeeded, go to the user path page
				// Variable 'isSignedIn' defined in UserPathActivity changes
				UserPageActivity.isSignedIn = true;
				Intent intent = new Intent(SignInActivity.this,
						UserPageActivity.class);
				startActivity(intent);
				finish();
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
}
