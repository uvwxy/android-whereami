package de.uvwxy.whereami2;

import com.squareup.otto.Subscribe;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import de.uvwxy.helper.IntentTools;
import de.uvwxy.sensors.location.GPSWIFIReader;
import de.uvwxy.sensors.location.LocationReader;
import de.uvwxy.sensors.location.LocationReader.LocationResultCallback;
import de.uvwxy.sensors.location.LocationReader.LocationStatusCallback;
import de.uvwxy.whereami2.proto.Messages;

public class WAILocation {

	private LocationReader readerLocation = null;
	private LocationResultCallback cbResult = new LocationResultCallback() {

		@Override
		public void result(Location l) {
			ActivityMain.bus.post(l);
		}
	};
	private LocationStatusCallback cbStatus = new LocationStatusCallback() {

		@Override
		public void status(Location l) {
		}
	};

	public WAILocation(Context ctx) {
		SharedPreferences prefs = IntentTools.getSettings(ctx, ActivityMain.SETTINGS);

		boolean useGPS = prefs.getBoolean(ActivityMain.SETTINGS_USE_GPS, true);
		boolean useWIFI = prefs.getBoolean(ActivityMain.SETTINGS_USE_WIFI, false);
		readerLocation = new GPSWIFIReader(ctx, 0, 0, cbStatus, cbResult, useGPS, useWIFI);
		ActivityMain.bus.register(this);

	}

	public void destroy() {
		ActivityMain.bus.unregister(this);
	}

	public LocationReader getReader() {
		return readerLocation;
	}

	public double getDistanceTo(Messages.Location loc) {
		return 1337.17;
	}

	public double getDistanceToHome() {
		return 0007.007;
	}
}
