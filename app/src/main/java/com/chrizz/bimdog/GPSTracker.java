package com.chrizz.bimdog;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;


public class GPSTracker extends Service implements LocationListener {
	
	public interface GPSTrackerListener {
		void locationUpdated(Location location);
	}
	
	public GPSTrackerListener listener;
	
	
	private boolean gpsEnabled = false;
	private boolean networkEnabled = false;
	private Location location;
	private LocationManager locationManager;
	private final Context context;
	private final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private final long MIN_TIME_BW_UPDATES = 1000 * 3;
	
	
	public GPSTracker(Context context) {
		this.context = context;
		locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
	}
	
	public boolean canGetLocation() {
		gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		return gpsEnabled || networkEnabled;
	}
	
	public void startUpdatingLocation() {
		location = getLocation();
		if (location != null) {
			listener.locationUpdated(location);
		}
	}
	
	public void stopUpdatingLocation(){
		if(locationManager != null){
			locationManager.removeUpdates(GPSTracker.this);
		}
	}
	
	private Location getLocation() {
		Location location = null;
		try {
			if (networkEnabled) {
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
				location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			if (gpsEnabled) {
				if (location == null) {
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}
	
	public void showSettingsAlert() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle("GPS Settings");
		dialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
		dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(intent);
			}
		});
		dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		dialog.show();
	}
	
	@Override public void onLocationChanged(Location location) {
		listener.locationUpdated(location);
	}
	
	@Override public void onProviderDisabled(String provider) { }
	
	@Override public void onProviderEnabled(String provider) { }
	
	@Override public void onStatusChanged(String provider, int status, Bundle extras) { }
	
	@Override public IBinder onBind(Intent arg0) { return null; }
}