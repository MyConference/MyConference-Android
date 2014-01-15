package es.ucm.myconference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class NavigationDrawerActivity extends ActionBarActivity {

		private ListView navigationDrawerList;
		private DrawerLayout navigationDrawerLayout;
		private ActionBar actionBar;
		private ActionBarDrawerToggle navigationDrawerToggle;
		private String[] drawerOptions;
		private Spinner conferencesSpinner;
		private LinearLayout linear;
		private final static String BASE_URL = "http://myconf-api-dev.herokuapp.com/users/";
		
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation_drawer_layout);
		
		// Set up the action bar
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		linear = (LinearLayout) findViewById(R.id.navigation_drawer_menu);
		
		navigationDrawerList = (ListView) findViewById(R.id.navigation_drawer_list);
		navigationDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);
		//navigationDrawerLayout.openDrawer(navigationDrawerList);
		navigationDrawerLayout.openDrawer(linear);
		
		// List of options and list's adapter
		drawerOptions = getResources().getStringArray(R.array.drawer_options);
		navigationDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
											android.R.id.text1, drawerOptions));
		
		navigationDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?>  parent, View view, int position, long id) {
				// Highlight the selected item and close drawer
                navigationDrawerList.setItemChecked(position, true);
                //navigationDrawerLayout.closeDrawer(navigationDrawerList);
        		navigationDrawerLayout.closeDrawer(linear);
                
				Toast.makeText(getApplicationContext(), drawerOptions[position] + " clicked", Toast.LENGTH_SHORT).show();
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
				 invalidateOptionsMenu(); //Not done yet. Remove action items that are contextual to the main content
			}
	
			@Override
			public void onDrawerOpened(View drawerView) {
				actionBar.setTitle("Menu");
                invalidateOptionsMenu();
			} 
		};
		
		navigationDrawerLayout.setDrawerListener(navigationDrawerToggle);
		
		// Conferences spinner
		conferencesSpinner = (Spinner) findViewById(R.id.navigation_drawer_conferences);
		String[] conferences = {"Conference 1", "Conference 2"};
		
		// With AsyncTask
		String uuid = getUserId();
		new ConferencesAsyncTask().execute(BASE_URL+uuid+"/conferences");
		
		conferencesSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
																conferences));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (item.getItemId() == android.R.id.home) {

                if (navigationDrawerLayout.isDrawerOpen(linear)) {
                        //navigationDrawerLayout.closeDrawer(navigationDrawerList);
            			navigationDrawerLayout.closeDrawer(linear);
                } else {
                        //navigationDrawerLayout.openDrawer(navigationDrawerList);
                		navigationDrawerLayout.openDrawer(linear);
                }
        }
		return super.onOptionsItemSelected(item);
	}

	// Toggle with icon
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		navigationDrawerToggle.syncState();
	}
	
	private String getUserId(){
		SharedPreferences user = getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
		return user.getString("id", null);
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
		protected void onPostExecute(String result) {
			Log.d("conferences", result);
			if(result!=null){
				// Get conferences names
				try {
					JSONObject jsonConfs = new JSONObject(result);
					
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
