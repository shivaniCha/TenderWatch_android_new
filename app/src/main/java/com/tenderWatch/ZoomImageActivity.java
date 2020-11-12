package com.tenderWatch;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.util.Util;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tenderWatch.SharedPreference.SessionManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ZoomImageActivity extends AppCompatActivity {

    private ImageView ivZoomTenderImage;
    private String tenderImage;
    private ProgressBar pbLoadImage;

    private final String TAG = ZoomImageActivity.class.getSimpleName();
    final String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Tender Image");

        String name = "";
        if (getIntent() != null && getIntent().hasExtra("tenderImageName")) {
            name = getIntent().getStringExtra("tenderImageName");
        }
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TenderWatch";
        new File(path).mkdir();
        final File file = new File(path, name);

        ivZoomTenderImage = (ImageView) findViewById(R.id.iv_zoom_tender_image);
        pbLoadImage = (ProgressBar) findViewById(R.id.pb_load_image);
        if (getIntent().hasExtra("tenderImage")) {
            tenderImage = getIntent().getStringExtra("tenderImage");
        } else {
            finish();
        }

        if (!TextUtils.isEmpty(tenderImage)) {
            //if (URLUtil.isValidUrl(tenderImage)) {
            try {
                Picasso.with(ZoomImageActivity.this).load(tenderImage).placeholder(R.drawable.avtar).resize(600, 400).centerInside().into(ivZoomTenderImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        pbLoadImage.setVisibility(View.GONE);

                        /*try {
                            if (!file.exists())
                                new AlertDialog.Builder(ZoomImageActivity.this)
                                        .setMessage(Html.fromHtml("To download Original(Full Resolution) image please click on <b>Follow Tender Process</b> on tender detail screen. or download now."))
                                        .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                setupImageDounload();
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .show();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }*/
                    }

                    @Override
                    public void onError() {
                        pbLoadImage.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        try {

            ivZoomTenderImage.setOnTouchListener(new ImageMatrixTouchHandler(ivZoomTenderImage.getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupImageDounload() {
        try {

            Dexter.withActivity(ZoomImageActivity.this)
                    .withPermission(permission)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                            try {
                                new DownloadImage().execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(ZoomImageActivity.this, "Please grant storage permission to download image.", Toast.LENGTH_SHORT).show();
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

    private void loadImage(int size) {
        if (size == 200) {
            Toast.makeText(this, "something wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        if (size == 0) {
            try {
                Picasso.with(ZoomImageActivity.this).load(tenderImage).placeholder(R.drawable.avtar).into(ivZoomTenderImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        pbLoadImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        loadImage(1200);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                loadImage(1200);
            }
        } else {
            try {
                Picasso.with(ZoomImageActivity.this).load(tenderImage).placeholder(R.drawable.avtar).resize(size, size).into(ivZoomTenderImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        pbLoadImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                loadImage(size - 200);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class DownloadImage extends AsyncTask<URL, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            //pbLoadImage.setVisibility(View.VISIBLE);
            Log.e(TAG, "onPreExecute: Download Start");
        }

        @Override
        protected Bitmap doInBackground(URL... urls) {
            URL url = null;
            try {
                url = new URL(tenderImage);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
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
            String name = "";
            if (getIntent() != null && getIntent().hasExtra("tenderImageName")) {
                name = getIntent().getStringExtra("tenderImageName");
            }
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
            //pbLoadImage.setVisibility(View.GONE);
            return Uri.parse(file.getAbsolutePath());
        } catch (IOException e) {
            //pbLoadImage.setVisibility(View.GONE);
            e.printStackTrace();
        }
        return null;
    }
}
