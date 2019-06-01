package com.chrizz.bimdog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;

import java.util.Locale;


public class GPSTracker extends Service implements LocationListener {
	
	public interface GPSTrackerListener {
		void locationUpdated(Location location);
		void logMessage(String message);
	}
	
	
	private final Context context;
	private final GPSTrackerListener listener;
	private final LocationManager locationManager;
	private static final int REQUEST_CODE_PERMISSION = 2;
	private static final String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BW_UPDATES = 1000 * 3;
	
	
	public GPSTracker(Context context, GPSTrackerListener listener) {
		this.context = context;
		this.listener = listener;
		this.locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
	}
	
	
	public void startUpdatingLocation() {
		if (! isLocationServiceEnabled()) {
			new AlertDialog.Builder(context)
					.setTitle("GPS not enabled")
					.setMessage("Please enable Location Services")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which) {
							context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						}
					})
					.show();
			return;
		}
		
		if (ActivityCompat.checkSelfPermission(context, mPermission) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions((Activity) context, new String[]{mPermission}, REQUEST_CODE_PERMISSION);
			return;
		}
		
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		logLocation("init GPS:", location, false);
		if (location == null) {
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			logLocation("init NW:", location, false);
		}
		
		checkLocationUpdate(location);
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
	}
	
	
	private void checkLocationUpdate(Location location) {
		if (location != null) {
			listener.locationUpdated(location);
			logLocation("update", location, true);
		}
	}
	
	private void logLocation(String message, Location location, boolean showProvider) {
		if (location != null) {
			if (showProvider) {
				listener.logMessage(String.format(Locale.ENGLISH,"%s (%s): %.4f,%.4f accuracy: %.1f", message, location.getProvider(), location.getLatitude(), location.getLongitude(), location.getAccuracy()));
			} else {
				listener.logMessage(String.format(Locale.ENGLISH,"%s %.4f,%.4f accuracy: %.1f", message, location.getLatitude(), location.getLongitude(), location.getAccuracy()));
			}
		} else {
			listener.logMessage(String.format("%s null", message));
		}
	}
	
	
	public void stopUpdatingLocation(){
		if(locationManager != null){
			locationManager.removeUpdates(GPSTracker.this);
		}
	}
	
	
	private Boolean isLocationServiceEnabled() {
		try {
			int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
			return mode != Settings.Secure.LOCATION_MODE_OFF;
		}
		catch (Settings.SettingNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	@Override public void onLocationChanged(Location location) {
		checkLocationUpdate(location);
	}
	@Override public void onProviderDisabled(String provider) { }
	@Override public void onProviderEnabled(String provider) { }
	@Override public void onStatusChanged(String provider, int status, Bundle extras) { }
	@Override public IBinder onBind(Intent arg0) { return null; }
}