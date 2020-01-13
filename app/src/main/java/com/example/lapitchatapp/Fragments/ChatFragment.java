package com.example.lapitchatapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lapitchatapp.Activities.ChatActivity;
import com.example.lapitchatapp.Model.Conv;
import com.example.lapitchatapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import  com.google.firebase.database.Query;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment
{
    private FirebaseAuth mauth;
    private DatabaseReference mMessagereference;
    private DatabaseReference mrootRef,mConvRef,mUSerRef;

    private RecyclerView mRecyclerView;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ChatFragment() {
    }

    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View v=inflater.inflate(R.layout.fragment_chat, container, false);
        mauth=FirebaseAuth.getInstance();
        mrootRef= FirebaseDatabase.getInstance().getReference();
        mConvRef=mrootRef.child("Chat").child(mauth.getCurrentUser().getUid());
        mConvRef.keepSynced(true);
        mUSerRef=mrootRef.child("users");
        mMessagereference=mrootRef.child("messages").child(mauth.getCurrentUser().getUid());


        mRecyclerView=v.findViewById(R.id.chatfragment_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);



        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query conversationQuery=mConvRef.orderByChild("timestamp");
        FirebaseRecyclerOptions<Conv> options=new FirebaseRecyclerOptions.Builder<Conv>().setQuery(conversationQuery,Conv.class).build();

        FirebaseRecyclerAdapter<Conv,ConvViewholder> adapter=new FirebaseRecyclerAdapter<Conv, ConvViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ConvViewholder holder, int position, @NonNull final Conv model)
            {
                Log.d("Name", String.valueOf(model.getTimestamp()));
             final String list_user_id=getRef(position).getKey();
             Query messageQuery=mMessagereference.child(list_user_id).limitToLast(1);
             messageQuery.addChildEventListener(new ChildEventListener() {
                 @Override
                 public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                 {
                  String data=dataSnapshot.child("message").getValue().toString();
                  holder.setMessage(data,model.getSeen());
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
             mUSerRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                 {
                     final String username=dataSnapshot.child("name").getValue().toString();
                     String image=dataSnapshot.child("image").getValue().toString();
                     holder.setName(username);
                     holder.setImage(image);

                     holder.v.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             Intent intent=new Intent(getContext(), ChatActivity.class);
                             intent.putExtra("userid",list_user_id);
                             intent.putExtra("username",username);
                             startActivity(intent);
;
                         }
                     });

                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });
            }

            @NonNull
            @Override
            public ConvViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View v=LayoutInflater.from(getActivity()).inflate(R.layout.user_single_layout,viewGroup,false);
                return new ConvViewholder(v);
            }
        };
        adapter.startListening();
        mRecyclerView.setAdapter(adapter);
    }
    public class ConvViewholder extends RecyclerView.ViewHolder
    {
        View v;
        public ConvViewholder(@NonNull View itemView) {
            super(itemView);
            v=itemView;
        }
        public void setName(String name)
        {
            TextView Name=v.findViewById(R.id.user_default_displayname);
            Name.setText(name);

        }
        public void setImage(String image)
        {
            CircleImageView imageview=v.findViewById(R.id.user_single_image);
            Picasso.with(v.getContext()).load(image).placeholder(R.drawable.avatar).into(imageview);
        }
        public void setMessage(String message,boolean isseen)
        {
            TextView user_message_view=v.findViewById(R.id.user_default_status);
            String [] test=message.split("://");
            if(test[0].equals("https"))
            {
                user_message_view.setText("New Image");
            }
            else
            {
                user_message_view.setText(message);
            }
            if(!isseen)
            {
                user_message_view.setTypeface(user_message_view.getTypeface(), Typeface.BOLD);
            }
            else
            {
                user_message_view.setTypeface(user_message_view.getTypeface(),Typeface.NORMAL);
            }
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
