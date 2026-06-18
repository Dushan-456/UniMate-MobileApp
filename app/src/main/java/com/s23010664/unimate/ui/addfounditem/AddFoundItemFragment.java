package com.s23010664.unimate.ui.addfounditem;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.s23010664.unimate.DBHelper;
import com.s23010664.unimate.R;

public class AddFoundItemFragment extends Fragment {

    private String selectedImageUri = "";
    private ImageView imagePreview;

    public static AddFoundItemFragment newInstance() {
        return new AddFoundItemFragment();
    }

    // Image Picker Launcher
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        selectedImageUri = imageUri.toString();
                        imagePreview.setImageURI(imageUri);
                        imagePreview.setVisibility(View.VISIBLE);
                    }
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_found_item, container, false);

        EditText titleInput = view.findViewById(R.id.found_title);
        EditText descInput = view.findViewById(R.id.found_description);
        EditText locationInput = view.findViewById(R.id.found_location);
        EditText dateInput = view.findViewById(R.id.found_date);
        EditText contactInput = view.findViewById(R.id.found_contact);

        Button selectImageBtn = view.findViewById(R.id.found_select_image_btn);
        imagePreview = view.findViewById(R.id.found_image_preview);
        Button saveBtn = view.findViewById(R.id.found_save_btn);

        DBHelper dbHelper = new DBHelper(requireContext());

        selectImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            imagePickerLauncher.launch(intent);
        });

        saveBtn.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String desc = descInput.getText().toString().trim();
            String location = locationInput.getText().toString().trim();
            String date = dateInput.getText().toString().trim();
            String contact = contactInput.getText().toString().trim();

            if (title.isEmpty() || desc.isEmpty() || location.isEmpty() || date.isEmpty() || contact.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isInserted = dbHelper.insertLostNFoundItem(
                    "found", title, desc, location, date, contact, selectedImageUri
            );

            if (isInserted) {
                Toast.makeText(requireContext(), "Found item added successfully!", Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigateUp(); // Go back to the Lost & Found page
            } else {
                Toast.makeText(requireContext(), "Failed to add found item", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}