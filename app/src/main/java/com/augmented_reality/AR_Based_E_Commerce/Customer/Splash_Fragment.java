package com.augmented_reality.AR_Based_E_Commerce.Customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.augmented_reality.AR_Based_E_Commerce.R;


public class Splash_Fragment extends Fragment {



    public Splash_Fragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_splash_, container, false);
    }
}