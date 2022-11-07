//20020862 Xinyu Fan, 20021908 Wenting Li, 18032967 JianPing Li
package com.example.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.ui.MemoViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final int GET_IMG_CODE = 2;
    private AppBarConfiguration mAppBarConfiguration;
    public ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MemoViewModel memoViewModel = new ViewModelProvider(this).get(MemoViewModel.class);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_setting, R.id.nav_recycler_bin,
                R.id.nav_editor)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setCheckedItem(R.id.menu_nav_home);
        //switch fragment when left NavigationView item changed
        navigationView.setNavigationItemSelectedListener(item -> {
            //reject double click
            if (item == navigationView.getCheckedItem()) {
                return false;
            }
            //switch to setting fragment
            if (item.getItemId() == R.id.menu_nav_setting) {
                navController.navigate(R.id.nav_setting);
                showDelete(false);
                showRestore(false);
            }
            //switch to home fragment
            if (item.getItemId() == R.id.menu_nav_home) {
                navController.navigate(R.id.nav_home);
                showDelete(true);
                showRestore(false);
                memoViewModel.setCurrentDataType(MemoViewModel.DATA_UNFINISHED);
            }
            //switch to recycler bin
            if (item.getItemId() == R.id.menu_nav_recycler) {
                navController.navigate(R.id.nav_recycler_bin);
                showDelete(true);
                showRestore(true);
                memoViewModel.setCurrentDataType(MemoViewModel.DATA_RECYCLABLE);
            }
            binding.drawerLayout.close();
            return true;
        });
        binding.appBarMain.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete) {
                deleteMemos(memoViewModel);
            }
            if (item.getItemId() == R.id.action_restore) {
                restoreRecyclableMemos(memoViewModel);
            }
            return true;
        });
    }

    /**
     * delete memos
     *
     * @param memoViewModel MemoViewModel
     */
    private void deleteMemos(MemoViewModel memoViewModel) {
        if (memoViewModel.getSelectedMemos().size() == 0) {
            Snackbar.make(binding.getRoot(), getString(R.string.delete_hint),
                    Snackbar.LENGTH_SHORT).show();
            return;
        }
        //confirm dialog
        if (!Objects.equals(memoViewModel.getCurrentDataType(), MemoViewModel.DATA_RECYCLABLE)) {
            new AlertDialog.Builder(this).setTitle(R.string.hint)
                    .setMessage("Sure to move these memos to recycler bin?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        memoViewModel.recycleMemos();
                    }).setNegativeButton("Cancel", (dialogInterface, i) -> {
                    }).show();
        } else {
            new AlertDialog.Builder(this).setTitle(R.string.hint)
                    .setMessage("Memos will be permanently deleted. Sure to do this?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        memoViewModel.deleteMemos();
                    }).setNegativeButton("Cancel", (dialogInterface, i) -> {
                    }).show();
        }
    }

    /**
     * restore memos from recycler bin
     */
    private void restoreRecyclableMemos(MemoViewModel memoViewModel) {
        if (memoViewModel.getSelectedMemos().size() == 0) {
            Snackbar.make(binding.getRoot(), getString(R.string.restore_hint),
                    Snackbar.LENGTH_SHORT).show();
        } else {
            new AlertDialog.Builder(this).setTitle(R.string.hint)
                    .setMessage("Restore selected memos from the recycle bin?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        memoViewModel.restoreRecyclableMemos();
                    }).setNegativeButton("Cancel", (dialogInterface, i) -> {
                    }).show();
        }
    }

    /**
     * show delete button on toolbar
     *
     * @param show true is show
     */
    public void showDelete(boolean show) {
        Menu menu = binding.appBarMain.toolbar.getMenu();
        menu.findItem(R.id.action_delete).setVisible(show);
    }

    /**
     * show restore button on toolbar
     *
     * @param show true is show
     */
    public void showRestore(boolean show) {
        Menu menu = binding.appBarMain.toolbar.getMenu();
        menu.findItem(R.id.action_restore).setVisible(show);
    }

    /**
     * set night mode
     */
    public void setNightMode() {
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    /**
     * set day mode
     */
    public void setDayMode() {
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    /**
     * check if it is in night mode
     *
     * @return true means night mode
     */
    public boolean isNightMode() {
        return getDelegate().getLocalNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
    }

    /**
     * show or hide toolbar
     *
     * @param show true is show
     */
    public void showToolbar(boolean show) {
        if (show) {
            binding.appBarMain.toolbarLayout.setVisibility(View.VISIBLE);
            NavigationView navigationView = binding.navView;
        } else {
            binding.appBarMain.toolbarLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}