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

import com.example.wgutermtrackerjc.data.DBContract.TermEntry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddTerm extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the term data loader
    public static final int EXISTING_TERM_LOADER = 0;

    // Content URI for the existing term (null if it's a new term)
    private Uri mCurrentTermUri;
    // EditText field to enter term name
    private EditText mTermNameEditText;
    // EditText field to enter the term start date
    private EditText mTermStartDateEditText;
    // EditText field to enter the term end date
    private EditText mTermEndDateEditText;
    // Button to save the term
    private Button mTermSaveButton;

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
        setContentView(R.layout.activity_add_term);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Examine the intent that launched this activity in order to figure out if a term is
        // being created or edited.
        Intent intent = getIntent();
        mCurrentTermUri = intent.getData();

        // If the intent DOES NOT contain a term content URI, then we know that we are creating
        //  a new term.
        if (mCurrentTermUri == null) {
            // Set the title to add a term indicating creating a new term
            setTitle("Add a Term");
        }
        else {
            // If not null, we must then be editing a term so set the title
            setTitle("Edit Term");
            // Initialize a loader to read the term data from the database and display the
            // values in the editor.
            getLoaderManager().initLoader(EXISTING_TERM_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mTermNameEditText = (EditText) findViewById(R.id.edit_term_name);
        mTermStartDateEditText = (EditText) findViewById(R.id.edit_term_start_date);
        mTermEndDateEditText = (EditText) findViewById(R.id.edit_term_end_date);
        mTermSaveButton = (Button) findViewById(R.id.button_save_term);

        // Setup OnTouchListeners on all the input fields to determine if the user has touched,
        // or modified them.
        mTermNameEditText.setOnTouchListener(mTouchListener);
        mTermStartDateEditText.setOnTouchListener(mTouchListener);
        mTermEndDateEditText.setOnTouchListener(mTouchListener);

        // Create onClickListener for save button to call insertTerm
        mTermSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            insertTerm();
            finish();
            }
        });

    }

    private void insertTerm() {
        // Read from the input fields and create strings from them
        String termNameString = mTermNameEditText.getText().toString().trim();
        if (termNameString.isEmpty()) {
            termNameString = null;
        }
        String termStartDateString = mTermStartDateEditText.getText().toString().trim();
        if(termStartDateString.isEmpty()) {
            termStartDateString = null;
        }
        String termEndDateString = mTermEndDateEditText.getText().toString().trim();
        if(termEndDateString.isEmpty()) {
            termEndDateString = null;
        }

        // Create the ContentValues object where column names are the keys,
        // and the input fields for the term are the values
        ContentValues values = new ContentValues();
        values.put(TermEntry.COLUMN_TERM_NAME, termNameString);
        values.put(TermEntry.COLUMN_TERM_START_DATE, termStartDateString);
        values.put(TermEntry.COLUMN_TERM_END_DATE, termEndDateString);

        // Determine if this is new or existing term by checking the mCurrentTermUri
        // If the URI is empty, it must be a new one
        if (mCurrentTermUri == null) {
            // Insert a new row into the database and return the ID of the new row
            //long newRowId = db.insert(TermEntry.TABLE_NAME, null, values);
            Uri newUri = getContentResolver().insert(TermEntry.CONTENT_URI_TERMS, values);
        }
        else {
            // if not null, then it is an existing term and we have to update it
            int rowsAffected = getContentResolver().update(mCurrentTermUri, values, null, null);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This loader will execute the ContentProviders query method on a background thread
        return new CursorLoader(this,
                mCurrentTermUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            //Find the index of each term column we are interested in
            int nameColumnIndex = cursor.getColumnIndex(TermEntry.COLUMN_TERM_NAME);
            int startColumnIndex = cursor.getColumnIndex(TermEntry.COLUMN_TERM_START_DATE);
            int endColumnIndex = cursor.getColumnIndex(TermEntry.COLUMN_TERM_END_DATE);

            // Extract out the values from the Cursor for the given index
            String name = cursor.getString(nameColumnIndex);
            String start = cursor.getString(startColumnIndex);
            String end = cursor.getString(endColumnIndex);

            // Update the TextView's on the screen with the values from the Database
            mTermNameEditText.setText(name);
            mTermStartDateEditText.setText(start);
            mTermEndDateEditText.setText(end);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
