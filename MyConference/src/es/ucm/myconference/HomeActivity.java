package es.ucm.myconference;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.actionbarsherlock.app.ActionBar;

import es.ucm.myconference.util.Constants;

public class HomeActivity extends MyConferenceActivity {
	
	private ActionBar actionBar;
	private ViewFlipper mViewFlipper;
	private Button homeStartSignIn;
	private Button homeStartSignUp;
	private TextView notRegistered;
	private TextView registered;
	/* Register layout */
	private Button homeSignUpButton;
	private static EditText signUpEmail;
	private static EditText signUpPass;
	private EditText signUpRepeatPass;
	private TextView signUpWrongPass;
	/* Login layout */
	private Button homeSignInButton;
	private EditText signInEmail;
	private EditText signInPass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// If the user didn't logout before exit, keep him log in
		if(!getLogout()){
			Intent i = new Intent(this, NavigationDrawerActivity.class);
			startActivity(i);
			finish();
		}
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_home);
		
		// Set up the action bar
		actionBar = getSupportActionBar();
		setProgressBarIndeterminateVisibility(false);
		
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
		
		// Register layout
		homeSignUpButton = (Button) findViewById(R.id.home_sign_up_button);
		homeSignUpButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				register();
			}
		});
		
		//Login layout
		homeSignInButton = (Button) findViewById(R.id.home_sign_in_button);
		homeSignInButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				signInEmail = (EditText) findViewById(R.id.home_sign_in_email);
				signInPass = (EditText) findViewById(R.id.home_sign_in_password);
				Login login = new Login(getApplicationContext(), signInEmail.getText().toString(),
										signInPass.getText().toString(), HomeActivity.this);
				login.login();
			}
		});
	}
	
	private void changeToSignIn(){
		mViewFlipper.setDisplayedChild(mViewFlipper.indexOfChild(findViewById(R.id.home_sign_in)));
		actionBar.setTitle(R.string.home_login);
	}
	
	private void changeToSignUp(){
		// Wrong passwords' label not shown
		signUpWrongPass = (TextView) findViewById(R.id.home_sign_up_wrong_passwords);
		signUpWrongPass.setVisibility(View.GONE);
		
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
	
	
	private void register(){
		signUpEmail = (EditText) findViewById(R.id.home_sign_up_email);
		signUpPass = (EditText) findViewById(R.id.home_sign_up_password);
		signUpRepeatPass = (EditText) findViewById(R.id.home_sign_up_password_again);
		
		//Both passwords must be equal
		if(!signUpPass.getText().toString().equals(signUpRepeatPass.getText().toString())){
			signUpWrongPass.setVisibility(View.VISIBLE);
			signUpPass.setText("");
			signUpRepeatPass.setText("");
		} else if(signUpPass.getText().length() < 8){ // Password must have 8 characters minimum
			signUpWrongPass.setText(R.string.home_password_length);
			signUpWrongPass.setVisibility(View.VISIBLE);
			signUpRepeatPass.setText("");
		} else { //Everything okey
			signUpWrongPass.setVisibility(View.GONE);
			
			// Request
			 Register register = new Register(getApplicationContext(),signUpEmail.getText().toString(),
			 									signUpPass.getText().toString(), this);
			 register.register();
			 
		}
	}
	
	private Boolean getLogout(){
		SharedPreferences user = getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
		return user.getBoolean(Constants.LOGOUT, true);
	}
}
