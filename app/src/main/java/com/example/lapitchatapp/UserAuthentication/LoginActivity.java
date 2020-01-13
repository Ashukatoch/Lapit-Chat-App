package com.example.lapitchatapp.UserAuthentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lapitchatapp.Activities.MainActivity;
import com.example.lapitchatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity
{
    private TextInputEditText memail,mpass;
    private FirebaseAuth mauth;
    private Button login;
    private Toolbar mtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mauth=FirebaseAuth.getInstance();
        login=findViewById(R.id.login_btn);
        mtoolbar=findViewById(R.id.login_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        memail=findViewById(R.id.loginemailedittext);
        mpass=findViewById(R.id.loginpasswordedittext);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email=memail.getText().toString().trim();
                String password=mpass.getText().toString().trim();
                if(TextUtils.isEmpty(email))
                {
                    memail.setError("Required Field....");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    mpass.setError("Required Field....");
                    return;
                }
                mauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(),"Login in Successfull:)",Toast.LENGTH_LONG).show();
                            Intent mainintent=new Intent(getApplicationContext(), MainActivity.class);
                            mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainintent);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Login failed:(",Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });
    }
}
