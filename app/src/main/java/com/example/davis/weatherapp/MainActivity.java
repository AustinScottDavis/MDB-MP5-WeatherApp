package com.example.davis.weatherapp;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm = getSupportFragmentManager();

        findViewById(R.id.toggleButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.animator.enter_from_left, R.animator.exit_to_right, R.animator.enter_from_right, R.animator.exit_to_left);
                ForecastFragment fragment = new ForecastFragment();
                transaction.replace(R.id.today, fragment);
//                transaction.addToBackStack(tag);
                transaction.commit();
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
