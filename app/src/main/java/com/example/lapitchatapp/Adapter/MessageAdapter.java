package com.example.lapitchatapp.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lapitchatapp.Model.messages;
import com.example.lapitchatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private FirebaseAuth mauth=FirebaseAuth.getInstance();
    private List<messages> mMessageList;
    MessageAdapter()
    {}


    public MessageAdapter(List<messages> mMessageList)
    {
        this.mMessageList = mMessageList;
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_single_layout,viewGroup,false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i)
    {
        if(mauth.getCurrentUser()!=null) {
            String currentuser = mauth.getCurrentUser().getUid();
            messages c = mMessageList.get(i);
            String fromuser = c.getFrom();
            if (fromuser.equals(currentuser))
            {
                messageViewHolder.messagetextview.setVisibility(View.INVISIBLE);
                messageViewHolder.motherimage.setVisibility(View.INVISIBLE);

                if(c.getType().equals("text"))
                {
                    messageViewHolder.userMessage.setVisibility(View.VISIBLE);
                    messageViewHolder.userMessage.setText(c.getMessage());
                    messageViewHolder.muserImage.setVisibility(View.INVISIBLE);
                    messageViewHolder.motherimage.setVisibility(View.INVISIBLE);
                }
                else
                {
                    messageViewHolder.userMessage.setVisibility(View.INVISIBLE);
                    messageViewHolder.muserImage.setVisibility(View.VISIBLE);
                    Picasso.with(messageViewHolder.muserImage.getContext()).load(c.getMessage()).placeholder(R.drawable.placeholderimage).into(messageViewHolder.muserImage);

                }

                messageViewHolder.mimage.setVisibility(View.INVISIBLE);

            }
            else
            {
                messageViewHolder.userMessage.setVisibility(View.INVISIBLE);
                messageViewHolder.muserImage.setVisibility(View.INVISIBLE);

                if(c.getType().equals("text"))
                {

                    messageViewHolder.messagetextview.setVisibility(View.VISIBLE);
                    messageViewHolder.motherimage.setVisibility(View.INVISIBLE);
                    messageViewHolder.messagetextview.setText(c.getMessage());

                }
                else
                {
                    messageViewHolder.motherimage.setVisibility(View.VISIBLE);
                    messageViewHolder.messagetextview.setVisibility(View.INVISIBLE);
                    Picasso.with(messageViewHolder.motherimage.getContext()).load(c.getMessage()).placeholder(R.drawable.placeholderimage).into(messageViewHolder.motherimage);
                }

                messageViewHolder.mimage.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public CircleImageView mimage;
        public TextView messagetextview,userMessage;
        public ImageView muserImage,motherimage;

        View mview;
        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mimage=itemView.findViewById(R.id.message_user_image);
            messagetextview=itemView.findViewById(R.id.message_textview);
            userMessage=itemView.findViewById(R.id.user_message);
            muserImage=itemView.findViewById(R.id.imagetosendbyuser);
            motherimage= itemView.findViewById(R.id.imagetosendbyother);

        }

    }
}
