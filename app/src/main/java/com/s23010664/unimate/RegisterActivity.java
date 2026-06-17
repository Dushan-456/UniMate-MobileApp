package com.s23010664.unimate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailField, firstNameField, lastNameField, mobileField, dobField, passwordField, confirmPasswordField;
    private Spinner spinnerUniversity;
    private RadioGroup radioGroupGender;
    private CheckBox cbTerms;
    private Button btnSignUp;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DBHelper(this);

        // --- Bind UI Elements ---
        emailField = findViewById(R.id.email);
        firstNameField = findViewById(R.id.first_name);
        lastNameField = findViewById(R.id.last_name);
        mobileField = findViewById(R.id.mobile_number);
        dobField = findViewById(R.id.dob);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.confirm_password);
        spinnerUniversity = findViewById(R.id.spinnerUniversity);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        cbTerms = findViewById(R.id.cbTerms);
        btnSignUp = findViewById(R.id.btnSignUp);

        // --- Navigate to Login ---
        TextView login_btn = findViewById(R.id.toLogin);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        // --- University Spinner Setup ---
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{
                        "",
                        "University of Colombo",
                        "University of Peradeniya",
                        "University of Sri Jayewardenepura",
                        "University of Kelaniya",
                        "University of Moratuwa",
                        "University of Jaffna",
                        "University of Ruhuna",
                        "Eastern University, Sri Lanka",
                        "South Eastern University of Sri Lanka",
                        "Rajarata University of Sri Lanka",
                        "Sabaragamuwa University of Sri Lanka",
                        "Wayamba University of Sri Lanka",
                        "University of the Visual & Performing Arts",
                        "Uva Wellassa University",
                        "University of Vavuniya",
                        "The Open University of Sri Lanka",
                        "General Sir John Kotelawala Defence University"
                });
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUniversity.setAdapter(adapter);

        // --- Sign Up Button ---
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    /**
     * Validates all form fields and registers the user via DBHelper.
     */
    private void registerUser() {
        String email = emailField.getText().toString().trim();
        String firstName = firstNameField.getText().toString().trim();
        String lastName = lastNameField.getText().toString().trim();
        String mobile = mobileField.getText().toString().trim();
        String dob = dobField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();
        String university = spinnerUniversity.getSelectedItem().toString();

        // --- Validation ---
        if (email.isEmpty()) {
            emailField.setError("Email is required");
            emailField.requestFocus();
            return;
        }
        if (firstName.isEmpty()) {
            firstNameField.setError("First name is required");
            firstNameField.requestFocus();
            return;
        }
        if (lastName.isEmpty()) {
            lastNameField.setError("Last name is required");
            lastNameField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordField.setError("Password is required");
            passwordField.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passwordField.setError("Password must be at least 6 characters");
            passwordField.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordField.setError("Passwords do not match");
            confirmPasswordField.requestFocus();
            return;
        }
        if (university.isEmpty()) {
            Toast.makeText(this, "Please select a university", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Determine Gender ---
        String gender = "Not specified";
        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedGenderId == R.id.rbMale) {
            gender = "Male";
        } else if (selectedGenderId == R.id.rbFemale) {
            gender = "Female";
        }

        // --- Register in Database ---
        boolean success = dbHelper.registerUser(email, firstName, lastName, gender, dob, university, password);

        if (success) {
            Toast.makeText(this, "Registration successful! Please log in.", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Email may already be registered.", Toast.LENGTH_LONG).show();
        }
    }
}