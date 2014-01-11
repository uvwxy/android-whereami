package de.uvwxy.whereami;

import java.util.Locale;

import android.app.Activity;
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

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import de.uvwxy.helper.IntentTools;
import de.uvwxy.sensors.location.LocationReader;
import de.uvwxy.units.Unit;
import de.uvwxy.units.UnitPrefix;
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
	public static final int SETTINGS_YARDS = 4;
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

	public static Unit unitV = null;
	public static Unit unitL = null;
	public static Unit unitA = null;
	public static int unitLBreak = 1500;

	public static DBLocationConnection data = null;
	public static de.uvwxy.whereami.LocationManager loc = null;

	public static Bus bus = new Bus();
	private static Context ctx;
	public static ActivityMain dhis = null;
	public static Activity act = null;

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	public static Location lastLocation;

	public static String mapConfig = "Mapnik";

	public static int geo6lat = 0;
	public static int geo6lon = 0;
	public static int zoomLevel = 8;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dhis = this;
		act = this;

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

		setUnits(prefs);

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
		setUnits();
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
				return new FragmentMap();
			case 4:
				return new FragmentSettings();
			}

			Fragment fragment = new FragmentSettings();
			return fragment;
		}

		@Override
		public int getCount() {
			return 5;
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

	public void setUnits() {
		setUnits(IntentTools.getSettings(getApplicationContext(), SETTINGS));
	}

	private void setUnits(SharedPreferences prefs) {
		readLength(prefs);
		readSpeed(prefs);
		readAngle(prefs);

	}

	private void readLength(SharedPreferences prefs) {
		int t = prefs.getInt(SETTINGS_LENGHT_TYPE, SETTINGS_LENGHT_TYPE_DEF);
		switch (t) {
		case SETTINGS_METRES:
			unitL = Unit.METRE.setPrefix(UnitPrefix.NONE);
			break;
		case SETTINGS_KILOMETRES:
			unitL = Unit.METRE.setPrefix(UnitPrefix.KILO);
			break;
		case SETTINGS_FOOT:
			unitL = Unit.FOOT;
			break;
		case SETTINGS_MILES:
			unitL = Unit.MILE;
			break;
		case SETTINGS_YARDS:
			unitL = Unit.YARD;
			break;
		default:
			unitL = Unit.METRE.setPrefix(UnitPrefix.NONE);
		}
	}

	private void readSpeed(SharedPreferences prefs) {
		int t = prefs.getInt(SETTINGS_VELOCTIY_TYPE, SETTINGS_VELOCITY_TYPE_DEF);
		switch (t) {
		case SETTINGS_METRES_PER_SECOND:
			unitV = Unit.METRES_PER_SECOND;
			break;
		case SETTINGS_KMH:
			unitV = Unit.KILOMETRES_PER_HOUR;
			break;
		case SETTINGS_MPH:
			unitV = Unit.MILES_PER_HOUR;
			break;
		default:
			unitV = Unit.METRES_PER_SECOND;
		}
	}

	private void readAngle(SharedPreferences prefs) {
		int t = prefs.getInt(SETTINGS_ANGLE_TYPE, SETTINGS_ANGLE_TYPE_DEF);
		switch (t) {
		case SETTINGS_ANGLE_DEGREES:
			unitA = Unit.DEGREES.setPrecision(6);
			break;
		case SETTINGS_ANGLE_MINUTES_SECONDS:
			unitA = Unit.DEGREES_MINUTES_SECONDS;
			break;
		default:
			unitA = Unit.DEGREES.setPrecision(6);
		}
	}

}
