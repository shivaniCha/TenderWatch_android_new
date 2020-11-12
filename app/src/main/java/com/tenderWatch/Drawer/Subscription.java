package com.tenderWatch.Drawer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.tenderWatch.Models.SubscriptionList;
import com.tenderWatch.PaymentSelection;
import com.tenderWatch.PesapalListActivity;
import com.tenderWatch.R;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lcom47 on 26/12/17.
 */

public class Subscription extends Fragment {
    private Api mAPIServices;
    private static final String TAG = Subscription.class.getSimpleName();
    private RecyclerView rvSubscription;
    private SessionManager sessionManager;
    private SwipeRefreshLayout srlSubscription;
    private List<SubscriptionList> subscriptios = new ArrayList<>();
    private SubscriptionAdapter adapter;
    private TextView tvNoSubscription;
    private String planType = "";
    private ProgressDialog mProgressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_subscription, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAPIServices = ApiUtils.getAPIService();

        rvSubscription = view.findViewById(R.id.rv_subscription);
        tvNoSubscription = view.findViewById(R.id.tv_no_subscription);
        sessionManager = new SessionManager(getActivity());
        srlSubscription = view.findViewById(R.id.srl_subscription);


        adapter = new SubscriptionAdapter(subscriptios, getActivity());
        rvSubscription.setAdapter(adapter);
        rvSubscription.setLayoutManager(new LinearLayoutManager(getActivity()));

        srlSubscription.post(new Runnable() {
            @Override
            public void run() {
                srlSubscription.setRefreshing(true);
                initRecyclerView();
            }
        });

        srlSubscription.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initRecyclerView();
            }
        });
    }

    private void initRecyclerView() {
        String token = "Bearer " + sessionManager.getPreferences(getActivity(), "token");
        Log.e(TAG, "initRecyclerView: TOKEN : " + token);
        subscriptios.clear();
        mAPIServices.getSubcriptionList(token).enqueue(new Callback<List<SubscriptionList>>() {
            @Override
            public void onResponse(Call<List<SubscriptionList>> call, Response<List<SubscriptionList>> response) {
                Log.e(TAG, "onResponse: " + response.body());
                srlSubscription.setRefreshing(false);
                if (response.body() != null) {
                    subscriptios.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    if (subscriptios.size() > 0) {
                        rvSubscription.setVisibility(View.VISIBLE);
                        tvNoSubscription.setVisibility(View.GONE);
                    } else {
                        rvSubscription.setVisibility(View.GONE);
                        tvNoSubscription.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "Something Wrong.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SubscriptionList>> call, Throwable t) {
                srlSubscription.setRefreshing(false);
                tvNoSubscription.setVisibility(View.VISIBLE);
                rvSubscription.setVisibility(View.GONE);
            }
        });
    }

    private void
    onRenewSubscription(SubscriptionList subscription) {
        //HashMap<String,List<SubscriptionList.CategoryId>> selector=new HashMap<>();
        //selector.put(Subscription.getCountryId().getId(),Subscription.getCategoryId());
        Log.e("onRenewSubscription: ",""+subscription.getId() );
        int amount = 0;
        switch (subscription.getSubscriptionTime()) {
            case "2":
                amount = 15 * subscription.getCategoryId().size();
                break;
            case "3":
                amount = 120 * subscription.getCategoryId().size();
                break;
        }
        planType = subscription.getPlanType().getId();

        String subscriptionPackage = subscription.getSubscriptionTime();
        HashMap<String, Object> pesapal = new HashMap<>();
        pesapal.put("register", false);
        pesapal.put("planType", planType);

        HashMap<String, List<String>> category = new HashMap<>();
        List<String> categories = new ArrayList<>();
        for (int i = 0; i < subscription.getCategoryId().size(); i++) {
            categories.add(subscription.getCategoryId().get(i).getId());
        }

        category.put(subscription.getCountryId().getId(), categories);

        try {
            pesapal.put("selections", new JSONObject(new Gson().toJson(category)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pesapal.put("subscriptionPackage", subscriptionPackage);

        Intent intPesaPal = new Intent(getActivity(), PaymentSelection.class);
        intPesaPal.putExtra("isFromSubscription", true);
//        intPesaPal.putExtra("Subscription", Subscription);
        intPesaPal.putExtra("planType", planType);
        intPesaPal.putExtra("id",subscription.getId());
        intPesaPal.putExtra("amount", amount);
        intPesaPal.putExtra("pesapalInfo", new JSONObject(pesapal).toString());

        startActivity(intPesaPal);
        Log.e(TAG, "onRenewSubscription: REQUEST : amount : " + amount + " : data : " + new JSONObject(pesapal));
        //callApi(amount,new JSONObject(pesapal));
    }

    public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.SubscriptionHolder> {

        List<SubscriptionList> subscriptios;
        private Context context;

        public SubscriptionAdapter(List<SubscriptionList> list, Context context) {
            this.subscriptios = list;
            this.context = context;
        }

        @Override
        public SubscriptionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SubscriptionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscription, parent, false));
        }

        @Override
        public void onBindViewHolder(SubscriptionHolder holder, int position) {
            try {
                final SubscriptionList data = subscriptios.get(position);
                Bitmap bitmapCon = StringToBitMap(data.getCountryId().getImageString());
                holder.ivCountry.setImageBitmap(bitmapCon);
                holder.tvCountry.setText(data.getCountryId().getCountryName());
                holder.tvPlan.setText(data.getPlanType().getName());

                String dtStart = data.getExpiredAt();
                try {
                    holder.rvCategory.setAdapter(new CategoryAdapter(data.getCategoryId()));
                    holder.rvCategory.setLayoutManager(new LinearLayoutManager(context));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                switch (data.getSubscriptionTime()) {
                    case "1":
                        holder.tvSubscriptionType.setText("Free");
                        holder.btnRenew.setEnabled(false);
                        holder.btnRenew.setText("Free Subscription can't be renewed");
                        break;
                    case "2":
                        holder.tvSubscriptionType.setText("Monthly");
                        holder.btnRenew.setEnabled(true);
                        holder.btnRenew.setText("Renew Plan");
                        break;
                    case "3":
                        holder.tvSubscriptionType.setText("Yearly");
                        holder.btnRenew.setEnabled(true);
                        holder.btnRenew.setText("Renew Plan");
                        break;
                }

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                SimpleDateFormat formatToShow = new SimpleDateFormat("MMM dd yyyy");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
                try {
                    Date date = format.parse(dtStart);
                    holder.tvExpDate.setText(formatToShow.format(date) + "");

                    long elapsDay = showRenewPlan(simpleDateFormat.format(date));

                    if (Integer.parseInt(data.getSubscriptionTime()) > 1 && (elapsDay <= 7 && elapsDay >= 0)) {
                        holder.btnRenew.setEnabled(true);
                        holder.btnRenew.setText("Renew Plan");
                        holder.btnRenew.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dark_green));
                    } else if (elapsDay < 0) {
                        if (Integer.parseInt(data.getSubscriptionTime()) > 1) {
                            holder.btnRenew.setEnabled(true);
                            holder.btnRenew.setText("Expired (renew plan)");
                        }
                        else {
                            holder.btnRenew.setEnabled(false);
                            holder.btnRenew.setText("Expired");
                        }

                        holder.btnRenew.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), android.R.color.holo_red_light));
                    } else {
                        holder.btnRenew.setEnabled(false);
                        holder.btnRenew.setText("Currently Active");
                        holder.btnRenew.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.holo_green));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                holder.btnRenew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onRenewSubscription(data);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return subscriptios.size();
        }

        public class SubscriptionHolder extends RecyclerView.ViewHolder {
            private TextView tvCountry, tvSubscriptionType, tvExpDate, tvPlan;
            private ImageView ivCountry;
            private Button btnRenew;
            private RecyclerView rvCategory;

            public SubscriptionHolder(View itemView) {
                super(itemView);
                tvCountry = itemView.findViewById(R.id.tv_country);
                tvExpDate = itemView.findViewById(R.id.tv_exp_date);
                tvSubscriptionType = itemView.findViewById(R.id.tv_subscription_type);
                ivCountry = itemView.findViewById(R.id.iv_country);
                btnRenew = itemView.findViewById(R.id.btn_renew);
                rvCategory = itemView.findViewById(R.id.rv_subscription_category);
                tvPlan = itemView.findViewById(R.id.tv_plan);
            }
        }


    }

    public long showRenewPlan(String expire) {
        //milliseconds

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        String formattedDate = simpleDateFormat.format(c);

        Date startDate = null;
        Date endDate = null;
        try {
            startDate = simpleDateFormat.parse(formattedDate);
            endDate = simpleDateFormat.parse(expire);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (startDate == null)
            Log.e(TAG, "printDifference: date not found");

        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;

        return elapsedDays;
        /*if (elapsedDays <= 7) {
            return true;
        } else {
            return false;
        }*/
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

        List<SubscriptionList.CategoryId> categorys;

        public CategoryAdapter(List<SubscriptionList.CategoryId> list) {
            this.categorys = list;
        }

        @Override
        public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CategoryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscription_category, parent, false));
        }

        @Override
        public void onBindViewHolder(CategoryHolder holder, int position) {
            try {
                Bitmap bitmapCat = StringToBitMap(categorys.get(position).getImgString());
                holder.ivCategory.setImageBitmap(bitmapCat);
                holder.tvCategory.setText(categorys.get(position).getCategoryName());
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
}

