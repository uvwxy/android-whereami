package de.uvwxy.whereami.db_location;

import lombok.Getter;
import lombok.Setter;

public class Location {

	@Getter
	@Setter
	private double latitude;
	@Getter
	@Setter
	private double longitude;
	@Getter
	@Setter
	private double altitude;
	@Getter
	@Setter
	private double accuracy;
	@Getter
	@Setter
	private double bearing;
	@Getter
	@Setter
	private double speed;
	@Getter
	@Setter
	private long time;
	@Getter
	@Setter
	private String provider;
	@Getter
	@Setter
	private String name;
}
