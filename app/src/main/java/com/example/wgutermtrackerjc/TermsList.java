package com.example.wgutermtrackerjc;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.example.wgutermtrackerjc.data.DBContract.TermEntry;
import com.example.wgutermtrackerjc.data.DBContract.CourseEntry;
import com.example.wgutermtrackerjc.data.DBContract.AssessmentEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class TermsList extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifies the Loader being used
    public static final int TERM_LOADER =0;

    // Adapter in all the call back methods
    TermCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TermsList.this, AddTerm.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the term data
        ListView termListView = (ListView) findViewById(R.id.all_terms_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items
        View emptyView = findViewById(R.id.empty_term_view);
        termListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of term data in the cursor
        mCursorAdapter = new TermCursorAdapter(this, null);
        termListView.setAdapter(mCursorAdapter);

        // Setup the item click listener on the list items
        termListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to TermDetails
                Intent intent = new Intent(TermsList.this, TermDetails.class);
                // Form the content URI that represents the term that was clicked on
                Uri currentTermUri = ContentUris.withAppendedId(TermEntry.CONTENT_URI_TERMS, id);
                // Set the URI on the data fields of the Intent
                intent.setData(currentTermUri);
                // Launch the activity to display the data for the current Term
                startActivity(intent);
            }
        });

        // Initialized the CursorLoader
        getLoaderManager().initLoader(TERM_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_terms_list.xml file
        getMenuInflater().inflate(R.menu.menu_terms_list, menu);
        return true;
    }

    // Declare what to do with the items in the menu when clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert test data" menu option
            case R.id.action_insert_test_data:
                insertTestData();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_terms:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about
        String[] projection = {
                TermEntry._ID,
                TermEntry.COLUMN_TERM_NAME,
                TermEntry.COLUMN_TERM_START_DATE,
                TermEntry.COLUMN_TERM_END_DATE};

        // This loader wil execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                TermEntry.CONTENT_URI_TERMS,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update TermCursorAdapter with this new cursor containing updated term data
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    // Create the delete confirmation dialog message when deleting all terms
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete all terms?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteAllTerms();
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

    // Helper method to delete all the terms in the DataBase
    private void deleteAllTerms() {
        // Only perform the delete if there is an item in the terms table
        if(TermEntry.CONTENT_URI_TERMS != null) {
            // Define a projection that specifies the columns from the table we care about
            String[] projection = {
                    CourseEntry._ID,
                    CourseEntry.COLUMN_ASSOCIATED_TERM_ID,
                    CourseEntry.COLUMN_COURSE_NAME,
                    CourseEntry.COLUMN_COURSE_START,
                    CourseEntry.COLUMN_COURSE_END,
                    CourseEntry.COLUMN_COURSE_STATUS};

            // Get all Courses
            Cursor cursor = getContentResolver().query(CourseEntry.CONTENT_URI_COURSES, null,
                    null, null, null);
            if (cursor.getCount() == 0) {
                // Call the ContentResolver to delete the term at the given URI.
                int rowsDeleted = getContentResolver().delete(TermEntry.CONTENT_URI_TERMS, null, null);
            }
            else {
                Toast.makeText(this, "Terms cannot be deleted with courses still associated with them",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Create the data that will be inserted from the menu option Insert Test Data.
    private void insertTestData() {
        // Create a ContentValues object where column names are the keys,
        // and Term attributes are the the values.
        ContentValues values = new ContentValues();
        values.put(TermEntry.COLUMN_TERM_NAME, "Term 1 - Summer");
        values.put(TermEntry.COLUMN_TERM_START_DATE, "05/01/2020");
        values.put(TermEntry.COLUMN_TERM_END_DATE, "10/31/2020");

        ContentValues values1 = new ContentValues();
        values1.put(TermEntry.COLUMN_TERM_NAME, "Term 2 - Winter");
        values1.put(TermEntry.COLUMN_TERM_START_DATE, "11/01/2020");
        values1.put(TermEntry.COLUMN_TERM_END_DATE, "4/30/2021");

        ContentValues values2 = new ContentValues();
        values2.put(TermEntry.COLUMN_TERM_NAME, "Term 3 - Summer");
        values2.put(TermEntry.COLUMN_TERM_START_DATE, "05/01/2021");
        values2.put(TermEntry.COLUMN_TERM_END_DATE, "10/31/2021");

        ContentValues values3 = new ContentValues();
        values3.put(TermEntry.COLUMN_TERM_NAME, "Term 4 - Winter");
        values3.put(TermEntry.COLUMN_TERM_START_DATE, "11/01/2021");
        values3.put(TermEntry.COLUMN_TERM_END_DATE, "4/30/2022");

        // Insert a new row into the database and return the ID of the new row
        //long newRowId = db.insert(TermEntry.TABLE_NAME, null, values);
        getContentResolver().insert(TermEntry.CONTENT_URI_TERMS, values);
        getContentResolver().insert(TermEntry.CONTENT_URI_TERMS, values1);
        getContentResolver().insert(TermEntry.CONTENT_URI_TERMS, values2);
        getContentResolver().insert(TermEntry.CONTENT_URI_TERMS, values3);

        // Create a ContentValues object where column names are the keys,
        // and Course attributes are the the values.
        ContentValues courseValues = new ContentValues();
        courseValues.put(CourseEntry.COLUMN_ASSOCIATED_TERM_ID, 1);
        courseValues.put(CourseEntry.COLUMN_COURSE_NAME, "C182 - Introduction to IT");
        courseValues.put(CourseEntry.COLUMN_COURSE_START, "05/01/2020");
        courseValues.put(CourseEntry.COLUMN_COURSE_END, "06/10/2020");
        courseValues.put(CourseEntry.COLUMN_COURSE_STATUS, "Plan to Take");
        courseValues.put(CourseEntry.COLUMN_COURSE_MENTOR_NAME, "James Test");
        courseValues.put(CourseEntry.COLUMN_COURSE_MENTOR_PHONE, "773-123-1234");
        courseValues.put(CourseEntry.COLUMN_COURSE_MENTOR_EMAIL, "JamesT@email.com");

        ContentValues courseValues1 = new ContentValues();
        courseValues1.put(CourseEntry.COLUMN_ASSOCIATED_TERM_ID, 1);
        courseValues1.put(CourseEntry.COLUMN_COURSE_NAME, "C393 - IT Foundations");
        courseValues1.put(CourseEntry.COLUMN_COURSE_START, "06/11/2020");
        courseValues1.put(CourseEntry.COLUMN_COURSE_END, "06/30/2020");
        courseValues1.put(CourseEntry.COLUMN_COURSE_STATUS, "Plan to Take");
        courseValues1.put(CourseEntry.COLUMN_COURSE_MENTOR_NAME, "Juan Test");
        courseValues1.put(CourseEntry.COLUMN_COURSE_MENTOR_PHONE, "773-123-1234");
        courseValues1.put(CourseEntry.COLUMN_COURSE_MENTOR_EMAIL, "JuanT@email.com");

        ContentValues courseValues2 = new ContentValues();
        courseValues2.put(CourseEntry.COLUMN_ASSOCIATED_TERM_ID, 1);
        courseValues2.put(CourseEntry.COLUMN_COURSE_NAME, "C394 - IT Applications");
        courseValues2.put(CourseEntry.COLUMN_COURSE_START, "07/01/2020");
        courseValues2.put(CourseEntry.COLUMN_COURSE_END, "09/20/2020");
        courseValues2.put(CourseEntry.COLUMN_COURSE_STATUS, "Plan to Take");
        courseValues2.put(CourseEntry.COLUMN_COURSE_MENTOR_NAME, "Juan Test");
        courseValues2.put(CourseEntry.COLUMN_COURSE_MENTOR_PHONE, "773-123-1234");
        courseValues2.put(CourseEntry.COLUMN_COURSE_MENTOR_EMAIL, "JuanT@email.com");

        ContentValues courseValues3 = new ContentValues();
        courseValues3.put(CourseEntry.COLUMN_ASSOCIATED_TERM_ID, 2);
        courseValues3.put(CourseEntry.COLUMN_COURSE_NAME, "C464 - Introduction to Communication");
        courseValues3.put(CourseEntry.COLUMN_COURSE_START, "11/01/2020");
        courseValues3.put(CourseEntry.COLUMN_COURSE_END, "11/20/2020");
        courseValues3.put(CourseEntry.COLUMN_COURSE_STATUS, "Plan to Take");
        courseValues3.put(CourseEntry.COLUMN_COURSE_MENTOR_NAME, "Maria E. Test");
        courseValues3.put(CourseEntry.COLUMN_COURSE_MENTOR_PHONE, "773-123-1234");
        courseValues3.put(CourseEntry.COLUMN_COURSE_MENTOR_EMAIL, "MariaTest123@email.com");

        ContentValues courseValues4 = new ContentValues();
        courseValues4.put(CourseEntry.COLUMN_ASSOCIATED_TERM_ID, 2);
        courseValues4.put(CourseEntry.COLUMN_COURSE_NAME, "C779 - Web Development Foundations");
        courseValues4.put(CourseEntry.COLUMN_COURSE_START, "11/21/2020");
        courseValues4.put(CourseEntry.COLUMN_COURSE_END, "12/20/2020");
        courseValues4.put(CourseEntry.COLUMN_COURSE_STATUS, "Plan to Take");
        courseValues4.put(CourseEntry.COLUMN_COURSE_MENTOR_NAME, "Webber Test");
        courseValues4.put(CourseEntry.COLUMN_COURSE_MENTOR_PHONE, "773-123-1234");
        courseValues4.put(CourseEntry.COLUMN_COURSE_MENTOR_EMAIL, "WebberT@email.com");

        ContentValues courseValues5 = new ContentValues();
        courseValues5.put(CourseEntry.COLUMN_ASSOCIATED_TERM_ID, 2);
        courseValues5.put(CourseEntry.COLUMN_COURSE_NAME, "C777 - Web Development Applications");
        courseValues5.put(CourseEntry.COLUMN_COURSE_START, "12/21/2020");
        courseValues5.put(CourseEntry.COLUMN_COURSE_END, "01/30/2021");
        courseValues5.put(CourseEntry.COLUMN_COURSE_STATUS, "Plan to Take");
        courseValues5.put(CourseEntry.COLUMN_COURSE_MENTOR_NAME, "Webber Test");
        courseValues5.put(CourseEntry.COLUMN_COURSE_MENTOR_PHONE, "773-123-1234");
        courseValues5.put(CourseEntry.COLUMN_COURSE_MENTOR_EMAIL, "WebberT@email.com");

        ContentValues courseValues6 = new ContentValues();
        courseValues6.put(CourseEntry.COLUMN_ASSOCIATED_TERM_ID, 2);
        courseValues6.put(CourseEntry.COLUMN_COURSE_NAME, "C278 - College Algebra");
        courseValues6.put(CourseEntry.COLUMN_COURSE_START, "01/31/2021");
        courseValues6.put(CourseEntry.COLUMN_COURSE_END, "03/30/2021");
        courseValues6.put(CourseEntry.COLUMN_COURSE_STATUS, "Plan to Take");
        courseValues6.put(CourseEntry.COLUMN_COURSE_MENTOR_NAME, "Lucy Test");
        courseValues6.put(CourseEntry.COLUMN_COURSE_MENTOR_PHONE, "773-123-1234");
        courseValues6.put(CourseEntry.COLUMN_COURSE_MENTOR_EMAIL, "LucyT@email.com");

        ContentValues courseValues7 = new ContentValues();
        courseValues7.put(CourseEntry.COLUMN_ASSOCIATED_TERM_ID, 3);
        courseValues7.put(CourseEntry.COLUMN_COURSE_NAME, "C173 - Scripting and Programming Foundations");
        courseValues7.put(CourseEntry.COLUMN_COURSE_START, "05/01/2021");
        courseValues7.put(CourseEntry.COLUMN_COURSE_END, "06/01/2021");
        courseValues7.put(CourseEntry.COLUMN_COURSE_STATUS, "Plan to Take");
        courseValues7.put(CourseEntry.COLUMN_COURSE_MENTOR_NAME, "Christina Test");
        courseValues7.put(CourseEntry.COLUMN_COURSE_MENTOR_PHONE, "773-123-1234");
        courseValues7.put(CourseEntry.COLUMN_COURSE_MENTOR_EMAIL, "ChristinaT@email.com");

        ContentValues courseValues8 = new ContentValues();
        courseValues8.put(CourseEntry.COLUMN_ASSOCIATED_TERM_ID, 3);
        courseValues8.put(CourseEntry.COLUMN_COURSE_NAME, "C867 - Scripting and Programming Applications");
        courseValues8.put(CourseEntry.COLUMN_COURSE_START, "06/02/2021");
        courseValues8.put(CourseEntry.COLUMN_COURSE_END, "08/02/2021");
        courseValues8.put(CourseEntry.COLUMN_COURSE_STATUS, "Plan to Take");
        courseValues8.put(CourseEntry.COLUMN_COURSE_MENTOR_NAME, "Christina Test");
        courseValues8.put(CourseEntry.COLUMN_COURSE_MENTOR_PHONE, "773-123-1234");
        courseValues8.put(CourseEntry.COLUMN_COURSE_MENTOR_EMAIL, "ChristinaT@email.com");

        ContentValues courseValues9 = new ContentValues();
        courseValues9.put(CourseEntry.COLUMN_ASSOCIATED_TERM_ID, 3);
        courseValues9.put(CourseEntry.COLUMN_COURSE_NAME, "C773 - User Interface Design");
        courseValues9.put(CourseEntry.COLUMN_COURSE_START, "08/03/2021");
        courseValues9.put(CourseEntry.COLUMN_COURSE_END, "09/15/2021");
        courseValues9.put(CourseEntry.COLUMN_COURSE_STATUS, "Plan to Take");
        courseValues9.put(CourseEntry.COLUMN_COURSE_MENTOR_NAME, "Wilson Test");
        courseValues9.put(CourseEntry.COLUMN_COURSE_MENTOR_PHONE, "773-123-1234");
        courseValues9.put(CourseEntry.COLUMN_COURSE_MENTOR_EMAIL, "Wilson123T@email.com");

        ContentValues courseValues10 = new ContentValues();
        courseValues10.put(CourseEntry.COLUMN_ASSOCIATED_TERM_ID, 3);
        courseValues10.put(CourseEntry.COLUMN_COURSE_NAME, "C846 - Business of IT Applications");
        courseValues10.put(CourseEntry.COLUMN_COURSE_START, "09/16/2021");
        courseValues10.put(CourseEntry.COLUMN_COURSE_END, "10/25/2021");
        courseValues10.put(CourseEntry.COLUMN_COURSE_STATUS, "Plan to Take");
        courseValues10.put(CourseEntry.COLUMN_COURSE_MENTOR_NAME, "Thomas Test");
        courseValues10.put(CourseEntry.COLUMN_COURSE_MENTOR_PHONE, "773-123-1234");
        courseValues10.put(CourseEntry.COLUMN_COURSE_MENTOR_EMAIL, "Thomas_T@email.com");

        ContentValues courseValues11 = new ContentValues();
        courseValues11.put(CourseEntry.COLUMN_ASSOCIATED_TERM_ID, 4);
        courseValues11.put(CourseEntry.COLUMN_COURSE_NAME, "C175 - Data Management Foundations");
        courseValues11.put(CourseEntry.COLUMN_COURSE_START, "11/01/2021");
        courseValues11.put(CourseEntry.COLUMN_COURSE_END, "11/21/2020");
        courseValues11.put(CourseEntry.COLUMN_COURSE_STATUS, "Plan to Take");
        courseValues11.put(CourseEntry.COLUMN_COURSE_MENTOR_NAME, "Carlos Test");
        courseValues11.put(CourseEntry.COLUMN_COURSE_MENTOR_PHONE, "773-123-1234");
        courseValues11.put(CourseEntry.COLUMN_COURSE_MENTOR_EMAIL, "Car-losT@email.com");

        ContentValues courseValues12 = new ContentValues();
        courseValues12.put(CourseEntry.COLUMN_ASSOCIATED_TERM_ID, 4);
        courseValues12.put(CourseEntry.COLUMN_COURSE_NAME, "C170 - Data Management Applications");
        courseValues12.put(CourseEntry.COLUMN_COURSE_START, "11/22/2021");
        courseValues12.put(CourseEntry.COLUMN_COURSE_END, "01/01/2022");
        courseValues12.put(CourseEntry.COLUMN_COURSE_STATUS, "Plan to Take");
        courseValues12.put(CourseEntry.COLUMN_COURSE_MENTOR_NAME, "Carlos Test");
        courseValues12.put(CourseEntry.COLUMN_COURSE_MENTOR_PHONE, "773-123-1234");
        courseValues12.put(CourseEntry.COLUMN_COURSE_MENTOR_EMAIL, "Car-losT@email.com");

        ContentValues courseValues13 = new ContentValues();
        courseValues13.put(CourseEntry.COLUMN_ASSOCIATED_TERM_ID, 4);
        courseValues13.put(CourseEntry.COLUMN_COURSE_NAME, "C482 - Software I");
        courseValues13.put(CourseEntry.COLUMN_COURSE_START, "01/02/2022");
        courseValues13.put(CourseEntry.COLUMN_COURSE_END, "02/02/2022");
        courseValues13.put(CourseEntry.COLUMN_COURSE_STATUS, "Plan to Take");
        courseValues13.put(CourseEntry.COLUMN_COURSE_MENTOR_NAME, "Gracie Test");
        courseValues13.put(CourseEntry.COLUMN_COURSE_MENTOR_PHONE, "773-123-1234");
        courseValues13.put(CourseEntry.COLUMN_COURSE_MENTOR_EMAIL, "GracieT@email.com");

        ContentValues courseValues14 = new ContentValues();
        courseValues14.put(CourseEntry.COLUMN_ASSOCIATED_TERM_ID, 4);
        courseValues14.put(CourseEntry.COLUMN_COURSE_NAME, "C195 - Software II Advanced Java Concepts");
        courseValues14.put(CourseEntry.COLUMN_COURSE_START, "02/03/2022");
        courseValues14.put(CourseEntry.COLUMN_COURSE_END, "03/10/2022");
        courseValues14.put(CourseEntry.COLUMN_COURSE_STATUS, "Plan to Take");
        courseValues14.put(CourseEntry.COLUMN_COURSE_MENTOR_NAME, "Gracie Test");
        courseValues14.put(CourseEntry.COLUMN_COURSE_MENTOR_PHONE, "773-123-1234");
        courseValues14.put(CourseEntry.COLUMN_COURSE_MENTOR_EMAIL, "GracieT@email.com");

        ContentValues courseValues15 = new ContentValues();
        courseValues15.put(CourseEntry.COLUMN_ASSOCIATED_TERM_ID, 4);
        courseValues15.put(CourseEntry.COLUMN_COURSE_NAME, "C868 - Software Development Capstone");
        courseValues15.put(CourseEntry.COLUMN_COURSE_START, "03/11/2022");
        courseValues15.put(CourseEntry.COLUMN_COURSE_END, "04/29/2022");
        courseValues15.put(CourseEntry.COLUMN_COURSE_STATUS, "Plan to Take");
        courseValues15.put(CourseEntry.COLUMN_COURSE_MENTOR_NAME, "Jorge Test");
        courseValues15.put(CourseEntry.COLUMN_COURSE_MENTOR_PHONE, "773-123-1234");
        courseValues15.put(CourseEntry.COLUMN_COURSE_MENTOR_EMAIL, "JorgeT@email.com");

        // Insert a new row into the database and return the ID of the new row
        getContentResolver().insert(CourseEntry.CONTENT_URI_COURSES, courseValues);
        getContentResolver().insert(CourseEntry.CONTENT_URI_COURSES, courseValues1);
        getContentResolver().insert(CourseEntry.CONTENT_URI_COURSES, courseValues2);
        getContentResolver().insert(CourseEntry.CONTENT_URI_COURSES, courseValues3);
        getContentResolver().insert(CourseEntry.CONTENT_URI_COURSES, courseValues4);
        getContentResolver().insert(CourseEntry.CONTENT_URI_COURSES, courseValues5);
        getContentResolver().insert(CourseEntry.CONTENT_URI_COURSES, courseValues6);
        getContentResolver().insert(CourseEntry.CONTENT_URI_COURSES, courseValues7);
        getContentResolver().insert(CourseEntry.CONTENT_URI_COURSES, courseValues8);
        getContentResolver().insert(CourseEntry.CONTENT_URI_COURSES, courseValues9);
        getContentResolver().insert(CourseEntry.CONTENT_URI_COURSES, courseValues10);
        getContentResolver().insert(CourseEntry.CONTENT_URI_COURSES, courseValues11);
        getContentResolver().insert(CourseEntry.CONTENT_URI_COURSES, courseValues12);
        getContentResolver().insert(CourseEntry.CONTENT_URI_COURSES, courseValues13);
        getContentResolver().insert(CourseEntry.CONTENT_URI_COURSES, courseValues14);
        getContentResolver().insert(CourseEntry.CONTENT_URI_COURSES, courseValues15);

        // Create a ContentValues object where column names are the keys,
        // and assessment attributes are the the values.
        ContentValues assessmentValues = new ContentValues();
        assessmentValues.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 1);
        assessmentValues.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Objective Assessment: Introduction to IT - GSC1");
        assessmentValues.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "06/10/2020");
        assessmentValues.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "Take a 72 question test through your computer " +
                "with a remote proctor. This test will have to be completed within 120 minutes.");

        ContentValues assessmentValues1 = new ContentValues();
        assessmentValues1.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 2);
        assessmentValues1.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Objective Assessment: Third Party Assessment - CompTIA A+ Part 1/2 - KEV1");
        assessmentValues1.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "06/30/2020");
        assessmentValues1.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "An exam taken at a facility to get certified " +
                "This course prepares you for one assessment, CompTIA A+ Exam 220-901.");

        ContentValues assessmentValues2 = new ContentValues();
        assessmentValues2.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 3);
        assessmentValues2.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Objective Assessment: CompTIA - A+ - KFV1");
        assessmentValues2.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "09/20/2020");
        assessmentValues2.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "An exam taken at a facility to get certified " +
                "This course prepares you for one assessment, CompTIA A+ Exam 220-902. This will complete teh CompTIA A+ certification");

        ContentValues assessmentValues3 = new ContentValues();
        assessmentValues3.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 4);
        assessmentValues3.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Objective Assessment: Introduction to Communication - HRC1");
        assessmentValues3.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "11/10/2020");
        assessmentValues3.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "Take a 70 question test through your computer " +
                "with a remote proctor. This test will have to be completed within 120 minutes.");

        ContentValues assessmentValues4 = new ContentValues();
        assessmentValues4.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 4);
        assessmentValues4.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Performance Assessment: Introduction to Communication Applications - FBT1");
        assessmentValues4.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "11/20/2020");
        assessmentValues4.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "Create a presentation and record yourself giving this presentation. " +
                "It has to be at least 5 minutes long and display all the principals of communications.");

        ContentValues assessmentValues5 = new ContentValues();
        assessmentValues5.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 5);
        assessmentValues5.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Objective Assessment: CIW - Site Development Associate - HCV1");
        assessmentValues5.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "12/20/2020");
        assessmentValues5.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "You will take the Site Development Associate " +
                "CIW 1D0-61B exam. The multiple-choice exam has 30 questions, requires a minimum score of 63.33% to pass, and has a 30-minute time limit.");

        ContentValues assessmentValues6 = new ContentValues();
        assessmentValues6.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 6);
        assessmentValues6.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Objective Assessment: CIW - Advanced HTML5 and CSS3 - FRV1");
        assessmentValues6.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "01/30/2021");
        assessmentValues6.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "You will take the Advanced HTML5 & CSS3 Specialist CIW exam. " +
                "The exam has 55 questions, requires a minimum score of 72.73% to pass, and has a 75 minute time limit.");

        ContentValues assessmentValues7 = new ContentValues();
        assessmentValues7.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 7);
        assessmentValues7.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Objective Assessment: College Algebra - CEC1");
        assessmentValues7.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "03/30/2021");
        assessmentValues7.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "Take a 50 question test through your computer " +
                "with a remote proctor. This test will have to be completed within 180 minutes.");

        ContentValues assessmentValues8 = new ContentValues();
        assessmentValues8.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 8);
        assessmentValues8.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Objective Assessment: Scripting and Programming - Foundations - CEO1");
        assessmentValues8.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "06/01/2021");
        assessmentValues8.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "Take a 70 question test through your computer " +
                "with a remote proctor. This test will have to be completed within 120 minutes.");

        ContentValues assessmentValues9 = new ContentValues();
        assessmentValues9.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 9);
        assessmentValues9.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Performance Assessment: Scripting and Programming - Applications - FPP1");
        assessmentValues9.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "08/02/2021");
        assessmentValues9.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "You are hired as a contractor to help a university migrate an " +
                "existing student system to a new platform using C++ language. Since the application already exists, its requirements exist as well, and " +
                "they are outlined in the next section. You are responsible for implementing the part of the system based on these requirements. A list of " +
                "data is provided as part of these requirements. This part of the system is responsible for reading and manipulating the provided data.");

        ContentValues assessmentValues10 = new ContentValues();
        assessmentValues10.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 10);
        assessmentValues10.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Objective Assessment: CIW - User Interface Designer - FPV1");
        assessmentValues10.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "09/15/2021");
        assessmentValues10.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "An exam taken at a facility to get certified " +
                "This course prepares you for one assessment, CIW User Interface Designer. You need a 74% to pass this test.");

        ContentValues assessmentValues11 = new ContentValues();
        assessmentValues11.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 11);
        assessmentValues11.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Objective Assessment: Axelos - ITIL Foundation Certification - FSV1");
        assessmentValues11.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "10/25/2021");
        assessmentValues11.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "An exam taken at a facility to get certified " +
                "This course prepares you for one assessment, ITIL Foundation.");

        ContentValues assessmentValues12 = new ContentValues();
        assessmentValues12.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 12);
        assessmentValues12.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Objective Assessment: Data Management - Foundations - FKO1");
        assessmentValues12.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "11/21/2021");
        assessmentValues12.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "Take a 50 question test through your computer " +
                "with a remote proctor. This test will have to be completed within 150 minutes.");

        ContentValues assessmentValues13 = new ContentValues();
        assessmentValues13.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 13);
        assessmentValues13.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Objective Assessment: Data Management - Applications - GSA1");
        assessmentValues13.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "01/01/2022");
        assessmentValues13.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "Take a 11 question test through your computer " +
                "with a remote proctor. This test will have to be completed within 120 minutes.");

        ContentValues assessmentValues14 = new ContentValues();
        assessmentValues14.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 13);
        assessmentValues14.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Objective Assessment: Data Management - Applications - FJO1");
        assessmentValues14.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "01/01/2022");
        assessmentValues14.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "Take a 58 question test through your computer " +
                "with a remote proctor. This test will have to be completed within 150 minutes.");

        ContentValues assessmentValues15 = new ContentValues();
        assessmentValues15.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 14);
        assessmentValues15.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Performance Assessment: Software I - GYP1");
        assessmentValues15.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "02/02/2022");
        assessmentValues15.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "You are working for a small manufacturing " +
                "organization that has outgrown its current inventory system. They have been using a spreadsheet program to manually " +
                "enter inventory additions, deletions, and other data from a paper-based system but would now like you to develop a " +
                "more sophisticated inventory program using JAVA.");

        ContentValues assessmentValues16 = new ContentValues();
        assessmentValues16.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 15);
        assessmentValues16.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Performance Assessment: Software II - Advanced Java Concepts - GZP1");
        assessmentValues16.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "03/10/2022");
        assessmentValues16.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "You are working for a software company that has been contracted " +
                "to develop a scheduling desktop user interface application. The contract is with a global consulting organization that conducts business " +
                "in multiple languages and has main offices in Phoenix, Arizona; New York, New York; and London, England. The consulting organization has " +
                "provided a MySQL database that your application must pull data from. The database is used for other systems and therefore its structure " +
                "cannot be modified.");

        ContentValues assessmentValues17 = new ContentValues();
        assessmentValues17.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 16);
        assessmentValues17.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Performance Assessment: Software Development Capstone - EZP1");
        assessmentValues17.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "04/29/2022");
        assessmentValues17.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "TASK 1 : Create a software program using everything you " +
                "have learned through your degree program.");

        ContentValues assessmentValues18 = new ContentValues();
        assessmentValues18.put(AssessmentEntry.COLUMN_ASSOCIATED_COURSE_ID, 16);
        assessmentValues18.put(AssessmentEntry.COLUMN_ASSESSMENT_NAME, "Performance Assessment: Software Development Capstone - EZP1");
        assessmentValues18.put(AssessmentEntry.COLUMN_ASSESSMENT_DUE_DATE, "04/29/2022");
        assessmentValues18.put(AssessmentEntry.COLUMN_ASSESSMENT_DESCRIPTION, "TASK 2 : Create a detailed document that documents how " +
                "the software program you created in Task 1 works. It must be detailed and clear to the reader.");

        // Insert a new row into the database and return the ID of the new row
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues1);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues2);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues3);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues4);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues5);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues6);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues7);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues8);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues9);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues10);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues11);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues12);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues13);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues14);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues15);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues16);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues17);
        getContentResolver().insert(AssessmentEntry.CONTENT_URI_ASSESSMENTS, assessmentValues18);
    }

}
