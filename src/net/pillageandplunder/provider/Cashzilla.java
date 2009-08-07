package net.pillageandplunder.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Convenience definitions for CashzillaProvider
 */
public final class Cashzilla {
    /**
     * Records table
     */
    public static final class Records implements BaseColumns {
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI
                = Uri.parse("content://com.google.provider.Cashzilla/records");

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
        public static final String AMOUNT = "0";

        /**
         * The timestamp for when the record was created
         * <P>Type: INTEGER (long)</P>
         */
        public static final String CREATED_DATE = "created";

        /**
         * The timestamp for when the record was last modified
         * <P>Type: INTEGER (long)</P>
         */
        public static final String MODIFIED_DATE = "modified";
    }
}
