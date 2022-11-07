package com.example.myapplication.ui.editor;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentEditorBinding;
import com.example.myapplication.model.AMemo;
import com.example.myapplication.ui.MemoViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;
import java.util.Objects;


public class EditorFragment extends Fragment {
    private FragmentEditorBinding binding;
    private MemoViewModel memoViewModel;
    private AMemo currentMemo;
    private boolean unSaved = false;
    private ActivityResultLauncher<String> resultLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Handle the returned Uri
        resultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), this::insertNewImage);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        memoViewModel = new ViewModelProvider(activity).get(MemoViewModel.class);
        binding = FragmentEditorBinding.inflate(inflater, container, false);
        initView();
        receiveArgs();
        return binding.getRoot();
    }

    /**
     * init widget
     */
    private void initView() {
        //hide toolbar
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.showToolbar(false);
        binding.buttonBack.setOnClickListener(view -> {
            saveAndBack();
        });
        binding.buttonCancel.setOnClickListener(view -> {
            unSaved = false;
            saveAndBack();
        });
        binding.edittextTitle.addTextChangedListener(new TextWatcher() {
            private String string;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                string = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newString = editable.toString();
                unSaved = !Objects.equals(string, newString);
            }
        });
        binding.editTextEditor.addTextChangedListener(new TextWatcher() {
            private String string;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                string = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                string = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newString = editable.toString();
                unSaved = !Objects.equals(string, newString);
            }
        });
        binding.buttonSave.setOnClickListener(view -> save());
        binding.insertImage.setOnClickListener(view -> {
            resultLauncher.launch("image/*");
        });
    }

    /**
     * insert image to editor
     *
     * @param uri image uri
     */
    private void insertNewImage(Uri uri) {
        if (Objects.equals(uri, null)) {
            return;
        }
        String content = Html.toHtml(binding.editTextEditor.getText(), Html.FROM_HTML_MODE_COMPACT);
        content = String.format("%s<img src=\"%s\"></img>", content, uri);
        binding.editTextEditor.setText(getRichText(content));
    }

    /**
     * get rich text with images
     *
     * @param content String
     * @return Spanned
     */
    private Spanned getRichText(String content) {
        return Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT,
                source -> {
                    try {
                        MainActivity activity = (MainActivity) getActivity();
                        assert activity != null;
                        ContentResolver resolver = activity.getContentResolver();
                        Bitmap bitmap = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(source)));
                        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight());
                        return drawable;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                }, null);
    }

    /**
     * receive memo args when edit a memo
     */
    private void receiveArgs() {
        Bundle bundle = getArguments();
        if (Objects.equals(bundle, null)) {
            binding.tvFragmentTitle.setText(getText(R.string.create_new_memo));
            binding.edittextTitle.setText("");
            binding.editTextEditor.setText("");
        } else {
            AMemo memo = (AMemo) bundle.getSerializable(getString(R.string.args_memo));
            boolean editable = bundle.getBoolean(getString(R.string.args_editable));
            setEditable(editable);
            if (!Objects.equals(memo, null)) {
                binding.tvFragmentTitle.setText(getText(R.string.edit_memo));
                binding.edittextTitle.setText(memo.getTitle());
                currentMemo = memo;
                String content = memo.getContent();
                binding.editTextEditor.setText(getRichText(content));

            }
        }
    }

    /**
     * enable/disable edit
     *
     * @param editable true is editable.
     */
    private void setEditable(boolean editable) {
        binding.edittextTitle.setEnabled(editable);
        binding.editTextEditor.setEnabled(editable);
        if (!editable) {
            binding.bottomBar.setVisibility(View.GONE);
        } else {
            binding.bottomBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * save input and back to home fragment
     */
    private void saveAndBack() {
        if (unSaved) {
            Context context = getContext();
            assert context != null;
            new AlertDialog.Builder(context).setTitle(R.string.hint)
                    .setMessage(R.string.edit_exit_hint)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        back();
                    }).setNegativeButton(R.string.no, (dialogInterface, i) -> {
                    }).show();

        } else {
            back();
        }
    }

    /**
     * back to home fragment
     */
    private void back() {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.showToolbar(true);
        Navigation.findNavController(binding.getRoot()).popBackStack();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //handle back button event
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                saveAndBack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this,
                callback);
    }

    /**
     * save memo
     */
    private void save() {
        String errorMsg = checkData();
        if (!Objects.equals(errorMsg, "")) {
            showMsg(errorMsg);
            return;
        }
        String title = binding.edittextTitle.getText().toString();
        String content = Html.toHtml(binding.editTextEditor.getText(), Html.FROM_HTML_MODE_COMPACT);
        if (Objects.equals(currentMemo, null)) {
            memoViewModel.addMemo(title, content);
        } else {
            memoViewModel.updateMemo(currentMemo, title, content);
        }
        back();
    }

    /**
     * check user input
     *
     * @return errorMsg
     */
    private String checkData() {
        if (binding.edittextTitle.getText().length() == 0) {
            return "Title can not be empty!";
        }
        return "";
    }

    /**
     * show error message
     *
     * @param msg String
     */
    private void showMsg(String msg) {
        Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        //hide soft keyboard when back to home fragment.
        Context context = getContext();
        assert context != null;
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(binding.editTextEditor.getWindowToken(), 0);
        im.hideSoftInputFromWindow(binding.edittextTitle.getWindowToken(), 0);
        super.onPause();
    }
}