package es.ucm.myconference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.ucm.myconference.util.CommitteeFragmentAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class CommitteeFragment extends MyConferenceFragment {
	
	private ExpandableListView committeeList;
	private List<String> headersList;
	private HashMap<String, List<String>> childList;
	public CommitteeFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_committee, container, false);
		rootView.setBackgroundColor(getResources().getColor(R.color.light_green));
		
		committeeList = (ExpandableListView) rootView.findViewById(R.id.committee_exp_list);
		getData();
		CommitteeFragmentAdapter adapter = new CommitteeFragmentAdapter(getActivity(), headersList, childList);
		committeeList.setAdapter(adapter);
		
		return rootView;
	}
	
	private void getData(){
		//TODO From database
		headersList = new ArrayList<String>();
		childList = new HashMap<String, List<String>>();
		
		headersList.add("General Conference Chairs");
		headersList.add("Organization Chairs");
		headersList.add("Program Committee Chairs");
		headersList.add("Program Committee Members");
		
		List<String> general = new ArrayList<String>();
		general.add("Kang Zhang (USA)");
		general.add("Mengqi Zhou (China)");
		general.add("Yinglin Wang (China)");
		
		List<String> organization = new ArrayList<String>();
		organization.add("Yinglin Wang (China)");
		organization.add("Jian Cao (China)");
		
		List<String> programChairs = new ArrayList<String>();
		programChairs.add("Xuelong Li (China)");
		programChairs.add("Jie Lu (Australia)");
		programChairs.add("Hongming Cai (China)");
		programChairs.add("Yuan Luo (China)");
		
		List<String> programMem = new ArrayList<String>();
		programMem.add("Aarne Ranta (Sweden)");
		programMem.add("Alfredo	Cuzzocrea (Italy)");
		programMem.add("Alina Campan (US)");
		programMem.add("Amin Chaabane (Canada)");
		programMem.add("André Clouâtre (Canada)");
		programMem.add("Andy Connor (New Zealand)");
		programMem.add("Bin Wang (China)");
		programMem.add("Bo Zhou (China)");
		programMem.add("Chanchal K. Roy (Canada)");
		
		childList.put(headersList.get(0), general);
		childList.put(headersList.get(1), organization);
		childList.put(headersList.get(2), programChairs);
		childList.put(headersList.get(3), programMem);
	}

}
