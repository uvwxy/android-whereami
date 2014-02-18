package de.uvwxy.whereami.fragments;

import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import de.uvwxy.whereami.ActivityMain;
import de.uvwxy.whereami.proto.Messages;

public class FragmentOverlaySupportMap extends SupportMapFragment {
	private GoogleMap mMapView = null;

	public void onViewCreated(android.view.View view, android.os.Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mMapView = getMap();
		loadMarkers(mMapView);
	};

	private void loadMarkers(GoogleMap map) {
		if (map == null) {
			return;
		}
		loadMarkers(map, true);
		loadMarkers(map, false);
	}

	private void loadMarkers(GoogleMap map, boolean favorite) {
		ArrayList<Messages.Location> list = new ArrayList<Messages.Location>();
		boolean all = false;
		boolean fav = favorite;
		ActivityMain.mData.getAllEntries(list, all, fav);

		for (Messages.Location l : list) {
			MarkerOptions options = new MarkerOptions();
			options.position(new LatLng(l.getLatitude(), l.getLongitude()));
			options.draggable(false);
			if (favorite) {
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
			} else {
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			}
			map.addMarker(options);
		}

	}
}