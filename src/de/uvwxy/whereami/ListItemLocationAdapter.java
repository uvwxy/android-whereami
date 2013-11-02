package de.uvwxy.whereami;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import de.uvwxy.helper.IntentTools;
import de.uvwxy.helper.IntentTools.ReturnStringCallback;
import de.uvwxy.soundfinder.SoundFinder;
import de.uvwxy.whereami.proto.Messages;
import de.uvwxy.whereami.proto.Messages.Location;

public class ListItemLocationAdapter extends ArrayAdapter<Messages.Location> {
	private LayoutInflater inflater;
	private ArrayList<Messages.Location> locationList;

	private Context ctx;

	public ListItemLocationAdapter(Context context, List<Messages.Location> list) {
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
			ActivityMain.data.toggleFavorite(locationList.get(position));
			ActivityMain.bus.post(new BusUpdateList());

		} else if (s.equals(ctx.getString(R.string.MENU_SHARE))) {
			ActionShare.share(ActivityMain.act, loc);

		} else if (s.equals(ctx.getString(R.string.MENU_SHOW_ON_MAP))) {
			// TODO: implement this!
		} else if (s.equals(ctx.getString(R.string.MENU_AUDIO_NAV))) {
			SoundFinder.findNode(ActivityMain.dhis, loc.getLatitude(), //
					loc.getLongitude(), loc.getAltitude(), 25, 25, //
					Converter.createLoc(loc).distanceTo(ActivityMain.lastLocation));
		} else if (s.equals(ctx.getString(R.string.MENU_RENAME))) {

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityMain.dhis);
			final EditText et = new EditText(ctx);
			et.setText(loc.getName());
			alertDialog.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ActivityMain.data.rename(loc, et.getText().toString());
					ActivityMain.bus.post(new BusUpdateList());
				}
			});
			alertDialog.setNegativeButton(ctx.getString(R.string.MENU_CANCEL), null);
			alertDialog.setMessage("Modify the name below:");
			alertDialog.setView(et);
			alertDialog.setTitle("Rename Location");
			alertDialog.show();

		} else if (s.equals(ctx.getString(R.string.MENU_DELETE))) {

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityMain.dhis);

			alertDialog.setPositiveButton(ctx.getString(R.string.MENU_DELETE), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					ActivityMain.data.deleteEntry(locationList.get(position));
					locationList.remove(position);
					ActivityMain.bus.post(new BusUpdateList());
				}
			});

			alertDialog.setNegativeButton(ctx.getString(R.string.MENU_CANCEL), null);
			alertDialog.setMessage("Do you really want to delete the location \""
					+ locationList.get(position).getName() + "\"?");
			alertDialog.setTitle("Delete");
			alertDialog.show();
		}
	}

	public ArrayList<Messages.Location> getList() {
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
				IntentTools.userSelectString(ActivityMain.dhis, "Select Action:",
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

		Messages.Location item = locationList.get(position);
		tvItemTitle.setText("" + item.getName());
		if (ActivityMain.lastLocation != null) {
			String s = String.format(" %.2f m", LocationManager.getDistanceTo(item, ActivityMain.lastLocation));
			tvItemDistanceValue.setText(s);
		} else {
			tvItemDistanceValue.setText("[no location fix yet]");
		}

		return rootView;
	}
}
