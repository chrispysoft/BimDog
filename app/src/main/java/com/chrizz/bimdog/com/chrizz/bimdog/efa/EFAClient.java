package com.chrizz.bimdog.com.chrizz.bimdog.efa;

import android.location.Location;
import android.net.Uri.Builder;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class EFAClient {
	
	private final String baseURL = "https://www.linzag.at/static/";
	
	
	public ArrayList<Stop> loadStops(Location location) {
		Builder builder = Uri.parse(baseURL).buildUpon();
		builder.appendPath("XML_STOPFINDER_REQUEST");
		builder.appendQueryParameter("locationServerActive", "1");
		builder.appendQueryParameter("stateless", "1");
		builder.appendQueryParameter("outputFormat", "JSON");
		builder.appendQueryParameter("type_sf", "coord");
		builder.appendQueryParameter("name_sf", String.format(Locale.ENGLISH, "%.6f:%.6f:WGS84", location.getLongitude(), location.getLatitude()));
		builder.appendQueryParameter("coordOutputFormat", "WGS84%%5BDD.ddddd%%5D");
		Uri uri = builder.build();
		ArrayList stops = null;
		try {
			URL url = new URL(uri.toString());
			JSONObject rootObject = downloadJSONObject(url);
			JSONObject stopFinderObject = rootObject.getJSONObject("stopFinder");
			JSONArray stopsArray = stopFinderObject.getJSONArray("itdOdvAssignedStops");
			stops = new ArrayList<Stop>();
			for (int i=0; i<stopsArray.length(); i++) {
				JSONObject stopObject = stopsArray.getJSONObject(i);
				String stopID = stopObject.getString("stopID");
				String stopName = stopObject.getString("name");
				String stopDistance = stopObject.getString("distance");
				EFAClient.Stop stop = new EFAClient.Stop(stopID, stopName, stopDistance);
				stops.add(stop);
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return stops;
	}
	
	
	
	
	private JSONObject downloadJSONObject(URL url) {
		JSONObject jsonObject = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = url.openStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder stringBuilder = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}
				String result = stringBuilder.toString();
				jsonObject = new JSONObject(result);
				in.close();
				reader.close();
			} else {
			
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
		
		}
		return jsonObject;
	}
	
	
	public class Stop {
		public String id, name, distance;
		public Stop (String id, String name, String distance) {
			this.id = id; this.name = name; this.distance = distance;
		}
	}
	
	public class Departure {
		public String name, countdown;
		public Departure(String name, String countdown) {
			this.name = name; this.countdown = countdown;
		}
	}
	
}
