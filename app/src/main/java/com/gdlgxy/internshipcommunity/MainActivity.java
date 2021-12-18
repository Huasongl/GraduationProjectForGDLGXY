package com.gdlgxy.internshipcommunity;

import android.os.Bundle;
import android.view.MenuItem;

import com.gdlgxy.internshipcommunity.module.mainpageconfig.NavGraphBuilder;
import com.gdlgxy.internshipcommunity.module.mainpageconfig.view.AppBottomBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.gdlgxy.internshipcommunity.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBottomBar navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(this);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = NavHostFragment.findNavController(fragment);
        NavGraphBuilder.build(this,navController,fragment.getChildFragmentManager(),fragment.getId());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navController.navigate(item.getItemId());
        return true;
    }
}