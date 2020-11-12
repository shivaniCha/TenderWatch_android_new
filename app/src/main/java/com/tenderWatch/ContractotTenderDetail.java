package com.tenderWatch;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.tenderWatch.Drawer.MainDrawer;
import com.tenderWatch.Models.AllContractorTender;
import com.tenderWatch.Models.GetCategory;
import com.tenderWatch.Models.GetCountry;
import com.tenderWatch.Models.SubscriptionCategoryResponse;
import com.tenderWatch.Models.UpdateTender;
import com.tenderWatch.Models.User;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;
import com.tenderWatch.utils.JustifyTextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * new
 * Created by lcom47 on 6/1/18.
 */

public class ContractotTenderDetail extends AppCompatActivity {

    Api mApiService;
    private static final String TAG = PreviewTenderDetail.class.getSimpleName();
    AllContractorTender object;
    String day, flag, catFlagimg;
    Bitmap Bflag, catBflag;
    ImageView flag3, imagetender, catFlag;
    FrameLayout flFullProfilePic;
    ImageView ivFullProfilePic;
    TextView lblClientDetail, tenderTitle, Country, Category, ExpDay, City, Contact, LandLine, Email, Address, previewFollow, previewFollowTenderLink;
    private JustifyTextView Description, DescriptionLink;
    LinearLayout llEmail, llContact, llLandline, llAddress;
    Button removeTender, btnInterestedTender, btnRemoveFavorite, btnRemoveInterested;
    SessionManager sessionManager;
    String sender;
    private ProgressDialog mProgressDialog;
    ArrayList<GetCountry> countryList = new ArrayList<>();
    ArrayList<GetCategory> categoryList = new ArrayList<>();
    User user;
    Bitmap imageBitmap = null;
    private RecyclerView rvContractorCategory, rvTargetViewers;
    final String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private ProgressBar pbFavorite, pbInterested;
    private ImageView imgSendEmail;
    private String mailContent1 = "";
    private String mailContent = "";
    UpdateTender targetViewers;
    String imagePath;
    String countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ntender_detail);
        sessionManager = new SessionManager(ContractotTenderDetail.this);
        user = new SessionManager(this).getPreferencesObject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ContractotTenderDetail.this, MainDrawer.class);
                i.putExtra("nav_not", "true");
                startActivity(i);
            }
        });
        setTitle("Tender Detail");
        mApiService = ApiUtils.getAPIService();
        tenderTitle = (TextView) findViewById(R.id.preview_tender_title);
        Country = (TextView) findViewById(R.id.preview_country_name);
        Category = (TextView) findViewById(R.id.preview_category);
        ExpDay = (TextView) findViewById(R.id.preview_exp);
        Description = (JustifyTextView) findViewById(R.id.preview_description);
        DescriptionLink = (JustifyTextView) findViewById(R.id.preview_description_link);
        previewFollowTenderLink = (TextView) findViewById(R.id.preview_follow_tender_link);
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
        btnInterestedTender = (Button) findViewById(R.id.btn_interested_tender);
        imagetender = (ImageView) findViewById(R.id.preview_tender_image);
        lblClientDetail = (TextView) findViewById(R.id.lbl_clientDetail);
        catFlag = (ImageView) findViewById(R.id.preview_catflag_image);
        flFullProfilePic = (FrameLayout) findViewById(R.id.fl_full_profile_pic);
        ivFullProfilePic = (ImageView) findViewById(R.id.iv_full_profile_pic);
        rvContractorCategory = (RecyclerView) findViewById(R.id.rv_tender_detail_category);
        rvTargetViewers = (RecyclerView) findViewById(R.id.rv_target_viewer);
        previewFollow = (TextView) findViewById(R.id.preview_follow);
        btnRemoveFavorite = (Button) findViewById(R.id.remove_favorite_tender);
        btnRemoveInterested = (Button) findViewById(R.id.btn_remove_interested_tender);
        pbFavorite = (ProgressBar) findViewById(R.id.pb_favorite);
        pbInterested = (ProgressBar) findViewById(R.id.pb_interested);
        imgSendEmail = (ImageView) findViewById(R.id.img_sendemail);


        lblClientDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String jsonString = gson.toJson(object);
                Intent intent = new Intent(ContractotTenderDetail.this, ClientDetail.class);
                intent.putExtra("data", jsonString);
                intent.putExtra("sender", sender);
                startActivity(intent);
            }
        });

        String json = getIntent().getStringExtra("data");
        sender = getIntent().getStringExtra("sender");
        String amend = getIntent().getStringExtra("amended");
        imagePath = getIntent().getStringExtra("imagePath");
        if (amend != null) {
            sessionManager.ShowDialog(ContractotTenderDetail.this, "Tender has been amended by client");
        }
        Gson gson = new Gson();
        object = gson.fromJson(json, AllContractorTender.class);
        btnInterestedTender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallInterestedApi();
            }
        });

        btnRemoveInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeInterested();
            }
        });
        btnRemoveFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFavorite();
            }
        });
        Calendar c = Calendar.getInstance();
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

        if (endDateValue != null && startDateValue != null) {
            long diff = endDateValue.getTime() - startDateValue.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = (hours / 24) + 1;
            Log.d("days", "" + days);
            ExpDay.setText(days + " days");
        }

        removeTender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                confirmRemove();
                pbFavorite.setVisibility(View.VISIBLE);
                removeTender.setVisibility(View.GONE);
                String token = "Bearer " + sessionManager.getPreferences(ContractotTenderDetail.this, "token");
                String id = object.getId();
                mApiService.addFavorite(token, id).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dismissProgressDialog();
                        pbFavorite.setVisibility(View.GONE);
                        btnRemoveFavorite.setVisibility(View.VISIBLE);
                        Toast.makeText(ContractotTenderDetail.this, "Tender added to Favorite", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        dismissProgressDialog();
                        pbFavorite.setVisibility(View.GONE);
                        removeTender.setVisibility(View.VISIBLE);
                        Toast.makeText(ContractotTenderDetail.this, "Unable to add to favorite", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "response---" + t);
                    }
                });
            }
        });

        if (!TextUtils.isEmpty(object.getTenderPhoto()) && URLUtil.isHttpsUrl(object.getTenderPhoto())) {
            if (URLUtil.isValidUrl(object.getTenderPhoto())) {
                Picasso.with(ContractotTenderDetail.this)
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
        } else {
            findViewById(R.id.pb_image_loader).setVisibility(View.GONE);
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

                    Intent intZoomImage = new Intent(ContractotTenderDetail.this, ZoomImageActivity.class);
                    String name = object.getId() + object.getTenderName() + object.getId().substring(0, 5) + ".jpeg";
                    intZoomImage.putExtra("tenderImageName", name);
                    intZoomImage.putExtra("tenderImage", object.getTenderPhoto());
                    startActivity(intZoomImage);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (TextUtils.isEmpty(object.getDescriptionLink())) {
            DescriptionLink.setVisibility(View.GONE);
        } else {
            String txtwebDescLinkFirst = getColoredSpanned("Website Link:", "#000000");
            String txtWebDesclink = getColoredSpanned(object.getDescriptionLink(), "#007bF9");
            DescriptionLink.setText(Html.fromHtml(txtwebDescLinkFirst + " \t\t\t" + txtWebDesclink));

//            DescriptionLink.setText("Website Link :" + " " + );
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
//cheater Ikim or chochi lagse thanks to unknown persons bole ****
        String txtLinkFirst = getColoredSpanned("Follow Tender Link", "#000000");
        String txtLinkOpenRoundBracket = getColoredSpanned("(", "#000000");
        String txtWebsitelink = getColoredSpanned("Website Link", "#007bF9");
        String txtLinkCloseRoundBracket = getColoredSpanned(")", "#000000");
        previewFollowTenderLink.setText(Html.fromHtml(txtLinkFirst + " " + txtLinkOpenRoundBracket + txtWebsitelink + txtLinkCloseRoundBracket));

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

        imagetender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intZoomImage = new Intent(ContractotTenderDetail.this, ZoomImageActivity.class);
                String name = object.getId() + object.getTenderName() + object.getId().substring(0, 5) + ".jpeg";
                intZoomImage.putExtra("tenderImageName", name);
                intZoomImage.putExtra("tenderImage", object.getTenderPhoto());
                startActivity(intZoomImage);
            }
        });

        flFullProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flFullProfilePic.setVisibility(View.GONE);
            }
        });

        Email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(ContractotTenderDetail.this, ""+Email.getText().toString(), Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(Email.getText().toString())) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Email.getText().toString()});
                    intent.setType("text/plain");
                    startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                }
            }
        });

        Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(Contact.getText().toString())) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Log.e(TAG, "onClick: " + object.getContactNo());
                    intent.setData(Uri.parse("tel:" + object.getContactNo()));
                    startActivity(intent);
                }
            }
        });

        GetCategory();
        GetAllCountry();
        if (object.getInterested().contains(user.getId())) {
            btnInterestedTender.setVisibility(View.GONE);
            btnRemoveInterested.setVisibility(View.VISIBLE);
        } else {
            btnInterestedTender.setVisibility(View.VISIBLE);
            btnRemoveInterested.setVisibility(View.GONE);
        }

        if (object.getIsFollowTender()) {
            previewFollow.setVisibility(View.VISIBLE);
        } else {
            previewFollow.setVisibility(View.GONE);
        }

        if (object.getIsFollowTenderLink()) {
            previewFollowTenderLink.setVisibility(View.VISIBLE);
        } else {

            previewFollowTenderLink.setVisibility(View.GONE);
        }

        if (object.getFavorite().contains(user.getId())) {
            removeTender.setVisibility(View.GONE);
            btnRemoveFavorite.setVisibility(View.VISIBLE);
        } else {
            removeTender.setVisibility(View.VISIBLE);
            btnRemoveFavorite.setVisibility(View.GONE);
        }
        tenderTitle.setText(object.getTenderName());

        if (object.getDescription() != null && object.getCity() != null) {

            if (object.getDescription().isEmpty()) {
                Description.setVisibility(View.GONE);
            } else {
                Description.setVisibility(View.VISIBLE);
                Description.setText(object.getDescription());
            }

            if (object.getCity().isEmpty()) {
                City.setVisibility(View.GONE);
            } else {
                City.setText(object.getCity());
                City.setVisibility(View.VISIBLE);
            }
        } else if (object.getDescription() != null) {
            if (object.getDescription().isEmpty()) {
                Description.setVisibility(View.GONE);
            } else {
                Description.setVisibility(View.VISIBLE);
                Description.setText(object.getDescription());
            }
        } else {
            City.setText(object.getCity());
        }


        if (TextUtils.isEmpty(object.getContactNo())) {
            llContact.setVisibility(View.GONE);
        //} else {
//            Contact.setText(object.getContactNo());
          //  if (object.getContactNo().contains("+")) {
            //    Contact.setText(object.getContactNo());
            //} else {
              //  Contact.setText("0" + object.getContactNo());
            //}
//        } else {
////            Contact.setText(object.getContactNo());
//            if (object.getContactNo().contains("+")) {
//                Contact.setText(object.getContactNo());
//            } else {
//                Contact.setText("0" + object.getContactNo());
//            }
        }

        if (TextUtils.isEmpty(object.getLandlineNo())) {
            llLandline.setVisibility(View.GONE);
        } else {
            LandLine.setText(object.getLandlineNo());
        }
        LandLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                Log.e(TAG, "onClick: " + object.getLandlineNo());
                callIntent.setData(Uri.parse("tel:" + object.getLandlineNo()));
                startActivity(callIntent);

            }
        });


        if (TextUtils.isEmpty(object.getEmail())) {
            llEmail.setVisibility(View.GONE);
        } else {
            Email.setText(object.getEmail());
        }

        if (TextUtils.isEmpty(object.getAddress())) {
            llAddress.setVisibility(View.GONE);
        } else {
            Address.setText(object.getAddress());
        }
        flag3 = (ImageView) findViewById(R.id.preview_flag_image);
        targetViewers = new Gson().fromJson(getIntent().getStringExtra("targetViewers"), UpdateTender.class);
        try {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("targetViewers"))) {

                rvTargetViewers.setAdapter(new TargetViewerAdapter(targetViewers.getTargetViewers()));
                rvTargetViewers.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        imgSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mailContent = "";
                StringBuilder category = new StringBuilder();
                if (object != null && object.getCategory().size() > 0 && categoryList.size() > 0) {
                    for (int j = 0; j < object.getCategory().size(); j++) {
                        for (int i = 0; i < categoryList.size(); i++) {
                            if (categoryList.get(i).getId().equalsIgnoreCase(object.getCategory().get(j))) {
                                category.append(categoryList.get(i).getCategoryName());
                                if (i != categoryList.size() - 1) {
                                    category.append(", ");
                                }
                            }
                        }
                    }
                } else {
                    category.append("NAN");
                }

                StringBuilder targetViewerss = new StringBuilder();
                for (int i = 0; i < targetViewers.getTargetViewers().size(); i++) {
                    targetViewerss.append(targetViewers.getTargetViewers().get(i).getName());
                    if (i != targetViewers.getTargetViewers().size() - 1) {
                        targetViewerss.append(", ");
                    }
                }

                StringBuilder des = new StringBuilder();
                String City = "";
                if (!TextUtils.isEmpty(object.getCity())) {
                    City = object.getCity();
                }
                if (!TextUtils.isEmpty(object.getDescription())) {
                    des.append("Description:\n" + Utility.removeEmptyLine(object.getDescription()) + "\n");
                }

                StringBuilder contact = new StringBuilder();
                contact.append("How To Contact Client For This Tender: " + "\n\n");
                if (!TextUtils.isEmpty(object.getContactNo())) {
                    contact.append("ContactNo: " + countryCode + object.getContactNo() + "\n\n");
                }
                if (!TextUtils.isEmpty(object.getLandlineNo())) {
                    contact.append("LanlineNo: " + countryCode + object.getLandlineNo() + "\n\n");
                }
                if (!TextUtils.isEmpty(object.getEmail())) {
                    contact.append("Email: " + object.getEmail() + "\n\n");
                }
                if (!TextUtils.isEmpty(object.getAddress())) {
                    contact.append("Address: " + object.getAddress() + "\n\n");
                }
                if (!TextUtils.isEmpty(object.getTenderPhoto())) {
                    contact.append("Tender Photo: " + object.getTenderPhoto() + "\n");
                }
//                Spanned sharedString = Html.fromHtml("<html><b>Shared From</b></html>");
                mailContent = "Shared From  www.tenderwatch.com" + "\n\n" +
                        "Shared By: " + user.getEmail() + "\n\n" +
                        "Tender Title: " + object.getTenderName() + "\n\n" +
                        "Country: " + Country.getText() + "\n\n" +
                        "City:  " + City + "\n\n" +
                        "Category: " + category.toString() + "\n\n" +
                        " Target Contractor: " + targetViewerss.toString() + "\n\n" +
                        "Expiry Day: " + ExpDay.getText() + "\n\n" +
                        des.toString() + "\n\n" +
                        contact.toString();
                sendEmail();


            }

        });
    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    private void sendEmail() {
//        String[] TO = {user.getEmail()};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
//        emailIntent.putExtra(Intent.EXTRA_EMAIL,TO);
        emailIntent.putExtra(Intent.EXTRA_CC, user.getEmail());
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Tender Details - " + object.getTenderName());

        emailIntent.putExtra(Intent.EXTRA_TEXT, mailContent);

        if (imagePath != null) {
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imagePath));
        }

        emailIntent.setType("message/rfc822");
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        startActivity(emailIntent);

    }

    private void GetCategory() {
        showProgressDialog();

        final ArrayList<GetCategory> categories = new ArrayList<>();

        mApiService.getCategoryData().enqueue(new Callback<ArrayList<GetCategory>>() {
            @Override
            public void onResponse(Call<ArrayList<GetCategory>> call, Response<ArrayList<GetCategory>> response) {
                categoryList = response.body();
                dismissProgressDialog();
                for (int j = 0; j < object.getCategory().size(); j++) {
                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getId().equalsIgnoreCase(object.getCategory().get(j))) {
                            categories.add(categoryList.get(i));
                            /*Category.setText(categoryList.get(i).getCategoryName());
                            catFlagimg = categoryList.get(i).getImgString();
                            catBflag = StringToBitMap(catFlagimg);
                            catFlag.setImageBitmap(catBflag);*/
                            break;
                        }
                    }
                }

                rvContractorCategory.setAdapter(new CategoryAdapter(categories));
                rvContractorCategory.setLayoutManager(new LinearLayoutManager(ContractotTenderDetail.this));

            }

            @Override
            public void onFailure(Call<ArrayList<GetCategory>> call, Throwable t) {
                Log.i(TAG, "post submitted to API." + t);
                dismissProgressDialog();
            }
        });
    }

    private void CallInterestedApi() {
        btnInterestedTender.setVisibility(View.GONE);
        pbInterested.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getPreferences(ContractotTenderDetail.this, "token");
        String tenderId = object.getId();
        mApiService.callInterested(token, tenderId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                btnRemoveInterested.setVisibility(View.VISIBLE);
                pbInterested.setVisibility(View.GONE);
                new android.app.AlertDialog.Builder(ContractotTenderDetail.this)
                        .setTitle("Tender")
                        .setCancelable(false)
                        .setMessage(getString(R.string.success_tender_interested))
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
                Log.i(TAG, "post submitted to API." + response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pbInterested.setVisibility(View.GONE);
                btnInterestedTender.setVisibility(View.VISIBLE);
                Log.i(TAG, "post submitted to API." + t);
            }
        });
    }

    private void removeFavorite() {
        btnRemoveFavorite.setVisibility(View.GONE);
        pbFavorite.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getPreferences(ContractotTenderDetail.this, "token");
        String tenderId = object.getId();
        mApiService.removeFavorite(token, tenderId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                removeTender.setVisibility(View.VISIBLE);
                pbFavorite.setVisibility(View.GONE);
                Log.i(TAG, "post submitted to API." + response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pbFavorite.setVisibility(View.GONE);
                btnRemoveFavorite.setVisibility(View.VISIBLE);
                Log.i(TAG, "post submitted to API." + t);
            }
        });
    }

    private void removeInterested() {
        btnRemoveInterested.setVisibility(View.GONE);
        pbInterested.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getPreferences(ContractotTenderDetail.this, "token");
        String tenderId = object.getId();
        mApiService.removeInterested(token, tenderId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                btnInterestedTender.setVisibility(View.VISIBLE);
                pbInterested.setVisibility(View.GONE);
                new android.app.AlertDialog.Builder(ContractotTenderDetail.this)
                        .setTitle("Tender")
                        .setMessage("You are no longer Interested in this tender")
                        .setCancelable(false)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
                Log.i(TAG, "post submitted to API." + response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pbInterested.setVisibility(View.GONE);
                btnRemoveInterested.setVisibility(View.VISIBLE);
                Log.i(TAG, "post submitted to API." + t);
            }
        });
    }


    private void GetAllCountry() {
        showProgressDialog();

        mApiService.getCountryData().enqueue(new Callback<ArrayList<GetCountry>>() {
            @Override
            public void onResponse(Call<ArrayList<GetCountry>> call, Response<ArrayList<GetCountry>> response) {
                countryList = response.body();
                dismissProgressDialog();
                for (int i = 0; i < countryList.size(); i++) {
                    if (countryList.get(i).getId().equalsIgnoreCase(object.getCountry())) {
                        flag = countryList.get(i).getImageString();
                        Country.setText(countryList.get(i).getCountryName());
                        if(object.getContactNo()!=null && object.getContactNo().contains("-")){
                            Contact.setText("+" + countryList.get(i).getCountryCode() + object.getContactNo());
                        }else {
                            Contact.setText("+" + countryList.get(i).getCountryCode() + "-" + object.getContactNo());
                        }
                        LandLine.setText("+"+countryList.get(i).getCountryCode()+"-"+object.getLandlineNo());
                        countryCode="+"+countryList.get(i).getCountryCode()+"-";
                        Bflag = StringToBitMap(flag);
                        flag3.setImageBitmap(Bflag);
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

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            this.finish();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                this.onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
        if (!this.isFinishing() && mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public void confirmRemove() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Tender Watch");
            builder.setMessage("Are you sure you want to remove this Tender completely from your Account? ");

            builder.setPositiveButton("Remove",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            String token = "Bearer " + sessionManager.getPreferences(ContractotTenderDetail.this, "token");
                            String id = object.getId();
                            mApiService.removeTender(token, id).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    Intent intent = new Intent(ContractotTenderDetail.this, MainDrawer.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.i(TAG, "response---" + t);
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

    public class TargetViewerAdapter extends RecyclerView.Adapter<TargetViewerAdapter.TargetViewHolder> {

        List<SubscriptionCategoryResponse> targetViewers;

        public TargetViewerAdapter(List<SubscriptionCategoryResponse> targetViewers) {
            this.targetViewers = targetViewers;
        }

        @Override
        public TargetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TargetViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_target_viewers, parent, false));
        }

        @Override
        public void onBindViewHolder(TargetViewHolder holder, int position) {
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

    private class DownloadTenderImage extends AsyncTask<URL, Void, Bitmap> {

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
                saveImageToInternalStorage(bitmap);
                ;
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
            Uri imagePath = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);
            return imagePath;
        } catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }
        return null;
    }
}

