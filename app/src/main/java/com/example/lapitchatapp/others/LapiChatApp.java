package com.example.lapitchatapp.others;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class LapiChatApp extends Application
{
    private DatabaseReference mdatabasereference;
    private FirebaseAuth mauth;

    @Override
    public void onCreate() {
        super.onCreate();


        mauth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mdatabasereference = FirebaseDatabase.getInstance().getReference().child("users");

        if (mauth.getCurrentUser()!= null)
        {
            mdatabasereference.child(mauth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null)
                    {
                        if(mauth.getCurrentUser()!=null)
                        mdatabasereference.child(mauth.getCurrentUser().getUid()).child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //create picasso offline ability
        }
    }
        }
