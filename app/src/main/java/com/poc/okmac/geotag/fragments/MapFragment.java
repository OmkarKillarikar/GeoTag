package com.poc.okmac.geotag.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.poc.okmac.geotag.BuildConfig;
import com.poc.okmac.geotag.Database.GeoTagDatabase;
import com.poc.okmac.geotag.Database.GetTagsTask;
import com.poc.okmac.geotag.R;
import com.poc.okmac.geotag.Utils.AppFileManager;
import com.poc.okmac.geotag.activities.MainActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private static final int CAMERA_REQUEST = 101;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int VIBRATOR_REQUEST_CODE = 102;
    private static final int READ_REQUEST_CODE = 103;

    private Uri imageUri;
    private LatLng latLng;
    private String address = "temp";

    private GeoTagDatabase geoTagDatabase;
    private ArrayList<GeoTag> geoTags;
    private GoogleMap googleMap;
    private MainActivity mainActivity;
    private AppFileManager appFileManager;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geoTagDatabase = GeoTagDatabase.getDatabase(getContext());
        mainActivity = (MainActivity) getActivity();
        appFileManager = new AppFileManager(getContext());
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
        this.googleMap = googleMap;
        googleMap.setOnMapLongClickListener(this);
        if (checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQUEST_CODE);
        } else {
            getTags();
        }
        showSnackBar();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        this.latLng = latLng;
        if (checkSelfPermission(getContext(), Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.VIBRATE}, VIBRATOR_REQUEST_CODE);
        } else {
            vibrate();
        }
        if (checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}
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
                        geoTag.setAddress(getAddress(latLng));
                        LatLng latLng = new LatLng(geoTag.getLatitude(), geoTag.getLongitude());

                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        File image = appFileManager.getExistingFile(geoTag.getImageName());
                        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                        bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/26,bitmap.getHeight()/26,true);
                        googleMap.addMarker(new MarkerOptions().position(latLng)).setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                geoTagDatabase.geoTagDao().insert(geoTag);
                                mainActivity.notifyDbUpdated();

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

    private void getTags() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ArrayList<GeoTag>> future = executor.submit(new GetTagsTask(geoTagDatabase));
        try {
            geoTags = future.get();
            ArrayList<LatLng> latLngs = new ArrayList<>();
            if (geoTags != null) {
                for (GeoTag geoTag : geoTags) {
                    LatLng latLng = new LatLng(geoTag.getLatitude(), geoTag.getLongitude());
                    latLngs.add(latLng);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    File image = appFileManager.getExistingFile(geoTag.getImageName());
                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                    bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/26,bitmap.getHeight()/26,true);
                    googleMap.addMarker(new MarkerOptions().position(latLng)).setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                }

                //FIXME: latlng bounds animation not working
                /*LatLngBounds latLngBounds;
                if (latLngs.size() > 1) {
                    latLngBounds = new LatLngBounds(latLngs.get(1), latLngs.get(0));
                    for (int i = 2; i < latLngs.size(); i++) {
                        latLngBounds.including(latLngs.get(i));
                    }
                    googleMap.setLatLngBoundsForCameraTarget(latLngBounds);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, latLngs.size()));

                }*/
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    public String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addressList;
        String addressString = "";
        StringBuilder deviceAddress = new StringBuilder();
        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                if (address != null) {
                    deviceAddress = new StringBuilder();
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        deviceAddress.append(address.getAddressLine(i)).append(",");
                    }
                }
            }

            addressString = deviceAddress.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressString;
    }

    public void moveCameraToTag(GeoTag geoTag) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(geoTag.getLatitude(), geoTag.getLongitude()), 8));

    }

    private void showSnackBar(){
        Snackbar.make(getActivity().findViewById(android.R.id.content),
                "Tap & hold anywhere to add image !", Snackbar.LENGTH_LONG).show();
    }

}
