package com.s23010664.unimate.ui.userprofile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.s23010664.unimate.R;

public class ProfileFragment extends Fragment {

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        Button actionButton = view.findViewById(R.id.activity_btn);
        actionButton.setOnClickListener(v -> {
            BottomNavigationView navView = requireActivity().findViewById(R.id.bottom_nav);
            navView.setSelectedItemId(R.id.nav_dashboard);
        });

        return view;
    }
}
