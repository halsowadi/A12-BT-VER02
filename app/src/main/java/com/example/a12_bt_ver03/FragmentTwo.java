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


        binding.imgStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.imgUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String x=((MainActivity) getActivity()).cpf_EV3MoveMotor((byte) 0x00);
                binding.textView.setText(x);

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


}
