package es.ucm.myconference.util;

import android.net.Uri;

public class Constants {

	public static final String APP_ID = "514ab570-72e5-4512-9723-f496da08e13a";
	public static final String USER_ID = "id";
	public static final String USER_URI = "uri";
	
	public final static String ACCESS_TOKEN = "access_token";
	public final static String ACCESS_TOKEN_EXPIRES = "access_token_expires";
	public final static String REFRESH_TOKEN = "refresh_token";
	public final static String REFRESH_TOKEN_EXPIRES = "refresh_token_expires";
	
	public final static String LOGOUT = "logout";
	
	public final static String CONF_NAME = "name";
	public final static String CONF_DESCRP = "description";
	
	//AccountManager attributes
	// The authority for the sync adapter's content provider
    public static final String AUTHORITY = "es.ucm.myconference.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "es.ucm.myconference";
    // The account name
    public static final String ACCOUNT = "testaccount";
    
    //Provider
    public static final String PROVIDER_NAME = "es.ucm.myconference";
    public static final Uri CONTENT_URI_CONFS = Uri.parse("content://" + PROVIDER_NAME + "/conferences");
    public static final String _ID = "_id";
    public static final String DATABASE_TABLE_CONFS = "conferences";
    public static final int CONFS = 1;
    public static final int CONFS_ID = 2;
    public static String DB_NAME = "myconference.db";
    public static int DB_VERSION = 1;
}
