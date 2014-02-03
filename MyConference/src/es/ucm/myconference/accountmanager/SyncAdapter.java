package es.ucm.myconference.accountmanager;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {
	
	// Define a variable to contain a content resolver instance
	ContentResolver mContentResolver;

	
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
	}

}
