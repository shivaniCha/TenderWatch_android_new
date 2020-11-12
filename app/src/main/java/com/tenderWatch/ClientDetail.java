package com.tenderWatch;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tenderWatch.Models.GetCountry;
import com.tenderWatch.Models.ResponseRating;
import com.tenderWatch.Models.Sender;
import com.tenderWatch.Models.User;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;
import com.tenderWatch.utils.JustifyTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lcom47 on 3/1/18.
 */

public class ClientDetail extends AppCompatActivity {
    CircleImageView clientImage;
    ImageView close, flag, eRat1, eRat2, eRat3, eRat4, eRat5, fRat1, fRat2, fRat3, fRat4, fRat5;
    TextView email, mobile, country, occcupation, aboutMe, txtRate, tvRating;
    String sender, jsonString;
    Sender obj;
    Api mApiService;
    private List Data;
    ArrayList<GetCountry> countryList = new ArrayList<>();
    private static final ArrayList<String> falpha = new ArrayList<String>();
    private static final ArrayList<String> fcountryName = new ArrayList<String>();
    SessionManager sessionManager;
    private static final String TAG = ClientDetail.class.getSimpleName();
    String rate;
    Button btnClientSubmit;
    private ProgressDialog mProgressDialog;
    private float oldRate = 0.0f;
    private RatingBar rbClientProfile;
    private TextView tvCountryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_client_drawer);

        sessionManager = new SessionManager(ClientDetail.this);

        mApiService = ApiUtils.getAPIService();
        if (getIntent().getStringExtra("sender").contains("null")) {
            finish();
        }
        sender = getIntent().getStringExtra("sender");
        if (getIntent().hasExtra("data"))
            jsonString = getIntent().getStringExtra("data");
        flag = (ImageView) findViewById(R.id.c_flag);
        eRat1 = (ImageView) findViewById(R.id.e_s1);
        eRat2 = (ImageView) findViewById(R.id.e_s2);
        eRat3 = (ImageView) findViewById(R.id.e_s3);
        eRat4 = (ImageView) findViewById(R.id.e_s4);
        eRat5 = (ImageView) findViewById(R.id.e_s5);
        fRat1 = (ImageView) findViewById(R.id.f_s1);
        fRat2 = (ImageView) findViewById(R.id.f_s2);
        fRat3 = (ImageView) findViewById(R.id.f_s3);
        fRat4 = (ImageView) findViewById(R.id.f_s4);
        fRat5 = (ImageView) findViewById(R.id.f_s5);
        close = (ImageView) findViewById(R.id.img_close);
        email = (TextView) findViewById(R.id.txt_client_email);
        mobile = (TextView) findViewById(R.id.txt_client_mobileNo);
        country = (TextView) findViewById(R.id.txt_client_country);
        occcupation = (TextView) findViewById(R.id.txt_client_occupation);
        aboutMe = (TextView) findViewById(R.id.txt_client_aboutme);
        clientImage = (CircleImageView) findViewById(R.id.client_circleView);
        btnClientSubmit = (Button) findViewById(R.id.btn_client_submit);
        txtRate = (TextView) findViewById(R.id.txt_avgRate);
        tvRating = (TextView) findViewById(R.id.tv_rating);
        rbClientProfile = (RatingBar) findViewById(R.id.rb_client_profile);
        tvCountryCode = (TextView) findViewById(R.id.tv_country_code);

        /*eRat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fRat1.getVisibility() == View.VISIBLE && fRat2.getVisibility() != View.VISIBLE) {
                    fRat1.setVisibility(View.GONE);
                    tvRating.setText("");
                } else {
                    fRat1.setVisibility(View.VISIBLE);
                    tvRating.setText("Poor");
                }
                fRat2.setVisibility(View.GONE);
                fRat3.setVisibility(View.GONE);
                fRat4.setVisibility(View.GONE);
                fRat5.setVisibility(View.GONE);
                rate = String.valueOf(1);
            }
        });
        eRat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fRat1.setVisibility(View.VISIBLE);
                fRat2.setVisibility(View.VISIBLE);
                fRat3.setVisibility(View.GONE);
                fRat4.setVisibility(View.GONE);
                fRat5.setVisibility(View.GONE);
                rate = String.valueOf(2);
                tvRating.setText("Average");
            }
        });
        eRat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fRat1.setVisibility(View.VISIBLE);
                fRat2.setVisibility(View.VISIBLE);
                fRat3.setVisibility(View.VISIBLE);
                fRat4.setVisibility(View.GONE);
                fRat5.setVisibility(View.GONE);
                rate = String.valueOf(3);
                tvRating.setText("Good");
            }
        });
        eRat4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fRat1.setVisibility(View.VISIBLE);
                fRat2.setVisibility(View.VISIBLE);
                fRat3.setVisibility(View.VISIBLE);
                fRat4.setVisibility(View.VISIBLE);
                fRat5.setVisibility(View.GONE);
                rate = String.valueOf(4);
                tvRating.setText("Very Good");
            }
        });
        eRat5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fRat1.setVisibility(View.VISIBLE);
                fRat2.setVisibility(View.VISIBLE);
                fRat3.setVisibility(View.VISIBLE);
                fRat4.setVisibility(View.VISIBLE);
                fRat5.setVisibility(View.VISIBLE);
                rate = String.valueOf(5);
                tvRating.setText("Excellent");
            }
        });*/
        DisplayDetail();
        GetUser();
        btnClientSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callRatingApi();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mobile.getText().toString())) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + tvCountryCode.getText().toString()+"-"+mobile.getText().toString()));
                    startActivity(intent);
                }
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(email.getText().toString())) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email.getText().toString()});
                    intent.setType("text/plain");
                    startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                }
            }
        });

        rbClientProfile.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    if (rating < 1.1f) {
                        tvRating.setText("Poor");
                    } else if (rating < 2.2f) {
                        tvRating.setText("Average");
                    } else if (rating < 3.3f) {
                        tvRating.setText("Good");
                    } else if (rating < 4.4f) {
                        tvRating.setText("Very Good");
                    } else {
                        tvRating.setText("Excellent");
                    }
                    Log.e(TAG, "onRatingChanged: new ating : " + rating);
                }
            }
        });
    }

    private void GetUser() {
        if (obj != null) {
            String token = "Bearer " + sessionManager.getPreferences(ClientDetail.this, "token");
            String userId = obj.getId();
            showProgressDialog();

            mApiService.getUserDetail(token, userId).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    dismissProgressDialog();
                    Log.i(TAG, "post submitted to API." + response);
                    String rateAverage = response.body().getAvg().toString();
                    rbClientProfile.setRating(Float.valueOf(response.body().getReview().getRating().toString()));

                    txtRate.setText(rateAverage + "/5.0");
                /*if (!TextUtils.isEmpty(rateAverage)) {
                    int rating = Math.round(Float.parseFloat(rateAverage));
                    if (rating == 1)
                        eRat1.performClick();
                    else if (rating == 2)
                        eRat2.performClick();
                    else if (rating == 3)
                        eRat3.performClick();
                    else if (rating == 4)
                        eRat4.performClick();
                    else if (rating == 5)
                        eRat5.performClick();
                }*/
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    dismissProgressDialog();
                    Log.i(TAG, "post submitted to API." + t);
                }
            });
        }
    }


    private void callRatingApi() {
        showProgressDialog();

        String token = "Bearer " + sessionManager.getPreferences(ClientDetail.this, "token");
        String clientId = obj.getId();
        Log.e(TAG, "callRatingApi: new Rating : " + rbClientProfile.getRating());
        mApiService.giveRating(token, clientId, String.valueOf(Math.round(rbClientProfile.getRating()))).enqueue(new Callback<ResponseRating>() {
            @Override
            public void onResponse(Call<ResponseRating> call, Response<ResponseRating> response) {
                dismissProgressDialog();
                sessionManager.ShowDialog(ClientDetail.this, "Thank you for rating");

                txtRate.setText(response.body().getAvg() + "/5.0");
                rbClientProfile.setRating(Float.valueOf(response.body().getRating().toString()));
                /*int rating = response.body().getRating();
                if (rating == 1)
                    eRat1.performClick();
                else if (rating == 2)
                    eRat2.performClick();
                else if (rating == 3)
                    eRat3.performClick();
                else if (rating == 4)
                    eRat4.performClick();
                else if (rating == 5)
                    eRat5.performClick();*/

                Log.i(TAG, "post submitted to API." + response);
            }

            @Override
            public void onFailure(Call<ResponseRating> call, Throwable t) {
                dismissProgressDialog();
                Log.i(TAG, "post submitted to API." + t);
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
                    if (countryList.get(i).getCountryName().equalsIgnoreCase(obj.getCountry())) {
                        country.setText(countryList.get(i).getCountryName());
                        Bitmap bitmap = StringToBitMap(countryList.get(i).getImageString());
                        flag.setImageBitmap(bitmap);
                        tvCountryCode.setText("+"+countryList.get(i).getCountryCode());
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

    private void DisplayDetail() {
        Gson gson = new Gson();
        obj = gson.fromJson(sender, Sender.class);

        if (obj != null) {
            if (!TextUtils.isEmpty(obj.getProfilePhoto())) {

                Picasso.with(this).load(obj.getProfilePhoto()).placeholder(getResources().getDrawable(R.drawable.avtar)).into(clientImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        findViewById(R.id.pb_image_loader).setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        findViewById(R.id.pb_image_loader).setVisibility(View.GONE);
                        clientImage.setImageDrawable(getResources().getDrawable(R.drawable.avtar));
                    }
                });
            } else {
                findViewById(R.id.pb_image_loader).setVisibility(View.GONE);
            }
            FGetAllCountry();
            email.setText(obj.getEmail());
            mobile.setIncludeFontPadding(false);
            mobile.setText("-"+obj.getContactNo());
//            mobile.setText("Contact");
            occcupation.setText(obj.getOccupation());
            aboutMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(ClientDetail.this);
                    dialog.setContentView(R.layout.client_detail);
                    final JustifyTextView code = (JustifyTextView) dialog.findViewById(R.id.dialog_aboutMe);
                    code.setText(obj.getAboutMe());
                    dialog.show();
                }
            });
        } else {
            finish();
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(ClientDetail.this);
        mProgressDialog.setMessage("Loading....");
        mProgressDialog.setCancelable(false);

        if (!ClientDetail.this.isFinishing() && !mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        if (!ClientDetail.this.isFinishing() && mProgressDialog != null)
            mProgressDialog.dismiss();
    }
}
