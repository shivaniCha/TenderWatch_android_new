package com.tenderWatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.crazyorr.zoomcropimage.CropShape;
import com.github.crazyorr.zoomcropimage.ZoomCropImageActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tenderWatch.Adapters.CustomList;
import com.tenderWatch.ClientDrawer.ClientDrawer;
import com.tenderWatch.ClientDrawer.Home;
import com.tenderWatch.Models.CreateUser;
import com.tenderWatch.Models.GetCategory;
import com.tenderWatch.Models.GetCountry;
import com.tenderWatch.Models.ResponseRating;
import com.tenderWatch.Models.Sender;
import com.tenderWatch.Models.SubscriptionCategoryResponse;
import com.tenderWatch.Models.Tender;
import com.tenderWatch.Models.User;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;
import com.tenderWatch.Validation.MyScrollView;
import com.tenderWatch.Validation.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;

public class EditTenderDetail extends AppCompatActivity {
    Api mApiService;
    private static final ArrayList<String> alpha = new ArrayList<String>();
    private static final ArrayList<String> countryName = new ArrayList<String>();
    private static final ArrayList<String> alpha2 = new ArrayList<String>();
    private static final ArrayList<String> categoryName = new ArrayList<String>();
    private static final ArrayList<String> falpha = new ArrayList<String>();
    private static final ArrayList<String> fcountryName = new ArrayList<String>();
    private static final ArrayList<String> falpha2 = new ArrayList<String>();
    private static final ArrayList<String> fcategoryName = new ArrayList<String>();
    private ArrayList<String> targetViewers = new ArrayList<>();
    private ArrayList<String> targetViewersId = new ArrayList<>();
    private static final String TAG = EditTenderDetail.class.getSimpleName();
    private List<String> categories = new ArrayList<>();
    private List Data;
    private ListView lvCountry;
    ArrayList<GetCountry> countryList = new ArrayList<>();
    ArrayList<GetCategory> categoryList = new ArrayList<>();
    ArrayList<GetCategory> _categoryList = new ArrayList<>();
    private LinearLayout ll_contact_detail;
    CustomList countryAdapter, categoryAdapter;
    ListView spinner, spinner2, spinner_targetViewer;
    ImageView down_arrow, up_arrow, down_arrow2, up_arrow2, down_arrow3, up_arrow3, tenderImage, down_arrow_target_viewer, up_arrow_target_viewer;
    LinearLayout country_home, category_home, ll_target_viewer;
    TextView country, category, txt_contact_category_name, txt_target_viewer, btnDone;
    String countryCode, categoryname, countryname, id;
    SessionManager sessionManager;
    MyScrollView scrollView;
    MultipartBody.Part name1, id1, email1, countryId1, image1, categoryId1, landlineNo1, contactNo1, city1, description1, descriptionLink, address1, isFollowTenderLink, isFollowTendeer1, tenderPhono1, targetViewers1;
    EditText city, title, description, edtSearch, link;
    Button btnUploadTender;
    private RelativeLayout rl_target_viewer;
    private Uri mPictureUri;
    private static final int PICTURE_WIDTH = 1100;
    private static final int PICTURE_HEIGHT = 600;
    String token;
    private static final int REQUEST_CODE_SELECT_PICTURE = 0;
    private static final int REQUEST_CODE_CROP_PICTURE = 1;
    Tender object;
    private ProgressDialog mProgressDialog;
    private int selectedTargetViewer = -1;
    private CheckBox cbFlowTender, cbFollowTenderLink;
    private String ISO = "";
    private String clientCountry, clientCategory;
    List<String> contractorEmailList;
    JSONArray arrInterestedContractor;
    EditText mobile;
    EditText landline;
    EditText email2;
    EditText address;
    private boolean isValidContactDetails = false;
    TextView code,contact_landline_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tender_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessionManager = new SessionManager(EditTenderDetail.this);
        List<Sender> emailList = (ArrayList<Sender>) getIntent().getSerializableExtra("contractorEmail");
//        contractorEmailList = new ArrayList<String>();
        arrInterestedContractor=new JSONArray();
        clientCategory = getIntent().getStringExtra("clientCategory");
        if (emailList != null && emailList.size() > 0) {
            for (int i = 0; i < emailList.size(); i++) {
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("_id",emailList.get(i).getId());
                    jsonObject.put("email", emailList.get(i).getEmail());
                    arrInterestedContractor.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                contractorEmailList.add(emailList.get(i).getEmail());
            }
            Log.e("emailList", "" + arrInterestedContractor.toString());
        }
        lvCountry = (ListView) findViewById(R.id.lvCountry);
        mApiService = ApiUtils.getAPIService();
        spinner = (ListView) findViewById(R.id.spinner);
        spinner2 = (ListView) findViewById(R.id.spinner3);
        city = (EditText) findViewById(R.id.home_city);
        title = (EditText) findViewById(R.id.home_title);
        description = (EditText) findViewById(R.id.home_address);
        link = (EditText) findViewById(R.id.home_link);
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        down_arrow_target_viewer = (ImageView) findViewById(R.id.down_arrow_target_viewer);
        up_arrow_target_viewer = (ImageView) findViewById(R.id.up_arrow_target_viewer);
        btnUploadTender = (Button) findViewById(R.id.btn_uploadTender);
        spinner_targetViewer = (ListView) findViewById(R.id.spinner_targetViewer);
        spinner_targetViewer.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
//        down_arrow = (ImageView) findViewById(R.id.down_arrow);
//        up_arrow = (ImageView) findViewById(R.id.up_arrow);
        down_arrow2 = (ImageView) findViewById(R.id.down_arrow2);
        up_arrow2 = (ImageView) findViewById(R.id.up_arrow2);
        down_arrow3 = (ImageView) findViewById(R.id.down_arrow3);
        up_arrow3 = (ImageView) findViewById(R.id.up_arrow3);
        tenderImage = (ImageView) findViewById(R.id.tender_image);
        rl_target_viewer = (RelativeLayout) findViewById(R.id.rl_target_viewer);
        ll_target_viewer = (LinearLayout) findViewById(R.id.ll_target_viewer);
        country_home = (LinearLayout) findViewById(R.id.country_home);
        category_home = (LinearLayout) findViewById(R.id.category_home);
        country = (TextView) findViewById(R.id.txt_home_country_name);
        category = (TextView) findViewById(R.id.txt_contact_category_name);
        txt_target_viewer = (TextView) findViewById(R.id.txt_target_viewer);
        btnDone = (TextView) findViewById(R.id.btn_done_category);
        cbFollowTenderLink = (CheckBox) findViewById(R.id.cb_follow_tenderlink);
        ll_contact_detail = (LinearLayout) findViewById(R.id.ll_contact_detail);
        mApiService = ApiUtils.getAPIService();
        scrollView = (MyScrollView) findViewById(R.id.home_scroll);
        String json = getIntent().getStringExtra("data");
        Gson gson = new Gson();

        object = gson.fromJson(json, Tender.class);
        if (object == null)
            finish();
        setTitle("Edit Tender");
        id = object.getId();
        FGetAllCountry();
        FGetCategory();
        city.setText(object.getCity());
        title.setText(object.getTenderName());
        description.setText(object.getDescription());
        link.setText(object.getDescriptionLink());
        Log.e(TAG, "onCreate: " + object);
//        Toast.makeText(this, "Contact Number= "+object.getContactNo(), Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onCreate: "+object);
//        Toast.makeText(this, "Contact Number= "+object.getContactNo(), Toast.LENGTH_SHORT).show();

        if (!TextUtils.isEmpty(object.getTenderPhoto())) {
            Picasso.with(EditTenderDetail.this)
                    .load(object.getTenderPhoto())


                    .into(tenderImage);
        } else {
            tenderImage.setBackground(getResources().getDrawable(R.drawable.avtar));
        }

        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (countryAdapter != null)
                    countryAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

        });
        tenderImage.setTag("0");
        tenderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSelfPermission();
                //SetProfile();
            }
        });




//        country.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (down_arrow.getVisibility() == View.VISIBLE) {
//                    down_arrow.performClick();
//                } else {
//                    up_arrow.performClick();
//                }
//            }
//        });

//        down_arrow.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("NewApi")
//            @Override
//            public void onClick(View v) {
//                country_home.setVisibility(View.VISIBLE);
//                up_arrow.setVisibility(View.VISIBLE);
//                down_arrow.setVisibility(View.INVISIBLE);
//                category_home.setVisibility(View.GONE);
//                up_arrow2.setVisibility(View.GONE);
//                down_arrow2.setVisibility(View.VISIBLE);
//                scrollView.setScrolling(false);
//            }
//        });


        btnUploadTender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidDetails()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        CallApi();
                    }
                    alpha.clear();
                    alpha2.clear();
                    countryName.clear();
                    categoryName.clear();
                }
//                alpha.clear();
//                alpha2.clear();
//                countryName.clear();
//                categoryName.clear();
            }
        });

//        up_arrow.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("NewApi")
//            @Override
//            public void onClick(View v) {
//                country_home.setVisibility(View.GONE);
//                up_arrow.setVisibility(View.INVISIBLE);
//                down_arrow.setVisibility(View.VISIBLE);
//                scrollView.setScrolling(true);
//            }
//        });

        down_arrow2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                category_home.setVisibility(View.VISIBLE);
                up_arrow2.setVisibility(View.VISIBLE);
                down_arrow2.setVisibility(View.GONE);
                country_home.setVisibility(View.GONE);
//                up_arrow.setVisibility(View.INVISIBLE);
//                down_arrow.setVisibility(View.VISIBLE);
                scrollView.setScrolling(false);
            }
        });

        up_arrow2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                category_home.setVisibility(View.GONE);
                up_arrow2.setVisibility(View.GONE);
                down_arrow2.setVisibility(View.VISIBLE);
                scrollView.setScrolling(true);
            }
        });

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (down_arrow2.getVisibility() == View.VISIBLE) {
                    down_arrow2.performClick();
                } else {
                    up_arrow2.performClick();
                }
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectCategory();
            }
        });

        down_arrow3.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (country.getText().toString().equals("") || category.getText().toString().equals("")) {
                    sessionManager.ShowDialog(EditTenderDetail.this, "First Select Country and Category");
                } else {
                    ll_contact_detail.setVisibility(View.VISIBLE);
//                    if (dialog != null)
//                        dialog.show();
                }
            }
        });

        up_arrow3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_contact_detail.setVisibility(View.GONE);
            }
        });

        rl_target_viewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ll_target_viewer.getVisibility() == View.VISIBLE) {
                    ll_target_viewer.setVisibility(View.GONE);
                    down_arrow_target_viewer.setVisibility(View.VISIBLE);
                    up_arrow_target_viewer.setVisibility(View.GONE);
                    scrollView.setScrolling(true);
                } else {
                    ll_target_viewer.setVisibility(View.VISIBLE);
                    down_arrow_target_viewer.setVisibility(View.GONE);
                    up_arrow_target_viewer.setVisibility(View.VISIBLE);

                    category_home.setVisibility(View.GONE);
                    up_arrow2.setVisibility(View.GONE);
                    down_arrow2.setVisibility(View.VISIBLE);

                    country_home.setVisibility(View.GONE);
//                    up_arrow.setVisibility(View.INVISIBLE);
//                    down_arrow.setVisibility(View.VISIBLE);
                    scrollView.setScrolling(false);
                }
            }
        });

        findViewById(R.id.rl_contact_tender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (down_arrow3.getVisibility() == View.VISIBLE) {
                    down_arrow3.setVisibility(View.GONE);
                    up_arrow3.setVisibility(View.VISIBLE);
                    down_arrow3.performClick();
                } else {
                    up_arrow3.performClick();
                    down_arrow3.setVisibility(View.VISIBLE);
                    up_arrow3.setVisibility(View.GONE);
                }
            }
        });

        spinner_targetViewer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                txt_target_viewer.setText(targetViewers.get(position));
                if (targetViewers.get(position).equalsIgnoreCase("everyone")) {
                    targetViewers1 = MultipartBody.Part.createFormData("targetViewers", new Gson().toJson(targetViewersId));
                } else {
                    List<String> tv = new ArrayList<>();
                    tv.add(targetViewersId.get(position));
                    targetViewers1 = MultipartBody.Part.createFormData("targetViewers", new Gson().toJson(tv));
                }
                ll_target_viewer.setVisibility(View.GONE);
                selectedTargetViewer = position;
                down_arrow_target_viewer.setVisibility(View.VISIBLE);
                up_arrow_target_viewer.setVisibility(View.GONE);
                scrollView.setScrolling(true);
            }
        });

        alpha.clear();
        alpha2.clear();
        countryName.clear();
        categoryName.clear();
//        GetAllCountry();
        GetCategory();
        initDialoge();

        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                countryname = countryAdapter.countryNameList.get(position).split("~")[0];
                countryCode = countryAdapter.countryNameList.get(position).split("~")[1];
//                countryCode=countryList.get(position).getCountryCode();
//                Toast.makeText(EditTenderDetail.this, ""+sessionManager.getPreferences(getApplicationContext(),"countryCode"), Toast.LENGTH_SHORT).show();
//                sessionManager.setPreferences(getApplicationContext(),"countryCode",countryList.get(position).getCountryCode());
//                Log.d(TAG, "onItemClick: "+countryCode +"\n CountryCode= "+countryList.get(position).getCountryCode());
//                Toast.makeText(EditTenderDetail.this, ""+countryList.get(position).getCountryCode() +"\nCountry Code=  "+ countryCode, Toast.LENGTH_SHORT).show();
                //String id1 = countryName.get(position).split("~")[2];
                ISO = countryAdapter.countryNameList.get(position).split("~")[3];
                String id1 = countryAdapter.countryNameList.get(position).split("~")[2];
                //countryId1 = MultipartBody.Part.createFormData("country", id1);
                if (countryCode != null) {
                    code.setText("+" + countryCode + "-");
//                    mobile.setText("");
                }
                countryId1 = MultipartBody.Part.createFormData("country", id1);

                country.setText(countryname);
                country_home.setVisibility(View.GONE);
                scrollView.setScrolling(true);
                up_arrow.setVisibility(View.GONE);
                down_arrow.setVisibility(View.VISIBLE);
            }
        });

        spinner2.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int position, long l, boolean b) {

                String id1 = categoryName.get(position).split("~")[1];
                if (categories.contains(id1)) {
                    categories.remove(id1);
                } else {
                    categories.add(id1);
                }

                categoryId1 = MultipartBody.Part.createFormData("category", id1);
                categoryname = alpha2.get(position).split("~")[0];
                category.setText(categoryname);
                category_home.setVisibility(View.GONE);
                scrollView.setScrolling(true);
                up_arrow2.setVisibility(View.GONE);
                down_arrow2.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });
        spinner2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String id1 = categoryName.get(position).split("~")[1];
                if (categories.contains(id1)) {
                    categories.remove(id1);
                } else {
                    categories.add(id1);
                }
                categoryId1 = MultipartBody.Part.createFormData("category", id1);
                categoryname = alpha2.get(position).split("~")[0];
                category.setText(categoryname);
                category_home.setVisibility(View.GONE);
                scrollView.setScrolling(true);
                up_arrow2.setVisibility(View.GONE);
                down_arrow2.setVisibility(View.VISIBLE);
            }
        });


        lvCountry.setDivider(null);
        lvCountry.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getAllTargetViewer();
    }

    private void checkSelfPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissionRequire = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            };
            Dexter.withActivity(EditTenderDetail.this)
                    .withPermissions(permissionRequire)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.isAnyPermissionPermanentlyDenied()) {
                                new AlertDialog.Builder(getApplicationContext())
                                        .setTitle("Need Permission")
                                        .setMessage("This app needs storage, camera permission to upload tender image")
                                        .setCancelable(false)
                                        .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intPermission = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                                intPermission.setData(uri);
                                                startActivityForResult(intPermission, 101);
                                            }
                                        }).show();
                            } else if (report.areAllPermissionsGranted()) {
                                SetProfile();
                            } else {
                                checkSelfPermission();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .withErrorListener(new PermissionRequestErrorListener() {
                        @Override
                        public void onError(DexterError error) {

                        }
                    }).check();
        }
    }

    private void initDialoge() {
//        dialog = new Dialog(EditTenderDetail.this);
//        dialog.setContentView(R.layout.contact_to_tender);
//        dialog.setCancelable(false);
        final Button dismissButton = (Button) findViewById(R.id.contact_save);
        mobile = (EditText) findViewById(R.id.contact_mobile);
        landline = (EditText) findViewById(R.id.contact_landline);
        email2 = (EditText) findViewById(R.id.contact_email);
        address = (EditText) findViewById(R.id.contact_address);
        final ImageView box = (ImageView) findViewById(R.id.home_box);
        final ImageView boxright = (ImageView) findViewById(R.id.home_box_checked);
        cbFlowTender = (CheckBox) findViewById(R.id.cb_follow_tender);

        code = (TextView) findViewById(R.id.contact_code);
        contact_landline_code = (TextView) findViewById(R.id.contact_landline_code);
        Log.e(TAG, "initDialoge: " + object.getContactNo());
        if (TextUtils.isEmpty(object.getContactNo())) {
            mobile.setText("");
        } else {
            if(object.getContactNo() !=null && object.getContactNo().contains("-")){
                object.setContactNo(object.getContactNo().replace("-",""));
            }
            mobile.setText(object.getContactNo());
        }

        if (object.getLandlineNo() !=null && TextUtils.isEmpty(object.getLandlineNo())) {
            landline.setText("");
        } else {
            if(object.getLandlineNo().contains("-")){
                object.setLandlineNo(object.getLandlineNo().replace("-",""));
            }
            landline.setText(object.getLandlineNo());
        }

        if (TextUtils.isEmpty(object.getEmail())) {
            email2.setText("");
        } else {
            email2.setText(object.getEmail());
        }

        if (TextUtils.isEmpty(object.getAddress())) {
            address.setText("");
        } else {
            address.setText(object.getAddress());
        }

        if (object.getIsFollowTender()) {
            boxright.setVisibility(View.VISIBLE);
            box.setVisibility(View.GONE);
            dismissButton.setAlpha((float) 1);
            cbFlowTender.setChecked(true);
        } else {
            boxright.setVisibility(View.GONE);
            box.setVisibility(View.VISIBLE);
            dismissButton.setAlpha((float) 0.7);
            cbFlowTender.setChecked(false);
        }

        if (object.getIsFollowTenderLink()) {
            cbFollowTenderLink.setChecked(true);
        } else {
            cbFollowTenderLink.setChecked(false);
        }

        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boxright.setVisibility(View.VISIBLE);
                box.setVisibility(View.GONE);
                dismissButton.setAlpha((float) 1);
            }
        });
        boxright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boxright.setVisibility(View.GONE);
                box.setVisibility(View.VISIBLE);
                dismissButton.setAlpha((float) 0.7);
            }
        });
        if (countryCode != null) {
            code.setText("+" + countryCode + "-");
            mobile.setText("");
        }

        cbFollowTenderLink.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(link.getText().toString().isEmpty()){
                    Toast.makeText(EditTenderDetail.this, "Website link is empty", Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                }
            }
        });

        cbFlowTender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(tenderImage.getTag().equals("0")){
                    Toast.makeText(EditTenderDetail.this, "Please fill image", Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                }
            }
        });

        email2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Validation.isEmailAddress(email2, true);

            }
        });
        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Validation.isPhoneNumber(mobile, true);
            }
        });

        if (cbFlowTender.isChecked() ||
                !TextUtils.isEmpty(email2.getText().toString().trim()) ||
                !TextUtils.isEmpty(mobile.getText().toString().trim()) ||
                !TextUtils.isEmpty(address.getText().toString().trim()) ||
                !TextUtils.isEmpty(landline.getText().toString().trim())) {
//                    Toast.makeText(EditTenderDetail.this, "Mobile Number"+mobile.getText().toString(), Toast.LENGTH_SHORT).show();
            String e = email2.getText().toString();
            String m = !mobile.getText().toString().equals("") ? "-" + mobile.getText().toString() : "";
            String l = !landline.getText().toString().equals("")?"-"+landline.getText().toString():"";
            String a = address.getText().toString();

            email1 = MultipartBody.Part.createFormData("email", e);
//                    contactNo1 = MultipartBody.Part.createFormData("contactNo","+" + countryCode + "-"+ m);
            contactNo1 = MultipartBody.Part.createFormData("contactNo", m);
            landlineNo1 = MultipartBody.Part.createFormData("landlineNo", l);
            address1 = MultipartBody.Part.createFormData("address", a);
            isValidContactDetails = true;
        }


        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cbFlowTender.isChecked() ||
                        !TextUtils.isEmpty(email2.getText().toString().trim()) ||
                        !TextUtils.isEmpty(mobile.getText().toString().trim()) ||
                        !TextUtils.isEmpty(address.getText().toString().trim()) ||
                        !TextUtils.isEmpty(landline.getText().toString().trim())) {
//                    Toast.makeText(EditTenderDetail.this, "Mobile Number"+mobile.getText().toString(), Toast.LENGTH_SHORT).show();
                    String e = email2.getText().toString();
                    String m = !mobile.getText().toString().equals("") ? "-" + mobile.getText().toString() : "";
                    String l = !landline.getText().toString().equals("")?"-"+landline.getText().toString():"";
//                    String l = landline.getText().toString();
                    String a = address.getText().toString();

                    email1 = MultipartBody.Part.createFormData("email", e);
//                    contactNo1 = MultipartBody.Part.createFormData("contactNo","+" + countryCode + "-"+ m);
                    contactNo1 = MultipartBody.Part.createFormData("contactNo", m);
                    landlineNo1 = MultipartBody.Part.createFormData("landlineNo", l);
                    address1 = MultipartBody.Part.createFormData("address", a);

                    if (!TextUtils.isEmpty(mobile.getText())) {
                        try {
                            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
                            if (phoneNumberUtil.isValidNumberForRegion(phoneNumberUtil.parse(mobile.getText().toString(), ISO), ISO)) {
                                mobile.setError(null);
                                isValidContactDetails = true;
                                ll_contact_detail.setVisibility(View.GONE);
                                down_arrow3.setVisibility(View.VISIBLE);
                                up_arrow3.setVisibility(View.GONE);
                            } else {
                                mobile.requestFocus();
                                mobile.setError("Enter valid mobile number");
                                isValidContactDetails = false;
                            }
                        } catch (NumberParseException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        isValidContactDetails = true;
                        ll_contact_detail.setVisibility(View.GONE);
                        down_arrow3.setVisibility(View.VISIBLE);
                        up_arrow3.setVisibility(View.GONE);
                    }

//                    down_arrow3.setVisibility(View.VISIBLE);
//                    up_arrow3.setVisibility(View.GONE);
//                    ll_contact_detail.setVisibility(View.GONE);
                } else {
                    sessionManager.ShowDialog(EditTenderDetail.this, "please fill at least one information");
                }
            }
        });
    }

    private void onSelectCategory() {
        Log.e(TAG, "onItemClick: selected : " + new Gson().toJson(categoryAdapter.getSelected()));

        String categories = "";
        int position = 0;
        List<Boolean> selected = categoryAdapter.getSelected();
        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(i)) {
                position = i;
//                categories.add(categoryName.get(i).split("~")[1]);
                categories += (categoryName.get(i).split("~")[1] + ",");
            }
        }
        if (TextUtils.isEmpty(categories)) {
            Toast.makeText(EditTenderDetail.this, "Please select any category", Toast.LENGTH_SHORT).show();
        } else {
            category_home.setVisibility(View.GONE);
            scrollView.setScrolling(true);
            up_arrow2.setVisibility(View.GONE);
            down_arrow2.setVisibility(View.VISIBLE);

            categories = categories.substring(0, categories.lastIndexOf(","));
            if (categories.split(",").length > 1) {
                category.setText(categories.split(",").length + " category selected");
            } else if (categories.split(",").length == 1) {
                category.setText(alpha2.get(position).split("~")[0]);
            } else {
                category.setText("Select Category");
            }

            categoryId1 = MultipartBody.Part.createFormData("category", categories);
            Log.e(TAG, "onClick: category multipart : " + categoryId1.toString());
            Log.e(TAG, "onItemClick: selected : " + new Gson().toJson(categories));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void FGetCategory() {
        showProgressDialog();

        mApiService.getCategoryData().enqueue(new Callback<ArrayList<GetCategory>>() {
            @Override
            public void onResponse(Call<ArrayList<GetCategory>> call, Response<ArrayList<GetCategory>> response) {
                categoryList = response.body();
                dismissProgressDialog();
                List<String> lstCategoryID = new ArrayList<>();
                if (categoryList != null && !categoryList.isEmpty() && categoryList.size() > 0) {
                    for (int i = 0; i < categoryList.size(); i++) {
                        for (String _categoryID : object.getCategory()) {
                            if (_categoryID.contains(categoryList.get(i).getId())) {
                                lstCategoryID.add(_categoryID);
                            }
                        }
                    }
                }
                String strCategoryID = TextUtils.join(",", lstCategoryID);
                Log.e(TAG, "strCategoryID: " + strCategoryID);
                if (TextUtils.isEmpty(strCategoryID)) {
                    Toast.makeText(EditTenderDetail.this, "Please select any category", Toast.LENGTH_SHORT).show();
                } else {

//                    strCategoryID = strCategoryID.substring(0, strCategoryID.lastIndexOf(","));
                    if (strCategoryID.split(",").length > 1) {
                        category.setText(strCategoryID.split(",").length + " category selected");
                    } else if (strCategoryID.split(",").length == 1) {
                        for (GetCategory category1 : categoryList) {
                            if (category1.getId().contains(strCategoryID)) {
                                category.setText(category1.getCategoryName());
                            }
                        }
//                        category.setText(alpha2.get(0).split("~")[0]);
                    } else {
                        category.setText("Select Category");
                    }

                    categoryId1 = MultipartBody.Part.createFormData("category", strCategoryID);
                    Log.e(TAG, "onItemClick: selected : " + new Gson().toJson(strCategoryID));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetCategory>> call, Throwable t) {
                dismissProgressDialog();
            }
        });
    }

    private void FGetAllCountry() {
        showProgressDialog();

        mApiService.getCountryData().enqueue(new Callback<ArrayList<GetCountry>>() {
            @Override
            public void onResponse(Call<ArrayList<GetCountry>> call, Response<ArrayList<GetCountry>> response) {
                dismissProgressDialog();
                countryList = response.body();

                if (countryList != null && !countryList.isEmpty() && countryList.size() > 0) {
                    for (int i = 0; i < countryList.size(); i++) {
                        if (countryList.get(i).getId().equalsIgnoreCase(object.getCountry())) {
                            country.setText(countryList.get(i).getCountryName());
//                       //     countryCode=countryList.get(i).getCountryCode();
////                            sessionManager.setPreferences(getApplicationContext(),"countryCode",countryCode);
                            clientCountry=countryList.get(i).getCountryName();
                            ISO=countryList.get(i).getIsoCode();
                            countryname=countryList.get(i).getCountryName();
                            code.setText("+"+countryList.get(i).getCountryCode()+"-");
                            contact_landline_code.setText("+"+countryList.get(i).getCountryCode()+"-");
//                            Log.e("clientCountry: ", clientCountry);
                            break;
                        }
                    }
                }


                Data = response.body();
                try {
                    if (Data != null && Data.size() > 0 && response.body() != null) {
                        for (int i = 0; i < Data.size(); i++) {
                            alpha.add(response.body().get(i).getCountryName() + "~" + response.body().get(i).getImageString() + "~" + response.body().get(i).getId() + "~" + countryList.get(i).getIsoCode() + "~" + countryList.get(i).getCountryCode());
                            countryName.add(response.body().get(i).getCountryName() + "~" + response.body().get(i).getCountryCode() + "~" + response.body().get(i).getId());
                        }
                        Collections.sort(alpha);
                        Collections.sort(countryName);
                        countryAdapter = new CustomList(EditTenderDetail.this, alpha, false);
                        spinner.setAdapter(countryAdapter);
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetCountry>> call, Throwable t) {
                dismissProgressDialog();
            }
        });

    }

    public static File createPictureFile(String fileName) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        File picture = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + BuildConfig.APPLICATION_ID,
                fileName);

        File dirFile = picture.getParentFile();
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        return picture;
    }

    private void SetProfile() {
        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*");

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPictureUri = Uri.fromFile(createPictureFile("picture.png"));

        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPictureUri);

        Intent chooserIntent = Intent.createChooser(pickIntent,
                getString(R.string.take_or_select_a_picture));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                new Intent[]{takePhotoIntent});

        startActivityForResult(chooserIntent, REQUEST_CODE_SELECT_PICTURE);
    }

    //-working on add Subscription,-Research for Payment with paypal in android,-starting integrating payment with paypal in application.
    private void CallApi() {
        String City = city.getText().toString();
        String Title = title.getText().toString();
        String Des = description.getText().toString();
        String Link = link.getText().toString();
        if (City.equals("") || Title.equals("")) {
            sessionManager.ShowDialog(EditTenderDetail.this, "fill all detail");
            return;
        } else {
            city1 = MultipartBody.Part.createFormData("city", City);
            name1 = MultipartBody.Part.createFormData("tenderName", Title);
            description1 = MultipartBody.Part.createFormData("description", Des);
            descriptionLink = MultipartBody.Part.createFormData("descriptionLink", Link);

        }
        token = "Bearer " + sessionManager.getPreferences(EditTenderDetail.this, "token");
        id = object.getId();

        if (image1 == null) {
            image1 = MultipartBody.Part.createFormData("image", "");
        }
//        if (cbFlowTender.isSelected()) {
//            isFollowTendeer1 = MultipartBody.Part.createFormData("isFollowTender", "true");
//        } else {
//            isFollowTendeer1 = MultipartBody.Part.createFormData("isFollowTender", "false");
//        }

        isFollowTendeer1 = MultipartBody.Part.createFormData("isFollowTender", String.valueOf(cbFlowTender.isChecked()));
        isFollowTenderLink = MultipartBody.Part.createFormData("isFollowTenderLink", String.valueOf(cbFollowTenderLink.isChecked()));


//        if (cbFollowTenderLink.isSelected()) {
//            isFollowTenderLink = MultipartBody.Part.createFormData("isFollowTenderLink", "true");
//        } else {
//            isFollowTenderLink = MultipartBody.Part.createFormData("isFollowTenderLink", "false");
//        }
        if (countryId1 == null)
            countryId1 = MultipartBody.Part.createFormData("country", object.getCountry());


        if (targetViewers1 == null)
            targetViewers1 = MultipartBody.Part.createFormData("targetViewers", new Gson().toJson(object.getTargetViewers()));
        showProgressDialog();

        email1 = MultipartBody.Part.createFormData("email", email2.getText().toString());


        contactNo1 = MultipartBody.Part.createFormData("contactNo", mobile.getText().toString());


        landlineNo1 = MultipartBody.Part.createFormData("landlineNo", landline.getText().toString());

        address1 = MultipartBody.Part.createFormData("address", address.getText().toString());


        mApiService.updateTender(token, id, email1, name1, city1, description1, descriptionLink, contactNo1, landlineNo1, address1, countryId1, categoryId1, isFollowTendeer1, isFollowTenderLink, image1, targetViewers1)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.i(TAG, "response---" + response.body());
                        if (response.isSuccessful()) {

                            if (arrInterestedContractor != null && arrInterestedContractor.length() > 0) {
                                sendEmailOnInterested();
                            } else {
                                dismissProgressDialog();
                                final AlertDialog alertDialog = new AlertDialog.Builder(EditTenderDetail.this).create();
                                alertDialog.setTitle("Tender Watch");
                                alertDialog.setMessage("Tender successfully amended!");
                                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(EditTenderDetail.this, ClientDrawer.class);
                                        startActivity(intent);
                                    }
                                });
                                alertDialog.show();
                            }

                        } else {
                            dismissProgressDialog();
                            Toast.makeText(EditTenderDetail.this, "something wrong please try again", Toast.LENGTH_SHORT).show();
                        }
                        Log.i(TAG, "response---" + response.body());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        dismissProgressDialog();
                        Log.i(TAG, "response---" + t);
                    }
                });

    }

    private void sendEmailOnInterested() {
        String token = "Bearer " + sessionManager.getPreferences(EditTenderDetail.this, "token");
        Log.e("token: ", token);
        Object user = sessionManager.getPreferencesObject(EditTenderDetail.this);
        String userName = ((User) user).getFirstName() + ((User) user).getLastName();
        int length = clientCategory.length();
        //Check whether or not the string contains at least four characters; if not, this method is useless
        clientCategory = clientCategory.substring(0, length - 4) + " . ";
        JsonObject gsonObject = new JsonObject();
        try {
            //contractorEmailList.add("tenderwatch01@gmail.com");
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("Email", arrInterestedContractor);
            jsonObj.put("Category", clientCategory);
            jsonObj.put("TenderName", object.getTenderName());
            jsonObj.put("CountryName", countryname);
            jsonObj.put("UserName", userName);
            jsonObj.put("Operation", "Edit");

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(jsonObj.toString());

            //print parameter
            Log.e("Parameter:  ", "AS PARAMETER  " + gsonObject);
            Log.e("Parameter:  ", "token" + token);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mApiService.sendEmail(token, gsonObject).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
                final AlertDialog alertDialog = new AlertDialog.Builder(EditTenderDetail.this).create();
                alertDialog.setTitle("Tender Watch");
                alertDialog.setMessage("Tender successfully amended,Notification of amendment has been sent to all Interested Contractors!");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(EditTenderDetail.this, ClientDrawer.class);
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

    private void GetCategory() {
        showProgressDialog();

        mApiService.getCategoryData().enqueue(new Callback<ArrayList<GetCategory>>() {
            @Override
            public void onResponse(Call<ArrayList<GetCategory>> call, Response<ArrayList<GetCategory>> response) {
                _categoryList = response.body();
                dismissProgressDialog();
                for (int i = 0; i < _categoryList.size(); i++) {
                    alpha2.add(response.body().get(i).getCategoryName() + "~" + response.body().get(i).getImgString());
                    categoryName.add(response.body().get(i).getCategoryName() + "~" + response.body().get(i).getId());

                }
                categoryAdapter = new CustomList(EditTenderDetail.this, alpha2, true);
                spinner2.setAdapter(categoryAdapter);
//                for (int i = 0; i < Data2.size(); i++) {
//                    if (response.body().get(i).getId().equalsIgnoreCase(object.getCategory().get(0))) {
//
//                        spinner2.setItemChecked(i, true);
////                        spinner2.setSelection(i);
//                    }
//
//                }

                List<String> _lstCategoryID = new ArrayList<>();
                if (_categoryList != null && !_categoryList.isEmpty() && _categoryList.size() > 0) {
                    for (int i = 0; i < _categoryList.size(); i++) {
                        for (String _categoryID : object.getCategory()) {
                            if (_categoryID.contains(_categoryList.get(i).getId())) {
                                spinner2.setItemChecked(i, true);
                                _lstCategoryID.add(_categoryID);
                            }
                        }
                    }
                }
                lvCountry.setAdapter(categoryAdapter);

                categoryId1 = MultipartBody.Part.createFormData("category", TextUtils.join(",", object.getCategory()));
            }

            @Override
            public void onFailure(Call<ArrayList<GetCategory>> call, Throwable t) {
                dismissProgressDialog();
            }
        });
    }

    private void GetAllCountry() {
        showProgressDialog();

        mApiService.getCountryData().enqueue(new Callback<ArrayList<GetCountry>>() {
            @Override
            public void onResponse(Call<ArrayList<GetCountry>> call, Response<ArrayList<GetCountry>> response) {
                dismissProgressDialog();
                Data = response.body();
                try {
                    if (Data != null && Data.size() > 0 && response.body() != null) {
                        for (int i = 0; i < Data.size(); i++) {
                            alpha.add(response.body().get(i).getCountryName() + "~" + response.body().get(i).getImageString() + "~" + response.body().get(i).getId() + "~" + countryList.get(i).getIsoCode());
                            countryName.add(response.body().get(i).getCountryName() + "~" + response.body().get(i).getCountryCode() + "~" + response.body().get(i).getId());
                        }
                        Collections.sort(alpha);
                        Collections.sort(countryName);
                        countryAdapter = new CustomList(EditTenderDetail.this, alpha, false);
                        spinner.setAdapter(countryAdapter);
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<GetCountry>> call, Throwable t) {
                dismissProgressDialog();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQUEST_CODE_SELECT_PICTURE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Uri selectedImageUri = null;
                        if (data != null) {
                            selectedImageUri = data.getData();
                        }
                        if (selectedImageUri == null) {
                            selectedImageUri = mPictureUri;
                        }
                        final Uri finalSelectedImageUri = selectedImageUri;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(EditTenderDetail.this, ZoomCropImageActivity.class);
                                intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_URI, finalSelectedImageUri);
                                intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_OUTPUT_WIDTH, PICTURE_WIDTH);
                                intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_OUTPUT_HEIGHT, PICTURE_HEIGHT);

                                intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_CROP_SHAPE, CropShape.SHAPE_RECTANGLE);   //optional
                                File croppedPicture = createPictureFile("cropped.png");
                                if (croppedPicture != null) {
                                    intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_SAVE_DIR,
                                            croppedPicture.getParent());   //optional
                                    intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_FILE_NAME,
                                            croppedPicture.getName());   //optional
                                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), croppedPicture);
                                    image1 = MultipartBody.Part.createFormData("image", croppedPicture.getName(), requestFile);
                                }
                                startActivityForResult(intent, REQUEST_CODE_CROP_PICTURE);
                            }
                        },1000);

                        break;
                }
                break;
            case REQUEST_CODE_CROP_PICTURE:
                if (requestCode == 1) {

                    if (resultCode == Activity.RESULT_CANCELED) {
                        //Write your code if there's no result
                    }
                }
                switch (resultCode) {

                    case ZoomCropImageActivity.CROP_SUCCEEDED:

                        if (data != null) {
                            Uri croppedPictureUri = data
                                    .getParcelableExtra(ZoomCropImageActivity.INTENT_EXTRA_URI);
                            tenderImage.setImageURI(null);
                            tenderImage.setImageURI(croppedPictureUri);
                            tenderImage.setTag("UpdatedTag");

                            try {

                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), croppedPictureUri);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case ZoomCropImageActivity.CROP_CANCELLED:
                    case ZoomCropImageActivity.CROP_FAILED:
                        break;
                }
                break;
        }
    }

    private void getAllTargetViewer() {
        showProgressDialog();
        mApiService.getAllSubscriptionCategory().enqueue(new Callback<List<SubscriptionCategoryResponse>>() {
            @Override
            public void onResponse(Call<List<SubscriptionCategoryResponse>> call, Response<List<SubscriptionCategoryResponse>> response) {
                Log.e(TAG, "onResponse: ");
                dismissProgressDialog();
                if (response.isSuccessful()) {

                    if (response.body() != null && response.body().size() > 0) {
                        targetViewers.clear();
                        targetViewersId.clear();
                        for (int i = 0; i < response.body().size(); i++) {
                            targetViewers.add(response.body().get(i).getName());
                            targetViewersId.add(response.body().get(i).getId());
                        }

                        if (object.getTargetViewers().size() > 1) {
                            txt_target_viewer.setText("Everyone");
                            targetViewers1 = MultipartBody.Part.createFormData("targetViewers", new Gson().toJson(targetViewersId));
                        } else {
                            for (int i = 0; i < response.body().size(); i++) {
                                if (object.getTargetViewers().size() > 0) {
                                    if (response.body().get(i).getId().equalsIgnoreCase(object.getTargetViewers().get(0).getId())) {
                                        txt_target_viewer.setText(response.body().get(i).getName());
                                        List<String> id = new ArrayList<>();
                                        id.add(response.body().get(i).getId());
                                        targetViewers1 = MultipartBody.Part.createFormData("targetViewers", new Gson().toJson(id));
                                        break;
                                    }
                                }


                            }
                        }

                        targetViewers.add("Everyone");
                        spinner_targetViewer.setAdapter(new android.widget.ArrayAdapter<String>(EditTenderDetail.this, android.R.layout.simple_list_item_1, targetViewers));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SubscriptionCategoryResponse>> call, Throwable t) {
                Log.e(TAG, "onFailure: ");
            }
        });
    }

    public void showProgressDialog() {
        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading....");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing() && !mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        if (!this.isFinishing() && mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    private boolean isValidDetails() {

        if (countryname == null) {
            Toast.makeText(this, "please select any country", Toast.LENGTH_SHORT).show();
            scrollView.scrollTo(0, scrollView.getTop());
            return false;
        }

//        if (categoryname == null) {
//            Toast.makeText(this, "please select any category", Toast.LENGTH_SHORT).show();
//            scrollView.scrollTo(0, scrollView.getTop());
//            return false;
//        }

        if (targetViewers1 == null) {
            Toast.makeText(this, "please select target viewers", Toast.LENGTH_SHORT).show();
            scrollView.scrollTo(0, spinner_targetViewer.getTop());
            return false;
        }

        if (TextUtils.isEmpty(city.getText())) {
            city.setError("Enter city name");
            city.requestFocus();
            return false;
        } else {
            city.setError(null);
        }

        if (TextUtils.isEmpty(title.getText())) {
            title.setError("Enter tender title");
            title.requestFocus();
            return false;
        } else {
            title.setError(null);
        }

        if (TextUtils.isEmpty(link.getText()) && TextUtils.isEmpty(description.getText())) {
            description.setError("Enter either description or Website link");
            description.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(link.getText()) || TextUtils.isEmpty(description.getText())) {
            description.setError(null);
            if (!TextUtils.isEmpty(link.getText())) {
                boolean isLink = checkURL(link.getText().toString());
                if (!isLink) {
                    link.setError("Enter Valid Website Link");
                    link.requestFocus();
                    return false;
                } else {
                    link.setError(null);
                }

            }


        }

//        if (TextUtils.isEmpty(link.getText()) || TextUtils.isEmpty(description.getText())) {
//
//            boolean isLink = checkURL(link.getText().toString());
//            if (!isLink) {
//                link.setError("Enter Valid Website Link");
//                link.requestFocus();
//                return false;
//            } else {
//                link.setError(null);
//            }
//
//
//        }
//
//        if (TextUtils.isEmpty(link.getText()) || TextUtils.isEmpty(description.getText())) {
//            description.setError("Enter tender description");
//            description.requestFocus();
//            return false;
//        } else {
//            description.setError(null);
//        }

        if (!isValidContactDetails) {
            Toast.makeText(this, "Please fill any detail to contact", Toast.LENGTH_SHORT).show();
            ll_contact_detail.setVisibility(View.VISIBLE);
            down_arrow3.setVisibility(View.GONE);
            up_arrow3.setVisibility(View.VISIBLE);
            return false;
        }

        return true;
    }

    public boolean checkURL(String input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        Pattern URL_PATTERN = Patterns.WEB_URL;
        boolean isURL = URL_PATTERN.matcher(input).matches();
        if (!isURL) {
            String urlString = input + "";
            if (URLUtil.isNetworkUrl(urlString)) {
                try {
                    new URL(urlString);
                    isURL = true;
                } catch (Exception e) {
                }
            }
        }
        return isURL;
    }
}
