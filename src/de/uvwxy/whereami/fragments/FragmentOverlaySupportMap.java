package de.uvwxy.whereami.fragments;

import java.util.ArrayList;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.uvwxy.soundfinder.SoundFinder;
import de.uvwxy.whereami.ActivityMain;
import de.uvwxy.whereami.proto.Messages;

public class FragmentOverlaySupportMap extends SupportMapFragment {
	private GoogleMap mMapView = null;
	private OnMarkerClickListener mMarkerClick = new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker marker) {
			return false;
		}
	};

	public void onViewCreated(android.view.View view, android.os.Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mMapView = getMap();
		mMapView.setOnMarkerClickListener(mMarkerClick);
		mMapView.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng point) {
				float dist = 0;
				Location l = ActivityMain.mLastLocation;
				if (l != null) {
					Location t = new Location("");
					t.setLatitude(point.latitude);
					t.setLongitude(point.longitude);
					dist = l.distanceTo(t);
				}
				SoundFinder.findNode(getActivity(), point.latitude, point.longitude, 0, 20, 30, dist);
			}
		});
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
			options.title(l.getName());
			map.addMarker(options);
		}

	}
}
