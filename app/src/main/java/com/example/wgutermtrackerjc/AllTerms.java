package com.example.wgutermtrackerjc;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.wgutermtrackerjc.data.TermContract;
import com.example.wgutermtrackerjc.data.TermContract.TermEntry;
import com.example.wgutermtrackerjc.data.TermDBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AllTerms extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_terms);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllTerms.this, AddTerm.class);
                startActivity(intent);
            }
        });

       /* // Create a list of words
        ArrayList<String> words = new ArrayList<String>();
        words.add("one");
        words.add("two");
        words.add("three");
        words.add("four");
        words.add("five");
        words.add("six");
        words.add("seven");
        words.add("eight");
        words.add("nine");
        words.add("ten");

        // Create an ArrayAdapter whose data source is a list of strings
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, words);

        // Find the listView in the content_all_terms.xml file with the id list
        ListView listView = (ListView) findViewById(R.id.all_terms_list);
        // Call the setAdapter method on the listView object using the adapter created above
        listView.setAdapter(itemsAdapter);*/

       displayDatabaseInfo();
    }

    //When we get back to the activity make sure we get the information again from the DB
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        TermDBHelper mDbHelper = new TermDBHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        Cursor cursor = db.rawQuery("SELECT * FROM " + TermContract.TermEntry.TABLE_NAME, null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_term);
            displayView.setText("Number of rows in pets database table: " + cursor.getCount());
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    // Create the data that will be inserted from the menu option Insert Test Data.
    private void insertTestTerm() {
        TermDBHelper mDbHelper = new TermDBHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Term attributes are the the values.
        ContentValues values = new ContentValues();
        values.put(TermEntry.COLUMN_TERM_NAME, "Term 1");
        values.put(TermEntry.COLUMN_TERM_START_DATE, "05/01/2020");
        values.put(TermEntry.COLUMN_TERM_END_DATE, "11/01/2020");

        //Insert a new row into the terms Database
        long newRowId = db.insert(TermEntry.TABLE_NAME, null, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_terms.xml file
        getMenuInflater().inflate(R.menu.menu_terms, menu);
        return true;
    }

    // Declare what to do with the items in the menu when clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert test data" menu option
            case R.id.action_insert_test_data:
                insertTestTerm();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_terms:
                //showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
