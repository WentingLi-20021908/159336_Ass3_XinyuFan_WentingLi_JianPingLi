package com.example.myapplication.ui.setting;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.databinding.FragmentSettingBinding;


public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingBinding.inflate(inflater, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        binding.radioButtonNight.setChecked(mainActivity.isNightMode());
        //set day/night mode
        binding.radioButtonNight.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                mainActivity.setNightMode();
            }
        });
        binding.radioButtonDay.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                mainActivity.setDayMode();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //handle back button event
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(binding.getRoot()).popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this,
                callback);
    }
}