package com.tenderWatch.Retrofit;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.tenderWatch.Retrofit.ApiUtils.BASE_URL;

//https://github.com/amitshekhariitbhu/Fast-Android-Networking check it to learn network call.
public class NetworkingLibrary {
    private DataObserver dataObserver;

    public void callMethodUsingPostAndResponseObject(String endPath, JSONObject jsonObject) {

        AndroidNetworking.post(BASE_URL + endPath)
                .addJSONObjectBody(jsonObject) // posting java object
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .setOkHttpClient(getOkHttpClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        if (dataObserver != null) {
                            dataObserver.objectResponse(response);
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        if (dataObserver != null) {
                            dataObserver.onError(error, error.getMessage());
                        }
                    }
                });

    }

    public void callMethodUsingPostAndResponseArray(String endPath, String headerKey, String headerValue) {

        AndroidNetworking.post(BASE_URL + endPath)
                .addHeaders(headerKey, headerValue) // posting java object
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .setOkHttpClient(getOkHttpClient())
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        if (dataObserver != null) {
                            dataObserver.arrayResponse(response);
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error

                        if (dataObserver != null) {
                            dataObserver.onError(error, error.getMessage());
                        }
                    }
                });

    }


    public void callMethodUsingGetAndResponseObject(String endPath, String headerParams) {

        AndroidNetworking.get(BASE_URL + endPath)
                .addHeaders(headerParams) // posting java object
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .setOkHttpClient(getOkHttpClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        if (dataObserver != null) {
                            dataObserver.objectResponse(response);
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error

                        if (dataObserver != null) {
                            dataObserver.onError(error, error.getMessage());
                        }
                    }
                });

    }

    public void callMethodUsingPostAndResponseArray(String endPath, JSONObject jsonObject) {

        AndroidNetworking.post(BASE_URL + endPath)
                .addJSONObjectBody(jsonObject) // posting java object
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .setOkHttpClient(getOkHttpClient())
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        if (dataObserver != null) {

                            dataObserver.arrayResponse(response);
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error

                        if (dataObserver != null) {
                            dataObserver.onError(error, error.getMessage());
                        }
                    }
                });

    }


    public void callMethodUsingGetAndResponseArray(String endPath, String headerParams) {

        AndroidNetworking.get(BASE_URL + endPath)
                .addHeaders(headerParams) // posting java object
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .setOkHttpClient(getOkHttpClient())
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        if (dataObserver != null) {
                            dataObserver.arrayResponse(response);
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error

                        if (dataObserver != null) {
                            dataObserver.onError(error, error.getMessage());
                        }
                    }
                });

    }

    private OkHttpClient getOkHttpClient() {

        return new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();
    }

    public void getObserve(DataObserver observe) {
        this.dataObserver = observe;
    }

    public DataObserver getData() {
        return dataObserver;
    }
}