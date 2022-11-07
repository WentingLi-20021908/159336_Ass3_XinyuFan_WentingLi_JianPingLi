package com.example.myapplication.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.PagerAdapter;

import com.example.myapplication.R;
import com.example.myapplication.databinding.LayoutMemosBinding;
import com.example.myapplication.databinding.MemoItemBinding;
import com.example.myapplication.ui.MemoViewModel;

import java.util.ArrayList;
import java.util.Objects;

public class SlideAdapter extends PagerAdapter {
    //slide view list
    private final ArrayList<View> mViewList;
    //titles of views in list
    private final ArrayList<String> mTitleList;

    public SlideAdapter(Context context,  ArrayList<View> views) {
        mViewList = new ArrayList<>(views);
        mTitleList = new ArrayList<>();
        mTitleList.add(context.getString(R.string.unfinished));
        mTitleList.add(context.getString(R.string.finished));
    }


    @Override
    public int getCount() {
        return mViewList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return Objects.equals(view, object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(mViewList.get(position));
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }
}
