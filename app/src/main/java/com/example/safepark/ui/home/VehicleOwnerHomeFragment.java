package com.example.safepark.ui.home;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.safepark.R;
import com.example.safepark.databinding.FragmentVehicleownerHomescreenBinding;
import com.example.safepark.obj.DirectionsJSONParser;
import com.example.safepark.obj.ParkOwner;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class VehicleOwnerHomeFragment extends Fragment implements OnMapReadyCallback {

    boolean permissionGranted;
    GoogleMap googleMap;
    //    FloatingActionButton fbtn;
    private FusedLocationProviderClient mFusedLocationClient;
    private int GPS_REQEST_CODE = 9001;
    private SwipeRefreshLayout refreshLayout;
    private TextInputLayout searchLocField;

    private List<ParkOwner> parks = new ArrayList<>();
    private Spinner vehicleTypeFilterSpinner;
    private TextView availableParkingSpotsTextView;
    private TextView distanceDurationTextView;

    private Location userLocation;
    private int radiusInMeters = 5000;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        VehicleOwnerHomeViewModel vehicleOwnerHomeViewModel = new ViewModelProvider(this).get(VehicleOwnerHomeViewModel.class);
        com.example.safepark.databinding.FragmentVehicleownerHomescreenBinding binding = FragmentVehicleownerHomescreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint({"SetTextI18n", "VisibleForTests"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getContext(), "Please Refresh Once", Toast.LENGTH_SHORT).show();
        addParks();

        distanceDurationTextView = getView().findViewById(R.id.distanceDurationTextView);
        availableParkingSpotsTextView = getView().findViewById(R.id.availableParkingSpotsTextView);

        vehicleTypeFilterSpinner = getView().findViewById(R.id.vehicleTypeFilterSpinner);
        List<String> vehicleTypes = new ArrayList<>();
        vehicleTypes.add("Car");
        vehicleTypes.add("Lorry");
        vehicleTypes.add("Bike");
        vehicleTypes.add("Van");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, vehicleTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeFilterSpinner.setAdapter(dataAdapter);

        Button filterMapForVehicleTypeBtn = getView().findViewById(R.id.filterMapForVehicleTypeBtn);
        filterMapForVehicleTypeBtn.setOnClickListener(v -> {
            String vehicleType = vehicleTypeFilterSpinner.getSelectedItem().toString();
            if (permissionGranted) {
                switch (vehicleType) {
                    case "Car":
                        filterMap(1);
                        break;
                    case "Lorry":
                        filterMap(2);
                        break;
                    case "Bike":
                        filterMap(3);
                        break;
                    case "Van":
                        filterMap(4);
                        break;
                    default:
                        filterMap(0);
                        break;
                }
            } else {
                Toast.makeText(getContext(), "Please Refresh", Toast.LENGTH_SHORT).show();
            }
        });

        searchLocField = getView().findViewById(R.id.locationTextField);
        Button searchLocBtn = getView().findViewById(R.id.searchLocationBtn);

        refreshLayout = getView().findViewById(R.id.refreshLayoutVehicleOwner);
        refreshLayout.setOnRefreshListener(() -> {
            checkPermission();
            initMap();
            refreshLayout.setRefreshing(false);
        });

        mFusedLocationClient = new FusedLocationProviderClient(requireContext());
//        fbtn.setOnClickListener(v -> getCurrentLoc());
//        fbtn = getView().findViewById(R.id.fbtn);
//        fbtn.setVisibility(View.INVISIBLE);

        searchLocBtn.setOnClickListener(this::geoLocate);
        //show error dialog if GooglePlayServices not available
    }

    @SuppressLint({"SetTextI18n", "MissingPermission"})
    private void filterMap(int vehicleType) {
        googleMap.clear();

        mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userLocation = task.getResult();

                double userLat = userLocation.getLatitude();
                double userLon = userLocation.getLongitude();
                int spotsFound = 0;

                if (vehicleType != 0) {
                    for (ParkOwner parkOwner : parks) {
                        double parkLat = parkOwner.getParkDetails().getLat();
                        double parkLon = parkOwner.getParkDetails().getLon();
                        if (calculateDistanceDifference(parkLat, parkLon, userLat, userLon) < 10000) {
                            if (vehicleType == 1 && parkOwner.getParkDetails().getCarSlots() > 0) {
                                googleMap.addMarker(new MarkerOptions().position(new LatLng(parkLat, parkLon)).
                                        title(parkOwner.getParkDetails().getParkName()));
                                spotsFound++;
                            } else if (vehicleType == 2 && parkOwner.getParkDetails().getLorrySlots() > 0) {
                                googleMap.addMarker(new MarkerOptions().position(new LatLng(parkLat, parkLon)).
                                        title(parkOwner.getParkDetails().getParkName()));
                                spotsFound++;
                            } else if (vehicleType == 3 && parkOwner.getParkDetails().getBikeSlots() > 0) {
                                googleMap.addMarker(new MarkerOptions().position(new LatLng(parkLat, parkLon)).
                                        title(parkOwner.getParkDetails().getParkName()));
                                spotsFound++;
                            } else if (vehicleType == 4 && parkOwner.getParkDetails().getVanSlots() > 0) {
                                googleMap.addMarker(new MarkerOptions().position(new LatLng(parkLat, parkLon)).
                                        title(parkOwner.getParkDetails().getParkName()));
                                spotsFound++;
                            }
                        }
                    }
                    availableParkingSpotsTextView.setText("You have " + spotsFound + " parking spots nearby");
                } else {
                    Toast.makeText(getContext(), "No parking spots nearby", Toast.LENGTH_SHORT).show();
                    availableParkingSpotsTextView.setText("You have 0 parking spots nearby");
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);

//        googleMap.addCircle(new CircleOptions()
//                .center(googleMap.getCameraPosition().target)
//                .radius(2000)
//                .strokeWidth(0f)
//                .fillColor(0xFFFFFFFF)).re;

        mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 19);
                googleMap.animateCamera(cameraUpdate);

                this.googleMap.setOnMarkerClickListener(marker -> {
                    CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), 19);
                    googleMap.animateCamera(camUpdate);

                    LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
                    LatLng dest = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                    String url = getDirectionsUrl(origin, dest);
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);

                    return false;
                });
            }
        });

//        double radiusInMeters = 100.0;
//        int strokeColor = 0xffff0000;
//        int shadeColor = 0x44ff0000;
//
//        CircleOptions circleOptions = new CircleOptions().
//                center(googleMap.getCameraPosition().target).
//                radius(radiusInMeters).
//                fillColor(shadeColor).
//                strokeColor(strokeColor).
//                strokeWidth(2);
//        googleMap.addCircle(circleOptions);
    }

    private void geoLocate(View view) {
        String location = searchLocField.getEditText().getText().toString();
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(location, 1);
            if (addressList.size() > 0) {
                Address address = addressList.get(0);
                getLocation(address.getLatitude(), address.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())));
                Toast.makeText(getContext(), address.getLocality(), Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double calculateDistanceDifference(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            return (dist*1000);
        }
    }

    private void addParks() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance("https://safepark-c3e3f-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users");
        Query query = rootRef.child("ParkOwner");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        ParkOwner parkOwner = user.getValue(ParkOwner.class);
                        parks.add(parkOwner);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

//    @SuppressLint("MissingPermission")
//    private void getCurrentLoc() {
//        mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Location location = task.getResult();
//                getLocation(location.getLatitude(), location.getLongitude());
//            }
//        });
//    }

    private void getLocation(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 19);
        googleMap.animateCamera(cameraUpdate);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void initMap() {
        if (permissionGranted) {
            if (isGPSEnabled()) {
                SupportMapFragment supportMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapview);
                supportMapFragment.getMapAsync(this);
            }
        }
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(LOCATION_SERVICE);
        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnabled) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
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
        Dexter.withContext(getContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                permissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getContext().getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

//    public LatLng getLocationFromAddress(String strAddress){
//        Geocoder coder = new Geocoder(getContext());
//        try {
//            ArrayList<Address> addresses = (ArrayList<Address>) coder.getFromLocationName(strAddress, 50);
//            for(Address add : addresses){
//                    double longitude = add.getLongitude();
//                    double latitude = add.getLatitude();
//                return new LatLng(latitude, longitude);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQEST_CODE) {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (providerEnabled) {
                Toast.makeText(getContext(), "GPS is Enable", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "GPS is Disabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    @SuppressLint("LongLogTag")
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if (result.size() < 1) {
                Toast.makeText(getContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            distanceDurationTextView.setText("Distance :" + distance + ", Duration :" + duration);

            // Drawing polyline in the Google Map for the i-th route
            googleMap.addPolyline(lineOptions);
        }
    }
}