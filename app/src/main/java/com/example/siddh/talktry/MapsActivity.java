package com.example.siddh.talktry;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final int FINE_REQ_CODE = 1234;
    private static final int NETWORK_REQ_CODE = 1345;
    private static final int INTER_REQ_CODE = 4567;
    private GoogleMap mMap;
    LocationManager locationManager;
    String provider;
    private static final int COARSE_REQ_CODE = 1123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        provider = locationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestFinePermission();
            //requestCoarsePermission();
            //requestInternetPermission();
            //requestNetworkPermission();

            Log.d("checking location", "location permisiion");

            return;

        }

        Location location = locationManager.getLastKnownLocation(provider);
        Log.i("checking location", "location");
        /*if (location != null) {
            Log.i("Location_change", "Achieved");
            onLocationChanged(location);
        } else*/ if(location == null) {
            Log.i("Location_change", "Not Achieved");
        }

    }

    public boolean requestFinePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.v("location permission", "fine granted");
                return true;
            } else {

                Log.v("location permission", "fine not granted");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_REQ_CODE);
                return false;
            }

        } else {
            Log.v("location permission", "coarse les build granted");
            return true;
        }
    }

   /* public boolean requestNetworkPermission()
    {
        if(Build.VERSION.SDK_INT >= 23)
        {
            if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("location permission", "network granted");
                return true;
            }
            else {

                Log.v("location permission", "network not granted");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE},NETWORK_REQ_CODE);
                return false;
            }

        }

        else
        {
            Log.v("location permission", "fine les build granted");
            return true;
        }
    }

    public boolean requestCoarsePermission()
    {
        if(Build.VERSION.SDK_INT >= 23)
        {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.v("location permission", "coarse granted");
                return true;
            }
            else {

                Log.v("location permission", "coarse not granted");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},COARSE_REQ_CODE);
                return false;
            }

        }

        else
        {
            Log.v("location permission", "fine les build granted");
            return true;
        }
    }

    public boolean requestInternetPermission()
    {
        if(Build.VERSION.SDK_INT >= 23)
        {
            if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                Log.v("location permission", "internet granted");
                return true;
            }
            else {

                Log.v("location permission", "internet not granted");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},INTER_REQ_CODE);
                return false;
            }

        }

        else
        {
            Log.v("location permission", "fine les build granted");
            return true;
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_REQ_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            Log.v("location", "fine granted");

        else if (requestCode == FINE_REQ_CODE && grantResults[0] == PackageManager.PERMISSION_DENIED)
            ;
        activityRedirect();
    }

    private void activityRedirect() {

        finish();
        Intent i = new Intent(MapsActivity.this, CombinedActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 100, MapsActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {

        Double lat = location.getLatitude();
        Double longt = location.getLongitude();
        LatLng curr_loc = new LatLng(lat, longt);

        Float speed = location.getSpeed();
        Float bearing = location.getBearing();
        Float accurracy = location.getAccuracy();
        Double alt = location.getAltitude();

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocation(lat,longt,1);

            if(addressList!=null && addressList.size()>0)
            {
                Log.i("place address", addressList.get(0).toString());

                String addressHolder = "";

                for(int i=0; i<= addressList.get(0).getMaxAddressLineIndex(); i++)
                    addressHolder += addressList.get(0).getAddressLine(i) + "\n";


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Log.i("Location_change", lat.toString() + "hello " + longt.toString());

        mMap.clear();

        mMap.addMarker(new MarkerOptions().position(curr_loc).title(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curr_loc, 12));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curr_loc));

        chatMap(location);

    }

    private void chatMap(Location location) {

        DatabaseReference loc = FirebaseDatabase.getInstance().getReference().child("user_locations");

        Double lat = location.getLatitude();
        Double longt = location.getLongitude();

        GeoFire geo = new GeoFire(loc);

        geo.setLocation(formatEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().trim()), new GeoLocation(lat*0000001, longt*0000001), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Toast.makeText(MapsActivity.this,"There was an error saving the location to GeoFire: " + error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MapsActivity.this,"Location saved on server successfully!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        markLocation();
    }

    private void markLocation() {

        DatabaseReference loc = FirebaseDatabase.getInstance().getReference().child("user_locations");

        loc.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {

                    Double lat = (Double) singleSnapshot.child("l").child("0").getValue();
                    Double longt = (Double) singleSnapshot.child("l").child("1").getValue();

                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat,longt)).title("powered"));


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.d("checking location", "location");
        LatLng sydney = new LatLng(27.1750, 78.0422);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Agra"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12));

        //onLocationChanged();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestFinePermission();
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        Log.i("checking location", "location");
        if (location != null) {
            Log.i("Location_change", "Achieved");
            onLocationChanged(location);
        } /*else {
            Log.i("Location_change", "Not Achieved");
        }*/
    }

    public String formatEmail(String email)
    {
        String fmail;
        fmail = email.substring(0, email.length()-4);
        fmail = fmail.replace('@','X');
        fmail = fmail.replace('.','X');
        //System.out.println(fmail);
        return fmail;
    }

}
