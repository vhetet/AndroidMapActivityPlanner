package com.project.vincent.activityplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Vincent on 17/04/2016.
 */
public class EventSqlHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "EventDB";

    //Table name
    public static final String TABLE_NAME = "events";

    //Columns name
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_STARTING_DATE = "starting_date";
    public static final String COLUMN_NAME_ENDING_DATE = "ending_date";
    public static final String COLUMN_NAME_LATITUDE = "latitude";
    public static final String COLUMN_NAME_LONGITUDE = "longitude";
    public static final String COLUMN_NAME_RATING = "rating";
    public static final String COLUMN_NAME_IDAPI = "idAPI";
    public static final String COLUMN_NAME_PLACEID = "placeId";
    public static final String COLUMN_NAME_PRICE_LEVEL = "priceLevel";
    public static final String COLUMN_NAME_REFERENCE = "reference";
    public static final String COLUMN_NAME_ADDRESS = "address";

    // Queries
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String CREATE_EVENT_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_STARTING_DATE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_ENDING_DATE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_RATING + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_IDAPI + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_PLACEID + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_PRICE_LEVEL + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_REFERENCE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_ADDRESS + TEXT_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    public EventSqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    // TODO: generate the comments for the javadoc
    /**
     * add the event to the table
     *
     * @param event
     */
    public void addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_NAME, event.getName());
        values.put(COLUMN_NAME_STARTING_DATE, event.getStartingDate());
        values.put(COLUMN_NAME_ENDING_DATE, event.getEndingDate());
        values.put(COLUMN_NAME_LATITUDE, event.getLocation().getLatitude());
        values.put(COLUMN_NAME_LONGITUDE, event.getLocation().getLongitude());
        values.put(COLUMN_NAME_RATING, event.getRating());
        values.put(COLUMN_NAME_IDAPI, event.getIdAPI());
        values.put(COLUMN_NAME_PLACEID, event.getPlaceId());
        values.put(COLUMN_NAME_PRICE_LEVEL, event.getPriceLevel());
        values.put(COLUMN_NAME_REFERENCE, event.getReference());
        values.put(COLUMN_NAME_ADDRESS, event.getAddress());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Retrieve all the event of the table
     *
     * @return List<Event>
     */
    public List<Event> getAllEvents() {
        List<Event> events = new LinkedList<Event>();

        String query = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                events.add(parseEvent(cursor));
            } while (cursor.moveToNext());
        }

        return events;
    }

    /**
     * Return an event by id
     *
     * @param id
     * @return the event
     */
    public Event getEvent(int id) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE id=" + id;
        Event event = new Event();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            event = parseEvent(cursor);
        }
        return event;
    }

    /**
     * Return an event by idAPI
     *
     * @param idAPI
     * @return the event
     */
    public Event getEvent(String idAPI) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE idAPI=" + idAPI;
        Event event = new Event();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            event = parseEvent(cursor);
        }
        return event;
    }

    /**
     * Convert the data in the cursor from the events table to an event
     *
     * @param cursor
     * @return event
     */
    public Event parseEvent (Cursor cursor) {
        Event event = new Event();

        if (cursor.getString(0) != null )
            event.setId(cursor.getInt(0));
        if (cursor.getString(1) != null )
            event.setName(cursor.getString(1));
        else
            event.setName("");
        if (cursor.getString(2) != null)
            event.setStartingDate(cursor.getString(2));
        else
            event.setStartingDate("");
        if (cursor.getString(3) != null)
            event.setEndingDate(cursor.getString(3));
        else
            event.setEndingDate("");
        Location location = new Location("");
        if (cursor.getString(4) != null)
            location.setLatitude(cursor.getDouble(4));
        if (cursor.getString(5) != null)
            location.setLongitude(cursor.getDouble(5));
        event.setLocation(location);
        if (cursor.getString(6) != null )
            event.setRating(6);
        else
            event.setRating(0);
        if (cursor.getString(7) != null )
            event.setIdAPI(cursor.getString(7));
        else
            event.setIdAPI("");
        if (cursor.getString(8) != null )
            event.setPlaceId(cursor.getString(8));
        else
            event.setPlaceId("");
        if (cursor.getString(9) != null )
            event.setPriceLevel(cursor.getInt(9));
        else
            event.setPriceLevel(0);
        if (cursor.getString(10) != null )
            event.setReference(cursor.getString(10));
        else
            event.setReference("");
        if (cursor.getString(11) != null )
            event.setAddress(cursor.getString(11));
        else
            event.setAddress("");

        return event;
    }
}
