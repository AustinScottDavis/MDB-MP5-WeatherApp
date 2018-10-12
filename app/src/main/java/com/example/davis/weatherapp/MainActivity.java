package com.example.davis.weatherapp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//test
public class MainActivity extends AppCompatActivity {
    private FragmentManager fm;
    final private String tag = "fragTag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm = getSupportFragmentManager();


        TodayFragment todayFrag = new TodayFragment();
        fm.beginTransaction().add(R.id.fragment_container, todayFrag).commit();

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
