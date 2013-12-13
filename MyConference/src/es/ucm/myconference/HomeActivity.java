package es.ucm.myconference;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.actionbarsherlock.app.ActionBar;

public class HomeActivity extends ActionBarActivity implements OnClickListener {
	
	private ActionBar actionBar;
	private Button testButton;
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
	//private static final String BASE_URL = "http://raspi.darkhogg.es:4321/v0.1/auth/signup";
	//private static final String APP_ID = "df3ae937-c8d6-40f8-8145-c8747c3ca56c";
	//private static final String USER_PREF = "UserID";
	/* Login layout */
	private Button homeSignInButton;
	private EditText signInEmail;
	private EditText signInPass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_home);
		
		// Set up the action bar
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
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
		
		// Test button for Navigation Drawer
		testButton = (Button) findViewById(R.id.test_button);
		testButton.setOnClickListener(this);
		
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
			signUpPass.setText("");
			signUpRepeatPass.setText("");
		} else { //Everything okey
			signUpWrongPass.setVisibility(View.GONE);
			
			// Request
			//new RegisterAsyncTask().execute(BASE_URL);
			
			
			 Register register = new Register(getApplicationContext(),signUpEmail.getText().toString(),
			 									signUpPass.getText().toString(), this);
			 register.register();
			 
		}
	}
	
	/*private static String post(String url, Context context) throws ClientProtocolException, IOException{
		String result  = "";
		try{
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(url);
			
			// Include header
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Accept", "application/json");
			
			// Build JSON Object
			JSONObject jsonObject = new JSONObject();
			buildJSONObject(jsonObject, context);
			
			// JSON to String
			String json = jsonObject.toString();
			StringEntity se = new StringEntity(json);
			
			// Set HttpPost entity
			request.setEntity(se);
			
			//Execute
			HttpResponse response = client.execute(request);
			
			// Response as Inputstream and convert to String
			InputStream inputStream = response.getEntity().getContent();
			if(inputStream != null){
				result = inputStreamToString(inputStream);
			}
			
		} catch(Exception e){
			throw new RuntimeException(e);
		}
		return result;
	}
	
	private class RegisterAsyncTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			try {
				return post(params[0], getApplicationContext());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} 
		}

		@Override
		protected void onPostExecute(String result) {
			if(result!=null){
				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
				
				// Save user_id in SharedPreferences
				try{
					JSONArray arr = new JSONArray(result);
					JSONObject info = arr.getJSONObject(0);
					SharedPreferences preference = getPreferences(0);
					SharedPreferences.Editor editor = preference.edit();
					editor.putString(USER_PREF,info.getString("user_id"));
					editor.commit();
					
				} catch(JSONException e){
					throw new RuntimeException(e);
				}
			} else Toast.makeText(getApplicationContext(), "No data", Toast.LENGTH_LONG).show();
		}
		
	}
	
	private static String inputStreamToString(InputStream inputStream){
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        StringBuilder result = new StringBuilder();
        
        try {
			while((line = bufferedReader.readLine()) != null){
			    result.append(line);
			}
	        inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
        return result.toString();
	}
	
	private static void buildJSONObject(JSONObject jsonObject, Context context){
		try {
			jsonObject.accumulate("application_id", APP_ID);
			jsonObject.accumulate("device_id", Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
			JSONObject jsonUserData = new JSONObject();
			jsonUserData.accumulate("email", signUpEmail.getText().toString());
			jsonUserData.accumulate("password", signUpPass.getText().toString());
			jsonObject.accumulate("user_data", jsonUserData);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}*/
}
