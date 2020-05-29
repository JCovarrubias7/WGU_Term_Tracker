package com.example.wgutermtrackerjc.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wgutermtrackerjc.data.DBContract.TermEntry;
import com.example.wgutermtrackerjc.data.DBContract.CourseEntry;
import com.example.wgutermtrackerjc.data.DBContract.AssessmentEntry;
import com.example.wgutermtrackerjc.data.DBContract.NoteEntry;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    // Database name and version constants
    private static final String DATABASE_NAME = "terms.db";
    private static final int DATABASE_VERSION = 1;

    // Database constructor
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the terms table
        String SQL_CREATE_TERMS_TABLE = "CREATE TABLE " + TermEntry.TABLE_NAME_TERMS + " ("
                + TermEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TermEntry.COLUMN_TERM_NAME + " TEXT NOT NULL, "
                + TermEntry.COLUMN_TERM_START_DATE + " TEXT NOT NULL, "
                + TermEntry.COLUMN_TERM_END_DATE + " TEXT NOT NULL, "
                + TermEntry.COLUMN_TERM_ACTIVE + " INTEGER NOT NULL DEFAULT 0);";

        String SQL_CREATE_COURSES_TABLE = "CREATE TABLE " + CourseEntry.TABLE_NAME_COURSES + " ("
                + CourseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CourseEntry.COLUMN_ASSOCIATED_TERM_ID + " INTEGER NOT NULL, "
                + CourseEntry.COLUMN_COURSE_NAME + " TEXT NOT NULL, "
                + CourseEntry.COLUMN_COURSE_START + " TEXT NOT NULL, "
                + CourseEntry.COLUMN_COURSE_END + " TEXT NOT NULL, "
                + CourseEntry.COLUMN_COURSE_STATUS + " TEXT NOT NULL, "
                + CourseEntry.COLUMN_COURSE_MENTOR_NAME + " TEXT NOT NULL, "
                + CourseEntry.COLUMN_COURSE_MENTOR_PHONE + " TEXT NOT NULL, "
                + CourseEntry.COLUMN_COURSE_MENTOR_EMAIL + " TEXT NOT NULL);";

        String SQL_CREATE_ASSESSMENT_TABLE = "CREATE TABLE " + AssessmentEntry.TABLE_NAME_ASSESSMENTS + " ("
                + AssessmentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID + " INTEGER NOT NULL, "
                + AssessmentEntry.COLUMN_ASSESSMENT_NAME + " TEXT NOT NULL, "
                + AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE + " TEXT NOT NULL, "
                + AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION + " TEXT NOT NULL);";

        String SQL_CREATE_NOTES_TABLE = "CREATE TABLE " + NoteEntry.TABLE_NAME_NOTES + " ("
                + NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NoteEntry.COLUMN_NOTES_ASSOCIATED_COURSE_ID + " INTEGER NOT NULL, "
                + NoteEntry.COLUMN_NOTES + " TEXT);";

        // Execute the SQL Statement
        ArrayList<String> tableNamesList = new ArrayList<>();
        tableNamesList.add(SQL_CREATE_TERMS_TABLE);
        tableNamesList.add(SQL_CREATE_COURSES_TABLE);
        tableNamesList.add(SQL_CREATE_ASSESSMENT_TABLE);
        tableNamesList.add(SQL_CREATE_NOTES_TABLE);

        for (String str : tableNamesList) {
            db.execSQL(str);
        }
    }

    //Upgrade is called when the database needs to be upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is on version 1 and nothing needs to be done here.
    }

}
