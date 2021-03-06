package edu.uw.team7project.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import edu.uw.team7project.ui.messages.MessageListFragment;
import edu.uw.team7project.ui.weather.WeatherFragment;

/**
 * A fragment state adapter for contacts
 *
 * @author Trevor Nichols
 */
public class ContactsCollectionAdapter extends FragmentStateAdapter {

    private String mLable;

    public ContactsCollectionAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Fragment fragment = null;
        //Fragment fragment = new ContactListFragment();
        if(position  == 0){
            fragment = new ContactListFragment();
            mLable = "Contacts";
        }else if(position == 1){
            fragment = new ContactRequestListFragment();
            mLable = "Requests";
        }else{
            fragment = new FindContactFragment();
            mLable = "Find";
        }
        return fragment;
    }

    /**
     * Gets the lable.
     * @return a string representing the label of the tab.
     */
    public String getLable(int position){
        String theLabel;
        if(position  == 0){
            theLabel = "Contacts";
        }else if(position == 1){
            theLabel = "Requests";
        }else{
            theLabel = "Finder";
        }
        return theLabel;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

