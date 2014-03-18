package es.ucm.myconference;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

public class KeynoteFragment extends MyConferenceFragment {

	private ListView keynoteList;
	private Cursor keynoteCursor;
	
	public KeynoteFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_keynote, container, false);
		rootView.setBackgroundColor(getResources().getColor(R.color.light_green));
		
		keynoteList = (ListView) rootView.findViewById(R.id.keynote_list);
		//No custom adapter needed. Just bindView to change photo resolution
		String[] from = new String[] {"photo", "name", "charge", "origin"};
		int[] to = new int[] {R.id.keynote_photo, R.id.keynote_name, R.id.keynote_charge, R.id.keynote_origin};
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.keynote_list_item,
										keynoteCursor, from, to, 0);
		SimpleCursorAdapter.ViewBinder binder = new SimpleCursorAdapter.ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				// TODO Resize photo
				return false;
			}
		};
		adapter.setViewBinder(binder);
		keynoteList.setAdapter(adapter);
		
		keynoteList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Another activity to show the description
			}
		});
		
		return rootView;
	}
	
}
