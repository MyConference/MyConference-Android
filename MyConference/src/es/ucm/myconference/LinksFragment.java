package es.ucm.myconference;

import es.ucm.myconference.util.Constants;
import es.ucm.myconference.util.LinksFragmentAdapter;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class LinksFragment extends MyConferenceFragment{
	
	private ListView docList;
	private Cursor docCursor;
	private TextView docListEmpty;
	public LinksFragment(){}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_links, container, false);
		
		docList = (ListView) rootView.findViewById(R.id.links_list);
		docListEmpty = (TextView) rootView.findViewById(R.id.links_list_empty);
		getQuery();
		if(docCursor.getCount() == 0){ //If cursor is empty show a message
			docList.setVisibility(View.GONE);
		} else {
			docListEmpty.setVisibility(View.GONE);
			LinksFragmentAdapter adapter = new LinksFragmentAdapter(getActivity(), docCursor, 0);
			docList.setAdapter(adapter);
		}
		
		return rootView;
	}
	
	private void getQuery(){
		Uri uri = Uri.parse("content://" + Constants.PROVIDER_NAME + "/documents");
		String[] columns = new String[] {
			Constants._ID,
			Constants.CONF_UUID,
			Constants.DOC_TITLE,
			Constants.DOC_DESCRIPTION,
			Constants.DOC_TYPE,
			Constants.DOC_DATA
		};
		String where = Constants.CONF_UUID + " = ?";
		String[] whereArgs = { getArguments().getString(Constants.CONF_UUID) };
		docCursor = getActivity().getContentResolver().query(uri, columns, where, whereArgs, null);
		//DEBUG
		if(docCursor!=null){
			if(docCursor.moveToFirst()){
				do{
					Log.d("Cursor", "Cursor full");
				}while(docCursor.moveToNext());
			} else {
				Log.d("cursor", "Cursor empty");
			}
		} else {
			Log.d("DEBUG", "Cursor is null. " + docCursor.toString());
		}
	}
	
	

}