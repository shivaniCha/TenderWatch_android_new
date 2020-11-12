package com.tenderWatch.Drawer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.tenderWatch.Adapters.NotificationAdapter;
import com.tenderWatch.ClientDetail;
import com.tenderWatch.ClientDrawer.ClientDrawer;
import com.tenderWatch.Models.ResponseNotifications;
import com.tenderWatch.Models.Sender;
import com.tenderWatch.Models.UpdateTender;
import com.tenderWatch.NTenderDetail;
import com.tenderWatch.R;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lcom47 on 1/1/18.
 */

public class Notification extends Fragment {
    ListView notificationList;
    private static final String TAG = Notification.class.getSimpleName();
    Api mAPIServices;
    SessionManager sessionManager;
    ArrayList<ResponseNotifications> notification_list;
    NotificationAdapter adapter;
    Button delNotification, btnAllSelctNotification;
    ArrayList<String> idList = new ArrayList<String>();
    String edit;
    ResponseNotifications.Tender obj;
    private ProgressDialog mProgressDialog;
    private TextView tvNoNotification;
    private SwipeRefreshLayout srlNotification;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
//        try {
//            ((MainDrawer)getActivity()).setOptionMenu(true,false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        sessionManager = new SessionManager(getActivity());

        getActivity().setTitle("Notification");
        notificationList = view.findViewById(R.id.notificationlist);
        mAPIServices = ApiUtils.getAPIService();
        delNotification = view.findViewById(R.id.btn_del_notification);
        btnAllSelctNotification = view.findViewById(R.id.btn_all_delete_notification);
        tvNoNotification = view.findViewById(R.id.tv_no_notification);
        srlNotification=view.findViewById(R.id.src_notification);

        Bundle args = getArguments();
        if (args != null) {
            edit = args.getString("edit");
            delNotification.setVisibility(View.VISIBLE);
            btnAllSelctNotification.setVisibility(View.VISIBLE);
            btnAllSelctNotification.setVisibility(View.VISIBLE);

        }



        delNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter != null && adapter.getCheckedItem().size() > 0) {
                    idList = adapter.getCheckedItem();
                    DeleteNotification();
                }else{
                    Toast.makeText(getActivity(), "Please Select atleast one notification", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnAllSelctNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setSelectionAll();
                adapter.notifyDataSetChanged();

//                if (adapter != null && adapter.getCheckedItem().size() > 0) {
//                    idList = adapter.getCheckedItem();
//                    DeleteNotification();
//                }else{
//                    Toast.makeText(getActivity(), "Please Select atleast one notification", Toast.LENGTH_SHORT).show();
//                }
//                if (adapter != null && adapter.getCheckedItem().size() > 0) {
//
//                    for (int j=0;j<adapter.getCheckedItem().size();j++) {
//                        for (int i = 0; i < notificationList.getChildCount(); i++) {
//                            LinearLayout linearLayout = (LinearLayout) notificationList.getChildAt(i);
//                            ImageView imgChecked = linearLayout.findViewById(R.id.round_checked);
//                            ImageView imgUnchecked = linearLayout.findViewById(R.id.round);
//                            TextView rowID = linearLayout.findViewById(R.id.rowID);
////                        if(rowID)
//                            if(adapter.getCheckedItem().get(j).equalsIgnoreCase(rowID.getText().toString())){
//                            imgChecked.setVisibility(View.VISIBLE);
//                            imgUnchecked.setVisibility(View.GONE);
//                            }
//                        }
//                    }
//                }
            }
        });

        srlNotification.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetNotification();
            }
        });

        srlNotification.post(new Runnable() {
            @Override
            public void run() {
                GetNotification();
            }
        });

        notificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                boolean isCheckItem = false;
                if (getActivity() instanceof MainDrawer) {
                    isCheckItem = !((MainDrawer) getActivity()).editMenu.getTitle().equals("Edit");
                } else if (getActivity() instanceof ClientDrawer) {
                    isCheckItem = !((ClientDrawer) getActivity()).editMenu.getTitle().equals("Edit");
                }

                if (adapter != null && !isCheckItem) {
                    //obj = notification_list.get(i).getTender();
                    final Sender s = notification_list.get(i).getSender();
                    String tenderId = notification_list.get(i).getTender();
                    String token = "Bearer " + sessionManager.getPreferences(getActivity(), "token");
                    String id2 = notification_list.get(i).getId();

                    mAPIServices.readNotification(token, id2).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            dismissProgressDialog();
                            try {
                                notification_list.get(i).setRead(true);
                                adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.i(TAG, "post submitted to API." + response.body());
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            dismissProgressDialog();
                            Log.i(TAG, "post submitted to API." + t);
                        }
                    });
                    if (tenderId != null && !TextUtils.isEmpty(tenderId)) {
//                        String token = "Bearer " + sessionManager.getPreferences(getActivity(), "token");
                        mAPIServices.getTender(token, notification_list.get(i).getTender()).enqueue(new Callback<UpdateTender>() {
                            @Override
                            public void onResponse(Call<UpdateTender> call, Response<UpdateTender> response) {
                                dismissProgressDialog();
                                if (response.isSuccessful()) {
                                    UpdateTender tender = response.body();
                                    if (!tender.getIsActive()) {
                                        sessionManager.ShowDialog(getActivity(), "Tender Deleted. \n\nCan't show tender details.");
                                    } else {
                                        Gson gson = new Gson();
                                        String jsonString = gson.toJson(tender);
                                        String sender = gson.toJson(s);
                                        Intent intent;

                                        if (getActivity() instanceof ClientDrawer) {
                                            if (sender != null) {
                                                intent = new Intent(getActivity(), ClientDetail.class);
                                                intent.putExtra("data", jsonString);
                                                intent.putExtra("sender", sender);
                                                startActivity(intent);
                                            }
                                        } else {
                                            intent = new Intent(getActivity(), NTenderDetail.class);
                                            intent.putExtra("data", jsonString);
                                            intent.putExtra("sender", sender);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<UpdateTender> call, Throwable t) {
                                dismissProgressDialog();
                            }
                        });
                    }
                }
            }
        });


    }

    private void DeleteNotification() {
        showProgressDialog();
        String token = "Bearer " + sessionManager.getPreferences(getActivity(), "token");
        mAPIServices.deleteNotification(token, idList).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
                Log.i(TAG, "post submitted to API." + response);
                GetNotification();
                if (getActivity() instanceof MainDrawer) {
                    ((MainDrawer) getActivity()).callEdit();
                } else if (getActivity() instanceof ClientDrawer) {
                    ((ClientDrawer) getActivity()).callEdit();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dismissProgressDialog();
                Log.i(TAG, "post submitted to API." + t);
            }
        });
    }

    private void GetNotification() {
        showProgressDialog();
        String token = "Bearer " + sessionManager.getPreferences(getActivity(), "token");

        Log.e(TAG, "onResponseCount: "+token);
        mAPIServices.getNotifications(token).enqueue(new Callback<ArrayList<ResponseNotifications>>() {
            @Override
            public void onResponse(Call<ArrayList<ResponseNotifications>> call, Response<ArrayList<ResponseNotifications>> response) {
                dismissProgressDialog();

                int size = 0;
                if (response.body() != null && response.body().size() > 0)
                    size = response.body().size();
                if (size == 0) {
                    delNotification.setVisibility(View.GONE);
                    btnAllSelctNotification.setVisibility(View.GONE);
                    tvNoNotification.setVisibility(View.VISIBLE);
                } else {
                    tvNoNotification.setVisibility(View.GONE);
                }

                if (response.body() == null)
                    return;
                notification_list = response.body();
//                Log.e(TAG, "onResponse: "+response.body() );
                if (edit != null) {
                    adapter = new NotificationAdapter(getActivity(), notification_list, edit);
                } else {
                    adapter = new NotificationAdapter(getActivity(), notification_list, "");
                }
//                Log.e(TAG, "onResponseCount: "+response.body().get(2).getMessage());
                notificationList.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ArrayList<ResponseNotifications>> call, Throwable t) {
                dismissProgressDialog();
                tvNoNotification.setVisibility(View.VISIBLE);
                btnAllSelctNotification.setVisibility(View.GONE);
                delNotification.setVisibility(View.GONE);
                Log.i(TAG, "post submitted to API." + t);
            }
        });
    }

    public void showProgressDialog() {
       /* if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading....");
        mProgressDialog.setCancelable(false);

        if (!getActivity().isFinishing() && !mProgressDialog.isShowing())
            mProgressDialog.show();*/
       srlNotification.setRefreshing(true);
    }

    public void dismissProgressDialog() {
        /*if (getActivity() == null)
            return;

        if (!getActivity().isFinishing() && mProgressDialog != null)
            mProgressDialog.dismiss();*/
        srlNotification.setRefreshing(false);
    }


}
