package com.Revealit.SqliteDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {

    private static com.Revealit.SqliteDatabase.DatabaseManager sInstance = null;

    public static synchronized com.Revealit.SqliteDatabase.DatabaseManager getInstance(Context context,
                                                                                       String name, SQLiteDatabase.CursorFactory factory, int version) {

        if (sInstance == null) {
            sInstance = new com.Revealit.SqliteDatabase.DatabaseManager(context.getApplicationContext(),
                    name, factory, version);
        }
        return sInstance;
    }

    public DatabaseManager(Context context, String name, SQLiteDatabase.CursorFactory factory,
                           int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub


        db.execSQL(tableCategoryList());
        db.execSQL(tableCategoryWisePlayList());
        db.execSQL(tableRewardHistory());
        db.execSQL(tableCurrencyList());
        db.execSQL(tableRevealitHistoryList());
        db.execSQL(tableRevealitHistoryListSimulation());


    }




    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        throw new SQLiteException("Can't downgrade database from version " +
                oldVersion + " to " + newVersion);
        //super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }

    private String tableCategoryList() {
        return "CREATE TABLE IF NOT EXISTS " + DatabaseHelper.TABLE_CATEGORY_NAMES
                + " ( " + DatabaseHelper.KEY_ID + " INTEGER PRIMARY KEY,"
                + DatabaseHelper.KEY_CATEGORY_NAMES + " text, "
                + DatabaseHelper.KEY_CATEGORY_SLUG + " text);";
    }

    private String tableCategoryWisePlayList() {
        return "CREATE TABLE IF NOT EXISTS " + DatabaseHelper.TABLE_CATEGORY_WISE_PLAY_LIST
                + " ( " + DatabaseHelper.KEY_ID + " INTEGER PRIMARY KEY,"
                + DatabaseHelper.KEY_CATEGORY_NAMES + " text, "
                + DatabaseHelper.KEY_CATEGORY_SLUG + " text, "
                + DatabaseHelper.KEY_CATEGORY_WISE_PLAY_MEDIA_ID + " INTEGER, "
                + DatabaseHelper.KEY_CATEGORY_WISE_PLAY_MEDIA_SHOW_TITLE + " text, "
                + DatabaseHelper.KEY_CATEGORY_WISE_PLAY_MEDIA_TITLE + " text, "
                + DatabaseHelper.KEY_CATEGORY_WISE_PLAY_MEDIA_TYPE + " text, "
                + DatabaseHelper.KEY_CATEGORY_WISE_PLAY_MEDIA_URL + " text, "
                + DatabaseHelper.KEY_CATEGORY_WISE_PLAY_MEDIA_COVER_ART + " text);";
    }

    private String tableRewardHistory() {

        return "CREATE TABLE IF NOT EXISTS " + DatabaseHelper.TABLE_REWARD_HISTORY
                + " ( " + DatabaseHelper.KEY_ID + " INTEGER PRIMARY KEY,"
                + DatabaseHelper.KEY_REWARD_HISTORY_AMOUNT + " text, "
                + DatabaseHelper.KEY_REWARD_HISTORY_ACTION + " text, "
                + DatabaseHelper.KEY_REWARD_HISTORY_DISPLAY_DATE + " text);";
    }

    private String tableCurrencyList() {

        return "CREATE TABLE IF NOT EXISTS " + DatabaseHelper.TABLE_CURRENCY_LIST
                + " ( " + DatabaseHelper.KEY_ID + " INTEGER PRIMARY KEY,"
                + DatabaseHelper.KEY_CURRENCY_TITLE + " text, "
                + DatabaseHelper.KEY_CURRENCY_AMOUNT + " text, "
                + DatabaseHelper.KEY_CURRENCY_ICON_URL + " text, "
                + DatabaseHelper.KEY_CURRENCY_NAME + " text);";
    }

    private String tableRevealitHistoryList() {

        return "CREATE TABLE IF NOT EXISTS " + DatabaseHelper.TABLE_REVEALIT_HISTORY
                + " ( " + DatabaseHelper.KEY_ID + " INTEGER PRIMARY KEY,"
                + DatabaseHelper.KEY_REVEALIT_HISTORY_MATCH_ID + " INTEGER, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_MEDIA_ID + " INTEGER, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_MEDIA_TYPE + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_MEDIA_TITLE + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_MEDIA_URL + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_MEDIA_COVER_ART + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_CURRENT_TIME + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_PLAYBACK_OFFSET + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_PLAYBACK_DISPLAY + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_MATCH_TIMESTAMP + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_ALL_TIMESTAMP + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_ALL_TIMESTAMP_OFFSET + " text);";
    }

    private String tableRevealitHistoryListSimulation() {

        return "CREATE TABLE IF NOT EXISTS " + DatabaseHelper.TABLE_REVEALIT_HISTORY_SIMULATION
                + " ( " + DatabaseHelper.KEY_ID + " INTEGER PRIMARY KEY,"
                + DatabaseHelper.KEY_REVEALIT_HISTORY_MATCH_ID + " INTEGER, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_MEDIA_ID + " INTEGER, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_MEDIA_TYPE + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_MEDIA_TITLE + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_MEDIA_URL + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_MEDIA_COVER_ART + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_CURRENT_TIME + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_PLAYBACK_OFFSET + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_PLAYBACK_DISPLAY + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_MATCH_TIMESTAMP + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_ALL_TIMESTAMP + " text, "
                + DatabaseHelper.KEY_REVEALIT_HISTORY_ALL_TIMESTAMP_OFFSET + " text);";
    }

}

