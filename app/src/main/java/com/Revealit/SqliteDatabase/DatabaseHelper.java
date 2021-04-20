package com.Revealit.SqliteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.Revealit.ModelClasses.CoursesModel;

import java.util.ArrayList;

public class DatabaseHelper {

    public static final String DATABASE_NAME = "raviEdu.db";
    public static final int DATABASE_VERSION = 1;

    // Table Names
    public static String TABLE_COURSE_NAME = "tableAddCourseDetails";

    //FIELD
    public static String KEY_ID = "keyID";
    public static String KEY_COURSE_ID = "id";
    public static String KEY_STANDARD = "standard";
    public static String KEY_SUBJECT = "subject";
    public static String KEY_CHAPTER_NAME = "chapterName";
    public static String KEY_DESCRIPTION = "description";
    public static String KEY_LINK = "link";

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


    //INSERT ADD PATIENT
    public Long insertCourseData(int id,
                                 int standard,
                                 String subject,
                                 String chapterName,
                                 String description,
                                 String link) {

        ContentValues values = new ContentValues();
        values.put(KEY_COURSE_ID, id);
        values.put(KEY_STANDARD, standard);
        values.put(KEY_SUBJECT, subject);
        values.put(KEY_CHAPTER_NAME, chapterName);
        values.put(KEY_DESCRIPTION, description);
        values.put(KEY_LINK, link);

        return mSqLiteDatabase.insert(TABLE_COURSE_NAME, null, values);

    }

    //GET PATIENT RECORD
    public ArrayList<CoursesModel.DataBean> getAllCoursesList() {
        ArrayList<CoursesModel.DataBean> CoursesModelList = new ArrayList<>();

        try {
            result = mSqLiteDatabase.query(TABLE_COURSE_NAME, new String[]{}, null, null,
                    null, null, null);

            if (result.moveToFirst()) {
                do {
                    CoursesModel.DataBean mPatientModel = new CoursesModel.DataBean();
                    mPatientModel.setId(result.getInt(1));
                    mPatientModel.setStandard(result.getInt(2));
                    mPatientModel.setSubject(result.getString(3));
                    mPatientModel.setChapterName(result.getString(4));
                    mPatientModel.setDescription(result.getString(5));
                    mPatientModel.setLink(result.getString(6));

                    CoursesModelList.add(mPatientModel);

                } while (result.moveToNext());
            }

        } catch (Exception e) {

        } finally {
            if (result != null) {
                result.close();
                result = null;
            }
        }


        return CoursesModelList;
    }


    //GET PATIENT RECORD BY DATE
    public ArrayList<CoursesModel.DataBean> getChapterList(int intStandard, String strSubject) {
        ArrayList<CoursesModel.DataBean> CoursesModelList = new ArrayList<>();

        try {
            String query = "select * from "+ TABLE_COURSE_NAME + " where "+ KEY_STANDARD+ "="+ 8 + " and "+ KEY_SUBJECT +"like"+strSubject;


            result = mSqLiteDatabase.rawQuery(query, null);

            if (result.moveToFirst()) {
                do {
                    CoursesModel.DataBean mPatientModel = new CoursesModel.DataBean();
                    mPatientModel.setId(result.getInt(1));
                    mPatientModel.setStandard(result.getInt(2));
                    mPatientModel.setSubject(result.getString(3));
                    mPatientModel.setChapterName(result.getString(4));
                    mPatientModel.setDescription(result.getString(5));

                    CoursesModelList.add(mPatientModel);

                } while (result.moveToNext());
            }

        } catch (Exception e) {

        } finally {
            if (result != null) {
                result.close();
                result = null;
            }
        }


        return CoursesModelList;
    }

    //GET BANK NAMES
    public ArrayList<CoursesModel.DataBean> getSubjectlist(int intStandard) {
        ArrayList<CoursesModel.DataBean> CoursesModelList = new ArrayList<>();


        try {

            result = mSqLiteDatabase.query
                    (TABLE_COURSE_NAME,
                            new String[]{
                                    KEY_ID,
                                    KEY_COURSE_ID,
                                    KEY_STANDARD,
                                    KEY_SUBJECT,
                                    KEY_CHAPTER_NAME,
                                    KEY_DESCRIPTION,
                                    KEY_LINK
                            },
                            KEY_STANDARD + "=" + intStandard,
                            null, null, null, null, null
                    );

            if (result.moveToFirst()) {
                do {
                    CoursesModel.DataBean mPatientModel = new CoursesModel.DataBean();
                    mPatientModel.setId(result.getInt(1));
                    mPatientModel.setStandard(result.getInt(2));
                    mPatientModel.setSubject(result.getString(3));
                    mPatientModel.setChapterName(result.getString(4));
                    mPatientModel.setDescription(result.getString(5));

                    CoursesModelList.add(mPatientModel);

                } while (result.moveToNext());
            }

        } catch (Exception e) {

        } finally {
            if (result != null) {
                result.close();
                result = null;
            }
        }


        return CoursesModelList;
    }

    public static void clearCoursesTables() {

        mSqLiteDatabase.delete(TABLE_COURSE_NAME, null, null);
    }

}