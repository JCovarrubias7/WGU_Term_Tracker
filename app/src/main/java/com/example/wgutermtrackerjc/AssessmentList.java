package com.example.wgutermtrackerjc;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.example.wgutermtrackerjc.data.DBContract.AssessmentEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class AssessmentList extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the course data loader
    public static final int EXISTING_COURSE_LOADER = 0;

    // Content URI for existing course
    private Uri mCurrentCourseUri;

    // Adapter in all the call back methods
    AssessmentCursorAdapter mCursorAdapter;

    // Hold the course Id that launched this activity
    long currentCourseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssessmentList.this, AddAssessment.class);
                // Get the Id from the course to pass on to the AddAssessment activity
                intent.putExtra("courseId", currentCourseId);
                startActivity(intent);
            }
        });
        // Get the intent that launched this activity
        Intent intent = getIntent();
        mCurrentCourseUri = intent.getData();

        //Set currentTermId with value from URI
        currentCourseId = ContentUris.parseId(mCurrentCourseUri);

        // Find the ListView which will be populated with the courses data
        ListView assessmentsListView = (ListView) findViewById(R.id.all_assessments_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items
        View emptyView = findViewById(R.id.empty_assessments_view);
        assessmentsListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of course data in the cursor
        mCursorAdapter = new AssessmentCursorAdapter(this, null);
        assessmentsListView.setAdapter(mCursorAdapter);

        // Setup the item click listener on the list items
        assessmentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to AssessmentDetails
                Intent intent = new Intent(AssessmentList.this, AssessmentDetails.class);
                // Form the content URI that represents the assessment that was clicked on
                Uri currentAssessmentUri = ContentUris.withAppendedId(AssessmentEntry.CONTENT_URI_ASSESSMENTS, id);
                // Set the URI on the data fields of the Intent
                intent.setData(currentAssessmentUri);
                // Get Id from course to pass on to the AddAssessment activity
                intent.putExtra("courseId", currentCourseId);
                // Launch the activity to display the data fro the current Assessment
                startActivity(intent);
            }
        });

        // Initialize the loader to read the term data from the Database
        getLoaderManager().initLoader(EXISTING_COURSE_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_assessments_list.xml file
        getMenuInflater().inflate(R.menu.menu_assessments_list, menu);
        return true;
    }

    // Declare what to do with the items in the menu when clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert test data" menu option
            case R.id.action_delete_all_assessments:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Create the delete confirmation dialog message when deleting an assessment
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete all assessments?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteAssessments();
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

    // Perform the deletion of the assessments in the database
    private void deleteAssessments() {
        // Only perform the delete if there is an existing assessment
        if (AssessmentEntry.CONTENT_URI_ASSESSMENTS != null) {
            // Get the assessments in this course
            // Define a projection that specifies the columns from the table we care about
            String[] projection = {
                    AssessmentEntry._ID,
                    AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID};
            // Define a selection
            String selection = AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID + "=?";
            String[] selectionArgs = { String.valueOf(currentCourseId) };
            // Get the cursor with the assessments that have current course as their associated course
            Cursor cursor = getContentResolver().query(AssessmentEntry.CONTENT_URI_ASSESSMENTS, projection,
                    selection, selectionArgs, null);
            try {
                while (cursor.moveToNext()) {
                    // Get Column index
                    int idColumnIndex = cursor.getColumnIndex(AssessmentEntry._ID);
                    // Extract out the values from the Cursor for the given index
                    Long id = cursor.getLong(idColumnIndex);
                    // Form the content URI that represents the assessment
                    Uri currentAssessmentUri = ContentUris.withAppendedId(AssessmentEntry.CONTENT_URI_ASSESSMENTS, id);
                    // Call the ContentResolver to delete the course at the given URI.
                    int rowDelete = getContentResolver().delete(currentAssessmentUri, null, null);
                }
            }
            finally {
                cursor.close();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Get the columns from the table using the URI.
        // Define a projection that specifies the columns from the table we care about
        String[] projection = {
                AssessmentEntry._ID,
                AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID,
                AssessmentEntry.COLUMN_ASSESSMENT_NAME,
                AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE,
                AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION};

        // Define a selection
        String selection = AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID + "=?";
        String[] selectionArgs = {String.valueOf(currentCourseId)};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                AssessmentEntry.CONTENT_URI_ASSESSMENTS,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update AssessmentCursorAdapter with this new cursor containing updated assessment data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
