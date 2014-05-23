package com.example.daysjourney.user;

import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.example.daysjourney.R;
import com.example.daysjourney.common.MainActivity;
import com.example.daysjourney.core.AccountManager;
import com.example.daysjourney.entity.User;

/**
 * Activity for the user page. Whether user is signed in or not will be
 * checked at the very first. If signed in, the user's path will be displayed;
 * If not, user will be required to go to the main home page for sign in or sign
 * up, based on their choice. 
 * Three fragments for swipe view are contained in this page. 
 * First for the user's path, 
 * Second for the real-time information of user's home, 
 * Last for home control,
 * which will be controlled by three activities 
 * (UserPathActivity, EnvirontmentInfoActivity, EnvironmentControlActivity).
 * 
 */
public class UserPageActivity extends ActionBarActivity {
	/**
	 * This variable is used for checking whether signed in or not. If signed
	 * in, user path page will be shown. If not, user will go to the main home
	 * page (MainActivity) to sign up or sign in
	 **/
	public static boolean isSignedIn = false;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	String mUserId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO: Check whether the user is signed in or not
		// If not signed in, go to the main activity (main home page) to
		// sign in or sign up
		// Simulation of signed in or signed out
		if (!isSignedIn) {
			System.out.println("Logged out... Go back...");
			Intent intent = new Intent(UserPageActivity.this,
					MainActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_user_path_main);
		initResources();
	}
	
	private void initResources() {
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page
			// Return a PlaceholderFragment (defined as a static inner class
			// below)
			switch (position) {
			case 0:
				return UserPathFragment.newInstance(position + 1);
			case 1:
				return EnvironmentInsideInfoFragment.newInstance(position + 1);
			case 2:
				return EnvironmentOutsideInfoFragment.newInstance(position + 1);

			default:
				return null;
			}
			
		}
		
		/*
		 * The total page number of fragments
		 * (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#getCount()
		 */
		@Override
		public int getCount() {
			// Show 3 total pages
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
