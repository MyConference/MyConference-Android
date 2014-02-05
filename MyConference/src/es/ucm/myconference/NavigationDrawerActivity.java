package es.ucm.myconference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import es.ucm.myconference.util.Constants;
import es.ucm.myconference.util.Data;

public class NavigationDrawerActivity extends MyConferenceActivity {

		private ListView navigationDrawerList;
		private DrawerLayout navigationDrawerLayout;
		private ActionBar actionBar;
		private ActionBarDrawerToggle navigationDrawerToggle;
		private Spinner conferencesSpinner;
		private LinearLayout linear;
		private Cursor slideMenuCursor;
		private HashMap<String, String> conferencesList;
		private final static String BASE_URL = "http://myconf-api-dev.herokuapp.com/users/";
	    private Account mAccount;
		
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation_drawer_layout);
		
		setLogoutFalse();
		// Set up the action bar
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Menu");
		
		// Create the test account
		if(mAccount!=null)
			mAccount = CreateSyncAccount(this);

		linear = (LinearLayout) findViewById(R.id.navigation_drawer_menu);
		
		navigationDrawerList = (ListView) findViewById(R.id.navigation_drawer_list);
		navigationDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);
		navigationDrawerLayout.openDrawer(linear);
		
		// List of options and list's adapter
		String[] from = new String[] {"item", "icon"};
		int[] to = new int[] {R.id.item_name, R.id.item_icon};
		slideMenuCursor = Data.getSlideMenuCursor(getResources().getStringArray(R.array.drawer_options));
		
		navigationDrawerList.setAdapter(new SimpleCursorAdapter(this, R.layout.drawer_menu_item,
											slideMenuCursor, from, to, 0));
		
		navigationDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?>  parent, View view, int position, long id) {
				// Highlight the selected item and close drawer
                navigationDrawerList.setItemChecked(position, true);
        		navigationDrawerLayout.closeDrawer(linear);
                
				displayFragment(position);
			}
		});
		
		navigationDrawerToggle = new ActionBarDrawerToggle(
									this, // Activity
									navigationDrawerLayout, // Navigation Drawer layout
									R.drawable.ic_drawer, // Icon to use
									R.string.app_name, // Description when drawer is opened
									R.string.hello_world){ // Description when drawer is closed

			@Override
			public void onDrawerClosed(View drawerView) {
				 actionBar.setTitle(getResources().getString(R.string.app_name));
				 invalidateOptionsMenu(); //TODO Remove action items that are contextual to the main content
			}
	
			@Override
			public void onDrawerOpened(View drawerView) {
				actionBar.setTitle("Menu");
                invalidateOptionsMenu();
			} 
		};
		
		navigationDrawerLayout.setDrawerListener(navigationDrawerToggle);
		
		// Conferences spinner with AsyncTask
		conferencesList = new HashMap<String, String>();
		conferencesSpinner = (Spinner) findViewById(R.id.navigation_drawer_conferences);
		String uuid = getUserId();
		new ConferencesAsyncTask().execute(BASE_URL+uuid+"/conferences");
		
		conferencesSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				displayFragment(7);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
		switch(item.getItemId()){
			case android.R.id.home:
                if (navigationDrawerLayout.isDrawerOpen(linear)) {
            			navigationDrawerLayout.closeDrawer(linear);
                } else {
                		navigationDrawerLayout.openDrawer(linear);
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
				
			case R.id.action_refresh:
				//Respond by calling requestSync(). This is an asynchronous operation.
				// Pass the settings flags by inserting them in a bundle
		        Bundle settingsBundle = new Bundle();
		        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		        
		         //Request the sync for the default account, authority, and manual sync settings
		        ContentResolver.requestSync(mAccount, Constants.AUTHORITY, settingsBundle);
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
	
	private String getUserAccessToken(){
		SharedPreferences user = getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
		return user.getString(Constants.ACCESS_TOKEN, null);
	}
	
	private void setLogoutFalse(){
		SharedPreferences preferences = this.getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(Constants.LOGOUT, false);
		editor.commit();
	}
	
	private void setSpinner(Set<String> conferencesList){
		//Create a list
		List<String> list = new ArrayList<String>(conferencesList);
		// Fill the spinner with the list pass or if it's empty, with default list
		if(list.isEmpty()){
			list.add("No data");
		}
		
		conferencesSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, 
									list));
	}
	
	private void displayFragment(int position){

        Fragment fragment = null;
        switch(position){
        	case 0:
        		fragment = new WhatsNewFragment();
        		break;
        	case 7:
        		fragment = new AboutFragment();
        		Bundle args = new Bundle();
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
        }
        
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
	protected void onStop() {
		super.onStop();
		// Save SharedPreferences for keeping login
		SharedPreferences preferences = this.getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
		if(!preferences.getBoolean(Constants.LOGOUT, false)){
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(Constants.LOGOUT, false);
			editor.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.home, menu);
	    return true;
	}
	
	/**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(Constants.ACCOUNT, Constants.ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
           return newAccount;
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        	Log.d("AccountManager", "Some error occurred.");
        	return mAccount;
        }
    }




	private class ConferencesAsyncTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			try {
				return get(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected void onPostExecute(String result) {
			setProgressBarIndeterminateVisibility(false);
			Log.d("conferences", result);
			if(result!=null){
				// Get conferences names into an Arraylist
				try {
					JSONArray jsonConfs = new JSONArray(result);
					if(jsonConfs.length()!=0){
						for(int i=0; i<jsonConfs.length();i++){
							JSONObject conf = jsonConfs.getJSONObject(i);
							conferencesList.put(conf.getString(Constants.CONF_NAME), conf.getString(Constants.CONF_DESCRP));
						}
					}
					setSpinner(conferencesList.keySet());
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		private String get(String url) throws ClientProtocolException, IOException{
			String result="";
			
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			
			// Include header
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Accept", "application/json");
			request.setHeader("Authorization", "Token "+getUserAccessToken());
			
			//Execute
			HttpResponse response = client.execute(request);
			
			// Response as Inputstream and convert to String
			InputStream inputStream = response.getEntity().getContent();
			if(inputStream != null){
				result = inputStreamToString(inputStream);
			}
			
			return result;
		}
		
		private String inputStreamToString(InputStream inputStream){
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
	}
}
