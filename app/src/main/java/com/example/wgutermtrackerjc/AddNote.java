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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wgutermtrackerjc.data.DBContract.CourseEntry;
import com.example.wgutermtrackerjc.data.DBContract.NoteEntry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddNote extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the note data loader
    public static final int EXISTING_NOTE_LOADER = 0;

    // Hold the courseId that launched this activity;
    long currentCourseId;

    // Content URI for the existing note (null if it's a new note)
    private Uri mCurrentNoteUri;
    // TextView field that holds the course name
    private TextView mCourseNameText;
    // EditText filed to enter note
    private EditText mCourseNote;
    // Button to save the note
    private Button mNoteSaveButton;

    // Boolean flag that keeps track of whether the assessment has been edited(true) or note (false)
    private boolean mNoteHasChanged;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            mNoteHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Examine the intent that launched this activity in order to figure out if a term is
        // being created or edited.
        Intent intent = getIntent();
        mCurrentNoteUri = intent.getData();

        // Get the data that was placed in the intent.putExtra
        currentCourseId = intent.getLongExtra("courseId", -1);

        // If the intent DOES NOT contain a note content URI, then we know that we are creating a new note
        if (mCurrentNoteUri == null) {
            // Set the title of to add a note indicating creating a new note
            setTitle("Add a Course Note");
        } else {
            // If not null, we must then be editing a note so se the title
            setTitle("Edit Course Note");
            // Initialize a loader to read the note data from the database and display the values in the editor
            getLoaderManager().initLoader(EXISTING_NOTE_LOADER, null, this);
        }

        // Find all the relevant views that we will need to read user input from
        mCourseNameText = (TextView) findViewById(R.id.edit_note_course_name);
        mCourseNote = (EditText) findViewById(R.id.edit_note_course_note);
        mNoteSaveButton = (Button) findViewById(R.id.edit_note_save_button);

        // Set the Course Name to display
        setCourseName();

        // Setup OnToucheListeners on all the input files to determine if the user has touched,
        // or modified them
        mCourseNote.setOnTouchListener(mTouchListener);

        // Create onClickListener for save button to call insertNote
        mNoteSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertNote();
                finish();
            }
        });

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

    private void insertNote() {
        // Get Course Id and convert to string
        String courseIdString = String.valueOf(currentCourseId);
        // Read from the input fields and create strings from them
        String courseNoteString = mCourseNote.getText().toString().trim();
        if (courseNoteString.isEmpty()) {
            courseNoteString = null;
        }

        // Create the ContentValues object where column names are the keys,
        // and the input fields fro the note are the values
        ContentValues values = new ContentValues();
        values.put(NoteEntry.COLUMN_NOTES_ASSOCIATED_COURSE_ID, courseIdString);
        values.put(NoteEntry.COLUMN_NOTES, courseNoteString);

        // Determine if this is new or existing assessment b checking the mCurrentNoteUri
        // If URI is empty, it must be a new one
        if (mCurrentNoteUri == null) {
            // Insert a new row into the database and return the ID of the new row
            Uri newUri = getContentResolver().insert(NoteEntry.CONTENT_URI_NOTES, values);
        } else {
            // If not null, then it is an existing note and we have to update it
            int rowsAffected = getContentResolver().update(mCurrentNoteUri, values, null, null);
        }
    }

    @Override
    public void onBackPressed() {
        // If the note hasn't changed, continue with handling back button press
        if (!mNoteHasChanged) {
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
                mCurrentNoteUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
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
