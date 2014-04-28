package com.example.daysjourney.user;

import java.util.Locale;

import com.example.daysjourney.R;
import com.example.daysjourney.R.id;
import com.example.daysjourney.R.layout;
import com.example.daysjourney.R.menu;
import com.example.daysjourney.R.string;
import com.example.daysjourney.common.MainActivity;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO: Check whether the user is signed in or not
		// If not signed in, go to the main activity (main home page) to
		// sign in or sign up
		// Simulation of signed in or signed out
		if (!isSignedIn) {
			Intent intent = new Intent(UserPageActivity.this,
					MainActivity.class);
			startActivity(intent);
			finish();
		}

		setContentView(R.layout.activity_user_path_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

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
			return PlaceholderFragment.newInstance(position + 1);
		}

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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			int pageNum = getArguments().getInt(ARG_SECTION_NUMBER);
			System.out.println(pageNum);
			View rootView = null;
			switch (pageNum) {
			case 1:
				rootView = inflater.inflate(R.layout.activity_user_path,
						container, false);
				break;
			case 2:
				rootView = inflater.inflate(R.layout.activity_environment_info,
						container, false);
			case 3:
				rootView = inflater.inflate(R.layout.activity_environment_control,
						container, false);
			default:
				break;
			}
			return rootView;
		}
	}

	private void showToastMsg(String msg) {
		Toast.makeText(getApplication(), msg, Toast.LENGTH_LONG).show();
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
