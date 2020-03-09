package com.rcpit.g36fyp1920.micronlp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class microNLP {
    private static final String microNLPPreference = "mNLPPref";
    private static final String DATE = "dateKey";
    private static final String VIOLATIONS = "violationsKey";
    private static final String TOTAL_SEVERITY = "totalSeverityKey";
    private static final long HRS_24 = 86400;

    public static void test(Context context) {
        Toast.makeText(context, "Library Configured", Toast.LENGTH_SHORT).show();
    }

    public static void initMicroNLP(Context context){
        getNewsList(context);
    }

    private static void getNewsList(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        try {
            JSONObject json = new JSONObject(loadJSONFromAsset(context));
            JSONArray jArray = json.getJSONArray("fakenews");

            if (databaseHelper.getFakeNewsCount()==0) {
                for(int i=0; i<jArray.length(); i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    databaseHelper.addFakeNews(new Fakenews(
                            json_data.getString("data"),
                            json_data.getString("link"),
                            json_data.getString("tags"),
                            json_data.getInt("severity")));
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "DB has: " + databaseHelper.getFakeNewsCount() + " values", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static AlertDialog searchMessage(final Context context, String text) {
        final DatabaseHelper databaseHelper = new DatabaseHelper(context);
        final Fakenews fakenews = databaseHelper.searchFakeNews(text);
        if (fakenews != null){
            String message = "The message you are trying to send is a fake message. " +
                    "It is advised to not forward such messages \n"
                    + fakenews.getLink();
            SpannableString formattedMessage = new SpannableString(message); // msg should have url to enable clicking
            Linkify.addLinks(formattedMessage, Linkify.ALL);
            return new AlertDialog.Builder(context)
                    .setTitle("Alert!")
                    .setPositiveButton(R.string.continue_send, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!microNLP.checkViolations(context)){
                                setViolations(context, fakenews.getSeverity());
                            }
                        }
                    })
                    .setNegativeButton(R.string.open_link, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fakenews.getLink())));
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(context, "No application can handle this request."
                                        + " Please install a web browser",  Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setMessage(formattedMessage)
                    .create();
        }
        else {
            return null;
        }
    }

    private static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("fakenews.json");
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void setViolations(Context context, int severity) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(microNLPPreference,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(sharedPreferences.contains(DATE)){
            if(System.currentTimeMillis()/1000 - Long.parseLong(Objects.requireNonNull(sharedPreferences.getString(DATE, ""))) > HRS_24){
                editor.putString(DATE, "" + System.currentTimeMillis()/1000);
                editor.putInt(VIOLATIONS, 0);
                editor.putInt(TOTAL_SEVERITY, 0);
                editor.apply();
            }
            else {
                editor.putInt(VIOLATIONS, sharedPreferences.getInt(VIOLATIONS,0)+1);
                editor.putInt(TOTAL_SEVERITY, sharedPreferences.getInt(TOTAL_SEVERITY,0) + severity);
                editor.apply();
            }
        }else {
            editor.putString(DATE, "" + System.currentTimeMillis()/1000);
            editor.putInt(VIOLATIONS, 0);
            editor.apply();
        }
    }

    public static int getViolations(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(microNLPPreference,
                Context.MODE_PRIVATE);
        return sharedPreferences.getInt(VIOLATIONS,0);
    }

    public static boolean checkViolations(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(microNLPPreference,
                Context.MODE_PRIVATE);
        return sharedPreferences.getInt(VIOLATIONS, 0) > 3;
    }
    public static int getTotalSeverity(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(microNLPPreference,
                Context.MODE_PRIVATE);
        return sharedPreferences.getInt(TOTAL_SEVERITY, 0);
    }
}