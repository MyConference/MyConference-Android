package es.ucm.myconference;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import es.ucm.myconference.R;
import es.ucm.myconference.util.Constants;
import es.ucm.myconference.util.VenuesFragmentAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ListView;

public class VenuesFragment extends MyConferenceFragment {

	private Cursor venueCursor;
	private ListView venueList;
	private GoogleMap venueMap;
	public VenuesFragment(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_venues, container, false);
		rootView.setBackgroundColor(getResources().getColor(R.color.light_green));
		
		venueList = (ListView) rootView.findViewById(R.id.venues_list);
		getQuery();
		VenuesFragmentAdapter adapter = new VenuesFragmentAdapter(getActivity(), venueCursor, 0);
		venueList.setAdapter(adapter);
		
		//Google Map
		setUpMapIfNeeded();
		
		return rootView;
	}

	@Override
	public void onDestroyView() {
		//Removing map fragment. If don't, when changing fragments and coming back, it crashes.
		Fragment fragment = (getFragmentManager().findFragmentById(R.id.venues_map));
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		ft.remove(fragment);
		ft.commit();
		
		super.onDestroyView();
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

	 private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (venueMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            venueMap = ((SupportMapFragment) getActivity().getSupportFragmentManager()
            									.findFragmentById(R.id.venues_map)).getMap();
            // Check if we were successful in obtaining the map.
            if (venueMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
    	//Adding markers to every venue in the database
    	if(venueCursor!=null){
    		if(venueCursor.moveToFirst()){
    			do{
    				String name = venueCursor.getString(2);
    				Float lat = venueCursor.getFloat(3);
    				Float lng = venueCursor.getFloat(4);
    				venueMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(name));
    				venueCursor.moveToNext();
    			} while(!venueCursor.isAfterLast());
    		}
    	}
    	
    	//TODO Se añadirá el pais en conference/<id>
    	//Move map to city
    	CameraPosition toCity = new CameraPosition.Builder()
		.target(new LatLng(31.225394428, 121.4767527)) //Shangai
		.zoom(10)
		.build();
    	venueMap.animateCamera(CameraUpdateFactory.newCameraPosition(toCity));
    	
    	
    	//When the user click on the info windows, Google Maps is opened
    	venueMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker marker) {
				LatLng latLng = marker.getPosition();
				Uri geo = Uri.parse("geo:" + latLng.latitude + "," + latLng.longitude + 
								"?q=" + latLng.latitude + "," + latLng.longitude + "(" + marker.getTitle()
								+ ")");
				Intent intent = new Intent(Intent.ACTION_VIEW, geo);
				if (getActivity().getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
					Toast.makeText(getActivity(), R.string.venue_no_map, Toast.LENGTH_SHORT).show();
				} else {
					startActivity(intent);
				}
			}
		});
    }
	
}
