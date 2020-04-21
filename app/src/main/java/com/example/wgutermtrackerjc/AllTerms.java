package com.example.wgutermtrackerjc;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.example.wgutermtrackerjc.data.TermContract.TermEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
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
        //Define a projection that specifies what columns from the database you want to use
        String[] projection = {
                TermEntry._ID,
                TermEntry.COLUMN_TERM_NAME,
                TermEntry.COLUMN_TERM_START_DATE,
                TermEntry.COLUMN_TERM_END_DATE
        };

        //Perform a query on the Terms table using the ContentResolver
        Cursor cursor = getContentResolver().query(TermEntry.CONTENT_URI, projection,
                null, null, null);

        // Find the ListView which will be populated with the term data
        ListView termListView = (ListView) findViewById(R.id.all_terms_list);

        //Setup aan Adapter to create a list item for each row of term data in the Cursor
        TermCursorAdapter adapter = new TermCursorAdapter(this, cursor);

        // Attach the adapter to the list view
        termListView.setAdapter(adapter);
    }

    // Create the data that will be inserted from the menu option Insert Test Data.
    private void insertTestTerm() {
        // Create a ContentValues object where column names are the keys,
        // and Term attributes are the the values.
        ContentValues values = new ContentValues();
        values.put(TermEntry.COLUMN_TERM_NAME, "Term 1");
        values.put(TermEntry.COLUMN_TERM_START_DATE, "05/01/2020");
        values.put(TermEntry.COLUMN_TERM_END_DATE, "11/01/2020");

        // Insert a new row into the database and return the ID of the new row
        //long newRowId = db.insert(TermEntry.TABLE_NAME, null, values);
        Uri newUri = getContentResolver().insert(TermEntry.CONTENT_URI, values);
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
                int newDeletedRows = getContentResolver().delete(TermEntry.CONTENT_URI, null, null);
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
