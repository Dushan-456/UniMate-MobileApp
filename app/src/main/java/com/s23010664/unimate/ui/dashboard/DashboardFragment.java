package com.s23010664.unimate.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.s23010664.unimate.R;

public class DashboardFragment extends Fragment {

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Find the CardView by ID and set click listener
        CardView actionCard = view.findViewById(R.id.action);
        actionCard.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(DashboardFragment.this);
            navController.navigate(R.id.nav_user_profile); // Navigate to ProfileFragment
        });

        return view;
    }
}
