package com.s23010664.unimate.ui.calender;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import com.s23010664.unimate.ActivityAdapter;
import com.s23010664.unimate.ActivityItem;
import com.s23010664.unimate.DBHelper;
import com.s23010664.unimate.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalenderFragment extends Fragment {

    private DBHelper dbHelper;
    private ListView listView;
    private TextView emptyText;

    public static CalenderFragment newInstance() {
        return new CalenderFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calender, container, false);

        dbHelper = new DBHelper(requireContext());
        listView = view.findViewById(R.id.activityListView);
        emptyText = view.findViewById(R.id.emptyText);
        CalendarView calendarView = view.findViewById(R.id.calendarView);

        // Load today's activities by default
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(Calendar.getInstance().getTime());
        loadActivitiesForDate(todayDate);

        // When the user selects a date, load activities for that date
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                loadActivitiesForDate(selectedDate);
            }
        });

        return view;
    }

    /**
     * Loads all activities for the given date using the custom ActivityAdapter.
     */
    private void loadActivitiesForDate(String date) {
        List<ActivityItem> activities = dbHelper.getActivityItemsByDate(date);

        if (activities.isEmpty()) {
            listView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
            ActivityAdapter adapter = new ActivityAdapter(requireContext(), activities);
            listView.setAdapter(adapter);
        }
    }
}
