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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{
    private FirebaseAuth mauth;
    private TextInputEditText memail,mpass,mname;
    private Button regbtn;
    private Toolbar mtoolbar;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mauth=FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register);

        mtoolbar=findViewById(R.id.register_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        regbtn=findViewById(R.id.createaccount_btn);
        memail=findViewById(R.id.emailedittext);
        mpass=findViewById(R.id.passwordedittext);
        mname=findViewById(R.id.nameedittext);
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                final String displayname=mname.getText().toString().trim();
                String email=memail.getText().toString().trim();
                String password=mpass.getText().toString().trim();
                if(TextUtils.isEmpty(displayname))
                {
                    mname.setError("Required Field....");
                    return;
                }
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
                mauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser muser=mauth.getCurrentUser();
                            String uid=muser.getUid();
                            databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                            String tokenid= FirebaseInstanceId.getInstance().getToken();
                            HashMap<String,String>map=new HashMap<>();
                            map.put("tokenid",tokenid);
                            map.put("name",displayname);
                            map.put("status","Hi there,I'm using Lapit Chat App");
                            map.put("image","default");
                            map.put("thumbimage","default");
                            map.put("online", String.valueOf(ServerValue.TIMESTAMP));
                            databaseReference.setValue(map);

                            Toast.makeText(getApplicationContext(),"Sign in Successfull:)",Toast.LENGTH_LONG).show();
                            Intent mainintent=new Intent(getApplicationContext(), MainActivity.class);
                            mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainintent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Account not created:(",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

}
