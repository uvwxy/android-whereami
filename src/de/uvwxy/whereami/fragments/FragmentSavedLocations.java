package de.uvwxy.whereami.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import de.uvwxy.whereami.ActivityMain;
import de.uvwxy.whereami.ListItemLocationAdapter;
import de.uvwxy.whereami.R;
import de.uvwxy.whereami.db_location.Location;

public class FragmentSavedLocations extends Fragment {
	private TextView tvSavedLocationCount = null;
	private ListView lvSavedLocations = null;
	private ArrayList<Location> list = new ArrayList<Location>();
	private ListItemLocationAdapter listAdapter = null;
	public boolean isFav = false;

	private ActivityMain dhis;
	
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
		dhis = ActivityMain.dhis;
		
		dhis.mData.getAllEntries(list, !isFav, isFav);
		listAdapter = new ListItemLocationAdapter(getActivity(), list);

		tvSavedLocationCount.setText("" + list.size());
		lvSavedLocations.setAdapter(listAdapter);
		return rootView;
	}

	public void updateList() {
		dhis.mData.getAllEntries(list, !isFav, isFav);
		tvSavedLocationCount.setText("" + list.size());
		listAdapter.notifyDataSetChanged();
	}
	
	public void refreshList() {
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
