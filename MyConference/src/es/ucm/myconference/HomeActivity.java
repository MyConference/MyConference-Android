package es.ucm.myconference;

import com.actionbarsherlock.app.ActionBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends ActionBarActivity implements OnClickListener {
	
	private ActionBar actionBar;
	private Button testButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		// Set up the action bar
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		
		// Test button for Navigation Drawer
		testButton = (Button) findViewById(R.id.test_button);
		testButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(HomeActivity.this, NavigationDrawerActivity.class);
		startActivity(intent);
	}

}
