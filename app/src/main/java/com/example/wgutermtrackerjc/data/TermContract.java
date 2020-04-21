package com.example.wgutermtrackerjc.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class TermContract {

    // Content Authority string similar to the relationship between a domain name and its website.
    public static final String CONTENT_AUTHORITY = "com.example.wgutermtrackerjc";

    // user Content Authority to create the base of all URI's which apps will use to contact the
    // content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible path appended to the base content URI for possible URI's
    public static final String PATH_TERMS = "terms";

    public static abstract class TermEntry implements BaseColumns {

        // The content URI to access the term data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TERMS);

        // The MIME type of the CONTENT_URI for a list of terms
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TERMS;

        // THE MIME type of the CONTENT_URI for a single term
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TERMS;

        // Table name constant
        public static final String TABLE_NAME = "terms";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TERM_NAME = "term_name";
        public static final String COLUMN_TERM_START_DATE = "term_start_date";
        public static final String COLUMN_TERM_END_DATE = "term_end_date";
    }
}
