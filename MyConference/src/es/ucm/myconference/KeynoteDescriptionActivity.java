package es.ucm.myconference;

import com.actionbarsherlock.app.ActionBar;

import es.ucm.myconference.util.Constants;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class KeynoteDescriptionActivity extends MyConferenceActivity {

	private ActionBar actionBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keynote_description);
		
		actionBar = getSupportActionBar();
		actionBar.setTitle("Description");
		
		TextView descrp = (TextView) findViewById(R.id.keynote_description);
		Intent i = getIntent();
		descrp.setText(i.getStringExtra(Constants.KEYNOTES_DESCRIPTION));
	}

}
