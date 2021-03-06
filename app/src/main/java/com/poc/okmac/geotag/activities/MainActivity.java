package com.poc.okmac.geotag.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.poc.okmac.geotag.R;
import com.poc.okmac.geotag.adapters.CustomPagerAdapter;
import com.poc.okmac.geotag.fragments.GeoTag;
import com.poc.okmac.geotag.fragments.MapFragment;
import com.poc.okmac.geotag.fragments.TagListFragment;

public class MainActivity extends AppCompatActivity {
    private MapFragment mapFragment;
    private TagListFragment tagListFragment;
    private ViewPager vpFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewPager();
    }

    private void initViewPager() {

        vpFragments = findViewById(R.id.vpFragments);
        CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());

        mapFragment = new MapFragment();
        tagListFragment = new TagListFragment();

        customPagerAdapter.addFragment(mapFragment, getString(R.string.map_fragment_title));
        customPagerAdapter.addFragment(tagListFragment, getString(R.string.list_fragment_title));

        TabLayout tlFragments = findViewById(R.id.tl_fragments);
        tlFragments.setupWithViewPager(vpFragments);

        vpFragments.setAdapter(customPagerAdapter);
    }

    public void notifyMapFragment(GeoTag geoTag) {
        vpFragments.setCurrentItem(0);
        mapFragment.moveCameraToTag(geoTag);
    }

    public void notifyDbUpdated() {
        tagListFragment.refreshData();
    }
}
