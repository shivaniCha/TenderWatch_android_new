package com.tenderWatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tenderWatch.SharedPreference.SessionManager;

public class AboutMe extends AppCompatActivity {

    EditText edtAbotMe;
    TextView txtCount, txtSave;
    String AboutMe;
    int textCount = 1000;
    private Toolbar mToolbar;
    LinearLayout aboutmeBack;
    Intent intent, show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        mToolbar = (Toolbar) findViewById(R.id.mtoolbar2);
        setSupportActionBar(mToolbar);

        setTitle(getString(R.string.app_name));
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        edtAbotMe = (EditText) findViewById(R.id.txt_aboutme);
        txtCount = (TextView) findViewById(R.id.txt_count);
        txtSave = (TextView) findViewById(R.id.aboutme_save);
        aboutmeBack = (LinearLayout) findViewById(R.id.aboutme_back);
        show = getIntent();
        AboutMe = show.getStringExtra("about");
        if (!AboutMe.equals("About Me")) {
            edtAbotMe.setText(AboutMe);
            txtCount.setText(AboutMe.length() + "");
        } else
            txtCount.setText("0");

        edtAbotMe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                txtCount.setText(String.valueOf(edtAbotMe.getText().toString().length()));
                if (txtCount.getText().toString().length() == 1000) {
                    txtCount.setText(String.valueOf(textCount));
                    new SessionManager(AboutMe.this).ShowDialog(AboutMe.this, "Up tp 1000 Characters");
                }
                txtCount.setText(String.valueOf(edtAbotMe.getText().toString().length()));
            }
        });
        aboutmeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(AboutMe.this, SignUp.class);
                if (edtAbotMe.getText().toString().equals("")) {
                    intent.putExtra("aboutMe", "About Me");
                } else {
                    intent.putExtra("aboutMe", AboutMe);

                }
                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        });
        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(AboutMe.this, SignUp.class);
                intent.putExtra("aboutMe", edtAbotMe.getText().toString());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}
