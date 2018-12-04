package com.redmart.assignment.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.redmart.assignment.R;
import com.redmart.assignment.ui.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize fragment manager
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.container,new HomeFragment(),"Home").commit();

    }
}
