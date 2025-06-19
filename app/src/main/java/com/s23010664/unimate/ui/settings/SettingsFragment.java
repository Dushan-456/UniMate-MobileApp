package com.s23010664.unimate.ui.settings;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.s23010664.unimate.LoginActivity;
import com.s23010664.unimate.R;
import com.s23010664.unimate.RegisterActivity;
import com.s23010664.unimate.WelcomeActivity;
import com.s23010664.unimate.ui.userprofile.ProfileFragment;

public class SettingsFragment extends Fragment {


    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Find the button and set click listener
        Button login = view.findViewById(R.id.login);
        Button register = view.findViewById(R.id.register);
        Button welcome = view.findViewById(R.id.welcome);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginIntent);

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(registerIntent);

            }
        });
        welcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent welcomeIntent = new Intent(getActivity(), WelcomeActivity.class);
                startActivity(welcomeIntent);

            }
        });

        return view;
    }

}