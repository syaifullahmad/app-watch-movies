package com.lazday.youtubeplayer.fragment;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.lazday.youtubeplayer.adapter.ViewPagerAdapter;
import com.lazday.youtubeplayer.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView( binding.getRoot() );

        setTab();
    }

    private void setTab() {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new PopularFragment(), "TOP");
        adapter.addFrag(new NowPlayingFragment(), "NEW MOVIES");

        binding.viewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager( binding.viewPager );
    }
}
