package de.uvwxy.whereami;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.otto.Subscribe;

import de.uvwxy.helper.IntentTools;
import de.uvwxy.helper.IntentTools.ReturnStringCallback;
import de.uvwxy.sensors.location.LocationReader;

public class FragmentCurrentLocation extends Fragment {
	private ScrollView scrollView1 = null;
	private TextView tvLat = null;
	private TextView tvLon = null;
	private TextView tvAlt = null;
	private TextView tvBearing = null;
	private TextView tvSpeed = null;
	private TextView tvAcc = null;
	private TextView tvProvider = null;
	public static ToggleButton swUpdates = null;
	private Button btnSave = null;
	private Button btnSend = null;

	private void initGUI(View rootView) {
		scrollView1 = (ScrollView) rootView.findViewById(R.id.scrollView1);
		tvLat = (TextView) rootView.findViewById(R.id.tvLat);
		tvLon = (TextView) rootView.findViewById(R.id.tvLon);
		tvAlt = (TextView) rootView.findViewById(R.id.tvAlt);
		tvBearing = (TextView) rootView.findViewById(R.id.tvBearing);
		tvSpeed = (TextView) rootView.findViewById(R.id.tvSpeed);
		tvAcc = (TextView) rootView.findViewById(R.id.tvAcc);
		tvProvider = (TextView) rootView.findViewById(R.id.tvProvider);
		swUpdates = (ToggleButton) rootView.findViewById(R.id.swUpdates);
		btnSave = (Button) rootView.findViewById(R.id.btnSave);
		btnSend = (Button) rootView.findViewById(R.id.btnSend);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_current_location, container, false);

		initGUI(rootView);

		swUpdates.setChecked(ActivityMain.locationUpdatesEnabled);
		initClicks();

		onReceive(ActivityMain.lastLocation != null ? ActivityMain.lastLocation : new Location("[no fix]"));
		ActivityMain.bus.register(this);

		return rootView;
	}

	private void initClicks() {
		swUpdates.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ActivityMain.locationUpdatesEnabled = isChecked;
				if (ActivityMain.locationUpdatesEnabled) {
					ActivityMain.loc.getReader().startReading();
				} else {
					ActivityMain.loc.getReader().stopReading();
				}
			}
		});

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Location loc = ActivityMain.lastLocation;
				if (loc == null) {
					Toast.makeText(getActivity(), "No location yet", Toast.LENGTH_SHORT).show();
					return;
				}

				// set time to when it was saved, not when it was received.
				// this way a single location can be added multiple times.
				// nobody wants this, but it can happen, entering into db will fail
				// hinders testing somehow
				loc.setTime(System.currentTimeMillis());

				final EditText etName = new EditText(getActivity());

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

				alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String msg = null;
						long ret = ActivityMain.data.addEntry(Converter.createLoc(etName.getText().toString(), loc));

						if (ret == -1) {
							msg = "Failed to add entry to db";
						} else {
							msg = "Added entry to db";
							// TODO: listupdates!
							ActivityMain.bus.post(new BusUpdateList());
							//ActivityMain.data.getAllEntries(ActivityMain.listAdapter.getList(), );
							//ActivityMain.listAdapter.notifyDataSetChanged();
						}
						Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
					}
				});
				alertDialog.setView(etName);
				alertDialog.setNegativeButton("Cancel", null);
				alertDialog.setMessage("Please provide a name for this location:");
				alertDialog.setTitle("Save location");
				alertDialog.show();
			}
		});

		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Location loc = ActivityMain.lastLocation;
				if (loc == null) {
					Toast.makeText(getActivity(), "No location yet", Toast.LENGTH_SHORT).show();
					return;
				}

				ReturnStringCallback selected = new ReturnStringCallback() {

					@Override
					public void result(String s) {
						int type = IntentTools.TYPE_GMAPS;
						if ("Google Maps".equals(s)) {
							type = IntentTools.TYPE_GMAPS;
						} else if ("OpenStreetMap".equals(s)) {
							type = IntentTools.TYPE_OSM;
						}

						IntentTools.shareLocation(getActivity(), loc, type, "Shared Location", "Lat ", "Lon ", "Alt ",
								"Bearing ", "Accuracy ", "Speed ", "Length of shared message ",
								"Select application to share via:");

					}
				};
				IntentTools.userSelectString(getActivity(), "Select a provider", new String[] { "Google Maps",
						"OpenStreetMap" }, selected);
			}
		});
	}

	@Subscribe
	public void onReceive(Location l) {
		tvLat.setText("" + l.getLatitude() + " °");
		tvLon.setText("" + l.getLongitude() + " °");
		tvAlt.setText("" + l.getAltitude() + " m");
		tvSpeed.setText("" + l.getSpeed() + " m/s");
		tvProvider.setText(l.getProvider());
		tvBearing.setText("" + l.getBearing() + " °");
		tvAcc.setText("" + l.getAccuracy() + " m");
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ActivityMain.bus.unregister(this);
	}
}
