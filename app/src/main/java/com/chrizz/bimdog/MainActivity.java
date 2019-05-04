package com.chrizz.bimdog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.Manifest;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity {
	
	private static final int REQUEST_CODE_PERMISSION = 2;
	String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
	GPSTracker gpsTracker;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		try {
			//if (ActivityCompat.checkSelfPermission(this, mPermission) != MockPackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this, new String[]{mPermission}, REQUEST_CODE_PERMISSION);
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		gpsTracker = new GPSTracker(MainActivity.this);
		gpsTracker.listener = new GPSTracker.GPSTrackerListener() {
			@Override public void locationChanged(double latitude, double longitude) {
				StopListFragment stopListFragment = (StopListFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment).getChildFragmentManager().getPrimaryNavigationFragment();
				if (stopListFragment != null) {
					stopListFragment.updateGPSCoordinates(latitude, longitude);
				}
			}
		};
		
		if (gpsTracker.canGetLocation()) {
			gpsTracker.startUpdatatingLocation();
		} else {
			Log.i("Location", "Can't get Location");
			gpsTracker.showSettingsAlert();
		}
		
	}
	
}
