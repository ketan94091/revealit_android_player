package com.Revealit.SqliteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.Revealit.ModelClasses.CategoryNamesListModel;
import com.Revealit.ModelClasses.CategoryWisePlayListModel;
import com.Revealit.ModelClasses.GetAccountDetailsModel;
import com.Revealit.ModelClasses.RevealitHistoryModel;
import com.Revealit.ModelClasses.RewardHistoryDatabaseModel;

import java.util.ArrayList;

public class DatabaseHelper {

    public static final String DATABASE_NAME = "revealit.db";
    public static final int DATABASE_VERSION = 1;

    // Table Names
    public static String TABLE_CATEGORY_NAMES = "tableCategoryNames";
    public static String TABLE_CATEGORY_WISE_PLAY_LIST = "tableCategoryWisePlayList";
    public static String TABLE_REWARD_HISTORY = "tableRewardHistory";
    public static String TABLE_CURRENCY_LIST = "tableCurrencyList";
    public static String TABLE_LISTEN_SCREEN_LIST= "tableListenScreenList";
    public static String TABLE_TEST= "tableTest";
    public static String TABLE_REVEALIT_HISTORY= "tableRevealitHistory";
    public static String TABLE_REVEALIT_HISTORY_SIMULATION= "tableRevealitHistorSimulation";




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

    //FIELD TABLE_REWARD_HISTORY
    public static String KEY_REWARD_HISTORY_AMOUNT = "keyAmount";
    public static String KEY_REWARD_HISTORY_ACTION = "keyAction";
    public static String KEY_REWARD_HISTORY_DISPLAY_DATE = "keyDisplayDate";

    //FIELD TABLE_REWARD_HISTORY
    public static String KEY_CURRENCY_TITLE = "symbol";
    public static String KEY_CURRENCY_AMOUNT = "value";
    public static String KEY_CURRENCY_ICON_URL = "icon";
    public static String KEY_CURRENCY_NAME = "name";

    //FIELD TABLE_LISTEN_SCREEN_LIST
    public static String KEY_LISTEN_MEDIA_ID = "keyCategoryWisePlayMediaID";
    public static String KEY_LISTEN_MEDIA_SHOW_TITLE = "keyCategoryWisePlayMediaShowTitle";
    public static String KEY_LISTEN_MEDIA_TITLE = "keyCategoryWisePlayMediaTitle";
    public static String KEY_LISTEN_MEDIA_TYPE = "keyCategoryWisePlayMediaType";
    public static String KEY_LISTEN_MEDIA_URL = "keyCategoryWisePlayMediaIUrl";
    public static String KEY_LISTEN_MEDIA_COVER_ART = "keyCategoryWisePlayMediaCoverArt";

    //FIELD TABLE_REVEALIT_HISTORY
    public static String KEY_REVEALIT_HISTORY_MATCH_ID= "keyRevealitHistoryMatchID";
    public static String KEY_REVEALIT_HISTORY_MEDIA_ID= "keyRevealitHistoryMediaID";
    public static String KEY_REVEALIT_HISTORY_MEDIA_TYPE= "keyRevealitHistoryMediaType";
    public static String KEY_REVEALIT_HISTORY_MEDIA_TITLE= "keyRevealitHistoryMediaTitle";
    public static String KEY_REVEALIT_HISTORY_MEDIA_URL= "keyRevealitHistoryMediaURL";
    public static String KEY_REVEALIT_HISTORY_MEDIA_COVER_ART= "keyRevealitHistoryMediaCoverArt";
    public static String KEY_REVEALIT_HISTORY_CURRENT_TIME= "keyRevealitHistoryCurrentTime";
    public static String KEY_REVEALIT_HISTORY_PLAYBACK_OFFSET= "keyRevealitHistoryPlaybackOffset";
    public static String KEY_REVEALIT_HISTORY_PLAYBACK_DISPLAY= "tableRevealitHistoryPlaybackDisplay";
    public static String KEY_REVEALIT_HISTORY_MATCH_TIMESTAMP= "tableRevealitHistoryTimeStamp";
    public static String KEY_REVEALIT_HISTORY_ALL_TIMESTAMP= "tableRevealitHistoryAllTimeStamp";
    public static String KEY_REVEALIT_HISTORY_ALL_TIMESTAMP_OFFSET= "tableRevealitHistoryAllTimeStampOffset";


    public static String KEY_BYTE_ARRY = "keyByteArry";


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

    public void deleteFirstRow()
    {

        Cursor cursor = mSqLiteDatabase.query(TABLE_CATEGORY_WISE_PLAY_LIST, null, null, null, null, null, null);

        if(cursor.moveToFirst()) {
            String rowId = cursor.getString(cursor.getColumnIndex(KEY_ID));

            mSqLiteDatabase.delete(TABLE_CATEGORY_WISE_PLAY_LIST, KEY_ID + "=?",  new String[]{"0"});
        }

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


    //INSERT CATEGORY NAMES
    public Long insertRewardHistoryData(String strAmount,
                                        String strAction,
                                        String strDisplayDateName) {

        ContentValues values = new ContentValues();
        values.put(KEY_REWARD_HISTORY_AMOUNT, strAmount);
        values.put(KEY_REWARD_HISTORY_ACTION, strAction);
        values.put(KEY_REWARD_HISTORY_DISPLAY_DATE, strDisplayDateName);

        return mSqLiteDatabase.insert(TABLE_REWARD_HISTORY, null, values);

    }

    //GET CATEGORY NAMES LIST
    public ArrayList<RewardHistoryDatabaseModel.Datum> gettRewardHistoryData() {
        ArrayList<RewardHistoryDatabaseModel.Datum> mRewardHistoryList = new ArrayList<>();

        try {
            result = mSqLiteDatabase.query(TABLE_REWARD_HISTORY, new String[]{}, null, null,
                    null, null, null);

            if (result.moveToFirst()) {
                do {
                    RewardHistoryDatabaseModel.Datum mRewardHistoryModel = new RewardHistoryDatabaseModel.Datum();
                    mRewardHistoryModel.setAmount(result.getString(1));
                    mRewardHistoryModel.setAction(result.getString(2));
                    mRewardHistoryModel.setDisplayDate(result.getString(3));
                    mRewardHistoryList.add(mRewardHistoryModel);

                } while (result.moveToNext());
            }

        } catch (Exception e) {

        } finally {
            if (result != null) {
                result.close();
                result = null;
            }
        }

        return mRewardHistoryList;
    }

    //INSERT CATEGORY NAMES
    public Long insertCurrencyListData(String strTitle,
                                       String strAmount,
                                       String strIconURL,
                                       String strCurrencyName) {

        ContentValues values = new ContentValues();
        values.put(KEY_CURRENCY_TITLE, strTitle);
        values.put(KEY_CURRENCY_AMOUNT, strAmount);
        values.put(KEY_CURRENCY_ICON_URL, strIconURL);
        values.put(KEY_CURRENCY_NAME, strCurrencyName);

        return mSqLiteDatabase.insert(TABLE_CURRENCY_LIST, null, values);

    }

    //GET CATEGORY NAMES LIST
    public ArrayList<GetAccountDetailsModel.Token.Values> getCurrencyList() {
        ArrayList<GetAccountDetailsModel.Token.Values> mCurrencyList = new ArrayList<>();

        try {
            result = mSqLiteDatabase.query(TABLE_CURRENCY_LIST, new String[]{}, null, null,
                    null, null, null);

            if (result.moveToFirst()) {
                do {
                    GetAccountDetailsModel.Token.Values mCurrencyModel = new GetAccountDetailsModel.Token.Values();
                    mCurrencyModel.setSymbol(result.getString(1));
                    mCurrencyModel.setValue(result.getString(2));
                    mCurrencyModel.setIcon(result.getString(3));
                    mCurrencyModel.setName(result.getString(4));
                    mCurrencyList.add(mCurrencyModel);

                } while (result.moveToNext());
            }

        } catch (Exception e) {

        } finally {
            if (result != null) {
                result.close();
                result = null;
            }
        }

        return mCurrencyList;
    }

    //INSERT REVEALIT HISTORY IN LIVE MODE
    public Long insertRevealitHistoryData( int intMatchID,
              int intMediaID,
              String strMediaTitle,
              String strMediaType,
              String strMediaURL,
              String strMediaCoverArt,
              String strCurrentTime,
              String strPlaybackOffset,
              String strPlaybackDisplay,
              String strMatchTimeStamp,
              String strAllTimeStamp,
              String strAllTimeStampOffset ) {
        ContentValues values = new ContentValues();
        values.put(KEY_REVEALIT_HISTORY_MATCH_ID, intMatchID);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_ID, intMediaID);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_TYPE, strMediaTitle);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_TITLE, strMediaType);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_URL, strMediaURL);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_COVER_ART, strMediaCoverArt);
        values.put(KEY_REVEALIT_HISTORY_CURRENT_TIME, strCurrentTime);
        values.put(KEY_REVEALIT_HISTORY_PLAYBACK_OFFSET, strPlaybackOffset);
        values.put(KEY_REVEALIT_HISTORY_PLAYBACK_DISPLAY, strPlaybackDisplay);
        values.put(KEY_REVEALIT_HISTORY_MATCH_TIMESTAMP, strMatchTimeStamp);
        values.put(KEY_REVEALIT_HISTORY_ALL_TIMESTAMP, strAllTimeStamp);
        values.put(KEY_REVEALIT_HISTORY_ALL_TIMESTAMP_OFFSET, strAllTimeStampOffset);


        return mSqLiteDatabase.insert(TABLE_REVEALIT_HISTORY, null, values);

    }

    //GET REVEALIT HISTORY IN LIVE MODE
    public ArrayList<RevealitHistoryModel.Data> getRevealitHistoryData() {
        ArrayList<RevealitHistoryModel.Data> mRevealitHistoryList = new ArrayList<>();

        try {
            result = mSqLiteDatabase.query(TABLE_REVEALIT_HISTORY, new String[]{}, null, null,
                    null, null, null);

            if (result.moveToFirst()) {
                do {
                    RevealitHistoryModel.Data mRevealitHistoryModel = new RevealitHistoryModel.Data();
                    mRevealitHistoryModel.setMatch_id(result.getInt(1));
                    mRevealitHistoryModel.setMedia_id(result.getInt(2));
                    mRevealitHistoryModel.setMedia_title(result.getString(3));
                    mRevealitHistoryModel.setMedia_type(result.getString(4));
                    mRevealitHistoryModel.setMedia_url(result.getString(5));
                    mRevealitHistoryModel.setMedia_cover_art(result.getString(6));
                    mRevealitHistoryModel.setCurrent_time(result.getString(7));
                    mRevealitHistoryModel.setPlayback_offset(result.getString(8));
                    mRevealitHistoryModel.setPlayback_display(result.getString(9));
                    mRevealitHistoryModel.setMatch_time_stamp(result.getString(10));
                    mRevealitHistoryModel.setAllTimeStamp(result.getString(11));
                    mRevealitHistoryModel.setAllTimeStampOffset(result.getString(12));
                    mRevealitHistoryList.add(mRevealitHistoryModel);

                } while (result.moveToNext());
            }

        } catch (Exception e) {

        } finally {
            if (result != null) {
                result.close();
                result = null;
            }
        }

        return mRevealitHistoryList;
    }

    //GET REVEALIT HISTORY SIMULATION DATA FROM MEDIA ID
    public ArrayList<RevealitHistoryModel.Data> getRevealitHistoryItemFromMediaID(int mediaID) {
        ArrayList<RevealitHistoryModel.Data> mRevealitHistoryList = new ArrayList<>();
        try {
            result = mSqLiteDatabase.query(TABLE_REVEALIT_HISTORY,
                    new String[]{KEY_ID,
                            KEY_REVEALIT_HISTORY_MATCH_ID,
                            KEY_REVEALIT_HISTORY_MEDIA_ID,
                            KEY_REVEALIT_HISTORY_MEDIA_TYPE,
                            KEY_REVEALIT_HISTORY_MEDIA_TITLE,
                            KEY_REVEALIT_HISTORY_MEDIA_URL,
                            KEY_REVEALIT_HISTORY_MEDIA_COVER_ART,
                            KEY_REVEALIT_HISTORY_CURRENT_TIME,
                            KEY_REVEALIT_HISTORY_PLAYBACK_OFFSET,
                            KEY_REVEALIT_HISTORY_PLAYBACK_DISPLAY,
                            KEY_REVEALIT_HISTORY_MATCH_TIMESTAMP,
                            KEY_REVEALIT_HISTORY_ALL_TIMESTAMP,
                            KEY_REVEALIT_HISTORY_ALL_TIMESTAMP_OFFSET
                    },
                    KEY_REVEALIT_HISTORY_MEDIA_ID + "=?", new String[]{String.valueOf(mediaID)},
                    null, null, null, null);
            if (result.moveToFirst()) {
                do {
                    RevealitHistoryModel.Data mRevealitHistoryModel = new RevealitHistoryModel.Data();
                    mRevealitHistoryModel.setMatch_id(result.getInt(1));
                    mRevealitHistoryModel.setMedia_id(result.getInt(2));
                    mRevealitHistoryModel.setMedia_title(result.getString(3));
                    mRevealitHistoryModel.setMedia_type(result.getString(4));
                    mRevealitHistoryModel.setMedia_url(result.getString(5));
                    mRevealitHistoryModel.setMedia_cover_art(result.getString(6));
                    mRevealitHistoryModel.setCurrent_time(result.getString(7));
                    mRevealitHistoryModel.setPlayback_offset(result.getString(8));
                    mRevealitHistoryModel.setPlayback_display(result.getString(9));
                    mRevealitHistoryModel.setMatch_time_stamp(result.getString(10));
                    mRevealitHistoryModel.setAllTimeStamp(result.getString(11));
                    mRevealitHistoryModel.setAllTimeStampOffset(result.getString(12));
                    mRevealitHistoryList.add(mRevealitHistoryModel);

                } while (result.moveToNext());
            }
        } catch (Exception e) {

        }

        return mRevealitHistoryList;


    }
    public int updateRevealitHistoryList(int intMatchID,
                                      int intMediaID,
                                      String strMediaTitle,
                                      String strMediaType,
                                      String strMediaURL,
                                      String strMediaCoverArt,
                                      String strCurrentTime,
                                      String strPlaybackOffset,
                                      String strPlaybackDisplay,
                                      String strMatchTimeStamp,
                                      String strAllTimeStamp,
                                      String strAllTimeStampOffset ) {

        ContentValues values = new ContentValues();
        values.put(KEY_REVEALIT_HISTORY_MATCH_ID, intMatchID);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_ID, intMediaID);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_TYPE, strMediaTitle);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_TITLE, strMediaType);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_URL, strMediaURL);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_COVER_ART, strMediaCoverArt);
        values.put(KEY_REVEALIT_HISTORY_CURRENT_TIME, strCurrentTime);
        values.put(KEY_REVEALIT_HISTORY_PLAYBACK_OFFSET, strPlaybackOffset);
        values.put(KEY_REVEALIT_HISTORY_PLAYBACK_DISPLAY, strPlaybackDisplay);
        values.put(KEY_REVEALIT_HISTORY_MATCH_TIMESTAMP, strMatchTimeStamp);
        values.put(KEY_REVEALIT_HISTORY_ALL_TIMESTAMP, strAllTimeStamp);
        values.put(KEY_REVEALIT_HISTORY_ALL_TIMESTAMP_OFFSET, strAllTimeStampOffset);

        String whereArgs[] = new String[1];
        whereArgs[0] = "" + intMediaID;
        return mSqLiteDatabase.update(TABLE_REVEALIT_HISTORY, values, KEY_REVEALIT_HISTORY_MEDIA_ID + "= ?", whereArgs);

    }

    //INSERT REVEALIT HISTORY IN SIMULATION MODE
    public Long insertRevealitHistoryDataSimulation( int intMatchID,
                                           int intMediaID,
                                           String strMediaTitle,
                                           String strMediaType,
                                           String strMediaURL,
                                           String strMediaCoverArt,
                                           String strCurrentTime,
                                           String strPlaybackOffset,
                                           String strPlaybackDisplay,
                                           String strMatchTimeStamp,
                                           String strAllTimeStamp,
                                           String strAllTimeStampOffset ) {
        ContentValues values = new ContentValues();
        values.put(KEY_REVEALIT_HISTORY_MATCH_ID, intMatchID);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_ID, intMediaID);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_TYPE, strMediaTitle);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_TITLE, strMediaType);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_URL, strMediaURL);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_COVER_ART, strMediaCoverArt);
        values.put(KEY_REVEALIT_HISTORY_CURRENT_TIME, strCurrentTime);
        values.put(KEY_REVEALIT_HISTORY_PLAYBACK_OFFSET, strPlaybackOffset);
        values.put(KEY_REVEALIT_HISTORY_PLAYBACK_DISPLAY, strPlaybackDisplay);
        values.put(KEY_REVEALIT_HISTORY_MATCH_TIMESTAMP, strMatchTimeStamp);
        values.put(KEY_REVEALIT_HISTORY_ALL_TIMESTAMP, strAllTimeStamp);
        values.put(KEY_REVEALIT_HISTORY_ALL_TIMESTAMP_OFFSET, strAllTimeStampOffset);


        return mSqLiteDatabase.insert(TABLE_REVEALIT_HISTORY_SIMULATION, null, values);

    }

    //GET REVEALIT HISTORY IN SIMULATION MODE
    public ArrayList<RevealitHistoryModel.Data> getRevealitHistoryDataSimulation() {
        ArrayList<RevealitHistoryModel.Data> mRevealitHistoryList = new ArrayList<>();

        try {
            result = mSqLiteDatabase.query(TABLE_REVEALIT_HISTORY_SIMULATION, new String[]{}, null, null,
                    null, null, null);

            if (result.moveToFirst()) {
                do {
                    RevealitHistoryModel.Data mRevealitHistoryModel = new RevealitHistoryModel.Data();
                    mRevealitHistoryModel.setMatch_id(result.getInt(1));
                    mRevealitHistoryModel.setMedia_id(result.getInt(2));
                    mRevealitHistoryModel.setMedia_title(result.getString(3));
                    mRevealitHistoryModel.setMedia_type(result.getString(4));
                    mRevealitHistoryModel.setMedia_url(result.getString(5));
                    mRevealitHistoryModel.setMedia_cover_art(result.getString(6));
                    mRevealitHistoryModel.setCurrent_time(result.getString(7));
                    mRevealitHistoryModel.setPlayback_offset(result.getString(8));
                    mRevealitHistoryModel.setPlayback_display(result.getString(9));
                    mRevealitHistoryModel.setMatch_time_stamp(result.getString(10));
                    mRevealitHistoryModel.setAllTimeStamp(result.getString(11));
                    mRevealitHistoryModel.setAllTimeStampOffset(result.getString(12));
                    mRevealitHistoryList.add(mRevealitHistoryModel);

                } while (result.moveToNext());
            }

        } catch (Exception e) {

        } finally {
            if (result != null) {
                result.close();
                result = null;
            }
        }

        return mRevealitHistoryList;
    }

    //GET REVEALIT HISTORY SIMULATION DATA FROM MEDIA ID
    public ArrayList<RevealitHistoryModel.Data> getRevealitHistoryItemFromMediaIDSimulation(int mediaID) {
        ArrayList<RevealitHistoryModel.Data> mRevealitHistoryList = new ArrayList<>();
        try {
            result = mSqLiteDatabase.query(TABLE_REVEALIT_HISTORY_SIMULATION,
                    new String[]{KEY_ID,
                            KEY_REVEALIT_HISTORY_MATCH_ID,
                            KEY_REVEALIT_HISTORY_MEDIA_ID,
                            KEY_REVEALIT_HISTORY_MEDIA_TYPE,
                            KEY_REVEALIT_HISTORY_MEDIA_TITLE,
                            KEY_REVEALIT_HISTORY_MEDIA_URL,
                            KEY_REVEALIT_HISTORY_MEDIA_COVER_ART,
                            KEY_REVEALIT_HISTORY_CURRENT_TIME,
                            KEY_REVEALIT_HISTORY_PLAYBACK_OFFSET,
                            KEY_REVEALIT_HISTORY_PLAYBACK_DISPLAY,
                            KEY_REVEALIT_HISTORY_MATCH_TIMESTAMP,
                            KEY_REVEALIT_HISTORY_ALL_TIMESTAMP,
                            KEY_REVEALIT_HISTORY_ALL_TIMESTAMP_OFFSET
                    },
                    KEY_REVEALIT_HISTORY_MEDIA_ID + "=?", new String[]{String.valueOf(mediaID)},
                    null, null, null, null);
            if (result.moveToFirst()) {
                do {
                    RevealitHistoryModel.Data mRevealitHistoryModel = new RevealitHistoryModel.Data();
                    mRevealitHistoryModel.setMatch_id(result.getInt(1));
                    mRevealitHistoryModel.setMedia_id(result.getInt(2));
                    mRevealitHistoryModel.setMedia_title(result.getString(3));
                    mRevealitHistoryModel.setMedia_type(result.getString(4));
                    mRevealitHistoryModel.setMedia_url(result.getString(5));
                    mRevealitHistoryModel.setMedia_cover_art(result.getString(6));
                    mRevealitHistoryModel.setCurrent_time(result.getString(7));
                    mRevealitHistoryModel.setPlayback_offset(result.getString(8));
                    mRevealitHistoryModel.setPlayback_display(result.getString(9));
                    mRevealitHistoryModel.setMatch_time_stamp(result.getString(10));
                    mRevealitHistoryModel.setAllTimeStamp(result.getString(11));
                    mRevealitHistoryModel.setAllTimeStampOffset(result.getString(12));
                    mRevealitHistoryList.add(mRevealitHistoryModel);

                } while (result.moveToNext());
            }
        } catch (Exception e) {

        }

        return mRevealitHistoryList;


    }
    public int updateRevealitHistoryListSimulation(int intMatchID,
                                         int intMediaID,
                                         String strMediaTitle,
                                         String strMediaType,
                                         String strMediaURL,
                                         String strMediaCoverArt,
                                         String strCurrentTime,
                                         String strPlaybackOffset,
                                         String strPlaybackDisplay,
                                         String strMatchTimeStamp,
                                         String strAllTimeStamp,
                                         String strAllTimeStampOffset ) {

        ContentValues values = new ContentValues();
        values.put(KEY_REVEALIT_HISTORY_MATCH_ID, intMatchID);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_ID, intMediaID);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_TYPE, strMediaTitle);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_TITLE, strMediaType);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_URL, strMediaURL);
        values.put(KEY_REVEALIT_HISTORY_MEDIA_COVER_ART, strMediaCoverArt);
        values.put(KEY_REVEALIT_HISTORY_CURRENT_TIME, strCurrentTime);
        values.put(KEY_REVEALIT_HISTORY_PLAYBACK_OFFSET, strPlaybackOffset);
        values.put(KEY_REVEALIT_HISTORY_PLAYBACK_DISPLAY, strPlaybackDisplay);
        values.put(KEY_REVEALIT_HISTORY_MATCH_TIMESTAMP, strMatchTimeStamp);
        values.put(KEY_REVEALIT_HISTORY_ALL_TIMESTAMP, strAllTimeStamp);
        values.put(KEY_REVEALIT_HISTORY_ALL_TIMESTAMP_OFFSET, strAllTimeStampOffset);

        String whereArgs[] = new String[1];
        whereArgs[0] = "" + intMediaID;
        return mSqLiteDatabase.update(TABLE_REVEALIT_HISTORY, values, KEY_REVEALIT_HISTORY_MEDIA_ID + "= ?", whereArgs);

    }





    public long clearSimulationHistoryItem(int mediaID) {

        return mSqLiteDatabase.delete(TABLE_REVEALIT_HISTORY_SIMULATION, KEY_REVEALIT_HISTORY_MEDIA_ID + "= ?", new String[]{"" + mediaID});
    }

    public static void clearSimulationHistoryDataTable() {

        mSqLiteDatabase.delete(TABLE_REVEALIT_HISTORY_SIMULATION, null, null);
    }


    public static void clearCurrencyListTable() {

        mSqLiteDatabase.delete(TABLE_CURRENCY_LIST, null, null);
    }

    public static void clearRewardTable() {

        mSqLiteDatabase.delete(TABLE_REWARD_HISTORY, null, null);
    }

    public static void clearRevealitHistoryListTable() {

        mSqLiteDatabase.delete(TABLE_REVEALIT_HISTORY, null, null);
    }

    public static void clearAllTables() {

        mSqLiteDatabase.delete(TABLE_CATEGORY_NAMES, null, null);
        mSqLiteDatabase.delete(TABLE_CATEGORY_WISE_PLAY_LIST, null, null);
        mSqLiteDatabase.delete(TABLE_REVEALIT_HISTORY, null, null);
    }

}