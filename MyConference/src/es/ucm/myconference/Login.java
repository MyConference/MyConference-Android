package es.ucm.myconference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

public class Login {

	private static final String BASE_URL = "http://myconf-api-dev.herokuapp.com/auth";
	private static final String APP_ID = "514ab570-72e5-4512-9723-f496da08e13a";
	private Context context;
	private String email;
	private String pass;
	private Activity activity;
	private final static String ACCESS_TOKEN = "access_token";
	private final static String ACCESS_TOKEN_EXPIRES = "access_token_expires";
	private final static String REFRESH_TOKEN = "refresh_token";
	private final static String REFRESH_TOKEN_EXPIRES = "refresh_token_expires";
	private final static String USER_ID = "id";
	private final static String USER_URI = "uri";
	
	public Login(Context context, String email, String pass, Activity activity){
		this.context = context;
		this.email = email;
		this.pass = pass;
		this.activity = activity;
	}
	

	public void login() {
		// Request
		new LoginAsyncTask().execute(BASE_URL);
	}
	
	private class LoginAsyncTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			try{
				return post(params[0]);
			} catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPreExecute() {
			activity.setProgressBarIndeterminateVisibility(true);
		}
		
		@Override
		protected void onPostExecute(String result) {
			activity.setProgressBarIndeterminateVisibility(false);
			if(result!=null){
				// Save access_token, access_token_expires, refresh_token and refresh_token_expires
				try {
					JSONObject jsonObj = new JSONObject(result);
					SharedPreferences preferences = context.getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = preferences.edit();
					
					editor.putString(ACCESS_TOKEN, jsonObj.getString("access_token"));
					editor.putString(REFRESH_TOKEN, jsonObj.getString("refresh_token"));
					editor.putString(ACCESS_TOKEN_EXPIRES, jsonObj.getString("access_token_expires"));
					editor.putString(REFRESH_TOKEN_EXPIRES, jsonObj.getString("refresh_token_expires"));
					JSONObject user = jsonObj.getJSONObject("user");
					editor.putString(USER_ID, user.getString("id"));
					editor.putString(USER_URI, user.getString("uri"));
					editor.commit();
					
					Log.d("tokens", result);
					
					// Redirect to NavigationDrawer activity
					Intent i = new Intent(context, NavigationDrawerActivity.class);
					activity.startActivity(i);
					
				} catch (JSONException e) {
					Toast.makeText(context, R.string.home_login_error, Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
			else Toast.makeText(context, R.string.home_login_error, Toast.LENGTH_LONG).show();
		}
		
		private String post(String url) throws ClientProtocolException, IOException{
			String result = "";
			try{
				HttpClient client = new DefaultHttpClient();
				HttpPost request = new HttpPost(url);
				
				// Include header
				request.setHeader("Content-Type", "application/json");
				request.setHeader("Accept", "application/json");
				
				// Build JSON Object
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("application_id", APP_ID);
				jsonObject.accumulate("device_id", Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
				JSONObject jsonCredentials = new JSONObject();
				jsonCredentials.accumulate("type", "password");
				jsonCredentials.accumulate("email", email);
				jsonCredentials.accumulate("password", pass);
				jsonObject.accumulate("credentials", jsonCredentials);
				
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
			} catch(JSONException e){
				throw new RuntimeException(e);
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
