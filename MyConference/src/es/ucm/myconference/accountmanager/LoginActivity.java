package es.ucm.myconference.accountmanager;

import es.ucm.myconference.Login;
import es.ucm.myconference.NavigationDrawerActivity;
import es.ucm.myconference.R;
import es.ucm.myconference.Register;
import es.ucm.myconference.util.Constants;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends AccountAuthenticatorActivity {

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	
	private AccountManager mAccountManager;
	private String mAuthTokenType;
	private final String TAG = "LoginActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// If the user didn't logout before exit, keep him logged in
		if(!getLogout()){
			Intent i = new Intent(this, NavigationDrawerActivity.class);
			startActivity(i);
			finish();
		}
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
		mAccountManager = AccountManager.get(getBaseContext());
		mAuthTokenType = Constants.AUTHTOKEN_TYPE;

		// Set up the login form.
		mEmailView = (EditText) findViewById(R.id.email);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 9) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Intent> {
		@Override
		protected Intent doInBackground(Void... params) {
			//Attempt authentication against a network service.
			String authToken = null;
			Bundle data = new Bundle();
			Login login = new Login(getApplicationContext(), mEmail, mPassword);
			try {
				Log.d(TAG, "login.userLogin()");
				authToken = login.userLogin();
				
				data.putString(AccountManager.KEY_ACCOUNT_NAME, mEmail);
				data.putString(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
				data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
				data.putString(Constants.USER_PASS, mPassword);
				
			} catch (Exception e) {
				data.putString(Constants.ERROR_MSG, e.getMessage());
			}

			if(data.containsKey(Constants.ERROR_MSG)){
				//Remove previous errors
				data.remove(Constants.ERROR_MSG);
				//Cannot login. Register the new account
				Register register = new Register(getApplicationContext(), mEmail, mPassword);
				try{
					Log.d(TAG, "userRegisterAndLogin()");
					register.userRegisterAndLogin();

					data.putString(AccountManager.KEY_ACCOUNT_NAME, mEmail);
					data.putString(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
					data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
					data.putString(Constants.USER_PASS, mPassword);
					
				} catch (Exception e) {
					data.putString(Constants.ERROR_MSG, e.getMessage());
				}
			}
			
			final Intent res = new Intent();
			res.putExtras(data);
			return res;
		}

		@Override
		protected void onPostExecute(Intent intent) {
			mAuthTask = null;
			showProgress(false);

			if (!intent.hasExtra(Constants.ERROR_MSG)) {
				Log.d(TAG, "Login or Register ok");
				finishLogin(intent);
			} else {
				Log.d(TAG, "Error while login or registering: " + intent.getStringExtra(Constants.ERROR_MSG));
				if(intent.getStringExtra(Constants.ERROR_MSG).startsWith("Email")){
					//Email already existing. Wrong password
					mPasswordView.requestFocus();
					mPasswordView.setError(getString(R.string.error_incorrect_password));
				} else {
					Toast.makeText(getApplicationContext(), R.string.home_login_error, Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
		
		private void finishLogin(Intent intent){
			String userEmail = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			String userPass = intent.getStringExtra(Constants.USER_PASS);
			
			final Account account = new Account(userEmail, 
												intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
			
			Log.d(TAG, "Adding Account");
			//Add account
			String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
			mAccountManager.addAccountExplicitly(account, userPass, null);
	        // Not setting the auth token will cause another call to the server to authenticate the user
			mAccountManager.setAuthToken(account, mAuthTokenType, authToken);
			
			//Return the information back to the Authenticator
			setAccountAuthenticatorResult(intent.getExtras());
			setResult(RESULT_OK, intent);
			
			//Redirect to NavigationDrawer activity
			Intent i = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
			startActivity(i);
			finish();
		}
	}
	
	private Boolean getLogout(){
		SharedPreferences user = getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
		return user.getBoolean(Constants.LOGOUT, true);
	}
}
