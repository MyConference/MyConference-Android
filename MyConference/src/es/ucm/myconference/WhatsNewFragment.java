package es.ucm.myconference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.ucm.myconference.util.Constants;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.widget.SimpleCursorAdapter;

public class WhatsNewFragment extends MyConferenceFragment {

	private ListView announcementList;
	private Cursor announcementCursor;
	
	public WhatsNewFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_whats_new, container, false);
		rootView.setBackgroundColor(getResources().getColor(R.color.light_green));
		
		announcementList = (ListView) rootView.findViewById(R.id.announcements_list);
		getQuery();
		//Adapter
		String[] from = new String[] {"title","body","date"};
		int[] to = new int[] {R.id.announcement_title, R.id.announcement_body, R.id.announcement_date};
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.whats_new_list_item,
											announcementCursor, from, to, 0);
		
		SimpleCursorAdapter.ViewBinder binder = new SimpleCursorAdapter.ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				TextView announcement_date = (TextView) view.findViewById(R.id.announcement_date);
				if(announcement_date!=null){
					String date = cursor.getString(4);
					String[] noHour = date.split("T");
					SimpleDateFormat df  = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
					Date parsedDate;
					try {
						parsedDate = df.parse(noHour[0]);
						String dateToShow = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(parsedDate);
						Log.d("DATE", dateToShow);
						announcement_date.setText(dateToShow);
					} catch (ParseException e) {
						// If an error occurs, put date as it comes
						return false;
					}
					return true;
				} else {
					Log.d("VIEW", "Is null");
					return false;
				}
			}
		};
		adapter.setViewBinder(binder);
		announcementList.setAdapter(adapter);
		return rootView;
	}
	
	private void getQuery(){
		Uri uri = Uri.parse("content://" + Constants.PROVIDER_NAME + "/announcements");
		String[] columns = new String[] {
			Constants._ID,
			Constants.CONF_UUID,
			Constants.ANNOUNCEMENT_TITLE,
			Constants.ANNOUNCEMENT_BODY,
			Constants.ANNOUNCEMENT_DATE
		};
		String where = Constants.CONF_UUID + " = ?";
		String[] whereArgs = { getArguments().getString(Constants.CONF_UUID) };
		announcementCursor = getActivity().getContentResolver().query(uri, columns, where, whereArgs, 
																		Constants._ID+" DESC");
	}
	
	

}
