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
import android.widget.TextView;
import android.widget.Toast;
import de.uvwxy.helper.IntentTools;
import de.uvwxy.helper.IntentTools.ReturnStringCallback;
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

		if (s.equals(ctx.getString(R.string.MENU_FAVORITE))) {
			//ActivityMain.data.saveHomeLocation(ctx, locationList.get(position));
			Toast.makeText(ActivityMain.dhis, "TODO: Saved Home Location", Toast.LENGTH_SHORT).show();
		} else if (s.equals(ctx.getString(R.string.MENU_SHARE))) {

			ActionShare.share(ActivityMain.dhis.getParent(), locationList.get(position));

		} else if (s.equals("Delete")) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityMain.dhis);

			alertDialog.setPositiveButton("Delete!", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					ActivityMain.data.deleteEntry(locationList.get(position));
					locationList.remove(position);
					ActivityMain.bus.post(new BusUpdateList());
				}
			});

			alertDialog.setNegativeButton("Cancel", null);
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
								"Show on Map [TODO]", //
								"Audio Navigation [TODO]", //
								"Rename [TODO]", "Delete" }, selected);
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
