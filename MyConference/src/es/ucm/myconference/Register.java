package es.ucm.myconference;

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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

public class Register {
	
	private static final String BASE_URL = "http://myconf-api-dev.herokuapp.com/auth/signup";
	private String email, password;
	private Context context;
	private Activity activity;
	
	public Register(Context context, String email, String password, Activity activity){
		this.context = context;
		this.email = email;
		this.password = password;
		this.activity = activity;
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
		protected void onPreExecute() {
			activity.setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected void onPostExecute(String result) {
			activity.setProgressBarIndeterminateVisibility(false);
			if(result!=null){				
				// Save user (id and uri) in SharedPreferences
				try{
					JSONObject jsonObj = new JSONObject(result).getJSONObject("user");
					SharedPreferences preference = context.getSharedPreferences("USERPREFS", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = preference.edit();
					
					editor.putString(Constants.USER_ID,jsonObj.getString(Constants.USER_ID));
					editor.putString(Constants.USER_URI,jsonObj.getString(Constants.USER_URI));
					
					editor.commit();
					Log.d("user", result);
					Toast.makeText(context, R.string.home_register_ok, Toast.LENGTH_LONG).show();
					
				} catch(JSONException e){
					Toast.makeText(context, R.string.home_register_error, Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			} else Toast.makeText(context, R.string.home_register_error, Toast.LENGTH_LONG).show();
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
				jsonObject.accumulate("application_id", Constants.APP_ID);
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
					result = Data.inputStreamToString(inputStream);
				}
				
			} catch(Exception e){
				Toast.makeText(context, R.string.home_register_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			return result;
		}
	}

}
