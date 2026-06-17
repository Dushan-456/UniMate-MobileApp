package com.s23010664.unimate.ui.userprofile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.s23010664.unimate.DBHelper;
import com.s23010664.unimate.R;
import com.s23010664.unimate.WelcomeActivity;

public class ProfileFragment extends Fragment {

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // --- Load user data from database ---
        loadUserProfile(view);

        // Logout icon (top-right CardView)
        CardView logoutCard = view.findViewById(R.id.cardView1);
        logoutCard.setOnClickListener(v -> performLogout());

        // Logout button (bottom of page)
        Button logoutBtn = view.findViewById(R.id.logoutbtn);
        logoutBtn.setOnClickListener(v -> performLogout());

        return view;
    }

    /**
     * Loads the logged-in user's details from the database and populates the profile TextViews.
     */
    private void loadUserProfile(View view) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UniMatePrefs", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("userEmail", "");

        if (userEmail.isEmpty()) return;

        DBHelper dbHelper = new DBHelper(requireContext());
        Cursor cursor = dbHelper.getUserByEmail(userEmail);

        if (cursor != null && cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
            String birthday = cursor.getString(cursor.getColumnIndexOrThrow("birthday"));
            String university = cursor.getString(cursor.getColumnIndexOrThrow("university"));

            String fullName = firstName + " " + lastName;

            // --- Personal Details Card ---
            TextView userName = view.findViewById(R.id.userName);
            TextView profileName = view.findViewById(R.id.profileName);
            TextView profileBirthday = view.findViewById(R.id.profileBirthday);
            TextView profileUniversity = view.findViewById(R.id.profileUniversity);
            TextView profileEmail = view.findViewById(R.id.profileEmail);

            userName.setText(fullName);
            profileName.setText("Name - " + fullName);
            profileBirthday.setText("Birth Day - " + (birthday != null ? birthday : "N/A"));
            profileUniversity.setText(university != null ? university : "N/A");
            profileEmail.setText(email);

            // --- Contact Details Card ---
            TextView contactEmail = view.findViewById(R.id.contactEmail);
            TextView contactUniversity = view.findViewById(R.id.contactUniversity);
            TextView contactGender = view.findViewById(R.id.contactGender);

            contactEmail.setText("Email - \n " + email);
            contactUniversity.setText(university != null ? university : "N/A");
            contactGender.setText("Gender - " + (gender != null ? gender : "N/A"));

            cursor.close();
        }
    }

    /**
     * Clears the login session and navigates back to WelcomeActivity.
     */
    private void performLogout() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UniMatePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("userEmail");
        editor.apply();

        Intent intent = new Intent(requireActivity(), WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
