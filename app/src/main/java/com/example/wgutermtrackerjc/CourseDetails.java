package com.example.wgutermtrackerjc;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class CourseDetails extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the course data loader
    private static final int EXISTING_COURSE_LOADER = 0;

    // Content URI for existing course
    private Uri mCurrentCourseUri;

    // Adapter in all the call back methods
    CourseCursorAdapter mCursorAdapter;

    // Hold the course ID that launched this activity
    long currentCourseId;

    // TextView field that holds course name
    private TextView mCourseNameText;
    // TextView field that holds the course status
    private TextView mCourseStatusText;
    // TextView field that holds the course start date
    private TextView mCourseStartDateText;
    // TextView field that holds the course end date
    private TextView mCourseEndDateText;

    // Boolean flag that keeps track of whether the course has been edited(true) or not (false)
    private boolean mCourseHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            mCourseHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
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

        // Get the intent that lunched this activity
        Intent intent = getIntent();
        mCurrentCourseUri = intent.getData();

        // Get the data that was placed in the intent.putExtra
        currentCourseId = intent.getLongExtra("courseId", -1);
        String courseName = intent.getStringExtra("courseName");
        String courseStatus = intent.getStringExtra("courseStatus");
        String courseStart = intent.getStringExtra("courseStart");
        String courseEnd = intent.getStringExtra("courseEnd");

        // Find views that we want to set the values from the intent
        mCourseNameText = (TextView) findViewById(R.id.course_name);
        mCourseStatusText = (TextView) findViewById(R.id.course_item_status_text);
        mCourseStartDateText = (TextView) findViewById(R.id.course_item_start_date);
        mCourseEndDateText = (TextView) findViewById(R.id.course_item_end_date);

        // Set the TextView's values to the values from the intent
        mCourseNameText.setText(courseName);
        mCourseStatusText.setText(courseStatus);
        mCourseStartDateText.setText(courseStart);
        mCourseEndDateText.setText(courseEnd);

        // Find the ListView which will be populated with the assessments data
        ListView assessmentListView = (ListView) findViewById(R.id.course_assessment_list);

        // Setup an Adapter to create a list item for each row of assessment data in the cursor




    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
