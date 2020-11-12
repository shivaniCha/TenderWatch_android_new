package com.tenderWatch.Drawer;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonObject;
import com.tenderWatch.Models.GetCategory;
import com.tenderWatch.Models.GetCountry;
import com.tenderWatch.Models.SubscriptionCategoryResponse;
import com.tenderWatch.Models.User;
import com.tenderWatch.R;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentHistoryFragment extends Fragment {


    public PaymentHistoryFragment() {
        // Required empty public constructor
    }

    private SwipeRefreshLayout srlTransaction;
    private SessionManager sessionManager;
    private Api mApiService;
    private List<GetCountry> country = new ArrayList<>();
    private List<GetCategory> category = new ArrayList<>();
    private List<SubscriptionCategoryResponse> subscriptionPlans=new ArrayList<>();
    private PaymentAdapter adapter;
    private final String TAG = SubscriptionContainer.class.getSimpleName();
    private List<PendingHistory> history=new ArrayList<>();
    private TextView tvNoTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_history, container, false);
        RecyclerView rvTransaction = view.findViewById(R.id.rv_transaction);
        srlTransaction = view.findViewById(R.id.srl_transaction);
        tvNoTransaction= view.findViewById(R.id.no_transaction);
        mApiService = ApiUtils.getAPIService();
        sessionManager = new SessionManager(getContext());
        getAllCountry();
        getSubscriptionsPlan();
        adapter = new PaymentAdapter(history);
        rvTransaction.setAdapter(adapter);
        rvTransaction.setLayoutManager(new LinearLayoutManager(getContext()));
        srlTransaction.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserDetails();
            }
        });
        return view;
    }

    private void getAllCountry() {
        mApiService.getCountryData().enqueue(new Callback<ArrayList<GetCountry>>() {
            @Override
            public void onResponse(Call<ArrayList<GetCountry>> call, Response<ArrayList<GetCountry>> response) {
                if (response.isSuccessful()) {
                    country = response.body();
                    getAllCategory();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetCountry>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void getAllCategory() {
        mApiService.getCategoryData().enqueue(new Callback<ArrayList<GetCategory>>() {
            @Override
            public void onResponse(Call<ArrayList<GetCategory>> call, Response<ArrayList<GetCategory>> response) {
                if (response.isSuccessful()) {
                    category = response.body();
                    getUserDetails();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetCategory>> call, Throwable t) {

            }
        });
    }

    private void getSubscriptionsPlan(){
        mApiService.getAllSubscriptionCategory().enqueue(new Callback<List<SubscriptionCategoryResponse>>() {
            @Override
            public void onResponse(Call<List<SubscriptionCategoryResponse>> call, Response<List<SubscriptionCategoryResponse>> response) {
                if(response.isSuccessful()){
                    subscriptionPlans.clear();
                    subscriptionPlans.addAll(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<SubscriptionCategoryResponse>> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.getMessage() );
            }
        });
    }

    private void getUserDetails() {
        srlTransaction.setRefreshing(true);
        String token = "Bearer " + sessionManager.getPreferences(getContext(), "token");
        final String userId = (sessionManager.getPreferencesObject(getContext())).getId();
        mApiService.getUserDetail(token, userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    List<User.PesapalDetail> tmp=response.body().getPesapalDetails();
                    history.clear();
                    try {
                        for (int i = 0; i < tmp.size(); i++) {
                            JsonObject selection = tmp.get(i).getSelections();
                            JSONObject sa = new JSONObject(selection.toString());
                            Iterator<String> iter = sa.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                PendingHistory h=new PendingHistory();
                                h.setCountryId(key);
                                List<String> cat = new ArrayList<>();
                                for (int j = 0; j < selection.getAsJsonArray(key).size(); j++) {
                                    cat.add(selection.getAsJsonArray(key).get(j).getAsString());
                                }
                                h.setCategoryId(cat);
                                h.setPlanId(tmp.get(i).getPlanType());
                                h.setStatus(tmp.get(i).getPesapalStatus());
                                h.setSubscription(tmp.get(i).getSubscriptionPackage());
                                history.add(h);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(history.size()==0)
                        tvNoTransaction.setVisibility(View.VISIBLE);
                    else
                        tvNoTransaction.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    srlTransaction.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                srlTransaction.setRefreshing(false);
                if(history.size()==0)
                    tvNoTransaction.setVisibility(View.VISIBLE);
            }
        });
    }

    public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentHolder> {

        private List<PendingHistory> history;

        public PaymentAdapter(List<PendingHistory> history) {
            this.history = history;
        }

        @Override
        public PaymentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PaymentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscription, parent, false));
        }

        @Override
        public void onBindViewHolder(PaymentHolder holder, int position) {
            try {
                // setting country
                try{
                    PendingHistory ph=history.get(position);
                    for (int i = 0; i < country.size(); i++) {
                        if (country.get(i).getId().equalsIgnoreCase(ph.getCountryId())) {
                            try {
                                Bitmap bitmapCon = StringToBitMap(country.get(i).getImageString());
                                holder.ivCountry.setImageBitmap(bitmapCon);
                                holder.tvCountry.setText(country.get(i).getCountryName());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            holder.rvCategory.setAdapter(new CategoryAdapter(ph.getCategoryId()));
                            holder.rvCategory.setLayoutManager(new LinearLayoutManager(getContext()));
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
                    holder.btnRenew.setText(ph.getStatus());
                    holder.btnRenew.setEnabled(false);
                    holder.btnRenew.setBackground(null);
                    holder.btnRenew.setBackgroundColor(getResources().getColor(android.R.color.white));
                    holder.btnRenew.setTextColor(Color.parseColor("#81c784"));

                    for (int i = 0; i < subscriptionPlans.size(); i++) {
                        if(subscriptionPlans.get(i).getId().equalsIgnoreCase(ph.getPlanId())){
                            holder.tvPlan.setText(subscriptionPlans.get(i).getName());
                        }
                    }
                    holder.tvPlan.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    holder.tvPlanTitle.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    holder.llExpire.setVisibility(View.GONE);
                    holder.view.setVisibility(View.GONE);
                    holder.view1.setVisibility(View.GONE);
                }catch (Exception e){e.printStackTrace();}
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
            private View view,view1;

            public PaymentHolder(View itemView) {
                super(itemView);
                tvCountry = itemView.findViewById(R.id.tv_country);
                tvPlanTitle = itemView.findViewById(R.id.tv_plan_title);
                tvSubscriptionType = itemView.findViewById(R.id.tv_subscription_type);
                ivCountry = itemView.findViewById(R.id.iv_country);
                btnRenew = itemView.findViewById(R.id.btn_renew);
                rvCategory = itemView.findViewById(R.id.rv_subscription_category);
                tvPlan = itemView.findViewById(R.id.tv_plan);
                llExpire= itemView.findViewById(R.id.ll_expire);
                view= itemView.findViewById(R.id.view);
                view1= itemView.findViewById(R.id.view1);
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

    public class PendingHistory{
        String countryId,planId,status;
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

}
