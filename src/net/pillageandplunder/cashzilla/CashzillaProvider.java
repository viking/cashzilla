package net.pillageandplunder.cashzilla;

import net.pillageandplunder.cashzilla.Cashzilla.Records;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Provides access to a database of records. Each record has a description,
 * a category, an amount, a creation date and a modified data.
 */
public class CashzillaProvider extends ContentProvider {

    private static final String TAG = "CashzillaProvider";

    private static final String DATABASE_NAME = "cashzilla.db";
    private static final int DATABASE_VERSION = 1;
    private static final String RECORDS_TABLE_NAME = "records";

    private static HashMap<String, String> sRecordsProjectionMap;

    private static final int RECORDS = 1;
    private static final int RECORD_ID = 2;

    private static final UriMatcher sUriMatcher;

    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + RECORDS_TABLE_NAME + " ("
                    + Records._ID + " INTEGER PRIMARY KEY,"
                    + Records.DESCRIPTION + " TEXT,"
                    + Records.CATEGORY + " TEXT,"
                    + Records.AMOUNT + " INTEGER,"
                    + Records.CREATED_DATE + " INTEGER,"
                    + Records.MODIFIED_DATE + " INTEGER"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS records");
            onCreate(db);
        }
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
        case RECORDS:
            qb.setTables(RECORDS_TABLE_NAME);
            qb.setProjectionMap(sRecordsProjectionMap);
            break;

        case RECORD_ID:
            qb.setTables(RECORDS_TABLE_NAME);
            qb.setProjectionMap(sRecordsProjectionMap);
            qb.appendWhere(Records._ID + "=" + uri.getPathSegments().get(1));
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = Cashzilla.Records.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case RECORDS:
            return Records.CONTENT_TYPE;

        case RECORD_ID:
            return Records.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != RECORDS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        Long now = Long.valueOf(System.currentTimeMillis());

        // Make sure that the fields are all set
        if (values.containsKey(Cashzilla.Records.CREATED_DATE) == false) {
            values.put(Cashzilla.Records.CREATED_DATE, now);
        }

        if (values.containsKey(Cashzilla.Records.MODIFIED_DATE) == false) {
            values.put(Cashzilla.Records.MODIFIED_DATE, now);
        }

        if (values.containsKey(Cashzilla.Records.DESCRIPTION) == false) {
            Resources r = Resources.getSystem();
            values.put(Cashzilla.Records.DESCRIPTION, r.getString(android.R.string.untitled));
        }
        
        if (values.containsKey(Cashzilla.Records.CATEGORY) == false) {
            Resources r = Resources.getSystem();
            values.put(Cashzilla.Records.CATEGORY, r.getString(android.R.string.untitled));
        }

        if (values.containsKey(Cashzilla.Records.AMOUNT) == false) {
            values.put(Cashzilla.Records.AMOUNT, 0);
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(RECORDS_TABLE_NAME, Records.AMOUNT, values);
        if (rowId > 0) {
            Uri recordUri = ContentUris.withAppendedId(Cashzilla.Records.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(recordUri, null);
            return recordUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case RECORDS:
            count = db.delete(RECORDS_TABLE_NAME, where, whereArgs);
            break;

        case RECORD_ID:
            String recordId = uri.getPathSegments().get(1);
            count = db.delete(RECORDS_TABLE_NAME, Records._ID + "=" + recordId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case RECORDS:
            count = db.update(RECORDS_TABLE_NAME, values, where, whereArgs);
            break;

        case RECORD_ID:
            String recordId = uri.getPathSegments().get(1);
            count = db.update(RECORDS_TABLE_NAME, values, Records._ID + "=" + recordId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Cashzilla.AUTHORITY, "records", RECORDS);
        sUriMatcher.addURI(Cashzilla.AUTHORITY, "records/#", RECORD_ID);

        sRecordsProjectionMap = new HashMap<String, String>();
        sRecordsProjectionMap.put(Records._ID, Records._ID);
        sRecordsProjectionMap.put(Records.DESCRIPTION, Records.DESCRIPTION);
        sRecordsProjectionMap.put(Records.CATEGORY, Records.CATEGORY);
        sRecordsProjectionMap.put(Records.AMOUNT, Records.AMOUNT);
        sRecordsProjectionMap.put(Records.CREATED_DATE, Records.CREATED_DATE);
        sRecordsProjectionMap.put(Records.MODIFIED_DATE, Records.MODIFIED_DATE);
    }
}