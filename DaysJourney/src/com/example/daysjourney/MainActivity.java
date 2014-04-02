package com.example.daysjourney;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Activity for the very first main home page.
 * With two buttons for going to both sign up and sign in pages.
 * @author ajou
 *
 */
public class MainActivity extends Activity {

	public class ButtonClickHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int viewId = v.getId();
			switch (viewId) {
			case R.id.sign_in_page_button:
				Intent intent1 = new Intent(MainActivity.this, SignInActivity.class);
				startActivity(intent1);
				break;

			case R.id.sign_up_page_button:
				Intent intent2 = new Intent(MainActivity.this, SignUpActivity.class);
				startActivity(intent2);
				break;

			default:
				break;
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_home);

		Button signUpBtn = (Button) this.findViewById(R.id.sign_up_page_button);
		Button signInBtn = (Button) this.findViewById(R.id.sign_in_page_button);

		signUpBtn.setOnClickListener(new ButtonClickHandler());
		signInBtn.setOnClickListener(new ButtonClickHandler());
	}


}
