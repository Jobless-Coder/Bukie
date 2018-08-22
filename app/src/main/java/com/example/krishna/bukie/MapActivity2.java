package com.example.krishna.bukie;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity2 extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, View.OnClickListener {
    private static final float DEFAULT_ZOOM = 17f;
    private static final int FINE_LOCATION = 19;
    private  static final int PLACE_PICKER_REQUEST = 1;
    //private static final int ERROR_DIALOG = 25;
    private SupportMapFragment mapFragment;
    protected GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private AutoCompleteTextView autoCompleteTextView;
    private ImageView imageView;
    private GoogleMap googleMap;
    private Geopoint selectedpoint;
    private Geopoint currentpoint;
    private View view_curr_location, view_selected_location, searchbtn;
    private boolean isPermissiongranted = false;
    private Location currentLocation;
    private View picklocation,mylocation;
    //private PlaceDetectionClient placeDetectionClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    //private
    //private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(6.4626999, 68.1097),
            new LatLng(35.513327, 97.39535869999999)
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        int ZoomControl_id = 0x1;
        View zoomControls = mapFragment.getView().findViewById(ZoomControl_id);
        zoomControls.setPadding(0,0,0,150);
        view_curr_location = findViewById(R.id.currentlocation);
        view_selected_location = findViewById(R.id.selectedlocation);
        searchbtn = findViewById(R.id.search);
        searchbtn.setOnClickListener(this);
        view_selected_location.setOnClickListener(this);
        view_curr_location.setOnClickListener(this);
        mylocation=findViewById(R.id.mylocation);
        picklocation=findViewById(R.id.picklocation);
        mylocation.setOnClickListener(this);
        picklocation.setOnClickListener(this);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.input_search);
        //final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chats);
        //toolbar.setTitle("");
       // setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // mGeoDataClient = Places.getGeoDataClient(this, null);
        // mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        checkMyPermissionLocation();
    }

    private void checkMyPermissionLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)

                PermissionUtils.requestPermission(this);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                PermissionUtils.requestPermission(this);

            }
            PermissionUtils.requestPermission(this);
        } else {
            isPermissiongranted = true;
            init();
            //start maps
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        //If the request code does not match
        if (requestCode != PermissionUtils.REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION}, grantResults)) {

            if (PermissionUtils.isPermissionGranted(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION}, grantResults)) {
                //init();
            }
            isPermissiongranted = true;
            init();

        } else {
            //Toast.makeText(this, "Stop apps without permission to use location information", Toast.LENGTH_SHORT).show();
            //finish();
        }


    }

    private void init() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                BOUNDS_INDIA, null);

        autoCompleteTextView.setAdapter(placeAutocompleteAdapter);


        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    locatePlace(false);
                }

                return false;
            }
        });
    }

    private void locatePlace(boolean isCurrent) {
        String searchString="";
        if(!isCurrent)
        searchString = autoCompleteTextView.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity2.this);
        List<Address> list = new ArrayList<>();
       // list.clear();
        try {
            if(isCurrent&&currentLocation!=null) {
                list = geocoder.getFromLocation(currentLocation
                        .getLatitude(), currentLocation.getLongitude(), 1);
                //Toast.makeText(this, "ffffff", Toast.LENGTH_SHORT).show();
            }
                else if(!isCurrent)
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
           if (isCurrent) {
               Log.i("locationcurr",latLng.toString());
               currentpoint = new Geopoint(address.getLatitude() + "", address.getLongitude() + "", address.getLocality());

           }else {
               selectedpoint = new Geopoint(address.getLatitude() + "", address.getLongitude() + "", address.getLocality());
           }



           moveCamera(latLng,DEFAULT_ZOOM,address.getAddressLine(0),isCurrent);

           // Toast.makeText(this, ""+latLng.toString(), Toast.LENGTH_SHORT).show();
           /* CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,15);
            googleMap.animateCamera(cameraUpdate);*/
            //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

        }
    }


    private void moveCamera(LatLng latLng, float zoom, String title,boolean isCurrent) {
        Log.i("location2",latLng.toString());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(zoom)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.animateCamera(cameraUpdate);
        //this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        BitmapDescriptor icon = null;
        if(isCurrent)
            icon=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        else
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title).icon(icon);
        this.googleMap.addMarker(options);


        hideSoftKeyboard();
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,17);
        googleMap.animateCamera(cameraUpdate);
       // googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        //if(!title.equals("My Location")){
        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title).icon(icon);
        googleMap.addMarker(options);
        // }
        hideSoftKeyboard();*/
    }

    private void hideSoftKeyboard() {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
    }


    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(Activity.RESULT_CANCELED);
        finish();
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;
       this.googleMap.setLatLngBoundsForCameraTarget(BOUNDS_INDIA);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        getCurrentLocation();
      //  this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        this.googleMap.getUiSettings().setCompassEnabled(true);
        this.googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();
                Geocoder geocoder = new Geocoder(MapActivity2.this);
                List<Address> list = new ArrayList<>();
                try {
                    list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (list.size() > 0) {
                    Address address = list.get(0);

                    selectedpoint = new Geopoint(address.getLatitude() + "", address.getLongitude() + "", address.getLocality());
                    //Toast.makeText(MapActivity2.this, ""+latLng.toString(), Toast.LENGTH_SHORT).show();
                    Log.i("location3",latLng.toString());
                    moveCamera(latLng,DEFAULT_ZOOM,address.getAddressLine(0),false);


            }
        }

       // View zoomControls = this.googleMap.findViewById(ZoomControl_id);


    });
    }

    public void getCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (isPermissiongranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                FINE_LOCATION);

                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                FINE_LOCATION);
                    }

                }

            }
            //Toast.makeText(this, "kkk", Toast.LENGTH_SHORT).show();
             Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        currentLocation= (Location) task.getResult();

                        locatePlace(true);

                    }
                }
            });
       }
    }
    public void shareLocation(Geopoint geopoint1){
        //Geopoint geopoint = new Geopoint(Latitude, Longitude, Locality);
        Intent intent = new Intent(MapActivity2.this, ChatActivity.class);
        intent.putExtra("geopoint", geopoint1);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.picklocation:

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                //builder.setLatLngBounds(new LatLngBounds(selectedpoint.latitude,selectedpoint.longitude));

                try {
                    startActivityForResult(builder.build(MapActivity2.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.mylocation:
                getCurrentLocation();
                break;
            case R.id.selectedlocation:
                if(selectedpoint!=null) {
                    Log.i("locationsel",selectedpoint.getLatitude()+" "+selectedpoint.getLongitude());
                    shareLocation(selectedpoint);
                }
                break;
            case R.id.currentlocation:
                if(currentpoint!=null)
                shareLocation(currentpoint);
                break;
            case R.id.search:
                locatePlace(false);
                break;

        }
    }
}
