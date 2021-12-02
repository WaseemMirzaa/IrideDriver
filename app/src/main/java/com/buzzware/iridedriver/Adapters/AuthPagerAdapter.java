package com.buzzware.iridedriver.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.buzzware.iridedriver.Fragments.SignInFragment;
import com.buzzware.iridedriver.Fragments.SignUpFragment;

public class AuthPagerAdapter extends FragmentPagerAdapter {
    int tabCount;
    public AuthPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        tabCount= behavior;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new SignInFragment();
                break;
            case 1:
                fragment = new SignUpFragment();
                break;
        }
        return fragment;
    }
    @Override
    public int getCount() {
        return tabCount;
    }
}
