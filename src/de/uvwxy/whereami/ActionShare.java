package de.uvwxy.whereami;

import android.app.Activity;
import android.location.Location;
import de.uvwxy.helper.IntentTools;
import de.uvwxy.helper.IntentTools.ReturnStringCallback;

public class ActionShare {
	public static void share(final Activity act, final Location loc) {
		if (act == null || loc == null) {
			return;
		}

		ReturnStringCallback selected = new ReturnStringCallback() {

			@Override
			public void result(String s) {
				int type = IntentTools.TYPE_GMAPS;
				if (act.getString(R.string.googlemaps).equals(s)) {
					type = IntentTools.TYPE_GMAPS;
				} else if (act.getString(R.string.openstreetmap).equals(s)) {
					type = IntentTools.TYPE_OSM;
				}

				IntentTools.shareLocation(act, loc, type, act.getString(R.string.shared_location), //
						act.getString(R.string.LBL_LATITUDE), //
						act.getString(R.string.LBL_LONGITUDE), //
						act.getString(R.string.LBL_ALTITUDE), //
						act.getString(R.string.LBL_BEARING), //
						act.getString(R.string.LBL_ACCURACY), //
						act.getString(R.string.LBL_SPEED), //
						act.getString(R.string.length_of_shared_message), //
						act.getString(R.string.select_share_app));

			}
		};
		IntentTools.userSelectString(act, act.getString(R.string.select_a_provider), //
				new String[] { act.getString(R.string.googlemaps), act.getString(R.string.openstreetmap) }, selected);
	}

	public static void share(Activity parent, de.uvwxy.whereami.proto.Messages.Location location) {
		// convert from proto to android.location
		share(parent, Converter.createLoc(location));
	}
}
