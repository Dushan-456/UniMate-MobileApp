package com.s23010664.unimate.ui.addactivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.s23010664.unimate.DBHelper;
import com.s23010664.unimate.R;

public class AddActivityFragment extends Fragment {

    private DBHelper dbHelper;

    public static AddActivityFragment newInstance() {
        return new AddActivityFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_activity, container, false);

        dbHelper = new DBHelper(requireContext());

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
        EditText lectureRoom = view.findViewById(R.id.lectureroom);
        RadioGroup radioGroupLecture = view.findViewById(R.id.radioGroupLectureType);

        // Spinner Logic
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

        // Lecture Type Toggle
        radioGroupLecture.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioOnline) {
                zoomLink.setVisibility(View.VISIBLE);
                lectureRoom.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioPhysical) {
                zoomLink.setVisibility(View.GONE);
                lectureRoom.setVisibility(View.VISIBLE);
            }
        });

        // Event Fields
        EditText eventName = view.findViewById(R.id.layoutEvent).findViewById(R.id.editTextEventName);
        EditText eventOrganizer = view.findViewById(R.id.layoutEvent).findViewById(R.id.editTextEventOrganizer);
        EditText eventDate = view.findViewById(R.id.layoutEvent).findViewById(R.id.editTextEventDate);
        EditText eventTime = view.findViewById(R.id.layoutEvent).findViewById(R.id.editTextEventTime);
        EditText eventLocation = view.findViewById(R.id.layoutEvent).findViewById(R.id.editTextEventLocation);
        Button btnAddEvent = view.findViewById(R.id.add_new_event);

        btnAddEvent.setOnClickListener(v -> {
            String name = eventName.getText().toString().trim();
            String organizer = eventOrganizer.getText().toString().trim();
            String date = eventDate.getText().toString().trim();
            String time = eventTime.getText().toString().trim();
            String location = eventLocation.getText().toString().trim();

            if (name.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(getContext(), "Event name, date, and time are required", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = dbHelper.insertEvent( name, organizer, date, time, location);
                Toast.makeText(getContext(), success ? "Event added" : "Failed to add", Toast.LENGTH_SHORT).show();
                eventName.setText("");
                eventOrganizer.setText("");
                eventDate.setText("");
                eventTime.setText("");
                eventLocation.setText("");
            }
        });

        // Lecture Fields
        EditText lectureSubject = view.findViewById(R.id.layoutLecture).findViewById(R.id.editTextLectureSubject);
        EditText lectureDate = view.findViewById(R.id.layoutLecture).findViewById(R.id.editTextLectureDate);
        EditText lectureTime = view.findViewById(R.id.layoutLecture).findViewById(R.id.editTextLectureTime);
        Button btnAddLecture = view.findViewById(R.id.add_new_lecture);

        btnAddLecture.setOnClickListener(v -> {
            String subject = lectureSubject.getText().toString().trim();
            String date = lectureDate.getText().toString().trim();
            String time = lectureTime.getText().toString().trim();
            String type = radioGroupLecture.getCheckedRadioButtonId() == R.id.radioOnline ? "Online" : "Physical";
            String Link = zoomLink.getText().toString().trim() ;
            String Room = lectureRoom.getText().toString().trim();

            if (subject.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(getContext(), "Lecture subject, date, and time are required", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = dbHelper.insertLecture(subject, date, time, type, Link, Room);
                Toast.makeText(getContext(), success ? "Lecture added" : "Failed to add", Toast.LENGTH_SHORT).show();
                lectureSubject.setText("");
                lectureDate.setText("");
                lectureTime.setText("");
                zoomLink.setText("");
                lectureRoom.setText("");
                radioGroupLecture.clearCheck();
                zoomLink.setVisibility(View.GONE);
                lectureRoom.setVisibility(View.GONE);
            }
        });

        // Assignment Fields
        EditText assignmentName = view.findViewById(R.id.layoutAssignment).findViewById(R.id.editTextAssignmentName);
        EditText assignmentSubject = view.findViewById(R.id.layoutAssignment).findViewById(R.id.editTextAssignmentSubject);
        EditText assignmentDueDate = view.findViewById(R.id.layoutAssignment).findViewById(R.id.editTextAssignmentDueDate);
        EditText assignmentDueTime = view.findViewById(R.id.layoutAssignment).findViewById(R.id.editTextAssignmentDueTime);
        EditText assignmentLink = view.findViewById(R.id.layoutAssignment).findViewById(R.id.editTextAssignmentLink);
        Button btnAddAssignment = view.findViewById(R.id.add_new_assignment);

        btnAddAssignment.setOnClickListener(v -> {
            String name = assignmentName.getText().toString().trim();
            String subject = assignmentSubject.getText().toString().trim();
            String date = assignmentDueDate.getText().toString().trim();
            String time = assignmentDueTime.getText().toString().trim();
            String link = assignmentLink.getText().toString().trim();

            if (name.isEmpty() || subject.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(getContext(), "Assignment name, subject, due date, and time are required", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = dbHelper.insertAssignment( name, subject, date, time, link);
                Toast.makeText(getContext(), success ? "Assignment added" : "Failed to add", Toast.LENGTH_SHORT).show();
                assignmentName.setText("");
                assignmentSubject.setText("");
                assignmentDueDate.setText("");
                assignmentDueTime.setText("");
                assignmentLink.setText("");
            }
        });

        return view;
    }
}
