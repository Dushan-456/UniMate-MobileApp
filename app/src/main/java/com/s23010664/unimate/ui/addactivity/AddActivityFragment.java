package com.s23010664.unimate.ui.addactivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.s23010664.unimate.DBHelper;
import com.s23010664.unimate.MapPickerActivity;
import com.s23010664.unimate.R;

import java.util.Calendar;
import java.util.Locale;

/**
 * A fragment that provides a user interface for adding different types of activities
 * (Events, Lectures, Assignments) to the database.
 */
public class AddActivityFragment extends Fragment {

    // Database helper instance to interact with the SQLite database.
    private DBHelper dbHelper;

    // --- UI Element Declarations ---
    private EditText eventName, eventOrganizer, eventDate, eventTime, eventLocation;
    private EditText lectureSubject, lectureDate, lectureTime, zoomLink, lectureRoom;
    private EditText assignmentName, assignmentSubject, assignmentDueDate, assignmentDueTime, assignmentLink;
    private Button btnAddEvent, btnAddLecture, btnAddAssignment;
    private RadioGroup radioGroupLecture;

    // --- Map Location Data ---
    // Variables to store the coordinates received from MapPickerActivity.
    private double selectedLatitude = -1;
    private double selectedLongitude = -1;

    // Modern way to handle activity results: a launcher for the map activity.
    private ActivityResultLauncher<Intent> mapLauncher;

    /**
     * Factory method to create a new instance of this fragment.
     */
    public static AddActivityFragment newInstance() {
        return new AddActivityFragment();
    }

    /**
     * Called when the fragment is first created. This is where you should initialize
     * components that you need to retain across pauses and resumes.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the ActivityResultLauncher. This must be done in onCreate() or onAttach().
        // It defines what to do when the MapPickerActivity returns a result.
        mapLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Check if the result is OK (i.e., the user confirmed a location).
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            // Extract the latitude and longitude from the result intent.
                            selectedLatitude = data.getDoubleExtra("latitude", -1);
                            selectedLongitude = data.getDoubleExtra("longitude", -1);

                            // Update the EditText to show the user what was selected.
                            eventLocation.setText(String.format(Locale.getDefault(), "Lat: %.4f, Lng: %.4f", selectedLatitude, selectedLongitude));
                            Toast.makeText(getContext(), "Location selected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_activity, container, false);
        dbHelper = new DBHelper(requireContext());

        // --- View Initialization ---
        LinearLayout layoutEvent = view.findViewById(R.id.layoutEvent);
        LinearLayout layoutLecture = view.findViewById(R.id.layoutLecture);
        LinearLayout layoutAssignment = view.findViewById(R.id.layoutAssignment);

        Spinner spinner = view.findViewById(R.id.spinnerActivityType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item,
                new String[]{"Select Type", "Event", "Lecture", "Assignment"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        eventName = view.findViewById(R.id.editTextEventName);
        eventOrganizer = view.findViewById(R.id.editTextEventOrganizer);
        eventDate = view.findViewById(R.id.editTextEventDate);
        eventTime = view.findViewById(R.id.editTextEventTime);
        eventLocation = view.findViewById(R.id.editTextEventLocation);
        btnAddEvent = view.findViewById(R.id.add_new_event);

        lectureSubject = view.findViewById(R.id.editTextLectureSubject);
        lectureDate = view.findViewById(R.id.editTextLectureDate);
        lectureTime = view.findViewById(R.id.editTextLectureTime);
        radioGroupLecture = view.findViewById(R.id.radioGroupLectureType);
        zoomLink = view.findViewById(R.id.editTextZoomLink);
        lectureRoom = view.findViewById(R.id.lectureroom);
        btnAddLecture = view.findViewById(R.id.add_new_lecture);

        assignmentName = view.findViewById(R.id.editTextAssignmentName);
        assignmentSubject = view.findViewById(R.id.editTextAssignmentSubject);
        assignmentDueDate = view.findViewById(R.id.editTextAssignmentDueDate);
        assignmentDueTime = view.findViewById(R.id.editTextAssignmentDueTime);
        assignmentLink = view.findViewById(R.id.editTextAssignmentLink);
        btnAddAssignment = view.findViewById(R.id.add_new_assignment);

        // --- UI Logic Setup ---

        // Make the location EditText behave like a button to launch the map.
        eventLocation.setFocusable(false);
        eventLocation.setClickable(true);
        eventLocation.setOnClickListener(v -> {
            // Create an intent to start the MapPickerActivity.
            Intent intent = new Intent(getContext(), MapPickerActivity.class);
            // Launch the activity using the launcher we defined in onCreate().
            mapLauncher.launch(intent);
        });

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

        radioGroupLecture.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioOnline) {
                zoomLink.setVisibility(View.VISIBLE);
                lectureRoom.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioPhysical) {
                zoomLink.setVisibility(View.GONE);
                lectureRoom.setVisibility(View.VISIBLE);
            }
        });

        setupDatePickerListeners();
        setupButtonClickListeners();

        return view;
    }

    private void setupDatePickerListeners() {
        eventDate.setOnClickListener(v -> showDatePickerDialog(eventDate));
        eventTime.setOnClickListener(v -> showTimePickerDialog(eventTime));
        lectureDate.setOnClickListener(v -> showDatePickerDialog(lectureDate));
        lectureTime.setOnClickListener(v -> showTimePickerDialog(lectureTime));
        assignmentDueDate.setOnClickListener(v -> showDatePickerDialog(assignmentDueDate));
        assignmentDueTime.setOnClickListener(v -> showTimePickerDialog(assignmentDueTime));
    }

    private void setupButtonClickListeners() {
        btnAddEvent.setOnClickListener(v -> {
            String name = eventName.getText().toString().trim();
            String organizer = eventOrganizer.getText().toString().trim();
            String date = eventDate.getText().toString().trim();
            String time = eventTime.getText().toString().trim();
            String location = eventLocation.getText().toString().trim();

            if (name.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(getContext(), "Event name, date, and time are required", Toast.LENGTH_SHORT).show();
            } else {
                // Pass all required data, including coordinates, to the insert method.
                boolean success = dbHelper.insertEvent(name, organizer, date, time, location, selectedLatitude, selectedLongitude);
                Toast.makeText(getContext(), success ? "Event added" : "Failed to add event", Toast.LENGTH_SHORT).show();

                // If insertion was successful, clear all fields and reset coordinates.
                if (success) {
                    eventName.setText("");
                    eventOrganizer.setText("");
                    eventDate.setText("");
                    eventTime.setText("");
                    eventLocation.setText("");
                    selectedLatitude = -1;
                    selectedLongitude = -1;
                }
            }
        });

        btnAddLecture.setOnClickListener(v -> {
            String subject = lectureSubject.getText().toString().trim();
            String date = lectureDate.getText().toString().trim();
            String time = lectureTime.getText().toString().trim();
            String type = radioGroupLecture.getCheckedRadioButtonId() == R.id.radioOnline ? "Online" : "Physical";
            String link = zoomLink.getText().toString().trim();
            String room = lectureRoom.getText().toString().trim();

            if (subject.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(getContext(), "Lecture subject, date, and time are required", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = dbHelper.insertLecture(subject, date, time, type, link, room);
                Toast.makeText(getContext(), success ? "Lecture added" : "Failed to add lecture", Toast.LENGTH_SHORT).show();
                if (success) {
                    lectureSubject.setText("");
                    lectureDate.setText("");
                    lectureTime.setText("");
                    zoomLink.setText("");
                    lectureRoom.setText("");
                    radioGroupLecture.clearCheck();
                    zoomLink.setVisibility(View.GONE);
                    lectureRoom.setVisibility(View.GONE);
                }
            }
        });

        btnAddAssignment.setOnClickListener(v -> {
            String name = assignmentName.getText().toString().trim();
            String subject = assignmentSubject.getText().toString().trim();
            String date = assignmentDueDate.getText().toString().trim();
            String time = assignmentDueTime.getText().toString().trim();
            String link = assignmentLink.getText().toString().trim();

            if (name.isEmpty() || subject.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(getContext(), "Assignment name, subject, due date, and time are required", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = dbHelper.insertAssignment(name, subject, date, time, link);
                Toast.makeText(getContext(), success ? "Assignment added" : "Failed to add assignment", Toast.LENGTH_SHORT).show();
                if (success) {
                    assignmentName.setText("");
                    assignmentSubject.setText("");
                    assignmentDueDate.setText("");
                    assignmentDueTime.setText("");
                    assignmentLink.setText("");
                }
            }
        });
    }

    private void showDatePickerDialog(final EditText dateEditText) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String formattedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    dateEditText.setText(formattedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog(final EditText timeEditText) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    timeEditText.setText(formattedTime);
                }, hour, minute, true);
        timePickerDialog.show();
    }
}