package es.ucm.myconference.util;

import java.util.Arrays;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foound.widget.AmazingAdapter;

import es.ucm.myconference.R;

public class AmazingSimpleCursorAdapter extends AmazingAdapter {
	
	private final SimpleCursorAdapter simpleCursorAdapter;

    private int sectionNum;
    private Object[] sections;
    private int[] sectionPositions;

    public AmazingSimpleCursorAdapter (
            Context context, int layout, Cursor cursor, String headerField, String[] from, int[] to, int flags)
        {
            simpleCursorAdapter = new SimpleCursorAdapter(context, layout, cursor, from, to, flags);

            // Obtain section headers

            sections = new Object[8];
            sectionPositions = new int[sections.length];
            sectionNum = 0;

            Object section = null;

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                ContentValues values = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cursor, values);
                Object obj = values.get(headerField);

                if ((section == null && obj != null) || (section != null && !section.equals(obj))) {
                    section = obj;

                    if (sectionNum >= sections.length) {
                        sections = Arrays.copyOf(sections, sections.length * 2 + 1);
                        sectionPositions = Arrays.copyOf(sectionPositions, sections.length);
                    }

                    sections[sectionNum] = section;
                    sectionPositions[sectionNum] = cursor.getPosition();

                    sectionNum++;
                }
            }
        }

        @Override
        public int getCount () {
            return simpleCursorAdapter.getCount();
        }

        @Override
        public Object getItem (int position) {
            return simpleCursorAdapter.getItem(position);
        }

        @Override
        public long getItemId (int position) {
            return simpleCursorAdapter.getItemId(position);
        }

        @Override
        protected void onNextPageRequested (int page) {

        }

        @Override
        protected void bindSectionHeader (View view, int position, boolean displaySectionHeader) {
            View header = view.findViewById(R.id.header);
            if (displaySectionHeader) {
                header.setVisibility(View.VISIBLE);
                TextView text = (TextView) header.findViewById(R.id.date);
                text.setText(sections[getSectionForPosition(position)].toString());

            } else {
                header.setVisibility(View.GONE);
            }
        }

        @Override
        public View getAmazingView (int position, View convertView, ViewGroup parent) {
            return simpleCursorAdapter.getView(position, convertView, parent);
        }

        @Override
        public void configurePinnedHeader (View header, int position, int alpha) {
            TextView text = (TextView) header.findViewById(R.id.date);
            text.setText(sections[getSectionForPosition(position)].toString());
        }

        @Override
        public int getPositionForSection (int section) {
            return sectionPositions[section];
        }

        @Override
        public int getSectionForPosition (int position) {
            int sect = Arrays.binarySearch(sectionPositions, 0, sectionNum, position);
            if (sect >= 0) {
                return sect;
            } else {
                return -sect - 2;
            }
        }

        @Override
        public Object[] getSections () {
            return sections;
        }

        public void setViewBinder (ViewBinder viewBinder) {
            simpleCursorAdapter.setViewBinder(viewBinder);
        }
}
