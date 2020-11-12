package com.tenderWatch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tenderWatch.Adapters.IndexingArrayAdapter;
import com.tenderWatch.Drawer.MainDrawer;
import com.tenderWatch.Models.GetCountry;
import com.tenderWatch.Models.SubscriptionCategoryResponse;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountryList extends AppCompatActivity {

    private ListView lvCountry;
    private EditText edtSearch;
    private static final String TAG = CountryList.class.getSimpleName();
    private Api mAPIService;
    Map<String, Integer> mapIndex;
    private static ArrayList<Item> countryList = new ArrayList<CountryList.Item>();
    private static ArrayList<String> alpha = new ArrayList<String>();
    private static ArrayList<String> alpha2 = new ArrayList<String>();
    public static ArrayList<String> list = new ArrayList<String>();
    public static char[] alphabetlist = new char[27];
    ArrayList<GetCountry> allCountryList;
    //ArrayList list;
    public static final String JSON_STRING = "{\"employee\":{\"name\":\"Sachin\",\"salary\":56000}}";
    private SideSelector sideSelector = null;
    IndexingArrayAdapter adapter;
    Button btn_next;
    String alphabetS = "";
    LinearLayout lltext, back, subscription;
    TextView txtSelectedContract;
    Intent intent;
    String check, s;
    SessionManager sessionManager;
    ArrayList<String> a_country = new ArrayList<String>();
    ImageView imgClose;
    int pos = 0;
    int subscriptionType = 1;
    private int selectedAmount = 0;
    private int selectedVat = 0;
    int totalAmount = 0;
    private RecyclerView rvContractCategory;
    private Dialog dialogPricing;
    private boolean fromRegister = false, isForSelectCountry = false;
    private TextView[] dots;
    private int[] layouts;
    private LinearLayout dotsLayout;
    private String planType = "";
    private String planTitle = "";
    private String ISO = "";

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
    private String googleToken = "";
    int tzPos=0;

    private boolean isTrialVisible=false;

    private boolean fromSubscription=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);

        sessionManager = new SessionManager(CountryList.this);

        edtSearch = (EditText) findViewById(R.id.edtSearch);
        lvCountry = (ListView) findViewById(R.id.lvCountry);
        lltext = (LinearLayout) findViewById(R.id.lltext);
        back = (LinearLayout) findViewById(R.id.country_toolbar);
        subscription = (LinearLayout) findViewById(R.id.subscription);
        imgClose = (ImageView) findViewById(R.id.img_close);

        sideSelector = (SideSelector) findViewById(R.id.side_selector);
        mAPIService = ApiUtils.getAPIService();
        lvCountry.setDivider(null);
        btn_next = (Button) findViewById(R.id.btn_CountryNext);
        txtSelectedContract = (TextView) findViewById(R.id.txt_selectedContract);

        lvCountry.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        Intent show = getIntent();
        check = show.getStringExtra("check");
        s = show.getStringExtra("sub");
        if(getIntent()!=null && getIntent().getBooleanExtra("fromSubscription",false)){
            fromSubscription=true;
        }
        if (getIntent() != null && getIntent().hasExtra("fromSignUP") && getIntent().getBooleanExtra("fromSignUP", false)) {
            fromRegister = true;
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
            googleToken = getIntent().getStringExtra("googleToken");
            file = getIntent().getStringExtra("file");
            isTrialVisible=getIntent().getBooleanExtra("isTrialVisible",false);
        }
        if (check == null) {
            CallContractorSignUp();
        } else {
            isForSelectCountry = true;
            btn_next.setText("Done");
        }
        if (s != null) {
            subscription.setVisibility(View.VISIBLE);
            back.setVisibility(View.GONE);
            txtSelectedContract.setVisibility(View.GONE);
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        if (!this.isFinishing())
            progressDialog.show();

        mAPIService.getCountryData().enqueue(new Callback<ArrayList<GetCountry>>() {
            @Override
            public void onResponse(Call<ArrayList<GetCountry>> call, Response<ArrayList<GetCountry>> response) {
                alpha = new ArrayList<>();
                list = new ArrayList<>();
                countryList = new ArrayList<>();
                alpha2 = new ArrayList<>();

                allCountryList = response.body();
                if (allCountryList != null && !allCountryList.isEmpty() && allCountryList.size() > 0) {
                    for (int i = 0; i < allCountryList.size(); i++) {
                        String name = response.body().get(i).getCountryName();
                        String flag = response.body().get(i).getImageString();
                        String countryCode = response.body().get(i).getCountryCode();
                        String id = response.body().get(i).getId();
                        if (fromRegister || fromSubscription) {
                            if(name.equalsIgnoreCase("Tanzania"))
                            alpha.add(name + '~' + id + '~' + countryCode + '`' + flag + '`' + response.body().get(i).getIsoCode());
                        }else{
                            alpha.add(name + '~' + id + '~' + countryCode + '`' + flag + '`' + response.body().get(i).getIsoCode());
                        }
                    }

                    Collections.sort(alpha);
                    if (fromRegister || fromSubscription) {
                        String name = alpha.get(0).split("~")[0];
                        String id = alpha.get(0).split("~")[1].split("~")[0];
                        String countryCode = alpha.get(0).split("~")[2].split("`")[0];
                        String flag = alpha.get(0).split("~")[2].split("`")[1];
                        String value = String.valueOf(name.charAt(0));
                        String ISO = alpha.get(0).split("`")[2];
                        if (!list.contains(value)) {
                            list.add(value);
                            alphabetS.concat(value);//alphabetlist.append(value);
                            Log.i("array-------", String.valueOf(list));
                            alpha2.add(value);
                            alpha2.add(name);

                            countryList.add(new SectionItem(value, "", "", "", ISO, false));
                            countryList.add(new EntryItem(name, flag, countryCode, id, ISO, false));
                        } else {
                            alpha2.add(name);
                            countryList.add(new EntryItem(name, flag, countryCode, id, ISO, false));
                        }
                    }else {
                        for (int i = 0; i < allCountryList.size(); i++) {
                            String name = alpha.get(i).split("~")[0];
                            String id = alpha.get(i).split("~")[1].split("~")[0];
                            String countryCode = alpha.get(i).split("~")[2].split("`")[0];
                            String flag = alpha.get(i).split("~")[2].split("`")[1];
                            String value = String.valueOf(name.charAt(0));
                            String ISO = alpha.get(i).split("`")[2];
                            if (!list.contains(value)) {
                                list.add(value);
                                alphabetS.concat(value);//alphabetlist.append(value);
                                Log.i("array-------", String.valueOf(list));
                                alpha2.add(value);
                                alpha2.add(name);

                                countryList.add(new SectionItem(value, "", "", "", ISO, false));
                                countryList.add(new EntryItem(name, flag, countryCode, id, ISO, false));
                            } else {
                                alpha2.add(name);
                                countryList.add(new EntryItem(name, flag, countryCode, id, ISO, false));
                            }
                        }
                    }
                }

                alpha.clear();
                String str = null;
                char[] chars = new char[0];
                char[] al = new char[0];
                if (!list.isEmpty() && list.size() > 0) {
                    str = list.toString().replaceAll(",", "");
                    chars = str.toCharArray();
                    Log.i(TAG, "post submitted to API." + chars);
                    al = new char[27];

                    for (int j = 1, i = 0; j < chars.length; j = j + 2, i++) {
                        al[i] = chars[j];
                        Log.i(TAG, "post." + chars[j]);
                    }

                    Log.i(TAG, "post submitted to API." + al);
                    SideSelector ss = new SideSelector(getApplicationContext());
                    ss.setAlphabet(al);
                    alphabetlist = str.substring(1, str.length() - 1).replaceAll(" ", "").toCharArray();
                    adapter = new IndexingArrayAdapter(getApplicationContext(), R.id.lvCountry, countryList, alpha2, list, chars);
                    lvCountry.clearChoices();
                    lvCountry.setAdapter(adapter);

                    lvCountry.clearChoices();
                    lvCountry.setTextFilterEnabled(true);
                }
                if (!CountryList.this.isFinishing())
                    progressDialog.dismiss();

                if (sideSelector != null && lvCountry != null)
                    sideSelector.setListView(lvCountry);
                else {
                    sessionManager.ShowDialog(CountryList.this, response.errorBody().source().toString().split("\"")[3]);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetCountry>> call, Throwable t) {
                if (!CountryList.this.isFinishing())
                    progressDialog.dismiss();
                sessionManager.ShowDialog(CountryList.this, "Server is down. Come back later!!");
                Log.i(TAG, "post submitted to API.");
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent i = new Intent(CountryList.this, MainDrawer.class);
                i.putExtra("nav_sub", "true");
                startActivity(i);*/
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(CountryList.this, SignUp.class);
                alpha2.clear();
                countryList.clear();
                list.clear();
                alpha.clear();
                intent.putExtra("Country", a_country);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setItemSelected(pos);
                    }
                });
                countryList.clear();
                alpha2.clear();
                list.clear();
                alpha.clear();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        lvCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
//               adapter.isEnabled(position);
                if (fromRegister || fromSubscription){
                    if (!adapter.getItem(position).getTitle().equalsIgnoreCase("Tanzania")) {

                        sessionManager.ShowDialog(CountryList.this, "You can choose only Tanzania country ");
                        return;
                    }
                }
                if ((txtSelectedContract.getText().toString().equalsIgnoreCase("Trial Version") || subscriptionType == 1) && adapter.getallitems().size() == 1 && !adapter.isItemSelected(position)) {
                    if (isForSelectCountry) {
                        sessionManager.ShowDialog(CountryList.this, "You can choose only 1 country");
                    } else {
                        sessionManager.ShowDialog(CountryList.this, "During free trial period you can choose only 1 country");
                    }
                    return;
                }


                adapter.setItemSelected(position);

                boolean isChecked = adapter.setCheckedItem(position);
                pos = position;
                HashMap<String, String> items = adapter.getallitems();
                if (isChecked) {
                    if (countryList.size() > items.size())
                        totalAmount = totalAmount + selectedAmount;
                } else {
                    if (countryList.size() > items.size())
                        totalAmount = items.size() * selectedAmount;
                    else
                        totalAmount = items.size() * selectedAmount;
                }

                ISO = adapter.getItem(position).getISO();

                if (subscriptionType == 2) {
                    if (totalAmount > 0)
                        txtSelectedContract.setText("Tzs" + totalAmount + " / month");
                    else
                        txtSelectedContract.setText("Tzs" + selectedAmount + " / month");
                } else if (subscriptionType == 3) {
                    if (totalAmount > 0)
                        txtSelectedContract.setText("Tzs" + totalAmount + " / year");
                    else
                        txtSelectedContract.setText("Tzs" + selectedAmount + " / year");
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> a_countryID = new ArrayList<String>();

                if (adapter != null && adapter.getallitems() != null) {
                    HashMap<String, String> items = adapter.getallitems();
                    for (Map.Entry<String, String> entry : items.entrySet()) {
                        a_country.add(entry.getValue());
                    }
                    HashMap<String, String> items2 = adapter.getallitems();
                    for (Map.Entry<String, String> entry : items2.entrySet()) {
                        a_countryID.add(entry.getValue().split("~")[2]);
                    }
                }

                if (txtSelectedContract.getText().toString().equalsIgnoreCase("Trial Version") || subscriptionType == 1) {
                    if (a_country.size() > 1) {
                        if (check == null) {
                            sessionManager.ShowDialog(CountryList.this, "During free trial period you can choose only 1 country");
                        } else {
//                            sessionManager.ShowDialog(CountryList.this, "Please choose one country");
                            sessionManager.ShowDialog(CountryList.this, "Please choose country");
                        }
                    } else {
                        if (check == null) {
                            if (a_country == null || a_country.size() == 0) {
//                                sessionManager.ShowDialog(CountryList.this, "Please choose one country");
                                sessionManager.ShowDialog(CountryList.this, "Please choose country");
                            } else {
                                sessionManager.setSubscribeType(CountryList.this, subscriptionType);
                                intent = new Intent(CountryList.this, Category.class);
                                intent.putExtra("sub", s);
                                intent.putExtra("isFromRegister", fromRegister);
                                intent.putExtra("CountryAtContractor", a_countryID);
                                intent.putExtra("Country", a_country);
                                intent.putExtra("version", txtSelectedContract.getText().toString());
                                intent.putExtra("amount", selectedAmount);
                                intent.putExtra("vat", selectedVat);
                                intent.putExtra("subscriptionType", subscriptionType);
                                intent.putExtra("planType", planType);
                                intent.putExtra("planTitle", planTitle);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.setItemSelected(pos);
                                    }
                                });


                                intent.putExtra("firstname", firstname);
                                intent.putExtra("lastname", lastname);
                                intent.putExtra("email", email);
                                intent.putExtra("password", password);
                                intent.putExtra("googleToken", googleToken);
                                intent.putExtra("country", country);
                                intent.putExtra("contact", contact);
                                intent.putExtra("occupation", occupation);
                                intent.putExtra("aboutMe", aboutMe);
                                intent.putExtra("role", role);
                                intent.putExtra("deviceId", deviceId);
                                intent.putExtra("subscribe", subscribe);
                                intent.putExtra("file", file);

                                countryList.clear();
                                alpha2.clear();
                                list.clear();
                                alpha.clear();
                                startActivity(intent);
//                                finish();
                            }
                        } else {
                            intent = new Intent(CountryList.this, SignUp.class);
                            intent.putExtra("Country", a_country);
                            intent.putExtra("ISO", ISO);
                            /*runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setItemSelected(pos);
                                }
                            });*/

                            countryList.clear();
                            alpha2.clear();
                            list.clear();
                            alpha.clear();
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    }
                } else {
                    if (a_country == null || a_country.size() == 0) {
                        sessionManager.ShowDialog(CountryList.this, "Please choose one country");
                    } else {
                        intent = new Intent(CountryList.this, Category.class);
                        intent.putExtra("sub", s);
                        intent.putExtra("isFromRegister", fromRegister);
                        intent.putExtra("CountryAtContractor", a_countryID);
                        intent.putExtra("Country", a_country);
                        intent.putExtra("version", txtSelectedContract.getText().toString());
                        intent.putExtra("amount", selectedAmount);
                        intent.putExtra("vat", selectedVat);
                        intent.putExtra("subscriptionType", subscriptionType);
                        intent.putExtra("planType", planType);
                        intent.putExtra("planTitle", planTitle);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setItemSelected(pos);
                            }
                        });

                        intent.putExtra("firstname", firstname);
                        intent.putExtra("lastname", lastname);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        intent.putExtra("country", country);
                        intent.putExtra("contact", contact);
                        intent.putExtra("occupation", occupation);
                        intent.putExtra("googleToken", googleToken);
                        intent.putExtra("aboutMe", aboutMe);
                        intent.putExtra("role", role);
                        intent.putExtra("deviceId", deviceId);
                        intent.putExtra("subscribe", subscribe);
                        intent.putExtra("file", file);
                        countryList.clear();
                        alpha2.clear();
                        list.clear();
                        alpha.clear();
                        startActivity(intent);
//                        finish();
                    }
                }
            }
        });
        edtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                sideSelector.setVisibility(View.INVISIBLE);
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null)
                    adapter.getFilter().filter(s.toString());

                if (s.toString().isEmpty())
                    sideSelector.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

        });
    }

    private void setUpContractCategory() {
        mAPIService.getAllSubscriptionCategory().enqueue(new Callback<List<SubscriptionCategoryResponse>>() {
            @Override
            public void onResponse(Call<List<SubscriptionCategoryResponse>> call, Response<List<SubscriptionCategoryResponse>> response) {
                if (response.isSuccessful()) {
                    List<SubscriptionCategoryResponse> categories = new ArrayList<>();
                    if (fromRegister) {
                        if (!isTrialVisible) {
                            SubscriptionCategoryResponse freeTrial = new SubscriptionCategoryResponse();
                            freeTrial.setId("free trial");
                            freeTrial.setName("from registration");
                            freeTrial.setVat(0);
                            freeTrial.setDeleted(false);
                            categories.add(freeTrial);
                        }
                    }
                    categories.addAll(response.body());

                    /* for list category */
                    rvContractCategory.setLayoutManager(new LinearLayoutManager(CountryList.this));
                    rvContractCategory.setAdapter(new ContractorListCategoryAdapter(categories));

                    /* for swipe category */
                    /*rvContractCategory.setAdapter(new ContractorCategoryAdapter(categories));
                    final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                    rvContractCategory.setLayoutManager(layoutManager);
                    SnapHelper snapHelper = new PagerSnapHelper();
                    snapHelper.attachToRecyclerView(rvContractCategory);
                    addBottomDots(0);
                    rvContractCategory.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                                //Dragging
                            } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                            }
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            //final int offset = topRv.computeHorizontalScrollOffset();
                            //if (offset % myCellWidth == 0) {
                            //    final int position = offset / myCellWidth ;
                            //}
                            int firstVisibleItem = ((LinearLayoutManager) rvContractCategory.getLayoutManager()).findFirstVisibleItemPosition();
                            if (firstVisibleItem != 0 && fromRegister) {

                            } else {

                            }
                            addBottomDots(firstVisibleItem);
                        }
                    });*/
                }
            }

            @Override
            public void onFailure(Call<List<SubscriptionCategoryResponse>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void CallContractorSignUp() {
        dialogPricing = new Dialog(CountryList.this);
        dialogPricing.setContentView(R.layout.select_contract);
        dialogPricing.setCancelable(false);
        Window window = dialogPricing.getWindow();

        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        lltext.setVisibility(View.VISIBLE);
        Button txtCancel = dialogPricing.findViewById(R.id.tv_cancel);
        rvContractCategory = (RecyclerView) dialogPricing.findViewById(R.id.rv_contract_subscription_category);
        setUpContractCategory();
        dotsLayout = (LinearLayout) dialogPricing.findViewById(R.id.layoutDots);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (!isFinishing())
            if (dialogPricing != null) {
                dialogPricing.show();
            }
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[rvContractCategory.getAdapter().getItemCount()];
        if (((LinearLayout) dotsLayout).getChildCount() > 0)
            ((LinearLayout) dotsLayout).removeAllViews();

        int colorsActive = getResources().getColor(R.color.green_color);
        int colorsInactive = getResources().getColor(android.R.color.darker_gray);

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive);
    }

    /**
     * row item
     */
    public interface Item {
        boolean isSection();

        String getTitle();

        String getFlag();

        String getCode();

        String getId();

        String getISO();

        boolean getSelected();

        void setSelected(boolean isSelected);
    }

    /**
     * Section Item
     */
    public static class SectionItem implements Item {
        private final String title;
        private final String flag;
        private final String code;
        private final String id;
        private final String ISO;

        private boolean isSelected;

        public SectionItem(String title, String flag, String code, String id, String ISO, boolean isSelected) {
            this.title = title;
            this.flag = flag;
            this.code = code;
            this.id = id;
            this.isSelected = isSelected;
            this.ISO = ISO;
        }

        public String getFlag() {
            return flag;
        }

        @Override
        public String getCode() {
            return code;
        }

        public String getId() {
            return id;
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

        @Override
        public String getISO() {
            return ISO;
        }
    }


    /**
     * Entry Item
     */
    public static class EntryItem implements Item {
        public final String title;
        private final String flag;
        private final String code;
        private final String id;
        private final String ISO;
        private boolean isSelected;

        public EntryItem(String title, String flag, String code, String id, String ISO, boolean isSelected) {
            this.title = title;
            this.flag = flag;
            this.code = code;
            this.id = id;
            this.isSelected = isSelected;
            this.ISO = ISO;
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

        @Override
        public String getCode() {
            return code;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean getSelected() {
            return isSelected;
        }

        @Override
        public boolean isSection() {
            return false;
        }

        @Override
        public String getISO() {
            return ISO;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public class ContractorListCategoryAdapter extends RecyclerView.Adapter<ContractorListCategoryAdapter.ContractListCategoryHolder> {

        private List<SubscriptionCategoryResponse> categories;
        private int selectedPosition = -1;
        private int lastPosition = -1;

        public ContractorListCategoryAdapter(List<SubscriptionCategoryResponse> categories) {
            this.categories = categories;
        }

        @Override
        public ContractListCategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ContractListCategoryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.contractor_list_category_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final ContractListCategoryHolder holder, final int position) {
//            final int position = fromRegister ? position1 - 1 : position1;
            if (categories.get(position).getId().equalsIgnoreCase("free trial") &&
                    categories.get(position).getName().equalsIgnoreCase("from registration") &&
                    categories.get(position).getVat() == 0 &&
                    position == 0) {
                holder.tvCategoryTitle.setText("Free trial (First Month)");
                holder.llCategoryTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        txtSelectedContract.setText("Trial Version");
                        subscriptionType = 1;
                        selectedAmount = 0;
                        selectedVat = 0;
                        dialogPricing.dismiss();
                        planType = "freeSubscribe";
                        planTitle = "Free Trial";
                    }
                });
                holder.ivArrow.setVisibility(View.GONE);
            } else {
                holder.tvCategoryTitle.setText(categories.get(position).getName());
                String month = "", year = "";
                Log.e(TAG, "onBindViewHolder month: "+ categories.get(position).getPrices().getMonthly());
                Log.e(TAG, "onBindViewHolder year: "+ categories.get(position).getPrices().getYearly());
                if (categories.get(position).getPrices().getMonthly() != null) {
                    month = "Monthly Subscription (Tzs %%/Month)".replace("%%", categories.get(position).getPrices().getMonthly().toString());
                } else {
                    holder.tvMonth.setVisibility(View.GONE);
                }

                if (categories.get(position).getPrices().getYearly() != null) {
                    year = "Yearly Subscription (Tzs %%/Year)".replace("%%", categories.get(position).getPrices().getYearly().toString());
                } else {
                    holder.tvYear.setVisibility(View.GONE);
                }

                holder.tvMonth.setText(month);
                holder.tvYear.setText(year);

                if (position == selectedPosition) {
                    holder.llCategorySub.setVisibility(View.VISIBLE);
                    holder.ivArrow.animate().rotationBy(180f).setInterpolator(new LinearInterpolator()).start();
//                    holder.ivArrow.setBackground(getResources().getDrawable(R.drawable.ic_close));
                } else {
                    holder.llCategorySub.setVisibility(View.GONE);
                    holder.ivArrow.setBackground(getResources().getDrawable(R.drawable.ic_open));
                }

                holder.llCategoryTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        holder.llCategorySub.setVisibility(holder.llCategorySub.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                        lastPosition = selectedPosition;
                        if (holder.llCategorySub.getVisibility() == View.VISIBLE) {
                            selectedPosition = -1;
                        } else {
                            selectedPosition = position;
                        }
                        notifyDataSetChanged();
                    }
                });

                holder.tvMonth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedAmount = categories.get(position).getPrices().getMonthly();
                        selectedVat = categories.get(position).getVat();
                        subscriptionType = 2;
                        txtSelectedContract.setText("Tzs %% / Month".replace("%%", categories.get(position).getPrices().getMonthly().toString()));
                        dialogPricing.dismiss();
                        planType = categories.get(position).getId();
                        planTitle = categories.get(position).getName();
                    }
                });

                holder.tvYear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedAmount = categories.get(position).getPrices().getYearly();
                        selectedVat = categories.get(position).getVat();
                        subscriptionType = 3;
                        txtSelectedContract.setText("Tzs %% / year".replace("%%", categories.get(position).getPrices().getYearly().toString()));
                        dialogPricing.dismiss();
                        planType = categories.get(position).getId();
                        planTitle = categories.get(position).getName();
                    }
                });

            }

        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        public class ContractListCategoryHolder extends RecyclerView.ViewHolder {

            private TextView tvMonth, tvYear, tvCategoryTitle;
            private LinearLayout llCategorySub, llCategoryTitle;
            private ImageView ivArrow;

            public ContractListCategoryHolder(View itemView) {
                super(itemView);
                tvMonth = itemView.findViewById(R.id.tv_month);
                tvYear = itemView.findViewById(R.id.tv_year);
                tvCategoryTitle = itemView.findViewById(R.id.tv_category);
                llCategorySub = itemView.findViewById(R.id.ll_category_sub);
                llCategoryTitle = itemView.findViewById(R.id.ll_category_title);
                ivArrow = itemView.findViewById(R.id.iv_arrow);
            }
        }
    }

}
