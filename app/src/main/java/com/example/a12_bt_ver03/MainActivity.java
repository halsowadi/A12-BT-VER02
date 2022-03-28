package com.example.a12_bt_ver03;
/*
Hussein Alsowadi
Last Updated: 1/23/22
Template App
 */

import com.example.a12_bt_ver03.databinding.ActivityMainBinding;
import com.example.a12_bt_ver03.databinding.FragmentOneBinding;
import com.google.android.material.tabs.TabLayout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class MainActivity extends AppCompatActivity implements MyFragmentDataPassListener{
    FragmentOne frag01;
    FragmentTwo frag02;


    private ActivityMainBinding binding1;
    private FragmentOneBinding binding;

    private static final String TAG = null;
    // BT Variables
    private final String CV_ROBOTNAME = "EV3A";
    private BluetoothAdapter cv_btInterface = null;
    private Set<BluetoothDevice> cv_pairedDevices = null;
    private BluetoothDevice cv_btDevice = null;
    private BluetoothSocket cv_btSocket = null;

    // Data stream to/from NXT bluetooth
    private InputStream cv_is = null;
    private OutputStream cv_os = null;
    String myString;
    //private static final UUID CONNECTION_UUID = UUID.fromString("00001101-0000-1000-8000-00165382A946");
    private static final UUID CONNECTION_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private TabLayout tablayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ////setContentView(R.layout.activity_main);
        binding1 = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding1.getRoot();
        setContentView(view);
        frag01 = new FragmentOne();
        frag02 = new FragmentTwo();
        setContentFragment(1);
        getSupportActionBar().setElevation(0);
        // attaching tab mediator
        tablayout = findViewById(R.id.tablayout);
        tablayout.addTab(tablayout.newTab().setText("Connect"));
        tablayout.addTab(tablayout.newTab().setText("Drive"));

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setContentFragment(tab.getPosition()+1);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    public static void loadFragment(AppCompatActivity activity, int containerId, Fragment fragment, String tag) {
        activity.getSupportFragmentManager().beginTransaction().
                replace(containerId, fragment, tag).commitAllowingStateLoss();
    }

    public void setContentFragment(int id) {
        switch (id) {
            case 1:
                loadFragment(this, R.id.vv_fragContainer, frag01, "fragment1");

                break;
            case 2:
                loadFragment(this, R.id.vv_fragContainer, frag02, "fragment2");

                break;
        }
    }

    @Override
    public void cf_firedByFragment(String str, int source) {
        if (source == 1) {
            setContentFragment(2);
            getSupportFragmentManager().executePendingTransactions();

        } else if (source == 2) {
            setContentFragment(1);
            getSupportFragmentManager().executePendingTransactions();

        }
    }

    }

