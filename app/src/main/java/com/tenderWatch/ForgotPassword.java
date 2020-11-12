package com.tenderWatch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.tenderWatch.Models.LoginPost;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;
import com.tenderWatch.Validation.Validation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {
    private Button btnSubmit;
    private EditText txtEmail;
    private static final String TAG = Login.class.getSimpleName();
    private Api mAPIService;
    private LinearLayout back;
    Intent intent;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        sessionManager = new SessionManager(ForgotPassword.this);
        InitView();
        InitListener();
    }

    private void InitListener() {
        btnSubmit.setOnClickListener(this);
        back.setOnClickListener(this);
        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Validation.isEmailAddress(txtEmail, true);
            }
        });
    }

    private void InitView() {
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        txtEmail = (EditText) findViewById(R.id.txt_forgotemail);
        mAPIService = ApiUtils.getAPIService();
        back = (LinearLayout) findViewById(R.id.forgot_toolbar);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_submit:
                if (checkValidation()) {
                    forgotPassword();
                } else
                    break;

            case R.id.forgot_toolbar:
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(ForgotPassword.this, Login.class);
                        startActivity(intent);
                    }
                });

        }
    }

    private boolean checkValidation() {
        boolean ret = true;

        if (!Validation.isEmailAddress(txtEmail, true)) ret = false;

        return ret;
    }

    private void forgotPassword() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        if (!this.isFinishing())
            progressDialog.show();

        String email = txtEmail.getText().toString();
        String role = "contractor";

        mAPIService.forgotPassword(email, role).enqueue(new Callback<LoginPost>() {
            @Override
            public void onResponse(Call<LoginPost> call, Response<LoginPost> response) {
                if (!ForgotPassword.this.isFinishing())
                    progressDialog.dismiss();
                if (response.isSuccessful()) {
                    sessionManager.ShowDialog(ForgotPassword.this, "Your password has been sent to your registered Email ID");
                } else {
                    sessionManager.ShowDialog(ForgotPassword.this, response.errorBody().source().toString().split("\"")[3]);
                }
            }

            @Override
            public void onFailure(Call<LoginPost> call, Throwable t) {
                if (!ForgotPassword.this.isFinishing())
                    progressDialog.dismiss();
                sessionManager.ShowDialog(ForgotPassword.this, "Server is down. Come back later!!");
                Log.e(TAG, "Unable to submit post to API.");
            }
        });
    }
}
