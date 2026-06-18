package com.s23010664.unimate.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.s23010664.unimate.ActivityAdapter;
import com.s23010664.unimate.ActivityItem;
import com.s23010664.unimate.DBHelper;
import com.s23010664.unimate.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        DBHelper dbHelper = new DBHelper(requireContext());

        // --- Welcome Text: show logged-in user's name ---
        TextView welcomeText = view.findViewById(R.id.welcomeText);
        SharedPreferences prefs = requireActivity().getSharedPreferences("UniMatePrefs", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("userEmail", "");
        if (!userEmail.isEmpty()) {
            android.database.Cursor cursor = dbHelper.getUserByEmail(userEmail);
            if (cursor != null && cursor.moveToFirst()) {
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
                welcomeText.setText("Welcome " + firstName);
                cursor.close();
            }
        }

        // --- Quick Navigation Cards ---
        CardView actionCard = view.findViewById(R.id.map);
        actionCard.setOnClickListener(v -> {
            BottomNavigationView navView = requireActivity().findViewById(R.id.bottom_nav);
            navView.setSelectedItemId(R.id.nav_unimap);
        });

        CardView activitycard = view.findViewById(R.id.activity_card);
        activitycard.setOnClickListener(v -> {
            BottomNavigationView navView = requireActivity().findViewById(R.id.bottom_nav);
            navView.setSelectedItemId(R.id.nav_activity_list);
        });

        CardView lostnfoundCard = view.findViewById(R.id.lostnfound);
        lostnfoundCard.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_lostnfound);
        });

        // --- Update Lost & Found Counts ---
        TextView lostCountText = view.findViewById(R.id.lost_count_text);
        TextView foundCountText = view.findViewById(R.id.found_count_text);
        int lostCount = dbHelper.getLostNFoundItemsCount("lost");
        int foundCount = dbHelper.getLostNFoundItemsCount("found");
        lostCountText.setText(lostCount + " Items in Lost");
        foundCountText.setText(foundCount + " Items in Found");

        // --- Update Activities Count ---
        TextView activityCountText = view.findViewById(R.id.activity_count_text);
        int activityCount = dbHelper.getAllActivityItems().size();
        activityCountText.setText("Activities : " + activityCount);

        // --- Get today's and tomorrow's date strings ---
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        String todayDate = sdf.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String tomorrowDate = sdf.format(calendar.getTime());

        // --- Today Activities Summary Card ---
        int[] todayCounts = dbHelper.getActivityCountsByDate(todayDate);
        TextView todayAssignments = view.findViewById(R.id.todayAssignments);
        TextView todayLectures = view.findViewById(R.id.todayLectures);
        TextView todayEvents = view.findViewById(R.id.todayEvents);
        todayAssignments.setText("Assignments - " + todayCounts[2]);
        todayLectures.setText("Lectures - " + todayCounts[1]);
        todayEvents.setText("Events - " + todayCounts[0]);

        // --- Tomorrow Activities Summary Card ---
        int[] tomorrowCounts = dbHelper.getActivityCountsByDate(tomorrowDate);
        TextView tomorrowAssignmentsView = view.findViewById(R.id.tomorrowAssignments);
        TextView tomorrowLecturesView = view.findViewById(R.id.tomorrowLectures);
        TextView tomorrowEventsView = view.findViewById(R.id.tomorrowEvents);
        tomorrowAssignmentsView.setText("Assignments - " + tomorrowCounts[2]);
        tomorrowLecturesView.setText("Lectures - " + tomorrowCounts[1]);
        tomorrowEventsView.setText("Events - " + tomorrowCounts[0]);

        // --- Today Activities List (Your Reminders section) ---
        // Uses the same card design (activity_list1) as the Activity List page
        ListView listView = view.findViewById(R.id.sampleListView);
        List<ActivityItem> todayActivities = dbHelper.getActivityItemsByDate(todayDate);

        ActivityAdapter adapter = new ActivityAdapter(requireContext(), todayActivities);
        listView.setAdapter(adapter);

        return view;
    }
}
