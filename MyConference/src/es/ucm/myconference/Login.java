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

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.widget.Toast;

public class Login {

	private static final String BASE_URL = "http://raspi.darkhogg.es:4321/v0.1/auth";
	private static final String APP_ID = "df3ae937-c8d6-40f8-8145-c8747c3ca56c";
	private Context context;
	private String email;
	private String pass;
	
	public Login(Context context, String email, String pass){
		this.context = context;
		this.email = email;
		this.pass = pass;
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
		protected void onPostExecute(String result) {
			if(result!=null)
				Toast.makeText(context, result, Toast.LENGTH_LONG).show();
			else Toast.makeText(context, "No data", Toast.LENGTH_LONG).show();
			// Save access_token, access_token_expires, refresh_token and refresh_token_expires
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
