package com.example.lapitchatapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lapitchatapp.R;
import com.example.lapitchatapp.UserAuthentication.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity
{
    private TextView mname,mstatus;
    private ImageView mimageView;
    private Button send_request_btn,Decline_request_btn;
    private DatabaseReference mdatabase,mfriendrequestdatabase,mfrienddatabase,mnotificationdatabase,mrootRef,newnotificationRef,mUserref;
    private FirebaseAuth mauth;
    private String userid,mcurrentstate;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        //Log.d("Tag",mauth.getUid());
        if( mauth.getCurrentUser()==null)
        {
            startActivity(new Intent(getApplicationContext(), StartActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userid=getIntent().getStringExtra("userid");
        Log.d("From",userid);
        mname=findViewById(R.id.user_profile_name);
        mstatus=findViewById(R.id.profile_status_id);
        send_request_btn=findViewById(R.id.send_request_btn_id);
        Decline_request_btn=findViewById(R.id.decline_request_btn_id);
        Decline_request_btn.setVisibility(View.INVISIBLE);
        mimageView=findViewById(R.id.imageView);
        mrootRef=FirebaseDatabase.getInstance().getReference();
        mauth=FirebaseAuth.getInstance();

        mUserref=FirebaseDatabase.getInstance().getReference().child("users").child(mauth.getCurrentUser().getUid());
        mnotificationdatabase=FirebaseDatabase.getInstance().getReference().child("notification");
        mcurrentstate="not_friends";
        mdatabase= FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        mfriendrequestdatabase=FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        mfrienddatabase=FirebaseDatabase.getInstance().getReference().child("Friends");
        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
               mname.setText(dataSnapshot.child("name").getValue().toString());
               mstatus.setText(dataSnapshot.child("status").getValue().toString());
               Picasso.with(getApplicationContext()).load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.avatar).into(mimageView);
               mfriendrequestdatabase.child(mauth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                   {
                    if(dataSnapshot.hasChild(userid))
                    {
                        String req_type=dataSnapshot.child(userid).child("Request_type").getValue().toString();
                        if(req_type.matches("Sent"))
                        {
                            mcurrentstate="Req_sent";
                            send_request_btn.setText("CANCEL FRIEND REQUEST");
                        }
                        else if(req_type.matches("Recieved"))
                        {
                            mcurrentstate="Request_Recieved";
                            send_request_btn.setText("ACCEPT FRIEND REQUEST");
                            Decline_request_btn.setVisibility(View.VISIBLE);
                        }
                    }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mfrienddatabase.child(mauth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
            if(dataSnapshot.hasChild(userid))
            {
                mcurrentstate="friends";
                Decline_request_btn.setVisibility(View.INVISIBLE);
                send_request_btn.setText("UNFRIEND THIS FRIEND");

            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        send_request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //            ----------Send Request state-----------
                if (mauth.getCurrentUser().getUid().equals(userid))
                    mcurrentstate = "friends";


                if (mcurrentstate.matches("not_friends")) {
                    newnotificationRef = mrootRef.child("notification").child(userid).push();
                    String notificationid = newnotificationRef.getKey();

                    HashMap<String, String> notificationdata = new HashMap<>();
                    notificationdata.put("from", mauth.getCurrentUser().getUid());
                    notificationdata.put("type", "request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_Request/" + mauth.getCurrentUser().getUid() + "/" + userid + "/Request_type", "Sent");
                    requestMap.put("Friend_Request/" + userid + "/" + mauth.getCurrentUser().getUid() + "/Request_type", "Recieved");
                    requestMap.put("notification/" + userid + "/" + notificationid, notificationdata);
                    mrootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                String error = databaseError.getMessage();
                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "Update Successsfull!!", Toast.LENGTH_LONG).show();
                                send_request_btn.setEnabled(false);
                                mcurrentstate = "Request_sent";
                                send_request_btn.setText("CANCEL FRIEND REQUEST");

                            }

                        }
                    });

                }
                //     ---------Cancel Request state--------------
                if (mcurrentstate.matches("Request_sent")) {
                    mfriendrequestdatabase.child(mauth.getCurrentUser().getUid()).child(userid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Request cancelled!!", Toast.LENGTH_LONG).show();
                                mfriendrequestdatabase.child(userid).child(mauth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            send_request_btn.setEnabled(true);
                                            mcurrentstate = "not_friends";
                                            send_request_btn.setText("SEND FRIEND REQUEST");
                                            Toast.makeText(getApplicationContext(), "Request Cancelled!!", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Cancellation failed!!", Toast.LENGTH_LONG).show();

                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(getApplicationContext(), "Cancelation failed", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
                //   -------------Request Recieved---------------
                if (mcurrentstate.matches("Request_Recieved")) {
                    final String current_date = DateFormat.getDateTimeInstance().format(new Date());
                    Map friendMap = new HashMap();
                    friendMap.put("Friends/" + userid + "/" + mauth.getCurrentUser().getUid() + "/date", current_date);
                    friendMap.put("Friends/" + mauth.getCurrentUser().getUid() + "/" + userid + "/date", current_date);

                    friendMap.put("Friend_Request/" + userid + "/" + mauth.getCurrentUser().getUid(), null);
                    friendMap.put("Friend_Request/" + mauth.getCurrentUser().getUid() + "/" + userid, null);

                    mrootRef.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference)
                        {
                            if(databaseError==null)
                            {
                                mcurrentstate = "friends";
                                send_request_btn.setEnabled(true);
                                send_request_btn.setText("UNFRIEND THIS PERSON");

                                Decline_request_btn.setVisibility(View.INVISIBLE);
                                Decline_request_btn.setEnabled(false);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                            }

                        }
                    });


                }
                //-----------UNFRIEND PERSON--------------
                if(mcurrentstate.equals("friends"))
                {
                    send_request_btn.setEnabled(false);
                    Map unfriendMap=new HashMap();
                    unfriendMap.put("Friends/"+userid+"/"+mauth.getCurrentUser().getUid(),null);
                    unfriendMap.put("Friends/"+mauth.getCurrentUser().getUid()+"/"+userid,null);
                    mrootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference)
                        {
                            if(databaseError==null)
                            {
                                send_request_btn.setText("SEND FRIEND REQUEST");
                                send_request_btn.setEnabled(true);
                                mcurrentstate="not_friends";
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                    }
            }
        });
        Decline_request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Map declinerequestMap=new HashMap();
                declinerequestMap.put("Friend_Request/"+mauth.getCurrentUser().getUid()+"/"+userid,null);
                declinerequestMap.put("Friend_Request/"+userid+"/"+mauth.getCurrentUser().getUid(),null);
                mrootRef.updateChildren(declinerequestMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference)
                    {
                        if(databaseError==null)
                        {
                            Toast.makeText(getApplicationContext(),"Friend Request Rejected!!",Toast.LENGTH_LONG).show();
                            mcurrentstate="not_friends";
                            send_request_btn.setText("SEND FRIEND REQUEST");
                            Decline_request_btn.setEnabled(false);
                            Decline_request_btn.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });
    }
}