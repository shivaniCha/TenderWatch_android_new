package com.tenderWatch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.tenderWatch.ClientDrawer.ClientDrawer;
import com.tenderWatch.Drawer.MainDrawer;
import com.tenderWatch.Models.CreateUser;
import com.tenderWatch.Models.Register;
import com.tenderWatch.Models.User;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class Agreement extends AppCompatActivity implements View.OnClickListener {
    ImageView box, boxChecked;
    Button signUp;
    CreateUser user = new CreateUser();
    private static final String TAG = Agreement.class.getSimpleName();
    private Api mAPIService;
    MultipartBody.Part deviceId2, selections1, email1, password1, country1, deviceTyp1, subscribe1, contactNo1, occupation1, aboutMe1, role1, deviceId1, image1,subRole1,firstname1,lastname1;
    SessionManager sessionManager;
    Intent intent;
    LinearLayout back, webLayout;
    private ProgressDialog mProgressDialog;
    private HashMap<String, ArrayList<String>> selection;
    private int amount = 0;
    private int subscriptionType = 1;
    private boolean isTrial = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        sessionManager =  new SessionManager(Agreement.this);
        getDataFromIntent();
        InitView();
        InitListener();
        Log.i(TAG, String.valueOf(user.getProfilePhoto()));
    }

    public void getDataFromIntent() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().getSerializable("selections") != null) {
                selection = (HashMap<String, ArrayList<String>>) getIntent().getExtras().getSerializable("selections");
            }
            amount = getIntent().getExtras().getInt("amount", 0);
            subscriptionType = getIntent().getExtras().getInt("subscriptionType", 1);
            isTrial = getIntent().getExtras().getBoolean("isTrial", true);
        }
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        //Checking for fragment count on backstack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }
    }

    private void InitView() {
        mAPIService = ApiUtils.getAPIService();

        WebView mWebView = (WebView) findViewById(R.id.agreement_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl("file:///android_res/raw/agreement.html");
        box = (ImageView) findViewById(R.id.box);
        boxChecked = (ImageView) findViewById(R.id.box_checked);
        back = (LinearLayout) findViewById(R.id.agreement_back);
        webLayout = (LinearLayout) findViewById(R.id.weblayout);
// Gets the layout params that will allow you to resize the layout
        ViewGroup.LayoutParams params = webLayout.getLayoutParams();
// Changes the height and width to the specified *pixels*
        params.height = Agreement.this.getResources().getDimensionPixelSize(R.dimen.value_340);
        webLayout.setLayoutParams(params);
        signUp = (Button) findViewById(R.id.post_signup);
    }

    private void InitListener() {
        box.setOnClickListener(this);
        boxChecked.setOnClickListener(this);
        signUp.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.box:
                box.setVisibility(View.GONE);
                boxChecked.setVisibility(View.VISIBLE);
                signUp.setAlpha((float) 1);
                break;
            case R.id.box_checked:
                boxChecked.setVisibility(View.GONE);
                box.setVisibility(View.VISIBLE);
                signUp.setAlpha((float) 0.7);
                break;
            case R.id.post_signup:
                SignUpPost();
                break;
            case R.id.agreement_back:
                if (sessionManager.getPreferences(Agreement.this, "role").equals("client")) {
                    intent = new Intent(Agreement.this, SignUp.class);
                    startActivityForResult(intent, 1);
                } else {
//                    intent = new Intent(Agreement.this, Category.class);
                    finish();
                }
//                startActivityForResult(intent, 1);
                break;
        }
    }

    private void uploadImage() {

        showProgressDialog();

        String email = user.getEmail();
        String password = user.getPassword();
        String country = user.getCountry();
        String contact = user.getContactNo();
        String occupation = user.getOccupation();
        String aboutMe = user.getAboutMe();
        String role = user.getRole();
        String deviceId = user.getDeviceId();
        File file1 = user.getProfilePhoto();
        String regId = FirebaseInstanceId.getInstance().getToken();
        String firstname=user.getFirstName();
        String lastname=user.getLastName();

        RequestBody requestFile;

        if (user.getProfilePhoto() != null) {
            requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
            image1 = MultipartBody.Part.createFormData("image", file1.getName(), requestFile);
        } else {
            image1 = MultipartBody.Part.createFormData("image", "");
        }

        firstname1= MultipartBody.Part.createFormData("firstName", firstname);
        lastname1= MultipartBody.Part.createFormData("lastName", lastname);
        email1 = MultipartBody.Part.createFormData("email", email);
        password1 = MultipartBody.Part.createFormData("password", password);
        country1 = MultipartBody.Part.createFormData("country", country);
        contactNo1 = MultipartBody.Part.createFormData("contactNo", contact);
        occupation1 = MultipartBody.Part.createFormData("occupation", occupation);
        aboutMe1 = MultipartBody.Part.createFormData("aboutMe", aboutMe);
        role1 = MultipartBody.Part.createFormData("role", role);
        deviceId1 = MultipartBody.Part.createFormData("androidDeviceId", regId);
        /*image1 = MultipartBody.Part.createFormData("image", file1.getName(), requestFile);
        if (image1 == null) {
            image1 = MultipartBody.Part.createFormData("image", "");
        }*/
        Call<Register> resultCall = mAPIService.uploadImage(email1, password1, country1, contactNo1, occupation1, aboutMe1, role1, deviceId1, image1,firstname1,lastname1);
        resultCall.enqueue(new Callback<Register>() {
            @Override
            public void onResponse(Call<Register> call, Response<Register> response) {

                dismissProgressDialog();

                if (response.isSuccessful()) {
                    User u1 = response.body().getUser();
                    sessionManager.setPreferencesObject(Agreement.this, u1);
                    sessionManager.setPreferences(Agreement.this, "token", response.body().getToken());
                    intent = new Intent(Agreement.this, ClientDrawer.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    sessionManager.ShowDialog(Agreement.this, "Successful Registration");
                } else {
                    sessionManager.ShowDialog(Agreement.this, response.errorBody().source().toString().split("\"")[3]);
                }
            }

            @Override
            public void onFailure(Call<Register> call, Throwable t) {
                dismissProgressDialog();
                Log.i(TAG, "error register-->");
                sessionManager.ShowDialog(Agreement.this, "Server is down. Come back later!!");
            }
        });
    }

    private void SignUpPost() {
        if (signUp.getAlpha() == 1) {
            if (sessionManager.getPreferences(Agreement.this, "role").equals("contractor")) {
                uploadContractor();
            } else {
                uploadImage();
            }
        }
    }

//    private void uploadContractor() {
//        showProgressDialog();
//
//        String email = user.getEmail();
//        String password = user.getPassword();
//        String country = user.getCountry();
//        String contact = user.getContactNo();
//        String occupation = user.getOccupation();
//        String aboutMe = user.getAboutMe();
//        String role = user.getRole();
//        String deviceId = FirebaseInstanceId.getInstance().getToken();
//        String firstname=user.getFirstName();
//        String lastname=user.getLastName();
//        //String subRole=user.getSubRole();
//        final String selections = String.valueOf(user.getSelections());
//        HashMap<String, ArrayList<String>> subscribe = user.getSubscribe();
//        String[] device = new String[1];
//
//        File file1 = user.getProfilePhoto();
//        RequestBody requestFile;
//        if (user.getProfilePhoto() != null) {
//            requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
//            image1 = MultipartBody.Part.createFormData("image", file1.getName(), requestFile);
//        } else {
//            image1 = MultipartBody.Part.createFormData("image", "");
//        }
//
//        firstname1 = MultipartBody.Part.createFormData("firstName", firstname);
//        lastname1 = MultipartBody.Part.createFormData("lastName", lastname);
//        email1 = MultipartBody.Part.createFormData("email", email);
//        password1 = MultipartBody.Part.createFormData("password", password);
//        country1 = MultipartBody.Part.createFormData("country", country);
//        contactNo1 = MultipartBody.Part.createFormData("contactNo", contact);
//        occupation1 = MultipartBody.Part.createFormData("occupation", occupation);
//        aboutMe1 = MultipartBody.Part.createFormData("aboutMe", aboutMe);
//        role1 = MultipartBody.Part.createFormData("role", role);
//        deviceId1 = MultipartBody.Part.createFormData("androidDeviceId", deviceId);
//        deviceId2 = MultipartBody.Part.createFormData("deviceId", "");
//        subscribe1 = MultipartBody.Part.createFormData("subscribe", selections);
//        selections1 = MultipartBody.Part.createFormData("selections", new Gson().toJson(subscribe));
//        //subRole1= MultipartBody.Part.createFormData("subRole",subRole);
//
//        Log.e(TAG, "subscribe: "+ new Gson().toJson(subscribe));
//        Log.e(TAG, "Firstname: "+ firstname);
//        Log.e(TAG, "lastName: "+ lastname);
//        Log.e(TAG, "email: "+ email);
//        Log.e(TAG, "password: "+ password);
//        Log.e(TAG, "confirmPassword: "+ password);
//        Log.e(TAG, "country: "+ country);
//        Log.e(TAG, "contactNo: "+ contact);
//        Log.e(TAG, "occupation: "+ occupation);
//        Log.e(TAG, "aboutMe: "+ aboutMe);
//        Log.e(TAG, "role: "+ role);
//        Log.e(TAG, "deviceId: "+ deviceId);
//        Log.e(TAG, "subscribe: "+ selections);
//
//        Call<Register> resultCall = mAPIService.uploadContractor(email1, password1, country1, contactNo1, occupation1, aboutMe1, role1, deviceId1, image1, subscribe1, selections1,firstname1,lastname1);
//        resultCall.enqueue(new Callback<Register>() {
//            @Override
//            public void onResponse(Call<Register> call, Response<Register> response) {
//                Log.i(TAG, "response register-->");
//
//                dismissProgressDialog();
//
//                if (response.isSuccessful()) {
//                    Gson gson = new Gson();
//                    String jsonString = gson.toJson(user);
//                    User u1 = response.body().getUser();
//                    sessionManager.setPreferencesObject(Agreement.this, u1);
//                    sessionManager.setPreferences(Agreement.this, "token", response.body().getToken());
//
//                    sessionManager.setPaymentSelections(Agreement.this, selection);
//                    sessionManager.setPayment(Agreement.this, amount);
//
//                    //if (!isTrial) {
//                        intent = new Intent(Agreement.this, CountryList.class);
//                        intent.putExtra("fromSignUP", true);
//                        startActivityForResult(intent, 1);
//                    /*} else {
//                        intent = new Intent(Agreement.this, MainDrawer.class);
//                        intent.putExtra("data", jsonString);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    }*/
//                    startActivity(intent);
//                } else {
//                    sessionManager.ShowDialog(Agreement.this, response.errorBody().source().toString().split("\"")[3]);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Register> call, Throwable t) {
//                dismissProgressDialog();
//                Log.i(TAG, "error register-->");
//                sessionManager.ShowDialog(Agreement.this, "Server is down. Come back later!!");
//            }
//        });
//    }



    private void uploadContractor() {
        showProgressDialog();

        final String email = user.getEmail();
        final String password = user.getPassword();
        final String country = user.getCountry();
        final String contact = user.getContactNo();
        final String occupation = user.getOccupation();
        final String aboutMe = user.getAboutMe();
        final String role = user.getRole();
        final String deviceId = FirebaseInstanceId.getInstance().getToken();
        final String firstname=user.getFirstName();
        final String lastname=user.getLastName();
        final String googleToken=user.getGoogleToken();
        //String subRole=user.getSubRole();
        final String selections = String.valueOf(user.getSelections());
        final HashMap<String, ArrayList<String>> subscribe = user.getSubscribe();
        String[] device = new String[1];

        final File file1 = user.getProfilePhoto();
        RequestBody requestFile;
        if (user.getProfilePhoto() != null) {
            requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
            image1 = MultipartBody.Part.createFormData("image", file1.getName(), requestFile);
        } else {
            image1 = MultipartBody.Part.createFormData("image", "");
        }

        try {
            mAPIService.isTrialVisible(email, contact).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    boolean isTrialVisible=false;
                    if (response.body().booleanValue()) {
                        isTrialVisible=true;
                    } else {
                        isTrialVisible=false;
                    }

                    intent = new Intent(Agreement.this, CountryList.class);
                    intent.putExtra("fromSignUP", true);
                    intent.putExtra("firstname",firstname);
                    intent.putExtra("lastname",lastname);
                    intent.putExtra("email",email);
                    intent.putExtra("password",password);
                    intent.putExtra("country",country);
                    intent.putExtra("contact",contact);
                    intent.putExtra("occupation",occupation);
                    intent.putExtra("aboutMe",aboutMe);
                    intent.putExtra("role",role);
                    intent.putExtra("deviceId",deviceId);
                    intent.putExtra("subscribe",subscribe);
                    intent.putExtra("googleToken",googleToken);
                    intent.putExtra("isTrialVisible",isTrialVisible);
                    if (user.getProfilePhoto() != null) {
                        intent.putExtra("file", file1.getAbsolutePath());
                    }else{
                        intent.putExtra("file", "");
                    }
                    startActivityForResult(intent, 1);
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


//        startActivity(intent);



//        firstname1 = MultipartBody.Part.createFormData("firstName", firstname);
//        lastname1 = MultipartBody.Part.createFormData("lastName", lastname);
//        email1 = MultipartBody.Part.createFormData("email", email);
//        password1 = MultipartBody.Part.createFormData("password", password);
//        country1 = MultipartBody.Part.createFormData("country", country);
//        contactNo1 = MultipartBody.Part.createFormData("contactNo", contact);
//        occupation1 = MultipartBody.Part.createFormData("occupation", occupation);
//        aboutMe1 = MultipartBody.Part.createFormData("aboutMe", aboutMe);
//        role1 = MultipartBody.Part.createFormData("role", role);
//        deviceId1 = MultipartBody.Part.createFormData("androidDeviceId", deviceId);
//        subscribe1 = MultipartBody.Part.createFormData("subscribe", selections);
//        selections1 = MultipartBody.Part.createFormData("selections", new Gson().toJson(subscribe));
//        //subRole1= MultipartBody.Part.createFormData("subRole",subRole);
//
//        Log.e(TAG, "subscribe: "+ new Gson().toJson(subscribe));
//        Log.e(TAG, "Firstname: "+ firstname);
//        Log.e(TAG, "lastName: "+ lastname);
//        Log.e(TAG, "email: "+ email);
//        Log.e(TAG, "password: "+ password);
//        Log.e(TAG, "confirmPassword: "+ password);
//        Log.e(TAG, "country: "+ country);
//        Log.e(TAG, "contactNo: "+ contact);
//        Log.e(TAG, "occupation: "+ occupation);
//        Log.e(TAG, "aboutMe: "+ aboutMe);
//        Log.e(TAG, "role: "+ role);
//        Log.e(TAG, "deviceId: "+ deviceId);
//        Log.e(TAG, "subscribe: "+ selections);
//
//        Call<Register> resultCall = mAPIService.uploadContractor(email1, password1, country1, contactNo1, occupation1, aboutMe1, role1, deviceId1, image1, subscribe1, selections1,firstname1,lastname1);
//        resultCall.enqueue(new Callback<Register>() {
//            @Override
//            public void onResponse(Call<Register> call, Response<Register> response) {
//                Log.i(TAG, "response register-->");
//
//                dismissProgressDialog();
//
//                if (response.isSuccessful()) {
//                    Gson gson = new Gson();
//                    String jsonString = gson.toJson(user);
//                    User u1 = response.body().getUser();
//                    sessionManager.setPreferencesObject(Agreement.this, u1);
//                    sessionManager.setPreferences(Agreement.this, "token", response.body().getToken());
//
//                    sessionManager.setPaymentSelections(Agreement.this, selection);
//                    sessionManager.setPayment(Agreement.this, amount);
//
//                    //if (!isTrial) {
//                    intent = new Intent(Agreement.this, CountryList.class);
//                    intent.putExtra("fromSignUP", true);
//                    startActivityForResult(intent, 1);
//                    /*} else {
//                        intent = new Intent(Agreement.this, MainDrawer.class);
//                        intent.putExtra("data", jsonString);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    }*/
//                    startActivity(intent);
//                } else {
//                    sessionManager.ShowDialog(Agreement.this, response.errorBody().source().toString().split("\"")[3]);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Register> call, Throwable t) {
//                dismissProgressDialog();
//                Log.i(TAG, "error register-->");
//                sessionManager.ShowDialog(Agreement.this, "Server is down. Come back later!!");
//            }
//        });
    }

    public void showProgressDialog() {
        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(Agreement.this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!Agreement.this.isFinishing() && !mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }
}
