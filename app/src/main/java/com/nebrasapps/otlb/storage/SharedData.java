package com.nebrasapps.otlb.storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;
/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */
public class SharedData {


    private static SharedPreferences sharedPreferences = null;
    private static SharedPreferences.Editor editor = null;

    private static SharedData instance;

    public static SharedData getPrefsHelper() {
        return instance;
    }

    public SharedData(Context context) {
        instance = this;
        String prefsFile = context.getPackageName();
        sharedPreferences = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void delete(String key) {
        if (sharedPreferences.contains(key)) {
            editor.remove(key).commit();
        }
    }

    public static void savePref(String key, Object value) {
        delete(key);

        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        }  else if (value != null) {
            throw new RuntimeException("Attempting to save non-primitive preference");
        }

        editor.commit();
    }
    public static void saveSetPref(String key, Set<String> value) {
        delete(key);


            editor.putStringSet(key,  value);

        editor.commit();
    }
    @SuppressWarnings("unchecked")
    public static <T> T getPref(String key) {
        return (T) sharedPreferences.getAll().get(key);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getPref(String key, T defValue) {
        T returnValue = (T) sharedPreferences.getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }

    public static boolean isPrefExists(String key) {
        return sharedPreferences.contains(key);
    }
    public static void reset() {

            editor.clear().commit();
    }

}
