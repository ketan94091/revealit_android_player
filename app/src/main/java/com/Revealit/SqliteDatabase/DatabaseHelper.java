package com.Revealit.SqliteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.Revealit.ModelClasses.CategoryNamesListModel;
import com.Revealit.ModelClasses.CategoryWisePlayListModel;

import java.util.ArrayList;

public class DatabaseHelper {

    public static final String DATABASE_NAME = "revealit.db";
    public static final int DATABASE_VERSION = 1;

    // Table Names
    public static String TABLE_CATEGORY_NAMES = "tableCategoryNames";
    public static String TABLE_CATEGORY_WISE_PLAY_LIST = "tableCategoryWisePlayList";

    //FIELD TABLE_CATEGORY_NAMES
    public static String KEY_ID = "keyID";
    public static String KEY_CATEGORY_NAMES = "keyCategoryNames";
    public static String KEY_CATEGORY_SLUG = "keyCategorySlug";

    //FIELD TABLE_CATEGORY_NAMES
    public static String KEY_CATEGORY_WISE_PLAY_MEDIA_ID = "keyCategoryWisePlayMediaID";
    public static String KEY_CATEGORY_WISE_PLAY_MEDIA_SHOW_TITLE = "keyCategoryWisePlayMediaShowTitle";
    public static String KEY_CATEGORY_WISE_PLAY_MEDIA_TITLE = "keyCategoryWisePlayMediaTitle";
    public static String KEY_CATEGORY_WISE_PLAY_MEDIA_TYPE = "keyCategoryWisePlayMediaType";
    public static String KEY_CATEGORY_WISE_PLAY_MEDIA_URL = "keyCategoryWisePlayMediaIUrl";
    public static String KEY_CATEGORY_WISE_PLAY_MEDIA_COVER_ART = "keyCategoryWisePlayMediaCoverArt";



    DatabaseManager dbManager;
    Context mContext;
    Cursor result = null;
    private static SQLiteDatabase mSqLiteDatabase;


    public DatabaseHelper(Context mContext) {
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
        dbManager = DatabaseManager.getInstance(mContext, DATABASE_NAME, null,
                DATABASE_VERSION);

    }

    public void open() {
        if (mSqLiteDatabase == null || mSqLiteDatabase.isOpen())
            mSqLiteDatabase = dbManager.getWritableDatabase();
    }

    public void close() {

        mSqLiteDatabase.close();
    }


    //INSERT CATEGORY NAMES
    public Long insertCategoryNames(String strCategoryName,
                                 String strSlugName) {

        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAMES, strCategoryName);
        values.put(KEY_CATEGORY_SLUG, strSlugName);

        return mSqLiteDatabase.insert(TABLE_CATEGORY_NAMES, null, values);

    }

    //GET CATEGORY NAMES LIST
    public ArrayList<CategoryNamesListModel.DataBean> getCategoryList() {
        ArrayList<CategoryNamesListModel.DataBean> mPlayCategoryNamesModelList = new ArrayList<>();

        try {
            result = mSqLiteDatabase.query(TABLE_CATEGORY_NAMES, new String[]{}, null, null,
                    null, null, null);

            if (result.moveToFirst()) {
                do {
                    CategoryNamesListModel.DataBean mPlayCategoryNamesModel = new CategoryNamesListModel.DataBean();
                    mPlayCategoryNamesModel.setCategoryName(result.getString(1));
                    mPlayCategoryNamesModel.setSlugName(result.getString(2));
                    mPlayCategoryNamesModelList.add(mPlayCategoryNamesModel);

                } while (result.moveToNext());
            }

        } catch (Exception e) {

        } finally {
            if (result != null) {
                result.close();
                result = null;
            }
        }

        return mPlayCategoryNamesModelList;
    }

    //INSERT CATEGORY WISE PLAYLIST
    public Long insertCategoryWisePlayData(String strCategoryName,
                                 String strSlugName,
                                 int strmediaID,
                                 String strmediaShowTitle,
                                 String strmediaTitle,
                                 String strmediaType,
                                 String strmediaUrl,
                                 String strmediaCoverArt
                                 ) {

        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAMES, strCategoryName);
        values.put(KEY_CATEGORY_SLUG, strSlugName);
        values.put(KEY_CATEGORY_WISE_PLAY_MEDIA_ID, strmediaID);
        values.put(KEY_CATEGORY_WISE_PLAY_MEDIA_SHOW_TITLE, strmediaShowTitle);
        values.put(KEY_CATEGORY_WISE_PLAY_MEDIA_TITLE, strmediaTitle);
        values.put(KEY_CATEGORY_WISE_PLAY_MEDIA_TYPE, strmediaType);
        values.put(KEY_CATEGORY_WISE_PLAY_MEDIA_URL, strmediaUrl);
        values.put(KEY_CATEGORY_WISE_PLAY_MEDIA_COVER_ART, strmediaCoverArt);

        return mSqLiteDatabase.insert(TABLE_CATEGORY_WISE_PLAY_LIST, null, values);

    }

    //GET CATEGORY WIS ALL PLAY LIST
    public ArrayList<CategoryWisePlayListModel.DataBean> getCategoryWisePlayList() {
        ArrayList<CategoryWisePlayListModel.DataBean> mCategoryWisePlayListModel = new ArrayList<>();

        try {
            result = mSqLiteDatabase.query(TABLE_CATEGORY_WISE_PLAY_LIST, new String[]{}, null, null,
                    null, null, null);

            if (result.moveToFirst()) {
                do {
                    CategoryWisePlayListModel.DataBean mPlayCategoryNamesModel = new CategoryWisePlayListModel.DataBean();
                    mPlayCategoryNamesModel.setCategoryName(result.getString(1));
                    mPlayCategoryNamesModel.setSlugName(result.getString(2));
                    mPlayCategoryNamesModel.setMediaID(result.getInt(3));
                    mPlayCategoryNamesModel.setMediaShowTitle(result.getString(4));
                    mPlayCategoryNamesModel.setMediaTitle(result.getString(5));
                    mPlayCategoryNamesModel.setMediaType(result.getString(6));
                    mPlayCategoryNamesModel.setMediaUrl(result.getString(7));
                    mPlayCategoryNamesModel.setMediaCoverArt(result.getString(8));
                    mCategoryWisePlayListModel.add(mPlayCategoryNamesModel);

                } while (result.moveToNext());
            }

        } catch (Exception e) {

        } finally {
            if (result != null) {
                result.close();
                result = null;
            }
        }

        return mCategoryWisePlayListModel;
    }

    //GET CATEGORY WIS ALL PLAY LIST
    public ArrayList<CategoryWisePlayListModel.DataBean> getCategoryWisePlayListByName(String strCategoryName) {
        ArrayList<CategoryWisePlayListModel.DataBean> mCategoryWisePlayListModel = new ArrayList<>();


        try {
            result = mSqLiteDatabase.query
                    (TABLE_CATEGORY_WISE_PLAY_LIST,
                            new String[]{
                                    KEY_ID,
                                    KEY_CATEGORY_NAMES,
                                    KEY_CATEGORY_SLUG,
                                    KEY_CATEGORY_WISE_PLAY_MEDIA_ID,
                                    KEY_CATEGORY_WISE_PLAY_MEDIA_SHOW_TITLE,
                                    KEY_CATEGORY_WISE_PLAY_MEDIA_TITLE,
                                    KEY_CATEGORY_WISE_PLAY_MEDIA_TYPE,
                                    KEY_CATEGORY_WISE_PLAY_MEDIA_URL,
                                    KEY_CATEGORY_WISE_PLAY_MEDIA_COVER_ART

                            },
                            KEY_CATEGORY_NAMES + "=" + "'" + strCategoryName + "'",
                            null, null, null, null, null
                    );

            if (result.moveToFirst()) {
                do {
                    CategoryWisePlayListModel.DataBean mPlayCategoryNamesModel = new CategoryWisePlayListModel.DataBean();
                    mPlayCategoryNamesModel.setCategoryName(result.getString(1));
                    mPlayCategoryNamesModel.setSlugName(result.getString(2));
                    mPlayCategoryNamesModel.setMediaID(result.getInt(3));
                    mPlayCategoryNamesModel.setMediaShowTitle(result.getString(4));
                    mPlayCategoryNamesModel.setMediaTitle(result.getString(5));
                    mPlayCategoryNamesModel.setMediaType(result.getString(6));
                    mPlayCategoryNamesModel.setMediaUrl(result.getString(7));
                    mPlayCategoryNamesModel.setMediaCoverArt(result.getString(8));
                    mCategoryWisePlayListModel.add(mPlayCategoryNamesModel);

                } while (result.moveToNext());
            }

        } catch (Exception e) {

        } finally {
            if (result != null) {
                result.close();
                result = null;
            }
        }

        return mCategoryWisePlayListModel;
    }

    public static void clearAllTables() {

        mSqLiteDatabase.delete(TABLE_CATEGORY_NAMES, null, null);
        mSqLiteDatabase.delete(TABLE_CATEGORY_WISE_PLAY_LIST, null, null);
    }

}