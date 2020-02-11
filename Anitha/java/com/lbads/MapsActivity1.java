package com.lbads;

import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.lbads.api.LocDetails;
import com.lbads.api.RetrifitAPI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import org.json.JSONObject;
import static android.graphics.Typeface.BOLD_ITALIC;

public class MapsActivity1 extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    int des_radius=100;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    double dest_lat,dest_lng;
    String dest_name;
    float zoom_level=17f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        autoPlaceComplete();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getAllPoints();
        Button btn_start_journey=(Button)findViewById(R.id.btn_start_journey);
        btn_start_journey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });
    }

    GPSTracker gps;
    double start_latitude;
    double start_longitude;
    Marker carMarker;
    private void getLatLng(){
        gps = new GPSTracker(MapsActivity1.this);
        // Check if GPS enabled
        if(gps.canGetLocation()) {
            start_latitude = gps.getLatitude();
            start_longitude = gps.getLongitude();
            //Toast.makeText(MapsActivity.this,""+latitude,Toast.LENGTH_SHORT).show();
            LatLng new_point = new LatLng(start_latitude, start_longitude );
            start_latLng = new LatLng(start_latitude, start_longitude );
            /*IconGenerator iconFactory = new IconGenerator(MapsActivity1.this);
            iconFactory.setRotation(0);
            iconFactory.setContentRotation(0);
            iconFactory.setStyle(IconGenerator.STYLE_ORANGE);
            addIcon(iconFactory, "Your Location", new_point,null);*/
            carMarker=mMap.addMarker(new MarkerOptions()
                    .position(new_point)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car))
                    );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new_point, zoom_level));
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }
    }
    ArrayList<MapsPoints> alPoints=new ArrayList<MapsPoints>();
    private void getAllPoints(){
        alPoints.clear();
        locDetails();
    }

    ProgressDialog pd;
    private void locDetails() {
        pd = new ProgressDialog(MapsActivity1.this);
        pd.setTitle("Please wait,Data is being loaded...");
        pd.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.locationbasedadvertising.site/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrifitAPI request = retrofit.create(RetrifitAPI.class);
        Call<List<LocDetails>> call = request.getLocDetails();
        call.enqueue(new Callback<List<LocDetails>>() {
            @Override
            public void onResponse(Call<List<LocDetails>> call, Response<List<LocDetails>> response) {
                pd.dismiss();
                //Toast.makeText(getApplicationContext(),""+response.body().size(),Toast.LENGTH_SHORT).show();
                for(int i=0;i<response.body().size();i++) {
                    LatLng ll = new LatLng(Double.parseDouble(response.body().get(i).lat), Double.parseDouble(response.body().get(i).log));
                    MapsPoints point = new MapsPoints(ll, response.body().get(i).name, response.body().get(i).des,response.body().get(i).type,response.body().get(i).location,response.body().get(i).offer,response.body().get(i).tim,response.body().get(i).img,response.body().get(i).phno,response.body().get(i).from_date,response.body().get(i).to_date);
                    alPoints.add(point);
                }
            }

            @Override
            public void onFailure(Call<List<LocDetails>> call, Throwable t) {
                pd.dismiss();
            }
        });
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLatLng();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getTag()!=null) {
                    MapsPoints mp = (MapsPoints) marker.getTag();
                    Toast.makeText(getApplicationContext(), mp.title + "", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), AdsDetailsActivity.class);
                    i.putExtra("title",mp.title);
                    i.putExtra("des",mp.description);
                    i.putExtra("type",mp.type);
                    i.putExtra("location",mp.location);
                    i.putExtra("offer",mp.offer);
                    i.putExtra("tim",mp.timings);
                    i.putExtra("imgs",mp.imgs);
                    i.putExtra("phno",mp.phno);
                    i.putExtra("from_date",mp.from_date);
                    i.putExtra("to_date",mp.to_date);
                    startActivity(i);
                }
                return false;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                // TODO Auto-generated method stub

                drawPath2Points();

                LatLng new_point = new LatLng(arg0.latitude, arg0.longitude);
                animateMarker(new_point,true);

                IconGenerator iconFactory = new IconGenerator(MapsActivity1.this);
                iconFactory.setRotation(0);
                iconFactory.setContentRotation(0);
                iconFactory.setStyle(IconGenerator.STYLE_ORANGE);

                for(int i=0;i<alPoints.size();i++){
                    LatLng new_point1 = alPoints.get(i).latlng;
                    iconFactory.setRotation(0);
                    iconFactory.setContentRotation(0);
                    iconFactory.setStyle(IconGenerator.STYLE_GREEN);

                    float ft=getKmFromLatLong(alPoints.get(i).latlng,arg0);
                    if(ft<=des_radius) {
                        addIcon(iconFactory, alPoints.get(i).type, new_point1,alPoints.get(i));
                        //Toast.makeText(getApplicationContext(),"added..",Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
        // Add a marker in Sydney and move the camera



    }
    public float getKmFromLatLong(LatLng latlng,LatLng myloc){
        Location loc1 = new Location("");
        loc1.setLatitude(latlng.latitude);
        loc1.setLongitude(latlng.longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(myloc.latitude);
        loc2.setLongitude(myloc.longitude);

        float distanceInMeters = loc1.distanceTo(loc2);
        //return distanceInMeters/1000;
        return distanceInMeters;
    }

    private void addIcon(IconGenerator iconFactory, CharSequence text, LatLng position,MapsPoints mp) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        Marker m=mMap.addMarker(markerOptions);
        if(mp!=null) {
            m.setTag(mp);
        }
    }
    Circle circle;
    void drawCircle(Double latitude,Double longitude){
        CircleOptions circleOptions=new CircleOptions();
        circleOptions.center(new LatLng(latitude, longitude));
        circleOptions.radius(des_radius*1000f);
        circleOptions.strokeWidth(1.0f);
        circleOptions.strokeColor(Color.RED);
        circleOptions.fillColor(Color.parseColor("#44000000"));
        mMap.addCircle(circleOptions);
    }

    private void autoPlaceComplete(){
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyB0UHUCxSw1Q5h4BBkxj_R-T4yUScgifn0");
        }
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                //Toast.makeText(getApplicationContext(),""+place.getLatLng().latitude,Toast.LENGTH_SHORT).show();
                //Log.i("Map", "Place: " + place.getName() + ", " + place.getId());
                mMap.clear();
                LatLng new_point = new LatLng(start_latitude, start_longitude );
                carMarker=mMap.addMarker(new MarkerOptions()
                        .position(new_point)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car))
                );
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new_point, zoom_level));
                dest_name = place.getName();
                dest_lat = place.getLatLng().latitude;
                dest_lng = place.getLatLng().longitude;

                drawPath2Points();
            }
            @Override
            public void onError(Status status) { }
        });
    }
    LatLng start_latLng;
    private void drawPath2Points(){
        LatLng start_point = new LatLng(start_latitude, start_longitude );
        IconGenerator iconFactory1 = new IconGenerator(MapsActivity1.this);
        iconFactory1.setRotation(0);
        iconFactory1.setContentRotation(0);
        iconFactory1.setStyle(IconGenerator.STYLE_ORANGE);
        addIcon(iconFactory1, "Starting Point", start_point,null);



        LatLng new_point = new LatLng(dest_lat, dest_lng );
        IconGenerator iconFactory = new IconGenerator(MapsActivity1.this);
        iconFactory.setRotation(0);
        iconFactory.setContentRotation(0);
        iconFactory.setStyle(IconGenerator.STYLE_RED);
        addIcon(iconFactory, "Destination : "+dest_name, new_point,null);

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(start_point, new_point);
        Log.i("My Url",url);
        DownloadTask downloadTask = new DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


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

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving&key=AIzaSyA6HZpU5TaXDjNbU4GfY4fHDTyWQBcunEo";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

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
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public void animateMarker(final LatLng toPosition, final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(start_latLng);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Location prevLoc = new Location("service Provider");
                prevLoc.setLatitude(startLatLng.latitude);
                prevLoc.setLongitude(startLatLng.longitude);
                Location newLoc = new Location("service Provider");
                newLoc.setLatitude(toPosition.latitude);
                newLoc.setLongitude(toPosition.longitude);
                float bearing = prevLoc.bearingTo(newLoc);
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / 5000);
                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;
                carMarker.setPosition(new LatLng(lat, lng));
                carMarker.setRotation(bearing);
                carMarker.setAnchor(0.5f, 0.5f);
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(17f).build()));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }else{
                    start_latLng=new LatLng(lat, lng);
                    /*else {
                    if (hideMarker) {
                        cabMarker.setVisible(false);
                    } else {
                        cabMarker.setVisible(true);
                    }
                }*/
                }
            }
        });
    }

    LocationManager locationManager;
    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 3, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(TestMove2Activity.this, ""+location.getLatitude(), Toast.LENGTH_SHORT).show();
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());

        animateMarker(latLng,true);

        IconGenerator iconFactory = new IconGenerator(MapsActivity1.this);
        iconFactory.setRotation(0);
        iconFactory.setContentRotation(0);
        iconFactory.setStyle(IconGenerator.STYLE_ORANGE);

        for(int i=0;i<alPoints.size();i++){
            LatLng new_point1 = alPoints.get(i).latlng;
            iconFactory.setRotation(0);
            iconFactory.setContentRotation(0);

            if(alPoints.get(i).type.equals("Resturant")) {
                iconFactory.setStyle(IconGenerator.STYLE_GREEN);
            }else if(alPoints.get(i).type.equals("Job")) {
                iconFactory.setStyle(IconGenerator.STYLE_BLUE);
            }else if(alPoints.get(i).type.equals("Mall")) {
                iconFactory.setStyle(IconGenerator.STYLE_RED);
            }else{
                iconFactory.setStyle(IconGenerator.STYLE_PURPLE);
            }

            float ft=getKmFromLatLong(alPoints.get(i).latlng,latLng);
            if(ft<=des_radius) {
                addIcon(iconFactory, alPoints.get(i).type, new_point1,alPoints.get(i));
                //Toast.makeText(getApplicationContext(),"added..",Toast.LENGTH_SHORT).show();
            }

        }

        animateMarker(latLng,true);
    }
    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MapsActivity1.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}
