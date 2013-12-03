package es.ucm.myconference;

import com.actionbarsherlock.app.ActionBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class HomeActivity extends ActionBarActivity implements OnClickListener {
	
	private ActionBar actionBar;
	private Button testButton;
	private ViewFlipper mViewFlipper;
	private Button homeStartSignIn;
	private Button homeStartSignUp;
	private TextView notRegistered;
	private TextView registered;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		// Set up the action bar
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		
		// View Flipper actions
		mViewFlipper = (ViewFlipper) findViewById(R.id.home_view_flipper);
		// Sign in button
		homeStartSignIn = (Button) findViewById(R.id.home_start_sign_in_button);
		homeStartSignIn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeToSignIn();
			}
		});
		// If not registered
		notRegistered = (TextView) findViewById(R.id.home_sign_in_not_registered);
		notRegistered.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeToSignUp();
			}
		});
		// Sign up button
		homeStartSignUp = (Button) findViewById(R.id.home_start_sign_up_button);
		homeStartSignUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeToSignUp();
			}
		});
		//If registered
		registered = (TextView) findViewById(R.id.home_sign_up_registered);
		registered.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeToSignIn();
			}
		});
		
		// Test button for Navigation Drawer
		testButton = (Button) findViewById(R.id.test_button);
		testButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(HomeActivity.this, NavigationDrawerActivity.class);
		startActivity(intent);
	}
	
	private void changeToSignIn(){
		mViewFlipper.setDisplayedChild(mViewFlipper.indexOfChild(findViewById(R.id.home_sign_in)));
		actionBar.setTitle(R.string.home_login);
	}
	
	private void changeToSignUp(){
		mViewFlipper.setDisplayedChild(mViewFlipper.indexOfChild(findViewById(R.id.home_sign_up)));
		actionBar.setTitle(R.string.home_register);
	}

	@Override
	public void onBackPressed() {
		//Go back to start layout
		if(mViewFlipper.getDisplayedChild() == mViewFlipper.indexOfChild(findViewById(R.id.home_sign_in))
				|| mViewFlipper.getDisplayedChild() == mViewFlipper.indexOfChild(findViewById(R.id.home_sign_up))){
			mViewFlipper.setDisplayedChild(mViewFlipper.indexOfChild(findViewById(R.id.home_start)));
			actionBar.setTitle(R.string.app_name);
		}else{
			super.onBackPressed();
		}
	}
	
	

}
