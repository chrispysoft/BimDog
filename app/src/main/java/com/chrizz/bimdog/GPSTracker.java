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
	
	interface GPSTrackerListener {
		void locationChanged(double latitude, double longitude);
	}
	
	GPSTrackerListener listener;
	
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	Location location;
	
	
	private final Context mContext;
	
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // The minimum distance to change Updates in meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 3; //1000 * 60 * 1; // The minimum time between updates in milliseconds
	
	protected LocationManager locationManager;
	
	
	public GPSTracker(Context context) {
		this.mContext = context;
		getLocation();
	}
	
	public boolean canGetLocation() {
		return this.canGetLocation;
	}
	
	public void startUpdatatingLocation() {
		location = getLocation();
		if (location != null) {
			listener.locationChanged(location.getLatitude(), location.getLongitude());
		}
	}
	
	public void stopUsingGPS(){
		if(locationManager != null){
			locationManager.removeUpdates(GPSTracker.this);
		}
	}
	
	private Location getLocation() {
		try {
			locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			
			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				// First get location from Network Provider
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					}
				}
				
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						
						if (locationManager != null) {
							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}
	
	
	
	public void showSettingsAlert(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle("GPS settings");
		dialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
		dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
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
		listener.locationChanged(location.getLatitude(), location.getLongitude());
	}
	
	@Override public void onProviderDisabled(String provider) {
	}
	
	@Override public void onProviderEnabled(String provider) {
	}
	
	@Override public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	@Override public IBinder onBind(Intent arg0) {
		return null;
	}
}