package es.ucm.myconference;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TravelInfoFragment extends MyConferenceFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_travel_info, container, false);
		rootView.setBackgroundColor(getResources().getColor(R.color.light_green));
		
		return rootView;
	}

}
