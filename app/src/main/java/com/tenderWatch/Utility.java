package com.tenderWatch;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class Utility {


    public static OkHttpClient getOkHttpClient() {

        return new OkHttpClient().newBuilder()
                .connectTimeout(160, TimeUnit.SECONDS)
                .readTimeout(160, TimeUnit.SECONDS)
                .writeTimeout(160, TimeUnit.SECONDS)
                .build();
    }

    static String removeEmptyLine(String text){
        String newText="";
        newText=text.replaceAll("(?m)^[ \t]*\r?\n", "");
        return newText;
    }
}
