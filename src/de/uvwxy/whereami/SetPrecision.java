package de.uvwxy.whereami;

import javax.measure.DecimalMeasure;
import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public class SetPrecision {
	public static <Q extends Quantity> Measure<Double, Q> setDoubleTo(Measure<Double, Q> measureInUnits, int precision,
			Unit<Q> unit) {
		double x = measureInUnits.doubleValue(unit);

		x *= Math.pow(10, precision);
		long y = (long) x;
		x = y / Math.pow(10.0, precision);
		return DecimalMeasure.valueOf(x, unit);
	}
}
