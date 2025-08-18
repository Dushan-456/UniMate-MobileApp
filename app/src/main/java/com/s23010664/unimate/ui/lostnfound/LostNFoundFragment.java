package com.s23010664.unimate.ui.lostnfound;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;
import com.s23010664.unimate.MainActivity;
import com.s23010664.unimate.R;

public class LostNFoundFragment extends Fragment {


    public static LostNFoundFragment newInstance() {
        return new LostNFoundFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lost_n_found, container, false);


        TabLayout tabLayout = view.findViewById(R.id.lostnfound_tab);
        LinearLayout lostTab = view.findViewById(R.id.lost_tab);
        LinearLayout foundTab = view.findViewById(R.id.found_tab);

        // Add tabs dynamically
        tabLayout.addTab(tabLayout.newTab().setText("Lost Items"));
        tabLayout.addTab(tabLayout.newTab().setText("Found Items"));

        // Set default view
        lostTab.setVisibility(View.VISIBLE);
        foundTab.setVisibility(View.GONE);

        Button add_lost_items = view.findViewById(R.id.add_lost_items);
        add_lost_items.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_addlostitem);

        });

        Button add_found_items = view.findViewById(R.id.add_found_items);
        add_found_items.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_addfounditem);

        });

        // Tab selected listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    lostTab.setVisibility(View.VISIBLE);
                    foundTab.setVisibility(View.GONE);
                } else {
                    lostTab.setVisibility(View.GONE);
                    foundTab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });


        return view;
    }
}
