package com.example.davis.weatherapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

//test
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private FragmentManager fm;
    final private String tag = "fragTag";
    private static String sURL = "https://api.darksky.net/forecast/295a15b47e1a3e4649c5f43bfa41a17e/";
    public String json;

    // Location-related declarations
    private static String mLocationString;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 6 * 600000; // 60 min
    private long FASTEST_INTERVAL = 300000; // 5min
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm = getSupportFragmentManager();

        checkLocation();
        startLocationUpdates(); // sets Utils.location



        findViewById(R.id.toggle_button2).setVisibility(View.INVISIBLE);
        findViewById(R.id.toggle_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.toggle_button2).setVisibility(View.VISIBLE);
                findViewById(R.id.toggle_button).setVisibility(View.INVISIBLE);
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.setCustomAnimations(R.animator.enter_from_left, R.animator.exit_to_right, R.animator.enter_from_right, R.animator.exit_to_left);
                ForecastFragment forecastFrag = new ForecastFragment();
                transaction.replace(R.id.fragment_container, forecastFrag);
                transaction.addToBackStack(tag);
                transaction.commit();
            }
        });

        findViewById(R.id.toggle_button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.toggle_button2).setVisibility(View.INVISIBLE);
                findViewById(R.id.toggle_button).setVisibility(View.VISIBLE);
                fm.popBackStack();
            }
        });
    }

    // Location stuff (from implementing GoogleApiClient)
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) { }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }

    @Override
    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

//        TextView mLocationView = findViewById(R.id.locationView);
//        mLocationView.setText(location.getLatitude()) + ", " + String.valueOf(location.getLongitude());
        Utils.location = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
        sURL += Utils.location;

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            Utils.cityName = addresses.get(0).getLocality();
        }


        setUp();
        TodayFragment todayFrag = new TodayFragment();
        fm.beginTransaction().add(R.id.fragment_container, todayFrag).commit();

        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());



    }

    // Location helpers
    protected void startLocationUpdates() {
        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // Check if permission granted. If not, request it
        checkLocationPermission();

        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        startLocationUpdates();
                    }

                } else {
                    // permission denied
                    Toast.makeText(this,"Permission denied! App cannot function as normal.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // Fragment stuff
    public static class TodayFragment extends Fragment {
        public TodayFragment() { }

        public static TodayFragment newInstance() {
            return new TodayFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.today, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            TextView loc = getView().findViewById(R.id.locationView);
            TextView desc = getView().findViewById(R.id.descView);
            TextView low = getView().findViewById(R.id.loTempView);
            TextView hi = getView().findViewById(R.id.hiTempView);
            TextView temp = getView().findViewById(R.id.tempView);
            TextView city = getView().findViewById(R.id.cityView);
            TextView ifRain = getView().findViewById(R.id.ifRainView);
            TextView rainProb = getView().findViewById(R.id.rainProbView);

            desc.setText(Utils.description);
            low.setText(Utils.tempLow);
            hi.setText(Utils.tempHigh);
            temp.setText(Utils.tempCurrent);
            loc.setText(Utils.location);
            city.setText(Utils.cityName);
            rainProb.setText(Utils.rainProb);
            if (Utils.isRaining) {
                ifRain.setText("Currently raining");
            } else {
                ifRain.setText("Not raining");
            }
        }
    }

    public static class ForecastFragment extends Fragment {

        public ForecastFragment() { }

        public static ForecastFragment newInstance() {
            return new ForecastFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.forecast, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            TextView low1 = getView().findViewById(R.id.lowView1);
            TextView low2 = getView().findViewById(R.id.lowView2);
            TextView low3 = getView().findViewById(R.id.lowView3);
            TextView high1 = getView().findViewById(R.id.highView1);
            TextView high2 = getView().findViewById(R.id.highView2);
            TextView high3 = getView().findViewById(R.id.highView3);
            TextView summary = getView().findViewById(R.id.summaryView);
            TextView date1 = getView().findViewById(R.id.dateView1);
            TextView date2 = getView().findViewById(R.id.dateView2);
            TextView date3 = getView().findViewById(R.id.dateView3);
            TextView darksky = getView().findViewById(R.id.darksky);

            low1.setText(Utils.low1);
            low2.setText(Utils.low2);
            low3.setText(Utils.low3);
            high1.setText(Utils.high1);
            high2.setText(Utils.high2);
            high3.setText(Utils.high3);
            summary.setText(Utils.weekSummary);

            /*Date date = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int dayOfWeek1 = 1 + c.get(Calendar.DAY_OF_WEEK);
            int dayOfWeek2 = 2 + c.get(Calendar.DAY_OF_WEEK);
            int dayOfWeek3 = 3 + c.get(Calendar.DAY_OF_WEEK);
            date1.setText(new SimpleDateFormat("EE").format(dayOfWeek1));
            date2.setText(new SimpleDateFormat("EE").format(dayOfWeek2));
            date3.setText(new SimpleDateFormat("EE").format(dayOfWeek3));*/

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate date = LocalDate.parse("23/2/2010", formatter); // LocalDate = 2010-02-23
            DayOfWeek dow = date.getDayOfWeek();  // Extracts a `DayOfWeek` enum object.
            date1.setText(dow.getDisplayName(TextStyle.SHORT, Locale.US));

            darksky.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("https://darksky.net/dev");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

    }

    // JSON stuff
    private class JsonTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }

    private void setUp() {
        try {
            json = new JsonTask().execute(sURL).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.parseJSON(json);
    }
}
