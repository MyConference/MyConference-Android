package es.ucm.myconference.accountmanager;

import java.util.ArrayList;
import java.util.List;

import es.ucm.myconference.util.Constants;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlHelper extends SQLiteOpenHelper {

	public SqlHelper(Context context) {
		super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// All tables will be created here
		if(db.isReadOnly()) db = getWritableDatabase();
		String create = "CREATE TABLE IF NOT EXISTS " + Constants.DATABASE_TABLE_CONFS +
						" (" + Constants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
						Constants.CONF_NAME + " TEXT, " + Constants.CONF_DESCRP + " TEXT);";
		db.execSQL(create);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_CONFS);
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
