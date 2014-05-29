package es.ucm.myconference;

import com.actionbarsherlock.view.Window;

import es.ucm.myconference.accountmanager.LoginActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends MyConferenceActivity {

	private final int SPLASH_DISPLAY_LENGHT = 3000;
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);

        /* New Handler to start the LoginActivity 
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the LoginActivity. */
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, SPLASH_DISPLAY_LENGHT);
    }
}
