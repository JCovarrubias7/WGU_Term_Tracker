package com.example.wgutermtrackerjc;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.wgutermtrackerjc.data.TermContract.TermEntry;

public class TermCursorAdapter extends CursorAdapter {

    // Constructor
    public TermCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    // Create a new blank list item view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    // This method binds the term data to the given list item layout
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.list_item_name);
        TextView startTextView = (TextView) view.findViewById(R.id.list_item_start_date);
        TextView endTextView = (TextView) view.findViewById(R.id.list_item_end_date);

        // Find the columns of term attributes we are interested in
        int nameColumnIndex = cursor.getColumnIndex(TermEntry.COLUMN_TERM_NAME);
        int startColumnIndex = cursor.getColumnIndex(TermEntry.COLUMN_TERM_START_DATE);
        int endColumnIndex = cursor.getColumnIndex(TermEntry.COLUMN_TERM_END_DATE);

        // Read the term attributes from the Cursor for the current term
        String termName = cursor.getString(nameColumnIndex);
        String termStart = cursor.getString(startColumnIndex);
        String termEnd = cursor.getString(endColumnIndex);

        // Update the TextViews with the attributes for the current term
        nameTextView.setText(termName);
        startTextView.setText(termStart);
        endTextView.setText(termEnd);
    }

}
