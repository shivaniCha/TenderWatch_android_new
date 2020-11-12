package com.tenderWatch.SharedPreference;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tenderWatch.Models.CreateUser;
import com.tenderWatch.Models.LoginUser;
import com.tenderWatch.Models.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lcom48 on 24/11/17.
 */

public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences clientPref;
    SharedPreferences contractorPref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "TenderwatchPref";
    private final String PREF_CONTRACTOR="TenderWatchContractorPref";
    private final String PREF_CLIENT="TenderWatchClientPref";


    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        /*contractorPref = PreferenceManager.getDefaultSharedPreferences(context);
        clientPref = PreferenceManager.getDefaultSharedPreferences(context);*/
        contractorPref=_context.getSharedPreferences(PREF_CONTRACTOR,PRIVATE_MODE);
        clientPref=_context.getSharedPreferences(PREF_CLIENT,PRIVATE_MODE);
        editor = pref.edit();
    }

    public void clearData() {
        editor.clear();
        editor.commit();
    }

    public void setPreferences(Context context, String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void removePreferences(Context context, String key) {
        editor.remove(key);
        editor.commit();
    }

    public void setPreferencesObject(Context context, User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("user", json);
        editor.commit();
    }


 

    public User getPreferencesObject(Context context) {
        Gson gson = new Gson();
        String json = pref.getString("user", "");
        return gson.fromJson(json, User.class);
    }



    public void setPaymentSelections(Context context, HashMap<String, ArrayList<String>> subscribe) {
        Gson gson = new Gson();
        String json = gson.toJson(subscribe);
        editor.putString("subscribe", json);
        editor.commit();
    }

    public HashMap<String, ArrayList<String>> getPaymentSelections(Context context) {
        HashMap<String, ArrayList<String>> subscribe;
        Gson gson = new Gson();
        String json = pref.getString("subscribe", "");

        Type type = new TypeToken<HashMap<String, ArrayList<String>>>() {
        }.getType();
        subscribe = gson.fromJson(json, type);

        return subscribe;
    }

    public void setPayment(Context context, int amount) {
        editor.putInt("amount", amount);
        editor.commit();
    }


    public int getPayment(Context context) {
        int amount = pref.getInt("amount", 0);
        return amount;
    }

    public void setSubscribeType(Context context, int type) {
        editor.putInt("SubscribeType", type);
        editor.commit();
    }


    public int getSubscribeType(Context context) {
        int amount = pref.getInt("SubscribeType", 0);
        return amount;
    }

    /**
     * This method is used to get shared object
     *
     * @param context Application context
     * @param key     shared object key
     * @return return value, for default "" asign.
     */
    public String getPreferences(Context context, String key) {
        String json = pref.getString(key, "");
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return json;
    }

    public void setClient(Context context, LoginUser createUser) {
        clientPref.edit().putString("client", new Gson().toJson(createUser)).commit();
    }

    public LoginUser getClient(Context context) {
        return new Gson().fromJson(clientPref.getString("client", ""), LoginUser.class);
    }

    public void removeClient(){
        clientPref.edit().clear().commit();
    }

    public void setContractor(Context context, LoginUser createUser) {
        contractorPref.edit().putString("contractor", new Gson().toJson(createUser)).commit();
    }

    public LoginUser getContractor(Context context) {
        return new Gson().fromJson(contractorPref.getString("contractor", ""), LoginUser.class);
    }

    public void removeContractor(){
        contractorPref.edit().clear().commit();
    }

    public void ShowDialog(Context context, String Msg) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    context);
            builder.setTitle("Tender Watch");
            builder.setMessage(Msg);

            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.dismiss();
                        }
                    });

            if (!((Activity) context).isFinishing())
                builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
