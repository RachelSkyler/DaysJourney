package user;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.daysjourney.R;

/**
 * Activity for the destination registration page. For the first goal of us,
 * Arduinos will only be equipped at home, not all destinations. Therefore, this
 * page will only get the user's email first, and get input of a location from
 * the user, then put it to the PATH table.
 */
public class RegisterDestinationActivity extends ActionBarActivity {

	/**
	 * FrameLayout of the map for registering a destination.
	 */
	FrameLayout mMapFrameLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_destination);

		Button searchLocationButton = (Button) this
				.findViewById(R.id.search_location_button);
		searchLocationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Go to the page for select a place
				
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register_destination, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
