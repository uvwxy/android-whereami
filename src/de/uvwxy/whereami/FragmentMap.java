package de.uvwxy.whereami;

import java.util.ArrayList;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.TilesOverlay;

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
import de.uvwxy.osmdroid.ExtractedOverlay;
import de.uvwxy.soundfinder.SoundFinder;
import de.uvwxy.whereami.proto.Messages;

public class FragmentMap extends Fragment {
	private org.osmdroid.views.MapView osmMap;
	private TilesOverlay baseOverlay;
	private LocationOverlayExtractor locExtractor = new LocationOverlayExtractor();
	private ExtractedOverlay<Messages.Location> locOverlay;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_map, container, false);
		osmMap = (org.osmdroid.views.MapView) rootView.findViewById(R.id.osmMap);
		osmMap.setBuiltInZoomControls(false);
		osmMap.setMultiTouchControls(true);
		osmMap.setUseSafeCanvas(false);
		osmMap.setMaxZoomLevel(18);

		baseOverlay = getOnlineMapOverlay(ActivityMain.act, ActivityMain.mapConfig);
		locOverlay = new ExtractedOverlay<Messages.Location>(locExtractor, getActivity(), osmMap, getActivity());

		osmMap.getOverlays().clear();
		osmMap.getOverlays().add(baseOverlay);
		osmMap.getOverlays().add(locOverlay.getOverlay());
		//		osmMap.getOverlays().add(new )

		ArrayList<Messages.Location> list = new ArrayList<Messages.Location>();
		ActivityMain.data.getAllEntries(list, true, true);
		locOverlay.replaceObjects(getActivity(), list);

		addMiniMapOverlay(getActivity());
		loadPoint();
		osmMap.invalidate();
		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		savePoint();
	}

	public void loadPoint() {
		SharedPreferences prefs = IntentTools.getSettings(getActivity(), ActivityMain.SETTINGS);

		GeoPoint mapCenter = new GeoPoint(prefs.getInt("E6LAT", 0), prefs.getInt("E6LON", 0));
		IMapController osmMapController = osmMap.getController();
		osmMapController.setCenter(mapCenter);
		osmMapController.setZoom(prefs.getInt("ZOOM", 4));
	}

	public void savePoint() {
		IGeoPoint p = osmMap.getMapCenter();
		Editor e = IntentTools.getSettingsEditor(getActivity(), ActivityMain.SETTINGS);
		e.putInt("E6LAT", p.getLatitudeE6());
		e.putInt("E6LON", p.getLongitudeE6());
		e.putInt("ZOOM", osmMap.getZoomLevel());
		IntentTools.saveEditor(e);
	};

	private TilesOverlay getOnlineMapOverlay(Context ctx, String name) {
		MapTileProviderBasic baseProvider = new MapTileProviderBasic(ctx, TileSourceFactory.getTileSource(name));
		TilesOverlay baseOverlay = new TilesOverlay(baseProvider, ctx);
		baseOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
		return baseOverlay;
	}

	public void navigateTo(Location l) {
		GeoPoint mapCenter = new GeoPoint(l.getLatitude() * 1000000, l.getLongitude() * 1000000);
		IMapController osmMapController = osmMap.getController();
		osmMapController.setCenter(mapCenter);
	}

	public void addMiniMapOverlay(Context ctx) {
		final MinimapOverlay miniMapOverlay = new MinimapOverlay(ctx, osmMap.getTileRequestCompleteHandler());
		osmMap.getOverlays().add(miniMapOverlay);
	}

	@SuppressWarnings("unused")
	// TODO: remove?
	private void switchOnlineMapOverlay(Context ctx, String name) {
		Log.i("MAP", "Switching to " + name);
		baseOverlay = getOnlineMapOverlay(ctx, name);

		if (baseOverlay != null) {
			osmMap.getOverlays().add(0, baseOverlay);
			Log.i("MAP", "Switched to " + name);
			ActivityMain.mapConfig = name;
		} else {
			Log.i("MAP", "" + name + " + not found..");

		}

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
