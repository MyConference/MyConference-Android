package es.ucm.myconference;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WhatsNewFragment extends MyConferenceFragment {

	public WhatsNewFragment(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.whats_new_fragment, container, false);
		return rootView;
	}
	
	

}