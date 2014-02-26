package es.ucm.myconference;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VenuesFragment extends MyConferenceFragment {

	public VenuesFragment(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_venues, container, false);
		
		return rootView;
	}

	
}
