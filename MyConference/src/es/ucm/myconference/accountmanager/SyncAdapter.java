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
	private String confIdForRequest = "";
	private final static String CONFS_URL = "http://myconf-api-dev.herokuapp.com";

	
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
		
		//GET users/<uuid>/conferences
		Log.d("sync", "GET users/"+extras.getString(Constants.USER_UUID)+"/conferences");
		String result="";
		
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(CONFS_URL+"/users/"+extras.getString(Constants.USER_UUID)+"/conferences");
		
		// Include header
		request.setHeader("Content-Type", "application/json");
		request.setHeader("Accept", "application/json");
		request.setHeader("Authorization", "Token "+extras.getString(Constants.ACCESS_TOKEN));

		result = executeAndResult(client, request);
		
		Log.d("Conferences", result);
		if(result!=null){
			if(result.startsWith("{")){
				Log.d("Error", "Something wrong happened");
				//TODO Hacer algo. La respuesta no es un array
			} else{
			// Get conferences names into an Arraylist
				try {
					JSONArray jsonConfs = new JSONArray(result);
					if(jsonConfs.length()!=0){
						Uri url = Uri.parse("content://" + Constants.PROVIDER_NAME + "/conferences");
						//Delete previuos values
						mContentResolver.delete(url, "1", null);
						
						ContentValues values;
						for(int i=0; i<jsonConfs.length();i++){
							JSONObject conf = jsonConfs.getJSONObject(i);
							//Save data to provider
							values = new ContentValues();
							String confName = conf.getString(Constants.CONF_NAME);
							values.put(Constants.CONF_NAME, confName);
							values.put(Constants.CONF_DESCRP, conf.getString(Constants.CONF_DESCRP));
							String confID = conf.getString("id");
							values.put(Constants.CONF_UUID, confID);
							if(confName.equals(extras.getString(Constants.CONF_NAME))){
								confIdForRequest = confID;
							}
							mContentResolver.insert(url, values);
						}
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		client = null;
		request = null;
		result="";
		
		//GET /conferences/<uuid>
		Log.d("sync", "GET /conferences/" + confIdForRequest);
		
		client = new DefaultHttpClient();
		request = new HttpGet(CONFS_URL+"/conferences/"+ confIdForRequest);
		
		// Include header
		request.setHeader("Content-Type", "application/json");
		request.setHeader("Accept", "application/json");
		request.setHeader("Authorization", "Token "+extras.getString(Constants.ACCESS_TOKEN));
		
		result = executeAndResult(client, request);
		
		Log.d("Conferences data", result);
		if(result != null){
			if(result.startsWith("{\"code\"")){
				Log.d("Error", "Something wrong happened");
				//TODO Hacer algo. La respuesta no es la esperada
			} else {
			//Documents is an array inside the JSONObject 
			//and inside there will be the fields title, description, type, data
				try {
					Uri url = Uri.parse("content://" + Constants.PROVIDER_NAME + "/documents");
					//Delete previous values
					mContentResolver.delete(url, "1", null);
					//Save data to provider
					ContentValues values;
					values = new ContentValues();
					JSONObject jsonData = new JSONObject(result);
					values.put(Constants.CONF_UUID, jsonData.getString("id"));
					JSONArray jsonDocs = jsonData.getJSONArray("documents");
					if(jsonDocs.length()!=0){
						for(int i=0; i<jsonDocs.length();i++){
							JSONObject conf = jsonDocs.getJSONObject(i);
							values.put(Constants.DOC_TITLE, conf.getString(Constants.DOC_TITLE));
							values.put(Constants.DOC_DESCRIPTION, conf.getString(Constants.DOC_DESCRIPTION));
							values.put(Constants.DOC_TYPE, conf.getString(Constants.DOC_TYPE));
							values.put(Constants.DOC_DATA, conf.getString(Constants.DOC_DATA));
							mContentResolver.insert(url, values);
						}
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private String executeAndResult(HttpClient client, HttpGet request){
		String res = "";
		try{
			//Execute
			HttpResponse response = client.execute(request);
			
			// Response as Inputstream and convert to String
			InputStream inputStream = response.getEntity().getContent();
			if(inputStream != null){
				res = Data.inputStreamToString(inputStream);
			}
		} catch(IOException e){
			e.printStackTrace();
		}
		
		return res;
	}

}
