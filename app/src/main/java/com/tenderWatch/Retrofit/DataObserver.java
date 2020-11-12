package com.tenderWatch.Retrofit;

import com.androidnetworking.error.ANError;

import org.json.JSONArray;
import org.json.JSONObject;

public interface DataObserver {

    void objectResponse(JSONObject object);

    void onError(ANError e, String msg);

    void arrayResponse(JSONArray jsonArray);
}
