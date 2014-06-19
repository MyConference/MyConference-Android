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
import android.content.Intent;
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
	private Context context;
	private String confIdForRequest = "";
	private boolean errorBoolean = false;
	private final static String CONFS_URL = Constants.API_URL;
	// Set up the sync adapter
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		
		mContentResolver = context.getContentResolver();
		this.context = context;
	}

	@SuppressLint("NewApi")
	public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
	        super(context, autoInitialize, allowParallelSyncs);
	        
	        mContentResolver = context.getContentResolver(); 
	        this.context = context;
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
		errorBoolean = false;
		//GET users/<uuid>/conferences
		Log.d("sync", "GET users/"+extras.getString(Constants.USER_UUID)+"/"+Constants.DATABASE_TABLE_CONFS);
		String result="";
		
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(CONFS_URL+"/users/"+extras.getString(Constants.USER_UUID)+"/"+Constants.DATABASE_TABLE_CONFS);
		
		// Include header
		request.setHeader("Content-Type", "application/json");
		request.setHeader("Accept", "application/json");
		request.setHeader("Authorization", "Token "+extras.getString(Constants.ACCESS_TOKEN));

		result = executeAndResult(client, request);
		
		Log.d("Conferences", result);
		if(result!=null){
			if(result.startsWith("{")){
				Log.d("Error", "Something wrong happened");
				//res = {"code":"invalid_access", "message":""}
				//Hay que volver a loguear enviando el refresh_token y devuelve todo lo del login
				errorBoolean = true;
			} else{
			// Get conferences names into an Arraylist
				try {
					JSONArray jsonConfs = new JSONArray(result);
					if(jsonConfs.length()!=0){
						Uri url = Uri.parse("content://"+Constants.PROVIDER_NAME+"/"+Constants.DATABASE_TABLE_CONFS);
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
		Log.d("sync", "GET /"+Constants.DATABASE_TABLE_CONFS+"/" + confIdForRequest);
		
		client = new DefaultHttpClient();
		request = new HttpGet(CONFS_URL+"/"+Constants.DATABASE_TABLE_CONFS+"/"+ confIdForRequest);
		
		// Include header
		request.setHeader("Content-Type", "application/json");
		request.setHeader("Accept", "application/json");
		request.setHeader("Authorization", "Token "+extras.getString(Constants.ACCESS_TOKEN));
		
		result = executeAndResult(client, request);
		
		Log.d("Conferences data", result);
		if(result != null){
			if(result.startsWith("{\"code\"")){
				Log.d("Error", "Something wrong happened");
				//res = {"code":"invalid_access", "message":""}
				//Hay que volver a loguear enviando el refresh_token y devuelve todo lo del login
				errorBoolean = true;
				
			} else {
				try {
					//Documents is an array inside the JSONObject 
					//and inside there will be the fields title, description, type, data
					
					Uri url = Uri.parse("content://"+Constants.PROVIDER_NAME+"/"+Constants.DATABASE_TABLE_DOCS);
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
					
					//Venues is an array
					url = Uri.parse("content://"+Constants.PROVIDER_NAME+"/"+Constants.DATABASE_TABLE_VENUES);
					//Delete previous values
					mContentResolver.delete(url, "1", null);
					//Save data to provider
					values = new ContentValues();
					values.put(Constants.CONF_UUID, jsonData.getString("id"));
					
					JSONArray jsonVenues = jsonData.getJSONArray("venues");
					if(jsonVenues.length()!=0){
						for(int i=0;i<jsonVenues.length();i++){
							JSONObject venue = jsonVenues.getJSONObject(i);
							values.put(Constants.VENUE_NAME, venue.getString(Constants.VENUE_NAME));
							JSONObject location = venue.getJSONObject("location");
							values.put(Constants.VENUE_LATITUDE, location.getDouble(Constants.VENUE_LATITUDE));
							values.put(Constants.VENUE_LONGITUDE, location.getDouble(Constants.VENUE_LONGITUDE));
							values.put(Constants.VENUE_DETAILS, venue.getString(Constants.VENUE_DETAILS));
							mContentResolver.insert(url, values);
						}
					}
					
					//Announcements is an array
					url = Uri.parse("content://"+Constants.PROVIDER_NAME+"/"+Constants.DATABASE_TABLE_ANNOUNCEMENTS);
					//Delete previous values
					mContentResolver.delete(url, "1", null);
					//Save data to provider
					values = new ContentValues();
					values.put(Constants.CONF_UUID, jsonData.getString("id"));
					
					JSONArray jsonAnnouncements = jsonData.getJSONArray("announcements");
					if(jsonAnnouncements.length()!=0){
						for(int i=0;i<jsonAnnouncements.length();i++){
							JSONObject announcement = jsonAnnouncements.getJSONObject(i);
							values.put(Constants.ANNOUNCEMENT_TITLE, announcement.getString(Constants.ANNOUNCEMENT_TITLE));
							values.put(Constants.ANNOUNCEMENT_BODY, announcement.getString(Constants.ANNOUNCEMENT_BODY));
							values.put(Constants.ANNOUNCEMENT_DATE, announcement.getString(Constants.ANNOUNCEMENT_DATE));
							mContentResolver.insert(url, values);
						}
					}
					
					//Keynote speakers is an array
					url = Uri.parse("content://"+Constants.PROVIDER_NAME+"/"+Constants.DATABASE_TABLE_KEYNOTE);
					//Delete previous values
					mContentResolver.delete(url, "1", null);
					//Save data to provider
					values = new ContentValues();
					values.put(Constants.CONF_UUID, jsonData.getString("id"));
					
					JSONArray jsonSpeakers = jsonData.getJSONArray("speakers");
					if(jsonSpeakers.length()!=0){
						for(int i=0; i<jsonSpeakers.length(); i++){
							JSONObject speaker = jsonSpeakers.getJSONObject(i);
							values.put(Constants.KEYNOTES_NAME, speaker.getString(Constants.KEYNOTES_NAME));
							values.put(Constants.KEYNOTES_CHARGE, speaker.getString(Constants.KEYNOTES_CHARGE));
							values.put(Constants.KEYNOTES_ORIGIN, speaker.getString(Constants.KEYNOTES_ORIGIN));
							values.put(Constants.KEYNOTES_DESCRIPTION, speaker.getString(Constants.KEYNOTES_DESCRIPTION));
							values.put(Constants.KEYNOTES_PHOTO, speaker.getString(Constants.KEYNOTES_PHOTO));
							//TODO Links nada por ahora
							mContentResolver.insert(url, values);
						}
					}
					
					//Committee is an array
					url = Uri.parse("content://"+Constants.PROVIDER_NAME+"/"+Constants.DATABASE_TABLE_COMMITTEE);
					//Delete previous values
					mContentResolver.delete(url, "1", null);
					//Save data to provider
					values = new ContentValues();
					values.put(Constants.CONF_UUID, jsonData.getString("id"));
					
					JSONArray jsonCommittee = jsonData.getJSONArray("organizers");
					if(jsonCommittee.length()!=0){
						for(int i=0;i<jsonCommittee.length(); i++){
							JSONObject organizer = jsonCommittee.getJSONObject(i);
							values.put(Constants.COMMITTEE_NAME, organizer.getString(Constants.COMMITTEE_NAME));
							values.put(Constants.COMMITTEE_ORIGIN, organizer.getString(Constants.COMMITTEE_ORIGIN));
							values.put(Constants.COMMITTEE_DETAILS, organizer.getString(Constants.COMMITTEE_DETAILS));
							values.put(Constants.COMMITTEE_GROUP, organizer.getString("group"));
							mContentResolver.insert(url, values);
						}
					}
					
					//Agenda is an array
					url = Uri.parse("content://"+Constants.PROVIDER_NAME+"/"+Constants.DATABASE_TABLE_AGENDA);
					//Delete previous values
					mContentResolver.delete(url, "1", null);
					//Save data to provider
					values = new ContentValues();
					values.put(Constants.CONF_UUID, jsonData.getString("id"));
					
					JSONArray jsonAgenda = jsonData.getJSONArray("agendaEvents");
					if(jsonAgenda.length()!=0){
						for(int i=0;i<jsonAgenda.length(); i++){
							JSONObject event = jsonAgenda.getJSONObject(i);
							values.put(Constants.AGENDA_TITLE, event.getString(Constants.AGENDA_TITLE));
							values.put(Constants.AGENDA_DESCRIPTION, event.getString(Constants.AGENDA_DESCRIPTION));
							values.put(Constants.AGENDA_DATE, event.getString(Constants.AGENDA_DATE));
							mContentResolver.insert(url, values);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		//Broadcast that sync has finished
		Intent intent = new Intent(Constants.SYNC_FINISHED);
		intent.putExtra(Constants.AUTH_ERROR, errorBoolean);
		context.sendBroadcast(intent);
		
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
