package com.s23010664.unimate;

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

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean biometricPassed = false;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private boolean isLocked = false; // Flag to prevent re-authentication spam

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup sensor manager and accelerometer
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Prepare biometric prompt
//        setupBiometricPrompt();

        // Show on app start
     //   showBiometricPrompt();
        // ViewBinding setup
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Toolbar
        setSupportActionBar(binding.appBarMain.toolbar);

        // Setup Floating Action Button (FAB) click to show popup menu
        binding.appBarMain.fab.setOnClickListener(view -> {
            View popupView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_menu, null);

            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    true
            );

            // Optional: set transparent background and outside touch
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, android.R.color.transparent));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setElevation(10);

            // Show popup in center of screen
            popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

            // Handle button actions inside popup
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

            // Dismiss popup when background is touched
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

    // Detect device flip using accelerometer
    @Override
    public void onSensorChanged(SensorEvent event) {
        float z = event.values[2];

        // If flipped upside down and not already locked, trigger biometric auth
        if (z < -9.0 && !isLocked) {
            isLocked = true;
            showBiometricPrompt();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    // Show fingerprint or device credential prompt
    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                isLocked = false; // Unlock
                Toast.makeText(getApplicationContext(), "Unlocked!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                // Still locked
                Toast.makeText(getApplicationContext(), "Authentication failed. Try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                // Prevent user from skipping authentication by re-showing the prompt
                if (isLocked && errorCode != BiometricPrompt.ERROR_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    Toast.makeText(getApplicationContext(), "Error: " + errString, Toast.LENGTH_SHORT).show();
                }

                // Force re-authentication if user cancels or closes the prompt
                if (isLocked && (errorCode == BiometricPrompt.ERROR_USER_CANCELED
                        || errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON
                        || errorCode == BiometricPrompt.ERROR_CANCELED)) {
                    Toast.makeText(getApplicationContext(), "Authentication required", Toast.LENGTH_SHORT).show();
                    showBiometricPrompt(); // Retry
                }
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentication Required")
                .setSubtitle("Use fingerprint or device PIN to unlock")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL) // Fingerprint or device PIN
                .build();

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
