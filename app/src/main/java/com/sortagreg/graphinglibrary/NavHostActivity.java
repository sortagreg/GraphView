package com.sortagreg.graphinglibrary;

import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class NavHostActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_host);
        drawerLayout = findViewById(R.id.navDrawer);
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(navController.getGraph())
                .setDrawerLayout(drawerLayout)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.navHostFragment).navigateUp();
    }
}
