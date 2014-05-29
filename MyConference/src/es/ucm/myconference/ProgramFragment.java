package es.ucm.myconference;

import com.foound.widget.AmazingListView;

import es.ucm.myconference.util.AmazingSimpleCursorAdapter;
import es.ucm.myconference.util.Data;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProgramFragment extends MyConferenceFragment {

	private AmazingListView list;
	
	public ProgramFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_program, container, false);
		rootView.setBackgroundColor(getResources().getColor(R.color.light_green));
		
		list = (AmazingListView) rootView.findViewById(R.id.list);

        String[] cursorFields = new String[] { "start_time", "title", "description" };
        int[] viewFields = new int[] { R.id.start_time, R.id.title, R.id.description };
        AmazingSimpleCursorAdapter adapter =
            new AmazingSimpleCursorAdapter(
                rootView.getContext(), R.layout.program_list_item, Data.getProgramCursor(), "day", cursorFields,
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
	
	
}
