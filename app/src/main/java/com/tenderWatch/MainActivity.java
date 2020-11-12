package com.tenderWatch;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.tenderWatch.ClientDrawer.ClientDrawer;
import com.tenderWatch.ClientDrawer.Support;
import com.tenderWatch.Drawer.MainDrawer;
import com.tenderWatch.SharedPreference.SessionManager;
import com.tenderWatch.app.Config;
import com.tenderWatch.databinding.ActivityMainBinding;
import com.tenderWatch.utils.NotificationUtils;
import com.tenderWatch.utils.TypeWriterTextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    Context context;
    Intent intent;
    SessionManager sessionManager;
    private static final String TAG = MainActivity.class.getSimpleName();
    Object user;
    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);

        sessionManager = new SessionManager(MainActivity.this);

        context = this;
        InitListener();
        user = sessionManager.getPreferencesObject(MainActivity.this);
        if (user != null) {
            String role = sessionManager.getPreferences(MainActivity.this, "role");
            if (!TextUtils.isEmpty(role) && role.equalsIgnoreCase("client")) {
                intent = new Intent(MainActivity.this, ClientDrawer.class);
            } else {
                intent = new Intent(MainActivity.this, MainDrawer.class);
            }
            sessionManager.setPreferences(MainActivity.this, "role", role);
            // intent.putExtra("Role", "client");
            Log.i(TAG, "testing");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
//            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    //txtMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();

        try {
            ((TypeWriterTextView) findViewById(R.id.tv_about)).setCharacterDelay(100);
            ((TypeWriterTextView) findViewById(R.id.tv_about)).animateText(((TypeWriterTextView) findViewById(R.id.tv_about)).getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            Log.e(TAG, "Firebase Reg Id: " + regId);
        else
            Log.e(TAG, "Firebase Reg Id is not received yet!");
    }

    private void InitListener() {
        binding.btnContractor.setOnClickListener(this);
        binding.btnClient.setOnClickListener(this);
        binding.aboutapp.setOnClickListener(this);
        String deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        sessionManager.setPreferences(getApplicationContext(), "deviceId", deviceId);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.aboutapp:
                final Dialog dialog = new Dialog(context, R.style.full_screen_dialog);
                dialog.setContentView(R.layout.aboutapp);

                Button dismissButton = dialog.findViewById(R.id.button_close);
                dismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                WebView mWebView = dialog.findViewById(R.id.about_webview);
                WebSettings webSettings = mWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                mWebView.loadUrl("file:///android_res/raw/about.htm");
                mWebView.setWebViewClient(new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Log.e(TAG, "shouldOverrideUrlLoading: URL : " + url);
                        if (url.contains("contactSupport")) {
                            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                            emailIntent.setType("plain/text");
                            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"support@tenderwatch.com"});
                            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                            view.stopLoading();
                            return false;
                        }
                        return super.shouldOverrideUrlLoading(view, url);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        Log.e(TAG, "onPageStarted: url : " + url);
                        super.onPageStarted(view, url, favicon);
                    }
                });
                dialog.show();
                break;
            case R.id.btn_contractor:
                intent = new Intent(MainActivity.this, Login.class);
                sessionManager.setPreferences(getApplicationContext(), "role", "contractor");
                intent.putExtra("Role", "contractor");
                startActivity(intent);
//                overridePendingTransition(R.anim.enter, R.anim.exit);

                break;
            case R.id.btn_client:
                intent = new Intent(MainActivity.this, Login.class);
                sessionManager.setPreferences(MainActivity.this, "role", "client");
                intent.putExtra("Role", "client");
                Log.i(TAG, "testing");
                startActivity(intent);
//                overridePendingTransition(R.anim.enter, R.anim.exit);

                break;
        }
    }

    boolean doubleBackToExitPressedOnce = true;

    @Override
    public void onBackPressed() {
        // Checking for fragment count on backstack
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
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


    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
