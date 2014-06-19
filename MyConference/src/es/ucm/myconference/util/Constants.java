package es.ucm.myconference.util;

import android.net.Uri;

public class Constants {

	public static final String APP_ID = "514ab570-72e5-4512-9723-f496da08e13a";
	public static final String CONFS_URL = "http://myconf-api-dev.herokuapp.com";
	
	public static final String USER_ID = "id";
	public static final String USER_URI = "uri";
	public static final String USER_UUID = "uuid";
	public static final String USER_NAME = "user_name";
	public static final String USER_PASS = "USER_PASS";
	
	public final static String ACCESS_TOKEN = "access_token";
	public final static String ACCESS_TOKEN_EXPIRES = "access_token_expires";
	public final static String REFRESH_TOKEN = "refresh_token";
	public final static String REFRESH_TOKEN_EXPIRES = "refresh_token_expires";
	
	public final static String LOGOUT = "logout";
	public final static String FIRST_TIME = "first_time";
	
	public final static String CONF_NAME = "name";
	public final static String CONF_DESCRP = "description";
	public final static String CONF_UUID = "conf_uuid";
	
	//AccountManager attributes
	// The authority for the sync adapter's content provider
    public static final String AUTHORITY = "es.ucm.myconference";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "es.ucm.myconference";
    // The account name
    public static final String ACCOUNT_NAME = "myconferenceaccount";
    public static final String AUTHTOKEN_TYPE = "es.ucm.myconference";
    
    public static final String SYNC_FINISHED = "sync_finished";
    public static final String ERROR_MSG = "ERROR_MSG";
    public static final String AUTH_ERROR = "authentication-error";
    
    //Provider
    public static String DB_NAME = "myconference.db";
    public static int DB_VERSION = 8;
    
    //Database
    public static final String PROVIDER_NAME = "es.ucm.myconference";
    public static final Uri CONTENT_URI_CONFS = Uri.parse("content://" + PROVIDER_NAME + "/conferences");
    public static final String _ID = "_id";
    public static final String DATABASE_TABLE_CONFS = "conferences";
    public static final int CONFS = 1;
    public static final int CONFS_ID = 2;
    
    public static final String DATABASE_TABLE_DOCS = "documents";
    public static final Uri CONTENT_URI_DOCS = Uri.parse("content://" + PROVIDER_NAME + "/documents");
    public static final String DOC_TITLE = "title";
    public static final String DOC_DESCRIPTION = "description";
    public static final String DOC_TYPE = "type";
    public static final String DOC_DATA = "data";
    public static final int DOCS = 3;
    public static final int DOCS_ID = 4;
    
    public static final String DATABASE_TABLE_VENUES = "venues";
    public static final Uri CONTENT_URI_VENUES = Uri.parse("content://" + PROVIDER_NAME + "/venues");
    public static final String VENUE_NAME = "name";
    public static final String VENUE_LATITUDE = "lat";
    public static final String VENUE_LONGITUDE = "lng";
    public static final String VENUE_DETAILS = "details";
    public static final int VENUES = 5;
    public static final int VENUES_ID = 6;
    
    public static final String DATABASE_TABLE_ANNOUNCEMENTS = "announcements";
    public static final Uri CONTENT_URI_ANNOUNCEMENTS = Uri.parse("content://" + PROVIDER_NAME + "/announcements");
    public static final String ANNOUNCEMENT_TITLE = "title";
    public static final String ANNOUNCEMENT_BODY = "body";
    public static final String ANNOUNCEMENT_DATE = "date";
    public static final int ANNOUNCEMENTS = 7;
    public static final int ANNOUNCEMENTS_ID = 8;
    
    public static final String DATABASE_TABLE_KEYNOTE = "keynotes";
    public static final Uri CONTENT_URI_KEYNOTES = Uri.parse("content://" + PROVIDER_NAME + "/keynotes");
    public static final String KEYNOTES_NAME = "name";
    public static final String KEYNOTES_CHARGE = "charge";
    public static final String KEYNOTES_ORIGIN = "origin";
    public static final String KEYNOTES_DESCRIPTION = "description";
    public static final String KEYNOTES_PHOTO = "picture_url";
    public static final String KEYNOTE_LINKS = "links";
    public static final int KEYNOTES = 9;
    public static final int KEYNOTES_ID = 10;
    
    public static final String DATABASE_TABLE_COMMITTEE = "committee";
    public static final Uri CONTENT_URI_COMMITTEE = Uri.parse("content://" + PROVIDER_NAME + "/committee");
    public static final String COMMITTEE_NAME = "name";
    public static final String COMMITTEE_ORIGIN = "origin";
    public static final String COMMITTEE_DETAILS = "details";
    public static final String COMMITTEE_GROUP = "groups";
    public static final int COMMITTEE = 11;
    public static final int COMMITTEE_ID = 12;
    
    public static final String DATABASE_TABLE_AGENDA = "agenda";
    public static final Uri CONTENT_URI_AGENDA = Uri.parse("content://" + PROVIDER_NAME + "/agenda");
    public static final String AGENDA_TITLE = "title";
    public static final String AGENDA_DESCRIPTION = "description";
    public static final String AGENDA_DATE = "date";
    public static final int AGENDA = 13;
    public static final int AGENDA_ID = 14;
}
