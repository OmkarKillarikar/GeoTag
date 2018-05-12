package com.poc.okmac.geotag.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.poc.okmac.geotag.Database.GeoTagDatabase;
import com.poc.okmac.geotag.Database.GetTagsTask;
import com.poc.okmac.geotag.R;
import com.poc.okmac.geotag.adapters.GeoTagsAdapter;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TagListFragment extends Fragment {

    private static final int READ_REQUEST_CODE = 100;
    private GeoTagDatabase geoTagDatabase;
    private GeoTagsAdapter geoTagsAdapter;

    private TextView tvNoTags;
    private RecyclerView rvTags;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geoTagDatabase = GeoTagDatabase.getDatabase(getContext());
        geoTagsAdapter = new GeoTagsAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_tag_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvNoTags = view.findViewById(R.id.tv_no_tags);
        rvTags = view.findViewById(R.id.rv_geo_tags);
        rvTags.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTags.setAdapter(geoTagsAdapter);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ||ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_REQUEST_CODE);
        } else {
            getTags();
        }
        invalidateUi();
    }

    private void getTags() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ArrayList<GeoTag>> future = executor.submit(new GetTagsTask(geoTagDatabase));
        try {
            geoTagsAdapter.geoTags = future.get();
            geoTagsAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }


    private void invalidateUi() {
        if (geoTagsAdapter.geoTags == null || geoTagsAdapter.geoTags.size() == 0) {
            tvNoTags.setVisibility(View.VISIBLE);
            rvTags.setVisibility(View.GONE);
        } else {
            tvNoTags.setVisibility(View.GONE);
            rvTags.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissionAccepted;
        switch (requestCode) {
            case READ_REQUEST_CODE:
                isPermissionAccepted = true;
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        isPermissionAccepted = false;
                        break;
                    }
                }
                if (isPermissionAccepted) {
                    getTags();
                }
                break;

        }
    }
}