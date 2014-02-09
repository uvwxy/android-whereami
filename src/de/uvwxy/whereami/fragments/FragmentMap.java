package de.uvwxy.whereami.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.uvwxy.helper.IntentTools;
import de.uvwxy.whereami.ActivityMain;
import de.uvwxy.whereami.proto.Messages;

public class FragmentMap extends Fragment {
	private ArrayList<Messages.Location> locationList = new ArrayList<Messages.Location>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//View rootView = inflater.inflate(R.layout.fragment_map, container, false);
		//		osmMap = (org.osmdroid.views.MapView) rootView.findViewById(R.id.osmMap);

		return null;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		saveMapLocation();
	}

	public void loadMapLocation() {
		SharedPreferences prefs = IntentTools.getSettings(getActivity(), ActivityMain.SETTINGS);
		//		IMapController osmMapController = osmMap.getController();
		//		GeoPoint mapCenter = new GeoPoint(prefs.getInt("E6LAT", 0), prefs.getInt("E6LON", 0));
		//		osmMapController.setCenter(mapCenter);
		//		osmMapController.setZoom(prefs.getInt("ZOOM", 4));
	}

	public void saveMapLocation() {
		//		IGeoPoint p = osmMap.getMapCenter();
		//		Editor e = IntentTools.getSettingsEditor(getActivity(), ActivityMain.SETTINGS);
		//		e.putInt("E6LAT", p.getLatitudeE6());
		//		e.putInt("E6LON", p.getLongitudeE6());
		//		e.putInt("ZOOM", osmMap.getZoomLevel());
		//		IntentTools.saveEditor(e);
	};

	public void navigateTo(Location l) {
		//		GeoPoint mapCenter = new GeoPoint(l.getLatitude() * 1000000, l.getLongitude() * 1000000);
		//		IMapController osmMapController = osmMap.getController();
		//		osmMapController.setCenter(mapCenter);
	}

	private void addMiniMapOverlay(Context ctx) {
		//		final MinimapOverlay miniMapOverlay = new MinimapOverlay(ctx, osmMap.getTileRequestCompleteHandler());
		//		osmMap.getOverlays().add(miniMapOverlay);
	}

}
