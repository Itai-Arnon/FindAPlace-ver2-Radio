package com.project.itai.FindAPlaceVer2.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.project.itai.FindAPlaceVer2.beans.SearchPlace;
import com.project.itai.FindAPlaceVer2.constants.SearchConstants;
import com.project.itai.FindAPlaceVer2.helper.SearchDBHelper;

import java.util.ArrayList;
import java.util.List;


/***********************************************WILL REPLACE DAO AT MAIN FILE***********************************/
/*
 * itai arnon  Start OF Work:  21.08,18
 *LEFT not working all the way doesn't load movies
 *
 *
 *
 **
 *
 * *************************************************************************************************************/

public class SearchDao {

    private SearchDBHelper dbhelper;
    private final static String LOG_TAG = "ITAI";


    public SearchDao(Context context) {
        dbhelper = new SearchDBHelper(context, SearchConstants.DATABASE_NAME, null,
                SearchConstants.DATABASE_VERSION);
    }

    // Adding a new searchPlace, will return 1 if no exception was made
    public long addSearchPlace(SearchPlace searchPlace) {
        long id = 0;
        //Content values uses put instead of the Result Set object
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues newSearchValues=getContentValuesFromBean(searchPlace);

        try {
            id = db.insertOrThrow(SearchConstants.TABLE_NAME, null, newSearchValues);

        } catch (SQLiteException ex) {
            Log.e(LOG_TAG, ex.getMessage());
            throw ex;
        } finally {
            db.close();
        }
        return id;
    }

    /***********************************************WILL REPLACE DAO AT MAIN FILE***********************************/

    public SearchPlace getSearchPlace(long id) {
        SearchPlace tmp = new SearchPlace();

        SQLiteDatabase db = dbhelper.getReadableDatabase();

        try {
            Cursor cursor = db.query(SearchConstants.TABLE_NAME, null, SearchConstants.SEARCH_ID + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);


            if (cursor.moveToFirst()) {
               tmp = getSearchPlaceFromCursor(cursor);
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
            throw ex;
        } finally {
            db.close();
        }

        return tmp;
    }

    /***********************************************WILL REPLACE DAO AT MAIN FILE***********************************/

    // Update a movie - requires the entire bean
    public long updateSearchPlace(SearchPlace searchPlace) {
        long id = 0;
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        ContentValues   values=getContentValuesFromBean(searchPlace);


        try {
            id = db.update(SearchConstants.TABLE_NAME, values, SearchConstants.SEARCH_ID
                    + "= ?", new String[]{String.valueOf(searchPlace.getId())});
        } catch (SQLiteException ex) {
            Log.e(LOG_TAG, ex.getMessage());
            throw ex;
        } finally {
            db.close();
        }
        return id;
    }

    public void deleteSearchPlace(long id) {//not all places
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        db.delete(SearchConstants.TABLE_NAME, SearchConstants.SEARCH_ID + "=" + id, null);
        db.close();
    }

    /*returns the entire list of movies|getSearchPlaceFromCursor aids in moving from the cursor to the ArrayList*/
    public List<SearchPlace> getAllSearchPlaces() {
        List<SearchPlace> searches = new ArrayList<>();
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor cursor = db.query(SearchConstants.TABLE_NAME, null, null,
                null, null, null, null);
        try {
            while (cursor.moveToNext())
                //getSearchPlaceFromCursor - adds one by one movie object translated from cursor
                searches.add(getSearchPlaceFromCursor(cursor));
        } catch (SQLiteException err) {
            Log.e(LOG_TAG, err.getMessage());
            throw err;
        } finally {
            db.close();
            cursor.close();
        }
        return searches;
    }

    // method to delete all movies from the DB (for the menu option: delete all)
    public void deleteAllSearchPlaces() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        try {
            db.delete(SearchConstants.TABLE_NAME, null, null);

        } catch (SQLiteException err) {
            Log.e(LOG_TAG, err.getMessage());
            throw err;
        } finally {
            db.close();

        }
    }

    /***********************************************WILL REPLACE DAO AT MAIN FILE***********************************/


    public boolean isSameSearchPlace(String name, String overView) {
        boolean exists = true;
        SQLiteDatabase db = dbhelper.getReadableDatabase();

//        String addrWhere = DbConstants.TITLE + " = ? AND "+DbConstants.OVERVIEW+" = ?"; //this is where clause and below is corresponding selection args..
        String[] addrWhereParams = new String[]{name, overView};
        Cursor cursor = db.query(SearchConstants.TABLE_NAME, null, SearchConstants.SEARCH_NAME +
                        "=?" + " AND " + SearchConstants.SEARCH_ADDRESS + "=?",//, DbConstants.XINDEX
                addrWhereParams, null, null, null, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }


    // assuring a movie exists
    public boolean isExistentInDB(SearchPlace movie) {
        SQLiteDatabase db = dbhelper.getReadableDatabase();


        Cursor cursor = db.query(SearchConstants.TABLE_NAME, null,
                SearchConstants.SEARCH_NAME + "=?" + " AND " + SearchConstants.SEARCH_ADDRESS + "=?",
                new String[]{String.valueOf(movie.getName()), String.valueOf(movie.getAddress())}, null, null, null, null);

        // Check if the SearchPlace was found
        if (cursor.moveToFirst()) {
            return true;
        }

        return false;
    }

    public long getSearchPlaceId(SearchPlace movie) {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor cursor;
        try {
            cursor = db.query(SearchConstants.TABLE_NAME, null,
                    SearchConstants.SEARCH_NAME + "=?" + " AND " + SearchConstants.SEARCH_ADDRESS + "=?",
                    new String[]{String.valueOf(movie.getName()),
                            String.valueOf(movie.getAddress())}, null, null, null, null);
        } catch (SQLiteException err) {
            Log.e(LOG_TAG, err.getMessage());
            throw err;
        }

        if (cursor.moveToFirst()) { // Check if the SearchPlace was found
            long id = cursor.getInt(cursor.getColumnIndex(SearchConstants.SEARCH_ID));
            return id;
        }
        return 0;
    }//end of dao


    //private function that transforms cursor entry to object type movie
    private SearchPlace getSearchPlaceFromCursor(Cursor cursor) {
        SearchPlace tmp = new SearchPlace();
        tmp.setId(cursor.getInt(0));// set ID
        tmp.setName(cursor.getString(1));//Subject
        tmp.setAddress(cursor.getString(2));//body
        tmp.setUrlImage(cursor.getString(3));//url
        tmp.setLat(cursor.getDouble(4));//Rating
        tmp.setLng(cursor.getDouble(5));//getLng  integer is converted to boolean
        tmp.setTimeStamp(cursor.getLong(6));
        return tmp;//tmp is the requested movie object
    }

    private ContentValues getContentValuesFromBean(SearchPlace searchPlace) {
//meant for update and add
        ContentValues values = new ContentValues();

        values.put(SearchConstants.SEARCH_NAME, searchPlace.getName());
        values.put(SearchConstants.SEARCH_ADDRESS, searchPlace.getAddress());
        values.put(SearchConstants.SEARCH_URL, searchPlace.getUrlImage());
        values.put(SearchConstants.SEARCH_LONGTITUDE, searchPlace.getLng());
        values.put(SearchConstants.SEARCH_LATITUDE, searchPlace.getLat());
        values.put(SearchConstants.SEARCH_TIME, searchPlace.getTimeStamp());
        return values;
    }
}



