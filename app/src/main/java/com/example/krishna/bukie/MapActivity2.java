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
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.flags.IFlagProvider;
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
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity2 extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, View.OnClickListener {
    private static final float DEFAULT_ZOOM = 17f;
    private static final int FINE_LOCATION = 19;
    private static final int SEARCH_LOCATION = 1;
    private  static final int TAPPED_LOCATION = 2;
    private  static final int CURRENT_LOCATION = 3;
    private BottomSheetDialog bottomSheetDialog;
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
    private PlacePickerAdapter placePickerAdapter;
    private View view_curr_location, view_selected_location, searchbtn,clearbtn;
    private boolean isPermissiongranted = false;
    //private Location currentLocation = new Location();
    private LatLng currentLocation;
    private View picklocation,mylocation;
    private boolean isSearch=true;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(6.4626999, 68.1097),
            new LatLng(35.513327, 97.39535869999999)
    );
    private List<Geopoint> nearbyList=new ArrayList<>();
    private List<Boolean> isNearbyList=new ArrayList<>();
    private List<String> nearbyListName=new ArrayList<>();
    private int previous_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);
        bottomSheetDialog=new BottomSheetDialog(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        int ZoomControl_id = 0x1;
        View zoomControls = mapFragment.getView().findViewById(ZoomControl_id);
        zoomControls.setPadding(30,0,0,100);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControls.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_START,RelativeLayout.TRUE);
        zoomControls.setLayoutParams(params);
        clearbtn=findViewById(R.id.clear);
        clearbtn.setOnClickListener(this);
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

        if (requestCode != PermissionUtils.REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION}, grantResults)) {

            if (PermissionUtils.isPermissionGranted(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION}, grantResults)) {

            }
            isPermissiongranted = true;
            init();

        } else {

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

                    try {
                        locatePlace(SEARCH_LOCATION);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                return false;
            }
        });
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(!isSearch){
                    if(s.length()!=after)
                    {
                        isSearch=true;
                        clearbtn.setVisibility(View.GONE);
                        searchbtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }



    private void locatePlace(int type) throws JSONException {
        String searchString="";
        if(type==SEARCH_LOCATION) {
            searchString = autoCompleteTextView.getText().toString().trim();
            if(searchString.length()==0)
                return;
        }



        Geocoder geocoder = new Geocoder(MapActivity2.this);
        List<Address> list = new ArrayList<>();

        try {
            if(type!=SEARCH_LOCATION&&currentLocation!=null) {
                list = geocoder.getFromLocation(currentLocation
                        .latitude, currentLocation.longitude, 1);

            }
                else  {
                isSearch=false;
                searchbtn.setVisibility(View.GONE);
                clearbtn.setVisibility(View.VISIBLE);
                list = geocoder.getFromLocationName(searchString, 1);
            }
        } catch (IOException e) {
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());

               selectedpoint = new Geopoint(address.getLatitude() + "", address.getLongitude() + "", address.getAddressLine(0));
               getNearbyPlaces();



           moveCamera(latLng,DEFAULT_ZOOM,address.getAddressLine(0),type);



        }
    }
    private void getNearbyPlaces()throws JSONException{
        String url="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+currentLocation.latitude+","+currentLocation.longitude+"&radius=1000&"+/*type=restaurant&keyword=cruise&*/"key="+getString(R.string.API_KEY);
       Log.i("url",url);
        //Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray ja = null;
                        try {
                            ja = response.getJSONArray("results");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        nearbyList.clear();
                        isNearbyList.clear();
                        if(placePickerAdapter!=null)
                        placePickerAdapter.notifyDataSetChanged();
                        if(bottomSheetDialog.isShowing())
                        bottomSheetDialog.dismiss();

                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject c = null;
                            try {
                                c = ja.getJSONObject(i);
                                Geopoint geopoint=new Geopoint(c.getJSONObject("geometry").getJSONObject("location").getDouble("lat")+"",c.getJSONObject("geometry").getJSONObject("location").getDouble("lng")+"",c.getString("name")+"");
                                nearbyList.add(geopoint);
                                isNearbyList.add(false);
                                nearbyListName.add(c.getString("name")+"");
                                } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if(!bottomSheetDialog.isShowing())
                        showBottomSheetDialog();
                        else
                            placePickerAdapter.notifyDataSetChanged();
                        Log.i("jsonlist",nearbyListName.toString());

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);

// Access the RequestQueue through your singleton class.
       // MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }



    private void moveCamera(LatLng latLng, float zoom, String title,int type) {
        Log.i("location2",latLng.toString());
        googleMap.clear();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(zoom)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.animateCamera(cameraUpdate);
        //this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        BitmapDescriptor icon = null;
        if(type==CURRENT_LOCATION)
            icon=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
        else
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title).icon(icon);
        this.googleMap.addMarker(options);
        hideSoftKeyboard();

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
                currentLocation=latLng;
                try {
                    locatePlace(TAPPED_LOCATION);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
    });
    }

    public void getCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (isPermissiongranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
                        Location templocation= (Location) task.getResult();
                        currentLocation= new LatLng(templocation.getLatitude(),templocation.getLongitude());

                        try {
                            locatePlace(CURRENT_LOCATION);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.layout_bottomsheet_placepicker, null);
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
       // bottomSheetDialog.
        BottomSheetBehavior bottomSheetBehavior=BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setPeekHeight(300);

        ListView listView=view.findViewById(R.id.listview);
        placePickerAdapter=new PlacePickerAdapter(this,nearbyList,isNearbyList);
        listView.setAdapter(placePickerAdapter);
        View share=view.findViewById(R.id.share);
        share.setOnClickListener(this);
        bottomSheetDialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MapActivity2.this, "kk", Toast.LENGTH_SHORT).show();

                if(!isNearbyList.get(position)) {
                    isNearbyList.set(position, true);
                    Geopoint geopoint=nearbyList.get(position);
                   currentLocation=new LatLng(Double.parseDouble(geopoint.getLatitude()),Double.parseDouble(geopoint.getLongitude())) ;
                    if(previous_position>0&&previous_position!=position)
                        isNearbyList.set(previous_position,false);
                    previous_position=position;
                    Log.i("kkk","kkk");
                    placePickerAdapter.notifyDataSetChanged();
                    try {
                        locatePlace(TAPPED_LOCATION);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });





    }
    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                String toastMsg = String.format("Place: %s", place.getName());
               // Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clear:
                isSearch=true;
                autoCompleteTextView.setText("");
                clearbtn.setVisibility(View.GONE);
                searchbtn.setVisibility(View.VISIBLE);
               // Toast.makeText(this, "ggg", Toast.LENGTH_SHORT).show();

                break;
            case R.id.picklocation:

               /* PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();


                try {
                    startActivityForResult(builder.build(MapActivity2.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }*/
                break;
            case R.id.share:
                break;
            case R.id.mylocation:
                getCurrentLocation();
                break;
            case R.id.selectedlocation:
                if(selectedpoint!=null) {
                    shareLocation(selectedpoint);
                }
                break;
            case R.id.currentlocation:
                if(currentpoint!=null)
                shareLocation(currentpoint);
                break;
            case R.id.search:
                //if()
                try {
                    locatePlace(SEARCH_LOCATION);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }
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

}
