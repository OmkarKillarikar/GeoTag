package com.poc.okmac.geotag.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.poc.okmac.geotag.BuildConfig;
import com.poc.okmac.geotag.Database.GeoTagDatabase;
import com.poc.okmac.geotag.R;
import com.poc.okmac.geotag.Utils.AppFileManager;

import java.io.File;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private static final int CAMERA_REQUEST = 101;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int VIBRATOR_REQUEST_CODE = 101;

    private Uri imageUri;
    private LatLng latLng;
    private String address = "temp";

    private GeoTagDatabase geoTagDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geoTagDatabase = GeoTagDatabase.getDatabase(getContext());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment supportMapFragment = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map));
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d("###LatLong: ", latLng.latitude + ", " + latLng.longitude);
        this.latLng = latLng;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.VIBRATE}, VIBRATOR_REQUEST_CODE);
        } else {
            vibrate();
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity()
                    , new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    , CAMERA_PERMISSION_CODE);
        } else {
            startCameraActivity();
        }
    }

    private void startCameraActivity() {
        try {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            AppFileManager fileManager = new AppFileManager(getContext());
            imageUri = fileManager.getFileUriToWrite(BuildConfig.APPLICATION_ID);
            if (imageUri != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissionAccepted;
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE:
                isPermissionAccepted = true;
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        isPermissionAccepted = false;
                        break;
                    }
                }
                if (isPermissionAccepted) {
                    startCameraActivity();
                }
                break;
            case VIBRATOR_REQUEST_CODE:

                isPermissionAccepted = true;
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        isPermissionAccepted = false;
                        break;
                    }
                }
                if (isPermissionAccepted) {
                    vibrate();
                }
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        File file = new File(imageUri.getPath());
                        final GeoTag geoTag = new GeoTag();
                        geoTag.setImageName(file.getName());
                        geoTag.setLatitude(latLng.latitude);
                        geoTag.setLongitude(latLng.longitude);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                geoTagDatabase.geoTagDao().insert(geoTag);
                            }
                        }).start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(70);
            }
        }
    }

}
