package ru.etalon5.draftsman;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

public class DrawSettingsDialogFragment extends DialogFragment {
	
	private DialogCommunicator mCommunicator;
	private CheckBox mCheckBoxSnapGrid;
	private CheckBox mCheckBoxSnapObjects;
	private RadioButton mRadioButtonWhiteBackground;
	private RadioButton mRadioButtonBlackBackground;
	
	
	public DrawSettingsDialogFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
    public void onAttach(Activity activity) {

		super.onAttach(activity);

		if (activity instanceof DialogCommunicator) {
			mCommunicator = (DialogCommunicator) getActivity();
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet DrawSettingsDialogFragment.communicator");
		}
    }
	
	@SuppressLint("InflateParams") 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle("Настройки");
		View v = inflater.inflate(R.layout.fragment_draft_settings, null);
		
		mCheckBoxSnapGrid = (CheckBox) v.findViewById(R.id.checkBoxBingGrid);
		mCheckBoxSnapObjects = (CheckBox) v.findViewById(R.id.checkBoxBindObjects);
		mRadioButtonWhiteBackground = (RadioButton) v.findViewById(R.id.radioButtonWhiteBackground);
		mRadioButtonBlackBackground = (RadioButton) v.findViewById(R.id.radioButtonBlackBackground);
		loadSettings();
		
		Button btnOk = (Button) v.findViewById(R.id.buttonDraftSettingsOk);
		btnOk.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				dismiss();
				saveSettings();
				mCommunicator.dialogFinish();				
			}
		});		
		
		return v;
	}
	
	private void loadSettings() {
		SharedPreferences reader = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mCheckBoxSnapGrid.setChecked(reader.getBoolean("draftIsSnapGrid", true));
		mCheckBoxSnapObjects.setChecked(reader.getBoolean("draftIsSnapObjects", true));
		if (reader.getBoolean("draftIsWhiteBackground", true))
			mRadioButtonWhiteBackground.setChecked(true);
		else mRadioButtonBlackBackground.setChecked(true);
		
	}
	
	private void saveSettings() {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
    	editor.putBoolean("draftIsSnapGrid", mCheckBoxSnapGrid.isChecked());
    	editor.putBoolean("draftIsSnapObjects", mCheckBoxSnapObjects.isChecked());
    	editor.putBoolean("draftIsWhiteBackground", mRadioButtonWhiteBackground.isChecked());
    	editor.commit();  
	}
	
    public interface DialogCommunicator {
        public void dialogFinish();
    }
}
