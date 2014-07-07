package es.ucm.myconference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.ucm.myconference.util.CommitteeFragmentAdapter;
import es.ucm.myconference.util.Constants;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class CommitteeFragment extends MyConferenceFragment {
	
	private ExpandableListView committeeList;
	private TextView committeeListEmpty;
	private List<String> headersList;
	private HashMap<String, List<String>> childList;
	private Cursor committeeCursor;
	public CommitteeFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_committee, container, false);
		rootView.setBackgroundColor(getResources().getColor(R.color.light_green));
		
		headersList = new ArrayList<String>();
		childList = new HashMap<String, List<String>>();
		
		committeeList = (ExpandableListView) rootView.findViewById(R.id.committee_exp_list);
		committeeListEmpty = (TextView) rootView.findViewById(R.id.committee_list_empty);
		getData();
		if(committeeCursor.getCount() == 0){
			committeeList.setVisibility(View.GONE);
		} else {
			committeeListEmpty.setVisibility(View.GONE);
			CommitteeFragmentAdapter adapter = new CommitteeFragmentAdapter(getActivity(), headersList, childList);
			committeeList.setAdapter(adapter);
		}
		
		return rootView;
	}
	
	private void getData(){
		Uri uri = Uri.parse("content://" + Constants.PROVIDER_NAME + "/committee");
		String[] columns = new String[] {
			Constants._ID,
			Constants.CONF_UUID,
			Constants.COMMITTEE_NAME,
			Constants.COMMITTEE_ORIGIN,
			Constants.COMMITTEE_GROUP
		};
		String where = Constants.CONF_UUID + " = ?";
		String[] whereArgs = { getArguments().getString(Constants.CONF_UUID) };
		committeeCursor = getActivity().getContentResolver().query(uri, columns, where, whereArgs, null);
		
		//Create lists for Adapter from Cursor
		if(committeeCursor.moveToFirst()){
			do {
				String group = committeeCursor.getString(4);
				if(!headersList.contains((String)group)){
					headersList.add(group);
					childList.put(group, new ArrayList<String>());
				}
				String name = committeeCursor.getString(2);
				String origin = committeeCursor.getString(3);
				String row;
				if(origin.isEmpty())
					row = name;
				else
					row = name + " (" + origin + ")";
				List<String> auxiliar = childList.get(group);
				auxiliar.add(row);
				childList.put(group, auxiliar);
				
			} while(committeeCursor.moveToNext());
		}
	}

}
