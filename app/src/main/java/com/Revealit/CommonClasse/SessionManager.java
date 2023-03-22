package com.Revealit.CommonClasse;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    //----(Added by ketan patel)--------//
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    Context mContext;

    public SessionManager(Context mContext) {
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
    }


    //-------(open share preference settings)-------//
    public void openSettings() {
        if (settings == null) {
            settings = mContext.getSharedPreferences("revealit", Context.MODE_PRIVATE);

        }
    }
    //-------(clear share preference )-------//
    public void clearSharePreference() {
        editor.clear();
        editor.apply();
    }



    //-------(update strring preferences)--------//
    public boolean updatePreferenceString(String key, String value) {
        editor = settings.edit();
        return editor.putString(key, value).commit();

    }


    //--------(Update integer preferences)--------//
    public boolean updatePreferenceInteger(String key, int value) {
        editor = settings.edit();
        return editor.putInt(key, value).commit();

    }

    //--------(Update Boolean preferences)--------//
    public boolean updatePreferenceBoolean(String key, boolean value) {
        editor = settings.edit();
        return editor.putBoolean(key, value).commit();

    }

    //--------(Update Long preferences)--------//
    public boolean updatePreferenceLong(String key, Long value) {
        editor = settings.edit();
        return editor.putLong(key, value).commit();

    }


    //--------(get Boolean preferences)--------//
    public boolean getPreferenceBoolean(String key) {
        return settings.getBoolean(key, false);
    }

    //--------(get String preferences)--------//
    public String getPreference(String key) {
        return settings.getString(key, "");
    }

    //--------(get Integer preferences)--------//
    public int getPreferenceInt(String key) {
        return settings.getInt(key, 0);
    }

    //--------(get Long preferences)--------//
    public Long getPreferenceLong(String key) {
        return settings.getLong(key, 0);
    }

    //-----(Remove preferences from share preference)------//
    public boolean removePreference(String key) {
        editor = settings.edit();
        return editor.remove(key).commit();
    }
}
