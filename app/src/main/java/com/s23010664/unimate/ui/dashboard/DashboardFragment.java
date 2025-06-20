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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
            BottomNavigationView navView = requireActivity().findViewById(R.id.bottom_nav);
            navView.setSelectedItemId(R.id.nav_user_profile);
        });

//
//
        ListView listView = view.findViewById(R.id.sampleListView);
//
        String[] sampleData = {
                "John Doe",
                "Jane Smith",
                "Nimal Perera",
                "John Doe",
                "Jane Smith",
                "Nimal Perera"
        };
//
//// Use a simple adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), // Use requireContext() in fragments
                R.layout.activity_list,
                R.id.listtext,
                sampleData
        );

//
//// Attach to ListView
        listView.setAdapter(adapter);
//
//
//
//

        return view;





    }
}
