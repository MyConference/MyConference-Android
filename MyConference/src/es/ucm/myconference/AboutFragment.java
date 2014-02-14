package es.ucm.myconference;

import es.ucm.myconference.util.Constants;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends MyConferenceFragment {
	
	private TextView aboutTitle;
	private TextView about;
	public AboutFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_about, container, false);

		aboutTitle = (TextView) rootView.findViewById(R.id.about_title);
		about = (TextView) rootView.findViewById(R.id.about);
		
		//Get the conference name and description
		String name = getArguments().getString(Constants.CONF_NAME);
		aboutTitle.setText("About "+name);
		String description = getArguments().getString(Constants.CONF_DESCRP);
		if(description!=null){
			about.setText(description);
		}
		
		return rootView;
	}

}
