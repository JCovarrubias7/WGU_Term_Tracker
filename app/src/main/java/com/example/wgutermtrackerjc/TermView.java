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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class TermView extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the term data loader
    private static final int EXISTING_TERM_LOADER = 0;

    // Content URI for existing term
    private Uri mCurrentTermUri;

    // Adapter in all the call back methods
    CourseCursorAdapter mCursorAdapter;

    // Hold the term ID that launched this activity
    long currentTermId;

    // EditText field to enter term name
    private TextView mTermNameText;
    // EditText field to enter the term start date
    private TextView mTermStartDateText;
    // EditText field to enter the term end date
    private TextView mTermEndDateText;

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
        setContentView(R.layout.activity_term_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Get the intent that launched this activity
        Intent intent = getIntent();
        mCurrentTermUri = intent.getData();

        currentTermId = intent.getLongExtra("termId", -1);
        String termName = intent.getStringExtra("termName");
        String termStart = intent.getStringExtra("termStart");
        String termEnd = intent.getStringExtra("termEnd");

        // Find views that we want to modify in the list item layout
        mTermNameText = (TextView) findViewById(R.id.term_name);
        mTermStartDateText = (TextView) findViewById(R.id.term_start_date);
        mTermEndDateText = (TextView) findViewById(R.id.term_end_date);

        // Set the textView values to the values from the intent
        mTermNameText.setText(termName);
        mTermStartDateText.setText(termStart);
        mTermEndDateText.setText(termEnd);

        // Find the ListView which will be populated with the courses data
        ListView coursesListView = (ListView) findViewById(R.id.term_courses_list);

        // Setup an Adapter to create a list item for each row of course data in the cursor
        mCursorAdapter = new CourseCursorAdapter(this, null);
        coursesListView.setAdapter(mCursorAdapter);

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
                // for the content URI that was sent from the AllTerms
                mCurrentTermUri = intent.getData();
                // Create new intent to go to the AddTerm activity
                Intent newIntent = new Intent(TermView.this, AddTerm.class);
                // Set the URI on the data files of the Intent
                newIntent.setData(mCurrentTermUri);
                // Launch the activity to edit the data for the current term
                startActivity(newIntent);
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
                CourseEntry._ID,
                CourseEntry.COLUMN_ASSOCIATED_TERM_ID,
                CourseEntry.COLUMN_COURSE_NAME,
                CourseEntry.COLUMN_COURSE_START,
                CourseEntry.COLUMN_COURSE_END,
                CourseEntry.COLUMN_COURSE_STATUS};

        // Define a selection
        String selection = CourseEntry.COLUMN_ASSOCIATED_TERM_ID + "=?";
        String[] selectionArgs = {String.valueOf(currentTermId)};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                CourseEntry.CONTENT_URI_COURSES,
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
