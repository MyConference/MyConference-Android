package es.ucm.myconference;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import es.ucm.myconference.util.Constants;
import es.ucm.myconference.util.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

public class Login {

	private static final String BASE_URL = "http://myconf-api-dev.herokuapp.com/auth";
	private static final String APP_ID = "514ab570-72e5-4512-9723-f496da08e13a";
	private Context context;
	private String email;
	private String pass;
	//private Activity activity;
	
	private String authToken;
	
	public Login(Context context, String email, String pass){// Activity activity){
		this.context = context;
		this.email = email;
		this.pass = pass;
		//this.activity = activity;
	}
	

	/*public void login() {
		// Request
		new LoginAsyncTask().execute(BASE_URL);
	}*/
	
	public String userLogin() throws Exception{
		String result = "";
		try{
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(BASE_URL);
			
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
				result = Data.inputStreamToString(inputStream);
			}
		} catch(JSONException e){
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//RESULT
		if(result!=null){
			// Save access_token, access_token_expires, refresh_token and refresh_token_expires
			try {
				JSONObject jsonObj = new JSONObject(result);
				SharedPreferences preferences = context.getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				
				editor.putString(Constants.ACCESS_TOKEN, jsonObj.getString(Constants.ACCESS_TOKEN));
				editor.putString(Constants.REFRESH_TOKEN, jsonObj.getString(Constants.REFRESH_TOKEN));
				editor.putString(Constants.ACCESS_TOKEN_EXPIRES, jsonObj.getString(Constants.ACCESS_TOKEN_EXPIRES));
				editor.putString(Constants.REFRESH_TOKEN_EXPIRES, jsonObj.getString(Constants.REFRESH_TOKEN_EXPIRES));
				JSONObject user = jsonObj.getJSONObject("user");
				editor.putString(Constants.USER_ID, user.getString(Constants.USER_ID));
				editor.putString(Constants.USER_URI, user.getString(Constants.USER_URI));
				editor.commit();
				
				
				authToken = jsonObj.getString(Constants.ACCESS_TOKEN);
				Log.d("tokens", result);
				
				// Redirect to NavigationDrawer activity
				/*Intent i = new Intent(context, NavigationDrawerActivity.class);
				activity.startActivity(i);
				activity.finish();*/
				
			} catch (JSONException e) {
				Toast.makeText(context, R.string.home_login_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
				throw new Exception("Invalid email or password");
			}
		} else {
			Toast.makeText(context, R.string.home_login_error, Toast.LENGTH_LONG).show();
			throw new Exception("Result null");
		}
		
		return authToken;
	}
	
	/*private class LoginAsyncTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			try{
				return userLogin(params[0]);
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
			//activity.setProgressBarIndeterminateVisibility(false);
			if(result!=null){
				// Save access_token, access_token_expires, refresh_token and refresh_token_expires
				try {
					JSONObject jsonObj = new JSONObject(result);
					SharedPreferences preferences = context.getSharedPreferences("ACCESSPREFS", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = preferences.edit();
					
					editor.putString(Constants.ACCESS_TOKEN, jsonObj.getString(Constants.ACCESS_TOKEN));
					editor.putString(Constants.REFRESH_TOKEN, jsonObj.getString(Constants.REFRESH_TOKEN));
					editor.putString(Constants.ACCESS_TOKEN_EXPIRES, jsonObj.getString(Constants.ACCESS_TOKEN_EXPIRES));
					editor.putString(Constants.REFRESH_TOKEN_EXPIRES, jsonObj.getString(Constants.REFRESH_TOKEN_EXPIRES));
					JSONObject user = jsonObj.getJSONObject("user");
					editor.putString(Constants.USER_ID, user.getString(Constants.USER_ID));
					editor.putString(Constants.USER_URI, user.getString(Constants.USER_URI));
					editor.commit();
					
					Log.d("tokens", result);
					
					// Redirect to NavigationDrawer activity
					Intent i = new Intent(context, NavigationDrawerActivity.class);
					activity.startActivity(i);
					activity.finish();
					
				} catch (JSONException e) {
					Toast.makeText(context, R.string.home_login_error, Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
			else Toast.makeText(context, R.string.home_login_error, Toast.LENGTH_LONG).show();
		}
	}*/
}
