package com.example.helloworld;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GroupDB extends SQLiteOpenHelper{
	private static final int VERSION = 1;
	private static final String DBNAME = "activity_group.db";
	
	//Constructor
	public GroupDB(Context context){
		super(context, DBNAME, null, VERSION);
		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		String CREATE_GROUP_TABLE = "create table activity_group(" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
				"title VARCHAR NOT NULL," +
				"location VARCHAR, " +
				"start DATETIME," +
				"end DATETIME," +
				"description VARCHAR" +
				")";
		
		
		db.execSQL(CREATE_GROUP_TABLE);
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		final String DROP_TABLE = "DROP TABLE IF EXISTS group";
		db.execSQL(DROP_TABLE);
		onCreate(db);
		
	}

}
