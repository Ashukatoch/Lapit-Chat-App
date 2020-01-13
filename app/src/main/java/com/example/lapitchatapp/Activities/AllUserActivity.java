package com.example.lapitchatapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lapitchatapp.Model.users;
import com.example.lapitchatapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUserActivity extends AppCompatActivity
{
    private Toolbar mtoolbar;
    private RecyclerView user_recyclerView;
    private FirebaseAuth mauth;
    private DatabaseReference mdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        mauth=FirebaseAuth.getInstance();
        mdatabase=FirebaseDatabase.getInstance().getReference().child("users");

        mtoolbar=(Toolbar)findViewById(R.id.allusers_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user_recyclerView=(RecyclerView)findViewById(R.id.user_recyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        user_recyclerView.setLayoutManager(linearLayoutManager);
        user_recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<users> options =
                new FirebaseRecyclerOptions.Builder<users>()
                        .setQuery(mdatabase, users.class)
                        .build();

        FirebaseRecyclerAdapter<users,USerViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<users, USerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull USerViewHolder holder, int position, @NonNull users model) {
                holder.setUsername(model.getName());
                holder.setUserStatus(model.getStatus());
                holder.setUserImage(model.getImage());
                final String userid=getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent=new Intent(getApplicationContext(), ProfileActivity.class);
                        intent.putExtra("userid",userid);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public USerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_single_layout, parent, false);
                return new USerViewHolder(view);

            }
        };
        firebaseRecyclerAdapter.startListening();
        user_recyclerView.setAdapter(firebaseRecyclerAdapter);

    }




    public class USerViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public USerViewHolder(@NonNull View itemView) {
            super(itemView);
            mView= itemView;

        }

        public void setUsername(String au_name)
        {
            TextView name_for_user=(TextView) mView.findViewById(R.id.user_default_displayname);
            name_for_user.setText(au_name);
        }
        public void setUserStatus(String au_status)
        {
            TextView status_for_user=(TextView) mView.findViewById(R.id.user_default_status);
            status_for_user.setText(au_status);
        }
        public void setUserImage(String au_image)
        {
            CircleImageView image_for_user=mView.findViewById(R.id.user_single_image);
            Picasso.with(AllUserActivity.this).load(au_image).placeholder(R.drawable.avatar).into(image_for_user);
        }

    }
}
