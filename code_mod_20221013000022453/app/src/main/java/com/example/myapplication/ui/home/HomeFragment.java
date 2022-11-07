package com.example.myapplication.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.dao.MemoDao;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.databinding.LayoutMemosBinding;
import com.example.myapplication.ui.MemoViewModel;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        MemoViewModel memoViewModel = new ViewModelProvider(activity).get(MemoViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        ArrayList<View> pagerViewList = new ArrayList<>();

        //unfinished view is not created
        LayoutMemosBinding bindingUnfinished = LayoutMemosBinding.inflate(
                LayoutInflater.from(container.getContext()),
                container, false);
        pagerViewList.add(createUnfinishedView(bindingUnfinished, memoViewModel));

        //finished view is not created
        LayoutMemosBinding bindingFinished = LayoutMemosBinding.inflate(
                LayoutInflater.from(container.getContext()),
                container, false);
        pagerViewList.add(createdFinishedView(bindingFinished, memoViewModel));

        SlideAdapter slideAdapter = new SlideAdapter(activity, pagerViewList);
        binding.pager.setAdapter(slideAdapter);
        binding.pagerTitle.setTextSize(Dimension.SP, 20);
        binding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    memoViewModel.setCurrentDataType(MemoViewModel.DATA_UNFINISHED);
                }
                if (position == 1) {
                    memoViewModel.setCurrentDataType(MemoViewModel.DATA_FINISHED);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.buttonSearch.setOnClickListener(view ->
                memoViewModel.searchMemos(binding.searchName.getText().toString()));
        binding.buttonCancel.setOnClickListener(view -> {
            binding.searchName.setText("");
            memoViewModel.cancelSearch();
        });
        return binding.getRoot();
    }

    /**
     * create unfinished view
     *
     * @param binding LayoutMemosBinding
     * @return View
     */
    private View createUnfinishedView(LayoutMemosBinding binding, MemoViewModel memoViewModel) {
        binding.fab.setOnClickListener(view -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_home_to_editor);
        });
        MemoListAdapter memoListAdapter = new MemoListAdapter(memoViewModel, true);
        memoListAdapter.setEditable(true);
        binding.recyclerviewMemo.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerviewMemo.setAdapter(memoListAdapter);

        //memo list update
        memoViewModel.getUnfinishedMemoList().observe(getViewLifecycleOwner(), aMemos -> {
            if (aMemos.size() > 0) {
                binding.tvNothing.setVisibility(View.GONE);
            } else {
                binding.tvNothing.setVisibility(View.VISIBLE);
            }
            memoListAdapter.refreshData(aMemos);
        });
        return binding.getRoot();
    }

    /**
     * create finished view
     *
     * @param binding LayoutMemosBinding
     * @return View
     */
    private View createdFinishedView(LayoutMemosBinding binding, MemoViewModel memoViewModel) {
        //hide float add button
        binding.fab.setVisibility(View.GONE);
        MemoListAdapter memoListAdapter = new MemoListAdapter(memoViewModel, true);
        memoListAdapter.setEditable(false);
        binding.recyclerviewMemo.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerviewMemo.setAdapter(memoListAdapter);
        memoViewModel.getFinishedMemoList().observe(getViewLifecycleOwner(), aMemos -> {
            if (aMemos.size() > 0) {
                binding.tvNothing.setVisibility(View.GONE);
            } else {
                binding.tvNothing.setVisibility(View.VISIBLE);
            }
            memoListAdapter.refreshData(aMemos);
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}