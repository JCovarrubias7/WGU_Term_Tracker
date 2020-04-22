package com.example.wgutermtrackerjc;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.example.wgutermtrackerjc.data.TermContract.TermEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class AllTerms extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifies the Loader being used
    public static final int TERM_LOADER =0;

    // Adapter in all the call back methods
    TermCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_terms);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllTerms.this, AddTerm.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the term data
        ListView termListView = (ListView) findViewById(R.id.all_terms_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items
        View emptyView = findViewById(R.id.empty_term_view);
        termListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each rof of term data in the cursor
        mCursorAdapter = new TermCursorAdapter(this, null);
        termListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        termListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to CourseView
                Intent intent = new Intent(AllTerms.this, CourseView.class);
                // Form the content URI that represents the term that was clicked on
                Uri currentTermUri = ContentUris.withAppendedId(TermEntry.CONTENT_URI, id);
                // Set the URI on the data fields of the Intent
                intent.setData(currentTermUri);

                // Launch the activity to display the data for the current Term
                startActivity(intent);
            }
        });

        // Initialized the CursorLoader
        getLoaderManager().initLoader(TERM_LOADER, null, this);
    }

    // Create the data that will be inserted from the menu option Insert Test Data.
    private void insertTestTerm() {
        // Create a ContentValues object where column names are the keys,
        // and Term attributes are the the values.
        ContentValues values = new ContentValues();
        values.put(TermEntry.COLUMN_TERM_NAME, "Term 1");
        values.put(TermEntry.COLUMN_TERM_START_DATE, "05/01/2020");
        values.put(TermEntry.COLUMN_TERM_END_DATE, "11/01/2020");

        // Insert a new row into the database and return the ID of the new row
        //long newRowId = db.insert(TermEntry.TABLE_NAME, null, values);
        getContentResolver().insert(TermEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_terms.xml file
        getMenuInflater().inflate(R.menu.menu_terms, menu);
        return true;
    }

    // Declare what to do with the items in the menu when clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert test data" menu option
            case R.id.action_insert_test_data:
                insertTestTerm();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_terms:
                deleteAllTerms();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about
        String[] projection = {
                TermEntry._ID,
                TermEntry.COLUMN_TERM_NAME,
                TermEntry.COLUMN_TERM_START_DATE,
                TermEntry.COLUMN_TERM_END_DATE};

        // This loader wil execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                TermEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        // Update TermCursorAdapter with this new cursor containing updated term data
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    // Helper method to delete all the terms in the DataBase
    private void deleteAllTerms() {
        // Only perform the delete if there is an item in the terms table
        if(TermEntry.CONTENT_URI != null) {
            int rowsDeleted = getContentResolver().delete(TermEntry.CONTENT_URI, null, null);
            // Show a toast message depending on condition
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Error with deleting all Terms",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "All terms successfully deleted",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
