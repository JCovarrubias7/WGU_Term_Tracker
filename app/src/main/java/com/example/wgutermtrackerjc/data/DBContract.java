package com.example.wgutermtrackerjc.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class DBContract {

    // Content Authority string similar to the relationship between a domain name and its website.
    public static final String CONTENT_AUTHORITY = "com.example.wgutermtrackerjc";

    // user Content Authority to create the base of all URI's which apps will use to contact the
    // content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible path appended to the base content URI for possible URI's
    public static final String PATH_TERMS = "terms";
    public static final String PATH_COURSES = "courses";
    public static final String PATH_ASSESSMENTS = "assessments";
    public static final String PATH_NOTES = "notes";

    public static abstract class TermEntry implements BaseColumns {
        // The content URI to access the terms table data in the provider
        public static final Uri CONTENT_URI_TERMS = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TERMS);

        // The MIME type of the CONTENT_URI for a list of terms
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TERMS;

        // THE MIME type of the CONTENT_URI for a single term
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TERMS;

        // Table name constant
        public static final String TABLE_NAME_TERMS = "terms";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TERM_NAME = "term_name";
        public static final String COLUMN_TERM_START_DATE = "term_start_date";
        public static final String COLUMN_TERM_END_DATE = "term_end_date";
        public static final String COLUMN_TERM_ACTIVE = "active";
    }

    public static abstract class CourseEntry implements BaseColumns {
        // The content URI to access the courses table data in the provider
        public static final Uri CONTENT_URI_COURSES = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_COURSES);

        // Table name constants
        public static final String TABLE_NAME_COURSES = "courses";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ASSOCIATED_TERM_ID = "associated_term_id";
        public static final String COLUMN_COURSE_NAME = "course_name";
        public static final String COLUMN_COURSE_START = "course_start_date";
        public static final String COLUMN_COURSE_END = "course_end_date";
        public static final String COLUMN_COURSE_STATUS = "course_status";
        public static final String COLUMN_COURSE_MENTOR_NAME = "course_mentor_name";
        public static final String COLUMN_COURSE_MENTOR_PHONE = "course_mentor_phone";
        public static final String COLUMN_COURSE_MENTOR_EMAIL = "course_mentor_email";
    }

    public static abstract class AssessmentEntry implements BaseColumns {
        // The content URI to access the assessment table data in the provider
        public static final Uri CONTENT_URI_ASSESSMENTS = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ASSESSMENTS);

        // Table name constants
        public static final String TABLE_NAME_ASSESSMENTS = "assessments";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ASSOCIATED_COURSE_ID = "associated_course_id";
        public static final String COLUMN_ASSESSMENT_NAME = "assessment_name";
        public static final String COLUMN_ASSESSMENT_DUE_DATE = "assessment_due_date";
        public static final String COLUMN_ASSESSMENT_DESCRIPTION = "assessment_description";
    }

    public static abstract class NoteEntry implements BaseColumns {
        // The content URI to access the notes table data in the provider
        public static final Uri CONTENT_URI_NOTES = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTES);

        // Table name constants
        public static final String TABLE_NAME_NOTES = "notes";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NOTES_ASSOCIATED_COURSE_ID = "notes_associated_course_id";
        public static final String COLUMN_NOTES = "course_notes";
    }
}
