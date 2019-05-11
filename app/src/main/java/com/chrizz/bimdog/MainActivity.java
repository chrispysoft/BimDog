package com.chrizz.bimdog;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;
import androidx.navigation.NavController;
import androidx.navigation.ui.NavigationUI;


public class MainActivity extends AppCompatActivity {
	
	private DrawerLayout drawer;
	private ActionBarDrawerToggle toggle;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
		drawer = findViewById(R.id.drawer_layout);
		NavigationUI.setupActionBarWithNavController(this, navController, drawer);
		
		toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
			@Override public void onDrawerStateChanged(int newState) {
				super.onDrawerStateChanged(newState);
				toggle.syncState();
			}
		};
		drawer.addDrawerListener(toggle);
		toggle.syncState();
	}
	
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("MainActivity", "onOptionsItemSelected");
		int navID = Navigation.findNavController(this, R.id.navHostFragment).getCurrentDestination().getId();
		if (navID == R.id.stopListFragment) {
			return toggle.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override public void onBackPressed() {
		Log.i("MainActivity", "onBackPressed");
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}
	
	@Override public boolean onSupportNavigateUp() {
		Log.i("MainActivity", "onSupportNavigateUp");
		return Navigation.findNavController(this, R.id.navHostFragment).navigateUp();
	}
}
