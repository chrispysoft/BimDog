package com.chrizz.bimdog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity implements GPSTracker.GPSTrackerListener {
	
	private final int REQUEST_CODE_PERMISSION = 2;
	private final String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
	private GPSTracker gpsTracker;
	public Location currentLocation;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		gpsTracker = new GPSTracker(MainActivity.this);
		gpsTracker.listener = this;
		try {
			Log.i("MainActivity", "Requesting Location permissions");
			ActivityCompat.requestPermissions(this, new String[]{mPermission}, REQUEST_CODE_PERMISSION);
			if (gpsTracker.canGetLocation()) {
				gpsTracker.startUpdatingLocation();
			} else {
				gpsTracker.showSettingsAlert();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override public void locationUpdated(Location location) {
		Log.i("MainActivity", "locationUpdated");
		currentLocation = location;
		StopListFragment stopListFragment = (StopListFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment).getChildFragmentManager().getPrimaryNavigationFragment();
		if (stopListFragment != null) {
			stopListFragment.updateStops(location);
		} else {
			Log.i("MainActivity", "stopListFragment is null");
		}
	}
}
