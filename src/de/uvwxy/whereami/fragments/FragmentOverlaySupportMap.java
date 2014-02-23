package de.uvwxy.whereami.fragments;

import java.util.ArrayList;

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
import de.uvwxy.whereami.db_location.Location;

public class FragmentOverlaySupportMap extends SupportMapFragment {
	public GoogleMap mMapView = null;
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
				android.location.Location l = ActivityMain.dhis.mLastLocation;
				if (l != null) {
					android.location.Location t = new android.location.Location("");
					t.setLatitude(point.latitude);
					t.setLongitude(point.longitude);
					dist = l.distanceTo(t);
				}
				SoundFinder.findNode(getActivity(), point.latitude, point.longitude, 0, 20, 30, dist);
			}
		});
		mMapView.getUiSettings().setMyLocationButtonEnabled(true);
		mMapView.getUiSettings().setCompassEnabled(true);
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
		ArrayList<Location> list = new ArrayList<Location>();
		boolean all = false;
		boolean fav = favorite;
		ActivityMain.dhis.mData.getAllEntries(list, all, fav);

		for (Location l : list) {
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
