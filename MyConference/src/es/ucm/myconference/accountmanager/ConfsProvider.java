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
    	default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
    	}
	}
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
    					String sortOrder) {
    	//TODO How to differentiate tables?
    	SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
    	sqlBuilder.setTables(Constants.DATABASE_TABLE_CONFS);
    	if(uriMatcher.match(uri) == Constants.CONFS_ID){
			sqlBuilder.appendWhere(Constants._ID + " = " + uri.getPathSegments().get(1));
		}
    	if(sortOrder == null || sortOrder == "") sortOrder = Constants._ID;
    	Cursor c = sqlBuilder.query(confsDB, projection, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
    }
    
    @Override
    public Uri insert(Uri uri, ContentValues values) {
    	Uri _uri = null;
    	//Select between tables
    	switch(uriMatcher.match(uri)){
    	case Constants.CONFS:
    		long rowID = confsDB.insert(Constants.DATABASE_TABLE_CONFS, "", values);
    		if(rowID >0){
    			_uri = ContentUris.withAppendedId(Constants.CONTENT_URI_CONFS, rowID);
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
    	int rows = confsDB.delete(Constants.DATABASE_TABLE_CONFS, selection, selectionArgs);
    	getContext().getContentResolver().notifyChange(uri, null);
    	return rows;
    }
    
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rows = confsDB.update(Constants.DATABASE_TABLE_CONFS, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }
}
