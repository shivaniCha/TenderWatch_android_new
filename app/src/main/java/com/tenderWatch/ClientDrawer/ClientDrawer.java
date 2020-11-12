package com.tenderWatch.ClientDrawer;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.tenderWatch.Drawer.MainDrawer;
import com.tenderWatch.Drawer.Notification;
import com.tenderWatch.MainActivity;
import com.tenderWatch.Models.ResponseNotifications;
import com.tenderWatch.Models.User;
import com.tenderWatch.R;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;
import com.tenderWatch.utils.ConnectivityReceiver;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Api mAPIService;
    SessionManager sessionManager;
    private static final String TAG = ClientDrawer.class.getSimpleName();
    Intent intent;
    CircleImageView circledrawerimage;
    User user;
    TextView emailText;
    NavigationView navigationView;
    public MenuItem menu2;
    public static MenuItem editMenu;
    boolean doubleBackToExitPressedOnce = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_drawer);
        sessionManager = new SessionManager(ClientDrawer.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.clienttoolbar);
        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.apple);

        mAPIService = ApiUtils.getAPIService();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle;
        toggle = new ActionBarDrawerToggle(
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
        circledrawerimage = navigationView.getHeaderView(0).findViewById(R.id.circledrawerimage);
        emailText = navigationView.getHeaderView(0).findViewById(R.id.textView);
        user = sessionManager.getPreferencesObject(ClientDrawer.this);
        try {
            Log.e(TAG, "onCreate: " + user.getId());
            if (!user.getProfilePhoto().equals("no image")) {
                Picasso.with(this).load(user.getProfilePhoto()).into(circledrawerimage);
            }
            emailText.setText(user.getEmail());
            if (checkConnection()) {
                displaySelectedScreen(R.id.nav_home);
            } else {
                sessionManager.ShowDialog(ClientDrawer.this, "Please Check your internet Connection");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected(ClientDrawer.this);
        return isConnected;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_drawer, menu);
        getMenuInflater().inflate(R.menu.main_drawer, menu);
        editMenu = menu.findItem(R.id.menu_item2);
        menu2 = menu.findItem(R.id.menu_item);
        editMenu.setTitle("Edit");
        menu2.setVisible(false);
        editMenu.setVisible(false);
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
            return true;
        }

        if (id == R.id.menu_item2) {
            callEdit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void callEdit() {
        Fragment fragment = null;
        fragment = new Notification();
        Bundle args = new Bundle();
        if (editMenu.getTitle().equals("Edit")) {
            editMenu.setTitle("Cancel");
            args.putString("edit", "true");
            fragment.setArguments(args);
        } else {
            editMenu.setTitle("Edit");
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_home:
                if (editMenu != null && menu2 != null) {
//                    editMenu.setVisible(false);
//                    menu2.setVisible(false);
                    setOptionMenu(false, false);
                }
                fragment = new TenderList();
                break;
            case R.id.nav_uploadtender:
                if (editMenu != null && menu2 != null) {
//                    editMenu.setVisible(false);
//                    menu2.setVisible(false);

                    setOptionMenu(false, false);
                }
                fragment = new Home();
                break;
            case R.id.nav_editprofile:
                if (editMenu != null && menu2 != null) {
//                    editMenu.setVisible(false);
//                    menu2.setVisible(false);

                    setOptionMenu(false, false);
                }
                fragment = new EditProfile();
                break;
            case R.id.nav_changepassword:
                if (editMenu != null && menu2 != null) {
//                    editMenu.setVisible(false);
//                    menu2.setVisible(false);

                    setOptionMenu(false, false);
                }
                fragment = new ChangePassword();
                break;
            case R.id.nav_favorites:
                if (editMenu != null && menu2 != null) {
//                    editMenu.setVisible(false);
//                    menu2.setVisible(false);

                    setOptionMenu(false, false);
                }
                fragment = new ChangePassword();
                break;
            case R.id.nav_notifications:
                if (editMenu != null && menu2 != null) {
//                    editMenu.setVisible(true);
//                    menu2.setVisible(false);

                    setOptionMenu(true, false);
                }
                fragment = new Notification();
                break;
            case R.id.nav_contactsupportteam:
                if (editMenu != null && menu2 != null) {
//                    editMenu.setVisible(false);
//                    menu2.setVisible(false);

                    setOptionMenu(false, false);
                }
                fragment = new Support();
                break;
            case R.id.nav_logout:
                Logout();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void setMenuCounter(@IdRes int itemId, int count) {
        TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView().findViewById(R.id.count_view);
        if (count > 0) {
            view.setBackgroundResource(R.drawable.bg_red);
            view.setText(String.valueOf(count));
        }else{
            view.setVisibility(View.GONE);
        }
    }

    public void setOptionMenu(boolean isNotification, boolean isSubscription) {
        if (isSubscription) {
            menu2.setVisible(true);
            editMenu.setVisible(false);
        } else if (isNotification) {
            editMenu.setTitle("Edit");
            editMenu.setVisible(true);
            menu2.setVisible(false);
        } else {
            editMenu.setVisible(false);
            menu2.setVisible(false);
        }

    }

    private void GetNotification() {
        String token = "Bearer " + sessionManager.getPreferences(ClientDrawer.this, "token");
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

    public void Logout() {

        final ProgressDialog progressDialog = new ProgressDialog(ClientDrawer.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        if (!ClientDrawer.this.isFinishing())
            progressDialog.show();

        String token = "Bearer " + sessionManager.getPreferences(ClientDrawer.this, "token");
        String deviceId = sessionManager.getPreferences(getApplicationContext(), "deviceId");
        String role = sessionManager.getPreferences(ClientDrawer.this, "role");

        mAPIService.logout(token, deviceId, role).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!ClientDrawer.this.isFinishing())
                    progressDialog.dismiss();

                sessionManager.removePreferences(ClientDrawer.this, "role");
                sessionManager.removePreferences(ClientDrawer.this, "email");
                sessionManager.removePreferences(ClientDrawer.this, "id");
                sessionManager.removePreferences(ClientDrawer.this, "profile");
                sessionManager.removePreferences(ClientDrawer.this, "user");
                sessionManager.removePreferences(ClientDrawer.this, "token");
                sessionManager.clearData();


                intent = new Intent(ClientDrawer.this, MainActivity.class);
                startActivity(intent);
//                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!ClientDrawer.this.isFinishing())
                    progressDialog.dismiss();

                sessionManager.removePreferences(ClientDrawer.this, "role");
                sessionManager.removePreferences(ClientDrawer.this, "email");
                sessionManager.removePreferences(ClientDrawer.this, "id");
                sessionManager.removePreferences(ClientDrawer.this, "profile");
                sessionManager.removePreferences(ClientDrawer.this, "user");
                sessionManager.removePreferences(ClientDrawer.this, "token");

                sessionManager.clearData();


                intent = new Intent(ClientDrawer.this, MainActivity.class);
                startActivity(intent);
//                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Checking for fragment count on backstack
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
        //finish();
    }

}
