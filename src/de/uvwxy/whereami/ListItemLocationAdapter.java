package de.uvwxy.whereami;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import de.uvwxy.helper.IntentTools;
import de.uvwxy.helper.IntentTools.ReturnStringCallback;
import de.uvwxy.soundfinder.SoundFinder;
import de.uvwxy.units.Unit;
import de.uvwxy.whereami.db_location.Location;
import de.uvwxy.whereami.fragments.FragmentOverlaySupportMap;

public class ListItemLocationAdapter extends ArrayAdapter<Location> {
	private LayoutInflater inflater;
	private ArrayList<Location> locationList;

	private Context ctx;

	public ListItemLocationAdapter(Context context, List<Location> list) {
		super(context, R.layout.list_item_location, list);
		this.ctx = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.locationList = (ArrayList<Location>) list;
	}

	private void handleMenuItem(final int position, String s) {
		if (s == null) {
			return;
		}

		final Location loc = locationList.get(position);

		if (loc == null) {
			return;
		}

		if (s.equals(ctx.getString(R.string.MENU_FAVORITE))) {
			ActivityMain.mData.toggleFavorite(locationList.get(position));
			//TODO: ActivityMain.bus.post(new BusUpdateList());

		} else if (s.equals(ctx.getString(R.string.MENU_SHARE))) {
			ActionShare.share(ActivityMain.act, loc);

		} else if (s.equals(ctx.getString(R.string.MENU_SHOW_ON_MAP))) {
			ActivityMain.dhis.moveToTab(3);
			backgroundLocationReload(loc);
		} else if (s.equals(ctx.getString(R.string.MENU_AUDIO_NAV))) {
			android.location.Location l = ActivityMain.mLastLocation;
			if (l != null) {
				SoundFinder.findNode(ActivityMain.dhis, loc.getLatitude(), //
						loc.getLongitude(), loc.getAltitude(), 25, 25, //
						Converter.createLoc(loc).distanceTo(l));
			} else {
				Toast.makeText(ctx, R.string.no_location_yet, Toast.LENGTH_SHORT).show();
			}
		} else if (s.equals(ctx.getString(R.string.MENU_RENAME))) {

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityMain.dhis);
			final EditText et = new EditText(ctx);
			et.setText(loc.getName());
			alertDialog.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ActivityMain.mData.rename(loc, et.getText().toString());
					// TODO: ActivityMain.bus.post(new BusUpdateList());
				}
			});
			alertDialog.setNegativeButton(ctx.getString(R.string.MENU_CANCEL), null);
			alertDialog.setMessage(R.string.modify_the_name_below_);
			alertDialog.setView(et);
			alertDialog.setTitle(R.string.rename_location);
			alertDialog.show();

		} else if (s.equals(ctx.getString(R.string.MENU_DELETE))) {

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityMain.dhis);

			alertDialog.setPositiveButton(ctx.getString(R.string.MENU_DELETE), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					ActivityMain.mData.deleteEntry(locationList.get(position));
					locationList.remove(position);
					// TODO: ActivityMain.bus.post(new BusUpdateList());
				}
			});

			alertDialog.setNegativeButton(ctx.getString(R.string.MENU_CANCEL), null);
			alertDialog.setMessage(String.format(
					getContext().getString(R.string.do_you_really_want_to_delete_the_location_s_),
					locationList.get(position).getName()));
			alertDialog.setTitle("Delete");
			alertDialog.show();
		}
	}

	public ArrayList<Location> getList() {
		return locationList;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rootView = inflater.inflate(R.layout.list_item_location, parent, false);

		rootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ReturnStringCallback selected = new ReturnStringCallback() {

					@Override
					public void result(String s) {
						handleMenuItem(position, s);
					}

				};
				IntentTools.userSelectString(ActivityMain.dhis, getContext().getString(R.string.select_action_),
						new String[] { ctx.getString(R.string.MENU_FAVORITE), //
								ctx.getString(R.string.MENU_SHARE), //
								ctx.getString(R.string.MENU_RENAME), //
								ctx.getString(R.string.MENU_DELETE), //
								ctx.getString(R.string.MENU_SHOW_ON_MAP), //
								ctx.getString(R.string.MENU_AUDIO_NAV), //
								ctx.getString(R.string.MENU_CANCEL) }, selected);
			}
		});

		TextView tvItemTitle = (TextView) rootView.findViewById(R.id.tvItemTitle);
		TextView tvItemDistanceValue = (TextView) rootView.findViewById(R.id.tvItemDistanceValue);

		Location item = locationList.get(position);
		tvItemTitle.setText("" + item.getName());
		if (ActivityMain.mLastLocation != null) {
			Unit u = Unit.METRE.setValue(LocationManager.getDistanceTo(item, ActivityMain.mLastLocation));
			u = u.to(ActivityMain.mUnitL);
			String s = String.format("%s", u);
			tvItemDistanceValue.setText(s);
		} else {
			tvItemDistanceValue.setText(R.string._no_location_fix_yet_);
		}

		return rootView;
	}

	private void backgroundLocationReload(final Location loc) {
		Handler h = new Handler(ctx.getMainLooper());

		h.post(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (ActivityMain.fMap != null && ActivityMain.fMap instanceof FragmentOverlaySupportMap) {
					FragmentOverlaySupportMap m = (FragmentOverlaySupportMap) ActivityMain.fMap;
					if (m.mMapView != null) {

						m.mMapView.moveCamera(CameraUpdateFactory.newLatLngZoom(
								new LatLng(loc.getLatitude(), loc.getLongitude()), 10f));
					}
				}
			}
		});

	}
}
