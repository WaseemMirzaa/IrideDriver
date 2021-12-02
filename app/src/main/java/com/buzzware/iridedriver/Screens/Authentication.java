package com.buzzware.iridedriver.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.buzzware.iridedriver.Adapters.AuthPagerAdapter;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.databinding.ActivityAuthenticationBinding;
import com.google.android.material.tabs.TabLayout;

public class Authentication extends AppCompatActivity {

    ActivityAuthenticationBinding mBinding;

    AuthPagerAdapter authPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_authentication);

        try {
            Init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Init() {
        InitViewPager();
    }

    private void InitViewPager() {
        authPagerAdapter = new AuthPagerAdapter(getSupportFragmentManager(), mBinding.tabLay.getTabCount());
        mBinding.viewPager.setAdapter(authPagerAdapter);
        mBinding.tabLay.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                mBinding.viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0 || tab.getPosition() == 1)

                    authPagerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mBinding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mBinding.tabLay));

    }
}