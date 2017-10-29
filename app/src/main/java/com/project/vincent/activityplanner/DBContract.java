package com.project.vincent.activityplanner;

import android.provider.BaseColumns;
import android.provider.Telephony;

/**
 * Created by Vincent on 18/04/2016.
 */
public final class DBContract {

    public DBContract() {};

    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_STARTING_HOUR = "starting_hour";
        public static final String COLUMN_NAME_ENDING_HOUR = "ending_hour";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_RATING = "rating";
        public static final String COLUMN_NAME_IDAPI = "idAPI";
        public static final String COLUMN_NAME_PLACEID = "placeId";
        public static final String COLUMN_NAME_PRICE_LEVEL = "priceLevel";
        public static final String COLUMN_NAME_REFERENCE = "reference";
        public static final String COLUMN_NAME_ADDRESS = "address";
    }
}
