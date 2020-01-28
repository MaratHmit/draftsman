package ru.etalon5.draftsman;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {		
		super(context, Config.getDatabaseName(), null, Consts.DB_VERSION);
		Log.d("DBName", Config.getDatabaseName());
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		 db.execSQL("CREATE TABLE measurement ("
				+ "key TEXT PRIMARY KEY,"
				+ "num INTEGER,"
				+ "yearDoc INTEGER,"
				+ "dateDoc TEXT,"
				+ "keyCustomer TEXT,"
				+ "customer TEXT,"
				+ "address TEXT,"
				+ "phones TEXT,"
				+ "objectWork TEXT,"
				+ "dateWork TEXT,"
				+ "timeWork TEXT,"
				+ "desirableTime TEXT,"
				+ "keyMaster TEXT,"
				+ "note TEXT,"
				+ "keyUpdatedUser TEXT,"
				+ "keyCreatedUser TEXT,"
				+ "office TEXT,"
				+ "status INTEGER,"
				+ "isDeleted INTEGER,"
				+ "updatedAt INTEGER,"
				+ "createdAt INTEGER);");
		 db.execSQL("CREATE TABLE measurement_object ("
				 + "key TEXT PRIMARY KEY,"
				 + "keyMeasurement TEXT ,"
				 + "name TEXT,"
				 + "note TEXT,"
				 + "draft TEXT,"
				 + "sortIndex INTEGER,"
				 + "keyUpdatedUser TEXT,"
				 + "keyCreatedUser TEXT,"
				 + "isDeleted INTEGER,"
				 + "updatedAt INTEGER,"
				 + "createdAt INTEGER,"
				 + "FOREIGN KEY(keyMeasurement) REFERENCES measurement(key));");
		db.execSQL("CREATE TABLE drawing_template ("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
				+ "key TEXT NOT NULL UNIQUE,"
				+ "name TEXT NOT NULL,"
				+ "draft TEXT,"
				+ "updatedAt INTEGER,"
				+ "createdAt INTEGER);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 1 && newVersion == 2) {
			db.execSQL("CREATE TABLE drawing_template ("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
					+ "key TEXT NOT NULL UNIQUE,"
					+ "name TEXT NOT NULL,"
					+ "draft TEXT,"
					+ "updatedAt INTEGER,"
					+ "createdAt INTEGER);");
		}
	}

}
