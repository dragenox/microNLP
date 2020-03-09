package com.rcpit.g36fyp1920.micronlp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "microNLP";
    private static final String TABLE_FAKENEWS = "fakenews";
    private static final String KEY_ID = "id";
    private static final String KEY_DATA = "data";
    private static final String KEY_LINK = "link";
    private static final String KEY_TAGS = "tags";
    private static final String KEY_SEVERITY = "severity";


    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 10);
    }

    public boolean checkDataBase() {
        SQLiteDatabase checkDB = this.getReadableDatabase();
        return checkDB != null;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAKENEWS_TABLE = "CREATE TABLE " + TABLE_FAKENEWS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATA + " TEXT,"
                + KEY_LINK + " TEXT," + KEY_TAGS + " TEXT," + KEY_SEVERITY + " INTEGER" + ")";
        db.execSQL(CREATE_FAKENEWS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAKENEWS);

        // Create tables again
        onCreate(db);
    }

    void addFakeNews(Fakenews fakenews) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATA, fakenews.getData());
        values.put(KEY_LINK, fakenews.getLink());
        values.put(KEY_TAGS, fakenews.getTags());
        values.put(KEY_SEVERITY, fakenews.getSeverity());

        db.insert(TABLE_FAKENEWS, null, values);
        db.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    Fakenews searchFakeNews(String text) {
        SQLiteDatabase db = this.getReadableDatabase();
        Fakenews fakenews = null;
        Cursor cursor = db.query(TABLE_FAKENEWS, new String[]{KEY_ID,
                        KEY_DATA, KEY_LINK, KEY_TAGS, KEY_SEVERITY}, KEY_DATA + "=?",
                new String[]{text}, null, null, null, null);
        cursor.moveToFirst();
        if(cursor.getCount()>0){
            fakenews = new Fakenews(Integer.parseInt(Objects.requireNonNull(cursor).getString(0)),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3), Integer.parseInt(cursor.getString(4)));
            cursor.close();
        }

        // return news
        return fakenews;

    }

    int getFakeNewsCount() {
        int count;
        String countQuery = "SELECT  * FROM " + TABLE_FAKENEWS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }
}