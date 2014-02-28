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

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.util.Log;

public class Register {
	
	private static final String BASE_URL = "http://myconf-api-dev.herokuapp.com/auth/signup";
	private String email, password;
	private Context context;
	
	private String authToken;
	
	public Register(Context context, String email, String password){
		this.context = context;
		this.email = email;
		this.password = password;
	}
	
	public String userRegisterAndLogin() throws Exception{
		String result = "";
		try{
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(BASE_URL);
			
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
			e.printStackTrace();
		}
		
		//RESULT
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
				
			} catch(JSONException e){
				e.printStackTrace();
				throw new Exception("Email duplicate");
			}
		} else {
			throw new Exception("Register error");
		}
		// Register is ok. Now Login time
		Login login = new Login(context, email, password);
		try{
			authToken = login.userLogin();
		} catch(Exception e){
			throw new Exception("Login after registration error");
		}
		
		return authToken;
	}
}
