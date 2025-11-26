package com.comp3160.pockettru;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class BookmarkDBHandler extends SQLiteOpenHelper {
    // Database Information
    private static final String DATABASE_NAME = "studygroups.db";
    private static final int DATABASE_VERSION = 1;


    public static final String TABLE_STUDYGROUPS = "studygroups";


    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DESCRIPTION = "description";

    // Creating table query
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_STUDYGROUPS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_AUTHOR + " TEXT NOT NULL, " +
                    COLUMN_TIME + " TEXT NOT NULL, " +
                    COLUMN_DATE + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT NOT NULL UNIQUE" +
                    ");";

    public BookmarkDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDYGROUPS);
        onCreate(db);
    }


    public void addStudyGroup(StudyGroupModel group) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        values.put(COLUMN_AUTHOR, group.getAuthor());
        values.put(COLUMN_DATE, group.getDate());
        values.put(COLUMN_TIME, group.getTime());
        values.put(COLUMN_DESCRIPTION, group.getDescription());


        db.insert(TABLE_STUDYGROUPS, null, values);
        db.close(); // Closing database connection
    }


    public List<StudyGroupModel> getAllStudyGroups() {
        List<StudyGroupModel> studyGroupList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_STUDYGROUPS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                StudyGroupModel group = new StudyGroupModel();

                group.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)));
                group.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                group.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)));
                group.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));


                studyGroupList.add(group);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return studyGroupList;
    }

    public boolean isBookmarked(String description) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_STUDYGROUPS + " WHERE " + COLUMN_DESCRIPTION + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{description});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }


    public void deleteStudyGroup(String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_STUDYGROUPS, COLUMN_DESCRIPTION + " = ?", new String[]{description});
        db.close();
    }

    //An UPDATE method
    public int updateStudyGroup(StudyGroupModel group) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AUTHOR, group.getAuthor());
        values.put(COLUMN_DATE, group.getDate());
        values.put(COLUMN_TIME, group.getTime());
        values.put(COLUMN_DESCRIPTION, group.getDescription());
        return db.update(TABLE_STUDYGROUPS, values, COLUMN_DESCRIPTION + " = ?",
                new String[]{String.valueOf(group.getDescription())});
    }
}
