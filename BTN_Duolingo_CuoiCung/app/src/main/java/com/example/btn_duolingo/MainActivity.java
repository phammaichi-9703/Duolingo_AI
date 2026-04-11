package com.example.btn_duolingo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private String currentLanguage = "English";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Đảm bảo navigation drawer có thể mở được khi nhấn vào icon hamburger
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        
        if (savedInstanceState == null) {
            loadHomeFragment();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            if (isFinishing() || isDestroyed()) return false;

            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                loadHomeFragment();
                return true;
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commitAllowingStateLoss();
            }
            return true;
        });
    }

    private void loadHomeFragment() {
        if (isFinishing() || isDestroyed()) return;

        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("LANGUAGE", currentLanguage);
        homeFragment.setArguments(bundle);
        
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment, "HOME_FRAGMENT")
                .commitAllowingStateLoss();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (isFinishing() || isDestroyed()) return false;

        int id = item.getItemId();
        if (id == R.id.nav_english) {
            currentLanguage = "English";
            loadHomeFragment();
        } else if (id == R.id.nav_chinese) {
            currentLanguage = "Chinese";
            loadHomeFragment();
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
