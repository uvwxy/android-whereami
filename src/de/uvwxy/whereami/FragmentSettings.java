package de.uvwxy.whereami;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import de.uvwxy.helper.IntentTools;

public class FragmentSettings extends Fragment {
	private CheckBox cbStartUpdates = null;
	private CheckBox cbStopUpdates = null;
	private TextView textView1 = null;
	private CheckBox cbUseGPS = null;
	private CheckBox cbUseNetwork = null;
	private TextView textView2 = null;
	private RadioGroup rbgLengths = null;
	private RadioButton rbMetres = null;
	private RadioButton rbKilometres = null;
	private RadioButton rbFoot = null;
	private RadioButton rbMiles = null;
	private RadioGroup rbgVelocities = null;
	private RadioButton rbMetresPerSecond = null;
	private RadioButton rbKMH = null;
	private RadioButton rbMPH = null;
	private RadioGroup rbgAngles = null;
	private RadioButton rbAngleDegrees = null;
	private RadioButton rbAngleDegreesMinutesSeconds = null;

	private void initGUI(View r) {
		cbStartUpdates = (CheckBox) r.findViewById(R.id.cbStartUpdates);
		cbStopUpdates = (CheckBox) r.findViewById(R.id.cbStopUpdates);
		textView1 = (TextView) r.findViewById(R.id.textView1);
		cbUseGPS = (CheckBox) r.findViewById(R.id.cbUseGPS);
		cbUseNetwork = (CheckBox) r.findViewById(R.id.cbUseNetwork);
		textView2 = (TextView) r.findViewById(R.id.textView2);
		rbgLengths = (RadioGroup) r.findViewById(R.id.rbgLengths);
		rbMetres = (RadioButton) r.findViewById(R.id.rbMetres);
		rbKilometres = (RadioButton) r.findViewById(R.id.rbKilometres);
		rbFoot = (RadioButton) r.findViewById(R.id.rbFoot);
		rbMiles = (RadioButton) r.findViewById(R.id.rbMiles);
		rbgVelocities = (RadioGroup) r.findViewById(R.id.rbgVelocities);
		rbMetresPerSecond = (RadioButton) r.findViewById(R.id.rbMetresPerSecond);
		rbKMH = (RadioButton) r.findViewById(R.id.rbKMH);
		rbMPH = (RadioButton) r.findViewById(R.id.rbMPH);
		rbgAngles = (RadioGroup) r.findViewById(R.id.rbgAngles);
		rbAngleDegrees = (RadioButton) r.findViewById(R.id.rbAngleDegrees);
		rbAngleDegreesMinutesSeconds = (RadioButton) r.findViewById(R.id.rbAngleDegreesMinutesSeconds);
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
		IntentTools.switchSettings(getActivity(), rbMetres, ActivityMain.SETTINGS, ActivityMain.SETTINGS_LENGHT_TYPE,
				ActivityMain.SETTINGS_METRES);
		IntentTools.switchSettings(getActivity(), rbKilometres, ActivityMain.SETTINGS,
				ActivityMain.SETTINGS_LENGHT_TYPE, ActivityMain.SETTINGS_KILOMETRES);
		IntentTools.switchSettings(getActivity(), rbFoot, ActivityMain.SETTINGS, ActivityMain.SETTINGS_LENGHT_TYPE,
				ActivityMain.SETTINGS_FOOT);
		IntentTools.switchSettings(getActivity(), rbMiles, ActivityMain.SETTINGS, ActivityMain.SETTINGS_LENGHT_TYPE,
				ActivityMain.SETTINGS_MILES);

		IntentTools.switchSettings(getActivity(), rbMetresPerSecond, ActivityMain.SETTINGS,
				ActivityMain.SETTINGS_VELOCTIY_TYPE, ActivityMain.SETTINGS_METRES_PER_SECOND);
		IntentTools.switchSettings(getActivity(), rbKMH, ActivityMain.SETTINGS, ActivityMain.SETTINGS_VELOCTIY_TYPE,
				ActivityMain.SETTINGS_KMH);
		IntentTools.switchSettings(getActivity(), rbMPH, ActivityMain.SETTINGS, ActivityMain.SETTINGS_VELOCTIY_TYPE,
				ActivityMain.SETTINGS_MPH);

		IntentTools.switchSettings(getActivity(), rbAngleDegrees, ActivityMain.SETTINGS,
				ActivityMain.SETTINGS_ANGLE_TYPE, ActivityMain.SETTINGS_ANGLE_DEGREES);
		IntentTools.switchSettings(getActivity(), rbAngleDegreesMinutesSeconds, ActivityMain.SETTINGS,
				ActivityMain.SETTINGS_ANGLE_TYPE, ActivityMain.SETTINGS_ANGLE_MINUTES_SECONDS);

		return rootView;
	}
}