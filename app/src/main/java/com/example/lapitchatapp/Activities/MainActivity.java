package com.example.lapitchatapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.lapitchatapp.Fragments.ChatFragment;
import com.example.lapitchatapp.Fragments.FriendsFragment;
import com.example.lapitchatapp.Fragments.Request;
import com.example.lapitchatapp.R;
import com.example.lapitchatapp.others.SectionsPagerAdapter;
import com.example.lapitchatapp.UserAuthentication.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity implements Request.OnFragmentInteractionListener, ChatFragment.OnFragmentInteractionListener, FriendsFragment.OnFragmentInteractionListener
{
    private FirebaseAuth mauth;
    private FirebaseUser muser;
    private Toolbar mtoolbar;
    private DatabaseReference mUserref;
    private ViewPager mviewpager;
    private TabLayout mtablayout;
    String current_user;
    private SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mauth=FirebaseAuth.getInstance();
        mtoolbar=findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Lapit Chat");
        mviewpager=findViewById(R.id.main_viewpager);
        mtablayout=findViewById(R.id.main_tablayout);
        mUserref= FirebaseDatabase.getInstance().getReference().child("users");
        sectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());
        mviewpager.setAdapter(sectionsPagerAdapter);
        mtablayout.setupWithViewPager(mviewpager);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        muser= mauth.getCurrentUser();
        //Log.d("Tag",mauth.getUid());
        if(muser==null)
        {
            startActivity(new Intent(getApplicationContext(), StartActivity.class));
            finish();
        }
        else
        {
            mUserref.child(mauth.getCurrentUser().getUid()).child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mauth.getCurrentUser()!=null)
        {
            mUserref.child(mauth.getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.logoutmainmenu)
        {
            mUserref.child(mauth.getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
            mauth.signOut();
            startActivity(new Intent(getApplicationContext(),StartActivity.class));
        }
        if(item.getItemId()==R.id.accountsettingmainmenu)
        {
            startActivity(new Intent(getApplicationContext(), SettingAccount.class));
        }
        if(item.getItemId()==R.id.allusermainmenu)
        {
            startActivity(new Intent(getApplicationContext(), AllUserActivity.class));
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
