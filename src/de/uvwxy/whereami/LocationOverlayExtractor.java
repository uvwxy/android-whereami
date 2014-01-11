package de.uvwxy.whereami;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import de.uvwxy.osmdroid.AOverlayExtractor;
import de.uvwxy.units.Unit;
import de.uvwxy.whereami.proto.Messages;

public class LocationOverlayExtractor extends AOverlayExtractor<Messages.Location> {

	@Override
	public Location getLocation(Messages.Location e) {
		return Converter.createLoc(e);
	}

	@Override
	public String getTitle(Context ctx, Messages.Location e) {
		return e.getName();
	}

	@Override
	public String getDescription(Context ctx, Messages.Location e) {
		StringBuilder b = new StringBuilder(100);
		//		b.append)

		b.append("Latitude: ") //
				.append(Unit.DEGREES.setPrecision(6).setValue(e.getLatitude()).to(ActivityMain.unitA).toString());
		b.append("\nLongitude: ").append(
				Unit.DEGREES.setPrecision(6).setValue(e.getLongitude()).to(ActivityMain.unitA).toString());
		b.append("\nBearing: ").append(Unit.DEGREES.setPrecision(1).setValue(e.getBearing()).toString());
		b.append("\nAltitude: ").append(Unit.METRE.setValue(e.getAltitude()).to(ActivityMain.unitL).toString());
		b.append("\nAccuracy: ").append(Unit.METRE.setValue(e.getAccuracy()).to(ActivityMain.unitL).toString());
		b.append("\nSpeed: ").append(Unit.METRES_PER_SECOND.setValue(e.getSpeed()).to(ActivityMain.unitV).toString());
		b.append("\nProvider: ").append(e.getProvider());

		if (ActivityMain.lastLocation != null) {
			Unit u = Unit.METRE.setValue(LocationManager.getDistanceTo(e, ActivityMain.lastLocation));
			u = u.to(ActivityMain.unitL);
			b.append("\nDistance: ").append(String.format("%s", u));
		} else {
			b.append("\nDistance: ").append("Waiting for fix");
		}

		return null;
	}

	@Override
	public Drawable getMapIcon(Context ctx, Messages.Location e) {
		return ctx.getResources().getDrawable(R.drawable.ic_bubble_loc);
	}

}
