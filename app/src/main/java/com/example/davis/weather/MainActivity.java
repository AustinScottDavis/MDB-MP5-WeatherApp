package com.example.davis.weather;

import android.support.constraint.Placeholder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.davis.weatherapp.R;

//test
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
