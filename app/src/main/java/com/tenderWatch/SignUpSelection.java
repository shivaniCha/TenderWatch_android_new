package com.tenderWatch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tenderWatch.Models.CreateUser;
import com.tenderWatch.Models.Message;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;
import com.tenderWatch.Validation.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignUpSelection extends FragmentActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    Intent intent;
    Button signUp;
    private CallbackManager callbackManager;
    private static final String TAG = SignUpSelection.class.getSimpleName();
    private EditText txtEmail, txtPassword, txtConfirmPassword;
    private LoginButton loginButton;
    private Button btnFacebook, signUP;
    private static final int RC_SIGN_IN = 007;
    CreateUser user = new CreateUser();
    SessionManager sessionManager;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private SignInButton btnSignIn;
    private Button btnSignOut, btnRevokeAccess, btngoogle, btnlogin;
    private LinearLayout back;
    private ImageView imgProfilePic;
    private Api mAPIService;
    String newString, profilePicUrl;
    Bitmap main;
    boolean showPasswd = false,showConfirmPasswd=false;
    String socialToken ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_signup);

        sessionManager = new SessionManager(SignUpSelection.this);

        System.out.print("called");
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
        InitView();
        InitListener();
    }

    private void InitListener() {
        btnSignIn.setOnClickListener(this);
        btngoogle.setOnClickListener(this);
        signUp.setOnClickListener(this);
        back.setOnClickListener(this);

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Validation.isEmailAddress(txtEmail, true);
            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Validation.isPassword(txtPassword, true);
                Log.i(TAG, "post submitted to API." + Validation.isValidPassword(txtPassword.getText().toString()));
            }
        });

        txtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Validation.isPassword(txtConfirmPassword, true);
                Log.i(TAG, "post submitted to API." + Validation.isValidPassword(txtPassword.getText().toString()));
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestServerAuthCode(getString(R.string.server_client_id), false)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());
        btnFacebook.setOnClickListener(this);
        loginButton.setPermissions("email", "public_profile", "user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, String.valueOf(loginResult));

                String accessToken = loginResult.getAccessToken().getToken();
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.e("Main", response.toString());
                                try {
                                    Log.i(TAG, object.getString("email"));
                                    JSONObject data = response.getJSONObject();
                                    socialToken =object.getString("id");
                                    if (data.has("picture")) {
                                        profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                        String src = profilePicUrl;
                                        URL url = new URL(profilePicUrl);
                                        txtEmail.setText(object.getString("email"));
                                        Picasso.with(SignUpSelection.this)
                                                .load(profilePicUrl)
                                                .into(new Target() {
                                                    @Override
                                                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                                        Log.e("Main", String.valueOf(bitmap));
                                                        main = bitmap;
                                                        Picasso.with(SignUpSelection.this).load(profilePicUrl).into(target);
                                                    }

                                                    @Override
                                                    public void onBitmapFailed(Drawable errorDrawable) {
                                                        Log.e("Main", "errrorrrr");
                                                    }

                                                    @Override
                                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                    }
                                                });
                                        Log.i(TAG, profilePicUrl);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday,cover,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
                String deviceId = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                if (AccessToken.getCurrentAccessToken() != null) {
                    Log.v("User is login", "YES");
                }
                sessionManager.setPreferences(getApplicationContext(), "Login", "FBYES");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onConnectionFailed:");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d(TAG, "onConnectionFailed:"+e.toString());
            }
        });

    }

    private void InitView() {
        btnFacebook = (Button) findViewById(R.id.fbsignup);
        loginButton = (LoginButton) findViewById(R.id.signupfb_button);
        mAPIService = ApiUtils.getAPIService();

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        btnSignIn = (SignInButton) findViewById(R.id.signupgoogle_button);
        btngoogle = (Button) findViewById(R.id.googlesignup);
        txtEmail = (EditText) findViewById(R.id.signup_email);
        txtPassword = (EditText) findViewById(R.id.signup_password);
        signUp = (Button) findViewById(R.id.btn_client_signup);
        txtConfirmPassword = (EditText) findViewById(R.id.signup_confirmpassword);
        back = (LinearLayout) findViewById(R.id.client_signup_back);
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
            // showProgressDialog();
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
        }
    }

    private boolean checkValidation() {
        boolean ret = true;

        if (!Validation.isEmailAddress(txtEmail, true)) ret = false;
        if (!Validation.isPassword(txtPassword, true)) ret = false;

        return ret;
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        // if(sp.getPreferences(SignUpSelection.this,"Login") == null) {
        if (result.isSuccess()) {
            sessionManager.setPreferences(getApplicationContext(), "Login", "GOOGLEYES");
            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            final Uri personPhoto = acct.getPhotoUrl();
            txtEmail.setText(personEmail);

//            String idToken = acct.getIdToken();
            String idToken = acct.getId();
            socialToken =idToken;
            String deviceId = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Log.d(TAG, "idToken:" + idToken);
            Picasso.with(SignUpSelection.this)
                    .load(personPhoto)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            /* Save the bitmap or do something with it here */
                            Log.v("Main", String.valueOf(bitmap));
                            main = bitmap;
                            Picasso.with(SignUpSelection.this).load(personPhoto).into(target);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            Log.v("Main", "errrorrrr");
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
        } else {
            Log.e(TAG, "display name: ");
        }
        //}
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

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/saved.jpg");
            try {
                file.createNewFile();
                FileOutputStream ostream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                ostream.close();
                user.setProfilePhoto(file);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.googlesignup:
                signIn();
                break;
            case R.id.fbsignup:
                loginButton.performClick();
                break;
            case R.id.btn_client_signup:
                if (checkValidation()) {
                    checkEmail();
                }
                break;
            case R.id.client_signup_back:
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(SignUpSelection.this, Login.class);
                        startActivity(intent);
                    }
                });
                break;
            case R.id.show_password:
                if (txtPassword.getText().toString().trim().length() > 0) {
                    showPasswd = !showPasswd;
                    if (showPasswd) {
                        ((ImageView)findViewById(R.id.show_password)).setImageDrawable(getResources().getDrawable(R.drawable.password_hide));
                        //                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else {
                        ((ImageView)findViewById(R.id.show_password)).setImageDrawable(getResources().getDrawable(R.drawable.password_show));
                        //                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        txtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                    txtPassword.setSelection(txtPassword.getText().length());
                }
                break;
            case R.id.show_confirm_password:
                if (txtConfirmPassword.getText().toString().trim().length() > 0) {
                    showConfirmPasswd = !showConfirmPasswd;
                    if (showConfirmPasswd) {
                        ((ImageView)findViewById(R.id.show_confirm_password)).setImageDrawable(getResources().getDrawable(R.drawable.password_hide));
                        //                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        txtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else {
                        ((ImageView)findViewById(R.id.show_confirm_password)).setImageDrawable(getResources().getDrawable(R.drawable.password_show));
                        //                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        txtConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                    txtConfirmPassword.setSelection(txtConfirmPassword.getText().length());
                }
                break;
        }
    }

    public void checkEmail() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        if (!this.isFinishing())
            progressDialog.show();

        mAPIService.checkEmailExit(txtEmail.getText().toString(), sessionManager.getPreferences(getApplicationContext(), "role")).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                //  if (response.isSuccessful()) {
                if (!SignUpSelection.this.isFinishing())
                    progressDialog.dismiss();

                if (response.code() == 404) {
                    signup();
                } else {
                    String message = "This Email is already used.\n\nPlease use another email to signup in TenderWatch";
                    sessionManager.ShowDialog(SignUpSelection.this, message);
                    txtEmail.setError("Change Email");
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                if (!SignUpSelection.this.isFinishing())
                    progressDialog.dismiss();
                sessionManager.ShowDialog(SignUpSelection.this, "Server is down. Come back later!!");
            }
        });
    }

    private void signup() {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();
        if (!confirmPassword.equals(password)) {
            sessionManager.ShowDialog(SignUpSelection.this, "Confirm password does not match");
            txtConfirmPassword.setText("");
        } else {
            user.setEmail(email);
            user.setPassword(password);
            if(!socialToken.isEmpty()){
                user.setGoogleToken(socialToken);
            }
            intent = new Intent(SignUpSelection.this, SignUp.class);
            intent.putExtra("bitmap", main);
//            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
