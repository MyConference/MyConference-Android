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

public class LinksFragmentAdapter extends CursorAdapter {

	public LinksFragmentAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	

	@Override
	public void bindView(View view, Context context, Cursor c) {
		// here we are setting our data
        // that means, take the data from the cursor and put it in views
		if(c!=null){
			ImageView icon = (ImageView) view.findViewById(R.id.link_icon);
			if(c.getString(4).equals("pdf")){
				icon.setImageResource(R.drawable.ic_pdf);
			}else {
				icon.setImageResource(R.drawable.ic_document);
			}
			
			TextView title = (TextView) view.findViewById(R.id.link_name);
			title.setText(c.getString(2));
			
			TextView description = (TextView) view.findViewById(R.id.link_description);
			description.setText(c.getString(3));
		}
	}



	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		// When the view will be created for first time,
        // we need to tell the adapters how each item will look
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View newView = inflater.inflate(R.layout.links_list_item, null);
		return newView;
	}

}
