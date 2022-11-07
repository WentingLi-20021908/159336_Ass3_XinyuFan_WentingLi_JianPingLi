package com.example.myapplication.ui.recycler_bin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentEditorBinding;
import com.example.myapplication.databinding.FragmentRecyclerBinBinding;
import com.example.myapplication.model.AMemo;
import com.example.myapplication.ui.MemoViewModel;
import com.example.myapplication.ui.home.MemoListAdapter;

import java.util.ArrayList;

public class RecyclerBinFragment extends Fragment {
    private FragmentRecyclerBinBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecyclerBinBinding.inflate(inflater, container, false);
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        MemoViewModel memoViewModel = new ViewModelProvider(activity).get(MemoViewModel.class);
        MemoListAdapter memoListAdapter = new MemoListAdapter(memoViewModel, false);
        binding.recyclerviewMemo.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerviewMemo.setAdapter(memoListAdapter);
        memoViewModel.getRecyclableMemoList().observe(getViewLifecycleOwner(), memos -> {
            if (memos.size() > 0) {
                binding.tvNothingRecycler.setVisibility(View.GONE);
            } else {
                binding.tvNothingRecycler.setVisibility(View.VISIBLE);
            }
            memoListAdapter.refreshData(memos);
        });
        binding.buttonSearchRecycler.setOnClickListener(view ->
                memoViewModel.searchMemos(binding.searchInput.getText().toString()));
        binding.buttonCancelRecycler.setOnClickListener(view -> {
            binding.searchInput.setText("");
            memoViewModel.cancelSearch();
        });
        return binding.getRoot();
    }
}