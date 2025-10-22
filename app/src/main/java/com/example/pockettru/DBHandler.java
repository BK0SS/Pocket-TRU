package com.example.pockettru;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "coursesdb";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "courses";
    private static final String ID_COL = "id";
    private static final String NAME_COL = "name";
    private static final String DESCRIPTION_COL = "description";

    public DBHandler(Context context){super (context, DB_NAME, null, DB_VERSION);}


    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = " CREATE TABLE " + TABLE_NAME + "("
                + ID_COL + " TEXT PRIMARY KEY, "
                + NAME_COL + " INTEGER, "
                + DESCRIPTION_COL + " TEXT)";
        db.execSQL(query);
    }

    public ArrayList<CourseModel> readCourses(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        ArrayList<CourseModel> courseModelArrayList = new ArrayList<>();

        while(cursorCourses.moveToNext()){
            courseModelArrayList.add(new CourseModel(cursorCourses.getString(0),
                    cursorCourses.getString(1),
                    cursorCourses.getString(2)));
        }

        cursorCourses.close();
        return courseModelArrayList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
