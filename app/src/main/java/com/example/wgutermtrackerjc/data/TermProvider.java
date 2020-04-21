package com.example.wgutermtrackerjc.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import com.example.wgutermtrackerjc.data.TermContract.TermEntry;

public class TermProvider extends ContentProvider {

    // URI matcher code for the content URI for the Terms table and single term
    public static final int TERMS= 1000;
    public static final int TERM_ID = 1001;

    // URI object to match a content URI to a corresponding code
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // The content URI of TERMS will map to the integer in TERMS(1000) to provide access to the whole table
        sUriMatcher.addURI(TermContract.CONTENT_AUTHORITY, TermContract.PATH_TERMS, TERMS);
        // The content URI of TERM_ID will map to the integer in TERM_ID(1001) to provide access to one row
        sUriMatcher.addURI(TermContract.CONTENT_AUTHORITY, TermContract.PATH_TERMS + "/#", TERM_ID);
    }

    //Database helper object
    private TermDBHelper mDBHelper;

    @Override
    public boolean onCreate() {
        mDBHelper = new TermDBHelper(getContext());
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
                cursor = database.query(TermEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TERM_ID:
                selection = TermEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(TermEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TERMS:
                return insertTerm(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not support for" + uri);
        }
    }

    private Uri insertTerm(Uri uri, ContentValues values) {
        // Get writeable database
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        // Insert the new term with the given values
        long id = database.insert(TermEntry.TABLE_NAME, null, values);
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
        int rowsUpdated = database.update(TermEntry.TABLE_NAME, values, selection, selectionArgs);
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
                rowsDeleted = database.delete(TermEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TERM_ID:
                 // Delete a single row given by the ID in the URI
                selection = TermEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(TermEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
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
