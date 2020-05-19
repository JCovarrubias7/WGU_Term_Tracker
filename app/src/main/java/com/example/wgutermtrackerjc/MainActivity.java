package com.example.wgutermtrackerjc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.wgutermtrackerjc.data.DBContract.TermEntry;

public class MainActivity extends AppCompatActivity {

    ImageButton allTermsImageButton, currentTermImageButton;

    final int ACTIVE_TERM = 1;

    long activeTermId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the all terms ImageButton in the layout
        allTermsImageButton = findViewById(R.id.all_terms_button);

        // Set OnClickListener on the ImageButton to launch activity
        allTermsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLoadNewActivity = new Intent(MainActivity.this, TermsList.class);
                startActivity(intentLoadNewActivity);
            }
        });


        // Find the current terms ImageButton in the layout
        currentTermImageButton = findViewById(R.id.current_term_button);
        // Set OnClickListener on the ImageButton to launch activity
        currentTermImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Active Current Term
                getCurrentActiveTerm();
                // Create new intent to go to current active term
                Intent intent = new Intent(MainActivity.this, TermDetails.class);
                // Form the content URI for the current active term
                Uri currentActiveTermUri = ContentUris.withAppendedId(TermEntry.CONTENT_URI_TERMS, activeTermId);
                // Set the URI on the data files of the Intent
                intent.setData(currentActiveTermUri);
                // Launch the activity to display data for the current active term
                if (activeTermId == 0) {
                    Toast.makeText(MainActivity.this, "A term has not been marked as current, do this in Term Details",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    startActivity(intent);
                }
            }
        });
    }

    private void getCurrentActiveTerm() {
        // Define a projection that specifies the columns from the table we care about
        String[] projection = {
                TermEntry._ID,
                TermEntry.COLUMN_TERM_ACTIVE};

        // Define a selection
        String selection = TermEntry.COLUMN_TERM_ACTIVE + "=?";
        String[] selectionArgs = { String.valueOf(ACTIVE_TERM) };

        // Get the Cursor with the term that has active term = 1
        Cursor cursor = getContentResolver().query(TermEntry.CONTENT_URI_TERMS, projection,
                selection, selectionArgs, null);
        if(cursor.moveToFirst()) {
            // Get Column Index
            int idColumnIndex = cursor.getColumnIndex(TermEntry._ID);
            // Extract the values from the Cursor for the given index
            activeTermId = cursor.getLong(idColumnIndex);
        }
    }
}
