package com.chrizz.bimdog;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.navigation.Navigation;
import androidx.navigation.NavController;
import androidx.navigation.ui.NavigationUI;


public class MainActivity extends AppCompatActivity {
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
		NavigationUI.setupActionBarWithNavController(this, navController);
	}
	
	@Override public boolean onSupportNavigateUp() {
		return Navigation.findNavController(this, R.id.navHostFragment).navigateUp();
	}
}
