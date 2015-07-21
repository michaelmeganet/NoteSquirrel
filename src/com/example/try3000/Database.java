package com.example.try3000;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper{

	public Database(Context context) {
		super(context, "notes.db", null, 1);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		String sql = "create table POINTS (ID INTEGER PRIMARY KEY, X INTEGER NOT NULL, Y INTEGER NOT NULL)";
		
		db.execSQL(sql);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		
	}

}
