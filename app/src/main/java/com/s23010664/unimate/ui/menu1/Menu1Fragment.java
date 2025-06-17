package com.s23010664.unimate.ui.menu1;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.s23010664.unimate.R;
import com.s23010664.unimate.databinding.FragmentMenu1Binding;
import com.s23010664.unimate.ui.slideshow.SlideshowViewModel;

public class Menu1Fragment extends Fragment {

    private Menu1ViewModel Menu1ViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Menu1ViewModel Menu1ViewModel =
                new ViewModelProvider(this).get(Menu1ViewModel.class);

        @NonNull FragmentMenu1Binding binding = FragmentMenu1Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMenu;
        Menu1ViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Object binding = null;
    }


}