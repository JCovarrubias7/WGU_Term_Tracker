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

import com.example.wgutermtrackerjc.data.DBContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class CoursesList extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the term data loader
    private static final int EXISTING_TERM_LOADER = 0;

    // Content URI for existing term
    private Uri mCurrentTermUri;

    // Adapter in all the call back methods
    CourseCursorAdapter mCursorAdapter;

    // Hold the term ID that launched this activity
    long currentTermId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CoursesList.this, AddCourse.class);

                // Get Id from term to pass on to the AddCourse activity
                intent.putExtra("termId", currentTermId);
                startActivity(intent);
            }
        });
        // Get the intent that launched this activity
        Intent intent = getIntent();
        mCurrentTermUri = intent.getData();

        //Set currentTermId with value from URI
        currentTermId = ContentUris.parseId(mCurrentTermUri);

        // Find the ListView which will be populated with the courses data
        ListView coursesListView = (ListView) findViewById(R.id.all_courses_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items
        View emptyView = findViewById(R.id.empty_courses_view);
        coursesListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of course data in the cursor
        mCursorAdapter = new CourseCursorAdapter(this, null);
        coursesListView.setAdapter(mCursorAdapter);

        coursesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to CourseDetails
                Intent intent = new Intent(CoursesList.this, CourseDetails.class);
                // Form the content URI that represents the course that was clicked on
                Uri currentCourseUri = ContentUris.withAppendedId(DBContract.CourseEntry.CONTENT_URI_COURSES, id);
                // Set the URI on the data fields of the Intent
                intent.setData(currentCourseUri);

                // Get the TextViews that have the values we want
                TextView nameView = (TextView) view.findViewById(R.id.list_course_item_name);
                TextView startView = (TextView) view.findViewById(R.id.list_course_item_start_date);
                TextView endView = (TextView) view.findViewById(R.id.list_course_item_end_date);
                TextView statusView = (TextView) view.findViewById(R.id.list_course_item_status_text);

                // Get the string values of each text view of the list
                String courseName = nameView.getText().toString();
                String courseStart = startView.getText().toString();
                String courseEnd = endView.getText().toString();
                String courseStatus = statusView.getText().toString();

                intent.putExtra("courseId", id);
                intent.putExtra("courseName", courseName);
                intent.putExtra("courseStatus", courseStatus);
                intent.putExtra("courseStart", courseStart);
                intent.putExtra("courseEnd", courseEnd);


                // Launch the activity to display the data for the current Course
                startActivity(intent);
            }
        });

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
                Intent newIntent = new Intent(CoursesList.this, AddTerm.class);
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
        // Get the columns from the table using the URI.
        // Define a projection that specifies the columns from the table we care about
        String[] projection = {
                DBContract.CourseEntry._ID,
                DBContract.CourseEntry.COLUMN_ASSOCIATED_TERM_ID,
                DBContract.CourseEntry.COLUMN_COURSE_NAME,
                DBContract.CourseEntry.COLUMN_COURSE_START,
                DBContract.CourseEntry.COLUMN_COURSE_END,
                DBContract.CourseEntry.COLUMN_COURSE_STATUS};

        // Define a selection
        String selection = DBContract.CourseEntry.COLUMN_ASSOCIATED_TERM_ID + "=?";
        String[] selectionArgs = {String.valueOf(currentTermId)};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                DBContract.CourseEntry.CONTENT_URI_COURSES,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update CourseCursorAdapter with this new cursor containing updated course data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}