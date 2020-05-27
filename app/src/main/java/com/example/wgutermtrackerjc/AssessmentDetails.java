package com.example.wgutermtrackerjc;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.example.wgutermtrackerjc.data.DBContract.NoteEntry;
import com.example.wgutermtrackerjc.data.DBContract.AssessmentEntry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AssessmentDetails extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the assessment course data loader
    public static final int EXISTING_ASSESSMENT_LOADER = 0;

    // Content URI for existing assessment
    private Uri mCurrentAssessmentUri;

    // Hold the courseId that launched this activity and noteId
    long currentCourseId, noteId;

    // Get ImageButton for Notes
    ImageButton notesImageButton;

    // TextView field that holds the assessment name
    private TextView mAssessmentNameText;
    // TextView field that holds the assessment due date
    private TextView mAssessmentDueDateText;
    // TextView field that hols the assessment description
    private TextView mAssessmentDescriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the intent that launched this activity
        Intent intent = getIntent();
        mCurrentAssessmentUri = intent.getData();

        // Get the course ID
        currentCourseId = intent.getLongExtra("courseId", -1);

        // Send information to Add/Details Note activity when button is clicked
        notesImageButton = findViewById(R.id.notes_button);
        notesImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if courseNoteChecker is true get AddNote Activity, if false, get Note Details activity
                if (courseNoteChecker()) {
                    // Create new intent to go to AddNote activity
                    Intent newIntent = new Intent(AssessmentDetails.this, AddNote.class);
                    // Set the data on the intent
                    newIntent.putExtra("courseId", currentCourseId);
                    // Launch the activity to add the note to the course
                    startActivity(newIntent);
                }
                else {
                    // Create new intent to go to Note Details activity
                    Intent newIntent = new Intent(AssessmentDetails.this, NoteDetails.class);
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
        mAssessmentNameText = (TextView) findViewById(R.id.assessment_name);
        mAssessmentDueDateText = (TextView) findViewById(R.id.assessment_item_due_date);
        mAssessmentDescriptionText = (TextView) findViewById(R.id.assessment_item_description);

        // Initialize the loader to read the assessment data from the Database
        getLoaderManager().initLoader(EXISTING_ASSESSMENT_LOADER, null, this);
    }

    // Get id for note associated with course
    private void getNoteId() {
        //Define a selection
        String selection = NoteEntry.COLUMN_NOTES_ASSOCIATED_COURSE_ID + "=?";
        String[] selectionArgs = { String.valueOf(currentCourseId) };
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
        String[] selectionArgs = { String.valueOf(currentCourseId) };
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
        // Inflate the menu options from the res/menu/menu_assessment_details.xml file
        getMenuInflater().inflate(R.menu.menu_assessment_details, menu);
        return true;
    }

    // Declare what to do with the items in the menu when clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete Assessment" menu option
            case R.id.action_delete_current_assessment:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_edit_current_assessment:
                // Get the intent that launched this activity
                Intent intent = getIntent();
                // for the content URI that was sent from the AssessmentList
                mCurrentAssessmentUri = intent.getData();
                // Create new intent to go to the AddAssessment activity
                Intent newIntent = new Intent(AssessmentDetails.this, AddAssessment.class);
                // Set the URI on the data files of the Intent
                newIntent.setData(mCurrentAssessmentUri);
                // Get the course Id
                currentCourseId = intent.getLongExtra("courseId", -1);
                // Get Id from course to pass on to the AddAssessment activity
                newIntent.putExtra("courseId", currentCourseId);
                // Launch the activity to edit the data for the current Assessment
                startActivity(newIntent);
                return true;
            case R.id.action_enable_notifications:
                try {
                    enableNotificationsStart();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void enableNotificationsStart() throws ParseException {
        Intent intent = new Intent(AssessmentDetails.this, ReminderBroadcast.class);
        intent.putExtra("key", "Your projected assessment due date is today");
        intent.putExtra("channel_id", "assessmentDue");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AssessmentDetails.this, 2, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date stringDate = simpleDateFormat.parse(mAssessmentDueDateText.getText().toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(stringDate);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0);

        long dateMillis = cal.getTimeInMillis();

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                dateMillis,
                pendingIntent);

        Toast.makeText(AssessmentDetails.this, "Notification Set", Toast.LENGTH_SHORT).show();
    }

    // Create the delete confirmation dialog message when deleting an assessment
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this assessment?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteAssessment();
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

    // Perform the deletion of the assessment in the database
    private void deleteAssessment() {
        // Only perform the deletion if there is an existing assessment
        if (mCurrentAssessmentUri != null) {
            // Call the ContentResolver to delete the course at the given Uri
            int rowsDeleted = getContentResolver().delete(mCurrentAssessmentUri, null, null);
        }
        // Close the activity
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This loader will execute the ContentProviders' query method on a background thread
        return new CursorLoader(this,
                mCurrentAssessmentUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update AssessmentCursorAdapter with this new cursor containing update assessment data
        if(cursor.moveToFirst()) {
            // Find the index of each assessment column we are interested in
            int nameColumnIndex = cursor.getColumnIndex(AssessmentEntry.COLUMN_ASSESSMENT_NAME);
            int dueDateColumnIndex = cursor.getColumnIndex(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE);
            int descriptionColumnIndex = cursor.getColumnIndex(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION);

            // Extract out the values form the Cursor for the given index
            String name = cursor.getString(nameColumnIndex);
            String dueDate = cursor.getString(dueDateColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);

            // Update the TextViews on the screen with the values from the Database
            mAssessmentNameText.setText(name);
            mAssessmentDueDateText.setText(dueDate);
            mAssessmentDescriptionText.setText(description);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
