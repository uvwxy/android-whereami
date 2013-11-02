package de.uvwxy.whereami;

import javax.measure.DecimalMeasure;
import javax.measure.Measure;
import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

public class UnitDisplay {
	public static String showDistance(double value, Unit<Length> unit, int converstionPoint) {
		if (unit.equals(SI.METRE) && value > converstionPoint) {
			unit = SI.KILOMETRE;
		}
		if (unit.equals(NonSI.FOOT) && value > converstionPoint) {
			unit = NonSI.MILE;
		}

		Measure<Double, Length> measureInUnits = DecimalMeasure.valueOf(value, SI.METRE);
		measureInUnits = SetPrecision.setDoubleTo(measureInUnits, 2, unit);
		return measureInUnits.toString();
	}

	public static String showVelocity(double value, Unit<Velocity> unit) {
		Measure<Double, Velocity> measureInUnits = DecimalMeasure.valueOf(value, unit);
		measureInUnits = SetPrecision.setDoubleTo(measureInUnits, 2, unit);
		return measureInUnits.toString();
	}

	public static String showCoord(double valueDegreeAngle, Unit<javax.measure.quantity.Angle> unit) {
		Measure<Double, javax.measure.quantity.Angle> measureInUnits = //
		Measure.valueOf(valueDegreeAngle, NonSI.DEGREE_ANGLE);

		measureInUnits.to(unit);
		measureInUnits = SetPrecision.setDoubleTo(measureInUnits, 2, unit);
		return measureInUnits.toString();
	}
}
