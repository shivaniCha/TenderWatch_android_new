package com.tenderWatch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tenderWatch.Adapters.ContractorTenderListAdapter;
import com.tenderWatch.ClientDrawer.ClientDrawer;
import com.tenderWatch.Drawer.MainDrawer;
import com.tenderWatch.Models.AllContractorTender;
import com.tenderWatch.Models.CreateUser;
import com.tenderWatch.Models.LoginUser;
import com.tenderWatch.Models.Register;
import com.tenderWatch.Models.User;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.Retrofit.DataObserver;
import com.tenderWatch.SharedPreference.SessionManager;
import com.tenderWatch.Validation.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tenderWatch.Retrofit.ApiUtils.BASE_URL;

public class Login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    Intent intent;
    private CallbackManager callbackManager;
    private static final String TAG = Login.class.getSimpleName();
    private EditText txtEmail, txtPassword;
    private LoginButton loginButton;
    private TextView lblCreateAccount, lblForgotPassword;
    private Button fb;
    private static final int RC_SIGN_IN = 007;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private SignInButton btnSignIn;
    private Button btnSignOut, btnRevokeAccess, btngoogle, btnlogin;
    private LinearLayout back;
    private ImageView imgProfilePic;
    private Api mAPIService;
    String newString;
    SessionManager sessionManager;
    ImageView ivShowHidePasswd;
    boolean showPasswd = false;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(Login.this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                newString = null;
            } else {
                newString = extras.getString("Role");
            }
        } else {
            newString = (String) savedInstanceState.getSerializable("Role");
        }
        Init();
        InitListener();

        String role = sessionManager.getPreferences(Login.this, "role");
        if (!TextUtils.isEmpty(role)) {
            LoginUser createUser;
            if (role.equalsIgnoreCase("client")) {
                createUser = sessionManager.getClient(Login.this);
                if (createUser != null && !TextUtils.isEmpty(createUser.getEmail()) && !TextUtils.isEmpty(createUser.getPassword())) {
                    txtEmail.setText(createUser.getEmail());
                    txtPassword.setText(createUser.getPassword());
                }
        } else {
                createUser = sessionManager.getContractor(Login.this);
                if (createUser != null && !TextUtils.isEmpty(createUser.getEmail()) && !TextUtils.isEmpty(createUser.getPassword())) {
                    txtEmail.setText(createUser.getEmail());
                    txtPassword.setText(createUser.getPassword());
                }
            }
        }
    }

    private void Init() {
        fb = (Button) findViewById(R.id.fb);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        btnlogin = (Button) findViewById(R.id.btn_login);
        mAPIService = ApiUtils.getAPIService();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btngoogle = (Button) findViewById(R.id.google);
        txtEmail = (EditText) findViewById(R.id.txt_email);
        txtPassword = (EditText) findViewById(R.id.txt_password);
        lblCreateAccount = (TextView) findViewById(R.id.create_account);
        back = (LinearLayout) findViewById(R.id.login_toolbar);
        lblForgotPassword = (TextView) findViewById(R.id.lbl_forgotPassword);
        ivShowHidePasswd = (ImageView) findViewById(R.id.show_password);
    }

    private void InitListener() {
        ivShowHidePasswd.setOnClickListener(this);
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
        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(TAG, "post submitted to API." + Validation.isValidPassword(txtPassword.getText().toString()));

            }
        });
        btnSignIn.setOnClickListener(this);
        btngoogle.setOnClickListener(this);
        btnlogin.setOnClickListener(this);
        lblCreateAccount.setOnClickListener(this);
        back.setOnClickListener(this);
        lblForgotPassword.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestServerAuthCode(getString(R.string.server_client_id), false)
                .requestEmail()
                .build();

        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();*/

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());
        fb.setOnClickListener(this);

        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, String.valueOf(loginResult));

                dismissProgressDialog();

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.e("LoginActivity", response.toString());
                                try {
                                    // Application code
                                    String email = object.getString("email");

                                    /*if (!TextUtils.isEmpty(email))
                                        txtEmail.setText(email);*/

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link, first_name, last_name, email, picture{url}");
                request.setParameters(parameters);
                request.executeAsync();

                String accessToken = loginResult.getAccessToken().getUserId();
                String deviceId = FirebaseInstanceId.getInstance().getToken();
                String role = sessionManager.getPreferences(Login.this, "role");
                savePostFB(accessToken, role, deviceId);
                if (AccessToken.getCurrentAccessToken() != null) {
                    Log.v("User is login", "YES");

                }
                sessionManager.setPreferences(getApplicationContext(), "Login", "FBYES");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onConnectionFailed:");
                // info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d(TAG, "onConnectionFailed:");
                //  info.setText("Login attempt failed.");
            }
        });

    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!Validation.isEmailAddress(txtEmail, true)) ret = false;
        if (!Validation.isPassword(txtPassword, true)) ret = false;
        return ret;
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            ///showProgressDialog();
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
        }
    }

    public void savePostFB(String idToken, String role, String deviceId) {
        showProgressDialog();

        mAPIService.savePostFB(idToken, role, deviceId).enqueue(new Callback<Register>() {
            @Override
            public void onResponse(Call<Register> call, Response<Register> response) {
                dismissProgressDialog();
                if (response.isSuccessful()) {
                    sessionManager.setPreferencesObject(Login.this, response.body().getUser());
                    sessionManager.setPreferences(Login.this, "token", response.body().getToken());
                    String role = sessionManager.getPreferences(Login.this, "role");
                    if (role.equals("client")) {
                        intent = new Intent(Login.this, ClientDrawer.class);

                    } else {
                        intent = new Intent(Login.this, MainDrawer.class);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);

                } else {
                    if (response.errorBody().source().toString().split("\"").length >= 4)
                        sessionManager.ShowDialog(Login.this, response.errorBody().source().toString().split("\"")[3]);
                }
            }

            @Override
            public void onFailure(Call<Register> call, Throwable t) {
                dismissProgressDialog();
                sessionManager.ShowDialog(Login.this, "Server is down. Come back later!!");
                Log.e(TAG, "Unable to submit post to API.");
            }
        });
    }

    public void sendPostGoogle(String idToken, String role, String deviceId) {
        showProgressDialog();

        mAPIService.savePostGoogle(idToken, role, deviceId).enqueue(new Callback<Register>() {
            @Override
            public void onResponse(Call<Register> call, Response<Register> response) {
                dismissProgressDialog();
                if (response.isSuccessful()) {
                    sessionManager.setPreferencesObject(Login.this, response.body().getUser());
                    String role = sessionManager.getPreferences(Login.this, "role");
                    sessionManager.setPreferences(Login.this, "token", response.body().getToken());
                    if (role.equals("client")) {
                        intent = new Intent(Login.this, ClientDrawer.class);
                    } else {
                        intent = new Intent(Login.this, MainDrawer.class);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                    Log.i(TAG, "post submitted to API." + response.body().toString());
                } else {
                    if (response.errorBody().source().toString().split("\"").length >= 4)
                        sessionManager.ShowDialog(Login.this, response.errorBody().source().toString().split("\"")[3]);
                }
            }

            @Override
            public void onFailure(Call<Register> call, Throwable t) {
                dismissProgressDialog();
                sessionManager.ShowDialog(Login.this, "Server is down. Come back later!!");
            }
        });
    }



    public void sendPost(String email, String password, String role, final String deviceId) {
        showProgressDialog();

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            jsonObject.put("role", role);
            jsonObject.put("androidDeviceId", deviceId);

            AndroidNetworking.post(BASE_URL + "auth/login")
                    .addJSONObjectBody(jsonObject) // posting java object
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .setOkHttpClient(new OkHttpClient().newBuilder()
                            .connectTimeout(120, TimeUnit.SECONDS)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .writeTimeout(120, TimeUnit.SECONDS)
                            .build())
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            try {
                                dismissProgressDialog();
                                Gson gson = new Gson();
                                Register registerResponse = gson.fromJson(response.toString(), Register.class);

                                sessionManager.setPreferences(Login.this, "androidId", deviceId);
                                sessionManager.setPreferencesObject(Login.this, registerResponse.getUser());

                                String role = sessionManager.getPreferences(Login.this, "role");
                                String profile = registerResponse.getUser().getProfilePhoto();
                                String email = registerResponse.getUser().getEmail();
                                String id = registerResponse.getUser().getId();
                                String token = registerResponse.getToken();
                                sessionManager.setPreferences(Login.this, "profile", profile);
                                sessionManager.setPreferences(Login.this, "email", email);
                                sessionManager.setPreferences(Login.this, "id", id);
                                sessionManager.setPreferences(Login.this, "token", token);

                                if (!TextUtils.isEmpty(role)) {
                                    LoginUser createUser = new LoginUser();
                                    createUser.setEmail(txtEmail.getText().toString());
                                    createUser.setPassword(txtPassword.getText().toString());

                                    if (role.equalsIgnoreCase("client")) {
                                        sessionManager.setClient(Login.this, createUser);
                                        intent = new Intent(Login.this, ClientDrawer.class);

                                    } else {
                                        sessionManager.setContractor(Login.this, createUser);
                                        intent = new Intent(Login.this, MainDrawer.class);
                                    }
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                    startActivity(intent);
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            // handle error
                            dismissProgressDialog();
                            try {
                                if(error.getErrorBody()!=null) {
                                    if(!error.getErrorBody().startsWith("<!")) {
                                        JSONObject object = new JSONObject(error.getErrorBody());
                                        if (object.has("error")) {
                                            sessionManager.ShowDialog(Login.this, object.getString("error"));
                                        } else {
                                            sessionManager.ShowDialog(Login.this, "Server is down please check.");
                                        }
                                    }else{
                                        sessionManager.ShowDialog(Login.this, "Something went wrong.");

                                    }

                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

//            NetworkingLibrary networkingLibrary = new NetworkingLibrary();
//            networkingLibrary.callMethodUsingPostAndResponseObject("auth/login", jsonObject);
//            networkingLibrary.getObserve(new DataObserver() {
//                @Override
//                public void objectResponse(JSONObject response) {
//
//                    try {
//                        dismissProgressDialog();
//                        Gson gson = new Gson();
//                        Register registerResponse = gson.fromJson(response.toString(), Register.class);
//
//                        sessionManager.setPreferences(Login.this, "androidId", deviceId);
//                        sessionManager.setPreferencesObject(Login.this, registerResponse.getUser());
//
//                        String role = sessionManager.getPreferences(Login.this, "role");
//                        String profile = registerResponse.getUser().getProfilePhoto();
//                        String email = registerResponse.getUser().getEmail();
//                        String id = registerResponse.getUser().getId();
//                        String token = registerResponse.getToken();
//                        sessionManager.setPreferences(Login.this, "profile", profile);
//                        sessionManager.setPreferences(Login.this, "email", email);
//                        sessionManager.setPreferences(Login.this, "id", id);
//                        sessionManager.setPreferences(Login.this, "token", token);
//
//                        if (!TextUtils.isEmpty(role)) {
//                            LoginUser createUser = new LoginUser();
//                            createUser.setEmail(txtEmail.getText().toString());
//                            createUser.setPassword(txtPassword.getText().toString());
//
//                            if (role.equalsIgnoreCase("client")) {
//                                sessionManager.setClient(Login.this, createUser);
//                                intent = new Intent(Login.this, ClientDrawer.class);
//
//                            } else {
//                                sessionManager.setContractor(Login.this, createUser);
//                                intent = new Intent(Login.this, MainDrawer.class);
//                            }
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                            startActivity(intent);
//                            finish();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//                @Override
//                public void onError(ANError e, String msg) {
//                    dismissProgressDialog();
//                    Log.e(TAG, "onError: "+msg );
//                    try {
//                        if(e.getErrorBody()!=null) {
//                            JSONObject object = new JSONObject(e.getErrorBody());
//                            if(object.has("error")) {
//                                sessionManager.ShowDialog(Login.this, object.getString("error"));
//                            }else{
//                                sessionManager.ShowDialog(Login.this, "Server is down please check.");
//                            }
//
//                        }
//                    } catch (JSONException ex) {
//                        ex.printStackTrace();
//                    }
//
//                }
//
//                @Override
//                public void arrayResponse(JSONArray jsonArray) {
//
//                }
//            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        //  if(sp.getPreferences(Login.this,"Login") == null) {
        if (result.isSuccess()) {
            sessionManager.setPreferences(getApplicationContext(), "Login", "GOOGLEYES");
            GoogleSignInAccount acct = result.getSignInAccount();
//            String idToken = acct.getIdToken();
            String idToken_ = acct.getId();
            Log.e(TAG, "handleSignInResult----: "+idToken_ );
            String deviceId = FirebaseInstanceId.getInstance().getToken();
            String role = sessionManager.getPreferences(Login.this, "role");
            sendPostGoogle(idToken_, role, deviceId);
            // Show signed-in UI.
            Log.d(TAG, "idToken:" + idToken_);
        } else {
            Log.e(TAG, "display name: ");
        }
        //  }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.google:
                signIn();
                break;

            case R.id.fb:
                loginButton.performClick();
                break;

            case R.id.btn_login:
                if (checkValidation()) {
                    login();
                    break;
                }
                break;

            case R.id.create_account:
                intent = new Intent(Login.this, SignUpSelection.class);
                startActivity(intent);
//                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;

            case R.id.lbl_forgotPassword:
                intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
//                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;

            case R.id.login_toolbar:
                intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
//                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            case R.id.show_password:
                if (txtPassword.getText().toString().trim().length() > 0) {
                    showPasswd = !showPasswd;
                    if (showPasswd) {
                        ivShowHidePasswd.setImageDrawable(getResources().getDrawable(R.drawable.password_hide));
                        //                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else {
                        ivShowHidePasswd.setImageDrawable(getResources().getDrawable(R.drawable.password_show));
                        //                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        txtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                    txtPassword.setSelection(txtPassword.getText().length());
                }
                break;
        }
    }

    private void login() {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String role = sessionManager.getPreferences(Login.this, "role");
        //user =sp.getPreferencesObject(Login.this);
        String deviceId = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "loginData:"+deviceId );
        sendPost(email, password, role, deviceId);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    public void showProgressDialog() {
        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading....");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing() && !mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        if (!this.isFinishing() && mProgressDialog != null)
            mProgressDialog.dismiss();
    }
}
