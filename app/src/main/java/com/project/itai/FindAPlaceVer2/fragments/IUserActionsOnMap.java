package com.project.itai.FindAPlaceVer2.fragments;

import com.google.android.gms.maps.model.LatLng;

public interface IUserActionsOnMap {
     void onFocusOnLocation(LatLng location,String name);
     void onRequestFreeMap(LatLng thisPosition);
}
