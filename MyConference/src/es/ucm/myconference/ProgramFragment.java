package es.ucm.myconference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.foound.widget.AmazingListView;

import es.ucm.myconference.util.AmazingSimpleCursorAdapter;
import es.ucm.myconference.util.Constants;
import es.ucm.myconference.util.Data;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProgramFragment extends MyConferenceFragment {

	private AmazingListView list;
	private Cursor programCursor;
	private static long index = 0;
	
	public ProgramFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_program, container, false);
		rootView.setBackgroundColor(getResources().getColor(R.color.light_green));
		
		list = (AmazingListView) rootView.findViewById(R.id.list);
		getQuery();
		Cursor cursor;
		if(getArguments().getString(Constants.CONF_NAME)!= null){
			cursor = Data.getProgramCursor();
		} else {
			cursor = parseDate();
		}
        String[] cursorFields = new String[] { "start_time", "title", "description" };
        int[] viewFields = new int[] { R.id.start_time, R.id.title, R.id.description };
        AmazingSimpleCursorAdapter adapter =
            new AmazingSimpleCursorAdapter(
                rootView.getContext(), R.layout.program_list_item, cursor, "day", cursorFields,
                viewFields, 0);
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            @Override
            public boolean setViewValue (View view, Cursor cursor, int column) {
                if (cursor.getString(column) == null) {
                    view.setVisibility(View.GONE);
                    return true;
                }
                view.setVisibility(View.VISIBLE);
                return false;
            }
        });
        
        list.setPinnedHeaderView(getLayoutInflater(savedInstanceState).inflate(R.layout.program_header, list, false));
        list.setAdapter(adapter);
		
		
		return rootView;
	}
	
	private void getQuery(){
		Uri uri = Uri.parse("content://" + Constants.PROVIDER_NAME + "/agenda");
		String[] columns = new String[] {
			Constants._ID,
			Constants.CONF_UUID,
			Constants.AGENDA_TITLE,
			Constants.AGENDA_DESCRIPTION,
			Constants.AGENDA_DATE
		};
		String where = Constants.CONF_UUID + " = ?";
		String[] whereArgs = { getArguments().getString(Constants.CONF_UUID) };
		programCursor = getActivity().getContentResolver().query(uri, columns, where, whereArgs, 
																		Constants._ID+" DESC");
	}
	
	private Cursor parseDate(){
		MatrixCursor c = new MatrixCursor(new String[] {"_id", "title", "description", "day", "start_time"});
		if(programCursor!=null){
			if(programCursor.moveToFirst()){
				do {
					String date = programCursor.getString(4);
					String[] noHour = date.split("T");
					String[] hour = noHour[1].split(":");
					String hourToShow = hour[0] +":"+ hour[1];
					SimpleDateFormat df  = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
					Date parsedDate;
					String dateToShow;
					try {
						parsedDate = df.parse(noHour[0]);
						dateToShow = new SimpleDateFormat("MMM, dd", Locale.ENGLISH).format(parsedDate);
						Log.d("DATE", dateToShow);
						Log.d("HOUR", hourToShow);
					} catch (ParseException e) {
						// If an error occurs, put date as it comes
						dateToShow = noHour[0];
					}
					c.addRow(new Object[] {++index, programCursor.getString(2), programCursor.getString(3),
											dateToShow, hourToShow});
					
					programCursor.moveToNext();
				} while(!programCursor.isAfterLast());
			}
		}
		return c;
	}
}
