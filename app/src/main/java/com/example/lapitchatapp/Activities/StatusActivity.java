package com.example.lapitchatapp.Activities;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lapitchatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class StatusActivity extends AppCompatActivity
{
    private Toolbar mtoolbar;
    private FirebaseAuth mauth;
    private FirebaseUser muser;
    private DatabaseReference databaseReference;
    private Button change;
    private TextInputEditText S_status;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mtoolbar=findViewById(R.id.status_appbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mauth=FirebaseAuth.getInstance();
        S_status=findViewById(R.id.statusedittext);
        change=findViewById(R.id.ChangeStatus);
        muser=mauth.getCurrentUser();
        String current_uid=muser.getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String mStatus=S_status.getText().toString().trim();
                databaseReference.child("status").setValue(mStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            Toast.makeText(getApplicationContext(),"Changes Saved Successfully",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getApplicationContext(),"There was some problem in saving the chenges",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


    }


}
