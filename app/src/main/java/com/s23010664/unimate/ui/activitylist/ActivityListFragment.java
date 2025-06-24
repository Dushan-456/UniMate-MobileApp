package com.s23010664.unimate.ui.activitylist;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.s23010664.unimate.R;

public class ActivityListFragment extends Fragment {


    public static ActivityListFragment newInstance() {
        return new ActivityListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_activity_list, container, false);


//
        ListView listView = view.findViewById(R.id.activityListView);
//
        String[] sampleData = {
                "Online Lecture",
                "Assignment Submission",
                "Lab Test",
                "CAT1 Exam",
                "SESOC Event",
                "Online Lecture",
                "Assignment Submission",
                "Lab Test",
                "CAT1 Exam",
                "SESOC Event", "Online Lecture",
                "Assignment Submission",
                "Lab Test",
                "CAT1 Exam",
                "SESOC Event"
        };
//
//// Use a simple adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), // Use requireContext() in fragments
                R.layout.activity_list1,
                R.id.activity_text,
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
