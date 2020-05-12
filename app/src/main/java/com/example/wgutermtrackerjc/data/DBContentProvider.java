package com.example.wgutermtrackerjc.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import com.example.wgutermtrackerjc.data.DBContract.TermEntry;
import com.example.wgutermtrackerjc.data.DBContract.CourseEntry;
import com.example.wgutermtrackerjc.data.DBContract.AssessmentEntry;

public class DBContentProvider extends ContentProvider {

    // URI matcher code for the content URI for the Terms table and single term
    public static final int TERMS= 1000;
    public static final int TERM_ID = 1001;
    public static final int COURSES = 2000;
    public static final int COURSE_ID = 2001;
    public static final int ASSESSMENTS = 3000;
    public static final int ASSESSMENT_ID = 3001;

    // URI object to match a content URI to a corresponding code
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // The content URI of TERMS will map to the integer in TERMS(1000) to provide access to the whole table
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.PATH_TERMS, TERMS);
        // The content URI of TERM_ID will map to the integer in TERM_ID(1001) to provide access to one row
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.PATH_TERMS + "/#", TERM_ID);

        // The content URI of COURSES will map to the integer in COURSES(2000) to provide access to the whole table
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.PATH_COURSES, COURSES);
        // The content URI of COURSE_ID will map to the integer in COURSE_ID(2001) to provide access to one row
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.PATH_COURSES + "/#", COURSE_ID);

        // The content URI of ASSESSMENTS will map to the integer in ASSESSMENTS(3000) to provide access to the whole table
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.PATH_ASSESSMENTS, ASSESSMENTS);
        // The content URI of ASSESSMENT_ID will map to the integer in ASSESSMENT_ID(3001) to provide access to one row
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.PATH_ASSESSMENTS + "/#", ASSESSMENT_ID);
    }

    //Database helper object
    private DBHelper mDBHelper;

    @Override
    public boolean onCreate() {
        mDBHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri,String [] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Get a readable database
        SQLiteDatabase database = mDBHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TERMS:
                cursor = database.query(TermEntry.TABLE_NAME_TERMS, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TERM_ID:
                selection = TermEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(TermEntry.TABLE_NAME_TERMS, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case COURSES:
                cursor = database.query(CourseEntry.TABLE_NAME_COURSES, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case COURSE_ID:
                selection = CourseEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(CourseEntry.TABLE_NAME_COURSES, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ASSESSMENTS:
                cursor = database.query(AssessmentEntry.TABLE_NAME_ASSESSMENTS, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ASSESSMENT_ID:
                selection = AssessmentEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(AssessmentEntry.TABLE_NAME_ASSESSMENTS, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }
        // Set notification URI on the Cursor
        // If the data changes on the URI, then we update the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TERMS:
                return insertTerm(uri, contentValues);
            case COURSES:
                return insertCourse(uri, contentValues);
            case ASSESSMENTS:
                return insertAssessment(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not support for" + uri);
        }
    }

    private Uri insertTerm(Uri uri, ContentValues values) {
        // Get writeable database
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        // Insert the new term with the given values
        long id = database.insert(TermEntry.TABLE_NAME_TERMS, null, values);
        // Show a toast message whether or not the insertion was successful
        if (id == -1) {
            Toast.makeText(getContext(), "Error saving the term", Toast.LENGTH_SHORT).show();
            return null;
        }
        else {
            Toast.makeText(getContext(), "Term saved with row id: " + id, Toast.LENGTH_SHORT).show();
        }

        // Notify all listeners that the data has changed for the term content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row, return a new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertCourse(Uri uri, ContentValues values) {
        // Get Writeable database
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        // Insert the new course with the given values
        long id = database.insert(CourseEntry.TABLE_NAME_COURSES, null, values);
        // Show a toast message whether or not the insertion was successful
        if (id == -1) {
            Toast.makeText(getContext(), "Error saving the course", Toast.LENGTH_SHORT).show();
            return null;
        }
        else {
            Toast.makeText(getContext(), "Course saved with row id: " + id, Toast.LENGTH_SHORT).show();
        }

        // Notify all listeners that the data has changed for the course content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row, return a new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertAssessment(Uri uri, ContentValues values) {
        // Get Writable database
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        // Insert the new assessment with the given values
        long id = database.insert(AssessmentEntry.TABLE_NAME_ASSESSMENTS, null, values);
        // Show a toast message whether or not the insertion was successful
        if (id == -1) {
            Toast.makeText(getContext(), "Error saving the assessment", Toast.LENGTH_SHORT).show();
            return null;
        }
        else {
            Toast.makeText(getContext(), "Assessment saved with row id: " + id, Toast.LENGTH_SHORT).show();
        }

        // Notify all listeners that the data has changed for the assessment content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row, return a new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TERMS:
                return updateTerm(uri, contentValues, selection, selectionArgs);
            case TERM_ID:
                selection = TermEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                return updateTerm(uri, contentValues, selection, selectionArgs);
            case COURSES:
                return updateCourse(uri, contentValues, selection, selectionArgs);
            case COURSE_ID:
                selection = CourseEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                return updateCourse(uri, contentValues, selection, selectionArgs);
            case ASSESSMENTS:
                return updateAssessment(uri, contentValues, selection, selectionArgs);
            case ASSESSMENT_ID:
                selection = AssessmentEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                return updateAssessment(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not support for" + uri);
        }
    }

    private int updateTerm(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // No need to update the term if there are no values to update
        if (values.size() == 0) {
            return 0;
        }
        // Otherwise, get the writeable database to update the data
        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(TermEntry.TABLE_NAME_TERMS, values, selection, selectionArgs);
        // Show a toast message whether or not the update was successful
        if (rowsUpdated == 0) {
            Toast.makeText(getContext(), "Error saving the Term", Toast.LENGTH_SHORT).show();
            return 0;
        }
        else {
            Toast.makeText(getContext(), "Term updated", Toast.LENGTH_SHORT).show();
        }
        // If 1 or more rows were updated, notify all listeners about the change
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private int updateCourse(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // No need to update the course if there are no values to update
        if (values.size() == 0) {
            return 0;
        }
        // Otherwise, get the writeable database to update the data
        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(CourseEntry.TABLE_NAME_COURSES, values, selection, selectionArgs);
        // Show a toast message whether or not the update was successful
        if (rowsUpdated == 0) {
            Toast.makeText(getContext(), "Error saving the course", Toast.LENGTH_SHORT).show();
            return 0;
        }
        else {
            Toast.makeText(getContext(), "Course updated", Toast.LENGTH_SHORT).show();
        }
        // If 1 or more rows were updated, notify all listeners about the change
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private int updateAssessment(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // No need to update the assessment if there are no values to update
        if (values.size() == 0) {
            return 0;
        }
        // Otherwise, get the writeable database to update the data
        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(AssessmentEntry.TABLE_NAME_ASSESSMENTS, values, selection, selectionArgs);
        // Show a toast message whether or not the update was successful
        if (rowsUpdated == 0) {
            Toast.makeText(getContext(), "Error saving the assessment", Toast.LENGTH_SHORT).show();
            return 0;
        }
        else {
            Toast.makeText(getContext(), "Assessment updated", Toast.LENGTH_SHORT).show();
        }
        // If 1 or more rows were updated, notify all listeners about the change
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TERMS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(TermEntry.TABLE_NAME_TERMS, selection, selectionArgs);
                break;
            case TERM_ID:
                 // Delete a single row given by the ID in the URI
                selection = TermEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(TermEntry.TABLE_NAME_TERMS, selection, selectionArgs);
                break;
            case COURSES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(CourseEntry.TABLE_NAME_COURSES, selection, selectionArgs);
                break;
            case COURSE_ID:
                // Delete a single row given by the ID in the URI
                selection = CourseEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(CourseEntry.TABLE_NAME_COURSES, selection, selectionArgs);
                break;
            case ASSESSMENTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(AssessmentEntry.TABLE_NAME_ASSESSMENTS, selection, selectionArgs);
                break;
            case ASSESSMENT_ID:
                // Delete a single row give by the ID in the URI
                selection = AssessmentEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(AssessmentEntry.TABLE_NAME_ASSESSMENTS, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // Show a toast message whether or not the deletion was successful
        if (rowsDeleted == 0) {
            Toast.makeText(getContext(), "Error with deletion", Toast.LENGTH_SHORT).show();
            return 0;
        }
        else {
            Toast.makeText(getContext(), "Deletion completed successfully", Toast.LENGTH_SHORT).show();
        }
        // If 1 or more rows were deleted, notify all listeners that the data has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TERMS:
                return TermEntry.CONTENT_LIST_TYPE;
            case TERM_ID:
                return TermEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

}
