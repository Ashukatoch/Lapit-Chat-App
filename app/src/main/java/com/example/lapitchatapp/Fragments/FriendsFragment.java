package com.example.lapitchatapp.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lapitchatapp.Activities.ChatActivity;
import com.example.lapitchatapp.Model.Friends;
import com.example.lapitchatapp.Activities.ProfileActivity;
import com.example.lapitchatapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsFragment extends Fragment
{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatabaseReference mfrienddatabase,muserdatabase;
    private FirebaseAuth mauth;
    private String mcurrent_user_id;
    private View main_view;
    private RecyclerView mrecyclerview;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FriendsFragment() {
        // Required empty public constructor
    }

    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
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
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(mfrienddatabase, Friends.class)
                        .build();

        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model)
            {
                holder.setDate(model.getDate());
                final String list_userid=getRef(position).getKey();
                muserdatabase.child(list_userid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        final String username=dataSnapshot.child("name").getValue().toString();
                        String image=dataSnapshot.child("image").getValue().toString();
                        String useronline=dataSnapshot.child("online").getValue().toString();
                        Log.d("OnlineStatus", useronline);
                        holder.setName(username);
                        holder.setImage(image);
                        holder.setOnline(useronline);
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                             CharSequence options[]=new CharSequence[]{"View Profile","Send Messsge"};
                                AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                                dialog.setTitle("Select options");
                                dialog.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i)
                                    {
                                       if(i==0)
                                       {
                                           Intent intent=new Intent(getContext(), ProfileActivity.class);
                                           intent.putExtra("userid",list_userid);
                                           startActivity(intent);
                                       }
                                       else
                                       {
                                       Intent intent=new Intent(getContext(), ChatActivity.class);
                                       intent.putExtra("userid",list_userid);
                                       intent.putExtra("username",username);
                                       startActivity(intent);
                                       }
                                    }
                                });
                                dialog.show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });;

                Log.d("firebase:", "onBindViewHolder: " + model.getDate());
            }
            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity())
                        .inflate(R.layout.user_single_layout, parent, false);
                Log.d("firebase:", "onCreateViewHolder: view holder called");
                return new FriendsViewHolder(view);

            }
        };
        firebaseRecyclerAdapter.startListening();
        mrecyclerview.setAdapter(firebaseRecyclerAdapter);

    }
    public class FriendsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

       public void setDate(String date)
       {
           TextView friendate=mView.findViewById(R.id.user_default_status);
           friendate.setText(date);
       }
       public void setName(String name)
       {
           TextView Name= mView.findViewById(R.id.user_default_displayname);
           Name.setText(name);
       }
       public void setImage(String image)
       {
           CircleImageView imageview=mView.findViewById(R.id.user_single_image);
           Picasso.with(getContext()).load(image).placeholder(R.drawable.avatar).into(imageview);
       }
       public void setOnline(String useronline)
       {
           ImageView onlineimage=mView.findViewById(R.id.online_image);
           if(useronline.equals("true"))
           {
               onlineimage.setVisibility(View.VISIBLE);
           }
           else
           {
               onlineimage.setVisibility(View.INVISIBLE);
           }


       }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        main_view=inflater.inflate(R.layout.fragment_friends, container, false);
        mauth=FirebaseAuth.getInstance();
        mcurrent_user_id=mauth.getCurrentUser().getUid();
        mfrienddatabase= FirebaseDatabase.getInstance().getReference().child("Friends").child(mcurrent_user_id);
        muserdatabase=FirebaseDatabase.getInstance().getReference().child("users");
        mrecyclerview=main_view.findViewById(R.id.friends_fragment_recyclerview);
        mrecyclerview.setHasFixedSize(true);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));



        return main_view;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
