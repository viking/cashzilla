package net.pillageandplunder.cashzilla;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Convenience definitions for NotePadProvider
 */
public final class Cashzilla {
    public static final String AUTHORITY = "net.pillageandplunder.provider.Cashzilla";

    // This class cannot be instantiated
    private Cashzilla() {}

    /**
     * Records table
     */
    public static final class Records implements BaseColumns {
        // This class cannot be instantiated
        private Records() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/records");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of records.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.pillageandplunder.record";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single record.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.pillageandplunder.record";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        /**
         * The description of the record
         * <P>Type: TEXT</P>
         */
        public static final String DESCRIPTION = "description";

        /**
         * The category of the record
         * <P>Type: TEXT</P>
         */
        public static final String CATEGORY = "category";

        /**
         * The amount of the record
         * <P>Type: INTEGER</P>
         */
        public static final String AMOUNT = "amount";

        /**
         * The timestamp for when the record was created
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String CREATED_DATE = "created";

        /**
         * The timestamp for when the record was last modified
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String MODIFIED_DATE = "modified";
    }
}