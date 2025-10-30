package com.example.pockettru;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
//    private static String TAG = "DBHandler";
    private static String DB_PATH = "";
    private final Context mContext;
    private static final String DB_NAME = "coursesdb";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "courses";
    private static final String ID_COL = "id";
    private static final String NAME_COL = "name";
    private static final String DESCRIPTION_COL = "description";
    private SQLiteDatabase db;

    public DBHandler(Context context){
        super (context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        DB_PATH = context.getDatabasePath(DB_NAME).getAbsolutePath();
    }

    public void createDataBase() throws IOException {

        boolean mDatabaseExist = checkDataBase();
        if(!mDatabaseExist){
            this.getReadableDatabase();
            try{
                copyDataBase();
            } catch(IOException e){
                throw new Error("Error Copying DataBase");
            }
        }
    }

    private boolean checkDataBase(){
        File dbFile = new File(DB_PATH);
        return dbFile.exists();
    }

    private void copyDataBase() throws IOException{
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        OutputStream mOutput = new FileOutputStream(DB_PATH);

        byte[] mBuffer = new byte[1024];
        int length;
        while((length = mInput.read(mBuffer)) > 0){
            mOutput.write(mBuffer, 0, length);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

//    public boolean openDataBase() throws SQLException{
//        db = SQLiteDatabase.openDatabase(DB_FILE, null, SQLiteDatabase.CREATE_IF_NECESSARY);
//        return db != null;
//    }
//
//    @Override
//    public synchronized void close(){
//        if(db != null){
//            db.close();
//        }
//        super.close();
//    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (newVersion > oldVersion){
            try{
                copyDataBase();
                Log.i("DBHandler", "Database successfully upgraded and re-copied");
            } catch (IOException e){
                e.printStackTrace();
                Log.e("DBHandler", "Error copying database during upgrade.", e);
            }
        }
    }

    public ArrayList<CourseModel> readCourses(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " +  ID_COL + " ASC",null);

        ArrayList<CourseModel> courseModelArrayList = new ArrayList<>();

        while(cursorCourses.moveToNext()){
            courseModelArrayList.add(new CourseModel(cursorCourses.getString(0),
                    cursorCourses.getString(1),
                    cursorCourses.getString(2)));
        }

        cursorCourses.close();
        return courseModelArrayList;
    }

//    @Override
//    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        onCreate(db);
//    }
}
