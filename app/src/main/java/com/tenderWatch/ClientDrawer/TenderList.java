package com.tenderWatch.ClientDrawer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.tenderWatch.Adapters.ContractorTenderListAdapter;
import com.tenderWatch.Adapters.TenderListAdapter;
import com.tenderWatch.BuildConfig;
import com.tenderWatch.ContractotTenderDetail;
import com.tenderWatch.Drawer.MainDrawer;
import com.tenderWatch.EditTenderDetail;
import com.tenderWatch.InterestedContractorClass;
import com.tenderWatch.Models.AllContractorTender;
import com.tenderWatch.Models.GetCategory;
import com.tenderWatch.Models.Tender;
import com.tenderWatch.Models.TenderUploader;
import com.tenderWatch.Models.UpdateTender;
import com.tenderWatch.Models.User;
import com.tenderWatch.PreviewTenderDetail;
import com.tenderWatch.R;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tenderWatch.Constants.API_FAVOURITE_TENDER;
import static com.tenderWatch.Constants.API_GET_TENDER;
import static com.tenderWatch.Constants.API_REMOVE_TENDER;
import static com.tenderWatch.Constants.AUTHORIZATION;
import static com.tenderWatch.Constants.GLOBAL_TAG;
import static com.tenderWatch.Constants.PARAM_TENDER_DETAILID;
import static com.tenderWatch.Constants.PARAM_TENDER_ID;
import static com.tenderWatch.Retrofit.ApiUtils.BASE_URL;
import static com.tenderWatch.Utility.getOkHttpClient;

/**
 * Created on 14/12/17.
 */

public class TenderList extends Fragment {
    Api mAPIService;
    List<String> categoryList;
    String clientCategory,clientCountry,tenderName;
//    List<String> interestedContractors;
    JSONArray interestedContractorArray;
    SessionManager sessionManager;
    private RelativeLayout ll_search;
    private ImageView ivClearSearch;
    private static final String TAG = TenderList.class.getSimpleName();
    private TextView tvDate;
    Intent intent;
    ArrayList<Tender> allTender = new ArrayList<Tender>();
    TenderListAdapter adapter;
    ContractorTenderListAdapter Con_adapter;
    ListView list_tender;
    TextView tvNoTender;
    ArrayList<AllContractorTender> lstContractorTender = new ArrayList<AllContractorTender>();
    Tender tender;
    AllContractorTender contractorTender;
    String role;
    private ProgressDialog mProgressDialog;
    User user;
    int tendePosition;
    private EditText edtSearch;
    private String targetViewers = "";
    private SwipeRefreshLayout srlTender;
    final String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    String token;
    Context mContext;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAPIService = ApiUtils.getAPIService();
//        interestedContractors=new ArrayList<>();
        categoryList=new ArrayList<>();
        sessionManager = new SessionManager(getActivity());
        role = sessionManager.getPreferences(getActivity(), "role");
        Log.d("debug",role);
        user = sessionManager.getPreferencesObject(getActivity());
        Log.d("debug",user.getRole()+"");
        edtSearch = view.findViewById(R.id.edtSearch);
        ll_search = view.findViewById(R.id.ll_search);
        ivClearSearch = view.findViewById(R.id.iv_clear_search);
        tvDate = view.findViewById(R.id.tv_date);
        srlTender = view.findViewById(R.id.srl_tender);
        list_tender = view.findViewById(R.id.list_tender);
        tvNoTender = view.findViewById(R.id.tv_no_tender);
        token = "Bearer " + sessionManager.getPreferences(getActivity(), "token");
        Log.e(TAG, "GetAllFavoriteToken: "+token );
        try {
            SimpleDateFormat timeStampFormat = new SimpleDateFormat("EEEE MMM dd yyyy");
            Date myDate = new Date();
            String date = timeStampFormat.format(myDate);
            tvDate.setText("Date: " + date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);




        try {
            if(getActivity() instanceof MainDrawer) {
                ((MainDrawer) getActivity()).setOptionMenu(false, false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        srlTender.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                if (!TextUtils.isEmpty(role) && role.equals("client")) {
//                    GetAllTender();
//                } else {
//                    Bundle bundle = getArguments();
//                    if (bundle != null) {
//                        String value = bundle.getString("nav_fav");
//                        if (value != null) {
//                            GetAllFavorite();
//                            ll_search.setVisibility(View.GONE);
//                            tvDate.setVisibility(View.GONE);
//                        }
//                    } else {
//                        AllContractorTender();
//                    }
//                }

                getAllTenderListOrContractTender();
            }
        });


        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Fragment fragment2 = new Home();
                FragmentManager fragmentManager = getFragmentManager();
                final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment2);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        //String role=sp.getPreferences(getActivity(),"role");
        if (!TextUtils.isEmpty(role) && role.equals("client")) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }
        list_tender.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (role.equals("client")) {
                    ShowBox(position);
                } else {
                    Bundle bundle = getArguments();
                    if (bundle != null) {
                        String value = bundle.getString("nav_fav");
                        if (value != null) {
                            ShowBoxFavorite(position);
                        }
                    } else {
                        ShowBoxForContractor(position);
                    }
                }
                return true;
            }
        });
        ivClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtSearch.setText("");
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(role) && role.equals("client") && adapter != null) {
                    adapter.getFilter().filter(charSequence.toString());
                } else {
                    Bundle bundle = getArguments();
                    if (bundle != null) {
                        String value = bundle.getString("nav_fav");
                        if (value != null) {
                            //GetAllFavorite();
                        }
                    } else {
                        if (!TextUtils.isEmpty(charSequence))
                            Con_adapter.getFilter().filter(charSequence.toString());
                    }
                }
                if (charSequence.length() == 0) {
                    ivClearSearch.setVisibility(View.GONE);
                } else {
                    ivClearSearch.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        list_tender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (role != null) {
                    if (role.equals("client")) {
                        if (adapter != null) {
                            tender = adapter.getTender().get(position);

                            Gson gson = new Gson();
                            String jsonString = gson.toJson(tender);
//                            Toast.makeText(getActivity(), "list", Toast.LENGTH_SHORT).show();
//                            Toast.makeText(getActivity(), jsonString+"", Toast.LENGTH_SHORT).show();
                            Log.d("debug",jsonString);
                            Date startDateValue = null, endDateValue = null;
                            try {
                                // startDateValue = new SimpleDateFormat("yyyy-MM-dd").parse(formattedDate);
                                startDateValue = new SimpleDateFormat("yyyy-MM-dd").parse(tender.getCreatedAt().split("T")[0]);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            try {
                                endDateValue = new SimpleDateFormat("yyyy-MM-dd").parse(tender.getExpiryDate().split("T")[0]);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            //Date endDateValue = new Date(allTender.get(position).getExpiryDate().split("T")[0]);
                            long diff = endDateValue.getTime() - startDateValue.getTime();
                            long seconds = diff / 1000;
                            long minutes = seconds / 60;
                            long hours = minutes / 60;
                            long days = (hours / 24);

                            if (days == 0) {
                                sessionManager.ShowDialog(getActivity(), "Tender is not Activated.");
                            } else {
                                Intent intent = new Intent(getActivity(), PreviewTenderDetail.class);
                                intent.putExtra("data", jsonString);
                                startActivity(intent);
                            }
                        }
                    } else {
                        contractorTender = Con_adapter.getList().get(position);
                        tendePosition = position;
                        String id2 = contractorTender.getId();
                        mAPIService.getTender(token, id2).enqueue(new Callback<UpdateTender>() {
                            @Override
                            public void onResponse(Call<UpdateTender> call, Response<UpdateTender> response) {
                                Log.i(TAG, "post submitted to API." + response.body());
                                targetViewers = new Gson().toJson(response.body());
                                Log.e("photo", contractorTender.getTenderPhoto());
                                if (contractorTender.getTenderPhoto().equalsIgnoreCase("no image") || contractorTender.getTenderPhoto().isEmpty()) {
                                    Log.e("photo", "no image is there");
                                    TenderUploader client = contractorTender.getTenderUploader();
                                    Log.i(TAG, "post submitted to API." + client);
                                    Gson gson = new Gson();
                                    String jsonString = gson.toJson(contractorTender);
                                    String sender = gson.toJson(client);
                                    Intent intent = new Intent(getActivity(), ContractotTenderDetail.class);
                                    intent.putExtra("data", jsonString);
                                    intent.putExtra("sender", sender);
                                    intent.putExtra("targetViewers", targetViewers);
                                    if (lstContractorTender.get(position).getAmendRead() != null) {
                                        if (lstContractorTender.get(position).getAmendRead().size() > 0) {
                                            for (int i = 0; i < lstContractorTender.get(position).getAmendRead().size(); i++) {
                                                String Con_id = sessionManager.getPreferencesObject(getActivity()).getId();
                                                if (!lstContractorTender.get(position).getAmendRead().contains(Con_id)) {
                                                    intent.putExtra("amended", "true");
                                                }
                                            }
                                        }
                                        if (lstContractorTender.get(position).getAmendRead().size() == 0) {
                                            intent.putExtra("amended", "true");
                                        }
                                    }
                                    startActivity(intent);

                                } else {
                                    downloaImage();
                                }

                            }

                            @Override
                            public void onFailure(Call<UpdateTender> call, Throwable t) {
                                Log.i(TAG, "post submitted to API." + t);
                            }
                        });


                    }
                }
            }
        });
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitleColor(getActivity().getResources().getColor(R.color.colorWhite));

        getActivity().setTitle("TenderWatch");

    }

    private void ShowBoxFavorite(final int i) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        alertDialog.setTitle("Tender Watch");

        alertDialog.setMessage("Are you sure you want to remove favorite Tender?");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Remove from Favorites", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                showProgressDialog();
                contractorTender = lstContractorTender.get(i);
                String tenderid = contractorTender.getId();
                mAPIService.removeFavorite(token, tenderid).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dismissProgressDialog();
                        Log.i(TAG, "post submitted to API." + response.body());
                        GetAllFavorite();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        dismissProgressDialog();
                        Log.i(TAG, "post submitted to API." + t);
                    }
                });
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void GetAllFavorite() {
        showProgressDialog();


//        AndroidNetworking.get(BASE_URL + API_GET_TENDER)
//                .addHeaders(AUTHORIZATION, token) // posting java object
//                .setTag(GLOBAL_TAG)
//                .setPriority(Priority.LOW)
//                .setOkHttpClient(getOkHttpClient())
//                .build()
//                .getAsJSONArray(new JSONArrayRequestListener() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        // do anything with response
//                        try {
//                            dismissProgressDialog();
//                            lstContractorTender=new ArrayList<>();
//                            srlTender.setRefreshing(false);
//                            Gson gson = new Gson();
//                            for (int i = 0; i < response.length(); i++) {
//                                JSONObject obj = response.getJSONObject(i);
//                                AllContractorTender tender = gson.fromJson(obj.toString(), AllContractorTender.class);
//                                lstContractorTender.add(tender);
//                            }
//
//
//                            if(lstContractorTender.size()>0){
//
//                                list_tender.setVisibility(View.VISIBLE);
//                                tvNoTender.setVisibility(View.GONE);
//                                Con_adapter = new ContractorTenderListAdapter(getActivity(), lstContractorTender);
//                                list_tender.setAdapter(Con_adapter);
//                                Con_adapter.notifyDataSetChanged();
//                            }else{
//                                list_tender.setVisibility(View.GONE);
//                                tvNoTender.setText(getResources().getText(R.string.no_tender_client));
//                                tvNoTender.setVisibility(View.VISIBLE);
//                            }
//
//                            Log.e("getAllTender", "response in new Network Library "+allTender.size());
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(ANError error) {
//                        // handle error
//
//                        dismissProgressDialog();
//                        list_tender.setAdapter(null);
//                        tvNoTender.setVisibility(View.VISIBLE);
//                        tvNoTender.setText(getResources().getString(R.string.no_favorite_tender));
//                    }
//                });

        Log.e(TAG, "GetAllFavoriteToken: "+token );
        mAPIService.getAllFavoriteTender(token).enqueue(new Callback<ArrayList<AllContractorTender>>() {
            @Override
            public void onResponse(Call<ArrayList<AllContractorTender>> call, Response<ArrayList<AllContractorTender>> response) {
                Log.i(TAG, "post submitted to API." + response.body());
                dismissProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        lstContractorTender = response.body();

                        if (!lstContractorTender.isEmpty() && lstContractorTender.size() > 0) {
                            list_tender.setVisibility(View.VISIBLE);
                            tvNoTender.setVisibility(View.GONE);
                            Con_adapter = new ContractorTenderListAdapter(getActivity(), lstContractorTender);
                            list_tender.setAdapter(Con_adapter);
                        } else {
                            tvNoTender.setVisibility(View.VISIBLE);
                            tvNoTender.setText(getResources().getString(R.string.no_favorite_tender));
                        }
                    }

                } else {
                    list_tender.setVisibility(View.GONE);
                    tvNoTender.setVisibility(View.VISIBLE);
                    tvNoTender.setText("Your subscription has expired. Please renew your subscription to access Tenders");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AllContractorTender>> call, Throwable t) {
                dismissProgressDialog();
                list_tender.setAdapter(null);
                tvNoTender.setVisibility(View.VISIBLE);
                tvNoTender.setText(getResources().getString(R.string.no_favorite_tender));
            }
        });
    }

    private void AllContractorTender() {
        srlTender.setRefreshing(true);
        Log.e(TAG, "AllContractorTenderToken: "+token );
        Log.e("tokennn", "onResponse2: "+token );
        AndroidNetworking.post(BASE_URL + API_GET_TENDER)
                .addHeaders(AUTHORIZATION, token) // posting java object
                .setTag(GLOBAL_TAG)
                .setPriority(Priority.MEDIUM)
                .setOkHttpClient(getOkHttpClient())
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        try {
                            lstContractorTender = new ArrayList<>();
                            srlTender.setRefreshing(false);
                            Gson gson = new Gson();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                AllContractorTender tender = gson.fromJson(obj.toString(), AllContractorTender.class);
                                lstContractorTender.add(tender);
                            }
                            if (lstContractorTender.size() > 0) {

                                list_tender.setVisibility(View.VISIBLE);
                                tvNoTender.setVisibility(View.GONE);
                                Con_adapter = new ContractorTenderListAdapter(getActivity(), lstContractorTender);
                                list_tender.setAdapter(Con_adapter);
                                Con_adapter.notifyDataSetChanged();
                            } else {
                                list_tender.setVisibility(View.GONE);
                                Log.e("Tenderlistt", "--------.");
                                tvNoTender.setText(getResources().getText(R.string.no_tender_contractor_client));
                                tvNoTender.setVisibility(View.VISIBLE);
                            }

                            Log.e("getAllTender", "response in new Network Library " + allTender.size());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error

                        srlTender.setRefreshing(false);
                        list_tender.setVisibility(View.GONE);
                        Log.e(TAG, "onError: "+error.getErrorBody());
                        try {
                            JSONObject jsonObject=new JSONObject(error.getErrorBody());
                            if (jsonObject.getString("message").equalsIgnoreCase("no contract available"))
                                tvNoTender.setText("Your subscription has expired. Please renew your subscription to access Tenders");
                            else
                                tvNoTender.setText(getResources().getString(R.string.no_tender_contractor));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        tvNoTender.setVisibility(View.VISIBLE);
                    }
                });


//        NetworkingLibrary networkingLibrary = new NetworkingLibrary();
//        networkingLibrary.callMethodUsingPostAndResponseArray("tender/getTenders", "Authorization", token);
//        networkingLibrary.getObserve(new DataObserver() {
//            @Override
//            public void objectResponse(JSONObject object) {
//
//            }
//
//            @Override
//            public void onError(ANError e, String msg) {
//                srlTender.setRefreshing(false);
//                list_tender.setVisibility(View.GONE);
////                        if (error.getMessage().equalsIgnoreCase("no contract available"))
//                tvNoTender.setText("Your subscription has expired. Please renew your subscription to access Tenders");
////                        else
////                            tvNoTender.setText(getResources().getString(R.string.no_tender_contractor));
//                tvNoTender.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void arrayResponse(JSONArray response) {
//                try {
//                    srlTender.setRefreshing(false);
//                    Gson gson = new Gson();
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject obj = response.getJSONObject(i);
//                        AllContractorTender tender = gson.fromJson(obj.toString(), AllContractorTender.class);
//                        lstContractorTender.add(tender);
//                    }
//                    if(lstContractorTender.size()>0){
//
//                        list_tender.setVisibility(View.VISIBLE);
//                        tvNoTender.setVisibility(View.GONE);
//                        Con_adapter = new ContractorTenderListAdapter(getActivity(), lstContractorTender);
//                        list_tender.setAdapter(Con_adapter);
//                        Con_adapter.notifyDataSetChanged();
//                    }else{
//                        list_tender.setVisibility(View.GONE);
//                        tvNoTender.setText(getResources().getText(R.string.no_tender_client));
//                        tvNoTender.setVisibility(View.VISIBLE);
//                    }
//
//                    Log.e("getAllTender", "response in new Network Library "+allTender.size());
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });

    }

    private void GetAllTender() {
        //showProgressDialog();
        srlTender.setRefreshing(true);

        Log.e(TAG, "GetAllTenderToken: "+token );
        AndroidNetworking.post(BASE_URL + API_GET_TENDER)
                .addHeaders(AUTHORIZATION, token) // posting java object
                .setTag(GLOBAL_TAG)
                .setPriority(Priority.MEDIUM)
                .setOkHttpClient(getOkHttpClient())
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        try {
                            allTender=new ArrayList<>();
                            srlTender.setRefreshing(false);
                            Gson gson = new Gson();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                Tender tender = gson.fromJson(obj.toString(), Tender.class);
                                allTender.add(tender);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!allTender.isEmpty()) {
                                        Log.e("Tenderlistt", "onResponse non empty."+allTender.size());
                                        list_tender.setVisibility(View.VISIBLE);
                                        tvNoTender.setVisibility(View.GONE);
                                        adapter = new TenderListAdapter(getActivity(), allTender);
                                        list_tender.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Log.e("Tenderlistt", "onResponse empty."+allTender.size());
                                        list_tender.setVisibility(View.GONE);
                                        tvNoTender.setText(getResources().getText(R.string.no_tender_client));
                                        tvNoTender.setVisibility(View.VISIBLE);
                                    }
                                }
                            },1500);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error

                        srlTender.setRefreshing(false);
                        list_tender.setVisibility(View.GONE);
                        Log.e("Tenderlistt", "*****."+error.getErrorBody());
                        tvNoTender.setText(getResources().getText(R.string.no_tender_client));
                        tvNoTender.setVisibility(View.VISIBLE);
                    }
                });


    }

//    private void RemoveTender(int i, final AlertDialog alertDialog) {
//        tender = allTender.get(i);
//        String tenderid = tender.getId();
//        mAPIService.removeTender(token, tenderid).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Log.i(TAG, "response---" + response.body());
//                final Fragment fragment3 = new TenderList();
//                FragmentManager fragmentManager = getFragmentManager();
//                final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.content_frame, fragment3);
//                //fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//                alertDialog.dismiss();
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.i(TAG, "response---" + t);
//            }
//        });
//    }


//    private void getAllIntrestedContractor(String tender) {
//        showProgressDialog();
//        String token = "Bearer " + sessionManager.getPreferences(getActivity(), "token");
//        mAPIService.getAllInterestedContractor(token, tender).enqueue(new Callback<List<Sender>>() {
//            @Override
//            public void onResponse(Call<List<Sender>> call, Response<List<Sender>> response) {
//                dismissProgressDialog();
//                if (response.isSuccessful() && response.body().size() > 0) {
//                    Log.e(TAG, "onResponse: "+response.body() );
//                    sendEmailOnInterested(response.body());
//
//                } else {
//                    Log.e(TAG, "onResponse: No Interested found");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Sender>> call, Throwable t) {
//                dismissProgressDialog();
//                Log.e(TAG, "onFailure: " + t.getMessage());
//            }
//        });
//    }

    private void sendEmailOnInterested() {
        showProgressDialog();

        String token = "Bearer " + sessionManager.getPreferences(getActivity(), "token");
        Log.e("token: ", token);
        Object user = sessionManager.getPreferencesObject(getActivity());
        Log.e("username: ", ((User) user).getFirstName() + ((User) user).getLastName());
        String userName = ((User) user).getFirstName() + ((User) user).getLastName();
        FGetCategory(userName);
//        int length =clientCategory.length();
        //Check whether or not the string contains at least four characters; if not, this method is useless
//        clientCategory = clientCategory.substring(0, length - 4) + " . ";

    }

    private void ShowBox(final int i) {
        /*Code to fetch list of contractors for particular Tender*/
        tender = allTender.get(i);
        String tenderId=tender.getId();
        tenderName=tender.getTenderName();
        clientCountry=tender.getTenderUploader().getCountry();
        categoryList=tender.getCategory();

        Call<ArrayList<AllContractorTender>> resultCall = mAPIService.getTenderContractors(token, tenderId);
        resultCall.enqueue(new Callback<ArrayList<AllContractorTender>>() {
            @Override
            public void onResponse(Call<ArrayList<AllContractorTender>> call, Response<ArrayList<AllContractorTender>> response) {
                try {
                    if (response.body() != null && response.body().size() > 0) {
                        interestedContractorArray=new JSONArray();
                        for (int i = 0; i < response.body().size(); i++) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("_id", response.body().get(i).getId());
                            jsonObject.put("email", response.body().get(i).getEmail());
                            interestedContractorArray.put(jsonObject);
//                            interestedContractors.add(jsonObject.toString());
//                            Log.e(TAG, "onResponse: " + interestedContractors.get(0));

//                        categoryList.add(response.body().get(i).getCategory());
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

//                    }



            }

            @Override
            public void onFailure(Call<ArrayList<AllContractorTender>> call, Throwable t) {

            }
        });

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        alertDialog.setTitle("Tender Watch");

        alertDialog.setMessage("Are you sure to delete or edit record?");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                String Con_msg = "Tender will be completely removed from TenderWatch.are you sure you want to remove?";
                String Client_msg = "Are you sure you want to remove this Tender completely from your Account?";
                tender = allTender.get(i);
                final String tenderid = tender.getId().toString();


                Log.e(TAG, "onClick: " + tenderid);

                final AlertDialog deleteAlertDialog = new AlertDialog.Builder(getActivity()).create();
                deleteAlertDialog.setMessage("Are you sure to Delete Tender?");
                deleteAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showProgressDialog();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("tenderDetailId", tenderid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        AndroidNetworking.delete(BASE_URL + API_REMOVE_TENDER)
                                .addHeaders(AUTHORIZATION, token)
                                  .addPathParameter(PARAM_TENDER_DETAILID, tenderid)
                                .setTag(GLOBAL_TAG)
                                .setPriority(Priority.MEDIUM)
                                .setOkHttpClient(getOkHttpClient())
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.e(TAG, "onResponse: "+response.toString());
                                        dismissProgressDialog();
                                        sendEmailOnInterested();
                                        deleteAlertDialog.dismiss();
                                        getAllTenderListOrContractTender();
                                    }

                                    @Override
                                    public void onError(ANError error) {
                                        // handle error
                                        dismissProgressDialog();
                                    }
                                });


                    }


                });

                deleteAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAlertDialog.dismiss();
                    }
                });

                deleteAlertDialog.show();


            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Edit", new DialogInterface.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(DialogInterface dialog, int id) {
                tender = allTender.get(i);
                Gson gson = new Gson();
                String jsonString = gson.toJson(tender);
                Intent intent = new Intent(getActivity(), EditTenderDetail.class);
                intent.putExtra("data", jsonString);
                startActivity(intent);
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void ShowBoxForContractor(final int i) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        alertDialog.setTitle("Tender Watch");

        contractorTender = lstContractorTender.get(i);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                final AlertDialog deleteAlertDialog = new AlertDialog.Builder(getActivity()).create();
                deleteAlertDialog.setMessage("Are you sure to Delete Tender?");
                deleteAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showProgressDialog();


                        mAPIService.removeTender(token, contractorTender.getId()).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                dismissProgressDialog();
                                getAllTenderListOrContractTender();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                dismissProgressDialog();
                                Log.i(TAG, "response---" + t);
                            }
                        });


                    }


                });

                deleteAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAlertDialog.dismiss();
                    }
                });

                deleteAlertDialog.show();

//                showProgressDialog();
//
//                AndroidNetworking.delete(BASE_URL + API_REMOVE_TENDER)
//                        .addHeaders(AUTHORIZATION,token)
//                        .addPathParameter(PARAM_TENDER_DETAILID,tenderid)
//                        .setTag(GLOBAL_TAG)
//                        .setPriority(Priority.MEDIUM)
//                        .setOkHttpClient(getOkHttpClient())
//                        .build()
//                        .getAsJSONObject(new JSONObjectRequestListener() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                // do anything with response
//                                dismissProgressDialog();
//                                final Fragment fragment3 = new TenderList();
//                                FragmentManager fragmentManager = getFragmentManager();
//                                final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                                fragmentTransaction.replace(R.id.content_frame, fragment3);
//                                //fragmentTransaction.addToBackStack(null);
//                                fragmentTransaction.commit();
//                                alertDialog.dismiss();
//                            }
//
//                            @Override
//                            public void onError(ANError error) {
//                                // handle error
//                                dismissProgressDialog();
//                            }
//                        });


//                mAPIService.removeTender(token, tenderid).enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        dismissProgressDialog();
//                        Log.i(TAG, "response---" + response.body());
//                        final Fragment fragment3 = new TenderList();
//                        FragmentManager fragmentManager = getFragmentManager();
//                        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.replace(R.id.content_frame, fragment3);
//                        //fragmentTransaction.addToBackStack(null);
//                        fragmentTransaction.commit();
//                        alertDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        dismissProgressDialog();
//                        Log.i(TAG, "response---" + t);
//                    }
//                });
            }
        });

        List<String> favList = lstContractorTender.get(i).getFavorite();
        if (!favList.contains(user.getId())) {
            alertDialog.setMessage("Are you sure to Delete or add Favorite Tender?");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Favorite", new DialogInterface.OnClickListener() {

                @RequiresApi(api = Build.VERSION_CODES.N)
                public void onClick(DialogInterface dialog, int id) {
                    contractorTender = lstContractorTender.get(i);
                    String tenderid = contractorTender.getId();

                    showProgressDialog();

                    AndroidNetworking.put(BASE_URL + API_FAVOURITE_TENDER)
                            .addHeaders(AUTHORIZATION, token)
                            .addPathParameter(PARAM_TENDER_ID, tenderid)
                            .setTag(GLOBAL_TAG)
                            .setPriority(Priority.MEDIUM)
                            .setOkHttpClient(getOkHttpClient())
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // do anything with response
                                    dismissProgressDialog();
                                    final Fragment fragment3 = new TenderList();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.content_frame, fragment3);
                                    //fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                    alertDialog.dismiss();
                                }

                                @Override
                                public void onError(ANError error) {
                                    // handle error
                                    dismissProgressDialog();
                                }
                            });

//                    mAPIService.addFavorite(token, tenderid).enqueue(new Callback<ResponseBody>() {
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            dismissProgressDialog();
//                            Log.i(TAG, "response---" + response.body());
//                            final Fragment fragment3 = new TenderList();
//                            FragmentManager fragmentManager = getFragmentManager();
//                            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                            fragmentTransaction.replace(R.id.content_frame, fragment3);
//                            //fragmentTransaction.addToBackStack(null);
//                            fragmentTransaction.commit();
//                            alertDialog.dismiss();
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//                            dismissProgressDialog();
//                            Log.i(TAG, "response---" + t);
//                        }
//                    });
                }
            });
        } else
            alertDialog.setMessage("Are you sure you want to delete this Tender from your list?");

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getAllTenderListOrContractTender();
                    }
                }, 500);
            }
        });


    }

    private void FGetCategory(final String userName) {
        showProgressDialog();

        mAPIService.getCategoryData().enqueue(new Callback<ArrayList<GetCategory>>() {
            @Override
            public void onResponse(Call<ArrayList<GetCategory>> call, Response<ArrayList<GetCategory>> response) {
//                categoryList = response.body();
                dismissProgressDialog();
                List<String> categoryName=new ArrayList<>();

                if (categoryList != null && !categoryList.isEmpty() && categoryList.size() > 0) {
                    for (int i = 0; i < categoryList.size(); i++) {
                        if(response.body().get(i).getId().equalsIgnoreCase(categoryList.get(i))){
                            categoryName.add(response.body().get(i).getCategoryName());
                        }
//                        StringBuilder categoryBuilder = new StringBuilder();
//                        for (int k = 0; k < categoryList.size(); k++) {
//
//                            /*Category.setText(categoryList.get(i).getCategoryName());
//                            catFlagimg = categoryList.get(i).getImgString();
//                            catBflag = StringToBitMap(catFlagimg);
//                            catFlag.setImageBitmap(catBflag);*/
//                            int size = categoryList.size() - 1;
//                            categoryBuilder.append(categoryList.get(k));
//                            if (!(size == k)) {
//                                Log.e("size: ", "" + size + i);
//                                categoryBuilder.append(" || ");
//                            }
//                            clientCategory = categoryBuilder.toString();
//                            Log.e("clientCategory: ", clientCategory);
//
//                        }


//                        if (categoryList.get(i).getId().equalsIgnoreCase(object.getCategory().get(0))) {
//                            category.setText(categoryList.get(i).getCategoryName());
//                            break;
//                        }
                    }
                }

                if(categoryName != null && !categoryName.isEmpty() && categoryName.size()>0){
                    StringBuilder categoryBuilder = new StringBuilder();
                    for (int k = 0; k < categoryName.size(); k++) {

                            /*Category.setText(categoryName.get(i).getCategoryName());
                            catFlagimg = categoryName.get(i).getImgString();
                            catBflag = StringToBitMap(catFlagimg);
                            catFlag.setImageBitmap(catBflag);*/
                        int size = categoryName.size() - 1;
                        categoryBuilder.append(categoryName.get(k));
                        if (!(size == k)) {
                            categoryBuilder.append(" || ");
                        }
                        clientCategory = categoryBuilder.toString();
                        Log.e("clientCategory: ", clientCategory);

                    }
                }

                JsonObject gsonObject = new JsonObject();
                try {
                    // contractorEmailList.add("tenderwatch01@gmail.com");
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("Email", interestedContractorArray);
                    jsonObj.put("Category", clientCategory);
                    jsonObj.put("TenderName", tenderName);
                    jsonObj.put("CountryName", clientCountry);
                    jsonObj.put("UserName", userName);
                    jsonObj.put("Operation", "Remove");

                    JsonParser jsonParser = new JsonParser();
                    gsonObject = (JsonObject) jsonParser.parse(jsonObj.toString());

                    //print parameter
                    Log.e("MY gson.JSON:  ", "AS PARAMETER  " + gsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAPIService.sendEmail(token, gsonObject).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dismissProgressDialog();
                        Log.e("email send API.", "" + response);
                        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Tender Watch");
                        alertDialog.setMessage("Tender Removed,Email has been sent to Interested Contractor!");
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getActivity(), ClientDrawer.class);
                                startActivity(intent);
                            }
                        });
                        alertDialog.show();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        dismissProgressDialog();
                        Log.e("email send API.", "" + t);
                    }
                });
            }

            @Override
            public void onFailure(Call<ArrayList<GetCategory>> call, Throwable t) {
                dismissProgressDialog();
            }
        });
    }

    private void getAllTenderListOrContractTender() {
        if (role.equals("client")) {
            GetAllTender();
        } else {
            Bundle bundle = getArguments();
            if (bundle != null) {
                String value = bundle.getString("nav_fav");
                if (value != null) {
                    GetAllFavorite();
                    ll_search.setVisibility(View.GONE);
                    tvDate.setVisibility(View.GONE);
                }
            } else {
                AllContractorTender();
            }
        }

    }

    public void showProgressDialog() {
        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading....");
        mProgressDialog.setCancelable(false);

        if (!getActivity().isFinishing() && !mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        if (getActivity() == null)
            return;
        if (!getActivity().isFinishing() && mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    private void downloaImage() {
        try {
            Dexter.withActivity(this.getActivity())
                    .withPermission(permission)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                            new DownloadFile().execute(contractorTender.getTenderPhoto());
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(getContext(), "Please grant storage permission to download image.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();
                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());
                fileName = "TenderImage" + "_" + fileName;
                folder = Environment.getExternalStorageDirectory() + File.separator + "TenderWatch/";
                File directory = new File(folder);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                return "";

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }

        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            dismissProgressDialog();
            File file = new File(folder, fileName);
            Uri imagePath = FileProvider.getUriForFile(getActivity(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);
            TenderUploader client = contractorTender.getTenderUploader();
            Log.i(TAG, "post submitted to API." + client);
            Gson gson = new Gson();
            String jsonString = gson.toJson(contractorTender);
            String sender = gson.toJson(client);
            Intent intent = new Intent(mContext, ContractotTenderDetail.class);
            intent.putExtra("data", jsonString);
            intent.putExtra("sender", sender);
            intent.putExtra("imagePath", "" + imagePath);
            intent.putExtra("targetViewers", targetViewers);
            if (lstContractorTender.get(tendePosition).getAmendRead() != null) {
                if (lstContractorTender.get(tendePosition).getAmendRead().size() > 0) {
                    for (int i = 0; i < lstContractorTender.get(tendePosition).getAmendRead().size(); i++) {
                        String Con_id = sessionManager.getPreferencesObject(getActivity()).getId();
                        if (!lstContractorTender.get(tendePosition).getAmendRead().contains(Con_id)) {
                            intent.putExtra("amended", "true");
                        }
                    }
                }
                if (lstContractorTender.get(tendePosition).getAmendRead().size() == 0) {
                    intent.putExtra("amended", "true");
                }
            }
            startActivity(intent);
            // Display File path after downloading
            if (!message.isEmpty()) {
                Toast.makeText(getActivity(),
                        message, Toast.LENGTH_LONG).show();
            }
        }
    }


}