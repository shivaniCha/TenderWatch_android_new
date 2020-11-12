package com.tenderWatch;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tenderWatch.Drawer.MainDrawer;
import com.tenderWatch.Models.PesapalPaymentRESP;
import com.tenderWatch.Models.SubscriptionList;
import com.tenderWatch.Models.User;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;

public class PesapalListActivity extends AppCompatActivity {

    private static final String TAG = "PesapalListActivity";

    LinearLayout mLlPbLoader, llPaymentSucess, mLlToolbar;
    WebView mWebViewPesaPal;
    RecyclerView mRvPesapalList;

    private HashMap<String, ArrayList<String>> selection;
    private double amount = 0;
    private int subscriptionType = 1;
    private PesapalAdapter pesapalAdapter;
    private Api mApiService;
    private ProgressDialog progressDialog;
    private User user;
    private boolean isTrial = true;
    private boolean listOfPayment = false;
    SessionManager sessionManager;
    private boolean fromRegister;
    private String planType = "";
    private String paymentMethod = "";
    private String subscriptionId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesapal_list);
        sessionManager = new SessionManager(PesapalListActivity.this);
        initViews();

        if (!(sessionManager.getPreferencesObject(PesapalListActivity.this)).getIsPayment()) {
            mLlToolbar.setVisibility(View.INVISIBLE);
        }

        getDataFromIntent();
    }

    public void getDataFromIntent() {
        if (getIntent() != null && getIntent().getExtras() != null) {

            try {
                planType = getIntent().getStringExtra("planType");
                subscriptionId=getIntent().getStringExtra("id");
                paymentMethod = getIntent().getStringExtra("paymentMethod");
                if (getIntent().getBooleanExtra("isFromSubscription", false)) {
                    double amount = getIntent().getDoubleExtra("amount", 0);
                    String pesapalInfo = getIntent().getStringExtra("pesapalInfo");
//                    JSONObject pesapalInfo=getIntent().getParcelableExtra("pesapalInfo");
                    Log.e(TAG, "getDataFromIntentttttt: " + pesapalInfo);
                    if (paymentMethod.equals("pesapal"))
                        callApi(amount, new JSONObject(pesapalInfo));
                    else
                        callPayPalAPI(amount, new JSONObject(pesapalInfo));
                } /*else if(getIntent().hasExtra("isFromRegister") && !getIntent().getBooleanExtra("isFromRegister",true)){
                    int amount = getIntent().getIntExtra("amount", 0);
                    String pesapalInfo = getIntent().getStringExtra("pesapalInfo");
//                    JSONObject pesapalInfo=getIntent().getParcelableExtra("pesapalInfo");
                    Log.e(TAG, "getDataFromIntent: " + pesapalInfo);
                    callApi(amount, new JSONObject(pesapalInfo));
                }*/ else {
                    if (getIntent().getExtras().getSerializable("selections") != null) {
                        selection = (HashMap<String, ArrayList<String>>) getIntent().getExtras().getSerializable("selections");
                    }
                    amount = getIntent().getExtras().getDouble("amount", 0);
                    subscriptionType = getIntent().getExtras().getInt("subscriptionType", 1);
                    isTrial = getIntent().getExtras().getBoolean("isTrial", true);
                    listOfPayment = getIntent().getExtras().getBoolean("listOfPayment", false);

                    if (!listOfPayment)
                        callPesapalURL();
                    else
                        callApiToGetPesapalPaymentDetails();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    public void initViews() {

        mApiService = ApiUtils.getAPIService();
        progressDialog = new ProgressDialog(PesapalListActivity.this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        mWebViewPesaPal = (WebView) findViewById(R.id.webview_pesapal);
        mLlPbLoader = (LinearLayout) findViewById(R.id.ll_pb_loader);
//        llToolbar = (LinearLayout) findViewById(R.id.ll_toolbar);
        llPaymentSucess = (LinearLayout) findViewById(R.id.ll_payment_success);
        mRvPesapalList = (RecyclerView) findViewById(R.id.rv_pesapal_list);
        mLlToolbar = (LinearLayout) findViewById(R.id.ll_toolbar);
        mWebViewPesaPal.setVisibility(View.GONE);
        mLlPbLoader.setVisibility(View.GONE);
        llPaymentSucess.setVisibility(View.GONE);
        mRvPesapalList.setVisibility(View.GONE);

        mLlToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PesapalListActivity.this, MainDrawer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    public void initRecyclerView() {
        mRvPesapalList.setVisibility(View.VISIBLE);
        mWebViewPesaPal.setVisibility(View.GONE);
        mLlPbLoader.setVisibility(View.GONE);
        llPaymentSucess.setVisibility(View.GONE);

        DividerItemDecoration itemDecor = new DividerItemDecoration(PesapalListActivity.this, DividerItemDecoration.VERTICAL);
        mRvPesapalList.addItemDecoration(itemDecor);

        pesapalAdapter = new PesapalAdapter(PesapalListActivity.this, user.getPesapalDetails());
        mRvPesapalList.setAdapter(pesapalAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PesapalListActivity.this);
        mRvPesapalList.setLayoutManager(layoutManager);
        mRvPesapalList.setHasFixedSize(true);

        if (!PesapalListActivity.this.isFinishing())
            progressDialog.dismiss();
    }

    public void callPesapalURL() {
        // List<String> values = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        try {
            if (selection != null && selection.size() > 0) {
                for (String currentKey : selection.keySet()) {
                    //values = selection.get(currentKey);
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < selection.get(currentKey).size(); i++) {
                        array.put(selection.get(currentKey).get(i));
                    }
                    jsonObject.put(currentKey, array);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("planType", planType);
            jObj.put("selections", jsonObject);
            jObj.put("subscriptionPackage", subscriptionType + "");
            jObj.put("register", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        mLlPbLoader.setVisibility(View.VISIBLE);
        Log.e(TAG, "callPesapalURL: " + jObj);

        if (paymentMethod.equals("pesapal"))
            callApi(amount, jObj);
        else
            callPayPalAPI(amount, jObj);

    }

    private void callApi(double amount, JSONObject jObj) {
        if (progressDialog != null)
            progressDialog.show();
        final String token = "Bearer " + sessionManager.getPreferences(PesapalListActivity.this, "token");
        mApiService.getPesaPalURL(token, "PesaPal Payment", amount, sessionManager.getPreferencesObject(PesapalListActivity.this).getEmail(), jObj).enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
//                    mLlPbLoader.setVisibility(View.GONE);
                    String responseString = "";
                    if (response.body() != null)
                        responseString = response.body().string();
                    if (!TextUtils.isEmpty(responseString)) {
                        if (!PesapalListActivity.this.isFinishing())
                            progressDialog.show();
                        mWebViewPesaPal.setVisibility(View.VISIBLE);
                        try {
                            JSONObject jObj = new JSONObject(responseString);
                            final String url = jObj.optString("URL");

                            mWebViewPesaPal.setWebViewClient(new WebViewClient() {

                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    Log.e(TAG, "onPageFinished: " + url);
                                    if (url.contains("pesapal_transaction_tracking_id=")) {
                                        mWebViewPesaPal.setVisibility(View.GONE);
//                                        llPaymentSucess.setVisibility(View.VISIBLE);
                                        callApiToGetPesapalPaymentDetails();

                                    }
                                    super.onPageFinished(view, url);
                                    if (!PesapalListActivity.this.isFinishing())
                                        progressDialog.dismiss();
                                }

                                @Override
                                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                    super.onPageStarted(view, url, favicon);
                                }

                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    return super.shouldOverrideUrlLoading(view, url);
                                }
                            });

                            mWebViewPesaPal.getSettings().setJavaScriptEnabled(true);
                            mWebViewPesaPal.loadUrl(url);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                mLlPbLoader.setVisibility(View.GONE);
                if (!PesapalListActivity.this.isFinishing())
                    progressDialog.dismiss();
                Toast.makeText(PesapalListActivity.this, "Something Wrong : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void callPayPalAPI(double amount, JSONObject jObj) {
//        Toast.makeText(this, "Check CallpaypalApi", Toast.LENGTH_SHORT).show();
        if (progressDialog != null)
            progressDialog.show();
        Log.e("email: ",""+sessionManager.getPreferencesObject(PesapalListActivity.this).getEmail() );
        final String token = "Bearer " + sessionManager.getPreferences(PesapalListActivity.this, "token");
        Log.e(TAG, "callPayPalAPI: "+amount );
        Log.e(TAG, "callPayPalAPI: "+token );
        Log.e(TAG, "callPayPalAPI: "+sessionManager.getPreferencesObject(PesapalListActivity.this).getEmail() );
        Log.e(TAG, "paypalInfo: "+jObj.toString());
        mApiService.getPayPalURL(token, "PayPal Payment", amount, sessionManager.getPreferencesObject(PesapalListActivity.this).getEmail(), jObj).enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
//                    Toast.makeText(PesapalListActivity.this, "Hello", Toast.LENGTH_SHORT).show();
//                    mLlPbLoader.setVisibility(View.GONE);
                    String responseString = "{\"URL\":\"https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=EC-01J31394FB9797252\"}";
                    if (response.body() != null)
                        responseString = response.body().string();
                    if (!TextUtils.isEmpty(responseString)) {
                        if (!PesapalListActivity.this.isFinishing())
                            progressDialog.dismiss();
//                    Toast.makeText(PesapalListActivity.this, "Hello Stop Wait", Toast.LENGTH_SHORT).show();
                        mWebViewPesaPal.setVisibility(View.VISIBLE);
                        try {
//                            JSONArray jsonArray=new JSONArray(responseString);
//                            Log.e(TAG, "onResponseJsonArray: "+jsonArray.toString() );
                            JSONObject jObj = new JSONObject(String.valueOf(responseString));
                            Log.e(TAG, "onResponse: "+jObj);
                            final String url =jObj.optString("URL");
                            Log.e(TAG, "onResponseJSON: "+jObj.optString("URL"));

                            mWebViewPesaPal.setWebViewClient(new WebViewClient() {

                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    Log.e(TAG, "onPageFinished: " + url);

                                    if (url.contains("paymentId=") && url.contains("PayerID=")) {
                                        try {
                                            if (!PesapalListActivity.this.isFinishing())
                                                progressDialog.show();
                                            Uri uri = Uri.parse(url);
                                            String paymentId = uri.getQueryParameter("paymentId");
                                            String tokenId = uri.getQueryParameter("token");
                                            String payerID = uri.getQueryParameter("PayerID");

                                            Log.e(TAG, "paymentId: "+paymentId );
                                            Log.e(TAG, "tokenId: "+tokenId );
                                            Log.e(TAG, "payerID: "+payerID );
                                            mApiService.executePaypalPayment(token, paymentId, tokenId, payerID).enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    Log.e(TAG, "onResponse: PAYPAL TRANSACTION SUCCESS" );
                                                    if (response.isSuccessful()) {
                                                        mWebViewPesaPal.setVisibility(View.GONE);

                                                        callApiToGetPesapalPaymentDetails();
                                                    }
                                                    if (!PesapalListActivity.this.isFinishing())
                                                        progressDialog.dismiss();
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Log.e(TAG, "onFailure: PAYPAL EXECUTE FAIL : "+t.getMessage()+" : "+call.request().url() );
                                                    if (!PesapalListActivity.this.isFinishing())
                                                        progressDialog.dismiss();
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    } else if (!PesapalListActivity.this.isFinishing())
                                        progressDialog.dismiss();

                                    super.onPageFinished(view, url);
                                }

                                @Override
                                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                    super.onPageStarted(view, url, favicon);
                                }

                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    return super.shouldOverrideUrlLoading(view, url);
                                }
                            });

                            mWebViewPesaPal.getSettings().setJavaScriptEnabled(true);
                            mWebViewPesaPal.loadUrl(url);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                mLlPbLoader.setVisibility(View.GONE);
                if (!PesapalListActivity.this.isFinishing())
                    progressDialog.dismiss();
                Toast.makeText(PesapalListActivity.this, "Something Wrong : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void callApiToGetPesapalPaymentDetails() {
        if (!PesapalListActivity.this.isFinishing())
            progressDialog.show();

        mLlToolbar.performClick();

        String token = "Bearer " + sessionManager.getPreferences(PesapalListActivity.this, "token");
        String userId = (sessionManager.getPreferencesObject(PesapalListActivity.this)).getId();


        Log.e(TAG, "callApiToGetPesapalPaymentDetails token: "+token );
        Log.e(TAG, "callApiToGetPesapalPaymentDetails userId: "+userId );
        mApiService.getUserDetail(token, userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
               // initRecyclerView();
                sessionManager.setPreferencesObject(PesapalListActivity.this,user);
                callApiTogetRenewPlan();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (!PesapalListActivity.this.isFinishing())
                    progressDialog.dismiss();
            }
        });
    }

    public void callApiTogetRenewPlan() {
        if (!PesapalListActivity.this.isFinishing())
            progressDialog.show();
        mLlToolbar.performClick();
        String token = "Bearer " + sessionManager.getPreferences(PesapalListActivity.this, "token");
        Log.e("token: ",token );

        JsonObject gsonObject = new JsonObject();
        try {
            //contractorEmailList.add("tenderwatch01@gmail.com");
            JSONObject jsonObj = new JSONObject();
            if(subscriptionId!=null) {
                jsonObj.put("_id", subscriptionId);
            }
            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(jsonObj.toString());

            //print parameter
            Log.e("MY gson.JSON:  ", "AS PARAMETER Remove Subscription :" + gsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mApiService.removeSubscription(token,gsonObject).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
               // pesapalAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
                if(response.isSuccessful())
                {
                    Log.e("callApiTogetRenewPlan: ", ""+response.isSuccessful());
                    initRecyclerView();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!PesapalListActivity.this.isFinishing())
                    progressDialog.dismiss();
            }
        });
    }

    public class PesapalAdapter extends RecyclerView.Adapter<PesapalAdapter.PesapalHolder> {

        private Context context;
        private List<User.PesapalDetail> pesapalPaymentList;

        public PesapalAdapter(Context context, List<User.PesapalDetail> paypalPaymentList) {
            this.context = context;
            this.pesapalPaymentList = paypalPaymentList;
        }

        @Override
        public PesapalHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pesapal_payment, parent, false);
            PesapalHolder holder = new PesapalHolder(view);
            return holder;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(PesapalHolder holder, int position) {
            final User.PesapalDetail pesapalDetail = pesapalPaymentList.get(position);
            /*if (!TextUtils.isEmpty(pesapalDetail.getPayment())) {
                holder.mTvPesapalPayment.setText("Pesapal payment " + pesapalDetail.getPayment());
            } else {
                holder.mTvPesapalPayment.setText(context.getResources().getString(R.string.pesapal_payment_initiated));
            }*/
            holder.mTvReferenceNo.setText("Reference no : " + pesapalDetail.getReference());

            long date = Long.parseLong(pesapalDetail.getReference());
            if (date > 0) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(date);

                int mYear = cal.get(Calendar.YEAR);
                int mMonth = cal.get(Calendar.MONTH);
                int mDay = cal.get(Calendar.DAY_OF_MONTH);
                int mHour = cal.get(Calendar.HOUR_OF_DAY);
                int mMinute = cal.get(Calendar.MINUTE);

                String dateToFormat = mYear + "-" + (mMonth + 1) + "-" + mDay + " " + mHour + ":" + mMinute;
                holder.mTvPaymentTime.setText("Initiated at : " + formatDate(dateToFormat));
            }

            if (!TextUtils.isEmpty(pesapalDetail.getPesapalStatus())) {
                holder.mTvPaymentStatus.setText("Status : " + pesapalDetail.getPesapalStatus());
            } else {
                holder.mTvPaymentStatus.setText("Status : ");
            }

            holder.mIvRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callApiToGetStatusOfPayment(pesapalDetail);
                }
            });
        }

        @Override
        public int getItemCount() {
            return pesapalPaymentList.size();
        }

        public class PesapalHolder extends RecyclerView.ViewHolder {

            ImageView mIvRefresh;
            TextView mTvReferenceNo;
            TextView mTvPaymentTime;
            TextView mTvPaymentStatus;
//            TextView mTvPesapalPayment;

            public PesapalHolder(View itemView) {
                super(itemView);
                mIvRefresh = itemView.findViewById(R.id.ivPaymentRefresh);
                mTvReferenceNo = itemView.findViewById(R.id.tvReferenceNo);
                mTvPaymentTime = itemView.findViewById(R.id.tvPaymentTime);
                mTvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
//                mTvPesapalPayment = itemView.findViewById(R.id.tvPesapalPayment);
            }
        }
    }

    public String formatDate(String yourDateAsString) {
        String yourFormatedDateString = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            Date date = sdf.parse(yourDateAsString);

            if (android.text.format.DateFormat.is24HourFormat(PesapalListActivity.this))
                sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            else
                sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
            yourFormatedDateString = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return yourFormatedDateString;
    }

    public void callApiToGetStatusOfPayment(final User.PesapalDetail pesapalDetail) {
        if (!PesapalListActivity.this.isFinishing())
            progressDialog.show();
        String token = "Bearer " + sessionManager.getPreferences(PesapalListActivity.this, "token");

        mApiService = ApiUtils.getAPIService();
        mApiService.getPesaPalPaymentDetails(token, pesapalDetail.getReference()).enqueue(new Callback<PesapalPaymentRESP>() {
            @Override
            public void onResponse(Call<PesapalPaymentRESP> call, Response<PesapalPaymentRESP> response) {
                Log.e(TAG, "onResponse: " + response);
                if (response.body() != null) {
                    pesapalAdapter = new PesapalAdapter(PesapalListActivity.this, response.body().getNewUser().getPesapalDetails());
                    mRvPesapalList.setAdapter(pesapalAdapter);

                    if (!TextUtils.isEmpty(response.body().getServiceDetails().getPesapalStatus()) &&
                            response.body().getServiceDetails().getPesapalStatus().equalsIgnoreCase("COMPLETED")) {
                        PesapalPaymentRESP.NewUser newUser = response.body().getNewUser();
                        User user = new User();
                        user.setAboutMe(newUser.getAboutMe());
                        user.setActive(newUser.getIsActive());
                        user.setAndroidDeviceId(newUser.getAndroidDeviceId());
                        user.setContactNo(newUser.getContactNo());
                        user.setCountry(newUser.getCountry());
                        user.setCreatedAt(newUser.getCreatedAt());
                        user.setDeviceId(newUser.getDeviceId());
                        user.setEmail(newUser.getEmail());
                        user.setFirstName(newUser.getFirstName());
                        user.setId(newUser.getId());
                        user.setInvoiceURL(newUser.getInvoiceURL());
                        user.setIsActive(newUser.getIsActive());
                        user.setIsPayment(newUser.getIsPayment());
                        user.setLastName(newUser.getLastName());
                        user.setOccupation(newUser.getOccupation());
                        user.setPayment(newUser.getPayment());
                        user.setPayment(newUser.getIsPayment());
                        user.setPesapalDetails(newUser.getPesapalDetails());
                        user.setProfilePhoto(newUser.getProfilePhoto());
                        user.setRole(newUser.getRole());
//                        user.setStripeDetails(newUser.getStripeDetails());
                        user.setSubscribe(newUser.getSubscribe());


                        sessionManager.setPreferencesObject(PesapalListActivity.this, user);
                        callCompletedPaymentWebService(response.body().getServiceDetails().getSelections(), response.body().getServiceDetails().getAmount(), response.body().getServiceDetails().getSubscriptionPackage(), pesapalDetail);
                        if (!isTrial) {
                            Intent intent = new Intent(PesapalListActivity.this, MainDrawer.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                    if (!PesapalListActivity.this.isFinishing())
                        progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<PesapalPaymentRESP> call, Throwable t) {
                Log.e(TAG, "onFailure: ");
                if (!PesapalListActivity.this.isFinishing())
                    progressDialog.dismiss();
            }
        });
    }

    public void callCompletedPaymentWebService(HashMap<String, ArrayList<String>> selections, int payment, int subscribe, User.PesapalDetail pesapalDetail) {
        Log.e(TAG, "callCompletedPaymentWebService: Selections : " + selections);

        JSONObject jsonObject = new JSONObject();
        try {
            if (selections != null && selections.size() > 0) {
                for (String currentKey : selections.keySet()) {
                    //values = selection.get(currentKey);
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < selections.get(currentKey).size(); i++) {
                        array.put(selections.get(currentKey).get(i));
                    }
                    jsonObject.put(currentKey, array);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jObj = new JSONObject();

        try {
            jObj.put("selections", jsonObject);
            jObj.put("subscriptionPackage", subscribe + "");
            jObj.put("payment", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UpdatePaymentDetails paymentDetails = new UpdatePaymentDetails();
        paymentDetails.setSelections(selections);
        paymentDetails.setPayment(payment);
        paymentDetails.setSubscribe(String.valueOf(subscribe));

        Log.e(TAG, "callCompletedPaymentWebService: " + jsonObject +
                " Pesapal Selection : " + pesapalDetail.getSelections());

        String token = "Bearer " + sessionManager.getPreferences(PesapalListActivity.this, "token");

        mApiService = ApiUtils.getAPIService();
        mApiService.updatePesaPalPaymentDetails(token, paymentDetails).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e(TAG, "onResponse: " + response + " : Message " + response.message() + " : Error Body : " + response.errorBody());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });
    }

    public class UpdatePaymentDetails {
        public HashMap<String, ArrayList<String>> selections;
        public int payment;
        public String subscribe;

        public UpdatePaymentDetails() {
        }

        public HashMap<String, ArrayList<String>> getSelections() {
            return selections;
        }

        public void setSelections(HashMap<String, ArrayList<String>> selections) {
            this.selections = selections;
        }

        public int getPayment() {
            return payment;
        }

        public void setPayment(int payment) {
            this.payment = payment;
        }

        public String getSubscribe() {
            return subscribe;
        }

        public void setSubscribe(String subscribe) {
            this.subscribe = subscribe;
        }
    }
}
