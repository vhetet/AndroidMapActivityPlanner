package com.project.vincent.activityplanner;


import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;

import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.facebook.FacebookSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    public static final int NOTIFICATION_ID = 1;
    private ProgressDialog waitProgress;

    // I had to use a different API key for places and maps because there is a problem with google places for android.
    // I should normally be able to use the same key (as long as it is set up correctly with the google console).
    // But there is a bug the android and in order to make it work I had to request a web services key instead of an android one.
    private static String GOOGle_PLACES_API = "AIzaSyA7R2oTjoohMVa_3aqPqVQTF9VsgB0yffQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // allowing the url query
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // maps
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // places
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //background thread
        new BackgroundTask().execute();

        // notification
        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, Alarm.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        // Set the alarm to start at 9:00 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);

        // set the alarm manager to trigger at approximatively 9 am avery morning
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);


        // facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // navigation drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            hideSoftKeyboard(MainActivity.this);
            SavedEventFragment sep = new SavedEventFragment();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .remove(getSupportFragmentManager()
                            .findFragmentById(R.id.saved_event_frame))
                    .commit();
        } else if (id == R.id.nav_saved_event) {
            hideSoftKeyboard(MainActivity.this);
            SavedEventFragment sep = new SavedEventFragment();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    // I'll make my own customAnimation later
//                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .add(R.id.saved_event_frame, sep)
                    .commit();
        } else if (id == R.id.nav_share) {
            hideSoftKeyboard(MainActivity.this);

            Toast.makeText(MainActivity.this, "The facebook dialogbox can take time to appear, this is normal", Toast.LENGTH_LONG).show();

            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://play.google.com/store"))
                    .setContentDescription("This link is empty right now, but when the app will be published it will be a link to the google play")
                    .build();
            ShareDialog shareDialog = new ShareDialog(this);
            shareDialog.show(content);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // map
    @Override
    public void onMapReady(final GoogleMap map) {
        String[] permissionList = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            requestPermissions(permissionList, 1);
        }

        // search
        EditText searchText = (EditText) findViewById(R.id.searchText);
        Button buttonSearch = (Button) findViewById(R.id.searchButton);

        String parameter = searchText.getText().toString();

        buttonSearch.setOnClickListener(new CustomOnClickListener(map, parameter));
        map.setOnMapClickListener(new CustomOnMapClickListener());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            Location l = lm.getLastKnownLocation(lm.GPS_PROVIDER);


            if (l != null) {
                double latitude = 0;
                double longitude = 0;
                latitude = l.getLatitude();
                longitude = l.getLongitude();
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12));
            } else {
                Toast.makeText(MainActivity.this, "Unable to find you location, please restart the app", Toast.LENGTH_LONG).show();
            }

        }
    }

    // places
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Api connection", "Connection success");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MainActivity.this, "Connection suspended", Toast.LENGTH_SHORT).show();
        Log.d("Api connection", "Connection suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this, "Connection fail", Toast.LENGTH_SHORT).show();
        Log.d("Api connection", "Connection fail");
    }

    /**
     * Class that start and handle the background thread
     */
    public class BackgroundTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (waitProgress != null) {
                waitProgress.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            // 6 seconds after the user start the application he will receive a notification
            // the only purpose of this notification is to prove that the notifications work without
            // waiting the trigering time of the regular notification (9:00 am every day)
            SystemClock.sleep(6000);
            sendNotification();
            return null;
        }
    }

    /**
     * Send a request to the places api and return the results, it also display it with markers on the map
     * not used in the last version of the project
     *
     * @param map
     * @return the result of the search
     */
    public String nearbyQuery(GoogleMap map) {
        String res = "";
        URL url;
        URLConnection urlConnection;
        InputStream in;
        try {
            url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=41.8781,-87.6298&radius=250&name=burger&types=food&key=" + GOOGle_PLACES_API);
            urlConnection = url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            try {
                res = readStream(in);
                JSONObject jsonObject = new JSONObject(res);

                placeMarker(jsonObject, map);
            } finally {
                in.close();
            }
        } catch (java.io.IOException e) {
            Log.d("URL", e.toString());
        } catch (org.json.JSONException e) {
            Log.d("URL", e.toString());
        }
        return res;
    }

    /**
     * Custom on marker listener, when the marker is clicked a fragment InfoPanelActivity is displayed with information from the marker
     * The information are send from the activity to this marker listener in the snippet. The JSON object is stringified and place in the array
     * the in the listener the content of the snippet is converted back in JSON object and used.
     */
    public class CustomOnMarkerClickListener implements GoogleMap.OnMarkerClickListener {
        JSONObject place;
        GoogleMap map;

        public CustomOnMarkerClickListener(JSONObject place, GoogleMap map) {
            this.place = place;
            this.map = map;
        }

        @Override
        public boolean onMarkerClick(Marker marker) {

            hideSoftKeyboard(MainActivity.this);

            InfoPanelActivity ipa = new InfoPanelActivity();
            FragmentManager fm = getSupportFragmentManager();

            Bundle args = new Bundle();
            args.putString("place", marker.getSnippet());

            try {
                JSONObject place = new JSONObject(marker.getSnippet());
                JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getDouble("lat"), location.getDouble("lng")), 14));
            } catch (JSONException e) {
                Log.d("JSONException", e.toString());
            }

            ipa.setArguments(args);

            if (findViewById(R.id.textView2) == null) {
                fm.beginTransaction()
                        .add(R.id.info_panel_frame, ipa)
                        .commit();
            } else {
                fm.beginTransaction()
                        .replace(R.id.info_panel_frame, ipa)
                        .commit();
            }
            return true;
        }

    }

    /**
     * First version of the marker click listener, display the information fragment when call
     * not used in the last version of the project
     */
    public class CustomOnMarkerClickListener2 implements GoogleMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            InfoPanelActivity ipa = new InfoPanelActivity();

            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .add(R.id.info_panel_frame, ipa)
                    .commit();
            return false;
        }
    }

    /**
     * This listener start a search for places
     */
    public class CustomOnClickListener implements View.OnClickListener {

        GoogleMap map;
        String parameter;

        public CustomOnClickListener(GoogleMap map, String parameter) {
            this.map = map;
            this.parameter = parameter;
        }

        /**
         * start the search and display the result on the map. It also clear the search bar
         *
         * @param v
         */
        @Override
        public void onClick(View v) {
            hideSoftKeyboard(MainActivity.this);
            searchQuery(map, "food", parameter, map.getCameraPosition().target);
            EditText searchText = (EditText) findViewById(R.id.searchText);
            searchText.setText("");
        }

    }

    /**
     * This click listener is used to hide the keybord and the information fragment when the user click on the map
     */
    public class CustomOnMapClickListener implements GoogleMap.OnMapClickListener {
        @Override
        public void onMapClick(LatLng latLng) {
            if (getSupportFragmentManager().findFragmentById(R.id.info_panel_frame) != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(getSupportFragmentManager()
                                .findFragmentById(R.id.info_panel_frame))
                        .commit();
            }
            hideSoftKeyboard(MainActivity.this);
        }
    }

    /**
     * Use the data from a place JSONObject and display it on the map with a marker, it also set a listener on the marker so that when the marker is clicked the information fragment will be displayed
     *
     * @param map
     * @param place
     * @throws org.json.JSONException
     */
    public void setListener(GoogleMap map, JSONObject place) throws org.json.JSONException {
        Log.d("JSONObject", place.toString());
        JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
        double lat = location.getDouble("lat");
        double lng = location.getDouble("lng");
        map.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(place.getString("name"))
                .snippet(place.toString()));
        map.setOnMarkerClickListener(new CustomOnMarkerClickListener(place, map));
    }


    /**
     * Send a request to the places API and retrieve the data sent back by the API
     * The request take in parameter the word type by the user and the center and the zoom level of the map whe the search is started
     *
     * @param map
     * @param type
     * @param parameter
     * @param latLng
     * @return a JSONArray with places that match the search criteria
     */
    public String searchQuery(GoogleMap map, String type, String parameter, LatLng latLng) {


        // todo improve the query, it could be more accurate
        String res = "";
        String queryBase = "https://maps.googleapis.com/maps/api/place/nearbysearch";
        String queryOutPut = "/json";
        String queryLocation = "?location=" + latLng.latitude + "," + latLng.longitude;
        String queryRadius = "&radius=" + getRadiusWithZoom((int) map.getCameraPosition().zoom);
        String queryParameter = "&name=" + parameter;
        String queryTypes = "&types=" + type;
        String queryKey = "&key=" + GOOGle_PLACES_API;
        String query = queryBase + queryOutPut + queryLocation +
                queryRadius + queryParameter + queryTypes + queryKey;
        URL url;
        URLConnection urlConnection;
        InputStream in;
        try {
            url = new URL(query);
            urlConnection = url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            try {
                res = readStream(in);
                JSONObject jsonObject = new JSONObject(res);

                placeMarker(jsonObject, map);
            } finally {
                in.close();
            }
        } catch (java.io.IOException e) {
            Log.d("URL", e.toString());
        } catch (org.json.JSONException e) {
            Log.d("URL", e.toString());
        }
        return res;
    }

    /**
     * read the input stream and return it in a String
     *
     * @param is
     * @return the content of the input stream as a String
     */
    public String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public Integer getRadiusWithZoom(int zoom) {
        //todo make a function of that
        // this array give the radius for each zoom level;
        // the maximum radius for a search with Places API is 50km
        // this is why there is only 50000 (in meter ==> 50km) at the beginning of the array
        int[] radiusForZoom = {50000, 50000, 50000, 50000, 50000, 50000, 50000, 50000, 50000, 30000, 15000, 7500, 3750, 1800, 900, 450, 225, 225, 225, 225, 225, 225};
        // by default the maximum radius is set
        int radius = 50000;
        if (zoom >= 1 || zoom <= 21)
            radius = radiusForZoom[zoom];
        return radius;
    }

    /**
     * Use the JSON file retrieved by the places api search to display the marker on the map
     *
     * @param jsonObject
     * @param map
     */
    public void placeMarker(JSONObject jsonObject, GoogleMap map) {
        try {
            JSONArray results = jsonObject.getJSONArray("results");
            map.clear(); // clear delete all the previous marker
            for (int i = 0; i < results.length(); i++) {
                JSONObject place = results.getJSONObject(i);
                setListener(map, place);
            }
        } catch (org.json.JSONException e) {
            Log.d("URL", e.toString());
        }
    }

    /**
     * Send a sample notification using the NotificationCompat API.
     */
    public void sendNotification() {

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setContentTitle("Activity planner")
                .setContentText("An example to prove my notification works");

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * On the first start the user is requested if he accept that the app use his location.
     * If he accept the activity is restarted so that it can use the location
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                startActivity(new Intent(this, MainActivity.class));

    }
}