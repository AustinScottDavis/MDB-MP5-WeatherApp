package com.example.davis.weatherapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonStreamParser;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

//test
public class MainActivity extends AppCompatActivity {
    private FragmentManager fm;
    final private String tag = "fragTag";
    private static final String sURL = "https://api.darksky.net/forecast/295a15b47e1a3e4649c5f43bfa41a17e/37.8267,-122.4233";
    public String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm = getSupportFragmentManager();
        try {
            json = new JsonTask().execute(sURL).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.parseJSON(json);

        TodayFragment todayFrag = new TodayFragment();
        fm.beginTransaction().add(R.id.fragment_container, todayFrag).commit();
        //todayFrag.setText();

        findViewById(R.id.toggle_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                fm.popBackStack();
            }
        });

    }

    public static class TodayFragment extends Fragment {

        public TodayFragment() {

        }

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

            TextView desc = getView().findViewById(R.id.descView);
            desc.setText(Utils.description);
        }
    }

    public static class ForecastFragment extends Fragment {

        public ForecastFragment() {

        }

        public static ForecastFragment newInstance() {
            return new ForecastFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


            return inflater.inflate(R.layout.forecast, container, false);
        }

    }

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
}
