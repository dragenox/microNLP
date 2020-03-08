package com.rcpit.g36fyp1920.micronlp;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.io.IOException;

public class microNLP {
    public static void test(Context context) {
        Toast.makeText(context, "Library Configured", Toast.LENGTH_SHORT).show();
        initDatabase(context);
    }

    public static String summary(String text) {
        return text;
    }

    public static String sentiment(String text){
        return text;
    }

    private static void initDatabase(Context context) {
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        myDbHelper.openDataBase();
        Toast.makeText(context, "Successfully Imported", Toast.LENGTH_SHORT).show();
        Cursor cursor = myDbHelper.query("microNLP", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Toast.makeText(context,
                        "_id: " + cursor.getString(0) + "\n" +
                                "data: " + cursor.getString(1) + "\n" +
                                "link: " + cursor.getString(2) + "\n" +
                                "tags:  " + cursor.getString(3)+ "\n" +
                                "severity:  " + cursor.getString(4),
                        Toast.LENGTH_LONG).show();
            } while (cursor.moveToNext());
        }
    }
}
