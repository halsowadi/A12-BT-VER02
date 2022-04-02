package com.example.a12_bt_ver03;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.example.a12_bt_ver03.databinding.FragmentOneBinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentOne# newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentOne extends Fragment {
    TextView cv_tv;
    private MyFragmentDataPassListener cv_listener;
    FragmentOneBinding binding;
    MainActivity activity;
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




    String myDataFromActivity;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MyFragmentDataPassListener) {
            cv_listener = (MyFragmentDataPassListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must MyFragmentDataPassListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOneBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        setHasOptionsMenu(true);
        cpf_checkBTPermissions();


        binding.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv_btDevice = cpf_locateInPairedBTList(CV_ROBOTNAME);
                cpf_connectToEV3(cv_btDevice);
            }
        });



        binding.imgStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.imgUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        binding.imgDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        binding.imgLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        binding.imgRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        return v;


    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Inflating menu by overriding inflate() method of MenuInflater class.
        //Inflating here means parsing layout XML to views.
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        ////Toast.makeText(this, "You chose : " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
        switch (menuItem.getItemId()) {
            case R.id.menu_first:

                //Your code here
                cpf_requestBTPermissions();
                return true;
            case R.id.menu_second:
                //Your code here
                cv_btDevice = cpf_locateInPairedBTList(CV_ROBOTNAME);
                return true;
            case R.id.menu_third:
                //Your code here
                cpf_connectToEV3(cv_btDevice);
                return true;
            case R.id.menu_fourth:
                cpf_EV3MoveMotor((byte) 0x00);
                return true;
            case R.id.menu_fifth:
                cpf_EV3PlayTone();
                return true;
            case R.id.menu_sixith:
                cpf_disconnFromEV3(cv_btDevice);
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void cpf_checkBTPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            binding.vvTvOut1.setText("BLUETOOTH_SCAN already granted.\n");
           // myString = "BLUETOOTH_SCAN already granted.\n";
        } else {
            binding.vvTvOut1.setText("BLUETOOTH_SCAN NOT granted.\n");
            //myString=  "BLUETOOTH_SCAN NOT granted.\n";
        }
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
             binding.vvTvOut2.setText("BLUETOOTH_CONNECT NOT granted.\n");
           // myString ="BLUETOOTH_CONNECT NOT granted.\n";
        } else {
            binding.vvTvOut2.setText("BLUETOOTH_CONNECT already granted.\n");
           // myString = "BLUETOOTH_CONNECT already granted.\n";
        }
    }
    // https://www.geeksforgeeks.org/android-how-to-request-permissions-in-android-application/
    public void cpf_requestBTPermissions() {
        // We can give any value but unique for each permission.
        final int BLUETOOTH_SCAN_CODE = 100;
        final int BLUETOOTH_CONNECT_CODE = 101;

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
                    BLUETOOTH_SCAN_CODE);
        } else {
            Toast.makeText(getActivity(),
                    "BLUETOOTH_SCAN already granted", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    BLUETOOTH_CONNECT_CODE);
        } else {
            Toast.makeText(getActivity(),
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
    public void cpf_connectToEV3(BluetoothDevice bd) {
        try {
            //cv_btSocket = bd.createRfcommSocketToServiceRecord
            cv_btSocket = bd.createInsecureRfcommSocketToServiceRecord
                    (CONNECTION_UUID);
            //(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

            cv_btSocket.connect();
            cv_is = cv_btSocket.getInputStream();
            cv_os = cv_btSocket.getOutputStream();

              binding.vvTvOut2.setText("Connect to " + bd.getName() + " at " + bd.getAddress());

        } catch (Exception e) {
            binding.vvTvOut2.setText("Error interacting with remote device [" +e.getMessage() + "]");

        }
    }

    public void cpf_disconnFromEV3(BluetoothDevice bd) {
        try {
            cv_btSocket.close();
            cv_is.close();
            cv_os.close();
            binding.vvTvOut2.setText(bd.getName() + " is disconnect ");

        } catch (Exception e) {
            binding.vvTvOut2.setText("Error in disconnect -> " + e.getMessage());

        }
    }

    // Communication Developer Kit Page 27
    // 4.2.2 Start motor B & C forward at power 50 for 3 rotation and braking at destination
    public void cpf_EV3MoveMotor(Byte stop) {
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

            buffer[10] = (byte) 0x81;
            buffer[11] = (byte) 0x32;

            buffer[12] = 0;

            buffer[13] = (byte) 0x82;
            buffer[14] = (byte) 0x84;
            buffer[15] = (byte) 0x03;

            buffer[16] = (byte) 0x82;
            buffer[17] = (byte) 0xB4;
            buffer[18] = (byte) 0x00;

            buffer[19] = 1;

            cv_os.write(buffer);
            cv_os.flush();
        } catch (Exception e) {
            binding.vvTvOut1.setText("Error in MoveForward(" + e.getMessage() + ")");

        }
    }

    public void stop(Byte stop) {
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

            buffer[7] = (byte) 0;
            buffer[8] = 0;

            buffer[9] = (byte) 0;

            buffer[10] = (byte) 0;
            buffer[11] = (byte) 0;

            buffer[12] = 0;

            buffer[13] = (byte) 0;
            buffer[14] = (byte) 0;
            buffer[15] = (byte) 0;

            buffer[16] = (byte) 0;
            buffer[17] = (byte) 0;
            buffer[18] = (byte) 0;

            buffer[19] = 0;

            cv_os.write(buffer);
            cv_os.flush();
        } catch (Exception e) {
            binding.vvTvOut1.setText("Error in MoveForward(" + e.getMessage() + ")");

        }
    }

//    public void cpf_EV3MoveMotor(Byte stop) {
//        try {
//            byte[] buffer = new byte[23];       // 0x12 command length
//
////            //12 00xxxx80 00 00 AE 00 06 81 32 00 82 84 03 82 B4 00 01
////            //0F 00xxxx80 00 00 94 01 81 02 82 E8 03 82 E8 03
//            buffer[0] = 21;
//            buffer[1] = 0;
//
//            buffer[2] = 0;
//            buffer[3] = 12;
//
//            buffer[4] = (byte) 0x80;
//
//            buffer[5] = 0;
//            buffer[6] = 0;
//
//            buffer[7] = (byte) 0xae;
//            buffer[8] = 0;
//
//            buffer[9] = (byte) 0x06;
//
//            buffer[10] = (byte) 0x81;
//            buffer[11] = (byte) 0x32;
//
//            buffer[12] = 0;
//
//            buffer[13] = (byte) 0x82;
//            buffer[14] = (byte) 0x84;
//            buffer[15] = (byte) 0x03;
//
//            buffer[16] = (byte) 0x82;
//            buffer[17] = (byte) 0xB4;
//            buffer[18] = (byte) 0x00;
//
//            buffer[19] = 1;
//
//            cv_os.write(buffer);
//            cv_os.flush();
//        } catch (Exception e) {
//            binding.vvTvOut1.setText("Error in MoveForward(" + e.getMessage() + ")");
//
//        }
//    }


    // 4.2.5 Play a 1Kz tone at level 2 for 1 sec.
    public void cpf_EV3PlayTone() {
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
             binding.vvTvOut1.setText("Error in EV3PlayTone(" + e.getMessage() + ")");

        }
    }
    //Overriding onOptionsItemSelected to perform event on menu items
    //Setting Menu text when a option is selected


}
