package com.project.itai.FindAPlaceVer2.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import  com.project.itai.FindAPlaceVer2.constants.SearchConstants;

public class SearchDBHelper extends SQLiteOpenHelper {
    private static String LOG_TAG = "SearchDBHelper";

    public SearchDBHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, name, factory, version);
    }

    /*******************LEFT FOR FUTURE USE*********************************************************************/
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "Creating all the tables");
        String CREATE_MOVIES_TABLE = " CREATE TABLE " + SearchConstants.TABLE_NAME +
                "(" + SearchConstants.SEARCH_ID + " INTEGER PRIMARY KEY, " + SearchConstants.SEARCH_NAME + " TEXT,"
                + SearchConstants.SEARCH_ADDRESS + " TEXT, "+ SearchConstants.SEARCH_URL+ " TEXT, "
                + SearchConstants.SEARCH_LONGTITUDE + " REAL, " + SearchConstants.SEARCH_LATITUDE +" REAL, " + SearchConstants.SEARCH_TIME + " REAL )";

          // End: SearchConstants.MOVIE_URL+ " TEXT )";*/
        try {
            db.execSQL(CREATE_MOVIES_TABLE);
        } catch (SQLiteException ex) {
            Log.e(LOG_TAG, "Create table exception: " +
                    ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int
            newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion +
                " to " + newVersion + ", which will destroy all old date");
        db.execSQL("DROP TABLE IF EXISTS " + SearchConstants.TABLE_NAME);
        onCreate(db);
    }//end of onUpgrade
}// end of class








    

