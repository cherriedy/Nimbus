package com.optlab.nimbus.ui.view;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.optlab.nimbus.R;
import com.optlab.nimbus.databinding.ActivityMainBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.getWindow().setDecorFitsSystemWindows(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        ViewCompat.setOnApplyWindowInsetsListener(
                binding.getRoot(),
                (v, insets) -> {
                    // Get the insets for the status bar
                    Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
                    // Get the insets for the navigation bar
                    Insets navigationBarInsets =
                            insets.getInsets(WindowInsetsCompat.Type.navigationBars());

                    // Set the top padding to the status bar height
                    ViewGroup.LayoutParams statusBarLayoutParams =
                            binding.statusBar.getLayoutParams();
                    // Set the height of the status bar view to the height of the status bar
                    statusBarLayoutParams.height = statusBarInsets.top;
                    binding.statusBar.setLayoutParams(statusBarLayoutParams);

                    // Set the bottom padding to the navigation bar height
                    ViewGroup.LayoutParams navigationBarLayoutParams =
                            binding.navigationBar.getLayoutParams();
                    // Set the height of the navigation bar view to the height of the navigation bar
                    navigationBarLayoutParams.height = navigationBarInsets.bottom;
                    binding.navigationBar.setLayoutParams(navigationBarLayoutParams);

                    return insets;
                });
    }
}
