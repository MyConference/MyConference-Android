package es.ucm.myconference.accountmanager;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.ucm.myconference.util.Constants;
import es.ucm.myconference.util.Data;
import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {
	
	// Define a variable to contain a content resolver instance
	private ContentResolver mContentResolver;
	private final static String BASE_URL = "http://myconf-api-dev.herokuapp.com/users/";

	
     // Set up the sync adapter
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		
		mContentResolver = context.getContentResolver();
	}

	@SuppressLint("NewApi")
	public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
	        super(context, autoInitialize, allowParallelSyncs);
	        
	        mContentResolver = context.getContentResolver(); 
	}
	
	/*
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, 
								SyncResult syncResult) {
		//Put the data transfer code here.
		
		//GET conferences
		Log.d("sync", "GET conferences");
		String result="";
		
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(BASE_URL+extras.getString(Constants.USER_UUID)+"/conferences");
		
		// Include header
		request.setHeader("Content-Type", "application/json");
		request.setHeader("Accept", "application/json");
		request.setHeader("Authorization", "Token "+extras.getString(Constants.ACCESS_TOKEN));
		try{
			//Execute
			HttpResponse response = client.execute(request);
			
			// Response as Inputstream and convert to String
			InputStream inputStream = response.getEntity().getContent();
			if(inputStream != null){
				result = Data.inputStreamToString(inputStream);
			}
		} catch(IOException e){
			e.printStackTrace();
		}
		
		Log.d("conferences", result);
		if(result!=null){
			// Get conferences names into an Arraylist
			try {
				JSONArray jsonConfs = new JSONArray(result);
				if(jsonConfs.length()!=0){
					Uri url = Uri.parse("content://" + Constants.PROVIDER_NAME + "/conferences");
					ContentValues values;
					for(int i=0; i<jsonConfs.length();i++){
						JSONObject conf = jsonConfs.getJSONObject(i);
						//Save data to provider
						values = new ContentValues();
						values.put(Constants.CONF_NAME, conf.getString(Constants.CONF_NAME));
						values.put(Constants.CONF_DESCRP, conf.getString(Constants.CONF_DESCRP));
						mContentResolver.insert(url, values);
						//TODO Avisar al spinner para que cambie. Mirar Loaders
					}
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
