package com.example.wgutermtrackerjc;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.example.wgutermtrackerjc.data.DBContract.CourseEntry;
import com.example.wgutermtrackerjc.data.DBContract.NoteEntry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CourseDetails extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the course data loader
    private static final int EXISTING_COURSE_LOADER = 0;

    // Content URI for existing course
    private Uri mCurrentCourseUri;

    // Hold the term ID that launched this activity
    long currentTermId, currentCourseId, noteId;

    // Get the All Assessments Image Button
    ImageButton allAssessmentImageButton, notesImageButton;

    // Initialize Menu
    Menu myMenu;

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

        // Get the intent that launched this activity
        Intent intent = getIntent();
        mCurrentCourseUri = intent.getData();

        // Get the term Id and course Id
        currentTermId = intent.getLongExtra("termId", -1);
        currentCourseId = ContentUris.parseId(mCurrentCourseUri);

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
                // Launch the activity to view the assessments list
                startActivity(newIntent);
            }
        });

        // Send information to Add/Details Note activity when button is clicked
        notesImageButton = findViewById(R.id.notes_button);
        notesImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the intent that lunched this activity
                Intent intent = getIntent();
                mCurrentCourseUri = intent.getData();

                // if courseNoteChecker is true get AddNote Activity, if false, get Note Details activity
                if (courseNoteChecker()) {
                    // Create new intent to go to AddNote activity
                    Intent newIntent = new Intent(CourseDetails.this, AddNote.class);
                    // Set the data on the intent
                    newIntent.putExtra("courseId", currentCourseId);
                    // Launch the activity to add the note to the course
                    startActivity(newIntent);
                }
                else {
                    // Create new intent to go to Note Details activity
                    Intent newIntent = new Intent(CourseDetails.this, NoteDetails.class);
                    // Get the noteId
                    getNoteId();
                    // Form the content URI that represents the note that will be viewed on button click
                    Uri currentNoteUri = ContentUris.withAppendedId(NoteEntry.CONTENT_URI_NOTES, noteId);
                    // Set the data on the intent
                    newIntent.setData(currentNoteUri);
                    // Get Id from course to pass on to the Note Details activity
                    newIntent.putExtra("courseId", currentCourseId);
                    // Launch the activity to add the note to the course
                    startActivity(newIntent);
                }
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

        // Initialize the loader to read the course data from the Database
        getLoaderManager().initLoader(EXISTING_COURSE_LOADER, null, this);
    }

    // Get id for note associated with course
    private void getNoteId() {
        //Define a selection
        String selection = NoteEntry.COLUMN_NOTES_ASSOCIATED_COURSE_ID + "=?";
        String[] selectionArgs = { String.valueOf((ContentUris.parseId(mCurrentCourseUri))) };
        // Get Cursor with the notes associated to this course
        Cursor cursor = getContentResolver().query(NoteEntry.CONTENT_URI_NOTES, null,
                selection, selectionArgs, null);
        // Get the data that we need to set the noteId
        if (cursor.moveToFirst()) {
            // Find the index of each note column we are interested in
            int noteIdColumnIndex = cursor.getColumnIndex(NoteEntry._ID);
            // Extract out the values from the Cursor for the given index
            noteId = cursor.getLong(noteIdColumnIndex);
        }
    }

    // Check whether the course DOES NOT has a note associated with it(true) or does(false)
    private boolean courseNoteChecker() {
        //Define a selection
        String selection = NoteEntry.COLUMN_NOTES_ASSOCIATED_COURSE_ID + "=?";
        String[] selectionArgs = { String.valueOf((ContentUris.parseId(mCurrentCourseUri))) };
        // Get Cursor with the notes associated to this course
        Cursor cursor = getContentResolver().query(NoteEntry.CONTENT_URI_NOTES, null,
                selection, selectionArgs, null);
        if (cursor.getCount() == 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_course_details.xml file
        getMenuInflater().inflate(R.menu.menu_course_details, menu);
        myMenu = menu;
        // Check to see if the course is completed or dropped, if true, don't display start course menu item
        if(checkCourseStatus()) {
            myMenu.findItem(R.id.action_start_course).setVisible(false);
        }
        return true;
    }

    // Declare what to do with the items in the menu when clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String status;
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete Course" menu option
            case R.id.action_delete_current_course:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_edit_current_course: 
                editCurrentCourse();
                return true;
            case R.id.action_enable_notifications:
                try {
                    enableNotificationsStart();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    enableNotificationsEnd();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.action_start_course:
                status = "In Progress";
                updateCourseStatus(status);
                // Disable/Enable Menu Items
                myMenu.findItem(R.id.action_start_course).setVisible(false);
                myMenu.findItem(R.id.action_mark_course_complete).setVisible(true);
                myMenu.findItem(R.id.action_drop_course).setVisible(true);
                return true;
            case R.id.action_mark_course_complete:
                status = "Completed";
                updateCourseStatus(status);
                // Disable Menu Items
                myMenu.findItem(R.id.action_mark_course_complete).setVisible(false);
                myMenu.findItem(R.id.action_drop_course).setVisible(false);
                return true;
            case R.id.action_drop_course:
                status = "Dropped";
                updateCourseStatus(status);
                // Disable Menu Items
                myMenu.findItem(R.id.action_mark_course_complete).setVisible(false);
                myMenu.findItem(R.id.action_drop_course).setVisible(false);
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkCourseStatus() {
        String completed = "Completed";
        String dropped = "Dropped";
        Cursor cursor = getContentResolver().query(mCurrentCourseUri, null, null,
                null, null);
        if (cursor.moveToFirst()) {
            // Get Column Index
            int courseStatusColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_STATUS);
            // Extract out the value from the Cursor for the given index
            String courseStatus = cursor.getString(courseStatusColumnIndex);
            return courseStatus.equals(completed) | courseStatus.equals(dropped);
        }
        return false;
    }

    private void updateCourseStatus(String status) {
        // Set the value to update the course
        ContentValues values = new ContentValues();
        values.put(CourseEntry.COLUMN_COURSE_STATUS, status);
        // Call the ContentResolver to update the course
        int updatedRow = getContentResolver().update(mCurrentCourseUri, values, null, null);
    }

    private void enableNotificationsStart() throws ParseException {
        Intent intent = new Intent(CourseDetails.this, ReminderBroadcast.class);
        intent.putExtra("key", "Your Course Starts Today");
        intent.putExtra("channel_id", "start");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(CourseDetails.this, 0, intent,  PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date stringDate = simpleDateFormat.parse(mCourseStartDateText.getText().toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(stringDate);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0,0);

        long dateMillis = cal.getTimeInMillis();

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                dateMillis,
                pendingIntent);
    }

    private void enableNotificationsEnd() throws ParseException {
        Intent endIntent = new Intent(CourseDetails.this, ReminderBroadcast.class);
        endIntent.putExtra("key", "Your Course Ends Today");
        endIntent.putExtra("channel_id", "end");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(CourseDetails.this, 1, endIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date stringDate = simpleDateFormat.parse(mCourseEndDateText.getText().toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(stringDate);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0,0);

        long dateMillis = cal.getTimeInMillis();

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                dateMillis,
                pendingIntent);
    }

    private void editCurrentCourse() {
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
    }

    // Create the delete confirmation dialog message when deleting a course
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

    // Perform the deletion of the course in the database
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
