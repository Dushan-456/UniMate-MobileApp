package com.s23010664.unimate.ui.addactivity;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.s23010664.unimate.R;

public class AddActivityFragment extends Fragment {


    public static AddActivityFragment newInstance() {
        return new AddActivityFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_activity, container, false);


        Spinner spinner = view.findViewById(R.id.spinnerActivityType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item,
                new String[]{"Select Type", "Event", "Lecture", "Assignment"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

// Layouts
        LinearLayout layoutEvent = view.findViewById(R.id.layoutEvent);
        LinearLayout layoutLecture = view.findViewById(R.id.layoutLecture);
        LinearLayout layoutAssignment = view.findViewById(R.id.layoutAssignment);
        EditText zoomLink = view.findViewById(R.id.editTextZoomLink);
        EditText lectureroom = view.findViewById(R.id.lectureroom);
        RadioGroup radioGroupLecture = view.findViewById(R.id.radioGroupLectureType);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                layoutEvent.setVisibility(View.GONE);
                layoutLecture.setVisibility(View.GONE);
                layoutAssignment.setVisibility(View.GONE);

                if (position == 1) layoutEvent.setVisibility(View.VISIBLE);
                else if (position == 2) layoutLecture.setVisibility(View.VISIBLE);
                else if (position == 3) layoutAssignment.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

// Show Zoom link if "Online" selected
        radioGroupLecture.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioOnline) {
                zoomLink.setVisibility(View.VISIBLE);
                lectureroom.setVisibility(View.GONE);

            } else if (checkedId == R.id.radioPhysical) {
                lectureroom.setVisibility(View.VISIBLE);
                zoomLink.setVisibility(View.GONE);
            }


        });



        return view;
    }
}
