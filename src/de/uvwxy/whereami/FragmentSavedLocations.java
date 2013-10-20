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
	private TextView tvSavedLocationMaxDistance = null;
	private ListView lvSavedLocations = null;

	private void initGUI(View rootView) {
		tvSavedLocationCount = (TextView) rootView.findViewById(R.id.tvSavedLocationCount);
		tvSavedLocationMaxDistance = (TextView) rootView.findViewById(R.id.tvSavedLocationMaxDistance);
		lvSavedLocations = (ListView) rootView.findViewById(R.id.lvSavedLocations);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_saved_locations, container, false);
		initGUI(rootView);

		ArrayList<Messages.Location> list = new ArrayList<Messages.Location>();
		ActivityMain.data.getAllEntries(list);
		ActivityMain.listAdapter = new ListItemLocationAdapter(getActivity(), list);
		tvSavedLocationCount.setText("" + list.size());
		tvSavedLocationMaxDistance.setText("[wating for fix]");
		lvSavedLocations.setAdapter(ActivityMain.listAdapter);
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
		tvSavedLocationMaxDistance.setText("Dist calc: TODO");
		ActivityMain.listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ActivityMain.bus.unregister(this);
	}
}
