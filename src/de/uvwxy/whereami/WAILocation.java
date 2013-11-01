package de.uvwxy.whereami;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import de.uvwxy.helper.IntentTools;
import de.uvwxy.sensors.location.GPSWIFIReader;
import de.uvwxy.sensors.location.LocationReader;
import de.uvwxy.sensors.location.LocationReader.LocationResultCallback;
import de.uvwxy.sensors.location.LocationReader.LocationStatusCallback;
import de.uvwxy.whereami.proto.Messages;

public class WAILocation {
	private Context ctx = null;

	private LocationReader readerLocation = null;
	private LocationResultCallback cbResult = new LocationResultCallback() {

		@Override
		public void result(final Location l) {
			Handler h = new Handler(ctx.getMainLooper());
			h.post(new Runnable() {

				@Override
				public void run() {
					ActivityMain.bus.post(l);
				}
			});
		}
	};
	private LocationStatusCallback cbStatus = new LocationStatusCallback() {

		@Override
		public void status(Location l) {
		}
	};

	public WAILocation(Context ctx) {
		this.ctx = ctx;
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

	public static double getDistanceTo(Messages.Location from, android.location.Location dest) {
		return Converter.createLoc(from).distanceTo(dest);
	}

	public static double getBearingTo(Messages.Location from, android.location.Location dest) {
		return Converter.createLoc(from).bearingTo(dest);
	}

	public double getDistanceToHome() {
		return 0007.007;
	}
}
