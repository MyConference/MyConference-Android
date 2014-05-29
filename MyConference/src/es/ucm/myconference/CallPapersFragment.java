package es.ucm.myconference;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CallPapersFragment extends MyConferenceFragment {

	public CallPapersFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_papers, container, false);
		rootView.setBackgroundColor(getResources().getColor(R.color.light_green));
		
		return rootView;
	}
	
}
