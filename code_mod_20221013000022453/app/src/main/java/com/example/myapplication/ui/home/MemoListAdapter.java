package com.example.myapplication.ui.home;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.MemoItemBinding;
import com.example.myapplication.model.AMemo;
import com.example.myapplication.ui.MemoViewModel;

import java.util.ArrayList;
import java.util.Date;

public class MemoListAdapter extends RecyclerView.Adapter<MemoListAdapter.MemoViewHolder> {
    private final MemoViewModel memoViewModel;
    private final ArrayList<AMemo> memos;
    private final boolean enableFinishedBtn;
    private boolean editable = false;

    public MemoListAdapter(MemoViewModel viewModel, boolean enableFinishedBtn) {
        memoViewModel = viewModel;
        memos = new ArrayList<>();
        this.enableFinishedBtn = enableFinishedBtn;
    }

    /**
     * whether memo is editable.
     *
     * @param editable true is editable.
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshData(ArrayList<AMemo> memoList) {
        memos.clear();
        memos.addAll(memoList);
        notifyDataSetChanged();
    }

    public static class MemoViewHolder extends RecyclerView.ViewHolder {
        public final MemoItemBinding binding;
        private AMemo memo;

        public MemoViewHolder(@NonNull MemoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public AMemo getMemo() {
            return memo;
        }

        /**
         * bind data with view
         *
         * @param memo AMemo
         */
        public void bind(@NonNull AMemo memo, MemoViewModel memoViewModel) {
            this.memo = memo;
            String titleStr = memo.getTitle();
            if (titleStr.length() > 10) {
                titleStr = String.format("%s...", titleStr.substring(0, 10));
            }
            binding.tvMemoTitle.setText(titleStr);
            Date millisecondDate = new Date(memo.getModDate());
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String millisecondStrings = formatter.format(millisecondDate);
            binding.tvDate.setText(String.format("Last Updated: %s", millisecondStrings));

            //select state
            ArrayList<AMemo> selectedMemos = memoViewModel.getSelectedMemos();
            assert selectedMemos != null;
            //remove listener set before
            binding.checkBoxSelect.setOnCheckedChangeListener(null);
            binding.checkBoxSelect.setChecked(selectedMemos.contains(memo));
            //select listener
            binding.checkBoxSelect.setOnCheckedChangeListener((compoundButton, checked) -> {
                if (checked) {
                    memoViewModel.addMemoToSelected(memo);
                } else {
                    memoViewModel.removeMemoFromSelected(memo);
                }
            });

            //finished state
            ArrayList<AMemo> finishedMemos = memoViewModel.getFinishedMemoList().getValue();
            assert finishedMemos != null;
            //remove listener set before
            binding.checkboxFinished.setOnCheckedChangeListener(null);
            binding.checkboxFinished.setChecked(memo.isFinished());
            //finished check listener
            binding.checkboxFinished.setOnCheckedChangeListener((compoundButton, checked) -> {
                if (checked) {
                    memoViewModel.finishMemo(memo);
                } else {
                    memoViewModel.unFinishMemo(memo);
                }
            });
        }
    }

    @NonNull
    @Override
    public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MemoItemBinding memoItemBinding = MemoItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent, false);
        //enable/disable finished check
        memoItemBinding.checkboxFinished.setEnabled(enableFinishedBtn);
        MemoViewHolder holder = new MemoViewHolder(memoItemBinding);
        //edit memo
        memoItemBinding.getRoot().setOnClickListener(view -> {
            AMemo memo = holder.getMemo();
            Bundle bundle = new Bundle();
            bundle.putBoolean(parent.getContext().getString(R.string.args_editable), editable);
            bundle.putSerializable(parent.getContext().getString(R.string.args_memo), memo);
            Navigation.findNavController(view).navigate(R.id.nav_editor, bundle);
        });
        //make note to top
        memoItemBinding.topNote.setOnClickListener(view -> {
            AMemo memo = holder.getMemo();
            memoViewModel.setOnTop(memo);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MemoViewHolder holder, int position) {
        AMemo memo = memos.get(position);
        holder.bind(memo, memoViewModel);
    }

    @Override
    public int getItemCount() {
        return memos.size();
    }
}
