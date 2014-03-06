package es.ucm.myconference.util;

import java.util.HashMap;
import java.util.List;

import es.ucm.myconference.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class CommitteeFragmentAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<String> headersList;
	private HashMap<String, List<String>> childList; // header title, child title
	
	public CommitteeFragmentAdapter(Context context, List<String> headersList, 
										HashMap<String, List<String>> childList){
		
		this.context = context;
		this.headersList = headersList;
		this.childList = childList;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childList.get(headersList.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		final String childText = (String) getChild(groupPosition, childPosition);
		 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.committee_list_item, null);
        }
 
        TextView txtListChild = (TextView) convertView.findViewById(R.id.committee_list_child);
 
        txtListChild.setText(childText);
        return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childList.get(headersList.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return headersList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return headersList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.
            								getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.committee_list_group_header, null);
        }
 
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.committee_list_header);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
 
        return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
