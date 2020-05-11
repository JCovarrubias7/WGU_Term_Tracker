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

import com.example.wgutermtrackerjc.data.DBContract.CourseEntry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class CourseDetails extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the course data loader
    private static final int EXISTING_COURSE_LOADER = 0;

    // Content URI for existing course
    private Uri mCurrentCourseUri;

    // Hold the term ID that launched this activity
    long currentTermId;

    // Get the All Assessments Image Button
    ImageButton allAssessmentImageButton;

    // TextView field that holds course name
    private TextView mCourseNameText;
    // TextView field that holds the course status
    private TextView mCourseStatusText;
    // TextView field that holds the course start date
    private TextView mCourseStartDateText;
    // TextView field that holds the course end date
    private TextView mCourseEndDateText;
    // TextView field that hold the course mentor name
    private TextView mCourseMentorNameText;
    // TextView field that hold the course mentor phone
    private TextView mCourseMentorPhoneText;
    // TextView field that hold the course mentor email
    private TextView mCourseMentorEmailText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the intent that lunched this activity
        Intent intent = getIntent();
        mCurrentCourseUri = intent.getData();

        // Get the term Id
        currentTermId = intent.getLongExtra("termId", -1);

        // Send information to AssessmentList activity when button is clicked
        allAssessmentImageButton = findViewById(R.id.all_assignments_button);
        allAssessmentImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the intent that launched this activity
                Intent intent = getIntent();
                mCurrentCourseUri = intent.getData();
                // Create new intent to go to CourseDetails
                Intent newIntent = new Intent(CourseDetails.this, AssessmentList.class);
                // Set the URI on the data files of the Intent
                newIntent.setData(mCurrentCourseUri);
                // Launch the activity to edit the data for the current term
                startActivity(newIntent);
            }
        });

        // Find views that we want to set the values from the intent
        mCourseNameText = (TextView) findViewById(R.id.course_name);
        mCourseStatusText = (TextView) findViewById(R.id.course_item_status_text);
        mCourseStartDateText = (TextView) findViewById(R.id.course_item_start_date);
        mCourseEndDateText = (TextView) findViewById(R.id.course_item_end_date);
        mCourseMentorNameText = (TextView) findViewById(R.id.course_item_mentor_name);
        mCourseMentorPhoneText = (TextView) findViewById(R.id.course_item_mentor_phone);
        mCourseMentorEmailText = (TextView) findViewById(R.id.course_item_mentor_email);

        // Initialize the loader to red the term data from the Database
        getLoaderManager().initLoader(EXISTING_COURSE_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_course_details.xml file
        getMenuInflater().inflate(R.menu.menu_course_details, menu);
        return true;
    }

    // Declare what to do with the items in the menu when clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert test data" menu option
            case R.id.action_delete_current_course:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_edit_current_course:
                // Get the intent that launched this activity
                Intent intent = getIntent();
                // for the content URI that was sent from the CoursesList
                mCurrentCourseUri = intent.getData();
                // Create new intent to go to the AddCourse activity
                Intent newIntent = new Intent(CourseDetails.this, AddCourse.class);
                // Set the URI on the data files of the Intent
                newIntent.setData(mCurrentCourseUri);
                // Get the term Id
                currentTermId = intent.getLongExtra("termId", -1);
                // Get Id from term to pass on to the AddCourse activity
                newIntent.putExtra("termId", currentTermId);
                // Launch the activity to edit the data for the current Course
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
        builder.setMessage("Delete this course?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteCourse();
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
    private void deleteCourse() {
        // Only perform the delete if there is an existing course
        if (mCurrentCourseUri != null) {
            // Call the ContentResolver to delete the course at the given URI.
            int rowsDeleted = getContentResolver().delete(mCurrentCourseUri, null, null);
        }
        // Close the activity
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentCourseUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update CourseCursorAdapter with this new cursor containing updated course data
        if(cursor.moveToFirst()) {
            // Find the index of each course column we are interested in
            int nameColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_NAME);
            int statusColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_STATUS);
            int startColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_START);
            int endColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_END);
            int mentorNameColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_MENTOR_NAME);
            int mentorPhoneColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_MENTOR_PHONE);
            int mentorEmailColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_MENTOR_EMAIL);

            // Extract out the values from the Cursor for the given index
            String name = cursor.getString(nameColumnIndex);
            String status = cursor.getString(statusColumnIndex);
            String start  = cursor.getString(startColumnIndex);
            String end = cursor.getString(endColumnIndex);
            String mentorName = cursor.getString(mentorNameColumnIndex);
            String mentorPhone = cursor.getString(mentorPhoneColumnIndex);
            String mentorEmail = cursor.getString(mentorEmailColumnIndex);

            // Update the TextView's on the screen with the values from the Database
            mCourseNameText.setText(name);
            mCourseStatusText.setText(status);
            mCourseStartDateText.setText(start);
            mCourseEndDateText.setText(end);
            mCourseMentorNameText.setText(mentorName);
            mCourseMentorPhoneText.setText(mentorPhone);
            mCourseMentorEmailText.setText(mentorEmail);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
