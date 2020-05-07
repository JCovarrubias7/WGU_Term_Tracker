package com.example.wgutermtrackerjc;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.example.wgutermtrackerjc.data.DBContract.TermEntry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class TermDetails extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the term data loader
    private static final int EXISTING_TERM_LOADER = 0;

    // Content URI for existing term
    private Uri mCurrentTermUri;

    // Get the All Courses Image Button
    ImageButton allCoursesImageButton;

    // TextView field that holds the term name
    private TextView mTermNameText;
    // TextView field that holds the term start date
    private TextView mTermStartDateText;
    // TextView field that holds the term end date
    private TextView mTermEndDateText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the intent that launched this activity
        Intent intent = getIntent();
        mCurrentTermUri = intent.getData();

        // Send information to CourseList activity when button is clicked
        allCoursesImageButton = findViewById(R.id.all_courses_button);
        allCoursesImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the intent that launched this activity
                Intent intent = getIntent();
                mCurrentTermUri = intent.getData();
                // Create new intent to go to CourseDetails
                Intent newIntent = new Intent(TermDetails.this, CoursesList.class);
                // Set the URI on the data files of the Intent
                newIntent.setData(mCurrentTermUri);
                // Launch the activity to edit the data for the current term
                startActivity(newIntent);
            }
        });

        // Find views that we want to modify in the list item layout
        mTermNameText = (TextView) findViewById(R.id.term_name);
        mTermStartDateText = (TextView) findViewById(R.id.term_start_date);
        mTermEndDateText = (TextView) findViewById(R.id.term_end_date);

        // Initialize the loader to red the term data from the Database
        getLoaderManager().initLoader(EXISTING_TERM_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_terms.xml file
        getMenuInflater().inflate(R.menu.menu_term_view, menu);
        return true;
    }

    // Declare what to do with the items in the menu when clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert test data" menu option
            case R.id.action_delete_current_term:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_edit_current_term:
                // Get the intent that launched this activity
                Intent intent = getIntent();
                // for the content URI that was sent from the TermsList
                mCurrentTermUri = intent.getData();
                // Create new intent to go to the AddTerm activity
                Intent newIntent = new Intent(TermDetails.this, AddTerm.class);
                // Set the URI on the data files of the Intent
                newIntent.setData(mCurrentTermUri);
                // Launch the activity to edit the data for the current term
                startActivity(newIntent);
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Create the delete confirmation dialog message when deleting a term
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this term?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteTerm();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Perform the deletion of the term in the database
    private void deleteTerm() {
        // Only perform the delete if there is an existing term
        if (mCurrentTermUri != null) {
            // Call the ContentResolver to delete the product at the given URI.
            int rowsDeleted = getContentResolver().delete(mCurrentTermUri, null, null);
        }
        // Close the activity
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentTermUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update CourseCursorAdapter with this new cursor containing updated course data
        if(cursor.moveToFirst()) {
            //Find the index of each term column we are interested in
            int nameColumnIndex = cursor.getColumnIndex(TermEntry.COLUMN_TERM_NAME);
            int startColumnIndex = cursor.getColumnIndex(TermEntry.COLUMN_TERM_START_DATE);
            int endColumnIndex = cursor.getColumnIndex(TermEntry.COLUMN_TERM_END_DATE);

            // Extract out the values from the Cursor for the given index
            String name = cursor.getString(nameColumnIndex);
            String start = cursor.getString(startColumnIndex);
            String end = cursor.getString(endColumnIndex);

            // Update the TextView's on the screen with the values from the Database
            mTermNameText.setText(name);
            mTermStartDateText.setText(start);
            mTermEndDateText.setText(end);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
