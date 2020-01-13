package com.example.lapitchatapp.others;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.lapitchatapp.Fragments.ChatFragment;
import com.example.lapitchatapp.Fragments.FriendsFragment;
import com.example.lapitchatapp.Fragments.Request;

public class SectionsPagerAdapter extends FragmentPagerAdapter
{
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        switch(position)
        {
            case 0:
                Request request=new Request();
            return request;

            case 1:
                ChatFragment chatFragment=new ChatFragment();
            return chatFragment;

            case 2:
                FriendsFragment friendsFragment=new FriendsFragment();
            return friendsFragment;

            default:return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0: return "Requests";
            case 1: return "Chats";
            case 2: return "Friends";
            default: return null;
        }
    }
}
