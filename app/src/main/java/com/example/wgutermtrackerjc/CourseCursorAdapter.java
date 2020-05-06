package com.example.wgutermtrackerjc;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.wgutermtrackerjc.data.DBContract.CourseEntry;

public class CourseCursorAdapter extends CursorAdapter {

    // Constructor
    public CourseCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    // Create a new blank list item view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_course_item, parent, false);
    }

    // This method binds the term data to the given list item layout
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.list_course_item_name);
        TextView statusTextView = (TextView) view.findViewById(R.id.list_course_item_status_text) ;
        TextView startTextView = (TextView) view.findViewById(R.id.list_course_item_start_date);
        TextView endTextView = (TextView) view.findViewById(R.id.list_course_item_end_date);

        // Find the columns of course attributes we are interested in
        int nameColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_NAME);
        int statusColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_STATUS);
        int startColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_START);
        int endColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_END);

        // Read the course attributes from the Cursor for the current term
        String courseName = cursor.getString(nameColumnIndex);
        String courseStatus = cursor.getString(statusColumnIndex);
        String courseStart = cursor.getString(startColumnIndex);
        String courseEnd = cursor.getString(endColumnIndex);

        // Update the TextViews with the attributes for the current course
        nameTextView.setText(courseName);
        statusTextView.setText(courseStatus);
        startTextView.setText(courseStart);
        endTextView.setText(courseEnd);
    }

}
