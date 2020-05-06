package com.example.wgutermtrackerjc;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class AssessmentCursorAdapter extends CursorAdapter {

    // Constructor
    public AssessmentCursorAdapter(Context context, Cursor c) { super(context, c, 0); }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_assessment_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.list_assessment_item_name);
        TextView dueDateTextView = (TextView) view.findViewById(R.id.list_assessment_item_due_date_textview);

        // Find the columns of assessment attributes we are interested in

    }
}
