
package com.example.btn_duolingo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Duolingo.db";
    private static final int DATABASE_VERSION = 6; // Tăng lên 6 để nạp thêm email

    private static final String TABLE_USER = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_FULLNAME = "fullName";
    private static final String COLUMN_DOB = "dob";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_XP = "xp";
    private static final String COLUMN_STREAK = "streak";

    private static final String TABLE_EXERCISE = "exercises";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_QUESTION = "question";
    private static final String COLUMN_OPTIONS = "options";
    private static final String COLUMN_ANSWER = "answer";

    private static final String TABLE_PROGRESS = "user_progress";
    private static final String COLUMN_PROGRESS_USERNAME = "username";
    private static final String COLUMN_PROGRESS_LESSON_TITLE = "lesson_title";
    private static final String COLUMN_PROGRESS_COMPLETED = "completed";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_FULLNAME + " TEXT, " +
                COLUMN_DOB + " TEXT, " +
                COLUMN_ADDRESS + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_XP + " INTEGER, " +
                COLUMN_STREAK + " INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_EXERCISE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_QUESTION + " TEXT, " +
                COLUMN_OPTIONS + " TEXT, " +
                COLUMN_ANSWER + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_PROGRESS + " (" +
                COLUMN_PROGRESS_USERNAME + " TEXT, " +
                COLUMN_PROGRESS_LESSON_TITLE + " TEXT, " +
                COLUMN_PROGRESS_COMPLETED + " INTEGER, " +
                "PRIMARY KEY (" + COLUMN_PROGRESS_USERNAME + ", " + COLUMN_PROGRESS_LESSON_TITLE + "))");

        importDataFromJson(db, "exercises.json");
        importDataFromJson(db, "exercises_cn.json");
        importUsersFromJson(db);
    }

    private void importUsersFromJson(SQLiteDatabase db) {
        Gson gson = new Gson();
        String usersJson = loadJSONFromAsset("users.json");
        if (usersJson != null) {
            Type userListType = new TypeToken<List<User>>() {}.getType();
            List<User> users = gson.fromJson(usersJson, userListType);
            if (users != null) {
                for (User user : users) {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_USERNAME, user.getUsername());
                    values.put(COLUMN_PASSWORD, user.getPassword());
                    values.put(COLUMN_EMAIL, user.getEmail());
                    values.put(COLUMN_FULLNAME, user.getFullName());
                    values.put(COLUMN_DOB, user.getDob());
                    values.put(COLUMN_ADDRESS, user.getAddress());
                    values.put(COLUMN_PHONE, user.getPhone());
                    values.put(COLUMN_XP, user.getXp());
                    values.put(COLUMN_STREAK, user.getStreak());
                    db.insert(TABLE_USER, null, values);
                }
            }
        }
    }

    private void importDataFromJson(SQLiteDatabase db, String fileName) {
        Gson gson = new Gson();
        String exJson = loadJSONFromAsset(fileName);
        if (exJson != null) {
            Type exListType = new TypeToken<List<Exercise>>() {}.getType();
            List<Exercise> exercises = gson.fromJson(exJson, exListType);
            if (exercises != null) {
                for (Exercise ex : exercises) {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_ID, ex.getId());
                    values.put(COLUMN_TITLE, ex.getTitle());
                    values.put(COLUMN_DESCRIPTION, ex.getDescription());
                    values.put(COLUMN_QUESTION, ex.getQuestion());
                    values.put(COLUMN_OPTIONS, ex.getOptions());
                    values.put(COLUMN_ANSWER, ex.getAnswer());
                    db.insert(TABLE_EXERCISE, null, values);
                }
            }
        }
    }

    private String loadJSONFromAsset(String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRESS);
        onCreate(db);
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER, null);
        if (cursor.moveToFirst()) {
            do {
                users.add(new User(
                        cursor.getString(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5),
                        cursor.getString(6), cursor.getInt(7), cursor.getInt(8)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }

    public List<Exercise> getUniqueLessons() {
        List<Exercise> lessons = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COLUMN_TITLE + ", " + COLUMN_DESCRIPTION + " FROM " + TABLE_EXERCISE, null);
        if (cursor.moveToFirst()) {
            do {
                lessons.add(new Exercise(0, cursor.getString(0), cursor.getString(1), "", "", ""));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lessons;
    }

    public List<Exercise> getExercisesByTitle(String title) {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EXERCISE, null, COLUMN_TITLE + "=?", new String[]{title}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                exercises.add(new Exercise(
                        cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return exercises;
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getString(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5),
                    cursor.getString(6), cursor.getInt(7), cursor.getInt(8));
            cursor.close();
            return user;
        }
        return null;
    }

    public boolean updateUserInfo(String oldUsername, String newUsername, String fullName, String password, String dob, String address, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, newUsername);
        values.put(COLUMN_FULLNAME, fullName);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_DOB, dob);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_PHONE, phone);
        int result = db.update(TABLE_USER, values, COLUMN_USERNAME + "=?", new String[]{oldUsername});
        return result > 0;
    }

    public boolean insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_FULLNAME, user.getFullName());
        values.put(COLUMN_DOB, user.getDob());
        values.put(COLUMN_ADDRESS, user.getAddress());
        values.put(COLUMN_PHONE, user.getPhone());
        values.put(COLUMN_XP, user.getXp());
        values.put(COLUMN_STREAK, user.getStreak());
        long result = db.insert(TABLE_USER, null, values);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{username, password}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public void markLessonCompleted(String username, String lessonTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROGRESS_USERNAME, username);
        values.put(COLUMN_PROGRESS_LESSON_TITLE, lessonTitle);
        values.put(COLUMN_PROGRESS_COMPLETED, 1);
        db.insertWithOnConflict(TABLE_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public boolean isLessonCompleted(String username, String lessonTitle) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROGRESS, null, 
                COLUMN_PROGRESS_USERNAME + "=? AND " + COLUMN_PROGRESS_LESSON_TITLE + "=?",
                new String[]{username, lessonTitle}, null, null, null);
        boolean completed = (cursor.getCount() > 0);
        cursor.close();
        return completed;
    }

    public void addXP(String username, int xpAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_USER + " SET " + COLUMN_XP + " = " + COLUMN_XP + " + ? WHERE " + COLUMN_USERNAME + " = ?", new Object[]{xpAmount, username});
    }
}
