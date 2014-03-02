package es.ucm.myconference.accountmanager;

import java.util.ArrayList;
import java.util.List;

import es.ucm.myconference.util.Constants;
import android.content.ContentValues;
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

		ContentValues values = new ContentValues();
		values.put(Constants.CONF_UUID, "7d6e088e-d23f-4141-a88e-a52a17b0f30a");
		values.put(Constants.DOC_TITLE, "Prueba.pdf");
		values.put(Constants.DOC_DESCRIPTION, "Un ejemplo de un archivo pdf");
		values.put(Constants.DOC_TYPE, "pdf");
		values.put(Constants.DOC_DATA, "http://pic.sjtu.edu.cn/index.htm");
		long rowID = db.insert(Constants.DATABASE_TABLE_DOCS, null, values);
		Log.d("Table2", "Inserted new document? " + rowID);
		
		String createVenues = "CREATE TABLE IF NOT EXISTS " + Constants.DATABASE_TABLE_VENUES +
						" (" + Constants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
						Constants.CONF_UUID + " TEXT, " + Constants.VENUE_NAME + " TEXT, " + 
						Constants.VENUE_LATITUDE + " REAL, " + Constants.VENUE_LONGITUDE + " REAL, " +
						Constants.VENUE_DETAILS + " TEXT);";
		db.execSQL(createVenues);
		Log.d("Table3", "Venues table created");
		
		ContentValues valuesVenues = new ContentValues();
		valuesVenues.put(Constants.CONF_UUID, "7d6e088e-d23f-4141-a88e-a52a17b0f30a");
		valuesVenues.put(Constants.VENUE_NAME, "Mingya Hotel Shanghai");
		valuesVenues.put(Constants.VENUE_LATITUDE, 31.225394428808613);
		valuesVenues.put(Constants.VENUE_LONGITUDE, 121.47675279999999);
		valuesVenues.put(Constants.VENUE_DETAILS, "500 Gushan Road Pudong, District Shanghai, China");
		rowID = db.insert(Constants.DATABASE_TABLE_VENUES, null, valuesVenues);
		Log.d("Table3", "Inserted new venue? " + rowID);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_CONFS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_DOCS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_VENUES);
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
