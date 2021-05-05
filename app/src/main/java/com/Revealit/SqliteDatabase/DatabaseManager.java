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


}

