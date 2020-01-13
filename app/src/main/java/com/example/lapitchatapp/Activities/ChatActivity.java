package com.example.lapitchatapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lapitchatapp.Adapter.MessageAdapter;
import com.example.lapitchatapp.others.GetTimeAgo;
import com.example.lapitchatapp.Model.messages;
import com.example.lapitchatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity
{
private String mChatuser;
private String mname;
private Toolbar mtoolbar;
private DatabaseReference muserdatabase,mUserRef,mMessagesRef;
private FirebaseAuth mauth;
private StorageReference mImagestorage;

private CircleImageView mchatimage;
private TextView onlinestatus;
private TextView mchattitle;

private EditText mchatmessage;
private ImageButton madd;
private ImageButton msend;


private RecyclerView messageRecyclerView;
private List<messages> mMessageList=new ArrayList<>();
private MessageAdapter messageAdapter;
private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseReference mrootRef;

    private static int Total_Items_to_Load=10;
    private static int mcurrentpage=1;
    private static int itemPos=0;
    private String mLastKey;
    private String mLastprevkey;

    private String downloaduri;

    @Override
    protected void onStart()
    {
        super.onStart();
        if(mauth.getCurrentUser()!=null)
        {
            mUserRef.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mauth.getCurrentUser()!=null)
        {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mauth=FirebaseAuth.getInstance();
        mChatuser=getIntent().getStringExtra("userid");
        if(mauth.getCurrentUser()!=null)
        mUserRef=FirebaseDatabase.getInstance().getReference().child("users").child(mauth.getCurrentUser().getUid());
        muserdatabase= FirebaseDatabase.getInstance().getReference().child("users").child(mChatuser);
        mrootRef=FirebaseDatabase.getInstance().getReference();
        mImagestorage= FirebaseStorage.getInstance().getReference();


        mtoolbar=findViewById(R.id.chattoolbar);
        setSupportActionBar(mtoolbar);

        messageRecyclerView=findViewById(R.id.messages_recyclerView);
        messageRecyclerView.setHasFixedSize(true);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter=new MessageAdapter(mMessageList);
        messageRecyclerView.setAdapter(messageAdapter);
        swipeRefreshLayout=findViewById(R.id.message_swipe_layout);
        loadmessages();


        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mname=getIntent().getStringExtra("username");
       // getSupportActionBar().setTitle(mname);
        LayoutInflater inflater= (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_barView=inflater.inflate(R.layout.custom_layout,null);
        actionBar.setCustomView(action_barView);

        mchatimage=findViewById(R.id.custom_profile_image);
        onlinestatus=findViewById(R.id.custum_online_status);
        mchattitle=findViewById(R.id.custom_username);

        mchatmessage=findViewById(R.id.chat_message);
        msend=findViewById(R.id.chat_send);
        madd=findViewById(R.id.chatadd_btn);

        mchattitle.setText(mname);
        muserdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
             String online=dataSnapshot.child("online").getValue().toString();
             String image=dataSnapshot.child("image").getValue().toString();
             if(online.equals("true"))
             {
                 onlinestatus.setText("Online");
             }
             else
             {
                 GetTimeAgo g=new GetTimeAgo();
                 long lasttime=Long.parseLong(online);
                 String lastSeentime=g.getTimeAgo(lasttime,getApplicationContext());
                 Log.d("Time", lastSeentime);
                 onlinestatus.setText(lastSeentime);

             }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(mauth.getCurrentUser()!=null)
        mrootRef.child("Chat").child(mauth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
             if(!dataSnapshot.hasChild(mChatuser))
             {
                   Map chataddmap=new HashMap();
                   chataddmap.put("seen",false);
                   chataddmap.put("timestamp",ServerValue.TIMESTAMP);

                   Map chatUserMap=new HashMap();
                   chatUserMap.put("Chat/"+mChatuser+"/"+mauth.getCurrentUser().getUid(),chataddmap);
                   chatUserMap.put("Chat/"+mauth.getCurrentUser().getUid()+"/"+mChatuser,chataddmap);

                   mrootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                       @Override
                       public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference)
                       {
                        if(databaseError!=null)
                        {
                            Toast.makeText(ChatActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                       }
                   });
             }
             else
             {

             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        msend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sendmessage();

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
             mcurrentpage++;
             itemPos=0;
             loadmoremessages();
            }
        });

        madd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
             Intent galleryintent=new Intent();
             galleryintent.setType("image/*");
             galleryintent.setAction(Intent.ACTION_GET_CONTENT);

             startActivityForResult(Intent.createChooser(galleryintent,"SELECT IMAGE"),1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1&&resultCode==RESULT_OK)
        {
            Uri uri=data.getData();
            final DatabaseReference user_message_push=mrootRef.child("messages").child(mauth.getCurrentUser().getUid()).child(mChatuser).push();
            final String push_id=user_message_push.getKey();

            final StorageReference filepath=mImagestorage.child("message_images").child(push_id+".jpg");
            filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {
                    if(task.isSuccessful())
                    {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                             downloaduri=uri.toString();

                             Map messageMap=new HashMap();
                             messageMap.put("from",mauth.getCurrentUser().getUid());
                             messageMap.put("seen",false);
                             messageMap.put("type","image");
                             messageMap.put("time",ServerValue.TIMESTAMP);
                             messageMap.put("message",downloaduri);

                             Map userMapmessage=new HashMap();
                             userMapmessage.put("messages/"+mauth.getCurrentUser().getUid()+"/"+mChatuser+"/"+push_id,messageMap);
                             userMapmessage.put("messages/"+mChatuser+"/"+mauth.getCurrentUser().getUid()+"/"+push_id,messageMap);

                             mrootRef.updateChildren(userMapmessage, new DatabaseReference.CompletionListener() {
                                 @Override
                                 public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference)
                                 {
                                  if(databaseError!=null)
                                  {
                                      Toast.makeText(ChatActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                  }
                                 }
                             });
                            }
                        });
                    }

                }
            });
        }
    }

    private void sendmessage()
    {
        String message=mchatmessage.getText().toString();
        mchatmessage.setText("");
      if(!TextUtils.isEmpty(message))
      {
          DatabaseReference user_message_push=FirebaseDatabase.getInstance().getReference().child("messages").child(mauth.getCurrentUser().getUid()).child(mChatuser).push();
          String push_id=user_message_push.getKey();

          Map messageMap=new HashMap();
          messageMap.put("message",message);
          messageMap.put("seen",false);
          messageMap.put("type","text");
          messageMap.put("time",ServerValue.TIMESTAMP);
          messageMap.put("from",mauth.getCurrentUser().getUid());


          Map messageUserMap=new HashMap();
          messageUserMap.put("messages/"+mauth.getCurrentUser().getUid()+"/"+mChatuser+"/"+push_id,messageMap);
          messageUserMap.put("messages/"+mChatuser+"/"+mauth.getCurrentUser().getUid()+"/"+push_id,messageMap);

          mrootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
              @Override
              public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference)
              {
               if(databaseError!=null)
               {
                   Toast.makeText(ChatActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
               }
              }
          });
      }
    }
    private void loadmessages()
    {
        mMessagesRef=FirebaseDatabase.getInstance().getReference().child("messages").child(mauth.getCurrentUser().getUid()).child(mChatuser);
        Query messageQuery=mMessagesRef.limitToLast(mcurrentpage*Total_Items_to_Load);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
             messages m=dataSnapshot.getValue(messages.class);
             mMessageList.add(m);
             itemPos++;
             if(itemPos==1)
             {
                 String messageKey=dataSnapshot.getKey();
                 mLastKey=messageKey;
                 mLastprevkey=messageKey;
             }
             messageAdapter.notifyDataSetChanged();
             messageRecyclerView.scrollToPosition(mMessageList.size()-1);
             swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void loadmoremessages()
    {
        mMessagesRef=FirebaseDatabase.getInstance().getReference().child("messages").child(mauth.getCurrentUser().getUid()).child(mChatuser);
        Query messageQuery=mMessagesRef.orderByKey().endAt(mLastKey).limitToLast(Total_Items_to_Load);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                messages m=dataSnapshot.getValue(messages.class);
                String messageKey=dataSnapshot.getKey();
                if(!mLastprevkey.equals(messageKey))
                {
                    mMessageList.add(itemPos++,m);
                }
                else
                {
                    mLastprevkey=mLastKey;
                }
                if((itemPos)==1)
                {
                    mLastKey=messageKey;

                }
                 messageAdapter.notifyDataSetChanged();
                    messageRecyclerView.scrollToPosition(mMessageList.size()-1);
                    swipeRefreshLayout.setRefreshing(false);


                }



            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
