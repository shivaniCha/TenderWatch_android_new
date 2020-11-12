package com.tenderWatch.ClientDrawer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tenderWatch.Drawer.MainDrawer;
import com.tenderWatch.R;
import com.tenderWatch.SharedPreference.SessionManager;

/**
 * Created by lcom47 on 26/12/17.
 */

public class Support extends Fragment {

    EditText userEmail,userSubject,userBodyText;
    private Button btnSend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_support_team, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Support");
        userEmail= view.findViewById(R.id.userEmail);
        userSubject= view.findViewById(R.id.subject);
        userBodyText= view.findViewById(R.id.bodyText);
        btnSend= view.findViewById(R.id.edit_btn_Update);
        userEmail.setText(new SessionManager(getActivity()).getPreferencesObject(getActivity()).getEmail());

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidate()){
                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"support@tenderwatch.com"});
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, userSubject.getText().toString().trim());
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, userBodyText.getText().toString().trim());
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((MainDrawer)getActivity()).setOptionMenu(false,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidate(){
        if(userSubject.getText().toString().trim().length()>3){
            userSubject.setError(null);
        }else {
            userSubject.setError("Enter Subject");
            userSubject.requestFocus();
            return false;
        }

        if(userBodyText.getText().toString().trim().length()>0){
            userBodyText.setError(null);
        }else{
            userBodyText.setError("Please enter detail");
            userBodyText.requestFocus();
            return  false;
        }
        return true;
    }
}