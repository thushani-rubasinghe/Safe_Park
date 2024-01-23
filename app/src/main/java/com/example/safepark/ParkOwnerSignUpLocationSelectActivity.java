package com.example.safepark;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.safepark.obj.BankDetails;
import com.example.safepark.obj.ParkDetails;
import com.example.safepark.obj.ParkOwner;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ParkOwnerSignUpLocationSelectActivity extends FragmentActivity implements OnMapReadyCallback {

    boolean permissionGranted;
    GoogleMap googleMap;
    private int GPS_REQEST_CODE = 9001;
    private Geocoder geocoder;
    private SwipeRefreshLayout refreshLayout;

    private TextView markerLocationTextView;
    private Button saveLocationBtn, clearMarkerBtn;
    private List<Marker> markers = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_owner_sign_up_location_select);

        mAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance("https://safepark-c3e3f-default-rtdb.asia-southeast1.firebasedatabase.app");
        reference = rootNode.getReference("users").child("ParkOwner");

        saveLocationBtn = findViewById(R.id.saveLocationMarkerBtn);
        clearMarkerBtn = findViewById(R.id.clearMarkerBtn);
        markerLocationTextView = findViewById(R.id.markerLocationTextView);
        Toast.makeText(getApplicationContext(), "Please Refresh Once", Toast.LENGTH_SHORT).show();

        refreshLayout = findViewById(R.id.refreshLayoutLocationSelect);
        refreshLayout.setOnRefreshListener(() -> {
            checkPermission();
            initMap();
            refreshLayout.setRefreshing(false);
        });

        saveLocationBtn.setOnClickListener(v -> {
            createParkOwner();
        });

        clearMarkerBtn.setOnClickListener(v -> {
            markers.clear();
            this.googleMap.clear();
            markerLocationTextView.setText("Latitude : 0 Longitude: 0");
        });
    }

    private void createParkOwner() {
        if(markers.size() == 0) {
            throwMessage("Please Select Park Location");
        } else {
            Bundle bundle = getIntent().getBundleExtra("personalDetails");

            String name = bundle.getString("name");
            String email = bundle.getString("email");
            String phoneNumber = bundle.getString("phoneNumber");
            String password = bundle.getString("password");

            String bankName = getIntent().getExtras().getString("bankName");
            String bankCode = getIntent().getExtras().getString("bankCode");
            String branchName = getIntent().getExtras().getString("branchName");
            String branchCode = getIntent().getExtras().getString("branchCode");
            String fullName = getIntent().getExtras().getString("fullName");
            String accountNumber = getIntent().getExtras().getString("accountNumber");
            String nicNumber = getIntent().getExtras().getString("nicNumber");

            String parkName = getIntent().getExtras().getString("parkName");
            String parkAddress =  getIntent().getExtras().getString("parkAddress");
            double latitude = markers.get(0).getPosition().latitude;
            double longitude = markers.get(0).getPosition().longitude;
            int cs =  getIntent().getExtras().getInt("cs");
            int cph =  getIntent().getExtras().getInt("cph");
            int ls =  getIntent().getExtras().getInt("ls");
            int lph =  getIntent().getExtras().getInt("lph");
            int bs =  getIntent().getExtras().getInt("bs");
            int bph =  getIntent().getExtras().getInt("bph");
            int vs =  getIntent().getExtras().getInt("vs");
            int vph =  getIntent().getExtras().getInt("vph");

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(ParkOwnerSignUpLocationSelectActivity.this, task -> {
                        if (task.isSuccessful()) {
                            throwMessage("Successfully Registered!");
                            startActivity(new Intent(ParkOwnerSignUpLocationSelectActivity.this, VerificationActivity.class));
                        } else {
                            throwMessage("Registration Failed: " + task.getException());
                        }
                    });

            BankDetails bankDetails = new BankDetails(bankName, bankCode, branchName, branchCode, fullName, accountNumber, nicNumber);
            ParkDetails parkDetails = new ParkDetails(parkName, parkAddress, latitude, longitude, cs, cph, ls, lph, bs, bph, vs, vph);
            ParkOwner parkOwner = new ParkOwner(name, email, phoneNumber, password, bankDetails, parkDetails);
            reference.child(parkOwner.getUniqueID()).setValue(parkOwner);
        }
    }

    private void initMap() {
        if (permissionGranted) {
            if (isGPSEnabled()) {
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.parkOwnerLocationSelectMap);
                supportMapFragment.getMapAsync(this);
            }
        }
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
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

    @SuppressLint({"MissingPermission", "SetTextI18n"})
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        this.googleMap.setOnMapClickListener(latLng -> {
            try {
                if (geocoder == null)
                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (address.size() > 0 && markers.size() < 1) {
                    markers.add(googleMap.addMarker(new MarkerOptions().position(latLng).title("Name:" + address.get(0).getCountryName()
                            + "\n Address:" + address.get(0).getAddressLine(0))));
                }
            } catch (IOException ex) {
                Toast.makeText(getApplicationContext(), "Error:" + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        this.googleMap.setOnMarkerClickListener(marker -> {
            markerLocationTextView.setText(marker.getTitle() + "\n Latitude :" + marker.getPosition().latitude + " Longitude :" + marker.getPosition().longitude);
            return false;
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQEST_CODE) {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (providerEnabled) {
                Toast.makeText(getApplicationContext(), "GPS is Enable", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "GPS is Disabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void throwMessage(String error) {
        Toast.makeText(ParkOwnerSignUpLocationSelectActivity.this, error, Toast.LENGTH_SHORT).show();
    }
}