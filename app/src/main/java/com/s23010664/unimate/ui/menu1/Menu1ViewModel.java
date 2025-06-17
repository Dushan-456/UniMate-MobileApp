package com.s23010664.unimate.ui.menu1;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Menu1ViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public Menu1ViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Menu1 fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}