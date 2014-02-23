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

import com.google.android.gms.maps.SupportMapFragment;

import de.uvwxy.cardpager.ActivityCardPager;
import de.uvwxy.cardpager.FragmentAbout;
import de.uvwxy.helper.IntentTools;
import de.uvwxy.sensors.location.LocationReader;
import de.uvwxy.units.Unit;
import de.uvwxy.units.UnitPrefix;
import de.uvwxy.whereami.db_location.DBLocationConnection;
import de.uvwxy.whereami.fragments.FragmentCurrentLocation;
import de.uvwxy.whereami.fragments.FragmentOverlaySupportMap;
import de.uvwxy.whereami.fragments.FragmentSavedLocations;
import de.uvwxy.whereami.fragments.FragmentSettings;

public class ActivityMain extends ActivityCardPager {
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

	public static boolean mLocationUpdatesEnabled = false;

	public static Unit mUnitV = null;
	public static Unit mUnitL = null;
	public static Unit mUnitA = null;
	public static int mUnitLBreak = 1500;

	public DBLocationConnection mData = null;
	public static de.uvwxy.whereami.LocationManager mLoc = null;

	private static Context ctx;
	public static ActivityMain dhis = null;
	public static Activity act = null;

	public Location mLastLocation;

	static SupportMapFragment fMap = null;
	FragmentAbout fAbout = null;
	FragmentSavedLocations fSavedLocations = null;
	FragmentSavedLocations fSavedLocationsFav = null;
	FragmentCurrentLocation fCurrentLocation = null;
	FragmentSettings fSettings = null;

	@Override
	public Fragment getFragment(int position) {
		switch (position) {

		case 0:
			if (fCurrentLocation == null) {
				fCurrentLocation = new FragmentCurrentLocation();
			}
			return fCurrentLocation;
		case 1:
			if (fSavedLocations == null) {
				fSavedLocations = new FragmentSavedLocations();
				fSavedLocations.setFav(false);
			}
			return fSavedLocations;
		case 2:
			if (fSavedLocationsFav == null) {
				fSavedLocationsFav = new FragmentSavedLocations();
				fSavedLocationsFav.setFav(true);
			}
			return fSavedLocationsFav;
		case 3:
			if (fMap == null) {
				fMap = new FragmentOverlaySupportMap();
			}
			return fMap;
		case 4:
			if (fSettings == null) {
				fSettings = new FragmentSettings();
			}
			return fSettings;
		case 5:
			if (fAbout == null) {
				fAbout = new FragmentAbout();
				fAbout.setTitle(getApplication().getText(R.string.app_name).toString());
				fAbout.setPackageName("de.uvwxy.whereami");
				fAbout.setMarketUrl("market://search?q=de.uvwxy.whereami&c=apps");
				fAbout.setAboutApp(getString(R.string.app_description));
				fAbout.setLicenses(new String[] { "lombok" });
			}
			return fAbout;
		}

		Fragment fragment = new FragmentSettings();
		return fragment;
	}

	@Override
	public CharSequence getFragmentTitle(int position) {
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
		case 4:
			return getString(R.string.title_section5).toUpperCase(l);
		case 5:
			return getString(R.string.title_section6).toUpperCase(l);
		}
		return null;
	}

	@Override
	public int getFragmentCount() {
		return 6;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dhis = this;
		act = this;

		ctx = getApplicationContext();

		mData = new DBLocationConnection(this);
		mData.openWrite();

		mLoc = new de.uvwxy.whereami.LocationManager(getApplicationContext());

		SharedPreferences prefs = IntentTools.getSettings(getApplicationContext(), SETTINGS);
		boolean startup_updates = prefs.getBoolean(SETTINGS_UPDATES_ON_STARTUP, SETTINGS_UPDATES_ON_STARTUP_DEF);

		if (startup_updates) {
			mLocationUpdatesEnabled = true;
			mLoc.getReader().startReading();
		}

		setUnits(prefs);

		alertIfProviderIsNotEnabled();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mLocationUpdatesEnabled) {
			mLoc.getReader().startReading();
		}
		setUnits();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (IntentTools.getSettings(ctx, SETTINGS) //
				.getBoolean(SETTINGS_STOP_UPDATES_ONPAUSE, SETTINGS_STOP_UPDATES_ONPAUSE_DEF)) {
			mLoc.getReader().stopReading();
			mLocationUpdatesEnabled = false;
			if (fCurrentLocation != null && fCurrentLocation.swUpdates != null) {
				fCurrentLocation.swUpdates.setChecked(false);
			}
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLoc.getReader().stopReading();
		mData.close();
	}

	private void alertIfProviderIsNotEnabled() {
		boolean provEnabledGPS = LocationReader.isEnabled(this, LocationManager.GPS_PROVIDER);
		boolean provEnabledWiFi = LocationReader.isEnabled(this, LocationManager.NETWORK_PROVIDER);

		SharedPreferences pref = IntentTools.getSettings(this, SETTINGS);
		boolean setEnabledGPS = pref.getBoolean(SETTINGS_USE_GPS, SETTINGS_USE_GPS_DEF);
		boolean setEnabledWiFi = pref.getBoolean(SETTINGS_USE_WIFI, SETTINGS_USE_WIFI_DEF);
		pref = null;

		boolean showAlert = true;
		String locationProviderStateMessage = getString(R.string.waiting_for_fix);
		if ((!provEnabledGPS && setEnabledGPS) && (!provEnabledWiFi && setEnabledWiFi)) {
			locationProviderStateMessage = getString(R.string.app_load_warn_no_gps_no_wifi);
		} else if (!provEnabledGPS && setEnabledGPS) {
			locationProviderStateMessage = getString(R.string.app_load_warn_no_gps);
		} else if (!provEnabledWiFi && setEnabledWiFi) {
			locationProviderStateMessage = getString(R.string.app_load_warn_no_wifi);
		} else {
			showAlert = false;
		}

		if (showAlert) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setNegativeButton(R.string.ok, null);
			alertDialog.setMessage(locationProviderStateMessage);
			alertDialog.setTitle(R.string.enable_provider);
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
			mUnitL = Unit.METRE.setPrefix(UnitPrefix.NONE);
			break;
		case SETTINGS_KILOMETRES:
			mUnitL = Unit.METRE.setPrefix(UnitPrefix.KILO);
			break;
		case SETTINGS_FOOT:
			mUnitL = Unit.FOOT;
			break;
		case SETTINGS_MILES:
			mUnitL = Unit.MILE;
			break;
		case SETTINGS_YARDS:
			mUnitL = Unit.YARD;
			break;
		default:
			mUnitL = Unit.METRE.setPrefix(UnitPrefix.NONE);
		}
	}

	private void readSpeed(SharedPreferences prefs) {
		int t = prefs.getInt(SETTINGS_VELOCTIY_TYPE, SETTINGS_VELOCITY_TYPE_DEF);
		switch (t) {
		case SETTINGS_METRES_PER_SECOND:
			mUnitV = Unit.METRES_PER_SECOND;
			break;
		case SETTINGS_KMH:
			mUnitV = Unit.KILOMETRES_PER_HOUR;
			break;
		case SETTINGS_MPH:
			mUnitV = Unit.MILES_PER_HOUR;
			break;
		default:
			mUnitV = Unit.METRES_PER_SECOND;
		}
	}

	private void readAngle(SharedPreferences prefs) {
		int t = prefs.getInt(SETTINGS_ANGLE_TYPE, SETTINGS_ANGLE_TYPE_DEF);
		switch (t) {
		case SETTINGS_ANGLE_DEGREES:
			mUnitA = Unit.DEGREES.setPrecision(6);
			break;
		case SETTINGS_ANGLE_MINUTES_SECONDS:
			mUnitA = Unit.DEGREES_MINUTES_SECONDS;
			break;
		default:
			mUnitA = Unit.DEGREES.setPrecision(6);
		}
	}

	public void updateLists() {
		if (fSavedLocations != null) {
			fSavedLocations.updateList();
		}

		if (fSavedLocationsFav != null) {
			fSavedLocationsFav.updateList();
		}
	}

	public void refreshLists() {
		if (fSavedLocations != null) {
			fSavedLocations.refreshList();
		}

		if (fSavedLocationsFav != null) {
			fSavedLocationsFav.refreshList();
		}
	}

}
