package es.ucm.myconference;

import com.actionbarsherlock.app.ActionBar;

import es.ucm.myconference.util.Constants;
import es.ucm.myconference.util.SpeakersDescriptionHelper;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

public class KeynoteDescriptionActivity extends MyConferenceActivity {

	private ActionBar actionBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keynote_description);
		
		actionBar = getSupportActionBar();
		actionBar.setTitle("Description");
		
		ImageView photo = (ImageView) findViewById(R.id.keynote_description_photo);
		TextView descrp = (TextView) findViewById(R.id.keynote_description);
		Intent i = getIntent();
		String text = i.getStringExtra(Constants.KEYNOTES_DESCRIPTION);
		descrp.setText(text);
		int position = i.getIntExtra("position", 0);
		if(position==0){
			photo.setImageResource(R.drawable.h_fujita);
		} else if(position==1){
			photo.setImageResource(R.drawable.d_roth);
		} else if(position==2){
			photo.setImageResource(R.drawable.gzhou);
		} else {
			photo.setImageResource(R.drawable.vlopez);
		}
		
		Display display = getWindowManager().getDefaultDisplay();
		SpeakersDescriptionHelper.tryFlowText(text, photo, descrp, display);
	}

}
