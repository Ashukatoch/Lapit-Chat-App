package com.example.lapitchatapp.UserAuthentication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.lapitchatapp.R;
import com.example.lapitchatapp.UserAuthentication.LoginActivity;
import com.example.lapitchatapp.UserAuthentication.RegisterActivity;

public class StartActivity extends AppCompatActivity
{
    private Button reg,login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        reg=findViewById(R.id.registrationbutton);
        login=findViewById(R.id.loginbtn);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }
}
