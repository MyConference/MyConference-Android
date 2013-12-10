package es.ucm.myconference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.widget.Toast;

public class Register {
	
	private static final String BASE_URL = "http://raspi.darkhogg.es:4321/v0.1/auth/signup";
	private static final String APP_ID = "df3ae937-c8d6-40f8-8145-c8747c3ca56c";
	private static final String USER_PREF = "UserID";
	private String email, password;
	private Context context;
	
	public Register(Context context, String email, String password){
		this.context = context;
		this.email = email;
		this.password = password;
	}
	
	public void register(){
		// Request
		new RegisterAsyncTask().execute(BASE_URL);
	}
	
	private class RegisterAsyncTask extends AsyncTask<String, Void, String>{

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
		protected void onPostExecute(String result) {
			if(result!=null){
				Toast.makeText(context, result, Toast.LENGTH_LONG).show();
				
				// Save user_id in SharedPreferences
				try{
					JSONArray arr = new JSONArray(result);
					JSONObject info = arr.getJSONObject(0);
					SharedPreferences preference = context.getSharedPreferences("MYPREFS", 0);
					SharedPreferences.Editor editor = preference.edit();
					editor.putString(USER_PREF,info.getString("user_id"));
					editor.commit();
					
				} catch(JSONException e){
					throw new RuntimeException(e);
				}
			} else Toast.makeText(context, "No data", Toast.LENGTH_LONG).show();
		}
		
		private String post(String url){
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
				JSONObject jsonUserData = new JSONObject();
				jsonUserData.accumulate("email", email);
				jsonUserData.accumulate("password", password);
				jsonObject.accumulate("user_data", jsonUserData);
				
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
				
			} catch(Exception e){
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
