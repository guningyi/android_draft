package com.example.helloworld;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class UserDB extends SQLiteOpenHelper{

	private static final int VERSION = 1;
	private static final String DBNAME = "user.db";
	//Constructor
	public UserDB(Context context){
		super(context, DBNAME, null, VERSION);
		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		String CREATE_USER_TABLE = 
				"create table user(" + 
				"id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
				"username VARCHAR NOT NULL," +
				"password VARCHAR NOT NULL" +
				")";
		db.execSQL(CREATE_USER_TABLE);
		
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
		final String DROP_TABLE = "DROP TABLE IF EXISTS user";
		db.execSQL(DROP_TABLE);
		onCreate(db);
	}
}
