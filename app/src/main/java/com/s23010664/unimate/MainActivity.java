package com.s23010664.unimate;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.s23010664.unimate.databinding.ActivityMainBinding;

import android.content.SharedPreferences;

import java.util.concurrent.Executor;

/**
 * MainActivity serves as the main container for the application, handling navigation,
 * sidebar drawers, bottom navigation, accelerometer sensor events, and biometric locks.
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Configuration for the app's top action bar and drawer navigation
    private AppBarConfiguration mAppBarConfiguration;
    // View binding instance to access layout components directly
    private ActivityMainBinding binding;

    // Hardware sensor variables for detecting device flip
    private SensorManager sensorManager;
    private Sensor accelerometer;
    
    // Status flags and prompts for fingerprint / lockscreen biometric authentication
    private boolean biometricPassed = false;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private boolean isLocked = false; // Flag to prevent re-authentication prompt spam

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the sensor services to access the device accelerometer
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Immediately request biometric unlock when the application launches
        showBiometricPrompt();
        
        // Inflate the activity layout using ViewBinding and display it on screen
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Toolbar
        setSupportActionBar(binding.appBarMain.toolbar);

        // Setup Floating Action Button (FAB) click handler to display a fullscreen pop-up action menu
        binding.appBarMain.fab.setOnClickListener(view -> {
            // Inflate the popup overlay layout resource
            View popupView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_menu, null);

            // Instantiate popup window covering the full screen
            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    true
            );

            // Configure style properties for the popup overlay
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, android.R.color.transparent));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setElevation(10);

            // Position and display the window centered on the main container content
            popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

            // Retrieve interactive buttons from inside the popup layout
            Button btnActivity = popupView.findViewById(R.id.addactivity);
            Button btnLost = popupView.findViewById(R.id.addlost);
            Button btnFound = popupView.findViewById(R.id.addfind);
            Button btnIssue = popupView.findViewById(R.id.addissues);

            // Button Click: Navigate to Add Activity page
            btnActivity.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_addactivity);
                popupWindow.dismiss(); // Close popup
            });

            // Button Click: Navigate to Add Lost Item page
            btnLost.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_addlostitem);
                popupWindow.dismiss(); // Close popup
            });

            // Button Click: Navigate to Add Found Item page
            btnFound.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_addfounditem);
                popupWindow.dismiss(); // Close popup
            });

            // Button Click: Show placeholder action message for Issues option
            btnIssue.setOnClickListener(v -> {
                Toast.makeText(MainActivity.this, "Issues Clicked", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            });

            // Dismiss the full popup overlay whenever any empty background area is tapped
            FrameLayout root = popupView.findViewById(R.id.popup_root);
            root.setOnClickListener(v -> popupWindow.dismiss());
        });

        // Navigation Drawer setup
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Define top-level destinations
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard, R.id.nav_activity_list, R.id.nav_calender, R.id.nav_user_profile, R.id.nav_settings, R.id.nav_unimap)
                .setOpenableLayout(drawer)
                .build();

        // Setup NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Handle sidebar Logout menu item click.
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                // Clear session and navigate to WelcomeActivity.
                SharedPreferences prefs = getSharedPreferences("UniMatePrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.remove("userEmail");
                editor.apply();

                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            // For all other items, let NavigationUI handle the navigation.
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                drawer.closeDrawers();
            }
            return handled;
        });

        // Bottom Navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register sensor listener
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister sensor listener to save battery
        sensorManager.unregisterListener(this);
    }

    /**
     * Triggered automatically by the system whenever there is a sensor state update.
     * Evaluates accelerometer gravity values to detect if the device has been turned face-down.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Retrieve vertical accelerometer axis (Z axis) reading
        float z = event.values[2];

        // If Z-value drops below -9.0 (indicating the phone is flipped upside down/face-down)
        // and the device is not already locked, trigger biometric authentication
        if (z < -9.0 && !isLocked) {
            isLocked = true;
            showBiometricPrompt();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    /**
     * Instantiates and triggers the system biometric prompt dialog.
     * Prevents user progression without valid credentials (fingerprint or PIN).
     */
    private void showBiometricPrompt() {
        // Executor running on the main UI thread to run biometric callbacks
        Executor executor = ContextCompat.getMainExecutor(this);

        // Define authenticating prompt setup & listeners
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                isLocked = false; // Successfully authenticated; unlock active interaction
                Toast.makeText(getApplicationContext(), "Unlocked!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                // Rejection of fingerprint scan
                Toast.makeText(getApplicationContext(), "Authentication failed. Try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                // Ignore notifications if error is caused by developer cancellation or normal dismissals
                if (isLocked && errorCode != BiometricPrompt.ERROR_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    Toast.makeText(getApplicationContext(), "Error: " + errString, Toast.LENGTH_SHORT).show();
                }

                // If user dismisses or cancels the lock window, re-prompt to enforce the security lock
                if (isLocked && (errorCode == BiometricPrompt.ERROR_USER_CANCELED
                        || errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON
                        || errorCode == BiometricPrompt.ERROR_CANCELED)) {
                    Toast.makeText(getApplicationContext(), "Authentication required", Toast.LENGTH_SHORT).show();
                    showBiometricPrompt(); // Enforce lock loop
                }
            }
        });

        // Set up descriptive metadata and allowed unlock techniques
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentication Required")
                .setSubtitle("Use fingerprint or device PIN to unlock")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL) // Accept fingerprint, face-unlock, or PIN/pattern
                .build();

        // Launch prompt dialog
        biometricPrompt.authenticate(promptInfo);
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
