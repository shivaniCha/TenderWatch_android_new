package com.tenderWatch;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.squareup.picasso.Target;
import com.tenderWatch.ClientDrawer.ClientDrawer;
import com.tenderWatch.Drawer.MainDrawer;
import com.tenderWatch.Models.GetCategory;
import com.tenderWatch.Models.GetCountry;
import com.tenderWatch.Models.Sender;
import com.tenderWatch.Models.SubscriptionCategoryResponse;
import com.tenderWatch.Models.Tender;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NTenderDetail extends AppCompatActivity {
    Api mApiService;
    private static final ArrayList<String> alpha = new ArrayList<String>();
    private static final ArrayList<String> countryName = new ArrayList<String>();

    private static final ArrayList<String> alpha2 = new ArrayList<String>();
    private static final ArrayList<String> categoryName = new ArrayList<String>();
    private static final String TAG = PreviewTenderDetail.class.getSimpleName();
    UpdateTender object;
    String day, flag, countryName1, categoryName1;
    Bitmap Bflag, catBflag;
    ImageView flag3, imagetender;
    User user;

    TextView lblClientDetail, tenderTitle, Country, Category, ExpDay, City, Contact, LandLine, Email, Address, previewFollow;
    private JustifyTextView Description;
    LinearLayout llEmail, llContact, llLandline, llAddress;
    Button removeTender, editTender, btnInterestedTender,btnRemoveFavorite,btnRemoveInterested;
    SessionManager sessionManager;
    String sender;
    private ProgressDialog mProgressDialog;
    ArrayList<GetCategory> categoryList = new ArrayList<>();
    ArrayList<GetCountry> countryList = new ArrayList<>();
    private RecyclerView rvContractorCategory, rvTargetViewers;
    final String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private ProgressBar pbFavorite, pbInterested;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ntender_detail);

        sessionManager = new SessionManager(NTenderDetail.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NTenderDetail.this, MainDrawer.class);
                i.putExtra("nav_not", "true");
                startActivity(i);
            }
        });
        setTitle("Tender Detail");
        mApiService = ApiUtils.getAPIService();
        tenderTitle = (TextView) findViewById(R.id.preview_tender_title);
        Country = (TextView) findViewById(R.id.preview_country_name);
        ExpDay = (TextView) findViewById(R.id.preview_exp);
        Description = (JustifyTextView) findViewById(R.id.preview_description);
        City = (TextView) findViewById(R.id.preview_tender_city);
        Contact = (TextView) findViewById(R.id.preview_tender_mobile);
        LandLine = (TextView) findViewById(R.id.preview_tender_landline);
        Email = (TextView) findViewById(R.id.preview_tender_email);
        Address = (TextView) findViewById(R.id.preview_tender_address);
        llAddress = (LinearLayout) findViewById(R.id.ll_preview_address);
        btnInterestedTender = (Button) findViewById(R.id.btn_interested_tender);
        llContact = (LinearLayout) findViewById(R.id.ll_preview_mobile);
        llLandline = (LinearLayout) findViewById(R.id.ll_preview_landline);
        llEmail = (LinearLayout) findViewById(R.id.ll_preview_email);
        removeTender = (Button) findViewById(R.id.remove_tender);
        imagetender = (ImageView) findViewById(R.id.preview_tender_image);
        lblClientDetail = (TextView) findViewById(R.id.lbl_clientDetail);
        rvContractorCategory = (RecyclerView) findViewById(R.id.rv_tender_detail_category);
        rvTargetViewers = (RecyclerView) findViewById(R.id.rv_target_viewer);
        previewFollow = (TextView) findViewById(R.id.preview_follow);
        btnRemoveFavorite= (Button) findViewById(R.id.remove_favorite_tender);
        btnRemoveInterested= (Button) findViewById(R.id.btn_remove_interested_tender);
        pbFavorite= (ProgressBar) findViewById(R.id.pb_favorite);
        pbInterested= (ProgressBar) findViewById(R.id.pb_interested);

        lblClientDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String jsonString = gson.toJson(object);
                Intent intent = new Intent(NTenderDetail.this, ClientDetail.class);
                intent.putExtra("data", jsonString);
                intent.putExtra("sender", sender);
                startActivity(intent);
            }
        });

        String json = getIntent().getStringExtra("data");
        sender = getIntent().getStringExtra("sender");
        Gson gson = new Gson();
        object = gson.fromJson(json, UpdateTender.class);
        user = new SessionManager(this).getPreferencesObject(this);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = null;
        df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = null;
        formattedDate = df.format(c.getTime());
        Date startDateValue = null, endDateValue = null;
        try {
            startDateValue = new SimpleDateFormat("yyyy-MM-dd").parse(formattedDate);
            //  startDateValue = new SimpleDateFormat("yyyy-MM-dd").parse(tenderList.get(position).getCreatedAt().split("T")[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            endDateValue = new SimpleDateFormat("yyyy-MM-dd").parse(object.getExpiryDate().split("T")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (object.getInterested().contains(user.getId())) {
            btnInterestedTender.setVisibility(View.GONE);
            btnRemoveInterested.setVisibility(View.VISIBLE);
        } else {
            btnInterestedTender.setVisibility(View.VISIBLE);
            btnRemoveInterested.setVisibility(View.GONE);
        }

        if(object.getFavorite().contains(user.getId())){
            removeTender.setVisibility(View.GONE);
            btnRemoveFavorite.setVisibility(View.VISIBLE);
        }else{
            removeTender.setVisibility(View.VISIBLE);
            btnRemoveFavorite.setVisibility(View.GONE);
        }

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

        if(object.getIsFollowTender()){
            previewFollow.setVisibility(View.VISIBLE);
        }else{
            previewFollow.setVisibility(View.GONE);
        }
        //Date endDateValue = new Date(allTender.get(position).getExpiryDate().split("T")[0]);
        long diff = endDateValue.getTime() - startDateValue.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = (hours / 24) + 1;
        Log.d("days", "" + days);
        Email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    intent.setData(Uri.parse("tel:" + Contact.getText().toString()));
                    startActivity(intent);
                }
            }
        });
        removeTender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                confirmRemove();
                pbFavorite.setVisibility(View.VISIBLE);
                removeTender.setVisibility(View.GONE);
                String token = "Bearer " + sessionManager.getPreferences(NTenderDetail.this, "token");
                String id = object.getId();
                mApiService.addFavorite(token, id).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dismissProgressDialog();
                        pbFavorite.setVisibility(View.GONE);
                        btnRemoveFavorite.setVisibility(View.VISIBLE);
                        Toast.makeText(NTenderDetail.this, "Tender added to Favorite", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        dismissProgressDialog();
                        pbFavorite.setVisibility(View.GONE);
                        removeTender.setVisibility(View.VISIBLE);
                        Toast.makeText(NTenderDetail.this, "Unable to add to favorite", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "response---" + t);
                    }
                });
            }
        });

        if (!TextUtils.isEmpty(object.getTenderPhoto()) && URLUtil.isHttpsUrl(object.getTenderPhoto())) {
            Picasso.with(NTenderDetail.this)
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

        }else{
            findViewById(R.id.pb_image_loader).setVisibility(View.GONE);
        }

        imagetender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intZoomImage = new Intent(NTenderDetail.this, ZoomImageActivity.class);
                /*ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(ContractotTenderDetail.this,imagetender, ViewCompat.getTransitionName(imagetender));
                startActivity(intZoomImage,options.toBundle());*/
                String name = object.getId() + object.getTenderName() + object.getId().substring(0, 5) + ".jpeg";
                intZoomImage.putExtra("tenderImageName",name);
                intZoomImage.putExtra("tenderImage", object.getTenderPhoto());
                startActivity(intZoomImage);
            }
        });

        btnInterestedTender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallInterestedApi();
            }
        });

        previewFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    Dexter.withActivity(NTenderDetail.this)
                            .withPermission(permission)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse response) {
                                    showProgressDialog();
                                    try {
                                        new DownloadImage().execute(new URL(object.getTenderPhoto()));
                                    } catch (MalformedURLException e) {
                                        dismissProgressDialog();
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse response) {
                                    Toast.makeText(NTenderDetail.this, "Please grant storage permission to download image.", Toast.LENGTH_SHORT).show();
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
        });

        GetCategory();
        GetAllCountry();
        setUpTargetViewers();
        tenderTitle.setText(object.getTenderName());

        ExpDay.setText(days + " days");
        Description.setText(object.getDescription());
        City.setText(object.getCity());
        if (object.getContactNo() !=null && TextUtils.isEmpty(object.getContactNo())) {
            llContact.setVisibility(View.GONE);
        } else {
            if(object.getContactNo() !=null && object.getContactNo().contains("+")){
                Contact.setText(object.getContactNo());
            }else{
                Contact.setText("0"+object.getContactNo());
            }
        }

        if (object.getLandlineNo() !=null && TextUtils.isEmpty(object.getLandlineNo())) {
            llLandline.setVisibility(View.GONE);
        } else {
            LandLine.setText(object.getLandlineNo());
        }

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

    }

    private void setUpTargetViewers() {
        rvTargetViewers.setAdapter(new TargetViewersAdapter(object.getTargetViewers()));
        rvTargetViewers.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void CallInterestedApi() {
        btnInterestedTender.setVisibility(View.GONE);
        pbInterested.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getPreferences(NTenderDetail.this, "token");
        String tenderId = object.getId();
        mApiService.callInterested(token, tenderId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                btnRemoveInterested.setVisibility(View.VISIBLE);
                pbInterested.setVisibility(View.GONE);
                new AlertDialog.Builder(NTenderDetail.this)
                        .setTitle("Tender")
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

    private void removeFavorite(){
        btnRemoveFavorite.setVisibility(View.GONE);
        pbFavorite.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getPreferences(NTenderDetail.this, "token");
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

    private void removeInterested(){
        btnRemoveInterested.setVisibility(View.GONE);
        pbInterested.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getPreferences(NTenderDetail.this, "token");
        String tenderId = object.getId();
        mApiService.removeInterested(token, tenderId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                btnInterestedTender.setVisibility(View.VISIBLE);
                pbInterested.setVisibility(View.GONE);
                new android.app.AlertDialog.Builder(NTenderDetail.this)
                        .setTitle("Tender")
                        .setMessage("You are no longer Interested in this tender")
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
                        if (categoryList.get(i).getId().equalsIgnoreCase(object.getCategory().get(j).getId())) {
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
                rvContractorCategory.setLayoutManager(new LinearLayoutManager(NTenderDetail.this));

            }

            @Override
            public void onFailure(Call<ArrayList<GetCategory>> call, Throwable t) {
                Log.i(TAG, "post submitted to API." + t);
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
                countryList = response.body();
                if (countryList != null && !countryList.isEmpty() && countryList.size() > 0) {
                    for (int i = 0; i < countryList.size(); i++) {
                        if (countryList.get(i).getId().equals(object.getCountry().getId())) {
                            flag = countryList.get(i).getImageString();
                            countryName1 = countryList.get(i).getCountryName();
                            Country.setText(countryName1);
                            Bflag = StringToBitMap(flag);
                            flag3.setImageBitmap(Bflag);
                            break;
                        }
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
        if (!this.isFinishing() && mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    public class TargetViewersAdapter extends RecyclerView.Adapter<TargetViewersAdapter.TargetViewersHolder> {

        private List<SubscriptionCategoryResponse> targetViewers;

        public TargetViewersAdapter(List<SubscriptionCategoryResponse> targetViewers) {
            this.targetViewers = targetViewers;
        }

        @Override
        public TargetViewersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TargetViewersHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_target_viewers, parent, false));
        }

        @Override
        public void onBindViewHolder(TargetViewersHolder holder, int position) {
            holder.tvTargetViewers.setText(targetViewers.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return targetViewers.size();
        }

        public class TargetViewersHolder extends RecyclerView.ViewHolder {

            private TextView tvTargetViewers;

            public TargetViewersHolder(View itemView) {
                super(itemView);
                tvTargetViewers = itemView.findViewById(R.id.tv_target_viewers);
            }
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
            e.printStackTrace();
        }
        return null;
    }
}
