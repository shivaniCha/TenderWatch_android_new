package com.tenderWatch.Drawer;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.crazyorr.zoomcropimage.CropShape;
import com.github.crazyorr.zoomcropimage.ZoomCropImageActivity;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.squareup.picasso.Picasso;
import com.tenderWatch.AboutMe;
import com.tenderWatch.ClientDrawer.ClientDrawer;
import com.tenderWatch.CountryList;
import com.tenderWatch.Models.CreateUser;
import com.tenderWatch.Models.GetCountry;
import com.tenderWatch.Models.User;
import com.tenderWatch.R;
import com.tenderWatch.Retrofit.Api;
import com.tenderWatch.Retrofit.ApiUtils;
import com.tenderWatch.SharedPreference.SessionManager;
import com.tenderWatch.SignUp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.tenderWatch.SignUp.createPictureFile;

/**
 * Created on 14/12/17.
 */

public class EditProfile extends Fragment implements View.OnClickListener {
    EditText txtCountry, txtMobileNo, txtOccupation, txtAboutMe, txtFName, txtLName;
    private TextView tvCountryCode;
    Api mAPIService;
    CreateUser users = new CreateUser();
    LinearLayout rlCountryList;
    Button btnUpdate, btnRemoveAccount;
    CircleImageView profileImg;
    Intent intent;
    String txtaboutMe, countryName1, countryCode1;
    private Uri mPictureUri;
    SessionManager sessionManager;
    User user;
    MultipartBody.Part email1, password1, country1, contactNo1, occupation1, aboutMe1, role1, deviceId1, image1, firstName, lastName;
    final String[] permission = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private static final String TAG = com.tenderWatch.ClientDrawer.EditProfile.class.getSimpleName();

    private static final String KEY_PICTURE_URI = "KEY_PICTURE_URI";

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private static final int REQUEST_CODE_SELECT_PICTURE = 0;
    private static final int REQUEST_CODE_CROP_PICTURE = 1;

    private static final int PICTURE_WIDTH = 600;
    private static final int PICTURE_HEIGHT = 600;

    private String ISO = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_editprofile, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAPIService = ApiUtils.getAPIService();
        sessionManager = new SessionManager(getActivity());
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Edit Profile");
        txtAboutMe = view.findViewById(R.id.edit_aboutme);
        txtFName = view.findViewById(R.id.edit_fname);
        txtLName = view.findViewById(R.id.edit_lname);
        txtCountry = view.findViewById(R.id.edit_country);
        txtMobileNo = view.findViewById(R.id.edit_mobileNo);
        tvCountryCode = view.findViewById(R.id.tv_country_code);
        txtOccupation = view.findViewById(R.id.edit_occupation);
        rlCountryList = view.findViewById(R.id.edit_rlselectCountry);
        btnUpdate = view.findViewById(R.id.edit_btn_Update);
        profileImg = view.findViewById(R.id.edit_circleView);
        btnRemoveAccount = view.findViewById(R.id.edit_btn_remove);

        user = sessionManager.getPreferencesObject(getActivity());

        Picasso.with(getApplicationContext()).load(user.getProfilePhoto())
                .placeholder(R.drawable.avtar).error(R.drawable.avtar)
                .into(profileImg);
        String c = ((User) user).getCountry();
        txtCountry.setText(c);
        txtaboutMe = ((User) user).getAboutMe();
        if (txtaboutMe.equals("")) {
            txtaboutMe = "About Me";
        }
        /*String[] mobile = user.getContactNo().split("-");
        if (mobile.length > 1) {
            tvCountryCode.setText(mobile[0]);
            txtMobileNo.setText(mobile[1]);
        } else {
            txtMobileNo.setText(user.getContactNo());
        }*/
        txtFName.setText(user.getFirstName());
        txtFName.setSelection(txtFName.getText().length());
        txtLName.setText(user.getLastName());
        txtAboutMe.setText(txtaboutMe);
        txtOccupation.setText(user.getOccupation());
        FGetAllCountry();
//        if(user.getContactNo().contains("+"))
//        {
////            String contactnumber=user.getContactNo().replace(user.getContactNo().substring(0,4),"");
////            Log.d(TAG+"1", "onViewCreated:"+contactnumber);
//            if(txtMobileNo.getText().toString().contains("-")){
//                txtMobileNo.setText(contactnumber);
//            }
//            txtMobileNo.setText("-"+contactnumber);
//        }
//        else
//        {
////            Log.d(TAG, "onViewCreated:"+user.getContactNo());
//            if(txtMobileNo.getText().toString().contains("-")){
//                txtMobileNo.setText(user.getContactNo());
//            }
//            else{
//                txtMobileNo.setText("-"+user.getContactNo());
//            }
//
//        }
        if (user.getContactNo().contains("-")) {
            txtMobileNo.setText(user.getContactNo());
        } else {
            txtMobileNo.setText("-" + user.getContactNo());
        }
        InitListener();

    }

    private void InitListener() {
        rlCountryList.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnRemoveAccount.setOnClickListener(this);
        profileImg.setOnClickListener(this);
        txtAboutMe.setOnClickListener(this);

        /*txtMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CheckValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.edit_rlselectCountry:
                intent = new Intent(getActivity(), CountryList.class);
                intent.putExtra("check", "signup");
                startActivityForResult(intent, 1);
                break;
            case R.id.edit_btn_Update:
                if (CheckValidation())
                    ApiCall();
                break;
            case R.id.edit_circleView:
                if (requestPermissionWriteExternalStorage())
                    SetProfile();
                break;
            case R.id.edit_aboutme:
                intent = new Intent(getActivity(), AboutMe.class);
                if (txtAboutMe.getText().toString().equals("About Me")) {
                    intent.putExtra("about", "About Me");
                } else {
                    intent.putExtra("about", txtaboutMe);
                }
                startActivityForResult(intent, 1);
                break;
            case R.id.edit_btn_remove:
                onRemoveAccount();
                break;
        }
    }

    private void onRemoveAccount() {
        new AlertDialog.Builder(getContext())
                .setTitle("Remove Account")
                .setMessage("Are you sure you want to remove your account from TenderWatch? This will permanently remove all your data and presence on TenderWatch ?")
                .setPositiveButton("remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String token = "Bearer " + sessionManager.getPreferences(getActivity(), "token");
                        String userId = user.getId();
                        mAPIService.removeAccount(token, userId).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    sessionManager.removeContractor();
                                    ((MainDrawer) getActivity()).Logout();
                                } else {
                                    Toast.makeText(getContext(), "Something wrong to remove account", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.e(TAG, "onFailure: error : " + t.getMessage());
                            }
                        });
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
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

    private void FGetAllCountry() {

        mAPIService.getCountryData().enqueue(new Callback<ArrayList<GetCountry>>() {
            @Override
            public void onResponse(Call<ArrayList<GetCountry>> call, Response<ArrayList<GetCountry>> response) {

                for (int i = 0; i < response.body().size(); i++) {
                    if (response.body().get(i).getCountryName().equals(user.getCountry())) {
                        ISO = response.body().get(i).getIsoCode();
                        tvCountryCode.setText("+" + response.body().get(i).getCountryCode());
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetCountry>> call, Throwable t) {
            }
        });

    }

    private boolean CheckValidation() {
        if (TextUtils.isEmpty(txtFName.getText()))
            txtFName.setError("First Name is Require");
        else
            txtFName.setError(null);

        if (TextUtils.isEmpty(txtLName.getText()))
            txtLName.setError("Last Name is Require");
        else
            txtLName.setError(null);


        if (TextUtils.isEmpty(txtMobileNo.getText()))
            txtMobileNo.setError("Mobile number is Require");
        else
            txtMobileNo.setError(null);

        if (!TextUtils.isEmpty(txtMobileNo.getText()) && !TextUtils.isEmpty(txtCountry.getText()) && !TextUtils.isEmpty(txtFName.getText()) && !TextUtils.isEmpty(txtLName.getText())) {
            if (((User) user).getContactNo().equalsIgnoreCase(txtMobileNo.getText().toString())) {
                return true;
            }
            String mobileno = txtMobileNo.getText().toString().replaceFirst("^0+(?!$)", "");
            if (!mobileno.equalsIgnoreCase(txtMobileNo.getText().toString()))
                txtMobileNo.setText(mobileno);

            txtMobileNo.getText().toString().trim();
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            try {
                if (phoneNumberUtil.isValidNumberForRegion(phoneNumberUtil.parse(mobileno, ISO), ISO)) {
                    txtMobileNo.setError(null);
                    return true;
                } else {
                    txtMobileNo.setError("Enter valid mobile number");
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

    private void ApiCall() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        if (!getActivity().isFinishing())
            progressDialog.show();
        File file1 = users.getProfilePhoto();

        RequestBody requestFile = null;
        String country = txtCountry.getText().toString();
        String mobile = txtMobileNo.getText().toString();
        String occupation = txtOccupation.getText().toString();
        String about = txtaboutMe;
        String Id = user.getId();
        String firstName = txtFName.getText().toString().trim();
        String lastName = txtLName.getText().toString().trim();

        country1 = MultipartBody.Part.createFormData("country", country);
        contactNo1 = MultipartBody.Part.createFormData("contactNo", mobile);
        occupation1 = MultipartBody.Part.createFormData("occupation", occupation);
        aboutMe1 = MultipartBody.Part.createFormData("aboutMe", about);
        this.firstName = MultipartBody.Part.createFormData("firstName", firstName);
        this.lastName = MultipartBody.Part.createFormData("lastName", lastName);

        if (users.getProfilePhoto() != null) {
            requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
            image1 = MultipartBody.Part.createFormData("image", file1.getName(), requestFile);
        } else {
            image1 = MultipartBody.Part.createFormData("image", "");
        }
        String token = "Bearer " + sessionManager.getPreferences(getActivity(), "token");
        Call<User> resultCall = mAPIService.UpdateUser(token, Id, country1, contactNo1, occupation1, aboutMe1, image1, this.firstName, this.lastName);

        resultCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!getActivity().isFinishing())
                    progressDialog.dismiss();
                Log.i(TAG, "response register-->");
                if (response.isSuccessful()) {
                    sessionManager.setPreferencesObject(getActivity(), response.body());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Tender Watch");
                    builder.setMessage("Profile Update Successful");
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    intent = new Intent(getActivity(), MainDrawer.class);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                    if (!getActivity().isFinishing())
                        builder.show();
                } else {
                    sessionManager.ShowDialog(getActivity(), response.errorBody().source().toString().split("\"")[3]);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (!getActivity().isFinishing())
                    progressDialog.dismiss();
                sessionManager.ShowDialog(getActivity(), "Server is down. Come back later!!");
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //requestPermissionWriteExternalStorage();
        try {
            ((MainDrawer) getActivity()).setOptionMenu(false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                        final Uri finalSelectedImageUri = selectedImageUri;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getActivity(), ZoomCropImageActivity.class);
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
                                    users.setProfilePhoto(croppedPicture);
                                    //image1 = MultipartBody.Part.createFormData("image", croppedPicture.getName(), requestFile);
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
                                txtAboutMe.setText(txtaboutMe);
                            } else {
                                if (txtaboutMe.length() > 10) {
                                    txtAboutMe.setText(txtaboutMe.substring(0, 10) + "....");
                                } else {
                                    txtAboutMe.setText(txtaboutMe);
                                }
                            }
                            // user.setAboutMe(txtaboutMe);
                        }
                        if (data.getStringArrayListExtra("Country") != null) {
                            ISO = data.getStringExtra("ISO");
                            ArrayList<String> result = data.getStringArrayListExtra("Country");
                            if (result.size() > 0) {
                                Log.i(TAG, String.valueOf(result));
                                countryName1 = result.get(0).split("~")[0];
                                countryCode1 = result.get(0).split("~")[1].split("~")[0];
                                txtCountry.setText(countryName1);
                                tvCountryCode.setText("+" + countryCode1);
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getActivity().finish();
                }
                break;
            }
        }
    }

    private boolean requestPermissionWriteExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            final int requestCode = MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

            if (ContextCompat.checkSelfPermission(getContext(), permission[0]) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getContext(), permission[1]) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission[1]) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission[0])) {
                    ActivityCompat.requestPermissions(getActivity(), permission, requestCode);
                } else {
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Tender Watch");
                        builder.setMessage("Please grant permission to access camera and storage, to upload profile photo");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                        intent.setData(uri);
                                        startActivityForResult(intent, requestCode);
                                        dialog.dismiss();
                                    }
                                });

                        if (!getActivity().isFinishing())
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
}