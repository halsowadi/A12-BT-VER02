package com.example.a12_bt_ver03;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.a12_bt_ver03.databinding.FragmentTwoBinding;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentTwo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTwo extends Fragment {
    FragmentTwoBinding binding;
    private InputStream cv_is = null;
    private OutputStream cv_os = null;
    private MyFragmentDataPassListener cv_listener;

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

        binding = FragmentTwoBinding.inflate(inflater, container, false);
        View v = binding.getRoot();


        binding.ForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cpf_EV3MoveMotor();

            }
        });


        return v;
    }
    public void cpf_EV3MoveMotor() {
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
            binding.textView.setText("Error in MoveForward(" + e.getMessage() + ")");

        }
    }

}
