package es.ucm.myconference.accountmanager;

import java.util.ArrayList;
import java.util.List;

import es.ucm.myconference.util.Constants;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqlHelper extends SQLiteOpenHelper {

	public SqlHelper(Context context) {
		super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// All tables will be created here
		if(db.isReadOnly()) db = getWritableDatabase();
		String createConfs = "CREATE TABLE IF NOT EXISTS " + Constants.DATABASE_TABLE_CONFS +
						" (" + Constants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
						Constants.CONF_NAME + " TEXT, " + Constants.CONF_DESCRP + " TEXT, " +
						Constants.CONF_UUID + " TEXT);";
		db.execSQL(createConfs);
		Log.d("Table1", "Conferences table created");
		
		String createDocs = "CREATE TABLE IF NOT EXISTS " + Constants.DATABASE_TABLE_DOCS +
						" (" + Constants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
						Constants.CONF_UUID + " TEXT, " + Constants.DOC_TITLE + " TEXT, " +
						Constants.DOC_DESCRIPTION + " TEXT, " + Constants.DOC_TYPE + " TEXT, " +
						Constants.DOC_DATA + " TEXT);";
		db.execSQL(createDocs);
		Log.d("Table2", "Documents table created");
		
		String createVenues = "CREATE TABLE IF NOT EXISTS " + Constants.DATABASE_TABLE_VENUES +
						" (" + Constants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
						Constants.CONF_UUID + " TEXT, " + Constants.VENUE_NAME + " TEXT, " + 
						Constants.VENUE_LATITUDE + " REAL, " + Constants.VENUE_LONGITUDE + " REAL, " +
						Constants.VENUE_DETAILS + " TEXT);";
		db.execSQL(createVenues);
		Log.d("Table3", "Venues table created");
		
		String createAnnouncements = "CREATE TABLE IF NOT EXISTS " + Constants.DATABASE_TABLE_ANNOUNCEMENTS +
						" (" + Constants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
						Constants.CONF_UUID + " TEXT, " + Constants.ANNOUNCEMENT_TITLE + " TEXT, " + 
						Constants.ANNOUNCEMENT_BODY + " TEXT, " + Constants.ANNOUNCEMENT_DATE + 
						" TEXT);";
		db.execSQL(createAnnouncements);
		Log.d("Table4", "Announcements table created");
		
		String createKeynotes = "CREATE TABLE IF NOT EXISTS " + Constants.DATABASE_TABLE_KEYNOTE +
						" (" + Constants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
						Constants.CONF_UUID + " TEXT, " + Constants.KEYNOTES_NAME + " TEXT, " +
						Constants.KEYNOTES_CHARGE + " TEXT, " + Constants.KEYNOTES_ORIGIN + " TEXT, " +
						Constants.KEYNOTES_DESCRIPTION + " TEXT, " + Constants.KEYNOTES_PHOTO + " TEXT, " +
						Constants.KEYNOTE_LINKS + " TEXT);";
		db.execSQL(createKeynotes);
		Log.d("Table5", "Keynotes table created");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed TODO Dani me dijo algo de actualizar lo nuevo solo
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_CONFS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_DOCS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_VENUES);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_ANNOUNCEMENTS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_KEYNOTE);
        // Create tables again
        onCreate(db);
	}
	
	public List<String> getConfsNames(){
    	List<String> list = new ArrayList<String>();
    	String query = "SELECT NAME FROM " + Constants.DATABASE_TABLE_CONFS;
    	SQLiteDatabase db = this.getWritableDatabase();
    	Cursor c = db.rawQuery(query, null);
    	if (c.moveToFirst()) {
            do {
                list.add(c.getString(0));
            } while (c.moveToNext());
        }
    	c.close();
    	db.close();
    	return list;
    }
}
