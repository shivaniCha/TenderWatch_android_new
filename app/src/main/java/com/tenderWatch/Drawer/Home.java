package com.tenderWatch.Drawer;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tenderWatch.Models.User;
import com.tenderWatch.R;
import com.tenderWatch.SharedPreference.SessionManager;

/**
 * Created by lcom48 on 14/12/17.
 */

public class Home extends Fragment {
    User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = new SessionManager(getActivity()).getPreferencesObject(getActivity());
        Picasso.with(getActivity())
                .load(user.getProfilePhoto())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                        Log.v("Main", String.valueOf(bitmap));
                        //circledrawerimage.setImageBitmap(bitmap);
                        //main = bitmap;
                        // Picasso.with(SignUpSelection.this).load(profilePicUrl).into(target);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Log.v("Main", "errrorrrr");

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Home");
    }
}
