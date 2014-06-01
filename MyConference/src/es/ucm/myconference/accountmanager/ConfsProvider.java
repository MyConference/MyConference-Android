package es.ucm.myconference.accountmanager;

import es.ucm.myconference.util.Constants;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class ConfsProvider extends ContentProvider {

	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
    	uriMatcher.addURI(Constants.PROVIDER_NAME, "conferences", Constants.CONFS);
    	uriMatcher.addURI(Constants.PROVIDER_NAME, "conferences/#", Constants.CONFS_ID);
    	uriMatcher.addURI(Constants.PROVIDER_NAME, "documents", Constants.DOCS);
    	uriMatcher.addURI(Constants.PROVIDER_NAME, "documents/#", Constants.DOCS_ID);
    	uriMatcher.addURI(Constants.PROVIDER_NAME, "venues", Constants.VENUES);
    	uriMatcher.addURI(Constants.PROVIDER_NAME, "venues/#", Constants.VENUES_ID);
    	uriMatcher.addURI(Constants.PROVIDER_NAME, "announcements", Constants.ANNOUNCEMENTS);
    	uriMatcher.addURI(Constants.PROVIDER_NAME, "announcements/#", Constants.ANNOUNCEMENTS_ID);
    	uriMatcher.addURI(Constants.PROVIDER_NAME, "keynotes", Constants.KEYNOTES);
    	uriMatcher.addURI(Constants.PROVIDER_NAME, "keynotes/#", Constants.KEYNOTES_ID);
    	uriMatcher.addURI(Constants.PROVIDER_NAME, "committee", Constants.COMMITTEE);
    	uriMatcher.addURI(Constants.PROVIDER_NAME, "committee/#", Constants.COMMITTEE_ID);
    	uriMatcher.addURI(Constants.PROVIDER_NAME, "agenda", Constants.AGENDA);
    	uriMatcher.addURI(Constants.PROVIDER_NAME, "agenda/#", Constants.AGENDA_ID);
    }
    private SQLiteDatabase confsDB;
	
    @Override
    public boolean onCreate() {
    	Log.d("Provider", "Creating database");
    	Context context = getContext();
    	SqlHelper dbHelper = new SqlHelper(context); //Creates all tables in database
    	confsDB = dbHelper.getWritableDatabase();
        return (confsDB == null) ? false : true;
    }
    @Override
	public String getType(Uri uri) {
    	switch(uriMatcher.match(uri)){
    	case Constants.CONFS:
    		return "vnd.android.cursor.dir/vnd.ucm.myconference.conferences";    	
    	case Constants.CONFS_ID:
    		return "vnd.android.cursor.item/vnd.ucm.myconference.conferences";    		
    	case Constants.DOCS:
    		return "vnd.android.cursor.dir/vnd.ucm.myconference.documents";    	
    	case Constants.DOCS_ID:
    		return "vnd.android.cursor.item/vnd.ucm.myconference.documents";     		
    	case Constants.VENUES:
    		return "vnd.android.cursor.dir/vnd.ucm.myconference.venues";    	
    	case Constants.VENUES_ID:
    		return "vnd.android.cursor.item/vnd.ucm.myconference.venues";
    	case Constants.ANNOUNCEMENTS:
    		return "vnd.android.cursor.dir/vnd.ucm.myconference.announcements";    	
    	case Constants.ANNOUNCEMENTS_ID:
    		return "vnd.android.cursor.item/vnd.ucm.myconference.announcements";
    	case Constants.KEYNOTES:
    		return "vnd.android.cursor.dir/vnd.ucm.myconference.keynotes";    	
    	case Constants.KEYNOTES_ID:
    		return "vnd.android.cursor.item/vnd.ucm.myconference.keynotes";
    	case Constants.COMMITTEE:
    		return "vnd.android.cursor.dir/vnd.ucm.myconference.committee";    	
    	case Constants.COMMITTEE_ID:
    		return "vnd.android.cursor.item/vnd.ucm.myconference.committee";
    	case Constants.AGENDA:
    		return "vnd.android.cursor.dir/vnd.ucm.myconference.agenda";    	
    	case Constants.AGENDA_ID:
    		return "vnd.android.cursor.item/vnd.ucm.myconference.agenda";
    	default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
    	}
	}
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
    					String sortOrder) {
    	SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
    	
    	switch(uriMatcher.match(uri)){
    	case Constants.CONFS:
        	sqlBuilder.setTables(Constants.DATABASE_TABLE_CONFS);
        	if(sortOrder == null || sortOrder == "") sortOrder = Constants._ID;
    		break;
    	
    	case Constants.CONFS_ID:
        	sqlBuilder.setTables(Constants.DATABASE_TABLE_CONFS);
			sqlBuilder.appendWhere(Constants._ID + " = " + uri.getPathSegments().get(1));
	    	if(sortOrder == null || sortOrder == "") sortOrder = Constants._ID;
    		break;
    	
    	case Constants.DOCS:
    		sqlBuilder.setTables(Constants.DATABASE_TABLE_DOCS);
        	if(sortOrder == null || sortOrder == "") sortOrder = Constants._ID;
    		break;
    	
    	case Constants.DOCS_ID:
    		sqlBuilder.setTables(Constants.DATABASE_TABLE_DOCS);
			sqlBuilder.appendWhere(Constants._ID + " = " + uri.getPathSegments().get(1));
	    	if(sortOrder == null || sortOrder == "") sortOrder = Constants._ID;
    		break;
    		
    	case Constants.VENUES:
    		sqlBuilder.setTables(Constants.DATABASE_TABLE_VENUES);
        	if(sortOrder == null || sortOrder == "") sortOrder = Constants._ID;
    		break;
    	
    	case Constants.VENUES_ID:
    		sqlBuilder.setTables(Constants.DATABASE_TABLE_VENUES);
			sqlBuilder.appendWhere(Constants._ID + " = " + uri.getPathSegments().get(1));
	    	if(sortOrder == null || sortOrder == "") sortOrder = Constants._ID;
    		break;
    		
    	case Constants.ANNOUNCEMENTS:
    		sqlBuilder.setTables(Constants.DATABASE_TABLE_ANNOUNCEMENTS);
        	if(sortOrder == null || sortOrder == "") sortOrder = Constants._ID;
    		break;
    	
    	case Constants.ANNOUNCEMENTS_ID:
    		sqlBuilder.setTables(Constants.DATABASE_TABLE_ANNOUNCEMENTS);
			sqlBuilder.appendWhere(Constants._ID + " = " + uri.getPathSegments().get(1));
	    	if(sortOrder == null || sortOrder == "") sortOrder = Constants._ID;
    		break;
    		
    	case Constants.KEYNOTES:
    		sqlBuilder.setTables(Constants.DATABASE_TABLE_KEYNOTE);
        	if(sortOrder == null || sortOrder == "") sortOrder = Constants._ID;
    		break;
    	
    	case Constants.KEYNOTES_ID:
    		sqlBuilder.setTables(Constants.DATABASE_TABLE_KEYNOTE);
			sqlBuilder.appendWhere(Constants._ID + " = " + uri.getPathSegments().get(1));
	    	if(sortOrder == null || sortOrder == "") sortOrder = Constants._ID;
    		break;
    		
    	case Constants.COMMITTEE:
    		sqlBuilder.setTables(Constants.DATABASE_TABLE_COMMITTEE);
        	if(sortOrder == null || sortOrder == "") sortOrder = Constants._ID;
    		break;
    	
    	case Constants.COMMITTEE_ID:
    		sqlBuilder.setTables(Constants.DATABASE_TABLE_COMMITTEE);
			sqlBuilder.appendWhere(Constants._ID + " = " + uri.getPathSegments().get(1));
	    	if(sortOrder == null || sortOrder == "") sortOrder = Constants._ID;
    		break;
    		
    	case Constants.AGENDA:
    		sqlBuilder.setTables(Constants.DATABASE_TABLE_AGENDA);
        	if(sortOrder == null || sortOrder == "") sortOrder = Constants._ID;
    		break;
    	
    	case Constants.AGENDA_ID:
    		sqlBuilder.setTables(Constants.DATABASE_TABLE_AGENDA);
			sqlBuilder.appendWhere(Constants._ID + " = " + uri.getPathSegments().get(1));
	    	if(sortOrder == null || sortOrder == "") sortOrder = Constants._ID;
    		break;
    		
    	default:
    		throw new IllegalArgumentException("Unknown URL " + uri);
    	}
    	
    	Cursor c = sqlBuilder.query(confsDB, projection, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
    }
    
    @Override
    public Uri insert(Uri uri, ContentValues values) {
    	Uri _uri = null;
    	long rowID = 0;
    	//Select between tables
    	switch(uriMatcher.match(uri)){
    	case Constants.CONFS:
    		rowID = confsDB.insert(Constants.DATABASE_TABLE_CONFS, "", values);
    		if(rowID >0){
    			_uri = ContentUris.withAppendedId(Constants.CONTENT_URI_CONFS, rowID);
    			getContext().getContentResolver().notifyChange(_uri, null);
    		}
    		break;
    		
    	case Constants.DOCS:
    		rowID = confsDB.insert(Constants.DATABASE_TABLE_DOCS, "", values);
    		if(rowID >0){
    			_uri = ContentUris.withAppendedId(Constants.CONTENT_URI_DOCS, rowID);
    			getContext().getContentResolver().notifyChange(_uri, null);
    		}
    		break;
    		
    	case Constants.VENUES:
    		rowID = confsDB.insert(Constants.DATABASE_TABLE_VENUES, "", values);
    		if(rowID >0){
    			_uri = ContentUris.withAppendedId(Constants.CONTENT_URI_VENUES, rowID);
    			getContext().getContentResolver().notifyChange(_uri, null);
    		}
    		break;
    		
    	case Constants.ANNOUNCEMENTS:
    		rowID = confsDB.insert(Constants.DATABASE_TABLE_ANNOUNCEMENTS, "", values);
    		if(rowID >0){
    			_uri = ContentUris.withAppendedId(Constants.CONTENT_URI_ANNOUNCEMENTS, rowID);
    			getContext().getContentResolver().notifyChange(_uri, null);
    		}
    		break;
    		
    	case Constants.KEYNOTES:
    		rowID = confsDB.insert(Constants.DATABASE_TABLE_KEYNOTE, "", values);
    		if(rowID >0){
    			_uri = ContentUris.withAppendedId(Constants.CONTENT_URI_KEYNOTES, rowID);
    			getContext().getContentResolver().notifyChange(_uri, null);
    		}
    		break;
    		
    	case Constants.COMMITTEE:
    		rowID = confsDB.insert(Constants.DATABASE_TABLE_COMMITTEE, "", values);
    		if(rowID >0){
    			_uri = ContentUris.withAppendedId(Constants.CONTENT_URI_COMMITTEE, rowID);
    			getContext().getContentResolver().notifyChange(_uri, null);
    		}
    		break;
    		
    	case Constants.AGENDA:
    		rowID = confsDB.insert(Constants.DATABASE_TABLE_AGENDA, "", values);
    		if(rowID >0){
    			_uri = ContentUris.withAppendedId(Constants.CONTENT_URI_AGENDA, rowID);
    			getContext().getContentResolver().notifyChange(_uri, null);
    		}
    		break;
    		
    	default: 
    		throw new SQLException("Failed to insert row into " + uri);
    	}
    	return _uri; 
    }
    
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
    	int rows = 0;
    	switch(uriMatcher.match(uri)){
    	case Constants.CONFS:
	    	rows = confsDB.delete(Constants.DATABASE_TABLE_CONFS, selection, selectionArgs);
	    	getContext().getContentResolver().notifyChange(uri, null);
	    	break;
	    	
    	case Constants.DOCS:
    		rows = confsDB.delete(Constants.DATABASE_TABLE_DOCS, selection, selectionArgs);
	    	getContext().getContentResolver().notifyChange(uri, null);
	    	break;
	    
    	case Constants.VENUES:
    		rows = confsDB.delete(Constants.DATABASE_TABLE_VENUES, selection, selectionArgs);
	    	getContext().getContentResolver().notifyChange(uri, null);
	    	break;
	    	
    	case Constants.ANNOUNCEMENTS:
    		rows = confsDB.delete(Constants.DATABASE_TABLE_ANNOUNCEMENTS, selection, selectionArgs);
	    	getContext().getContentResolver().notifyChange(uri, null);
	    	break;
	    	
    	case Constants.KEYNOTES:
    		rows = confsDB.delete(Constants.DATABASE_TABLE_KEYNOTE, selection, selectionArgs);
	    	getContext().getContentResolver().notifyChange(uri, null);
	    	break;
	    	
    	case Constants.COMMITTEE:
    		rows = confsDB.delete(Constants.DATABASE_TABLE_COMMITTEE, selection, selectionArgs);
	    	getContext().getContentResolver().notifyChange(uri, null);
	    	break;
	    	
    	case Constants.AGENDA:
    		rows = confsDB.delete(Constants.DATABASE_TABLE_AGENDA, selection, selectionArgs);
	    	getContext().getContentResolver().notifyChange(uri, null);
	    	break;
	    	
	    default: 
	    	throw new IllegalArgumentException("Unknown URL " + uri);
    	}
    	
    	return rows;
    }
    
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    	int rows = 0;
    	switch(uriMatcher.match(uri)){
    	case Constants.CONFS:
	    	rows = confsDB.update(Constants.DATABASE_TABLE_CONFS, values, selection, selectionArgs);
	    	getContext().getContentResolver().notifyChange(uri, null);
	    	break;
	    	
    	case Constants.DOCS:
    		rows = confsDB.update(Constants.DATABASE_TABLE_DOCS, values, selection, selectionArgs);
	    	getContext().getContentResolver().notifyChange(uri, null);
	    	break;
	    	
    	case Constants.VENUES:
    		rows = confsDB.update(Constants.DATABASE_TABLE_VENUES, values, selection, selectionArgs);
	    	getContext().getContentResolver().notifyChange(uri, null);
	    	break;
	    
    	case Constants.ANNOUNCEMENTS:
    		rows = confsDB.update(Constants.DATABASE_TABLE_ANNOUNCEMENTS, values, selection, selectionArgs);
	    	getContext().getContentResolver().notifyChange(uri, null);
	    	break;
	    
    	case Constants.KEYNOTES:
    		rows = confsDB.update(Constants.DATABASE_TABLE_KEYNOTE, values, selection, selectionArgs);
	    	getContext().getContentResolver().notifyChange(uri, null);
	    	break;
	    	
    	case Constants.COMMITTEE:
    		rows = confsDB.update(Constants.DATABASE_TABLE_COMMITTEE, values, selection, selectionArgs);
	    	getContext().getContentResolver().notifyChange(uri, null);
	    	break;
	    	
    	case Constants.AGENDA:
    		rows = confsDB.update(Constants.DATABASE_TABLE_AGENDA, values, selection, selectionArgs);
	    	getContext().getContentResolver().notifyChange(uri, null);
	    	break;
	    	
	    default: 
	    	throw new IllegalArgumentException("Unknown URL " + uri);
    	}
    	
    	return rows;
    }
}
