package com.project.itai.FindAPlaceVer2.fragments;


// The fragment delegates the click event, to the activity
// Because in this specific excercise, we've decided that the button's behavior context related
// context related = the activity decides (the behavior can change in different activities)


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.itai.FindAPlaceVer2.R;
import com.project.itai.FindAPlaceVer2.adapters.SearchAdapter;
import com.project.itai.FindAPlaceVer2.beans.Place;
import com.project.itai.FindAPlaceVer2.constants.PlacesConstants;
import com.project.itai.FindAPlaceVer2.dao.PlacesDao;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.project.itai.FindAPlaceVer2.utils.MyApp.getContext;


public class Fragment1 extends Fragment {

    private IUserActionsOnMap parentActivity;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ArrayList<Place> placesData = new ArrayList<>();
    private PlacesDao placesDao;

    private RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    protected RadioGroup radioGroup;
    protected RadioButton radioButton;

    //  keys  needed preferences values
    private final String FAVTABLE = "Shared_Text";
    private final String JSSTRING = "json_string:";
    private final String USER_LOC_LATITUDE = "user_lat";
    private final String USER_LOC_LONGTITUDE = "user_long";
    private final String UNIT = "isKm";
    private boolean isLongClick = false; //assures a long click won't activate a short click


    //User Location
    //locListener is an interface that derives locations service via  locManager
    // who derives from the System Loc Services
    private LocationManager locManager;
    private static Location lastLocation = null;
    private LocationListener locListener;
    private String provider;

    // zero means it checks all the time
    final static int AMOUNT_OF_MS_BETWEEN_LOC_UPDATE = 0;
    private final String PERMISSION_STRING = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private Snackbar userLocationSnackbar;
    private Snackbar distanceFailBar;
    private Snackbar distanceBar;
    private Snackbar userLocationSnackbarError;
    private boolean toShowDialog = true;
    private String ALERT_DIALOG_START;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locListener = new LocationListener() {

            //the method  is dynamic, allowing the user to keep track
            //of his whereabouts all the time by using lastLocation, a class var
            @Override
            // location parameter presents the updated location of the device
            public void onLocationChanged(Location location) {
                if (location != null) {
                    lastLocation = location;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };


        //Location Services L Manager Instance
        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
// Optional Additions to the service Criteria Object

        /*Crucial for the  function success/work -- check if any permission was not granted*/
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {


            //
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getContext(), "Permissions were not Granted \n Make sure the problem is remefied", Toast.LENGTH_LONG).show();
            return;
        }
        //following location provider -- set to update continuously
        //Alternate Version:  locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, AMOUNT_OF_MS_BETWEEN_LOC_UPDATE, 0, new locListener());

        //prepare location provider
        //provider = locManager.getBestProvider(new Criteria(), true);
        provider = locManager.getBestProvider(new Criteria(), true);
        if (provider != null) {
            //request location updates
            lastLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locListener);
        } else {
            Toast.makeText(getContext(), "Location Provider Has Failed, Exiting", Toast.LENGTH_LONG).show();
            System.exit(0);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View fragmentView = inflater.inflate(R.layout.fragment_fragment1, container, false);
        Log.e(PlacesConstants.MY_NAME, "is container null:" + (container == null));
        mRecyclerView = fragmentView.findViewById(R.id.search_recycler_view);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        //linear layout creates are linear type View
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);





        //defining  the fragment preferences from the default file name
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());


        //retrieval of Saved list - the one saved in the search button
        Gson gson = new Gson();
        String jsStringRestore = preferences.getString(JSSTRING, null);
        Type type = new TypeToken<ArrayList<Place>>() {
        }.getType();
        if (!TextUtils.isEmpty(jsStringRestore))
            placesData = gson.fromJson(jsStringRestore, type);

        if (placesData.size() > 0) {
            mAdapter = new SearchAdapter(placesData, new OnLocationListener(), new OnLocationLongListener());
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

        final View.OnClickListener radioListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placesData.clear();
                switch (v.getId()) {

                    case R.id.city_but:

                        placesData.add(new Place("Nassau", 25.05, -77.4833, "New Providence", "Bahamas", "tmp"));
                        placesData.add(new Place("Brussels", 50.833, 4.33, "Belgium", "", "tmp"));
                        placesData.add(new Place("Vienna", 48.2, 16.337, "Austria", "", "tmp"));
                        placesData.add(new Place("Sydney", -1 * 33.8688, 151.2093, "Australia", "", "tmp"));
                        placesData.add(new Place("London", 51.507351, -0.127758, "England", "", "tmp"));
                        placesData.add(new Place("Tel Aviv", 32.078829, 34.781175, "Israel", "", "tmp"));
                        break;

                    case R.id.hotel_but:

                        placesData.add(new Place("Dan Tlv Hotel", 32.064715, 34.763190, "Yarkon 22", "Tel Aviv", "http:"));
                        placesData.add(new Place("Hilton Tlv Hotel", 32.089191,
                                34.770317, "Yarkon 10", "Tel Aviv", "http:"));
                        placesData.add(new Place("Dan Eilat Hotel", 29.55805, 34.94821, "N.Coast p.o 176", "Eilat", "http:"));
                        placesData.add(new Place("Leonardo Tiberias\n\tHotel", 32.79221, 35.53124, "Habanim St.1", "Tiberias", "http:"));
                        break;

                    case R.id.resto_but:

                        placesData.add(new Place("Giraffe", 32.076992, 34.781158, "Ibn Gabirol St.49", " Tel Aviv", "tmp"));
                        placesData.add(new Place("River TLV", 32.081441, 34.789787, "Weizmann St.14", "Tel Aviv", "tmp"));
                        placesData.add(new Place("La Cuccina", 29.548784, 34.9638, "Royal Beach Boardwalk", "Eilat", "tmp"));
                        placesData.add(new Place("Avenue Beach Bar", 29.549800, 34.954800, "Pa'amei HaShalom 10", "Eilat", "tmp"));
                        placesData.add(new Place("HaBokrim Stake\n\tHouse", 32.790633, 34.964091, "Kdoshey Yassi St 1", "Haifa", "tmp"));
                        placesData.add(new Place("Ha Chavit", 32.795067, 34.956454, "David Elazar Rd.", "Haifa", "tmp"));

                        break;

                    default:
                        //todo check if it's ok
                        placesData.add(new Place("Giraffe", 32.076992, 34.781158, "Ibn Gabirol St.49", " Tel Aviv", "tmp"));
                        placesData.add(new Place("River TLV", 32.081441, 34.789787, "Weizmann St.14", "Tel Aviv", "tmp"));
                        placesData.add(new Place("La Cuccina", 29.548784, 34.9638, "Royal Beach Boardwalk", "Eilat", "tmp"));
                        placesData.add(new Place("Avenue Beach Bar", 29.549800, 34.954800, "Pa'amei HaShalom 10", "Eilat", "tmp"));
                        placesData.add(new Place("HaBokrim Stake\n\t House", 32.790633, 34.964091, "Kdoshey Yassi St 1", "Haifa", "tmp"));
                        placesData.add(new Place("Ha Chavit", 32.795067, 34.956454, "David Elazar Rd.", "Haifa", "tmp"));

                }
            }
        };


        //At this stage placesData is updated according to the radio buttons
        //defining the views, and activating the anonymous class via radioListener

        //Search Button
        Button searchButton = fragmentView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                radioGroup = fragmentView.findViewById(R.id.radiogroup);
                int radioId = radioGroup.getCheckedRadioButtonId();
                radioButton = fragmentView.findViewById(radioId);
                radioListener.onClick(radioButton);


                if (placesData.size() > 0) {

                    mAdapter = new SearchAdapter(placesData, new OnLocationListener(), new OnLocationLongListener());
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    //insertion
                    //todo check if need to declare again
                    Gson gson = new Gson();
                    String jsStringInsert = gson.toJson(placesData);
                    // resetPreferences(); using the editor to insert
                    editor = preferences.edit();
                    editor.putString(JSSTRING, jsStringInsert);
                    Log.d(PlacesConstants.MY_NAME + "JSSTRING PREF   ", "test pref" + jsStringInsert + "::");
                    editor.apply();

                } else {
                    //todo check
                    Toast.makeText(getContext(), "An Error Occurred", Toast.LENGTH_LONG)
                            .show();
                    System.exit(0);

                }

            }
        });

        // Returns the location of the user
        Button userLocation = fragmentView.findViewById(R.id.user_loc);
        userLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = getlastLocation();
                LatLng yourPosition;
//todo problems

                if (location != null) {
                    double lat = location.getLatitude(); //location is turned into LatLng object
                    double lng = location.getLongitude();

                    yourPosition = new LatLng(lat, lng);
                    parentActivity.onFocusOnLocation(yourPosition, "User Location");


                    //saving loci in SP: meant for future features
                    editor = preferences.edit();
                    editor.putString(USER_LOC_LONGTITUDE, String.valueOf(location.getLongitude()));
                    editor.putString(USER_LOC_LATITUDE, String.valueOf(location.getLatitude()));
                    editor.apply();

                    //informing the user about his loci. The snackbar will not disappear
                    // until the dismiss button is pressed and so are most of the snackbars
                    //these two round lat & lng to provide a better look
                    double latitude = Double.parseDouble(new DecimalFormat("##.####").format(lat));
                    double longitude = Double.parseDouble(new DecimalFormat("##.####").format(lng));
                    userLocationSnackbar = Snackbar.make(getView(), "your latitude is: " + latitude
                                    + "\nyour longitude is: " + longitude,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    userLocationSnackbar.dismiss();
                                }
                            });
                    userLocationSnackbar.show();
                } else {
                    userLocationSnackbarError = Snackbar.make(getView(), "Failed to find your Location",
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    userLocationSnackbarError.dismiss();
                                }
                            });
                    userLocationSnackbarError.show();
                }
            }

        });

        //clears out the Model , allows more flexibility
        Button newSearch = fragmentView.findViewById(R.id.new_search);
        newSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placesData.clear();
                mAdapter = new SearchAdapter(placesData, new OnLocationListener(), new OnLocationLongListener());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Contents Cleared", Toast.LENGTH_LONG).show();
            }
        });

        Button freeMap = fragmentView.findViewById(R.id.free_map);
        freeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LatLng thisLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                parentActivity.onRequestFreeMap(thisLocation);
            }
        });

        return fragmentView;
    }



    //The Listener class RecyclerAdapter
    class OnLocationListener implements SearchAdapter.Listener {
        Location targetLocation = new Location(provider);
        Location userLocation;// the requested on focus location to be measure with User Location
        protected double distanceInMeters = 0;

        @Override
        public void onLocation(Place place) {
            //assures a long click won't activate a short click
            if (!(isLongClick)) {

                // We extract the item which the user clicked on. And notify of his choice
                Toast.makeText(getActivity().getApplicationContext(), place.getName() +
                        "\n lat: " + place.getLat() + " lng: " + place.getLng(), Toast.LENGTH_LONG).show();
                LatLng newLocation = new LatLng(place.getLat(), place.getLng());
                //define  user location
                userLocation = getlastLocation();
                //distance calculation
                if (userLocation != null && place != null) {

                    //transfer loci from place obj to location object
                    targetLocation.setLatitude(place.getLat());
                    targetLocation.setLongitude(place.getLng());
                    //calculate distance part of onLocationChanged method:LocationListener interface
                    distanceInMeters = targetLocation.distanceTo(userLocation);


                    // via method textBasedOnUnit() arrays both miles and km are accommodated

                    distanceBar = Snackbar.make(getView(), "Distance to " + place.getName() + ": " + textBasedOnUnit(userLocation, targetLocation),
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    distanceBar.dismiss();
                                }
                            });
                    distanceBar.show();
                    parentActivity.onFocusOnLocation(newLocation, place.getName());
                    //failure to find distance
                } else {
                    distanceFailBar = Snackbar.make(getView(), "Failed to find Distance",
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    distanceFailBar.dismiss();
                                }
                            });
                    distanceFailBar.show();
                    parentActivity.onFocusOnLocation(newLocation, place.getName());
                }
            }
            isLongClick = false; // short click is now allowed
        }
    }

    // implements item long click from the inside of the SearchAdapter
    class OnLocationLongListener implements SearchAdapter.LongClickListener {
        @Override
        public boolean onLongLocation(Place place) {
            isLongClick = true; //assures a long click won't activate a short click
            if (placesDao == null)
                placesDao = new PlacesDao(getContext());

            Log.e("MyApp", "Place Retreived " + place.getName());
            Log.d("MyApp", "Place " + place.toString2());

            //assures similar places will not enter the placesdao
            if (!placesDao.isExistentInDB(place)) {
                Place newPlace = new Place(place.getName(), place.getLat(), place.getLng(), place.getAddress(), place.getCity(), place.getUrlImage());
                placesDao.addPlace(newPlace);

                //Saving favorites to json format
                ArrayList<Place> tmpList = new ArrayList<>();
                tmpList.add(newPlace);
                Gson gson = new Gson();
                String FavWrite = gson.toJson(tmpList);
                //preferences
                editor = preferences.edit();
                editor.putString(FAVTABLE, FavWrite);
                editor.apply();

                //announcing addition to favorite
                Toast.makeText(getActivity().getApplicationContext(), "Adding to Favorites: " + place.getName()
                        , Toast.LENGTH_LONG).show();
                //Log test
                Log.d(PlacesConstants.MY_NAME + "JSSTRING PREF   ", "test pref" + FavWrite + "::");


            } else
                Toast.makeText(getActivity().getApplicationContext(), "Already Exists In Favorites", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public Location getlastLocation() {

        return lastLocation;
    }


    @Override
    public void onResume() {
        super.onResume();
        // toShowDialog - is the parameter decided whether to show or not
        if (preferences.getInt(ALERT_DIALOG_START, 0) == 0){

            // Alert Dialog - to inform about how the mock should work
            //todo check if to remove
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Find A Place:");
            alert.setMessage("Search by Categories:\n 1.CITIES\n 2.RESTAURANTS\n 3.HOTELS\n" +
                    "And then press SEARCH PLACES\n\n" +
                    "Pressing for a prolonged time on the list will add the location\n" +
                    "to the Favorites List and Data Base, where it can be accessed again\n" +
                    "You can access the Favorites list by sliding\n the search list to the left\n" +
                    "Favorites will not show unless\n you press Load Favorites \n" +
                    "A long press will erase the specific favorites\n " +
                    "Pressing Delete Favorites will delete the entire list \n" +
                    "After turning the cell phone, press Load Favorites again");


            alert.setPositiveButton("dismiss", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();


                }

            });
            alert.show();
            preferences.edit().putInt(ALERT_DIALOG_START, 1).apply();

        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("places", placesData);
    }

    @Override
    // The context is in fact the activity which hosts the fragment
    // This function is being called after the activity is being created
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof IUserActionsOnMap) {
            // If the activity which holds the current fragment, obeys to the rules in the
            // "contract", defined in the interface ("IUserActions"), then we save a
            // reference to the external activity, in order to call it, each time the button
            // had been pressed
            this.parentActivity = (IUserActionsOnMap) context;//retrieves context from MainActivity
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IUserActionsOnMap");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parentActivity = null;
    }


    // Stops the Location listener
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locManager != null) {
            if (ContextCompat.checkSelfPermission(getContext(), PERMISSION_STRING) == PackageManager.PERMISSION_GRANTED)
                locManager.removeUpdates(locListener);
            locManager = null;
        }
        // preferences.edit().putInt(ALERT_DIALOG_START, 0).apply();
   }


    public interface IUserActionsOnMap {
        void onFocusOnLocation(LatLng newLocation, String name);

        void onRequestFreeMap(LatLng thisLocation);

    }


    // Calculates the the distance based on the preferred units and returns it as text
    public String textBasedOnUnit(Location userLoci, Location targetLoci) {

        String UnitName[] = {"km", "mile"};
        String distanceText;
        double distanceInMeters = userLoci.distanceTo(targetLoci);
        double distanceInMetersRounded = Double.parseDouble(new DecimalFormat("#.0")
                .format(distanceInMeters));

        double UnitFactor[] =
                {(distanceInMetersRounded / 1000),
                        (distanceInMetersRounded / (1000 * 1.6))};

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        boolean kmFlag = preferences.getBoolean(UNIT, false);

        if (kmFlag)
            distanceText = " " + UnitFactor[0] + " " + UnitName[0];
        else
            distanceText = " " + UnitFactor[1] + " " + UnitName[1];
        return distanceText;
    }


}



