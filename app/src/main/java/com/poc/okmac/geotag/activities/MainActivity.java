package com.poc.okmac.geotag.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.poc.okmac.geotag.R;
import com.poc.okmac.geotag.adapters.CustomPagerAdapter;
import com.poc.okmac.geotag.fragments.MapFragment;
import com.poc.okmac.geotag.fragments.TagListFragment;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
    }

    private void initViewPager() {
        ViewPager vpFragments = findViewById(R.id.vpFragments);
        CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());

        MapFragment mapFragment = new MapFragment();
        TagListFragment tagListFragment = new TagListFragment();

        customPagerAdapter.addFragment(mapFragment, getString(R.string.map_fragment_title));
        customPagerAdapter.addFragment(tagListFragment, getString(R.string.list_fragment_title));

        TabLayout tlFragments = findViewById(R.id.tl_fragments);
        tlFragments.setupWithViewPager(vpFragments);

        vpFragments.setAdapter(customPagerAdapter);
    }

    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.VIBRATE}
                    , PERMISSION_REQUEST_CODE);
        } else {
            initViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:

                break;
        }
    }
}
