package es.ucm.myconference.util;

import es.ucm.myconference.R;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class VenuesFragmentAdapter extends CursorAdapter {

	public VenuesFragmentAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if(cursor!=null){
			ImageView icon = (ImageView) view.findViewById(R.id.venue_icon);
			icon.setImageResource(R.drawable.ic_menu_venue);
			
			TextView title = (TextView) view.findViewById(R.id.venue_name);
			title.setText(cursor.getString(2));
			
			TextView descr = (TextView) view.findViewById(R.id.venue_description);
			descr.setText(cursor.getString(5));
		}

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// When the view will be created for first time,
        // we need to tell the adapters how each item will look
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View newView = inflater.inflate(R.layout.venues_list_item, null);
		return newView;
	}

}
