package com.example.lapitchatapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lapitchatapp.R;
import com.example.lapitchatapp.UserAuthentication.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class SettingAccount extends AppCompatActivity
{
    private static final int MAX_LENGTH =5 ;
    private FirebaseAuth mauth;
    private Bitmap thumb_bitmap;
    private DatabaseReference databaseReference,mUserref;
    private FirebaseUser muser;
    private TextView mstatus,mname;
    private Button changestatus,mimagebtn;
    private StorageReference storageReference;
    private CircleImageView mcimage;
    private String download_url;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_account);
        mauth=FirebaseAuth.getInstance();
        muser=mauth.getCurrentUser();
        String current_uid=muser.getUid();
        mstatus=findViewById(R.id.profile_status);
        mcimage=findViewById(R.id.profile_image);
        mUserref=FirebaseDatabase.getInstance().getReference().child("users").child(mauth.getCurrentUser().getUid());
        mname=findViewById(R.id.displayname);
        mimagebtn=findViewById(R.id.changeimage);
        changestatus=findViewById(R.id.ChangeStatus);
        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);
        databaseReference.keepSynced(true);
        changestatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), StatusActivity.class));
            }
        });
        mimagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),1);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            Uri imageuri = data.getData();
            CropImage.activity(imageuri).setAspectRatio(1, 1).start(this);
        }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK)
                {
                    Uri resultUri = result.getUri();
                    String current_uid = muser.getUid();
                    File thumb_filepath=new File(resultUri.getPath());
                    try {
                       thumb_bitmap=new Compressor(this).setMaxHeight(200).setMaxWidth(200).setQuality(75).compressToBitmap(thumb_filepath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baos=new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                    final byte[] i_data=baos.toByteArray();
                    final StorageReference filepath = storageReference.child("profile_images").child(current_uid + ".jpg");
                    final StorageReference thumb_path=storageReference.child("profile_images").child("thumbs").child(current_uid+".jpg");
                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful())
                            {
                             filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                 @Override
                                 public void onSuccess(Uri uri)
                                 {
                                     download_url=uri.toString();
                                     UploadTask uploadTask=thumb_path.putBytes(i_data);
                                     uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                         @Override
                                         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                         {
                                          String upload_thumb=taskSnapshot.toString();
                                          databaseReference.child("thumbimage").setValue(upload_thumb).addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task)
                                              {
                                               if(task.isSuccessful())
                                                   Toast.makeText(getApplicationContext(),"Thumb upload successfull!!",Toast.LENGTH_LONG).show();
                                               else
                                                   Toast.makeText(getApplicationContext(),"upload failed failed!!",Toast.LENGTH_LONG).show();

                                              }
                                          });
                                         }
                                     });
                                 //Toast.makeText(getApplicationContext(), uri.toString(), Toast.LENGTH_LONG).show();
                                         databaseReference.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             if(task.isSuccessful())
                                             {
                                              Toast.makeText(getApplicationContext(),"Post Successfull!!",Toast.LENGTH_LONG).show();
                                             }
                                             else
                                              Toast.makeText(getApplicationContext(),"post failed!!",Toast.LENGTH_LONG).show();

                                         }
                                  });
                                 }
                             });
                            }
                        }
                    });
                }


                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                {
                    Exception error = result.getError();
                }
            }
        }


    @Override
    protected void onStart() {
        super.onStart();

        if(mauth.getCurrentUser()==null)
        {
            startActivity(new Intent(getApplicationContext(), StartActivity.class));
            finish();
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String s_name=dataSnapshot.child("name").getValue().toString();
                String s_status=dataSnapshot.child("status").getValue().toString();
                String s_image=dataSnapshot.child("image").getValue().toString();
                String s_Thumb_image=dataSnapshot.child("thumbimage").getValue().toString();

                mname.setText(s_name);
                mstatus.setText(s_status);
                if(!s_image.matches("default"))
                {
                    Picasso.with(getApplicationContext()).load(s_image).placeholder(R.drawable.avatar).into(mcimage);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

