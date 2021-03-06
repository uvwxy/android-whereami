package de.uvwxy.whereami.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import de.uvwxy.helper.IntentTools;
import de.uvwxy.units.Unit;
import de.uvwxy.whereami.ActionShare;
import de.uvwxy.whereami.ActivityMain;
import de.uvwxy.whereami.Converter;
import de.uvwxy.whereami.R;

@SuppressWarnings("unused")
public class FragmentCurrentLocation extends Fragment {
	private ScrollView scrollView1 = null;
	private TextView tvLat = null;
	private TextView tvLon = null;
	private TextView tvAlt = null;
	private TextView tvBearing = null;
	private TextView tvSpeed = null;
	private TextView tvAcc = null;
	private TextView tvProvider = null;
	public ToggleButton swUpdates = null;
	private Button btnSave = null;
	private Button btnSend = null;
	private Button btnShow = null;
	
	private ActivityMain dhis;

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
		btnShow = (Button) rootView.findViewById(R.id.btnShow);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_current_location, container, false);
		dhis = ActivityMain.dhis;
		
		initGUI(rootView);

		swUpdates.setChecked(ActivityMain.mLocationUpdatesEnabled);
		initClicks();

		updateLocation(dhis.mLastLocation != null ? dhis.mLastLocation : new Location(getActivity()
				.getString(R.string._no_fix_)));

		return rootView;
	}

	private void initClicks() {
		swUpdates.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ActivityMain.mLocationUpdatesEnabled = isChecked;
				if (ActivityMain.mLocationUpdatesEnabled) {
					ActivityMain.mLoc.getReader().startReading();
				} else {
					ActivityMain.mLoc.getReader().stopReading();
				}
			}
		});

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Location loc = dhis.mLastLocation;
				if (loc == null) {
					Toast.makeText(getActivity(), R.string.no_location_yet, Toast.LENGTH_SHORT).show();
					return;
				}

				// set time to when it was saved, not when it was received.
				// this way a single location can be added multiple times.
				// nobody wants this, but it can happen, entering into db will fail
				// hinders testing somehow
				loc.setTime(System.currentTimeMillis());

				final EditText etName = new EditText(getActivity());

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

				alertDialog.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String msg = null;
						long ret = dhis.mData.addEntry(Converter.createLoc(etName.getText().toString(), loc));

						if (ret == -1) {
							msg = getActivity().getString(R.string.failed_to_add_entry_to_db);
						} else {
							msg = getActivity().getString(R.string.added_entry_to_db);
							dhis.updateLists();
						}
						Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
					}
				});
				alertDialog.setView(etName);
				alertDialog.setNegativeButton(R.string.cancel, null);
				alertDialog.setMessage(R.string.please_provide_a_name_for_this_location_);
				alertDialog.setTitle(R.string.save_location);
				alertDialog.show();
			}
		});

		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Location loc = dhis.mLastLocation;
				if (loc == null) {
					Toast.makeText(getActivity(), R.string.no_location_yet, Toast.LENGTH_SHORT).show();
					return;
				}
				Log.d("WAI", dhis.mLastLocation.toString());

				ActionShare.share(getActivity(), loc);
			}
		});
		
		btnShow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				final Location loc = dhis.mLastLocation;
				if (loc != null){
					IntentTools.showLocation(getActivity(), loc.getLatitude(), loc.getLongitude());
				}
			}
		});
	}

	public void updateLocation(Location l) {

		tvLat.setText(Unit.from(Unit.DEGREES).setPrecision(6).setValue(l.getLatitude()).to(ActivityMain.mUnitA).toString());
		tvLon.setText(Unit.from(Unit.DEGREES).setPrecision(6).setValue(l.getLongitude()).to(ActivityMain.mUnitA).toString());
		tvBearing.setText(Unit.from(Unit.DEGREES).setPrecision(1).setValue(l.getBearing()).toString());

		tvAlt.setText(Unit.from(Unit.METRE).setValue(l.getAltitude()).to(ActivityMain.mUnitL).toString());
		tvAcc.setText(Unit.from(Unit.METRE).setValue(l.getAccuracy()).to(ActivityMain.mUnitL).toString());

		tvSpeed.setText(Unit.from(Unit.METRES_PER_SECOND).setValue(l.getSpeed()).to(ActivityMain.mUnitV).toString());

		tvProvider.setText(l.getProvider());
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

}
