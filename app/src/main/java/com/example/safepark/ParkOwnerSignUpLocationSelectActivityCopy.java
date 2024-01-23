package com.example.safepark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ParkOwnerSignUpLocationSelectActivityCopy extends FragmentActivity implements OnMapReadyCallback {

    boolean permissionGranted;
    private int GPS_REQEST_CODE = 9001;
    private GoogleMap googleMap;
    private Geocoder geocoder;
    private TextView markerLocationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_owner_sign_up_location_select);

        markerLocationTextView = findViewById(R.id.markerLocationTextView);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermission();
        initMap();
    }

    private void initMap() {
        if (permissionGranted) {
            if (isGPSEnabled()) {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.parkOwnerLocationSelectMap);
                mapFragment.getMapAsync(this);
            }
        }
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnabled) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext())
                    .setTitle("GPS Permission")
                    .setMessage("GPS IS required for this app to work. Please enable GPS")
                    .setPositiveButton("YES", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();
        }
        return false;
    }

    private void checkPermission() {
        Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                permissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @SuppressLint({"SetTextI18n", "VisibleForTests"})
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        geocoder = new Geocoder(ParkOwnerSignUpLocationSelectActivityCopy.this, Locale.getDefault());
        this.googleMap.setOnMapClickListener(latLng -> {
            try {
                if (geocoder == null)
                    geocoder = new Geocoder(ParkOwnerSignUpLocationSelectActivityCopy.this, Locale.getDefault());
                List<Address> address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (address.size() > 0) {
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Name:" + address.get(0).getCountryName()
                            + ". Address:" + address.get(0).getAddressLine(0)));
                }
            } catch (IOException ex) {
                if (ex != null)
                    Toast.makeText(ParkOwnerSignUpLocationSelectActivityCopy.this, "Error:" + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });

        this.googleMap.setOnMarkerClickListener(marker -> {
            markerLocationTextView.setText(marker.getTitle() + " Lat:" + marker.getPosition().latitude + " Long:" + marker.getPosition().longitude);
            return false;
        });
    }
}