package es.ucm.myconference.accountmanager;

import es.ucm.myconference.util.Constants;
import android.content.Context;
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
		// TODO Auto-generated method stub
	}

}
