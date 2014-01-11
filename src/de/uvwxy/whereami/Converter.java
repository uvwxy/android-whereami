package de.uvwxy.whereami;

import de.uvwxy.whereami.proto.Messages;

public class Converter {
	public static Messages.Location createLoc(String name, android.location.Location androidLocation) {
		Messages.Location.Builder b = Messages.Location.newBuilder();

		b.setLatitude(androidLocation.getLatitude());
		b.setLongitude(androidLocation.getLongitude());
		b.setAltitude(androidLocation.getAltitude());
		b.setBearing(androidLocation.getBearing());
		b.setAccuracy(androidLocation.getAccuracy());
		b.setTime(androidLocation.getTime());
		b.setSpeed(androidLocation.getSpeed());
		b.setProvider(androidLocation.getProvider());
		b.setName(name);

		return b.build();
	}

	public static android.location.Location createLoc(Messages.Location protoLocation) {
		android.location.Location b = new android.location.Location(protoLocation.getProvider());

		b.setLatitude(protoLocation.getLatitude());
		b.setLongitude(protoLocation.getLongitude());
		b.setAltitude(protoLocation.getAltitude());
		b.setBearing((float) protoLocation.getBearing());
		b.setAccuracy((float) protoLocation.getAccuracy());
		b.setTime(protoLocation.getTime());
		b.setSpeed((float) protoLocation.getSpeed());
		b.setProvider(protoLocation.getProvider());

		return b;
	}
}
