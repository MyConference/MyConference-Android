package es.ucm.myconference;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class NavigationDrawerActivity extends ActionBarActivity {

		private ListView navigationDrawerList;
		private DrawerLayout navigationDrawerLayout;
		private static final String[] listOptions = {"Option 1", "Option 2", "Option 3"};
		private ActionBar actionBar;
		private ActionBarDrawerToggle navigationDrawerToggle;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation_drawer_layout);
		
		// Set up the action bar
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		navigationDrawerList = (ListView) findViewById(R.id.navigation_drawer_list);
		navigationDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);
		
		navigationDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
											android.R.id.text1, listOptions));
		
		navigationDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Toast.makeText(getApplicationContext(), listOptions[arg2] + " clicked", Toast.LENGTH_SHORT).show();
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
			}
	
			@Override
			public void onDrawerOpened(View drawerView) {
				actionBar.setTitle("Menu");
			} 
			// This is an example. Changing names.
			
		};
		
		navigationDrawerLayout.setDrawerListener(navigationDrawerToggle);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case android.R.id.home:
				return true;
		}
		return super.onOptionsItemSelected(item);
	}



	// Toggle with icon
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		navigationDrawerToggle.syncState();
	}
	
	

	
}
