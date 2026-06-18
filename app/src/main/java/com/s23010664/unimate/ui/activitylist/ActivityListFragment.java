package com.s23010664.unimate.ui.activitylist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.material.tabs.TabLayout;
import com.s23010664.unimate.ActivityAdapter;
import com.s23010664.unimate.ActivityItem;
import com.s23010664.unimate.DBHelper;
import com.s23010664.unimate.R;

import java.util.List;

public class ActivityListFragment extends Fragment {

    public static ActivityListFragment newInstance() {
        return new ActivityListFragment();
    }

    private DBHelper dbHelper;
    private ListView listView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_activity_list, container, false);

        listView = view.findViewById(R.id.activityListView);
        dbHelper = new DBHelper(requireContext());

        // Load "All" tab data by default
        loadList(dbHelper.getAllActivityItems());

        // Wire tab selection to filter the list
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0: // All
                        loadList(dbHelper.getAllActivityItems());
                        break;
                    case 1: // Lectures
                        loadList(dbHelper.getLectureItems());
                        break;
                    case 2: // Assignments
                        loadList(dbHelper.getAssignmentItems());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    /**
     * Refreshes the ListView with the provided ActivityItem list.
     */
    private void loadList(List<ActivityItem> data) {
        ActivityAdapter adapter = new ActivityAdapter(requireContext(), data);
        listView.setAdapter(adapter);
    }
}
