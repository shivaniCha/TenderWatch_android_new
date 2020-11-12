package com.tenderWatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tenderWatch.Adapters.CustomList;
import com.tenderWatch.ClientDrawer.ClientDrawer;
import com.tenderWatch.Drawer.MainDrawer;
import com.tenderWatch.Models.GetCategory;
import com.tenderWatch.Models.GetCountry;
import com.tenderWatch.Models.IntrestedContractor;
import com.tenderWatch.Models.Sender;
import com.tenderWatch.Models.SubscriptionCategoryResponse;
import com.tenderWatch.Models.Tender;
import com.tenderWatch.Models.User;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;
import com.tenderWatch.utils.JustifyTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreviewTenderDetail extends AppCompatActivity {
    Api mApiService;
    private static final String TAG = PreviewTenderDetail.class.getSimpleName();
    String day, flag, clientCategory;
    Tender object;
    Bitmap Bflag, catBflag;
    ImageView flag3, imagetender, catFlag;

    ArrayList<GetCountry> countryList;
    ArrayList<GetCategory> categoryList;
    public List<Sender> intrestedContractors;
    TextView tenderTitle, Country, Category, ExpDay, City, Contact, LandLine, Email, Address, previewFollow, previewFollowTenderLink;
    private JustifyTextView Description, DescriptionLink;
    LinearLayout llEmail, llContact, llLandline, llAddress;
    Button removeTender, editTender;
    SessionManager sessionManager;
    private ProgressDialog mProgressDialog;
    private RecyclerView rvContractorCategory, rvTargetViewers;
    final String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private LinearLayout llInterested;
    private RecyclerView rvInterested;
    String clientCountry;
    List<String> contractorEmailList;
    String countryCode= "";
    CustomList countryAdapter;
    String country;
    private static final ArrayList<String> alpha = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_tender_detail);

        sessionManager = new SessionManager(PreviewTenderDetail.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Tender Detail");

        categoryList=new ArrayList<>();
        mApiService = ApiUtils.getAPIService();
        tenderTitle = (TextView) findViewById(R.id.preview_tender_title);
        Country = (TextView) findViewById(R.id.preview_country_name);
        Category = (TextView) findViewById(R.id.preview_category);
        ExpDay = (TextView) findViewById(R.id.preview_exp);
        Description = (JustifyTextView) findViewById(R.id.preview_description);
        DescriptionLink = (JustifyTextView) findViewById(R.id.preview_descriptionlink);
        City = (TextView) findViewById(R.id.preview_tender_city);
        Contact = (TextView) findViewById(R.id.preview_tender_mobile);
        LandLine = (TextView) findViewById(R.id.preview_tender_landline);
        Email = (TextView) findViewById(R.id.preview_tender_email);
        Address = (TextView) findViewById(R.id.preview_tender_address);
        llAddress = (LinearLayout) findViewById(R.id.ll_preview_address);
        llContact = (LinearLayout) findViewById(R.id.ll_preview_mobile);
        llLandline = (LinearLayout) findViewById(R.id.ll_preview_landline);
        llEmail = (LinearLayout) findViewById(R.id.ll_preview_email);
        removeTender = (Button) findViewById(R.id.remove_tender);
        editTender = (Button) findViewById(R.id.edit_tender);
        imagetender = (ImageView) findViewById(R.id.preview_tender_image);
        catFlag = (ImageView) findViewById(R.id.preview_catflag_image);
        rvContractorCategory = (RecyclerView) findViewById(R.id.rv_tender_detail_category);
        rvTargetViewers = (RecyclerView) findViewById(R.id.rv_target_viewer);
        previewFollow = (TextView) findViewById(R.id.preview_follow);
        previewFollowTenderLink = (TextView) findViewById(R.id.preview_follow_tender_link);
        llInterested = (LinearLayout) findViewById(R.id.ll_interested);
        rvInterested = (RecyclerView) findViewById(R.id.rv_interested);
        String json = getIntent().getStringExtra("data");

        Gson gson = new Gson();
        object = gson.fromJson(json, Tender.class);
        country=object.getCountry();
//        Toast.makeText(this, ""+object.getCountry(), Toast.LENGTH_SHORT).show();
        Calendar c = Calendar.getInstance();
        countryAdapter = new CustomList(getApplicationContext(), alpha, false);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        String formattedDate = df.format(c.getTime());

        Date startDateValue = null, endDateValue = null;
        try {
            startDateValue = new SimpleDateFormat("yyyy-MM-dd").parse(formattedDate);
            //  startDateValue = new SimpleDateFormat("yyyy-MM-dd").parse(tenderList.get(position).getCreatedAt().split("T")[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            endDateValue = new SimpleDateFormat("yyyy-MM-dd").parse(object.getExpiryDate().split("T")[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Date endDateValue = new Date(allTender.get(position).getExpiryDate().split("T")[0]);

        if (endDateValue != null && startDateValue != null) {
            long diff = endDateValue.getTime() - startDateValue.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = (hours / 24) + 1;
            ExpDay.setText(days + " days");
        }

        editTender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String jsonString = gson.toJson(object);

                Intent intent = new Intent(PreviewTenderDetail.this, EditTenderDetail.class);
                intent.putExtra("data", jsonString);
                intent.putExtra("clientCategory", clientCategory);
                for(int i=0;i<countryAdapter.getCount();i++)
                intent.putExtra("categories",countryAdapter.getSelected().get(i));
                if (intrestedContractors != null && intrestedContractors.size() > 0) {
                    intent = intent.putExtra("contractorEmail", (Serializable) intrestedContractors);
                    Log.e("contractorEmail: ", "" + intrestedContractors);
                }
                startActivity(intent);
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String jsonString = gson.toJson(object);
                String role = sessionManager.getPreferences(PreviewTenderDetail.this, "role");
                Intent intent;
                if (role.equals("client")) {
                    intent = new Intent(PreviewTenderDetail.this, ClientDrawer.class);
                } else {
                    intent = new Intent(PreviewTenderDetail.this, MainDrawer.class);
                }
                intent.putExtra("data", jsonString);
                startActivity(intent);
            }
        });
        removeTender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmRemove();
            }
        });

        if (!TextUtils.isEmpty(object.getTenderPhoto())) {
            Picasso.with(PreviewTenderDetail.this)
                    .load(object.getTenderPhoto())
                    .placeholder(R.drawable.avtar)
                    .error(R.drawable.avtar)
                    .resize(400, 400)
                    .into(imagetender, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            findViewById(R.id.pb_image_loader).setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            findViewById(R.id.pb_image_loader).setVisibility(View.GONE);
                        }
                    });

        }

        imagetender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intZoomImage = new Intent(PreviewTenderDetail.this, ZoomImageActivity.class);
                String name = object.getId() + object.getTenderName() + object.getId().substring(0, 5) + ".jpeg";
                /*ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(ContractotTenderDetail.this,imagetender, ViewCompat.getTransitionName(imagetender));
                startActivity(intZoomImage,options.toBundle());*/
                intZoomImage.putExtra("tenderImage", object.getTenderPhoto());
                intZoomImage.putExtra("tenderImageName", name);
                startActivity(intZoomImage);
            }
        });

        if (object.getIsFollowTender()) {
            previewFollow.setVisibility(View.VISIBLE);
        } else {
            previewFollow.setVisibility(View.GONE);
        }
        String txtImageFirst = getColoredSpanned("Follow Tender Process", "#000000");
        String txtImageOpenRoundBracket = getColoredSpanned("(", "#000000");
        String txtImagelink = getColoredSpanned("As in image above", "#007bF9");
        String txtImageCloseRoundBracket = getColoredSpanned(")", "#000000");
        previewFollow.setText(Html.fromHtml(txtImageFirst + " " + txtImageOpenRoundBracket + txtImagelink + txtImageCloseRoundBracket));


        previewFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    Intent intZoomImage = new Intent(PreviewTenderDetail.this, ZoomImageActivity.class);
                    String name = object.getId() + object.getTenderName() + object.getId().substring(0, 5) + ".jpeg";
                    intZoomImage.putExtra("tenderImageName", name);
                    intZoomImage.putExtra("tenderImage", object.getTenderPhoto());
                    startActivity(intZoomImage);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        Spannable word = new SpannableString("Follow Tender Link");
//
//        word.setSpan(new ForegroundColorSpan(Color.BLUE), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        previewFollowTenderLink.setText(word);
//        Spannable wordTwo = new SpannableString("Website link");
//
//        wordTwo.setSpan(new ForegroundColorSpan(Color.BLUE), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        previewFollowTenderLink.append(wordTwo);


        String txtLinkFirst = getColoredSpanned("Follow Tender Link", "#000000");
        String txtLinkOpenRoundBracket = getColoredSpanned("(", "#000000");
        String txtWebsitelink = getColoredSpanned("Website Link", "#007bF9");
        String txtLinkCloseRoundBracket = getColoredSpanned(")", "#000000");
        previewFollowTenderLink.setText(Html.fromHtml(txtLinkFirst + " " + txtLinkOpenRoundBracket + txtWebsitelink + txtLinkCloseRoundBracket));


        if (object.getIsFollowTenderLink()) {
            previewFollowTenderLink.setVisibility(View.VISIBLE);
        } else {

            previewFollowTenderLink.setVisibility(View.GONE);
        }

        previewFollowTenderLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(object.getDescriptionLink())) {
                    if (!object.getDescriptionLink().startsWith("http://") && !object.getDescriptionLink().startsWith("https://")) {
                        String url = "http://" + object.getDescriptionLink();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    } else {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(object.getDescriptionLink()));
                        startActivity(browserIntent);
                    }
                }
            }
        });

        GetCategory();
        GetAllCountry();
        FGetAllCountry();
        getAllIntrestedContractor(object.getId());
        tenderTitle.setText(object.getTenderName());
        if (TextUtils.isEmpty(object.getDescription())) {
            Description.setVisibility(View.GONE);
        } else {
            Description.setText(object.getDescription());
        }
        City.setText(object.getCity());
        String txtLandline="";
        if (TextUtils.isEmpty(object.getContactNo())) {
            llContact.setVisibility(View.GONE);
        } else {
            final String txtContact;
//            Toast.makeText(this, ""+sessionManager.getPreferences(getApplicationContext(),"countryCode"), Toast.LENGTH_SHORT).show();
            if (object.getContactNo().contains("+")) {

                txtContact = getColoredSpanned(object.getContactNo(), "#007bF9");

            } else {
                txtContact = getColoredSpanned(object.getContactNo(), "#007bF9");
//                if(sessionManager.getPreferences(getApplicationContext(),"countryCode")!=null ){
//                    txtContact = getColoredSpanned("+"+sessionManager.getPreferences(getApplicationContext(),"countryCode") + object.getContactNo(), "#007bF9");
//                }
//                else{
//                    txtContact = getColoredSpanned("0" + object.getContactNo(), "#007bF9");
//                }

            }
//            Contact.setText(Html.fromHtml(txtContact));
//            Contact.setText(object.getContactNo());
            Contact.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:"+Html.fromHtml(txtContact)));
                    startActivity(callIntent);
                }
            });


//            if (object.getLandlineNo().contains("+")) {
//                txtLandline = object.getLandlineNo();
//
//            } else {
////                txtLandline= getColoredSpanned("0" + object.getLandlineNo(), "#007bF9");
//                txtLandline=  object.getLandlineNo();
//
//            }

        }
        if (TextUtils.isEmpty(object.getLandlineNo())) {
            llLandline.setVisibility(View.GONE);
        } else {
            LandLine.setText(object.getLandlineNo());
        }

        if (TextUtils.isEmpty(object.getDescriptionLink())) {
            DescriptionLink.setVisibility(View.GONE);
        } else {
            String txtwebDescLinkFirst = getColoredSpanned("Website Link: ", "#000000");
            String txtWebDesclink = getColoredSpanned(object.getDescriptionLink(), "#007bF9");
            DescriptionLink.setText(Html.fromHtml("<b>"+txtwebDescLinkFirst + "</b> \t\t\t" + txtWebDesclink));

//            DescriptionLink.setText("Website Link :" + " " + object.getDescriptionLink());
            DescriptionLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!object.getDescriptionLink().startsWith("http://") && !object.getDescriptionLink().startsWith("https://")) {
                        String url = "http://" + object.getDescriptionLink();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    }
                }
            });
        }

        if (TextUtils.isEmpty(object.getEmail())) {
            llEmail.setVisibility(View.GONE);
        } else {
//            Email.setText(object.getEmail());
            String txtEmail = getColoredSpanned(object.getEmail(), "#007bF9");
            Email.setText(Html.fromHtml(txtEmail));
        }

        if (TextUtils.isEmpty(object.getAddress())) {
            llAddress.setVisibility(View.GONE);
        } else {
            Address.setText(object.getAddress());
        }
        flag3 = (ImageView) findViewById(R.id.preview_flag_image);

        try {
            rvTargetViewers.setAdapter(new TargetViewerAdapter(object.getTargetViewers()));
            rvTargetViewers.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            rvInterested.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }


        LandLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+object.getLandlineNo()));
                startActivity(callIntent);

            }
        });

    }
    private void FGetAllCountry() {
        showProgressDialog();

        mApiService.getCountryData().enqueue(new Callback<ArrayList<GetCountry>>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(Call<ArrayList<GetCountry>> call, Response<ArrayList<GetCountry>> response) {
                dismissProgressDialog();
                countryList = response.body();
                for (int i = 0; i < countryList.size(); i++) {
                    if (countryList.get(i).getCountryName().equalsIgnoreCase(object.getCountry())) {
//                        country.setText(countryList.get(i).getCountryName());
//                        Bitmap bitmap = StringToBitMap(countryList.get(i).getImageString());
//                        flag.setImageBitmap(bitmap);
//                        Toast.makeText(PreviewTenderDetail.this, ""+countryList.get(i).getCountryCode(), Toast.LENGTH_SHORT).show();
//                      countryCode= "+"+countryList.get(i).getCountryCode();
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetCountry>> call, Throwable t) {
                dismissProgressDialog();
            }
        });

    }
    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }
    private void GetCategory() {
        showProgressDialog();
        final ArrayList<GetCategory> categories = new ArrayList<>();
        mApiService.getCategoryData().enqueue(new Callback<ArrayList<GetCategory>>() {
            @Override
            public void onResponse(Call<ArrayList<GetCategory>> call, Response<ArrayList<GetCategory>> response) {
                categoryList = response.body();
                dismissProgressDialog();
                StringBuilder categoryBuilder = new StringBuilder();
                for (int j = 0; j < object.getCategory().size(); j++) {
                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getId().equalsIgnoreCase(object.getCategory().get(j))) {
                            categories.add(categoryList.get(i));
                            /*Category.setText(categoryList.get(i).getCategoryName());
                            catFlagimg = categoryList.get(i).getImgString();
                            catBflag = StringToBitMap(catFlagimg);
                            catFlag.setImageBitmap(catBflag);*/
                            int size = categoryList.size() - 1;
                            categoryBuilder.append(categoryList.get(i).getCategoryName());
                            if (!(size == i)) {
                                Log.e("size: ", "" + size + i);
                                categoryBuilder.append(" || ");
                            }
                            clientCategory = categoryBuilder.toString();
                            Log.e("clientCategory: ", clientCategory);
                            break;

                        }
                    }
                }

                rvContractorCategory.setAdapter(new CategoryAdapter(categories));
                rvContractorCategory.setLayoutManager(new LinearLayoutManager(PreviewTenderDetail.this));

            }

            @Override
            public void onFailure(Call<ArrayList<GetCategory>> call, Throwable t) {
                Log.i(TAG, "post submitted to API." + t);
                dismissProgressDialog();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                String role = sessionManager.getPreferences(PreviewTenderDetail.this, "role");
                Intent i;
                if (role.equals("client")) {
                    i = new Intent(PreviewTenderDetail.this, ClientDrawer.class);
                } else {
                    i = new Intent(PreviewTenderDetail.this, MainDrawer.class);
                }
                i.putExtra("nav_not", "true");
                startActivity(i);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void GetAllCountry() {
        showProgressDialog();

        mApiService.getCountryData().enqueue(new Callback<ArrayList<GetCountry>>() {
            @Override
            public void onResponse(Call<ArrayList<GetCountry>> call, Response<ArrayList<GetCountry>> response) {

                countryList = response.body();
                if (countryList != null && !countryList.isEmpty() && countryList.size() > 0) {
                    for (int i = 0; i < countryList.size(); i++) {
                        if (countryList.get(i).getId().equalsIgnoreCase(object.getCountry())) {
                            flag = countryList.get(i).getImageString();
                            Country.setText(countryList.get(i).getCountryName());
                            if(object.getContactNo() !=null && object.getContactNo().contains("-")) {
                                object.setContactNo(object.getContactNo());
                            }
                            else{
                                object.setContactNo("-"+ object.getContactNo());
                            }
                            Contact.setText("+" + countryList.get(i).getCountryCode() + object.getContactNo());
                            if(object.getLandlineNo()!=null && object.getLandlineNo().contains("-")) {
                                object.setLandlineNo(object.getLandlineNo());
                            }
                            else{
                                object.setLandlineNo("-"+ object.getLandlineNo());
                            }
                            LandLine.setText("+" + countryList.get(i).getCountryCode() + object.getLandlineNo());
                            countryCode=countryList.get(i).getCountryCode();
                            sessionManager.setPreferences(getApplicationContext(),"countryCode",countryCode);
//                            Toast.makeText(PreviewTenderDetail.this, "Country Code"+countryCode, Toast.LENGTH_SHORT).show();
                            clientCountry = countryList.get(i).getCountryName();
                            Bflag = StringToBitMap(flag);
                            flag3.setImageBitmap(Bflag);
                            break;
                        }
                    }
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ArrayList<GetCountry>> call, Throwable t) {
                dismissProgressDialog();
            }
        });
    }

    private void getAllIntrestedContractor(String tender) {
        showProgressDialog();
        String token = "Bearer " + sessionManager.getPreferences(PreviewTenderDetail.this, "token");
        mApiService.getAllInterestedContractor(token, tender).enqueue(new Callback<List<Sender>>() {
            @Override
            public void onResponse(Call<List<Sender>> call, Response<List<Sender>> response) {
                dismissProgressDialog();
                if (response.isSuccessful() && response.body().size() > 0) {
                    List<Sender> listSender=new ArrayList<>();
                    Log.e(TAG, "onResponse: "+response.body() );
                    llInterested.setVisibility(View.VISIBLE);
                    rvInterested.setLayoutManager(new LinearLayoutManager(PreviewTenderDetail.this));
                    for (Sender sender:response.body()) {
                        if(sender.getIsActive()) {
                            listSender.add(sender);
                        }
                    }
                    rvInterested.setAdapter(new IntrestedContractorAdapter(listSender));
                } else {
                    llInterested.setVisibility(View.GONE);
                    Log.e(TAG, "onResponse: No Interested found");
                }
            }

            @Override
            public void onFailure(Call<List<Sender>> call, Throwable t) {
                dismissProgressDialog();
                Log.e(TAG, "onFailure: " + t.getMessage());
                llInterested.setVisibility(View.GONE);
            }
        });
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

    public void confirmRemove() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    this);
            builder.setTitle("Tender Watch");
            builder.setMessage("Tender will be completely removed from Tenderwatch?");

            builder.setPositiveButton("Remove",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            showProgressDialog();
                            String token = "Bearer " + sessionManager.getPreferences(PreviewTenderDetail.this, "token");
                            String id = object.getId();

                            Log.e(TAG, "onClick: "+token+ "id== "+id);
                            mApiService.removeTender(token, id).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                    dismissProgressDialog();
                                    if (intrestedContractors != null && intrestedContractors.size() > 0) {
//                                        Toast.makeText(PreviewTenderDetail.this, "Hello" + intrestedContractors.size(), Toast.LENGTH_SHORT).show();
                                        sendEmailOnInterested();
                                    } else {
                                        final AlertDialog alertDialog = new AlertDialog.Builder(PreviewTenderDetail.this).create();
                                        alertDialog.setTitle("Tender Watch");
                                        alertDialog.setMessage("Tender Removed!");
                                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                sendEmailOnInterested();
                                                Intent intent = new Intent(PreviewTenderDetail.this, ClientDrawer.class);
                                                startActivity(intent);
                                            }
                                        });
                                        alertDialog.show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    dismissProgressDialog();
                                }
                            });
                        }
                    });

            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.dismiss();
                        }
                    });

            if (!this.isFinishing())
                builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEmailOnInterested() {
        showProgressDialog();
        contractorEmailList = new ArrayList<String>();
        JSONArray jsonArray=new JSONArray();
        try {
            if (intrestedContractors != null && intrestedContractors.size() > 0) {
                for (int i = 0; i < intrestedContractors.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("_id", intrestedContractors.get(i).getId());
                    jsonObject.put("email", intrestedContractors.get(i).getEmail());
//                contractorEmailList.add(intrestedContractors.get(i).getEmail());
                    jsonArray.put(jsonObject);
                }
                Log.e("emailList", "" + contractorEmailList);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        String token = "Bearer " + sessionManager.getPreferences(PreviewTenderDetail.this, "token");
        Log.e("token: ", token);
        Object user = sessionManager.getPreferencesObject(PreviewTenderDetail.this);
        Log.e("username: ", ((User) user).getFirstName() + ((User) user).getLastName());
        String userName = ((User) user).getFirstName() + ((User) user).getLastName();
        int length = clientCategory.length();
        //Check whether or not the string contains at least four characters; if not, this method is useless
        clientCategory = clientCategory.substring(0, length - 4) + " . ";
        JsonObject gsonObject = new JsonObject();
        try {
            // contractorEmailList.add("tenderwatch01@gmail.com");
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("Email", jsonArray);
            jsonObj.put("Category", clientCategory);
            jsonObj.put("TenderName", object.getTenderName());
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
        mApiService.sendEmail(token, gsonObject).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
                Log.e("email send API.", "" + response);
                final AlertDialog alertDialog = new AlertDialog.Builder(PreviewTenderDetail.this).create();
                alertDialog.setTitle("Tender Watch");
                alertDialog.setMessage("Tender Removed,Email has been sent to Interested Contractor!");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(PreviewTenderDetail.this, ClientDrawer.class);
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

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

        List<GetCategory> categorys;

        public CategoryAdapter(List<GetCategory> list) {
            this.categorys = list;
        }

        @Override
        public CategoryAdapter.CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CategoryAdapter.CategoryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscription_category, parent, false));
        }

        @Override
        public void onBindViewHolder(CategoryAdapter.CategoryHolder holder, int position) {
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

    public class IntrestedContractorAdapter extends RecyclerView.Adapter<IntrestedContractorAdapter.IntrestedViewHolder> {


        public IntrestedContractorAdapter(List<Sender> intrestedContractorss) {
            intrestedContractors = intrestedContractorss;
        }

        @Override
        public IntrestedContractorAdapter.IntrestedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new IntrestedContractorAdapter.IntrestedViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interested_user, parent, false));
        }

        @Override
        public void onBindViewHolder(IntrestedContractorAdapter.IntrestedViewHolder holder, final int position) {
            holder.tvName.setText(intrestedContractors.get(position).getFirstName() + " " + intrestedContractors.get(position).getLastName());
            if(intrestedContractors.get(position).getContactNo().equalsIgnoreCase("-")){
                holder.tvContact.setText(intrestedContractors.get(position).getContactNo().replace("-",""));
            }else{
                holder.tvContact.setText(intrestedContractors.get(position).getContactNo());
            }

            holder.tvEmail.setText(intrestedContractors.get(position).getEmail());



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(object);
                    Intent intent = new Intent(PreviewTenderDetail.this, ClientDetail.class);
                    //intent.putExtra("data", jsonString);
                    intent.putExtra("sender", gson.toJson(intrestedContractors.get(position)));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return intrestedContractors.size();
        }

        public class IntrestedViewHolder extends RecyclerView.ViewHolder {

            private TextView tvName, tvContact, tvEmail;

            public IntrestedViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_name);
                tvContact = itemView.findViewById(R.id.tv_contact);
                tvEmail = itemView.findViewById(R.id.tv_email);
            }
        }
    }

    public class TargetViewerAdapter extends RecyclerView.Adapter<TargetViewerAdapter.TargetViewHolder> {

        List<SubscriptionCategoryResponse> targetViewers;

        public TargetViewerAdapter(List<SubscriptionCategoryResponse> targetViewers) {
            this.targetViewers = targetViewers;
        }

        @Override
        public TargetViewerAdapter.TargetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TargetViewerAdapter.TargetViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_target_viewers, parent, false));
        }

        @Override
        public void onBindViewHolder(TargetViewerAdapter.TargetViewHolder holder, int position) {
            holder.tvTargetViewer.setText(targetViewers.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return targetViewers.size();
        }

        public class TargetViewHolder extends RecyclerView.ViewHolder {

            private TextView tvTargetViewer;

            public TargetViewHolder(View itemView) {
                super(itemView);
                tvTargetViewer = itemView.findViewById(R.id.tv_target_viewers);
            }
        }
    }

    private class DownloadImage extends AsyncTask<URL, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            Log.e(TAG, "onPreExecute: Download Start");
        }

        @Override
        protected Bitmap doInBackground(URL... urls) {
            URL url = urls[0];
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();

                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                connection.disconnect();
                return bmp;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                Uri uri = saveImageToInternalStorage(bitmap);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(uri, "image/*");
                startActivity(i);

            }
        }
    }


    protected Uri saveImageToInternalStorage(Bitmap bitmap) {
        try {
            String name = object.getId() + object.getTenderName() + object.getId().substring(0, 5) + ".jpeg";
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TenderWatch";
            new File(path).mkdir();
            File file = new File(path, name);
            if (!file.exists()) {
                OutputStream stream = null;
                stream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.flush();
                stream.close();
            }
            dismissProgressDialog();
            return Uri.parse(file.getAbsolutePath());
        } catch (IOException e) // Catch the exception
        {
            dismissProgressDialog();
            e.printStackTrace();
        }
        return null;
    }
}
