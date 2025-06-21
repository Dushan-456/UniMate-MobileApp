package com.s23010664.unimate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.s23010664.unimate.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewBinding setup
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Toolbar
        setSupportActionBar(binding.appBarMain.toolbar);

        // Setup FAB click
        binding.appBarMain.fab.setOnClickListener(view -> {
            // Inflate with Activity context
            View popupView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_menu, null);

            // Create the popup window
            final PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            // Set background (optional)
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.popup_background));
            popupWindow.setElevation(10);

            // Show near the FAB (view is the FAB here)
            popupWindow.showAsDropDown(view, -450, -550); // Adjust x/y offset

            // Setup popup buttons
            Button btnActivity = popupView.findViewById(R.id.addactivity);
            Button btnLost = popupView.findViewById(R.id.addlost);
            Button btnIssue = popupView.findViewById(R.id.addissues);

            btnActivity.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_addactivity);
                popupWindow.dismiss();
            });

            btnLost.setOnClickListener(v -> {
                Toast.makeText(MainActivity.this, "Lost Clicked", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            });
            btnIssue.setOnClickListener(v -> {
                Toast.makeText(MainActivity.this, "Issues Clicked", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            });
        });




        // Navigation Drawer setup
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Define top-level destinations
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard, R.id.nav_activity_list, R.id.nav_slideshow, R.id.nav_user_profile,R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();

        // Setup NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        // Link ActionBar and Drawer with NavController
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //  Bottom Navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
