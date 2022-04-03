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
    String x;
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

                 x =((MainActivity) getActivity()).cpf_checkBTPermissions();
                binding.vvTvOut2.setText(x);



        binding.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv_btDevice =  ((MainActivity) getActivity()).cpf_locateInPairedBTList(CV_ROBOTNAME);
               String x= ((MainActivity) getActivity()).cpf_connectToEV3(cv_btDevice);
               binding.vvTvOut2.setText(x);
            }
        });
        binding.btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv_btDevice =  ((MainActivity) getActivity()).cpf_locateInPairedBTList(CV_ROBOTNAME);
               String x= ((MainActivity) getActivity()).cpf_disconnFromEV3(cv_btDevice);
                binding.vvTvOut1.setText(x);
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
                ((MainActivity) getActivity()).cpf_requestBTPermissions();
                return true;
            case R.id.menu_second:
                //Your code here
                cv_btDevice = ((MainActivity) getActivity()).cpf_locateInPairedBTList(CV_ROBOTNAME);
                return true;
            case R.id.menu_third:
                //Your code here
               x= ((MainActivity) getActivity()).cpf_connectToEV3(cv_btDevice);
               binding.vvTvOut2.setText(x);
                return true;
            case R.id.menu_fourth:

                x=((MainActivity) getActivity()).cpf_EV3MoveMotor((byte)50);
                binding.vvTvOut2.setText(x);
                return true;
            case R.id.menu_fifth:

                ((MainActivity) getActivity()). cpf_EV3PlayTone();
                binding.vvTvOut2.setText(x);

                return true;
            case R.id.menu_sixith:

                x= ((MainActivity) getActivity()).cpf_disconnFromEV3(cv_btDevice);
                binding.vvTvOut2.setText(x);
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }


    // https://www.geeksforgeeks.org/android-how-to-request-permissions-in-android-application/





}
