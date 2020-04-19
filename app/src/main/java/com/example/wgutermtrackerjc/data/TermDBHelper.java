package com.example.wgutermtrackerjc.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wgutermtrackerjc.data.TermContract.TermEntry;

public class TermDBHelper extends SQLiteOpenHelper {

    // Database name and version constants
    private static final String DATABASE_NAME = "terms.db";
    private static final int DATABASE_VERSION = 1;

    // Database constructor
    public TermDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the terms table
        String SQL_CREATE_TERMS_TABLE = "CREATE TABLE " + TermEntry.TABLE_NAME + " ("
                + TermEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TermEntry.COLUMN_TERM_NAME + " TEXT NOT NULL, "
                + TermEntry.COLUMN_TERM_START_DATE + " TEXT NOT NULL, "
                + TermEntry.COLUMN_TERM_END_DATE + " TEXT NOT NULL);";

        // Execute the SQL Statement
        db.execSQL(SQL_CREATE_TERMS_TABLE);
    }

    //Upgrade is called when the database needs to be upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is on version 1 and nothing needs to be done here.
    }

}
