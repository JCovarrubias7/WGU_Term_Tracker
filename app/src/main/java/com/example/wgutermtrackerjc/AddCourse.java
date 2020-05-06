package com.example.wgutermtrackerjc;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.example.wgutermtrackerjc.data.DBContract.CourseEntry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddCourse extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the course data loader
    public static final int EXISTING_COURSE_LOADER = 0;

    // Hold the term ID that launched this activity
    long currentTermId;

    // Content URI for the existing course (null if it's a new course)
    private Uri mCurrentCourseUri;
    // EditText field to enter course name
    private EditText mCourseNameEditText;
    // EditText field to enter the course start date
    private EditText mCourseStartDateEditText;
    // EditText field to enter the course end date
    private EditText mCourseEndDateEditText;
    // EditText field to enter the course status
    private EditText mCourseStatusEditText;
    // Button to save the Course
    private Button mCourseSaveButton;

    // Boolean flag that keeps track of whether the term has been edited(true) or not (false)
    private boolean mTermHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            mTermHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Examine the intent that launched this activity in order to figure out if a term is
        // being created or edited.
        Intent intent = getIntent();
        mCurrentCourseUri = intent.getData();

        // Get the data that was placed in the intent.putExtra
        currentTermId = intent.getLongExtra("termId", -1);

        // If the intent DOES NOT contain a course content URI, then we know that we are creating
        //  a new course.
        if (mCurrentCourseUri == null) {
            // Set the title to add a course indicating creating a new course
            setTitle("Add a Course");
        }
        else {
            // If not null, we must then be editing a course so set the title
            setTitle("Edit Course");
            // Initialize a loader to read the course data from the database and display the
            // values in the editor.
            getLoaderManager().initLoader(EXISTING_COURSE_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mCourseNameEditText = (EditText) findViewById(R.id.edit_course_name);
        mCourseStartDateEditText = (EditText) findViewById(R.id.edit_course_start_date);
        mCourseEndDateEditText = (EditText) findViewById(R.id.edit_course_end_date);
        mCourseStatusEditText = (EditText) findViewById(R.id.edit_course_status);
        mCourseSaveButton = (Button) findViewById(R.id.edit_course_save_button);

        // Create onClickListener for save button to call insertTerm
        mCourseSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertCourse();
                finish();
            }
        });
    }

    private void insertCourse() {
        // Get Term ID and convert to String
        String termIdString =  String.valueOf(currentTermId);
        // Read from the input fields and create strings from them
        String courseNameString = mCourseNameEditText.getText().toString().trim();
        if (courseNameString.isEmpty()) {
            courseNameString = null;
        }
        String courseStartDateString = mCourseStartDateEditText.getText().toString().trim();
        if(courseStartDateString.isEmpty()) {
            courseStartDateString = null;
        }
        String courseEndDateString = mCourseEndDateEditText.getText().toString().trim();
        if(courseEndDateString.isEmpty()) {
            courseEndDateString = null;
        }
        String courseStatusString = mCourseStatusEditText.getText().toString().trim();
        if(courseStatusString.isEmpty()) {
            courseStatusString = null;
        }

        // Create the ContentValues object where column names are the keys,
        // and the input fields for the course are the values
        ContentValues values = new ContentValues();
        values.put(CourseEntry.COLUMN_ASSOCIATED_TERM_ID, termIdString);
        values.put(CourseEntry.COLUMN_COURSE_NAME, courseNameString);
        values.put(CourseEntry.COLUMN_COURSE_START, courseStartDateString);
        values.put(CourseEntry.COLUMN_COURSE_END, courseEndDateString);
        values.put(CourseEntry.COLUMN_COURSE_STATUS, courseStatusString);

        // Determine if this is new or existing course by checking the mCurrentCourseUri
        // If the URI is empty, it must be a new one
        if (mCurrentCourseUri == null) {
            // Insert a new row into the database and return the ID of the new row
            Uri newUri = getContentResolver().insert(CourseEntry.CONTENT_URI_COURSES, values);
        }
        else {
            // if not null, then it is an existing course and we have to update it
            int rowsAffected = getContentResolver().update(mCurrentCourseUri, values, null, null);
        }
    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mTermHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This loader will execute the ContentProviders query method on a background thread
        return new CursorLoader(this,
                mCurrentCourseUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            //Find the index of each term column we are interested in
            int nameColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_NAME);
            int startColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_START);
            int endColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_END);
            int statusColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_STATUS);

            // Extract out the values from the Cursor for the given index
            String name = cursor.getString(nameColumnIndex);
            String start = cursor.getString(startColumnIndex);
            String end = cursor.getString(endColumnIndex);
            String status = cursor.getString(statusColumnIndex);

            // Update the TextView's on the screen with the values from the Database
            mCourseNameEditText.setText(name);
            mCourseStartDateEditText.setText(start);
            mCourseEndDateEditText.setText(end);
            mCourseStatusEditText.setText(status);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
