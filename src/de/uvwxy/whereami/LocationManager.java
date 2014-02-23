package de.uvwxy.whereami;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import de.uvwxy.helper.IntentTools;
import de.uvwxy.sensors.location.GPSWIFIReader;
import de.uvwxy.sensors.location.LocationReader;
import de.uvwxy.sensors.location.LocationReader.LocationResultCallback;
import de.uvwxy.sensors.location.LocationReader.LocationStatusCallback;

public class LocationManager {
	private Context ctx = null;

	private LocationReader readerLocation = null;
	private LocationResultCallback cbResult = new LocationResultCallback() {

		@Override
		public void result(final android.location.Location l) {
			Handler h = new Handler(ctx.getMainLooper());
			h.post(new Runnable() {

				@Override
				public void run() {
					if (ActivityMain.dhis.fCurrentLocation != null) {
						ActivityMain.dhis.fCurrentLocation.updateLocation(l);
					}

				}
			});
		}
	};
	private LocationStatusCallback cbStatus = new LocationStatusCallback() {

		@Override
		public void status(android.location.Location l) {
		}
	};

	public LocationManager(Context ctx) {
		this.ctx = ctx;
		SharedPreferences prefs = IntentTools.getSettings(ctx, ActivityMain.SETTINGS);

		boolean useGPS = prefs.getBoolean(ActivityMain.SETTINGS_USE_GPS, true);
		boolean useWIFI = prefs.getBoolean(ActivityMain.SETTINGS_USE_WIFI, false);
		readerLocation = new GPSWIFIReader(ctx, 0, 0, cbStatus, cbResult, useGPS, useWIFI);
	}

	public LocationReader getReader() {
		return readerLocation;
	}

	public static double getDistanceTo(de.uvwxy.whereami.db_location.Location from, android.location.Location dest) {
		return Converter.createLoc(from).distanceTo(dest);
	}

	public static double getBearingTo(de.uvwxy.whereami.db_location.Location from, android.location.Location dest) {
		return Converter.createLoc(from).bearingTo(dest);
	}

	public double getDistanceToHome() {
		return 0007.007;
	}
}
