package com.syp;

import java.util.HashMap;

public interface IOnLoadLocationListener {
    void onLoadLocationSuccess(HashMap<String, MyLatLng> latLngs);
    void onLoadLocationFailure(String message);
}
