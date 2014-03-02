package es.ucm.myconference;

import es.ucm.myconference.util.Constants;
import es.ucm.myconference.util.VenuesFragmentAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class VenuesFragment extends MyConferenceFragment {

	private Cursor venueCursor;
	private ListView venueList;
	public VenuesFragment(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_venues, container, false);
		rootView.setBackgroundColor(getResources().getColor(R.color.light_green));
		
		venueList = (ListView) rootView.findViewById(R.id.venues_list);
		getQuery();
		VenuesFragmentAdapter adapter = new VenuesFragmentAdapter(getActivity(), venueCursor, 0);
		venueList.setAdapter(adapter);
		
		venueList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				float lat, lng;
				if(venueCursor.moveToPosition(position)){
					lat = venueCursor.getFloat(3);
					lng = venueCursor.getFloat(4);
					Uri geo = Uri.parse("geo:" + lat + "," + lng + "?q=" + lat + "," + lng +
												"(" + venueCursor.getString(2) + ")");
					Intent intent = new Intent(Intent.ACTION_VIEW, geo);
					if (getActivity().getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
			            Toast.makeText(getActivity(), R.string.venue_no_map, Toast.LENGTH_SHORT).show();
			        } else {
			            startActivity(intent);
			        }
				}
			}
		});
		
		return rootView;
	}
	
	private void getQuery(){
		Uri uri = Uri.parse("content://" + Constants.PROVIDER_NAME + "/venues");
		String[] columns = new String[] {
			Constants._ID,
			Constants.CONF_UUID,
			Constants.VENUE_NAME,
			Constants.VENUE_LATITUDE,
			Constants.VENUE_LONGITUDE,
			Constants.VENUE_DETAILS
		};
		String where = Constants.CONF_UUID + " = ?";
		String[] whereArgs = { getArguments().getString(Constants.CONF_UUID) };
		venueCursor = getActivity().getContentResolver().query(uri, columns, where, whereArgs, null);
	}

	
}
