package com.project.itai.FindAPlaceVer2.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.itai.FindAPlaceVer2.beans.Place;
import com.project.itai.FindAPlaceVer2.constants.PlacesConstants;

import java.lang.reflect.Type;
import java.util.ArrayList;

//Application Class is in charge of communications between the app and the Android Framework
public class MyApp extends Application {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private final String FIRST_TIME_FLAG = "first_time_flag";
    private final String FIRST_TIME_FLAG_B = "first_time_flag_b";
    private final String JSSTRING_DEFAULT = "js_string_default";
    private final String JSSTRING = "json_string";
    private ArrayList<Place> placesDataDefault = new ArrayList<>();


    private static Context context;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //allow the Instruction Dialog to work only once

        placesDataDefault.add(new Place("Nassau", 25.05, -77.4833, "New Providence", "Bahamas", "tmp"));
        placesDataDefault.add(new Place("Brussels", 50.833, 4.33, "Belgium", "", "tmp"));
        placesDataDefault.add(new Place("Vienna", 48.2, 16.337, "Austria", "", "tmp"));
        placesDataDefault.add(new Place("Sydney", -1 * 33.8688, 151.2093, "Australia", "", "tmp"));
        placesDataDefault.add(new Place("London", 51.507351, -0.127758, "England", "", "tmp"));
        placesDataDefault.add(new Place("Tel Aviv", 32.078829, 34.781175, "Israel", "", "tmp"));




        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.edit().putInt(FIRST_TIME_FLAG, 0).apply();;
        preferences.edit().putInt(FIRST_TIME_FLAG_B, 0).apply();;


        //Overrides the ArrayList data from the last session and creates A default
        Gson gson = new Gson();
        String jsStringInsert = gson.toJson(placesDataDefault);

        if (!TextUtils.isEmpty(jsStringInsert)) {
            editor = preferences.edit();
            editor.putString(JSSTRING_DEFAULT, jsStringInsert);//the default string
            editor.putString(JSSTRING, jsStringInsert);//the regularly used  string is initialized
            editor.apply();
        }

        Log.d(PlacesConstants.MY_NAME , "Inserted the Default search items\n\n" + jsStringInsert + "::");

    }


    public static Context getContext() {
        return context;

    }


}
