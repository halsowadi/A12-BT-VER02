package com.example.a12_bt_ver03;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.a12_bt_ver03.databinding.FragmentTwoBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentTwo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTwo extends Fragment {
    FragmentTwoBinding binding;

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


        binding.BackToFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cv_listener.cf_firedByFragment("#FF0000", 2);

            }
        });


        return v;
    }

}
