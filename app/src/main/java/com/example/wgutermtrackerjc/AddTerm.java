package com.example.wgutermtrackerjc;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.wgutermtrackerjc.data.TermContract.TermEntry;
import com.example.wgutermtrackerjc.data.TermDBHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddTerm extends AppCompatActivity {

    // EditText field to enter term name
    private EditText mTermNameEditText;
    // EditText field to enter the term start date
    private EditText mTermStartDateEditText;
    // EditText field to enter the term end date
    private EditText mTermEndDateEditText;
    // Button to save the term
    private Button mTermSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find all relevant views that we will need to read user input from
        mTermNameEditText = (EditText) findViewById(R.id.edit_term_name);
        mTermStartDateEditText = (EditText) findViewById(R.id.edit_term_start_date);
        mTermEndDateEditText = (EditText) findViewById(R.id.edit_term_end_date);
        mTermSaveButton = (Button) findViewById(R.id.button_save_term);

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

        // Create database helper
        TermDBHelper mDbHelper = new TermDBHelper(this);
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create the ContentValues object where column names are the keys,
        // and the input fields for the term are the values
        ContentValues values = new ContentValues();
        values.put(TermEntry.COLUMN_TERM_NAME, termNameString);
        values.put(TermEntry.COLUMN_TERM_START_DATE, termStartDateString);
        values.put(TermEntry.COLUMN_TERM_END_DATE, termEndDateString);

        // Insert a new row into the database and return the ID of the new row
        long newRowId = db.insert(TermEntry.TABLE_NAME, null, values);

        // Show a toast message whether or not the insertion was successful
        if (newRowId == -1) {
            Toast.makeText(this, "Error saving the term", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Term saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }



}
