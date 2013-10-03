package de.uvwxy.whereami2;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.uvwxy.whereami2.proto.Messages;
import de.uvwxy.whereami2.proto.Messages.Location;

public class ListItemLocationAdapter extends ArrayAdapter<Messages.Location> {
	private LayoutInflater inflater;
	private ArrayList<Messages.Location> locationList;

	public ListItemLocationAdapter(Context context, int textViewResourceId, List<Messages.Location> list) {
		super(context, textViewResourceId);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.locationList = (ArrayList<Location>) list;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rootView = inflater.inflate(R.layout.list_item_location, parent, false);

		TextView tvItemTitle = (TextView) rootView.findViewById(R.id.tvItemTitle);
		TextView tvItemDistanceValue = (TextView) rootView.findViewById(R.id.tvItemDistanceValue);
		
		Messages.Location item = locationList.get(position);
		tvItemTitle.setText("" + item.getName());
		tvItemDistanceValue.setText("" + ActivityMain.loc.getDistanceTo(item));
		
		return rootView;
	}
}
