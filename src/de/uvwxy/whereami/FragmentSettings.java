package de.uvwxy.whereami;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import de.uvwxy.helper.IntentTools;

public class FragmentSettings extends Fragment {
	private CheckBox cbStartUpdates = null;
	private CheckBox cbStopUpdates = null;
	private CheckBox cbUseGPS = null;
	private CheckBox cbUseNetwork = null;

	private void initGUI(View rootView) {
		cbStartUpdates = (CheckBox) rootView.findViewById(R.id.cbStartUpdates);
		cbStopUpdates = (CheckBox) rootView.findViewById(R.id.cbStopUpdates);
		cbUseGPS = (CheckBox) rootView.findViewById(R.id.cbUseGPS);
		cbUseNetwork = (CheckBox) rootView.findViewById(R.id.cbUseNetwork);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		initGUI(rootView);

		IntentTools.toggleSettings(getActivity(), cbStartUpdates, ActivityMain.SETTINGS,
				ActivityMain.SETTINGS_UPDATES_ON_STARTUP, true);
		IntentTools.toggleSettings(getActivity(), cbStopUpdates, ActivityMain.SETTINGS,
				ActivityMain.SETTINGS_STOP_UPDATES_ONPAUSE, false);
		IntentTools.toggleSettings(getActivity(), cbUseGPS, ActivityMain.SETTINGS, ActivityMain.SETTINGS_USE_GPS, true);
		IntentTools.toggleSettings(getActivity(), cbUseNetwork, ActivityMain.SETTINGS, ActivityMain.SETTINGS_USE_WIFI,
				false);

		return rootView;
	}
}