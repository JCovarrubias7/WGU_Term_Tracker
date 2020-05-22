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
import com.example.wgutermtrackerjc.data.DBContract.NoteEntry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class NoteDetails extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the note course data loader
    public static final int EXISTING_NOTE_LOADER = 0;

    // Content URI for existing note
    private Uri mCurrentNoteUri;

    // Hold the courseID that came to this activity
    long currentCourseId;

    // TextView field that holds the course name
    private TextView mCourseNameText;
    // TextView field that holds the note
    private TextView mCourseNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the intent that launched this activity
        Intent intent = getIntent();
        mCurrentNoteUri = intent.getData();

        // Get the Note ID
        currentCourseId = intent.getLongExtra("courseId", -1);

        // Find views that we want to set the values from the intent
        mCourseNote = (TextView) findViewById(R.id.course_note_data);
        mCourseNameText = (TextView) findViewById(R.id.note_details_course_name);

        // Set the Course Name to display
        setCourseName();

        // Initialize the loader to read the note data from the Database
        getLoaderManager().initLoader(EXISTING_NOTE_LOADER, null, this);
    }

    private void setCourseName() {
        // Define a projection that specifies the columns from the table we care about
        String[] projection = {
                CourseEntry._ID,
                CourseEntry.COLUMN_COURSE_NAME};

        // Define a selection
        String selection = CourseEntry._ID + "=?";
        String[] selectionArgs = { String.valueOf(currentCourseId) };
        // Get the cursor with the course data
        Cursor cursor = getContentResolver().query(CourseEntry.CONTENT_URI_COURSES, projection,
                selection, selectionArgs, null);
        if (cursor.moveToFirst()) {
            // Find the index of each course column we are interested in
            int courseNameColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_NAME);
            // Extract the values from the Cursor for the given index
            mCourseNameText.setText(cursor.getString(courseNameColumnIndex));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_note_details.xml file
        getMenuInflater().inflate(R.menu.menu_note_details, menu);
        return true;
    }

    // Declare what to do with the items in the menu when clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete Note" menu option
            case R.id.action_delete_current_note:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_edit_current_note:
                // Get the intent that launched this activity
                Intent intent = getIntent();
                // for the content URI that was sent from the CourseDetails
                mCurrentNoteUri = intent.getData();
                // Create new intent to go to the AddNote activity
                Intent newIntent = new Intent(NoteDetails.this, AddNote.class);
                // Set the URI on the data files of the Intent
                newIntent.setData(mCurrentNoteUri);
                // Get the courseID
                currentCourseId = intent.getLongExtra("courseId", -1);
                // Get Id from course to pass on the AddNote activity
                newIntent.putExtra("courseId", currentCourseId);
                // Launch the activity to edit the data fro the current Note
                startActivity(newIntent);
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Create the delete confirmation dialog message when deleting a note
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this note?");
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

    // Perform the deletion of the note in the database
    private void deleteAssessment() {
        // Only perform the deletion if there is an existing note
        if (mCurrentNoteUri != null) {
            // Call the ContentResolver to delete the note at the given Uri
            int rowsDeleted = getContentResolver().delete(mCurrentNoteUri, null, null);
        }
        // Close the activity
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This loader will execute the ContentProviders' query method on a background thread
        return new CursorLoader(this,
                mCurrentNoteUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update the fields with the note data
        if (cursor.moveToFirst()) {
            // Find the index of each note column we are interested in
            int noteColumnIndex = cursor.getColumnIndex(NoteEntry.COLUMN_NOTES);
            // Extract out the values from the Cursor for the given index
            String note = cursor.getString(noteColumnIndex);
            // Update the EditView on the screen with the values from the Database
            mCourseNote.setText(note);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
