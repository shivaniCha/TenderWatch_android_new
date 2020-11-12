package com.tenderWatch.Drawer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tenderWatch.ClientDrawer.ClientDrawer;
import com.tenderWatch.Models.Success;
import com.tenderWatch.Models.User;
import com.tenderWatch.R;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;
import com.tenderWatch.Validation.Validation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lcom48 on 14/12/17.
 */

public class ChangePassword extends Fragment implements View.OnClickListener {
    EditText txt_oldPassword, txt_newPassword, txt_confirmPassword;
    Button btn_change;
    private static final String TAG = com.tenderWatch.ClientDrawer.ChangePassword.class.getSimpleName();
    SessionManager sessionManager;
    User user;
    Api mAPIService;
    Intent intent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_changepassword, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getActivity());
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Change Password");
        InitView(view);
        InitListener();
    }

    private void InitListener() {
        btn_change.setOnClickListener(this);
    }

    private void InitView(View v) {
        mAPIService = ApiUtils.getAPIService();

        txt_oldPassword = v.findViewById(R.id.txt_old_password);
        txt_newPassword = v.findViewById(R.id.txt_new_password);
        txt_confirmPassword = v.findViewById(R.id.txt_confirm_password);
        btn_change = v.findViewById(R.id.btn_changePassword);

        txt_newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    Validation.isPassword(txt_newPassword, true);
            }
        });

        txt_confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    Validation.isPassword(txt_confirmPassword, true);
            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_changePassword:
                if (checkValidation()) {
                    changePassword();
                }
        }
    }

    private void changePassword() {
        String password = txt_newPassword.getText().toString();
        String confirmPassword = txt_confirmPassword.getText().toString();
        if (!password.equals(confirmPassword)) {
            sessionManager.ShowDialog(getActivity(), "Confirm Password does not match");
        } else {
            ApiCall();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((MainDrawer)getActivity()).setOptionMenu(false,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ApiCall() {
        user = sessionManager.getPreferencesObject(getActivity());
        String Id = user.getId();
        String token = "Bearer " + sessionManager.getPreferences(getActivity(), "token");
        String oldPassword = txt_oldPassword.getText().toString();
        String newPassword = txt_newPassword.getText().toString();
        Call<Success> resultCall = mAPIService.ChangePassword(token, Id, oldPassword, newPassword);
        resultCall.enqueue(new Callback<Success>() {
            @Override
            public void onResponse(Call<Success> call, Response<Success> response) {
                Log.i(TAG, "response register-->");
                if (response.isSuccessful()) {
                    sessionManager.ShowDialog(getActivity(), response.body().getMessage());
                    if (response.body().getMessage().equalsIgnoreCase("Old password is wrong!!!")) {
                        txt_oldPassword.setText("");
                        txt_newPassword.setText("");
                        txt_confirmPassword.setText("");
                    } else {
                        intent = new Intent(getActivity(), MainDrawer.class);
                        startActivity(intent);
                    }
                } else {
                    sessionManager.ShowDialog(getActivity(), response.errorBody().source().toString().split("\"")[3]);
                }
            }

            @Override
            public void onFailure(Call<Success> call, Throwable t) {
                Log.i(TAG, "error register-->");
                sessionManager.ShowDialog(getActivity(), "Server is down. Come back later!!");
            }
        });
    }

    private boolean checkValidation() {
        boolean ret = true;

        if (!Validation.isPassword(txt_newPassword, true)) ret = false;
        if (!Validation.isPassword(txt_confirmPassword, true)) ret = false;

        return ret;
    }
}