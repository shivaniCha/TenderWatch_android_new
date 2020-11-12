package com.tenderWatch.ClientDrawer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.crazyorr.zoomcropimage.CropShape;
import com.github.crazyorr.zoomcropimage.ZoomCropImageActivity;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import com.tenderWatch.Adapters.ArrayAdapter;
import com.tenderWatch.Adapters.CustomList;
import com.tenderWatch.BuildConfig;
import com.tenderWatch.Models.GetCategory;
import com.tenderWatch.Models.GetCountry;
import com.tenderWatch.Models.SubscriptionCategoryResponse;
import com.tenderWatch.Models.UploadTender;
import com.tenderWatch.Models.User;
import com.tenderWatch.R;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;
import com.tenderWatch.Validation.MyScrollView;
import com.tenderWatch.Validation.Validation;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

/**
 * Created by lcom48 on 14/12/17.
 */

public class Home extends Fragment implements AdapterView.OnItemSelectedListener {

    private Api mApiService;
    private static final ArrayList<String> alpha = new ArrayList<String>();
    private static final ArrayList<String> countryName = new ArrayList<String>();
    private static final ArrayList<String> alpha2 = new ArrayList<String>();
    private static final ArrayList<String> categoryName = new ArrayList<String>();
    private static final String TAG = Home.class.getSimpleName();
    private ArrayList<String> targetViewers = new ArrayList<>();
    private ArrayList<String> targetViewersId = new ArrayList<>();
    RelativeLayout rlCountry, rlCategory, rlContact, rl_target_viewer;
    ArrayList<GetCountry> countryList;
    ArrayList<GetCategory> categoryList;
    CustomList countryAdapter, categoryAdapter;
    ListView spinner, spinner2, spinner_targetViewer;
    private ImageView down_arrow, up_arrow, down_arrow2, up_arrow2, down_arrow3, up_arrow3, tenderImage, down_arrow_target_viewer, up_arrow_target_viewer;
    LinearLayout country_home, category_home, llContactDetail, ll_target_viewer;
    TextView country, category, txt_target_viewer;
    String countryCode, categoryname, countryname, follow = "false";
    SessionManager sessionManager;
    MyScrollView scrollView;
    MultipartBody.Part name1, email1, countryId1, image1, categoryId1, landlineNo1, contactNo1, city1,
            description1, address1, isFollowTenderLink, isFollowTendeer1, tenderPhono1,
            targetViewers1, descriptionLink;

    EditText city, title, description, edtSearch;
    Button btnUploadTender;
    TextView btnDone;
    private Uri mPictureUri;
    private static final int PICTURE_WIDTH = 1100;
    private static final int PICTURE_HEIGHT = 600;
    private int selectedTargetViewer = -1;
    private static final String KEY_PICTURE_URI = "KEY_PICTURE_URI";

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private static final int REQUEST_CODE_SELECT_PICTURE = 108;
    private static final int REQUEST_CODE_CROP_PICTURE = 109;
    private ProgressDialog mProgressDialog;
    private boolean isValidContactDetails = false;
    private String ISO = "";

    Button dismissButton;
    EditText mobile;
    EditText landline;
    EditText email2;
    EditText address;
    EditText link;
    ImageView box;
    ImageView boxright;
    TextView code;
    TextView contact_landline_code;
    private CheckBox cbFlowTender, cbFollowTenderLink;
    User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload_teander, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getPreferencesObject(getActivity());
        getActivity().setTitle("New Tender");
        spinner = view.findViewById(R.id.spinner);
        spinner2 = view.findViewById(R.id.spinner3);
        spinner_targetViewer = view.findViewById(R.id.spinner_targetViewer);
        spinner_targetViewer.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        spinner2.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        btnDone = view.findViewById(R.id.btn_done_category);

        edtSearch = view.findViewById(R.id.edtSearch);
        llContactDetail = view.findViewById(R.id.ll_contact_detail);

        city = view.findViewById(R.id.home_city);
        title = view.findViewById(R.id.home_title);
        description = view.findViewById(R.id.home_address);
        link = view.findViewById(R.id.home_link);
        btnUploadTender = view.findViewById(R.id.btn_uploadTender);

        down_arrow = view.findViewById(R.id.down_arrow);
        up_arrow = view.findViewById(R.id.up_arrow);
        down_arrow2 = view.findViewById(R.id.down_arrow2);
        up_arrow2 = view.findViewById(R.id.up_arrow2);
        down_arrow3 = view.findViewById(R.id.down_arrow3);
        up_arrow3 = view.findViewById(R.id.up_arrow3);
        tenderImage = view.findViewById(R.id.tender_image);
        country_home = view.findViewById(R.id.country_home);
        category_home = view.findViewById(R.id.category_home);
        country = view.findViewById(R.id.txt_home_country_name);
        category = view.findViewById(R.id.txt_contact_category_name);
        mApiService = ApiUtils.getAPIService();
        spinner.setOnItemSelectedListener(this);
        scrollView = view.findViewById(R.id.home_scroll);
        txt_target_viewer = view.findViewById(R.id.txt_target_viewer);
        down_arrow_target_viewer = view.findViewById(R.id.down_arrow_target_viewer);
        up_arrow_target_viewer = view.findViewById(R.id.up_arrow_target_viewer);

        dismissButton =  view.findViewById(R.id.contact_save);
        mobile =  view.findViewById(R.id.contact_mobile);
        landline =  view.findViewById(R.id.contact_landline);
        email2 =  view.findViewById(R.id.contact_email);
        address =  view.findViewById(R.id.contact_address);
        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (null != address.getLayout() && address.getLayout().getLineCount() > 4) {
                    address.getText().delete(address.getText().length() - 1, address.getText().length());
                }
            }
        });

        box = (ImageView) view.findViewById(R.id.home_box);
        boxright = (ImageView) view.findViewById(R.id.home_box_checked);
        code = (TextView) view.findViewById(R.id.contact_code);
        contact_landline_code = (TextView) view.findViewById(R.id.contact_landline_code);

        rlCategory = view.findViewById(R.id.rl_category);
        rlContact = view.findViewById(R.id.rl_contact);
        rlCountry = view.findViewById(R.id.rl_country);

        rl_target_viewer = view.findViewById(R.id.rl_target_viewer);
        ll_target_viewer = view.findViewById(R.id.ll_target_viewer);

        cbFlowTender = view.findViewById(R.id.cb_follow_tender);
        cbFollowTenderLink = view.findViewById(R.id.cb_follow_tenderlink);
        tenderImage.setTag("0");
        tenderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSelfPermission();
            }
        });

        cbFollowTenderLink.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(link.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Website link is empty", Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                }
            }
        });

        cbFlowTender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if(tenderImage.getTag().equals("0")){
                   Toast.makeText(getContext(), "Please fill image", Toast.LENGTH_SHORT).show();
                   compoundButton.setChecked(false);
               }
            }
        });



        down_arrow.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                /*country_home.setVisibility(View.VISIBLE);
                up_arrow.setVisibility(View.VISIBLE);
                down_arrow.setVisibility(View.INVISIBLE);
                category_home.setVisibility(View.GONE);
                up_arrow2.setVisibility(View.GONE);
                down_arrow2.setVisibility(View.VISIBLE);
                scrollView.setScrolling(false);*/
                // homeScroll.setScrollbarFadingEnabled(false);
            }
        });

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
            }
        });

        up_arrow.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                /*country_home.setVisibility(View.GONE);
                up_arrow.setVisibility(View.INVISIBLE);
                down_arrow.setVisibility(View.VISIBLE);
                scrollView.setScrolling(true);*/
            }
        });
        down_arrow2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                /*category_home.setVisibility(View.VISIBLE);
                up_arrow2.setVisibility(View.VISIBLE);
                down_arrow2.setVisibility(View.GONE);
                country_home.setVisibility(View.GONE);
                up_arrow.setVisibility(View.INVISIBLE);
                down_arrow.setVisibility(View.VISIBLE);
                scrollView.setScrolling(false);*/
                //homeScroll.setEnabled(false);
            }
        });

        up_arrow2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                /*category_home.setVisibility(View.GONE);
                up_arrow2.setVisibility(View.GONE);
                down_arrow2.setVisibility(View.VISIBLE);
                scrollView.setScrolling(true);*/
                onSelectCategory();
            }
        });



        rlCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (down_arrow.getVisibility() == View.VISIBLE) {
                    country_home.setVisibility(View.VISIBLE);
                    up_arrow.setVisibility(View.VISIBLE);
                    down_arrow.setVisibility(View.INVISIBLE);
                    category_home.setVisibility(View.GONE);
                    up_arrow2.setVisibility(View.GONE);
                    down_arrow2.setVisibility(View.VISIBLE);
                    scrollView.setScrolling(false);
                    ll_target_viewer.setVisibility(View.GONE);
                    up_arrow_target_viewer.setVisibility(View.GONE);
                    down_arrow_target_viewer.setVisibility(View.VISIBLE);
                } else {
                    country_home.setVisibility(View.GONE);
                    up_arrow.setVisibility(View.INVISIBLE);
                    down_arrow.setVisibility(View.VISIBLE);
                    scrollView.setScrolling(true);
                }
            }
        });

        rlCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (down_arrow2.getVisibility() == View.VISIBLE) {
                    category_home.setVisibility(View.VISIBLE);
                    up_arrow2.setVisibility(View.VISIBLE);
                    down_arrow2.setVisibility(View.GONE);
                    country_home.setVisibility(View.GONE);
                    up_arrow.setVisibility(View.INVISIBLE);
                    down_arrow.setVisibility(View.VISIBLE);
                    ll_target_viewer.setVisibility(View.GONE);
                    up_arrow_target_viewer.setVisibility(View.GONE);
                    down_arrow_target_viewer.setVisibility(View.VISIBLE);
                    scrollView.setScrolling(false);
                } else {
                    onSelectCategory();
                }
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
                    up_arrow.setVisibility(View.INVISIBLE);
                    down_arrow.setVisibility(View.VISIBLE);
                    scrollView.setScrolling(false);
                }
            }
        });

        email2.setText(user.getEmail());
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

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //submit

//                if (!email2.getText().toString().equals("") || !mobile.getText().toString().equals("") || !address.getText().toString().equals("") || !landline.getText().toString().equals("") || cbFlowTender.isChecked()) {
                if (!email2.getText().toString().equals("") || (!mobile.getText().toString().equals("") || !address.getText().toString().equals("") || !landline.getText().toString().equals("")))
                {
                    String e = email2.getText().toString();
//                        sessionManager.setPreferences(getContext(),"countryCode",countryCode);
//                    String m = !mobile.getText().toString().equals("") ? "+"+countryCode+"-" + mobile.getText().toString() : "";
                    String m = !mobile.getText().toString().equals("") ? "-" + mobile.getText().toString() : "";
//                    String m1 = !mobile.getText().toString().equals("") ? "+" +"-" + mobile.getText().toString() : "";
//                    String l = !landline.getText().toString().equals("")?"+"+countryCode+ "-"+landline.getText().toString():"";
                    String l = !landline.getText().toString().equals("")?"-"+landline.getText().toString():"";
                    String a = address.getText().toString();

                    email1 = MultipartBody.Part.createFormData("email", e);
                    contactNo1 = MultipartBody.Part.createFormData("contactNo", m);

                    landlineNo1 = MultipartBody.Part.createFormData("landlineNo", l);
                    address1 = MultipartBody.Part.createFormData("address", a);
                    //dialog.dismiss();
//if(email2.getText().equals("")){

                    if (!TextUtils.isEmpty(mobile.getText())) {
                        try {
                               PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
                            if (phoneNumberUtil.isValidNumberForRegion(phoneNumberUtil.parse(mobile.getText().toString(), ISO), ISO)) {
                                mobile.setError(null);
                                isValidContactDetails = true;
                                llContactDetail.setVisibility(View.GONE);
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
                        llContactDetail.setVisibility(View.GONE);
                        down_arrow3.setVisibility(View.VISIBLE);
                        up_arrow3.setVisibility(View.GONE);
                    }
                } else {
                    sessionManager.ShowDialog(getActivity(), "please fill at least one information");

                }
            }
        });

        rlContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (down_arrow3.getVisibility() == View.VISIBLE) {
                    if (countryname == null || category == null) {
                        sessionManager.ShowDialog(getActivity(), "First Select Country and Category");
                    } else {
                    /*final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.contact_to_tender);*/
                    /*final Button dismissButton = (Button) dialog.findViewById(R.id.contact_save);
                    final EditText mobile = (EditText) dialog.findViewById(R.id.contact_mobile);
                    final EditText landline = (EditText) dialog.findViewById(R.id.contact_landline);
                    final EditText email2 = (EditText) dialog.findViewById(R.id.contact_email);
                    final EditText address = (EditText) dialog.findViewById(R.id.contact_address);
                    final ImageView box = (ImageView) dialog.findViewById(R.id.home_box);
                    final ImageView boxright = (ImageView) dialog.findViewById(R.id.home_box_checked);
                    final TextView code = (TextView) dialog.findViewById(R.id.contact_code);*/
                        code.setText("+" + countryCode + "-");
                        contact_landline_code.setText("+" + countryCode + "-");
                        llContactDetail.setVisibility(View.VISIBLE);
                        down_arrow3.setVisibility(View.GONE);
                        up_arrow3.setVisibility(View.VISIBLE);
                        box.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boxright.setVisibility(View.VISIBLE);
                                box.setVisibility(View.GONE);
                                //dismissButton.setAlpha((float) 1);
                                follow = "true";
                            }
                        });
                        boxright.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boxright.setVisibility(View.GONE);
                                box.setVisibility(View.VISIBLE);
                                //dismissButton.setAlpha((float) 0.7);
                                follow = "false";
                            }
                        });

                        //dialog.show();
                    }
                } else {
                    llContactDetail.setVisibility(View.GONE);
                    down_arrow3.setVisibility(View.VISIBLE);
                    up_arrow3.setVisibility(View.GONE);
                }
            }
        });


        alpha.clear();
        alpha2.clear();
        countryName.clear();
        categoryName.clear();
        GetAllCountry(view);
        getAllTargetViewer();
        GetCategory(view);

        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (spinner.getAdapter() instanceof CustomList) {
                    countryname = countryAdapter.countryNameList.get(position).split("~")[0];
                    if(!countryname.equalsIgnoreCase("Tanzania")){
                        sessionManager.ShowDialog(getActivity(), "You can only select Tanzania..");
                        return;
                    }
//                    countryCode = countryAdapter.countryNameList.get(position).split("~")[1];
                    countryCode = countryAdapter.countryNameList.get(position).split("~")[3];
                    ISO = countryAdapter.countryNameList.get(position).split("~")[4];
                    String id1 = countryAdapter.countryNameList.get(position).split("~")[2];
                    countryId1 = MultipartBody.Part.createFormData("country", id1);
                    country.setText(countryname);
                    country_home.setVisibility(View.GONE);
                    scrollView.setScrolling(true);
                    up_arrow.setVisibility(View.GONE);
                    down_arrow.setVisibility(View.VISIBLE);
                }
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String id1 = categoryName.get(position).split("~")[1];
                categoryId1 = MultipartBody.Part.createFormData("category", id1);
                categoryname = alpha2.get(position).split("~")[0];
                category.setText(categoryname);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
                    Log.e(TAG, "onItemClick targetViewers: "+  new Gson().toJson(tv));
                    targetViewers1 = MultipartBody.Part.createFormData("targetViewers", new Gson().toJson(tv));
                }
                ll_target_viewer.setVisibility(View.GONE);
                selectedTargetViewer = position;
                down_arrow_target_viewer.setVisibility(View.VISIBLE);
                up_arrow_target_viewer.setVisibility(View.GONE);
                scrollView.setScrolling(true);
            }
        });

        /*spinner2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });*/

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectCategory();
            }
        });

    }

    private boolean isValidDetails() {

        if (countryname == null) {
            Toast.makeText(getContext(), "please select any country", Toast.LENGTH_SHORT).show();
            scrollView.scrollTo(0, scrollView.getTop());
            return false;
        }

        if (categoryname == null) {
            Toast.makeText(getContext(), "please select any category", Toast.LENGTH_SHORT).show();
            scrollView.scrollTo(0, scrollView.getTop());
            return false;
        }

        if (targetViewers1 == null) {
            Toast.makeText(getContext(), "please select target viewers", Toast.LENGTH_SHORT).show();
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

        String e = email2.getText().toString();
//                        sessionManager.setPreferences(getContext(),"countryCode",countryCode);
//                    String m = !mobile.getText().toString().equals("") ? "+"+countryCode+"-" + mobile.getText().toString() : "";
        String m = !mobile.getText().toString().equals("") ? "-" + mobile.getText().toString() : "";
//                    String m1 = !mobile.getText().toString().equals("") ? "+" +"-" + mobile.getText().toString() : "";
//                    String l = !landline.getText().toString().equals("")?"+"+countryCode+ "-"+landline.getText().toString():"";
        String l = !landline.getText().toString().equals("")?"-"+landline.getText().toString():"";
        String a = address.getText().toString();

        if (!isValidContactDetails) {
            if(email2.getText().toString().isEmpty() && mobile.getText().toString().isEmpty() && landline.getText().toString().isEmpty() && address.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please fill any detail to contact", Toast.LENGTH_SHORT).show();
                llContactDetail.setVisibility(View.VISIBLE);
                down_arrow3.setVisibility(View.GONE);
                up_arrow3.setVisibility(View.VISIBLE);
                return false;
            }
        }

        return true;
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
            Toast.makeText(getContext(), "Please select any category", Toast.LENGTH_SHORT).show();
        } else {
            category_home.setVisibility(View.GONE);
            scrollView.setScrolling(true);
            up_arrow2.setVisibility(View.GONE);
            down_arrow2.setVisibility(View.VISIBLE);

            categories = categories.substring(0, categories.lastIndexOf(","));
            if (categories.split(",").length > 1) {
                category.setText(categories.split(",").length + " category selected");
                categoryname = categories.split(",").length + " category selected";
            } else if (categories.split(",").length == 1) {
                category.setText(alpha2.get(position).split("~")[0]);
                categoryname = alpha2.get(position).split("~")[0];
            } else {
                category.setText("Select Category");
            }

            categoryId1 = MultipartBody.Part.createFormData("category", categories);
            Log.e(TAG, "onClick: category multipart : " + categoryId1.toString());
            Log.e(TAG, "onItemClick: selected : " + new Gson().toJson(categories));
        }

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

    private void checkSelfPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissionRequire = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            };
            Dexter.withActivity(getActivity())
                    .withPermissions(permissionRequire)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.isAnyPermissionPermanentlyDenied()) {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Need Permission")
                                        .setMessage("This app needs storage, camera permission to upload tender image")
                                        .setCancelable(false)
                                        .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intPermission = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
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

    private void SetProfile() {
        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*");

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPictureUri = Uri.fromFile(createPictureFile("picture"+ System.currentTimeMillis()+".png"));

        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPictureUri);

        Intent chooserIntent = Intent.createChooser(pickIntent,
                getString(R.string.take_or_select_a_picture));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                new Intent[]{takePhotoIntent});

        startActivityForResult(chooserIntent, REQUEST_CODE_SELECT_PICTURE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void CallApi() {
        String City = city.getText().toString();
        String Title = title.getText().toString();
        String Link = link.getText().toString();
        String Des = description.getText().toString();


        if (City.equals("") || Title.equals("")) {
            sessionManager.ShowDialog(getActivity(), "fill all detail");
            return;
        } else {
            city1 = MultipartBody.Part.createFormData("city", City);
            name1 = MultipartBody.Part.createFormData("tenderName", Title);
            description1 = MultipartBody.Part.createFormData("description", Des);
            descriptionLink = MultipartBody.Part.createFormData("descriptionLink", Link);

        }

        if(!email2.getText().toString().isEmpty()) {
            email1 = MultipartBody.Part.createFormData("email", email2.getText().toString());
        }

        if(!mobile.getText().toString().isEmpty()) {
            contactNo1 = MultipartBody.Part.createFormData("contactNo", mobile.getText().toString());
        }
        if(!landline.getText().toString().isEmpty()) {
            landlineNo1 = MultipartBody.Part.createFormData("landlineNo", landline.getText().toString());
        }
        if(address.getText().toString().isEmpty()) {
            address1 = MultipartBody.Part.createFormData("address", address.getText().toString());
        }

        isFollowTendeer1 = MultipartBody.Part.createFormData("isFollowTender", String.valueOf(cbFlowTender.isChecked()));

        if(cbFollowTenderLink.isChecked()){
            isFollowTenderLink = MultipartBody.Part.createFormData("isFollowTenderLink", String.valueOf(cbFollowTenderLink.isChecked()));
        }



        if (image1 == null) {
            image1 = MultipartBody.Part.createFormData("image", "");
        }
        if (selectedTargetViewer < 0) {
            sessionManager.ShowDialog(getActivity(), "Please select TargetViewers");
            return;
        }
        String token = "Bearer " + sessionManager.getPreferences(getActivity(), "token");
        showProgressDialog();
        Log.e(TAG, "CallApi targetViewers1: "+targetViewers1.toString() );
        Log.e(TAG, "CallApi categoryId1: "+categoryId1.toString() );
        mApiService.uploadTender(token, email1, name1, city1, description1,descriptionLink, contactNo1, landlineNo1, address1, countryId1, categoryId1, isFollowTendeer1,isFollowTenderLink, image1, targetViewers1)
                .enqueue(new Callback<UploadTender>() {
                    @Override
                    public void onResponse(Call<UploadTender> call, Response<UploadTender> response) {
                        dismissProgressDialog();
                        if (response.isSuccessful()) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            alertDialog.setTitle("Tender Watch");
                            alertDialog.setMessage("Your tender has been posted out to all potential Contractors. You will be notified when any Contractor is interested in your Tender");
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(getActivity(), ClientDrawer.class);
                                    startActivity(intent);
//                                    for(i=0;i<getActivity().getSupportFragmentManager().getBackStackEntryCount();i++) {
//                                        getActivity().getSupportFragmentManager().popBackStack();
//                                    }
                                }
                            });
                            alertDialog.show();
                            Log.i(TAG, "response---" + response.body());
                        } else {
                            Toast.makeText(getActivity(), "Fail to upload tender", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: error : " + response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<UploadTender> call, Throwable t) {
                        dismissProgressDialog();
                        Log.e(TAG, "onFailure: Error : " + t.getMessage());
                    }
                });

    }

    private void GetCategory(final View v) {
        showProgressDialog();

        mApiService.getCategoryData().enqueue(new Callback<ArrayList<GetCategory>>() {
            @Override
            public void onResponse(Call<ArrayList<GetCategory>> call, Response<ArrayList<GetCategory>> response) {
                dismissProgressDialog();

                categoryList = response.body();
                if (categoryList != null && !categoryList.isEmpty() && categoryList.size() > 0) {
                    for (int i = 0; i < categoryList.size(); i++) {
                        alpha2.add(categoryList.get(i).getCategoryName() + "~" + categoryList.get(i).getImgString());
                        categoryName.add(categoryList.get(i).getCategoryName() + "~" + categoryList.get(i).getId());
                    }
                    categoryAdapter = new CustomList(getContext(), alpha2, true);
                    spinner2.setAdapter(categoryAdapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetCategory>> call, Throwable t) {

            }
        });
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
                        targetViewers.add("Everyone");
                        spinner_targetViewer.setAdapter(new android.widget.ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, targetViewers));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SubscriptionCategoryResponse>> call, Throwable t) {
                Log.e(TAG, "onFailure: ");
            }
        });
    }

    private void GetAllCountry(final View v) {
        showProgressDialog();
        mApiService.getCountryData().enqueue(new Callback<ArrayList<GetCountry>>() {
            @Override
            public void onResponse(Call<ArrayList<GetCountry>> call, Response<ArrayList<GetCountry>> response) {
                dismissProgressDialog();

                countryList = response.body();
                if (countryList != null && !countryList.isEmpty() && countryList.size() > 0) {
                    for (int i = 0; i < countryList.size(); i++) {
                        alpha.add(countryList.get(i).getCountryName() + "~" + countryList.get(i).getImageString() + "~" + countryList.get(i).getId() + "~" + countryList.get(i).getCountryCode() + "~" + countryList.get(i).getIsoCode());
                        countryName.add(countryList.get(i).getCountryName() + "~" + countryList.get(i).getCountryCode() + "~" + countryList.get(i).getId());
                    }
                    Collections.sort(alpha);
                    Collections.sort(countryName);
                    countryAdapter = new CustomList(getContext(), alpha, false);
                    spinner.setAdapter(countryAdapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetCountry>> call, Throwable t) {
                dismissProgressDialog();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getItemAtPosition(position) != null) {
            String item = parent.getItemAtPosition(position).toString();

            // Showing selected spinner item
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
                                Intent intent = new Intent(getActivity(), ZoomCropImageActivity.class);
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
                                    //user.setProfilePhoto(croppedPicture);
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

                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), croppedPictureUri);

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

    public void showProgressDialog() {
        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading....");
        mProgressDialog.setCancelable(false);

        if (!getActivity().isFinishing() && !mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        try {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
        }catch (Exception e){
            e.printStackTrace();
        }
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
