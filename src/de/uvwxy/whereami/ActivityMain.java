package de.uvwxy.whereami;

import java.util.Locale;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import de.uvwxy.helper.IntentTools;
import de.uvwxy.sensors.location.LocationReader;
import de.uvwxy.whereami.db_location.DBLocationConnection;

public class ActivityMain extends FragmentActivity {
	public static final String SETTINGS = "WAI_SETTINGS";

	public static final String SETTINGS_UPDATES_ON_STARTUP = "WAI_UPDATES_ON_STARTUP";
	public static final boolean SETTINGS_UPDATES_ON_STARTUP_DEF = true;

	public static final String SETTINGS_STOP_UPDATES_ONPAUSE = "SETTINGS_STOP_UPDATES_ONPAUSE";
	public static final boolean SETTINGS_STOP_UPDATES_ONPAUSE_DEF = false;

	public static final String SETTINGS_USE_GPS = "SETTINGS_USE_GPS";
	public static final boolean SETTINGS_USE_GPS_DEF = true;

	public static final String SETTINGS_USE_WIFI = "SETTINGS_USE_WIFI";
	public static final boolean SETTINGS_USE_WIFI_DEF = false;

	public static final String SETTINGS_LENGHT_TYPE = "SETTINGS_LENGTH_TYPE";
	public static final int SETTINGS_METRES = 0;
	public static final int SETTINGS_KILOMETRES = 1;
	public static final int SETTINGS_FOOT = 2;
	public static final int SETTINGS_MILES = 3;
	public static final int SETTINGS_LENGHT_TYPE_DEF = SETTINGS_METRES;

	public static final String SETTINGS_VELOCTIY_TYPE = "SETTINGS_VELOCITY_TYPE";
	public static final int SETTINGS_METRES_PER_SECOND = 0;
	public static final int SETTINGS_KMH = 1;
	public static final int SETTINGS_MPH = 2;
	public static final int SETTINGS_VELOCITY_TYPE_DEF = SETTINGS_METRES_PER_SECOND;

	public static final String SETTINGS_ANGLE_TYPE = "SETTINGS_ANGLE_TYPE";
	public static final int SETTINGS_ANGLE_DEGREES = 0;
	public static final int SETTINGS_ANGLE_MINUTES_SECONDS = 1;

	public static final int SETTINGS_ANGLE_TYPE_DEF = SETTINGS_ANGLE_DEGREES;

	public static boolean locationUpdatesEnabled = false;

	public static Unit<Velocity> unitV = null;
	public static Unit<Length> unitL = null;
	public static Unit<Angle> unitA = null;
	public static int unitLBreak = 1500;

	public static DBLocationConnection data = null;
	public static de.uvwxy.whereami.LocationManager loc = null;

	public static Bus bus = new Bus();
	private static Context ctx;
	public static ActivityMain dhis = null;

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	public static Location lastLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dhis = this;
		ctx = getApplicationContext();

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		data = new DBLocationConnection(this);
		data.openWrite();
		loc = new de.uvwxy.whereami.LocationManager(getApplicationContext());

		SharedPreferences prefs = IntentTools.getSettings(getApplicationContext(), SETTINGS);
		boolean startup_updates = prefs.getBoolean(SETTINGS_UPDATES_ON_STARTUP, SETTINGS_UPDATES_ON_STARTUP_DEF);

		if (startup_updates) {
			locationUpdatesEnabled = true;
			loc.getReader().startReading();
		}

		int t = prefs.getInt(SETTINGS_LENGHT_TYPE, SETTINGS_LENGHT_TYPE_DEF);
		switch (t) {
		case SETTINGS_METRES:
			unitL = SI.METRE;
			break;
		case SETTINGS_KILOMETRES:
			unitL = SI.KILOMETRE;
			break;
		case SETTINGS_FOOT:
			unitL = NonSI.FOOT;
			break;
		case SETTINGS_MILES:
			unitL = NonSI.MILE;
			break;
		default:
			unitL = SI.METRE;
		}
		t = prefs.getInt(SETTINGS_VELOCTIY_TYPE, SETTINGS_VELOCITY_TYPE_DEF);
		switch (t) {
		case SETTINGS_METRES_PER_SECOND:
			unitV = SI.METERS_PER_SECOND;
			break;
		case SETTINGS_KMH:
			unitV = NonSI.KILOMETRES_PER_HOUR;
			break;
		case SETTINGS_MPH:
			unitV = NonSI.MILES_PER_HOUR;
			break;
		default:
			unitV = SI.METERS_PER_SECOND;
		}

		t = prefs.getInt(SETTINGS_ANGLE_TYPE, SETTINGS_ANGLE_TYPE_DEF);
		switch (t) {
		case SETTINGS_ANGLE_DEGREES:
			unitA = NonSI.DEGREE_ANGLE;
			break;
		case SETTINGS_ANGLE_MINUTES_SECONDS:
			unitA = NonSI.DEGREE_ANGLE.compound(NonSI.MINUTE_ANGLE).compound(NonSI.SECOND_ANGLE);
			break;
		default:
			unitA = NonSI.DEGREE_ANGLE;
		}

		bus.register(this);

		alertIfProviderIsNotEnabled();
	}

	@Subscribe
	public void onReceive(Location l) {
		lastLocation = l;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (locationUpdatesEnabled) {
			loc.getReader().startReading();
		}
		bus.post(new BusUpdateList());
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (IntentTools.getSettings(ctx, SETTINGS) //
				.getBoolean(SETTINGS_STOP_UPDATES_ONPAUSE, SETTINGS_STOP_UPDATES_ONPAUSE_DEF)) {
			loc.getReader().stopReading();
			locationUpdatesEnabled = false;
			if (FragmentCurrentLocation.swUpdates != null) {
				FragmentCurrentLocation.swUpdates.setChecked(false);
			}
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		loc.getReader().stopReading();
		loc.destroy();
		data.close();
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
				FragmentSavedLocations t1 = new FragmentSavedLocations();
				t1.setFav(false);
				return t1;
			case 2:
				FragmentSavedLocations t2 = new FragmentSavedLocations();
				t2.setFav(true);
				return t2;
			case 3:
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

	private void alertIfProviderIsNotEnabled() {
		boolean provEnabledGPS = LocationReader.isEnabled(this, LocationManager.GPS_PROVIDER);
		boolean provEnabledWiFi = LocationReader.isEnabled(this, LocationManager.NETWORK_PROVIDER);

		SharedPreferences pref = IntentTools.getSettings(this, SETTINGS);
		boolean setEnabledGPS = pref.getBoolean(SETTINGS_USE_GPS, SETTINGS_USE_GPS_DEF);
		boolean setEnabledWiFi = pref.getBoolean(SETTINGS_USE_WIFI, SETTINGS_USE_WIFI_DEF);
		pref = null;

		boolean showAlert = true;
		String locationProviderStateMessage = "Waiting for fix";
		if ((!provEnabledGPS && setEnabledGPS) && (!provEnabledWiFi && setEnabledWiFi)) {
			locationProviderStateMessage = "GPS+Network location provider is not enabled!\n\nLocation updates will not work as specified in the settings.";
		} else if (!provEnabledGPS && setEnabledGPS) {
			locationProviderStateMessage = "GPS location provider is not enabled!\n\nLocation updates will not work as specified in the settings.";
		} else if (!provEnabledWiFi && setEnabledWiFi) {
			locationProviderStateMessage = "Network location provider not enabled!\n\nLocation updates will not work as specified in the settings.";
		} else {
			showAlert = false;
		}

		if (showAlert) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setNegativeButton("OK", null);
			alertDialog.setMessage(locationProviderStateMessage);
			alertDialog.setTitle("Enable Provider");
			alertDialog.show();
		}
	}

}
