package com.project.itai.FindAPlaceVer2.controllers;

import com.project.itai.FindAPlaceVer2.fragments.IUserActionsOnMap;
import com.google.android.gms.maps.model.LatLng;

class TabletController implements IUserActionsOnMap {

    private IUserActionsOnMap mapsFragment;

    public TabletController(IUserActionsOnMap mapsFragment){
        this.mapsFragment = mapsFragment;
    }

    public void onFocusOnLocation(LatLng newLocation, String name){
        mapsFragment.onFocusOnLocation(newLocation,name);
    }

    @Override
    public void onRequestFreeMap(LatLng thisPosition) {
        mapsFragment.onRequestFreeMap(thisPosition);
    }
}
