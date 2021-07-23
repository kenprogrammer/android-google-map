package com.example.googlemap;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.googlemap.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationClient;
    private ActivityMapsBinding binding;
    String[] cordinates={"-34, 151","-31.083332, 150.916672","-32.916668, 151.750000","-27.470125, 153.021072"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Check location permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

        mMap.setMyLocationEnabled(true);

        //Get last known location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            ///String mLocation=getLocationName(location.getLatitude(), location.getLongitude());

                            if(mMap != null) {
                                for (int i = 0; i < cordinates.length; i++) {
                                    String[] result=cordinates[i].split(",");
                                    System.out.println("Lat: "+result[0]);
                                    System.out.println("Lng: "+result[1]);

                                    String mLocation=getLocationName(Double.parseDouble(result[0]), Double.parseDouble(result[1]));

                                    LatLng mylocation = new LatLng(Double.parseDouble(result[0]),Double.parseDouble(result[1]));
                                    mMap.addMarker(new MarkerOptions().position(mylocation).title(mLocation));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), "Map Not Ready", Toast.LENGTH_LONG).show();
                            }

                            Toast.makeText(getApplicationContext(), "Longitude: " + location.getLongitude() + " Latitude: " + location.getLatitude(), Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "No Location", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //Check location permission
    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }

    //Get location
    public String getLocationName(Double latitude,Double longitude)
    {
        String cityName;
        String stateName;
        String countryName;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!addresses.isEmpty()) {
            cityName = addresses.get(0).getAddressLine(0);
            stateName = addresses.get(0).getAddressLine(1);
            countryName = addresses.get(0).getAddressLine(2);
        }else{
            cityName="Marker";
        }

        return cityName;
    }
}