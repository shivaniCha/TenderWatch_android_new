package com.tenderWatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PatternMatcher;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.crazyorr.zoomcropimage.CropShape;
import com.github.crazyorr.zoomcropimage.ZoomCropImageActivity;
import com.google.gson.JsonArray;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tenderWatch.Models.CreateUser;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private TextView tvCouuntryCode;
    Intent intent;
    LinearLayout rlcountry, rlSelectContractorType;
    EditText country, mobileNo, aboutMe, occupation, contractorType, firstName, lastName;
    String countryName, countryCode, txtaboutMe;
    Button btnSignUp;
    ArrayList<String> empNo;
    CreateUser user = new CreateUser();
    SessionManager sessionManager;
    MultipartBody.Part email1, password1, country1, contactNo1, occupation1, aboutMe1, role1, deviceId1, image1;
    CircleImageView profileImg;
    final String[] permission = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private JsonArray jaSubrole = new JsonArray();
    private static final String TAG = SignUp.class.getSimpleName();

    private static final String KEY_PICTURE_URI = "KEY_PICTURE_URI";

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private static final int REQUEST_CODE_SELECT_PICTURE = 0;
    private static final int REQUEST_CODE_CROP_PICTURE = 1;

    private static final int PICTURE_WIDTH = 600;
    private static final int PICTURE_HEIGHT = 600;
    private final int REQUEST_PERMISSION_SETTING = 101;

    private LinearLayout back;
    private Uri mPictureUri;
    private Api mAPIService;
    private String ISO = "";
    Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_signup2);
        sessionManager = new SessionManager(SignUp.this);
        InitView();
        getSubRole();
        InitListener();
    }

    private void getSubRole() {
        mAPIService.getSubRole().enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    jaSubrole.addAll(response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void InitListener() {
        rlcountry.setOnClickListener(this);
        rlSelectContractorType.setOnClickListener(SignUp.this);
        aboutMe.setOnClickListener(this);
        profileImg.setOnClickListener(this);
        back.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

        mobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (!TextUtils.isEmpty(charSequence))
                    mobileNo.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence))
                    firstName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence))
                    lastName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mobileNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (TextUtils.isEmpty(tvCouuntryCode.getText())) {
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    SignUp.this);
                            builder.setTitle("Tender Watch");
                            builder.setMessage("Please select country first");
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            Intent intent = new Intent(SignUp.this, CountryList.class);
                                            intent.putExtra("check", "signup");
                                            startActivityForResult(intent, 1);
                                            dialog.dismiss();
                                        }
                                    });

                            if (!isFinishing())
                                builder.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void InitView() {
        rlcountry = (LinearLayout) findViewById(R.id.selectCountry);
        country = (EditText) findViewById(R.id.country);
        mobileNo = (EditText) findViewById(R.id.mobileNo);
        tvCouuntryCode = (TextView) findViewById(R.id.tv_country_code);
        aboutMe = (EditText) findViewById(R.id.edt_aboutme);
        profileImg = (CircleImageView) findViewById(R.id.circleView);
        btnSignUp = (Button) findViewById(R.id.btn_Next);
        occupation = (EditText) findViewById(R.id.occupation);
        rlSelectContractorType = (LinearLayout) findViewById(R.id.select_contractor_type);
        contractorType = (EditText) findViewById(R.id.contractor_type);
        firstName = (EditText) findViewById(R.id.edit_fname);
        lastName = (EditText) findViewById(R.id.edit_lname);
        mAPIService = ApiUtils.getAPIService();

        Intent show = getIntent();
        Bitmap newimg = show.getParcelableExtra("bitmap");
        if (newimg != null) {
            profileImg.setImageBitmap(newimg);
        }
        empNo = show.getStringArrayListExtra("Country");
        txtaboutMe = show.getStringExtra("aboutMe");
        back = (LinearLayout) findViewById(R.id.signup2_toolbar);
        if (txtaboutMe != null) {
            if (txtaboutMe.equals("About Me")) {
                aboutMe.setText(txtaboutMe);
            }
        }
        if (empNo != null) {
            countryName = empNo.get(0).split("~")[0];
            countryCode = empNo.get(0).split("~")[1];

            country.setText(countryName);
            tvCouuntryCode.setText("+"+countryCode);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.selectCountry:
                intent = new Intent(SignUp.this, CountryList.class);
                intent.putExtra("check", "signup");
                startActivityForResult(intent, 1);
                break;
            case R.id.edt_aboutme:
                intent = new Intent(SignUp.this, AboutMe.class);
                if (aboutMe.getText().toString().equals("About Me")) {
                    intent.putExtra("about", "About Me");
                } else {
                    intent.putExtra("about", txtaboutMe);
                }
                startActivityForResult(intent, 1);
                break;
            case R.id.circleView:
                checkSelfPermission();
//                if (requestPermissionWriteExternalStorage())
//                    SetProfile();
                break;
            case R.id.signup2_toolbar:
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                break;
            case R.id.btn_Next:
                SendData();
                break;
            case R.id.select_contractor_type:
                PopupMenu pum = new PopupMenu(SignUp.this, v);
                if (jaSubrole != null && jaSubrole.size() > 0) {

                    for (int i = 0; i < jaSubrole.size(); i++) {
                        pum.getMenu().add(jaSubrole.get(i).getAsString());
                    }
                } else {
                    Log.e(TAG, "onClick: NO sub role found");
                }
                pum.show();
                pum.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        contractorType.setText(item.getTitle().toString());
                        return false;

                    }
                });
                break;
        }
    }

    private boolean CheckValidation() {
        if (TextUtils.isEmpty(firstName.getText()))
            firstName.setError("First Name is Require");
        else
            firstName.setError(null);

        if (TextUtils.isEmpty(lastName.getText()))
            lastName.setError("Last Name is Require");
        else
            lastName.setError(null);
        if (TextUtils.isEmpty(mobileNo.getText()))
            mobileNo.setError("Mobile number is Require");
        else
            mobileNo.setError(null);

        if (!TextUtils.isEmpty(mobileNo.getText()) && !TextUtils.isEmpty(country.getText()) && !TextUtils.isEmpty(firstName.getText()) && !TextUtils.isEmpty(lastName.getText())) {
            String mobileno = mobileNo.getText().toString().replaceFirst("^0+(?!$)", "");
            if (!mobileno.equalsIgnoreCase(mobileNo.getText().toString()))
                mobileNo.setText(mobileno);

            mobileNo.getText().toString().trim();
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            try {
                if (phoneNumberUtil.isValidNumberForRegion(phoneNumberUtil.parse(mobileno, ISO), ISO)) {
                    mobileNo.setError(null);
                    return true;
                } else {
                    mobileNo.setError("Enter valid mobile number");
                    return false;
                }
            } catch (NumberParseException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    private void SendData() {
        if (!CheckValidation())
            return;
        String country1 = country.getText().toString();
        String mobile = mobileNo.getText().toString();
        String occupation1 = occupation.getText().toString();
        String aboutme = aboutMe.getText().toString();
        //String subRole=contractorType.getText().toString().trim();

        @SuppressLint("HardwareIds")
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String role = sessionManager.getPreferences(SignUp.this, "role");

        user.setCountry(country1);
        user.setContactNo(mobile);
        user.setOccupation(occupation1);
        user.setAboutMe(aboutme);
        user.setDeviceId(deviceId);
        user.setRole(role);
        user.setFirstName(firstName.getText().toString());
        user.setLastName(lastName.getText().toString());
//        user.setSubRole(subRole);
        intent = new Intent(SignUp.this, Agreement.class);
        intent.putExtra("isTrial", true);
        startActivityForResult(intent, 1);
       /* if (sessionManager.getPreferences(SignUp.this, "role").equalsIgnoreCase("contractor")) {
            intent = new Intent(SignUp.this, CountryList.class);
            intent.putExtra("fromSignUP", true);
            startActivityForResult(intent, 1);
            finish();
        } else {
            intent = new Intent(SignUp.this, Agreement.class);
            intent.putExtra("isTrial", true);
            startActivityForResult(intent, 1);
//            finish();
        }*/
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_PICTURE_URI, mPictureUri);
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

                        Handler handler=new Handler();
                        final Uri finalSelectedImageUri = selectedImageUri;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SignUp.this, ZoomCropImageActivity.class);
                                intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_URI, finalSelectedImageUri);
                                intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_OUTPUT_WIDTH, PICTURE_WIDTH);
                                intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_OUTPUT_HEIGHT, PICTURE_HEIGHT);
                                intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_CROP_SHAPE, CropShape.SHAPE_RECTANGLE);   //optional
                                File croppedPicture = createPictureFile("cropped"+System.currentTimeMillis()+".png");
                                if (croppedPicture != null) {
                                    intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_SAVE_DIR,
                                            croppedPicture.getParent());   //optional
                                    intent.putExtra(ZoomCropImageActivity.INTENT_EXTRA_FILE_NAME,
                                            croppedPicture.getName());   //optional
                                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), croppedPicture);
                                    user.setProfilePhoto(croppedPicture);
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
                    if (resultCode == Activity.RESULT_OK) {
                        if (data.getStringExtra("aboutMe") != null) {
                            txtaboutMe = data.getStringExtra("aboutMe");
                            if (txtaboutMe.equals("About Me")) {
                                aboutMe.setText(txtaboutMe);
                            } else {
                                if (txtaboutMe.length() > 10) {
                                    aboutMe.setText(txtaboutMe.substring(0, 10) + "....");
                                } else {
                                    aboutMe.setText(txtaboutMe);
                                }
                            }
                            user.setAboutMe(txtaboutMe);
                        }
                        if (data.getStringArrayListExtra("Country") != null) {
                            ISO = data.getStringExtra("ISO");
                            ArrayList<String> result = data.getStringArrayListExtra("Country");
                            Log.i(TAG, String.valueOf(result));
                            if (result != null && result.size() > 0) {
                                countryName = result.get(0).split("~")[0];
                                countryCode = result.get(0).split("~")[1].split("~")[0];
                                country.setText(countryName);
                                tvCouuntryCode.setText("+"+countryCode+" -");
                                mobileNo.requestFocus();
                            }
                        }
                    }
                    if (resultCode == Activity.RESULT_CANCELED) {
                        //Write your code if there's no result
                    }
                }
                switch (resultCode) {
                    case ZoomCropImageActivity.CROP_SUCCEEDED:

                        if (data != null) {
                            Uri croppedPictureUri = data
                                    .getParcelableExtra(ZoomCropImageActivity.INTENT_EXTRA_URI);
                            profileImg.setImageURI(null);
                            profileImg.setImageURI(croppedPictureUri);

                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), croppedPictureUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case ZoomCropImageActivity.CROP_CANCELLED:
                        break;
                    case ZoomCropImageActivity.CROP_FAILED:
                        break;
                }
                break;
            case REQUEST_PERMISSION_SETTING:
                checkSelfPermission();
//                if (requestPermissionWriteExternalStorage())
//                    SetProfile();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    finish();
                }
                break;
            }
        }
    }

    private boolean requestPermissionWriteExternalStorage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int requestCode = MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
            if (ContextCompat.checkSelfPermission(this, permission[0]) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, permission[1]) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission[1]) || ActivityCompat.shouldShowRequestPermissionRationale(this, permission[0])) {
                    ActivityCompat.requestPermissions(this, permission, requestCode);
                } else {
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                SignUp.this);
                        builder.setTitle("Tender Watch");
                        builder.setMessage("Please grant permission to access camera and storage, to upload profile photo");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                        dialog.dismiss();
                                    }
                                });

                        if (!isFinishing())
                            builder.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ProgressDialog mProgressDialog = new ProgressDialog(SignUp.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // Set progressdialog title
            mProgressDialog.setTitle("Download Image Tutorial");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();

                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            mProgressDialog.dismiss();
        }
    }

    private void checkSelfPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissionRequire = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            };
            Dexter.withActivity(this)
                    .withPermissions(permissionRequire)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.isAnyPermissionPermanentlyDenied()) {
                                new AlertDialog.Builder(SignUp.this)
                                        .setTitle("Need Permission")
                                        .setMessage("This app needs storage, camera permission to upload tender image")
                                        .setCancelable(false)
                                        .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                SetProfile();
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
}
