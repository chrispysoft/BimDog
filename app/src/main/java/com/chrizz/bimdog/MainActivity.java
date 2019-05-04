package com.chrizz.bimdog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		NavHostFragment hostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
		NavController navController = hostFragment.getNavController();
	}
	
}
