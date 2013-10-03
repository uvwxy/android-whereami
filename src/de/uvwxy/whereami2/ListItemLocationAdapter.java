package de.uvwxy.whereami2;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import de.uvwxy.helper.IntentTools;
import de.uvwxy.helper.IntentTools.ReturnStringCallback;
import de.uvwxy.whereami2.proto.Messages;
import de.uvwxy.whereami2.proto.Messages.Location;

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

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rootView = inflater.inflate(R.layout.list_item_location, parent, false);

		rootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ReturnStringCallback selected = new ReturnStringCallback() {

					@Override
					public void result(String s) {
						if (s.equals("Set as home location")) {
							ActivityMain.data.saveHomeLocation(ctx, locationList.get(position));
							Toast.makeText(ActivityMain.dhis, "Saved Home Location", Toast.LENGTH_SHORT).show();
						}
					}
				};
				IntentTools.userSelectString(ActivityMain.dhis, "Select Action:", new String[] { "Set as home location", "Send [TODO]", "Show on Map [TODO]",
						"Audio Navigation [TODO]", "Delete [TODO]" }, selected);
			}
		});

		TextView tvItemTitle = (TextView) rootView.findViewById(R.id.tvItemTitle);
		TextView tvItemDistanceValue = (TextView) rootView.findViewById(R.id.tvItemDistanceValue);

		Messages.Location item = locationList.get(position);
		tvItemTitle.setText("" + item.getName());
		if (ActivityMain.lastLocation != null) {
			String s = String.format(" %.2f m", WAILocation.getDistanceTo(item, ActivityMain.lastLocation));
			tvItemDistanceValue.setText(s);
		} else {
			tvItemDistanceValue.setText("[no location fix yet]");
		}

		return rootView;
	}
}
