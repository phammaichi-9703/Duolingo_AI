package com.example.btl;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class PreferenceManager {
    private static final String PREF_NAME = "BTL_PREFS";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOTAL_XP = "totalXp";
    private static final String KEY_STREAK = "streak";
    private static final String KEY_LESSONS_DONE = "lessonsDone";
    private static final String KEY_DARK_MODE = "darkMode";
    private static final String KEY_AVATAR_URI = "avatarUri";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putString(KEY_USERNAME, "Admin");
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "Admin");
    }

    public void addXp(int xp) {
        int currentXp = getTotalXp();
        editor.putInt(KEY_TOTAL_XP, currentXp + xp);
        editor.apply();
    }

    public int getTotalXp() {
        return sharedPreferences.getInt(KEY_TOTAL_XP, 0);
    }

    public int getStreak() {
        return sharedPreferences.getInt(KEY_STREAK, 7);
    }

    public int getLessonsDone() {
        return sharedPreferences.getInt(KEY_LESSONS_DONE, 0);
    }

    public void addLessonDone() {
        editor.putInt(KEY_LESSONS_DONE, getLessonsDone() + 1);
        editor.apply();
    }

    public void setAvatarUri(String uri) {
        editor.putString(KEY_AVATAR_URI, uri);
        editor.apply();
    }

    public String getAvatarUri() {
        return sharedPreferences.getString(KEY_AVATAR_URI, null);
    }

    public void setDarkMode(boolean isDark) {
        editor.putBoolean(KEY_DARK_MODE, isDark);
        editor.apply();
        applyTheme();
    }

    public boolean isDarkMode() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }

    public void applyTheme() {
        if (isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
