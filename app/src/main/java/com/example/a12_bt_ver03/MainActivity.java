package com.example.a12_bt_ver03;
/*
EV3 Bluetooth Ver 01
Note Move left, Move right non functioning
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
import android.widget.EditText;
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
    EditText editText;
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
    public  String cpf_checkBTPermissions() {
        String x;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            x= ("BLUETOOTH_SCAN already granted.\n");
            // myString = "BLUETOOTH_SCAN already granted.\n";
        } else {
            x =("BLUETOOTH_SCAN NOT granted.\n");
            //myString=  "BLUETOOTH_SCAN NOT granted.\n";
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            x=("BLUETOOTH_CONNECT NOT granted.\n");
            // myString ="BLUETOOTH_CONNECT NOT granted.\n";
        } else {
            x=("BLUETOOTH_CONNECT already granted.\n");
            // myString = "BLUETOOTH_CONNECT already granted.\n";
        }
        return x;
    }
    public void cpf_requestBTPermissions() {
        // We can give any value but unique for each permission.
        final int BLUETOOTH_SCAN_CODE = 100;
        final int BLUETOOTH_CONNECT_CODE = 101;
        String x;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
                    BLUETOOTH_SCAN_CODE);
        } else {
            Toast.makeText(this,
                    "BLUETOOTH_SCAN already granted", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    BLUETOOTH_CONNECT_CODE);
        } else {
            Toast.makeText(this,
                    "BLUETOOTH_CONNECT already granted", Toast.LENGTH_SHORT).show();
        }

    }
    // Modify from chap14, pp390 findRobot()
    public BluetoothDevice cpf_locateInPairedBTList(String name) {
        BluetoothDevice lv_bd = null;
        try {
            cv_btInterface = BluetoothAdapter.getDefaultAdapter();
            cv_pairedDevices = cv_btInterface.getBondedDevices();
            Iterator<BluetoothDevice> lv_it = cv_pairedDevices.iterator();
            while (lv_it.hasNext()) {
                lv_bd = lv_it.next();
                if (lv_bd.getName().equalsIgnoreCase(name)) {

                    //binding.vvTvOut1.setText(name + " is in paired list");
                    return lv_bd;
                }
            }
            // binding.vvTvOut1.setText(name + " is NOT in paired list");
            myString = name + " is NOT in paired list";
        } catch (Exception e) {
            //binding.vvTvOut1.setText("Failed in findRobot() " + e.getMessage());
            myString="Failed in findRobot() " + e.getMessage();
        }
        return null;
    }
    // Modify frmo chap14, pp391 connectToRobot()
    public String cpf_connectToEV3(BluetoothDevice bd) {
        String x;
        try {
            //cv_btSocket = bd.createRfcommSocketToServiceRecord
            cv_btSocket = bd.createInsecureRfcommSocketToServiceRecord
                    (CONNECTION_UUID);
            //(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

            cv_btSocket.connect();
            cv_is = cv_btSocket.getInputStream();
            cv_os = cv_btSocket.getOutputStream();

            x=("Connect to " + bd.getName() + " at " + bd.getAddress());

        } catch (Exception e) {
            x=("Error interacting with remote device [" +e.getMessage() + "]");

        }
        return x;
    }



    public String cpf_disconnFromEV3(BluetoothDevice bd) {
        String x;
        try {
            cv_btSocket.close();
            cv_is.close();
            cv_os.close();
            x=(bd.getName() + " is disconnect ");

        } catch (Exception e) {
            x=("Error in disconnect -> " + e.getMessage());

        }
        return x;
    }



    public String cpf_EV3MoveMotor(Byte action) {
        String x="Sucess!";
        try {
            byte[] buffer = new byte[20];       // 0x12 command length

//            //12 00xxxx80 00 00 AE 00 06 81 32 00 82 84 03 82 B4 00 01
//            //0F 00xxxx80 00 00 94 01 81 02 82 E8 03 82 E8 03
            buffer[0] = (byte) (20 - 2);
            buffer[1] = 0;

            buffer[2] = 34;
            buffer[3] = 12;

            buffer[4] = (byte) 0x80;

            buffer[5] = 0;
            buffer[6] = 0;

            buffer[7] = (byte) 0xae;
            buffer[8] = 0;

            buffer[9] = (byte) 0x06;

            buffer[10] = (byte) 129;
            buffer[11] = (byte) action;

            buffer[12] = 0;

            buffer[13] = (byte) -126;//does nothing
            buffer[14] = (byte) -126;//does nothing
            buffer[15] = (byte) 10;

            buffer[16] = (byte) 0x82;
            buffer[17] = (byte) 0xB4;
            buffer[18] = (byte) 0x00;

            buffer[19] = 0;


            cv_os.write(buffer);
            cv_os.flush();
        } catch (Exception e) {
            x= ("Error in MoveForward(" + e.getMessage() + ")");

        }
        return x;
    }

    public String stop(Byte stop) {
      String x="Sucess!";
        try {
            byte[] buffer = new byte[20];       // 0x12 command length

//            //12 00xxxx80 00 00 AE 00 06 81 32 00 82 84 03 82 B4 00 01
//            //0F 00xxxx80 00 00 94 01 81 02 82 E8 03 82 E8 03
            buffer[0] = (byte) (20 - 2);
            buffer[1] = 0;

            buffer[2] = 34;
            buffer[3] = 12;

            buffer[4] = (byte) 0x80;

            buffer[5] = 0;
            buffer[6] = 0;

            buffer[7] = (byte) 0xae;
            buffer[8] = 0;

            buffer[9] = (byte) 0x06;

            buffer[10] = (byte) 129;
            buffer[11] = (byte) 0;

            buffer[12] = 0;

            buffer[13] = (byte) 0x82;
            buffer[14] = (byte) 0x84;
            buffer[15] = (byte) 0x03;

            buffer[16] = (byte) 0x82;
            buffer[17] = (byte) 0xB4;
            buffer[18] = (byte) 0x10;


            buffer[19] = -1;

            cv_os.write(buffer);
            cv_os.flush();
        } catch (Exception e) {
            x= ("Error in MoveForward(" + e.getMessage() + ")");

        }
        return x;
    }




    // 4.2.5 Play a 1Kz tone at level 2 for 1 sec.
    public String cpf_EV3PlayTone() {
        String x ="sucess";
        try {

            byte[] buffer = new byte[17];       // 0x12 command length

            buffer[0] = (byte) (17 - 2);
            buffer[1] = 0;

            buffer[2] = 34;
            buffer[3] = 12;

            buffer[4] = (byte) 0x80;

//            buffer[5] = 0;
//            buffer[6] = 0;
//            //12 00xxxx80 00 00 AE 00 06 81 32 00 82 84 03 82 B4 00 01
//            //0F 00xxxx80 00 00 94 01 81 02 82 E8 03 82 E8 03
            buffer[5] = 0;
            buffer[6] = 0;

            buffer[7] = (byte) 0x94;
            buffer[8] = 1;

            buffer[9] = (byte) 0x81;
            buffer[10] = 2;

            buffer[11] = (byte) 0x82;
            buffer[12] = (byte) 0xe8;
            buffer[13] = 3;

            buffer[14] = (byte) 0x82;
            buffer[15] = (byte) 0xe8;
            buffer[16] = 3;

            cv_os.write(buffer);
            cv_os.flush();
        } catch (Exception e) {
            x=("Error in EV3PlayTone(" + e.getMessage() + ")");

        }
        return x;
    }
    //Overriding onOptionsItemSelected to perform event on menu items
    //Setting Menu text when a option is selected
    }

