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

import com.example.wgutermtrackerjc.data.DBContract.AssessmentEntry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddAssessment extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the course data loader
    public static final int EXISTING_ASSESSMENT_LOADER = 0;

     // Hold the course ID that launched this activity
    long currentCourseId;

    // Content URI for the existing course (null if it's a new course)
    private Uri mCurrentAssessmentUri;
    // EditText field to enter assessment name
    private EditText mAssessmentName;
    // EditText field to enter the due date
    private EditText mAssessmentDueDate;
    // EditText field to enter the description
    private EditText mAssessmentDescription;
    // Button to save the Assessment
    private Button mAssessmentSaveButton;

    // Boolean flag that keeps track of whether the assessment has been edited(true) or not (false)
    private boolean mAssessmentHasChanged;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            mAssessmentHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assessment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Examine the intent that launched this activity in order to figure out if a term is
        // being created or edited.
        Intent intent = getIntent();
        mCurrentAssessmentUri = intent.getData();

        // Get the data that was placed in the intent.putExtra
        currentCourseId = intent.getLongExtra("courseId", -1);

        // If the intent DOES NOT contain an assessment content URI, then we know that we are creating
        //  a new assessment.
        if (mCurrentAssessmentUri == null) {
            // Set the title to add an assessment indicating creating a new assessment
            setTitle("Add an Assessment");
        }
        else {
            // If not null, we must then be editing an assessment so set the title
            setTitle("Edit Assessment");
            // Initialize a loader to read the course data from the database and display the
            // values in the editor.
            getLoaderManager().initLoader(EXISTING_ASSESSMENT_LOADER, null, this);
        }

        // Find all the relevant views that we will need to read user input from
        mAssessmentName = (EditText) findViewById(R.id.edit_assessment_name);
        mAssessmentDueDate = (EditText) findViewById(R.id.edit_assessment_due_date);
        mAssessmentDescription = (EditText) findViewById(R.id.edit_assessment_description);
        mAssessmentSaveButton = (Button) findViewById(R.id.edit_assessment_save_button);

        // Create onClickListener for save button to call insertAssessment
        mAssessmentSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertAssessment();
                finish();
            }
        });
    }

    private void insertAssessment() {
        // Get the Course ID and convert it to a string
        String courseIdString = String.valueOf(currentCourseId);
        // Read from the input fields and create strings from them
        String assessmentNameString = mAssessmentName.getText().toString().trim();
        if (assessmentNameString.isEmpty()) {
            assessmentNameString = null;
        }
        String assessmentDueDateString = mAssessmentDueDate.getText().toString().trim();
        if (assessmentDueDateString.isEmpty()) {
            assessmentDueDateString = null;
        }
        String assessmentDescriptionString = mAssessmentDescription.getText().toString().trim();
        if (assessmentDescriptionString.isEmpty()) {
            assessmentDescriptionString = null;
        }

        // Create the ContentValues object where column names are the keys,
        // and the input fields for the assessment are the values
        ContentValues values = new ContentValues();
        values.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, courseIdString);
        values.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, assessmentNameString);
        values.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, assessmentDueDateString);
        values.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, assessmentDescriptionString);

        // Determine if this is new or existing assessment by checking the mCurrentAssessmentUri
        // If the URI is empty, it must be a new one
        if (mCurrentAssessmentUri == null) {
            // Insert a new row into the database and return the ID of the new row
            Uri newUri = getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, values);
        }
        else {
            // if not null, then it is an existing assessment and we we have to update it
            int rowsAffected = getContentResolver().update(mCurrentAssessmentUri, values, null, null);
        }
    }

    @Override
    public void onBackPressed() {
        // If the course hasn't changed, continue with handling back button press
        if (!mAssessmentHasChanged) {
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

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This loader will execute the ContentProviders query method on a background thread
        return new CursorLoader(this,
                mCurrentAssessmentUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            // Find the index of each assessment column we are interested in
            int nameColumnIndex = cursor.getColumnIndex(AssessmentEntry.COLUMN_ASSESSMENT_NAME);
            int dueDateColumnIndex = cursor.getColumnIndex(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE);
            int descriptionColumnIndex = cursor.getColumnIndex(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION);

            // Extract out the values from the Cursor for the given index
            String name = cursor.getString(nameColumnIndex);
            String dueDate = cursor.getString(dueDateColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);

            // Update the EditViews on the screen with the values from the Database
            mAssessmentName.setText(name);
            mAssessmentDueDate.setText(dueDate);
            mAssessmentDescription.setText(description);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
