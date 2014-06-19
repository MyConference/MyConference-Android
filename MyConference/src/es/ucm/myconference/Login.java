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

public class Login {

	private static final String BASE_URL = Constants.API_URL + "/auth";
	private static final String APP_ID = "514ab570-72e5-4512-9723-f496da08e13a";
	private Context context;
	private String email;
	private String pass;
	private String refreshToken;
	private String device_id;
	
	private String authToken;
	
	/**
	 * Normal login.
	 */
	public Login(Context context, String email, String pass){
		this.context = context;
		this.email = email;
		this.pass = pass;
		device_id = Secure.getString(this.context.getContentResolver(), Secure.ANDROID_ID);
	}
	/**
	 * Login with refresh token
	 */
	public Login(Context context, String refreshToken){
		this.context = context;
		this.refreshToken = refreshToken;
		device_id = Secure.getString(this.context.getContentResolver(), Secure.ANDROID_ID);
	}
	
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
			jsonObject.accumulate("device_id", device_id);
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
			Log.d("tokens", result);
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
				editor.putString(Constants.USER_NAME, email);
				editor.commit();
				
				
				authToken = jsonObj.getString(Constants.ACCESS_TOKEN);
				
			} catch (JSONException e) {
				e.printStackTrace();
				throw new Exception("Invalid email or password");
			}
		} else {
			throw new Exception("Result null");
		}
		
		return authToken;
	}
	
	public String loginWithRefresh(){
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
			jsonObject.accumulate("device_id", device_id);
			JSONObject jsonCredentials = new JSONObject();
			jsonCredentials.accumulate("type", "refresh");
			jsonCredentials.accumulate("refresh_token", refreshToken);
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
			Log.d("tokens", result);
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
				editor.putString(Constants.USER_NAME, email);
				editor.commit();
				
				
				authToken = jsonObj.getString(Constants.ACCESS_TOKEN);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return authToken;
	}
}
