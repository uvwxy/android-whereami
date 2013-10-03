package de.uvwxy.whereami2;

import java.util.Locale;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import de.uvwxy.helper.IntentTools;

public class ActivityMain extends FragmentActivity {
	public static final String SETTINGS = "WAI_SETTINGS";
	public static final String SETTINGS_UPDATES_ON_STARTUP = "WAI_UPDATES_ON_STARTUP";

	public static boolean update_location = false;

	private static WAIData data = new WAIData();
	public static WAILocation loc = null;

	public static Bus bus = new Bus();

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	private static Location lastLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		loc = new WAILocation(getApplicationContext());

		SharedPreferences prefs = IntentTools.getSettings(getApplicationContext(), SETTINGS);
		boolean startup_updates = prefs.getBoolean(SETTINGS_UPDATES_ON_STARTUP, true);

		if (startup_updates || update_location) {
			update_location = true;
			loc.getReader().startReading();
		}

		bus.register(this);
	}

	@Subscribe
	public void onReceive(Location l) {
		lastLocation = l;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		loc.getReader().stopReading();
		loc.destroy();
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			switch (position) {

			case 0:
				return new FragmentCurrentLocation();
			case 1:
				return new FragmentSavedLocations();
			case 2:
				return new FragmentSettings();
			}

			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return 4;
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
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_activity_main_dummy, container, false);
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

	public static class FragmentCurrentLocation extends Fragment {
		private ScrollView scrollView1 = null;
		private TextView tvLat = null;
		private TextView tvLon = null;
		private TextView tvAlt = null;
		private TextView tvBearing = null;
		private TextView tvSpeed = null;
		private TextView tvAcc = null;
		private TextView tvProvider = null;
		private Switch swUpdates = null;
		private Button btnSave = null;
		private Button btnMap = null;
		private Button btnSend = null;

		private void initGUI(View rootView) {
			scrollView1 = (ScrollView) rootView.findViewById(R.id.scrollView1);
			tvLat = (TextView) rootView.findViewById(R.id.tvLat);
			tvLon = (TextView) rootView.findViewById(R.id.tvLon);
			tvAlt = (TextView) rootView.findViewById(R.id.tvAlt);
			tvBearing = (TextView) rootView.findViewById(R.id.tvBearing);
			tvSpeed = (TextView) rootView.findViewById(R.id.tvSpeed);
			tvAcc = (TextView) rootView.findViewById(R.id.tvAcc);
			tvProvider = (TextView) rootView.findViewById(R.id.tvProvider);
			swUpdates = (Switch) rootView.findViewById(R.id.swUpdates);
			btnSave = (Button) rootView.findViewById(R.id.btnSave);
			btnMap = (Button) rootView.findViewById(R.id.btnMap);
			btnSend = (Button) rootView.findViewById(R.id.btnSend);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_current_location, container, false);

			initGUI(rootView);

			swUpdates.setChecked(update_location);
			swUpdates.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					update_location = isChecked;
					if (update_location) {
						ActivityMain.loc.getReader().startReading();
					} else {
						ActivityMain.loc.getReader().stopReading();
					}
				}
			});

			// TODO: handle button clicks: Save,  View on Map, Send
			onReceive(lastLocation != null ? lastLocation : new Location("[waiting]"));
			bus.register(this);
			return rootView;
		}

		@Subscribe
		public void onReceive(Location l) {
			tvLat.setText("" + l.getLatitude() + " °");
			tvLon.setText("" + l.getLongitude() + " °");
			tvAlt.setText("" + l.getAltitude() + " m");
			tvSpeed.setText("" + l.getSpeed() + " m/s");
			tvProvider.setText(l.getProvider());
			tvBearing.setText("" + l.getBearing() + " °");
			tvAcc.setText("" + l.getAccuracy() + " m");
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			bus.unregister(this);
		}
	}

	public static class FragmentSavedLocations extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_saved_locations, container, false);
			ListView lvSavedLocations = (ListView) rootView.findViewById(R.id.lvSavedLocations);
			TextView tvSavedLocationCount = (TextView) rootView.findViewById(R.id.tvSavedLocationCount);
			TextView tvSavedLocationMaxDistance = (TextView) rootView.findViewById(R.id.tvSavedLocationMaxDistance);

			tvSavedLocationCount.setText("" + data.getLocationCount());

			bus.register(this);
			return rootView;
		}

		@Subscribe
		public void onReceive(Location l) {

		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			bus.unregister(this);
		}
	}

	public static class FragmentSettings extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

			return rootView;
		}

	}
}
