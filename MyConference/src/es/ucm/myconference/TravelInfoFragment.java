package es.ucm.myconference;

import es.ucm.myconference.util.Constants;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TravelInfoFragment extends MyConferenceFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = null;
		if(getArguments().getString(Constants.CONF_NAME).equals("PIC 2014")){
			rootView = inflater.inflate(R.layout.fragment_travel_info, container, false);
			rootView.setBackgroundColor(getResources().getColor(R.color.light_green));
		}
		
		return rootView;
	}

}
