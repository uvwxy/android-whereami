package de.uvwxy.whereami.fragments;

import java.util.ArrayList;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.uvwxy.helper.IntentTools;
import de.uvwxy.osmdroid.CardOverlay;
import de.uvwxy.soundfinder.SoundFinder;
import de.uvwxy.whereami.ActivityMain;
import de.uvwxy.whereami.CardOverlayLocationConverter;
import de.uvwxy.whereami.proto.Messages;

public class FragmentMap extends Fragment {
	private org.osmdroid.views.MapView osmMap;
	private CardOverlay<Messages.Location> locOverlay;
	private ArrayList<Messages.Location> locationList = new ArrayList<Messages.Location>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//View rootView = inflater.inflate(R.layout.fragment_map, container, false);
		//		osmMap = (org.osmdroid.views.MapView) rootView.findViewById(R.id.osmMap);
		osmMap = new MapView(getActivity(), 256);
		osmMap.setBuiltInZoomControls(false);
		osmMap.setMultiTouchControls(true);
		osmMap.setUseSafeCanvas(false);
		osmMap.setMaxZoomLevel(18);
		osmMap.setTileSource(TileSourceFactory.getTileSource(0));
		osmMap.getOverlays().clear();

		locOverlay = new CardOverlay<Messages.Location>(CardOverlayLocationConverter.class, getActivity(), osmMap,
				getActivity());
		ActivityMain.data.getAllEntries(locationList, true, false);
		locOverlay.replaceObjects(ActivityMain.act.getApplicationContext(), locationList);
		osmMap.getOverlays().add(locOverlay.getOverlay());
		addMiniMapOverlay(getActivity());
		
		// set map to saved location
		loadMapLocation();
		osmMap.invalidate();
		return osmMap;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		saveMapLocation();
	}

	public void loadMapLocation() {
		SharedPreferences prefs = IntentTools.getSettings(getActivity(), ActivityMain.SETTINGS);
		IMapController osmMapController = osmMap.getController();
		GeoPoint mapCenter = new GeoPoint(prefs.getInt("E6LAT", 0), prefs.getInt("E6LON", 0));
		osmMapController.setCenter(mapCenter);
		osmMapController.setZoom(prefs.getInt("ZOOM", 4));
	}

	public void saveMapLocation() {
		IGeoPoint p = osmMap.getMapCenter();
		Editor e = IntentTools.getSettingsEditor(getActivity(), ActivityMain.SETTINGS);
		e.putInt("E6LAT", p.getLatitudeE6());
		e.putInt("E6LON", p.getLongitudeE6());
		e.putInt("ZOOM", osmMap.getZoomLevel());
		IntentTools.saveEditor(e);
	};

	public void navigateTo(Location l) {
		GeoPoint mapCenter = new GeoPoint(l.getLatitude() * 1000000, l.getLongitude() * 1000000);
		IMapController osmMapController = osmMap.getController();
		osmMapController.setCenter(mapCenter);
	}

	private void addMiniMapOverlay(Context ctx) {
		final MinimapOverlay miniMapOverlay = new MinimapOverlay(ctx, osmMap.getTileRequestCompleteHandler());
		osmMap.getOverlays().add(miniMapOverlay);
	}

	@SuppressWarnings("unused")
	// TODO: remove?
	private void switchOnlineMapOverlay(String name) {
		Log.d("MAP", "Switching to " + name);

		ITileSource ts = TileSourceFactory.getTileSource(name);
		if (ts == null) {
			Log.d("MAP", "" + name + " + not found..");
			return;
		}

		osmMap.setTileSource(ts);
		Log.d("MAP", "Switched to " + name);
	}

	@SuppressWarnings("unused")
	// TODO: remove?
	private MapEventsReceiver mMapEventReceiver = new MapEventsReceiver() {

		@Override
		public boolean singleTapUpHelper(IGeoPoint p) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean longPressHelper(IGeoPoint p) {
			Location l = new Location("Dummy");
			l.setLatitude(p.getLatitudeE6() / 1000000f);
			l.setLongitude(p.getLongitudeE6() / 1000000f);

			double dist = 1000;
			if (ActivityMain.lastLocation != null) {
				dist = ActivityMain.lastLocation.distanceTo(l);
			}
			SoundFinder.findNode(ActivityMain.act, l.getLatitude(), l.getLongitude(), l.getAltitude(), 25, 15, dist);
			return true;
		}
	};
}
