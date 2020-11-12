package com.tenderWatch.Drawer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.tenderWatch.ClientDrawer.Support;
import com.tenderWatch.ClientDrawer.TenderList;
import com.tenderWatch.CountryList;
import com.tenderWatch.MainActivity;
import com.tenderWatch.Models.ResponseNotifications;
import com.tenderWatch.Models.User;
import com.tenderWatch.PaymentSelection;
import com.tenderWatch.PesapalListActivity;
import com.tenderWatch.R;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Api mAPIService;
    private static final String TAG = MainDrawer.class.getSimpleName();
    Intent intent;
    public static MenuItem menu2;
    public static MenuItem editMenu;
    CircleImageView circledrawerimage;
    User user;
    TextView emailText;
    NavigationView navigationView;
    Menu menu;
    static Boolean display = false;
    SessionManager sessionManager;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.drawertoolbar);
        mAPIService = ApiUtils.getAPIService();

        sessionManager = new SessionManager(MainDrawer.this);

        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.about_3x);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawer,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description */
                R.string.navigation_drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                GetNotification();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        circledrawerimage = navigationView.getHeaderView(0).findViewById(R.id.circledrawerimage2);
        emailText = navigationView.getHeaderView(0).findViewById(R.id.textView2);
        user = sessionManager.getPreferencesObject(MainDrawer.this);

        if(user.getProfilePhoto()!=null) {
            if (!TextUtils.isEmpty(user.getProfilePhoto()) && !user.getProfilePhoto().equalsIgnoreCase("no image")) {
                Picasso.with(this).load(user.getProfilePhoto()).into(circledrawerimage);
            }
        }
        emailText.setText(user.getEmail());

        long days = getDate(user.getCreatedAt());

        if (days >= 30) {
            if (user.getSubscribe().equals("1")) {
                //Toast.makeText(this, "Your free trial is over please subscribe", Toast.LENGTH_SHORT).show();

            }
        } else {
            if (!user.getIsPayment() && (!user.getSubscribe().equals("1"))) {
//                Toast.makeText(this, ""+sessionManager.getSubscribeType(MainDrawer.this), Toast.LENGTH_SHORT).show();
                intent = new Intent(MainDrawer.this, PaymentSelection.class);
                intent.putExtra("amount", sessionManager.getPayment(MainDrawer.this));
                intent.putExtra("subscriptionType", Integer.parseInt(user.getSubscribe()));
                intent.putExtra("selections", sessionManager.getPaymentSelections(MainDrawer.this));
                startActivity(intent);
            }
        }
//        Toast.makeText(this, ""+user.getSubscribe(), Toast.LENGTH_SHORT).show();

        String get = getIntent().getStringExtra("nav_sub");
        String getnot = getIntent().getStringExtra("nav_not");
        if (getnot != null) {
            displaySelectedScreen(R.id.nav_notifications);
        } else if (get != null) {
            displaySelectedScreen(R.id.nav_subscriptiondetails);
        } else {
            emailText.setText(user.getEmail());
            displaySelectedScreen(R.id.nav_home);
        }
        GetNotification();

        circledrawerimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySelectedScreen(R.id.nav_editprofile);
            }
        });
    }

    private long getDate(String date) {
        try {
            long diff = System.currentTimeMillis() - new SimpleDateFormat("yyyy-MM-dd").parse(date.split("T")[0]).getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = (hours / 24);
            return days;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        } else {
//            super.onBackPressed();
        }
        // Checking for fragment count on backstack

        Bundle bundle = new Bundle();
//        Log.e(TAG, "onBackPressed: "+getSupportFragmentManager().getBackStackEntryAt(0).get());

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("tender");

//            Log.e(TAG, "onBackPressed: "+ getSupportFragmentManager().getFragments());

//            if(fragment instanceof Notification){
//                Log.e(TAG, "Notificationn" );
//            }else
//                setOptionMenu(false,false);

        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click Back again to exit.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_drawer, menu);
        editMenu = menu.findItem(R.id.menu_item2);
        menu2 = menu.findItem(R.id.menu_item);
        editMenu.setTitle("Edit");
        menu2.setVisible(false);
        editMenu.setVisible(false);
        // getMenuInflater().inflate(R.menu.main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_item) {
            call();
            return true;
        }
        if (id == R.id.drawertoolbar) {
            GetNotification();
            return true;
        }

        if (id == R.id.menu_item2) {
            callEdit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setMenuCounter(@IdRes int itemId, int count) {
        TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView().findViewById(R.id.count_view);
        if (count > 0) {
            view.setBackgroundResource(R.drawable.bg_red);
            view.setText(String.valueOf(count));
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void GetNotification() {
        String token = "Bearer " + sessionManager.getPreferences(MainDrawer.this, "token");
        Log.e(TAG, "GetNotification: "+ token );
        mAPIService.getNotifications(token).enqueue(new Callback<ArrayList<ResponseNotifications>>() {
            @Override
            public void onResponse(Call<ArrayList<ResponseNotifications>> call, Response<ArrayList<ResponseNotifications>> response) {
                Log.i(TAG, "post submitted to API." + response);
                int count = 0;
                if (response.body() != null) {
                    for (int i = 0; i < response.body().size(); i++) {
                        if (!response.body().get(i).getRead()) {

                            count += 1;
                        }
                    }
                }
                setMenuCounter(R.id.nav_notifications, count);
            }

            @Override
            public void onFailure(Call<ArrayList<ResponseNotifications>> call, Throwable t) {
                Log.i(TAG, "post submitted to API." + t);
            }
        });
    }

    public void callEdit() {
        Fragment fragment = null;
        fragment = new Notification();


        Bundle args = new Bundle();
        FragmentManager.BackStackEntry fragmentName = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1);
        Log.e(TAG, "callEdit: "+fragmentName );
        if (fragmentName instanceof Notification) {
            getSupportFragmentManager().popBackStack();

//
        }
        if (editMenu.getTitle().equals("Edit")) {

//
//            editMenu.setVisible(true);
            editMenu.setTitle("Cancel");
            args.putString("edit", "true");
            fragment.setArguments(args);
        } else {

//            Fragment fragmentName = getSupportFragmentManager().findFragmentByTag("tender");
//            if (fragmentName instanceof Notification) {
//                getSupportFragmentManager().popBackStack();
////
//            }
            editMenu.setTitle("Edit");
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment, "tender");
        ft.addToBackStack("tender");
        ft.commit();
//        Fragment fragmentName=getSupportFragmentManager().findFragmentByTag("tender");
//        if(fragmentName instanceof Notification){
//            getSupportFragmentManager().popBackStack();
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.content_frame, fragment, "tender");
//            ft.addToBackStack("tender");
//            ft.commit();
//        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void call() {
        Intent i = new Intent(getApplicationContext(), CountryList.class);
        i.putExtra("fromSubscription", true);
//        i.putExtra("sub", "1234");
        startActivity(i);
    }

    public void setOptionMenu(boolean isNotification, boolean isSubscription) {
        if (isSubscription) {
            menu2.setVisible(true);
            editMenu.setVisible(false);
        } else if (isNotification) {
            editMenu.setVisible(true);
            menu2.setVisible(false);
        } else {
            editMenu.setVisible(false);
            menu2.setVisible(false);
        }

    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;
        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_home:
                if (display) {
                    setOptionMenu(false, false);
                }
                fragment = new TenderList();
                display = true;
                break;
            case R.id.nav_subscriptiondetails:
                setOptionMenu(false, true);
                fragment = new SubscriptionContainer();
                break;
            case R.id.nav_editprofile:
                setOptionMenu(false, false);
                fragment = new EditProfile();
                break;
            case R.id.nav_changepassword:
                setOptionMenu(false, false);
                fragment = new ChangePassword();
                break;
            case R.id.nav_favorites:
                setOptionMenu(false, false);
                fragment = new TenderList();
                Bundle bundle = new Bundle();
                bundle.putString("nav_fav", "true");
                if (bundle != null) {
                    fragment.setArguments(bundle);
                }
                break;
            case R.id.nav_notifications:
                setOptionMenu(true, false);
                fragment = new Notification();
                break;
            case R.id.nav_contactsupportteam:
                setOptionMenu(false, false);
                fragment = new Support();
                break;
            case R.id.nav_logout:
                Logout();
                break;
            case R.id.nav_pesapal_list:
                Intent intent = new Intent(MainDrawer.this, PesapalListActivity.class);
                intent.putExtra("listOfPayment", true);
                startActivity(intent);
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment, "tender");
            ft.addToBackStack("tender");
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void Logout() {
        final ProgressDialog progressDialog = new ProgressDialog(MainDrawer.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        if (!MainDrawer.this.isFinishing())
            progressDialog.show();

        String token = "Bearer " + sessionManager.getPreferences(MainDrawer.this, "token");

        String role = sessionManager.getPreferences(MainDrawer.this, "role");
        String deviceId = sessionManager.getPreferences(MainDrawer.this, "androidId");

        mAPIService.logout(token, deviceId, role).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Log.i(TAG, "post submitted to API." + response);
                if (!MainDrawer.this.isFinishing())
                    progressDialog.dismiss();
                sessionManager.removePreferences(MainDrawer.this, "role");
                sessionManager.removePreferences(MainDrawer.this, "email");
                sessionManager.removePreferences(MainDrawer.this, "id");
                sessionManager.removePreferences(MainDrawer.this, "profile");
                sessionManager.removePreferences(MainDrawer.this, "user");
                sessionManager.removePreferences(MainDrawer.this, "token");

                sessionManager.clearData();

                intent = new Intent(MainDrawer.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
//                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "post submitted to API." + t);
                if (!MainDrawer.this.isFinishing())
                    progressDialog.dismiss();

                sessionManager.removePreferences(MainDrawer.this, "role");
                sessionManager.removePreferences(MainDrawer.this, "email");
                sessionManager.removePreferences(MainDrawer.this, "id");
                sessionManager.removePreferences(MainDrawer.this, "profile");
                sessionManager.removePreferences(MainDrawer.this, "user");
                sessionManager.removePreferences(MainDrawer.this, "token");

                sessionManager.clearData();

                intent = new Intent(MainDrawer.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
//                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}