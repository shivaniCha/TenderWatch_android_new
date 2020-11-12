package com.tenderWatch.Retrofit;

//import com.readystatesoftware.chuck.ChuckInterceptor;

import okhttp3.OkHttpClient;

/**
 * Created by lcom48 on 25/11/17.
 */

public class ApiUtils {

    private ApiUtils() {
    }
    public static final String BASE_URL = "http://tenderserverapi-env.pvc4hdqjc9.ap-south-1.elasticbeanstalk.com/api/"; // live server
//    public static final String BASE_URL = "http://192.168.0.101:8000/api/"; // Local

    public static Api getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(Api.class);
    }


}

