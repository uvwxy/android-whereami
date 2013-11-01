package de.uvwxy.whereami;

import java.util.ArrayList;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import de.uvwxy.whereami.proto.Messages;

public class FragmentSavedLocations extends Fragment {
	private TextView tvSavedLocationCount = null;
	private ListView lvSavedLocations = null;
	private ArrayList<Messages.Location> list = new ArrayList<Messages.Location>();
	private ListItemLocationAdapter listAdapter = null;
	public boolean isFav = false;

	public void setFav(boolean isFav) {
		this.isFav = isFav;
	}

	private void initGUI(View rootView) {
		tvSavedLocationCount = (TextView) rootView.findViewById(R.id.tvSavedLocationCount);
		lvSavedLocations = (ListView) rootView.findViewById(R.id.lvSavedLocations);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_saved_locations, container, false);
		initGUI(rootView);

		ActivityMain.data.getAllEntries(list, !isFav, isFav);
		listAdapter = new ListItemLocationAdapter(getActivity(), list);

		tvSavedLocationCount.setText("" + list.size());
		lvSavedLocations.setAdapter(listAdapter);
		ActivityMain.bus.register(this);
		return rootView;
	}

	@Subscribe
	public void onReceive(Location l) {
		//			if (data.getHomeLocation() == null) {
		//				tvSavedLocationMaxDistance.setText("[Home location not set. Use long press on a location below]");
		//			} else {
		//				String s = String.format(" %.2f m to " + data.getHomeLocation().getName(),
		//						WAILocation.getDistanceTo(data.getHomeLocation(), ActivityMain.lastLocation));
		//				tvSavedLocationMaxDistance.setText(s);
		//			}
		listAdapter.notifyDataSetChanged();
	}

	@Subscribe
	public void onReceive(BusUpdateList u) {
		ActivityMain.data.getAllEntries(list, !isFav, isFav);
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ActivityMain.bus.unregister(this);
	}
}
