package es.ucm.myconference;

import java.util.HashMap;
import java.util.List;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import es.ucm.myconference.accountmanager.SqlHelper;
import es.ucm.myconference.util.Constants;
import es.ucm.myconference.util.Data;

public class NavigationDrawerActivity extends MyConferenceActivity {

		private static final String TAG = "NavigationDrawerActivity";
	
		private ListView navigationDrawerList;
		private DrawerLayout navigationDrawerLayout;
		private ActionBar actionBar;
		private ActionBarDrawerToggle navigationDrawerToggle;
		private Spinner conferencesSpinner;
		private LinearLayout linear;
		//private Menu menu;
		private Cursor slideMenuCursor;
		private HashMap<String, String> conferencesList;
		private String confUUID;
	    private Account mAccount;
	    private AccountManager mAccountManager;
	    private Handler handler = new Handler();
	    private DatabaseObserver observer = null;
	    //SavedInstance
	    private int lastFragment = 0;
	    private boolean isMenuOpen = false;
	    //Receiver
	    private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				setProgressBarIndeterminateVisibility(false);
				//MenuItem refresh = menu.findItem(R.id.action_refresh_button);
		        //refresh.setVisible(false);
				Log.d("Sync", "Sync finished");
				//If any error, it will be in the intent extra
				boolean error = intent.getBooleanExtra(Constants.AUTH_ERROR, false);
				if(error){
					//Login with refresh_token - AsyncTask
					LoginRefreshToken mAsyncTask = new LoginRefreshToken();
					mAsyncTask.execute((Void) null);
					//onRefreshButton();
				}
			}
	    	
	    };
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.navigation_drawer_layout);
		
		setLogoutFalse();
		// Set up the action bar
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		// Get the account
		mAccountManager = AccountManager.get(this);
		getAccount();
		

		linear = (LinearLayout) findViewById(R.id.navigation_drawer_menu);
		
		navigationDrawerList = (ListView) findViewById(R.id.navigation_drawer_list);
		navigationDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);
		
		// List of options and list's adapter
		String[] from = new String[] {"item", "icon"};
		int[] to = new int[] {R.id.item_name, R.id.item_icon};
		slideMenuCursor = Data.getSlideMenuCursor(getResources().getStringArray(R.array.drawer_options));
		
		navigationDrawerList.setAdapter(new SimpleCursorAdapter(this, R.layout.drawer_menu_item,
											slideMenuCursor, from, to, 0));
		navigationDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		navigationDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?>  parent, View view, int position, long id) {
				displayFragment(position);
			}
		});
		
		navigationDrawerToggle = new ActionBarDrawerToggle(
									this, // Activity
									navigationDrawerLayout, // Navigation Drawer layout
									R.drawable.ic_drawer, // nav drawer icon to replace 'Up' caret
									R.string.app_name, // Description when drawer is opened
									R.string.app_name){ // Description when drawer is closed

			@Override
			public void onDrawerClosed(View drawerView) {
				actionBar.setTitle(conferencesSpinner.getSelectedItem().toString()
									+" - "+getResources().getStringArray(R.array.drawer_options)[lastFragment]);
				supportInvalidateOptionsMenu(); //Calls onPrepareOptionsMenu()
				isMenuOpen = false;
			}
	
			@Override
			public void onDrawerOpened(View drawerView) {
				actionBar.setTitle("Menu");
                supportInvalidateOptionsMenu();
                isMenuOpen = true;
			} 
		};
		
		navigationDrawerLayout.setDrawerListener(navigationDrawerToggle);
		
		// Conferences hashmap
		setConferencesHashmap();

		// Conferences spinner
		conferencesSpinner = (Spinner) findViewById(R.id.navigation_drawer_conferences);
		setSpinner();
		conferencesSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				displayFragment(lastFragment);
				if(isMenuOpen)
					navigationDrawerLayout.openDrawer(linear);
				else
					navigationDrawerLayout.closeDrawer(linear);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		actionBar.setTitle(conferencesSpinner.getSelectedItem().toString()
				+" - "+getResources().getStringArray(R.array.drawer_options)[lastFragment]);

		//Register observers
		registerObservers();
		
		//Load data for the first time 
		if(isFirstTime()){
			Log.d(TAG, "isFirstTime");
			setFirstTime(false);
			navigationDrawerLayout.openDrawer(linear);
			onRefreshButton();
		}
		
		
	}

	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//Remove action items that are contextual to the main content
		menu.findItem(R.id.action_refresh_button).setVisible(isMenuOpen);
		return super.onPrepareOptionsMenu(menu);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.home, menu);
	    //this.menu = menu;
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case android.R.id.home:
				// Pass the event to ActionBarDrawerToggle, if it returns
		        // true, then it has handled the app icon touch event
                if (navigationDrawerLayout.isDrawerOpen(linear)) {
            			navigationDrawerLayout.closeDrawer(linear);
            			isMenuOpen = false;
                } else {
                		navigationDrawerLayout.openDrawer(linear);
                		isMenuOpen = true;
                }
                return true;
			
			case R.id.action_logout:
				// Save logout in SharedPreferences
				SharedPreferences preferences = this.getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putBoolean(Constants.LOGOUT, true);
				editor.commit();
				// Exit app
				finish();
				return true;
				
			case R.id.action_refresh_button:
				
				onRefreshButton();
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
        }
	}

	// Toggle with icon
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		navigationDrawerToggle.syncState();
	}
	
	private String getUserId(){
		SharedPreferences user = getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
		return user.getString(Constants.USER_ID, null);
	}
	
	private String getUserName(){
		SharedPreferences user = getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
		return user.getString(Constants.USER_NAME, null);
	}
	
	private String getUserAccessToken(){
		SharedPreferences user = getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
		return user.getString(Constants.ACCESS_TOKEN, null);
	}
	
	private String getUserRefreshToken(){
		SharedPreferences user = getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
		return user.getString(Constants.REFRESH_TOKEN, null);
	}
	
	private void setLogoutFalse(){
		SharedPreferences preferences = this.getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(Constants.LOGOUT, false);
		editor.commit();
	}
	
	private void setFirstTime(boolean it){
		SharedPreferences preferences = this.getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(Constants.FIRST_TIME, it);
		editor.commit();
	}
	
	private boolean isFirstTime(){
		SharedPreferences user = getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
		return user.getBoolean(Constants.FIRST_TIME, true);
	}
	
	public void setSpinner(){
		SqlHelper helper = new SqlHelper(getApplicationContext());
		List<String> list = helper.getConfsNames();
		// Fill the spinner with the list pass or if it's empty, with default list
		if(list.isEmpty()){
			list.add("Loading...");
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
																	list);
		//TODO Custom spinner
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_list_item, 
		//														R.id.spinner_item, list);
		//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		conferencesSpinner.setAdapter(adapter);		
	}
	
	public void setConferencesHashmap(){
		conferencesList = new HashMap<String, String>();
		Cursor cursor;
		Uri uri = Uri.parse("content://" + Constants.PROVIDER_NAME + "/conferences");
		String[] columns = new String[]{
				Constants._ID,
				Constants.CONF_NAME,
				Constants.CONF_DESCRP,
		};
		
		cursor = getContentResolver().query(uri, columns, null, null, null);
		if(cursor!=null){
			while(cursor.moveToNext()){
				conferencesList.put(cursor.getString(1), cursor.getString(2));
			}
		}
		cursor.close();
	}
	
	private void displayFragment(int position){

        Fragment fragment = null;
        Bundle args = null;
        getCurrentConferenceID();
        switch(position){
        	case 0:
        		fragment = new WhatsNewFragment();
        		args = new Bundle();
        		args.putString(Constants.CONF_UUID, confUUID);
        		fragment.setArguments(args);
        		break;
        	case 2:
        		fragment = new CommitteeFragment();
        		args = new Bundle();
        		args.putString(Constants.CONF_UUID, confUUID);
        		fragment.setArguments(args);
        		break;
        	case 3:
        		fragment = new KeynoteFragment();
        		args = new Bundle();
        		args.putString(Constants.CONF_UUID, confUUID);
        		fragment.setArguments(args);
        		break;
        	case 4:
        		fragment = new VenuesFragment();
        		args = new Bundle();
        		args.putString(Constants.CONF_UUID, confUUID);
        		fragment.setArguments(args);
        		break;	
        	case 5:
        		fragment = new TravelInfoFragment();
        		break;
        	case 6:
        		fragment = new LinksFragment();
        		args = new Bundle();
        		args.putString(Constants.CONF_UUID, confUUID);
        		fragment.setArguments(args);
        		break;
        	case 7:
        		fragment = new AboutFragment();
        		args = new Bundle();
        		args.putString(Constants.CONF_NAME, conferencesSpinner.getSelectedItem().toString());
        		args.putString(Constants.CONF_DESCRP, conferencesList.get(conferencesSpinner.getSelectedItem()));
        		fragment.setArguments(args);
        		break;
        }
        
        if(fragment!=null){
        	FragmentManager fragmentManager = getSupportFragmentManager();
        	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        	fragmentTransaction.replace(R.id.main_layout, fragment);
        	fragmentTransaction.commit();
        	
        	// Highlight the selected item and close drawer
            navigationDrawerList.setItemChecked(position, true);
            navigationDrawerList.setSelection(position);
    		navigationDrawerLayout.closeDrawer(linear);
    		isMenuOpen = false;
        }
        lastFragment = position;
        
	}
	
	@Override
	public void onBackPressed() {
		AlertDialog.Builder exit = new AlertDialog.Builder(NavigationDrawerActivity.this);
		exit.setTitle(R.string.alert_title);
		exit.setMessage(R.string.alert_text);
		exit.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				NavigationDrawerActivity.super.onBackPressed();
			}
		});
		exit.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Nothing to do
				dialog.cancel();
			}
		});
		exit.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//Register receiver for sync status
		registerReceiver(syncFinishedReceiver, new IntentFilter(Constants.SYNC_FINISHED));
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "onStart()");
		super.onStart();
		//Register observers
		registerObservers();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//Unregister receiver
		unregisterReceiver(syncFinishedReceiver);
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop()");
		super.onStop();
		// Save SharedPreferences for keeping login
		SharedPreferences preferences = this.getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
		if(!preferences.getBoolean(Constants.LOGOUT, false)){
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(Constants.LOGOUT, false);
			editor.commit();
		}
		
		//Unregister observers
		unregisterObservers();
	}
	
	private void onRefreshButton(){

		Log.d(TAG, "onRefreshButton()");
		//Respond by calling requestSync(). This is an asynchronous operation.
		//Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        //User's id
        settingsBundle.putString(Constants.USER_UUID, getUserId()); 
        //User's access token
        settingsBundle.putString(Constants.ACCESS_TOKEN, getUserAccessToken()); 
        //Current conference's id
        settingsBundle.putString(Constants.CONF_NAME, conferencesSpinner.getSelectedItem().toString()); 
        
         //Request the sync for the default account, authority, and manual sync settings
        Log.d(TAG, "Account = " + mAccount.toString());
        ContentResolver.requestSync(mAccount, Constants.AUTHORITY, settingsBundle);
        
        //Set progress
        //MenuItem refresh = menu.findItem(R.id.action_refresh_button);
        //refresh.setVisible(false);
        setProgressBarIndeterminateVisibility(true);
	}
	
	private void getCurrentConferenceID(){
		Cursor c;
		Uri uri = Uri.parse("content://" + Constants.PROVIDER_NAME + "/conferences");
		String[] columns = new String[] {
				Constants.CONF_NAME,
				Constants.CONF_UUID
		};
		
		String where = Constants.CONF_NAME + " = ?";
		String[] whereArgs = { conferencesSpinner.getSelectedItem().toString() };
		c = getContentResolver().query(uri, columns, where, whereArgs, null);
		if(c != null){
			if(c.moveToFirst()){
				confUUID = c.getString(1);
			}
		}
		c.close();
	}
    
    public void getAccount(){
    	final Account[] accounts = mAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
    	if(accounts.length == 1){
    		mAccount = accounts[0];
    	} else {
    		String name[] = new String[accounts.length];
    		String userName = getUserName();
            for (int i = 1; i < accounts.length; i++) {
                name[i] = accounts[i].name;
                if(name[i].equals(userName))
                	mAccount = accounts[i];
            }
    	}
    }
    
    private class DatabaseObserver extends ContentObserver{

		public DatabaseObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			//When database changes, fill spinner with new data
	        setConferencesHashmap();
	        setSpinner();
		}

		@Override
		public void onChange(boolean selfChange) {
			onChange(selfChange, null);
		}
    }
    
    private void registerObservers(){
    	ContentResolver resolver = getContentResolver();
    	observer = new DatabaseObserver(handler);
    	resolver.registerContentObserver(Constants.CONTENT_URI_CONFS, true, observer);
    }
    
    private void unregisterObservers(){
    	ContentResolver resolver = getContentResolver();
    	resolver.unregisterContentObserver(observer);
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("lastFragment", lastFragment);
		outState.putBoolean("isMenuOpen", isMenuOpen);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		lastFragment = savedInstanceState.getInt("lastFragment");
		isMenuOpen = savedInstanceState.getBoolean("isMenuOpen");
	}
    
    public class LoginRefreshToken extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			String refresh_token = getUserRefreshToken();
			Login login = new Login(getApplicationContext(), refresh_token);
			Log.d(TAG, "loginWithRefresh()");
			String authToken = login.loginWithRefresh();
			return authToken;
		}

		@Override
		protected void onPostExecute(String authToken) {
			mAccountManager.setAuthToken(mAccount, Constants.AUTHTOKEN_TYPE, authToken);
		}

    }
}
