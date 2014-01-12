package de.uvwxy.whereami;

import org.osmdroid.views.MapView.Projection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import de.uvwxy.math.PointMath;
import de.uvwxy.osmdroid.CardOverlayObjectConverter;
import de.uvwxy.units.Unit;
import de.uvwxy.whereami.proto.Messages;

public class CardOverlayLocationConverter extends CardOverlayObjectConverter<Messages.Location> {

	public CardOverlayLocationConverter(de.uvwxy.whereami.proto.Messages.Location e) {
		super(e);
	}

	@Override
	public Location getLocation() {
		return Converter.createLoc(e);
	}

	@Override
	public String getTitle(Context ctx) {
		return e.getName();
	}

	@Override
	public String getDescription(Context ctx) {
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
	public Drawable getMapIcon(Context ctx) {
		return ctx.getResources().getDrawable(R.drawable.ic_bubble_loc);
	}

	public void draw(Canvas canvas, Point mCurScreenCoords, Projection pj) {
		Location tempLoc = getLocation();
		Paint p = new Paint();
		p.setColor(Color.GRAY);
		p.setAntiAlias(true);

		Log.d(this.getClass().getName(), "----------");
		Log.d(this.getClass().getName(), "Lat " + tempLoc.getLatitude());
		Log.d(this.getClass().getName(), "Lon " + tempLoc.getLongitude());
		Log.d(this.getClass().getName(), "x " + mCurScreenCoords.x);
		Log.d(this.getClass().getName(), "y " + mCurScreenCoords.y);

		canvas.drawCircle(mCurScreenCoords.x, mCurScreenCoords.y, 6, p);
		p.setStrokeWidth(1);
		p.setColor(Color.RED);
		canvas.drawLine(mCurScreenCoords.x, mCurScreenCoords.y - 3, mCurScreenCoords.x, mCurScreenCoords.y + 3, p);
		canvas.drawLine(mCurScreenCoords.x - 3, mCurScreenCoords.y, mCurScreenCoords.x + 3, mCurScreenCoords.y, p);

		double accuracy = tempLoc.getAccuracy();
		p.setColor(Color.YELLOW);
		p.setAlpha(50);
		canvas.drawCircle(mCurScreenCoords.x, mCurScreenCoords.y, (float) accuracy * pj.metersToEquatorPixels(1), p);
		p.setColor(Color.BLACK);
		p.setAlpha(255);
		p.setStyle(Style.STROKE);
		canvas.drawCircle(mCurScreenCoords.x, mCurScreenCoords.y, (float) accuracy * pj.metersToEquatorPixels(1), p);
		p.setStyle(Style.FILL_AND_STROKE);
		p.setColor(Color.GREEN);
		p.setStrokeWidth(3);

		float rotation = tempLoc.getBearing();
		Point rot = new Point();
		rot.x = mCurScreenCoords.x + 32;
		rot.y = mCurScreenCoords.y;

		// fix to compass 0 degrees going up
		rotation -= 90;
		rot = PointMath.rotate(rot, mCurScreenCoords, rotation);

		p.setColor(Color.BLACK);
		canvas.drawRect(0, 0, 100, 100, p);
		
		canvas.drawLine(mCurScreenCoords.x, mCurScreenCoords.y, rot.x, rot.y, p);
	}
}
