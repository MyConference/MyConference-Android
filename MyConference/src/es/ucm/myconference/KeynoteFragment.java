package es.ucm.myconference;

import com.squareup.picasso.Picasso;

import es.ucm.myconference.util.Constants;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class KeynoteFragment extends MyConferenceFragment {

	private ListView keynoteList;
	private TextView keynoteListEmpty;
	private Cursor keynoteCursor;
	
	public KeynoteFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_keynote, container, false);
		rootView.setBackgroundColor(getResources().getColor(R.color.light_green));
		
		keynoteList = (ListView) rootView.findViewById(R.id.keynote_list);
		keynoteListEmpty = (TextView) rootView.findViewById(R.id.keynote_list_empty);
		getQuery();
		if(keynoteCursor.getCount() == 0){
			keynoteList.setVisibility(View.GONE);
		} else {
			keynoteListEmpty.setVisibility(View.GONE);
			//No custom adapter needed. Just bindView to change photo resolution
			String[] from = new String[] {"picture_url", "name", "charge", "origin"};
			int[] to = new int[] {R.id.keynote_photo, R.id.keynote_name, R.id.keynote_charge, R.id.keynote_origin};
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.keynote_list_item,
											keynoteCursor, from, to, 0);
			SimpleCursorAdapter.ViewBinder binder = new SimpleCursorAdapter.ViewBinder() {
				
				@Override
				public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
					ImageView photo = (ImageView) view.findViewById(R.id.keynote_photo);
					if(getArguments().getString(Constants.CONF_NAME)!= null){
						if(photo!=null){
							if(cursor.getString(2).startsWith("H")){
								photo.setImageResource(R.drawable.h_fujita);
							} else if(cursor.getString(2).startsWith(" D")){
								photo.setImageResource(R.drawable.d_roth);
							} else if(cursor.getString(2).startsWith("V")){
								photo.setImageResource(R.drawable.vlopez);
							} else {
								photo.setImageResource(R.drawable.gzhou);
							}
							return true;
						} else {
							return false;
						}
					} else {
						String uri = cursor.getString(6);
						if(photo!=null){
							Picasso.with(getActivity()).load(uri).into(photo);
							return true;
						} else {
							return false;
						}
					}
				}
			};
			adapter.setViewBinder(binder);
			keynoteList.setAdapter(adapter);
			
			keynoteList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if(keynoteCursor.moveToPosition(position)){
						String descrp = keynoteCursor.getString(5);
						String uri = keynoteCursor.getString(6);
						Intent i = new Intent(getActivity(), KeynoteDescriptionActivity.class);
						i.putExtra(Constants.KEYNOTES_DESCRIPTION, descrp);
						i.putExtra("position", position);
						i.putExtra(Constants.KEYNOTES_PHOTO, uri);
						i.putExtra(Constants.CONF_NAME, getArguments().getString(Constants.CONF_NAME));
						startActivity(i);
					}
				}
				
			});
		}
		
		return rootView;
	}
	
	private void getQuery(){
		Uri uri = Uri.parse("content://" + Constants.PROVIDER_NAME + "/keynotes");
		String[] columns = new String[] {
			Constants._ID,
			Constants.CONF_UUID,
			Constants.KEYNOTES_NAME,
			Constants.KEYNOTES_CHARGE,
			Constants.KEYNOTES_ORIGIN,
			Constants.KEYNOTES_DESCRIPTION,
			Constants.KEYNOTES_PHOTO,
			Constants.KEYNOTE_LINKS
		};
		String where = Constants.CONF_UUID + " = ?";
		String[] whereArgs = { getArguments().getString(Constants.CONF_UUID) };
		keynoteCursor = getActivity().getContentResolver().query(uri, columns, where, whereArgs, null);
	}
}
