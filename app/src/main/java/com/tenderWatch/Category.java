package com.tenderWatch;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tenderWatch.Adapters.ArrayAdapter;
import com.tenderWatch.ClientDrawer.ClientDrawer;
import com.tenderWatch.Drawer.MainDrawer;
import com.tenderWatch.Drawer.PaymentHistoryFragment;
import com.tenderWatch.Models.CreateUser;
import com.tenderWatch.Models.GetCategory;
import com.tenderWatch.Models.Register;
import com.tenderWatch.Models.SubscriptionCategoryResponse;
import com.tenderWatch.Models.User;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Category extends AppCompatActivity {
    private ListView lvCountry;
    private static final String TAG = Category.class.getSimpleName();
    private Api mAPIService;
    private SideSelector sideSelector = null;
    private List Data;
    private List<GetCategory> category = new ArrayList<>();
    private static ArrayList<String> alpha = new ArrayList<String>();
    private static ArrayList<String> alpha2 = new ArrayList<String>();
    public static final ArrayList<String> list = new ArrayList<String>();
    public static final ArrayList<String> countryId = new ArrayList<String>();
    private String planTitle = "";
    private int selectedVat = 0;

    String alphabetS = "";
    private static ArrayList<Item> countryList = new ArrayList<Item>();
    ArrayAdapter bAdapter;
    public static char[] alphabetlist = new char[27];
    ArrayList<String> empNo, countryListName;
    CreateUser user = new CreateUser();
    Button btnCategory;
    private static int p = 0, k = 0;
    public static HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
    private static ArrayList<String> a_category = new ArrayList<String>();
    Intent intent;
    SessionManager sessionManager;
    LinearLayout back;
    TextView txtContract;
    String contract, s;
    private int amount = 0;
    private boolean fromRegister = false;
    private int subscriptionType = 1;
    double totalAmount = 0;
    private String planType = "";


    private String firstname = "";
    private String lastname = "";
    private String email = "";
    private String password = "";
    private String country = "";
    private String contact = "";
    private String occupation = "";
    private String aboutMe = "";
    private String role = "";
    private String deviceId = "";
    private String subscribe = "";
    private String file = "";
    private String googleToken="";

    private ProgressDialog mProgressDialog;
    MultipartBody.Part deviceId2, googleToken1,selections1, email1, password1, country1, deviceTyp1, subscribe1, contactNo1, occupation1, aboutMe1, role1, deviceId1, image1, subRole1, firstname1, lastname1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        sessionManager = new SessionManager(Category.this);
        getDataFromIntent();

        lvCountry = (ListView) findViewById(R.id.lvCategory);
        btnCategory = (Button) findViewById(R.id.btn_CategoryNext);
        sideSelector = (SideSelector) findViewById(R.id.category_side_selector);
        back = (LinearLayout) findViewById(R.id.category_toolbar);
        txtContract = (TextView) findViewById(R.id.txt_selectedContractcategory);
        mAPIService = ApiUtils.getAPIService();
        lvCountry.setDivider(null);
        lvCountry.clearChoices();

//        txtContract.setText(contract);
        if (empNo != null && empNo.size() > 0) {
            totalAmount = amount * empNo.size();
            if (subscriptionType == 2)
                txtContract.setText("Tzs " + totalAmount + " / month");
            else if (subscriptionType == 3)
                txtContract.setText("Tzs " + totalAmount + " / year");
            else
                txtContract.setText(contract);
        } else {
            totalAmount = amount;
            txtContract.setText(contract);
        }

        if (empNo != null && !empNo.isEmpty())
            user.setSelections(empNo.size());

        alpha = new ArrayList<>();
        alpha2 = new ArrayList<>();
        countryList = new ArrayList<>();

        callApiForGetCategoryData();

        lvCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final ProgressDialog progressDialog = new ProgressDialog(Category.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);

                if (!Category.this.isFinishing())
                    progressDialog.show();
                if (sessionManager.getPreferences(Category.this, "token") != null) {
                    String token = "Bearer " + sessionManager.getPreferences(Category.this, "token");
                    mAPIService.checkSubscription(token, countryList.get(position).getcountryId(), countryList.get(position).getId()).enqueue(new Callback<CheckServiceModel>() {
                        @Override
                        public void onResponse(Call<CheckServiceModel> call, Response<CheckServiceModel> response) {
                            if (!Category.this.isFinishing())
                                progressDialog.dismiss();
                            if (response.body() != null) {
                                if (response.body().status) {
                                    bAdapter.setItemSelected(position);
                                    boolean checked = bAdapter.setCheckedItem(position);

                                    HashMap<String, String> items = bAdapter.getallitems();
                                    Log.e(TAG, "onItemClick: Items : " + items.entrySet().size());

                                    if (checked) {
                                        if (items.size() > empNo.size())
                                            totalAmount = totalAmount + amount;
                                    } else {
                                        if (items.size() > empNo.size())
                                            totalAmount = items.size() * amount;
                                        else
                                            totalAmount = empNo.size() * amount;
                                    }

                                    if (subscriptionType == 2)
                                        txtContract.setText("Tzs " + totalAmount + " / month");
                                    else if (subscriptionType == 3)
                                        txtContract.setText("Tzs " + totalAmount + " / year");
                                } else {
                                    Toast.makeText(Category.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CheckServiceModel> call, Throwable t) {
                            if (!Category.this.isFinishing())
                                progressDialog.dismiss();
                            Toast.makeText(Category.this, "You have already subscribe for the same", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    bAdapter.setItemSelected(position);
                    boolean checked = bAdapter.setCheckedItem(position);

                    HashMap<String, String> items = bAdapter.getallitems();
                    Log.e(TAG, "onItemClick: Items : " + items.entrySet().size());

                    if (checked) {
                        if (items.size() > empNo.size())
                            totalAmount = totalAmount + amount;
                    } else {
                        if (items.size() > empNo.size())
                            totalAmount = items.size() * amount;
                        else
                            totalAmount = empNo.size() * amount;
                    }

                    if (subscriptionType == 2)
                        txtContract.setText("Tzs" + totalAmount + " / month");
                    else if (subscriptionType == 3)
                        txtContract.setText("Tzs " + totalAmount + " / year");
                }

            }
        });

        btnCategory.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                HashMap<String, String> items = bAdapter.getallitems();
                map.clear();
                for (Map.Entry<String, String> entry : items.entrySet()) {
                    Log.e(TAG, "onClick: entry : " + new Gson().toJson(entry));
                    populateMap(map, entry.getValue().split("~")[1], entry.getValue().split("~")[2]);
                }

                Log.e(TAG, "onClick: final entry details : " + new Gson().toJson(map));
                if (empNo.size() == map.size()) {
                    if (txtContract.getText().toString().equals("Trial Version")) {
                        if (items.size() > 1) {
                            sessionManager.ShowDialog(Category.this, "During Free Trial Period you can choose only 1 category");
                            return;
                        }
                    } else {
                        if (map == null || map.size() == 0) {
                            sessionManager.ShowDialog(Category.this, "Please choose one category");
                            return;
                        }
                    }
                    showConfirmationPayment();
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(Category.this).create();
                    alertDialog.setTitle("Tender Watch");
                    alertDialog.setMessage("Please select at least one category per country.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Okay", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Category.this, CountryList.class);
//                intent.putExtra("sub", "1234");
                intent.putExtra("fromSignUP", fromRegister);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showConfirmationPayment() {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.fragment_payment_history, null, false);
        RecyclerView rvTransaction = view.findViewById(R.id.rv_transaction);
        SwipeRefreshLayout srl = view.findViewById(R.id.srl_transaction);
        TextView tvAmount = view.findViewById(R.id.tv_amount);
        tvAmount.setVisibility(View.VISIBLE);
        srl.setEnabled(false);
        try {
            List<PendingHistory> history = new ArrayList<>();
            JSONObject sa = new JSONObject(map);
            Iterator<String> iter = sa.keys();
            for (int i = 0; i < map.size(); i++) {
                String key = iter.next();
                PendingHistory h = new PendingHistory();
                h.setCountryId(key);
                List<String> cat = new ArrayList<>();
                for (int j = 0; j < sa.getJSONArray(key).length(); j++) {
                    cat.add(sa.getJSONArray(key).get(j).toString());
                }
                h.setCategoryId(cat);
                h.setPlanId(planType);
                h.setSubscription(subscriptionType);
                history.add(h);
            }

            rvTransaction.setAdapter(new PaymentAdapter(history));
            rvTransaction.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        double vatAmount = (totalAmount / 100) * selectedVat;
        double baseAmount = totalAmount;
        totalAmount = baseAmount + vatAmount;
        DecimalFormat twoDForm = new DecimalFormat("#.##");

        vatAmount = Double.valueOf(twoDForm.format(vatAmount));
        totalAmount = Double.valueOf(twoDForm.format(totalAmount));
        baseAmount = Double.valueOf(twoDForm.format(baseAmount));

        tvAmount.setText("Total amount " + baseAmount + "Tzs + " + vatAmount + "Tzs vat = " +"Tzs "+ totalAmount);
        new AlertDialog.Builder(Category.this)
                .setTitle("Confirm your purchase")
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        selectPayment();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void selectPayment() {
        if (subscriptionType > 1) {
            //if (s != null || !fromRegister)
            if (fromRegister) {
                showProgressDialog();
                if (!file.isEmpty()) {
                    File file1 = new File(file);

                    RequestBody requestFile;
                    if (file != null) {
                        requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
                        image1 = MultipartBody.Part.createFormData("image", file, requestFile);
                    } else {
                        image1 = MultipartBody.Part.createFormData("image", "");
                    }
                } else {
                    image1 = MultipartBody.Part.createFormData("image", "");
                }
                firstname1 = MultipartBody.Part.createFormData("firstName", firstname);
                lastname1 = MultipartBody.Part.createFormData("lastName", lastname);
                email1 = MultipartBody.Part.createFormData("email", email);
                password1 = MultipartBody.Part.createFormData("password", password);
                country1 = MultipartBody.Part.createFormData("country", country);
                contactNo1 = MultipartBody.Part.createFormData("contactNo", contact);
                occupation1 = MultipartBody.Part.createFormData("occupation", occupation);
                aboutMe1 = MultipartBody.Part.createFormData("aboutMe", aboutMe);
                role1 = MultipartBody.Part.createFormData("role", role);
                deviceId1 = MultipartBody.Part.createFormData("androidDeviceId", deviceId);
                subscribe1 = MultipartBody.Part.createFormData("subscribe", String.valueOf(subscriptionType));
                selections1 = MultipartBody.Part.createFormData("selections", new Gson().toJson(map));
                if(googleToken!=null && !googleToken.isEmpty()) {
                    googleToken1 = MultipartBody.Part.createFormData("loginToken", googleToken);
                }else{
                    googleToken1 = MultipartBody.Part.createFormData("loginToken", "");
                }
//                selections1 = MultipartBody.Part.createFormData("selections", map.toString());
                //subRole1= MultipartBody.Part.createFormData("subRole",subRole);

                Log.e(TAG, "Firstname: " + firstname);
                Log.e(TAG, "lastName: " + lastname);
                Log.e(TAG, "email: " + email);
                Log.e(TAG, "password: " + password);
                Log.e(TAG, "confirmPassword: " + password);
                Log.e(TAG, "country: " + country);
                Log.e(TAG, "contactNo: " + contact);
                Log.e(TAG, "occupation: " + occupation);
                Log.e(TAG, "aboutMe: " + aboutMe);
                Log.e(TAG, "role: " + role);
                Log.e(TAG, "deviceId: " + deviceId);
                Log.e(TAG, "selections1: " + new Gson().toJson(map));
                Log.e(TAG, "subscribe1: " + subscriptionType);
                Log.e(TAG, "googleToken: " + googleToken);

                Call<Register> resultCall = mAPIService.uploadContractor(email1, password1, country1, contactNo1, occupation1, aboutMe1, role1, deviceId1, image1, subscribe1, selections1, firstname1, lastname1,googleToken1);
                resultCall.enqueue(new Callback<Register>() {
                    @Override
                    public void onResponse(Call<Register> call, Response<Register> response) {
                        Log.i(TAG, "response register-->");

                        dismissProgressDialog();

                        if (response.isSuccessful()) {
                            Gson gson = new Gson();
                            String jsonString = gson.toJson(user);
                            User u1 = response.body().getUser();
                            sessionManager.setPreferencesObject(Category.this, u1);
                            sessionManager.setPreferences(Category.this, "token", response.body().getToken());

                            sessionManager.setPaymentSelections(Category.this, map);
                            sessionManager.setPayment(Category.this, amount);
                            intent = new Intent(Category.this, PaymentSelection.class);
                            if (txtContract.getText().toString().equals("Trial Version"))
                                intent.putExtra("isTrial", true);
                            else
                                intent.putExtra("isTrial", false);

                            intent.putExtra("amount1", totalAmount);
                            intent.putExtra("subscriptionType", subscriptionType);
                            intent.putExtra("selections", map);
                            intent.putExtra("isFromRegister", fromRegister);
                            intent.putExtra("planType", planType);
                            user.setSubscribe(map);


                            startActivity(intent);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            map.clear();
                        } else {
                            sessionManager.ShowDialog(Category.this, response.errorBody().source().toString().split("\"")[3]);
                        }
                    }

                    @Override
                    public void onFailure(Call<Register> call, Throwable t) {
                        dismissProgressDialog();
                        Log.i(TAG, "error register-->" + t.getMessage());
                        sessionManager.ShowDialog(Category.this, "Server is down. Come back later!!");
                    }
                });
            } else {
                intent = new Intent(Category.this, PaymentSelection.class);
                //else
                //    intent = new Intent(Category.this, Category.class);
                if (txtContract.getText().toString().equals("Trial Version"))
                    intent.putExtra("isTrial", true);
                else
                    intent.putExtra("isTrial", false);

                intent.putExtra("amount1", totalAmount);
                intent.putExtra("subscriptionType", subscriptionType);
                intent.putExtra("selections", map);
                intent.putExtra("isFromRegister", fromRegister);
                intent.putExtra("planType", planType);
//        intent.putExtra("id", id);
                user.setSubscribe(map);
                startActivity(intent);
                map.clear();
            }
        } else {
            if (fromRegister) {
                showProgressDialog();
                if (!file.isEmpty()) {
                    File file1 = new File(file);

                    RequestBody requestFile;
                    if (file != null) {
                        requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
                        image1 = MultipartBody.Part.createFormData("image", file, requestFile);
                    } else {
                        image1 = MultipartBody.Part.createFormData("image", "");
                    }
                } else {
                    image1 = MultipartBody.Part.createFormData("image", "");
                }
                firstname1 = MultipartBody.Part.createFormData("firstName", firstname);
                lastname1 = MultipartBody.Part.createFormData("lastName", lastname);
                email1 = MultipartBody.Part.createFormData("email", email);
                password1 = MultipartBody.Part.createFormData("password", password);
                country1 = MultipartBody.Part.createFormData("country", country);
                contactNo1 = MultipartBody.Part.createFormData("contactNo", contact);
                occupation1 = MultipartBody.Part.createFormData("occupation", occupation);
                aboutMe1 = MultipartBody.Part.createFormData("aboutMe", aboutMe);
                role1 = MultipartBody.Part.createFormData("role", role);
                deviceId1 = MultipartBody.Part.createFormData("androidDeviceId", deviceId);
                subscribe1 = MultipartBody.Part.createFormData("subscribe", String.valueOf(subscriptionType));
                selections1 = MultipartBody.Part.createFormData("selections", new Gson().toJson(map));
                if(googleToken!=null && !googleToken.isEmpty()) {
                    googleToken1 = MultipartBody.Part.createFormData("loginToken", googleToken);
                }else{
                    googleToken1 = MultipartBody.Part.createFormData("loginToken", "");
                }
//                selections1 = MultipartBody.Part.createFormData("selections", map.toString());
                //subRole1= MultipartBody.Part.createFormData("subRole",subRole);
                String selectionGson = new Gson().toJson(map);
                Log.e(TAG, "Firstname: " + firstname);
                Log.e(TAG, "lastName: " + lastname);
                Log.e(TAG, "email: " + email);
                Log.e(TAG, "password: " + password);
                Log.e(TAG, "confirmPassword: " + password);
                Log.e(TAG, "country: " + country);
                Log.e(TAG, "contactNo: " + contact);
                Log.e(TAG, "occupation: " + occupation);
                Log.e(TAG, "aboutMe: " + aboutMe);
                Log.e(TAG, "role: " + role);
                Log.e(TAG, "deviceId: " + deviceId);
                Log.e(TAG, "selections1: " + new Gson().toJson(map));
                Log.e(TAG, "subscribe1: " + subscriptionType);
                try {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("image", "");
                    jsonObject.put("firstName", firstname);
                    jsonObject.put("lastName", lastname);
                    jsonObject.put("email", email);
                    jsonObject.put("password", password);
                    jsonObject.put("country", country);
                    jsonObject.put("contactNo", contact);
                    jsonObject.put("occupation", occupation);
                    jsonObject.put("aboutMe", aboutMe);
                    jsonObject.put("role", role);
                    jsonObject.put("androidDeviceId", deviceId);
                    jsonObject.put("subscribe", subscribe);
                    jsonObject.put("selections", new Gson().toJson(map));
                    jsonObject.put("loginToken", googleToken);

                    Log.e(TAG, "selectPayment: " + jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Call<Register> resultCall = mAPIService.uploadContractor(email1, password1, country1, contactNo1, occupation1, aboutMe1, role1, deviceId1, image1, subscribe1, selections1, firstname1, lastname1,googleToken1);
                resultCall.enqueue(new Callback<Register>() {
                    @Override
                    public void onResponse(Call<Register> call, Response<Register> response) {
                        Log.i(TAG, "response register-->");

                        dismissProgressDialog();

                        if (response.isSuccessful()) {
                            Gson gson = new Gson();
                            String jsonString = gson.toJson(user);
                            User u1 = response.body().getUser();
                            sessionManager.setPreferencesObject(Category.this, u1);
                            sessionManager.setPreferences(Category.this, "token", response.body().getToken());

                            Log.e("tokennn", "onResponse1: " + response.body().getToken());
                            sessionManager.setPaymentSelections(Category.this, map);
                            sessionManager.setPayment(Category.this, amount);
                            intent = new Intent(Category.this, MainDrawer.class);
                            if (txtContract.getText().toString().equals("Trial Version"))
                                intent.putExtra("isTrial", true);
                            else
                                intent.putExtra("isTrial", false);

                            intent.putExtra("amount1", totalAmount);
                            intent.putExtra("subscriptionType", subscriptionType);
                            intent.putExtra("selections", map);
                            intent.putExtra("isFromRegister", fromRegister);
                            intent.putExtra("planType", planType);
                            user.setSubscribe(map);
                            startActivity(intent);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            map.clear();
                        } else {
                            sessionManager.ShowDialog(Category.this, response.errorBody().source().toString().split("\"")[3]);
                        }
                    }

                    @Override
                    public void onFailure(Call<Register> call, Throwable t) {
                        dismissProgressDialog();
                        Log.i(TAG, "error register-->" + t.getMessage());
                        sessionManager.ShowDialog(Category.this, "Server is down. Come back later!!");
                    }
                });
            } else {

                intent = new Intent(Category.this, MainDrawer.class);
                if (txtContract.getText().toString().equals("Trial Version"))
                    intent.putExtra("isTrial", true);
                else
                    intent.putExtra("isTrial", false);

                intent.putExtra("amount1", totalAmount);
                intent.putExtra("subscriptionType", subscriptionType);
                intent.putExtra("selections", map);
                intent.putExtra("isFromRegister", fromRegister);
                intent.putExtra("planType", planType);
                user.setSubscribe(map);
                startActivity(intent);
                map.clear();
            }

        }

    }

    public void getDataFromIntent() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().getStringArrayList("CountryAtContractor") != null) {
                empNo = getIntent().getExtras().getStringArrayList("CountryAtContractor");
            }
            if (getIntent().getExtras().getString("sub") != null) {
                s = getIntent().getExtras().getString("sub");
            }
            if (getIntent().getExtras().getStringArrayList("Country") != null) {
                countryListName = getIntent().getExtras().getStringArrayList("Country");
            }
            if (getIntent().getExtras().getString("version") != null) {
                contract = getIntent().getExtras().getString("version");
            }
            planType = getIntent().getStringExtra("planType");
            planTitle = getIntent().getStringExtra("planTitle");
            selectedVat = getIntent().getIntExtra("vat", 1);
            fromRegister = getIntent().getBooleanExtra("isFromRegister", false);
            amount = getIntent().getExtras().getInt("amount", 0);
            subscriptionType = getIntent().getExtras().getInt("subscriptionType", 1);

            if (fromRegister) {
                firstname = getIntent().getStringExtra("firstname");
                lastname = getIntent().getStringExtra("lastname");
                email = getIntent().getStringExtra("email");
                password = getIntent().getStringExtra("password");
                country = getIntent().getStringExtra("country");
                contact = getIntent().getStringExtra("contact");
                occupation = getIntent().getStringExtra("occupation");
                aboutMe = getIntent().getStringExtra("aboutMe");
                role = getIntent().getStringExtra("role");
                deviceId = getIntent().getStringExtra("deviceId");
                subscribe = getIntent().getStringExtra("subscribe");
                file = getIntent().getStringExtra("file");
                googleToken = getIntent().getStringExtra("googleToken");
            }
        }
    }

    public void callApiForGetCategoryData() {

        final ProgressDialog progressDialog = new ProgressDialog(Category.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        if (!Category.this.isFinishing())
            progressDialog.show();

        mAPIService.getCategoryData().enqueue(new Callback<ArrayList<GetCategory>>() {
            @Override
            public void onResponse(Call<ArrayList<GetCategory>> call, Response<ArrayList<GetCategory>> response) {
                if (response.isSuccessful()) {
                    Log.i("array-------", response.body().get(0).getCategoryName());
                    Data = response.body();
                    category.clear();
                    category.addAll(response.body());
                    for (int i = 0; i < Data.size(); i++) {
                        String name = response.body().get(i).getCategoryName();
                        String flag = response.body().get(i).getImgString();
                        String id = response.body().get(i).getId();
                        alpha.add(name + '~' + id + '~' + flag);
                    }

                    for (int y = 0; y < empNo.size(); y++) {
                        String categoryName = countryListName.get(y).split("~")[0];
                        String categoryId = countryListName.get(y).split("~")[2];
                        String value = String.valueOf(categoryName.charAt(0));

                        countryList.add(new SectionItem(categoryName, "", categoryId, categoryId, false));

                        for (int i = 0; i < Data.size(); i++) {
                            String name = alpha.get(i).split("~")[0];
                            String id = alpha.get(i).split("~")[1];
                            String flag = alpha.get(i).split("~")[2];
                            if (!list.contains(value)) {
                                list.add(value);
                                alphabetS.concat(value);//alphabetlist.append(value);
                                Log.i("array-------", String.valueOf(list));
                                alpha2.add(value);
                                alpha2.add(name);
                                //set Country Header (Like:-A,B,C,...)
                                //set Country Name
                                countryList.add(new EntryItem(name, flag, id, categoryId, false));
                            } else {
                                alpha2.add(name);
                                //set Country Name
                                countryList.add(new EntryItem(name, flag, id, categoryId, false));
                            }
                        }
                    }

                    String str = list.toString().replaceAll(",", "");
                    char[] chars = str.toCharArray();
                    Log.i(TAG, "post submitted to API." + chars);
                    char[] al = new char[27];
                    for (int j = 1, i = 0; j < chars.length; j = j + 2, i++) {
                        al[i] = chars[j];
                        Log.i(TAG, "post." + chars[j]);
                    }

                    Log.i(TAG, "post submitted to API." + al);

                    SideSelector ss = new SideSelector(getApplicationContext());
                    ss.setAlphabet(al);
                    alphabetlist = str.substring(1, str.length() - 1).replaceAll(" ", "").toCharArray();
                    bAdapter = new ArrayAdapter(Category.this, R.id.lvCategory, countryList, alpha2, list, chars);
                    // set adapter
                    // adapter

                    lvCountry.setAdapter(bAdapter);
                    lvCountry.setTextFilterEnabled(true);
                    if (sideSelector != null)
                        sideSelector.setListView(lvCountry);
                } else {
                    sessionManager.ShowDialog(Category.this, response.errorBody().source().toString().split("\"")[3]);
                }

                if (!Category.this.isFinishing())
                    progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ArrayList<GetCategory>> call, Throwable t) {
                if (!Category.this.isFinishing())
                    progressDialog.dismiss();
                sessionManager.ShowDialog(Category.this, "Server is down. Come back later!!");
                Log.i(TAG, "post submitted to API.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

        }
    }

    void populateMap(HashMap<String, ArrayList<String>> map, String value, String key) {
        ArrayList<String> myList;
        if (!map.containsKey(key)) {
            myList = new ArrayList<String>();
            myList.add(value);
            map.put(key, myList);
        } else {
            myList = map.get(key);
            myList.add(value);
        }
    }

    public interface Item {
        boolean isSection();

        String getTitle();

        String getFlag();

        String getId();

        String getcountryId();

        boolean getSelected();

        void setSelected(boolean isSelected);
    }

    /**
     * Section Item
     */
    public class SectionItem implements Item {
        private final String title;
        private final String flag;
        private final String id;
        private final String countryId;

        private boolean isSelected;

        public SectionItem(String title, String flag, String id, String countryId, boolean isSelected) {
            this.title = title;
            this.flag = flag;
            this.id = id;
            this.countryId = countryId;
            this.isSelected = isSelected;
        }

        public String getFlag() {
            return flag;
        }

        public String getId() {
            return id;
        }

        public String getcountryId() {
            return countryId;
        }

        @Override
        public boolean getSelected() {
            return isSelected;
        }

        @Override
        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public boolean isSection() {
            return true;
        }
    }


    /**
     * Entry Item
     */
    public class EntryItem implements Item {
        public final String title;
        private final String flag;
        private final String id;
        private final String countryId;
        private boolean isSelected;

        public EntryItem(String title, String flag, String id, String countryId, boolean isSelected) {
            this.title = title;
            this.flag = flag;
            this.id = id;
            this.countryId = countryId;
            this.isSelected = isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public String getTitle() {
            return title;
        }

        public String getFlag() {
            return flag;
        }

        public String getId() {
            return id;
        }

        public String getcountryId() {
            return countryId;
        }

        @Override
        public boolean getSelected() {
            return isSelected;
        }

        @Override
        public boolean isSection() {
            return false;
        }
    }


    public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentHolder> {

        private List<PendingHistory> history;

        public PaymentAdapter(List<PendingHistory> history) {
            this.history = history;
        }

        @Override
        public PaymentAdapter.PaymentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PaymentAdapter.PaymentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscription, parent, false));
        }

        @Override
        public void onBindViewHolder(PaymentAdapter.PaymentHolder holder, int position) {
            try {
                // setting country
                try {
                    PendingHistory ph = history.get(position);
                    if (!TextUtils.isEmpty(ph.getCountryId())) {
                        for (int i = 0; i < countryList.size(); i++) {
                            if (countryList.get(i).getId().equalsIgnoreCase(ph.getCountryId())) {
                                try {
                                    holder.ivCountry.setVisibility(View.GONE);
                                    holder.tvCountry.setText(countryList.get(i).getTitle());
                                    holder.tvCountry.setTypeface(holder.tvCountry.getTypeface(), Typeface.BOLD);
                                    holder.tvCountry.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    holder.tvCountry.setTextSize(16);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                holder.rvCategory.setAdapter(new CategoryAdapter(ph.getCategoryId()));
                                holder.rvCategory.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                break;
                            }
                        }

                        switch (ph.getSubscription()) {
                            case 1:
                                holder.tvSubscriptionType.setText("Free");
                                break;
                            case 2:
                                holder.tvSubscriptionType.setText("Monthly");
                                break;
                            case 3:
                                holder.tvSubscriptionType.setText("Yearly");
                                break;
                        }

                        holder.btnRenew.setVisibility(View.GONE);
                        holder.tvPlan.setText(planTitle);
                        holder.tvPlan.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                        holder.tvPlanTitle.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                        holder.llExpire.setVisibility(View.GONE);
                        holder.view.setVisibility(View.GONE);
                        holder.view1.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return history.size();
        }

        public class PaymentHolder extends RecyclerView.ViewHolder {

            private TextView tvCountry, tvSubscriptionType, tvPlanTitle, tvPlan;
            private ImageView ivCountry;
            private Button btnRenew;
            private RecyclerView rvCategory;
            private LinearLayout llExpire;
            private View view, view1;

            public PaymentHolder(View itemView) {
                super(itemView);
                tvCountry = itemView.findViewById(R.id.tv_country);
                tvPlanTitle = itemView.findViewById(R.id.tv_plan_title);
                tvSubscriptionType = itemView.findViewById(R.id.tv_subscription_type);
                ivCountry = itemView.findViewById(R.id.iv_country);
                btnRenew = itemView.findViewById(R.id.btn_renew);
                rvCategory = itemView.findViewById(R.id.rv_subscription_category);
                tvPlan = itemView.findViewById(R.id.tv_plan);
                llExpire = itemView.findViewById(R.id.ll_expire);
                view = itemView.findViewById(R.id.view);
                view1 = itemView.findViewById(R.id.view1);
            }
        }
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

        List<String> categorys;

        public CategoryAdapter(List<String> list) {
            this.categorys = list;
        }

        @Override
        public CategoryAdapter.CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CategoryAdapter.CategoryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscription_category, parent, false));
        }

        @Override
        public void onBindViewHolder(CategoryAdapter.CategoryHolder holder, int position) {
            try {
                for (int i = 0; i < category.size(); i++) {
                    if (category.get(i).getId().equalsIgnoreCase(categorys.get(position))) {
                        Bitmap bitmapCat = StringToBitMap(category.get(i).getImgString());
                        holder.ivCategory.setImageBitmap(bitmapCat);
                        holder.tvCategory.setText(category.get(i).getCategoryName());
                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return categorys.size();
        }

        public class CategoryHolder extends RecyclerView.ViewHolder {
            private TextView tvCategory;
            private ImageView ivCategory;

            public CategoryHolder(View itemView) {
                super(itemView);
                tvCategory = itemView.findViewById(R.id.tv_category);
                ivCategory = itemView.findViewById(R.id.iv_category);
            }
        }
    }

    public class PendingHistory {
        String countryId, planId, status;
        int subscription;
        List<String> categoryId;

        public String getCountryId() {
            return countryId;
        }

        public void setCountryId(String countryId) {
            this.countryId = countryId;
        }

        public String getPlanId() {
            return planId;
        }

        public void setPlanId(String planId) {
            this.planId = planId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getSubscription() {
            return subscription;
        }

        public void setSubscription(int subscription) {
            this.subscription = subscription;
        }

        public List<String> getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(List<String> categoryId) {
            this.categoryId = categoryId;
        }
    }

    public class CheckServiceModel {
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("status")
        @Expose
        private Boolean status;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(Category.this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!Category.this.isFinishing() && !mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }
}
