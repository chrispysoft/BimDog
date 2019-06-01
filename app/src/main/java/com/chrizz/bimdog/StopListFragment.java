package com.chrizz.bimdog;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import java.util.ArrayList;
import com.chrizz.bimdog.com.chrizz.bimdog.efa.EFAClient;
import android.util.Log;


public class StopListFragment extends Fragment implements GPSTracker.GPSTrackerListener, AdapterView.OnItemClickListener {
	
	private GPSTracker gpsTracker;
	private ArrayList stops = new ArrayList<EFAClient.Stop>();
	private ListView listView;
	private TextView logView;
	
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.stop_list_fragment, container, false);
	}
	
	@Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		listView = getView().findViewById(R.id.stopListView);
		listView.setAdapter(new StopListAdapter(getContext(), stops));
		listView.setOnItemClickListener(this);
		logView = getView().findViewById(R.id.logTextView);
		logView.setText("");
		logView.setMovementMethod(new ScrollingMovementMethod());
		gpsTracker = new GPSTracker(getActivity(), this);
	}
	
	@Override public void onResume() {
		super.onResume();
		Log.i("StopListFragment", "onResume");
		gpsTracker.startUpdatingLocation();
	}
	
	@Override public void onPause() {
		super.onPause();
		Log.i("StopListFragment", "onPause");
		gpsTracker.stopUpdatingLocation();
	}
	
	@Override public void locationUpdated(Location location) {
		Log.i("StopListFragment", "locationUpdated");
		if (EFAClient.canAccessNetwork(getActivity())) {
			new EFAClientStopRequest().execute(location);
		} else {
			new AlertDialog.Builder(getContext())
					.setTitle("No Network")
					.setPositiveButton("OK", null)
					.show();
		}
	}
	
	@Override public void logMessage(String message) {
		logView.append(message + "\n");
	}
	
	@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		EFAClient.Stop stop = (EFAClient.Stop) parent.getAdapter().getItem(position);
		Bundle bundle = new Bundle();
		bundle.putString("stopID", stop.id);
		bundle.putString("stopName", stop.name);
		Navigation.findNavController(view).navigate(R.id.departureListFragment, bundle);
	}
	
	
	private class EFAClientStopRequest extends AsyncTask<Location, Void, ArrayList<EFAClient.Stop>> {
		@Override protected ArrayList<EFAClient.Stop> doInBackground(Location... locations) {
			return new EFAClient().loadStops(locations[0]);
		}
		@Override protected void onPostExecute(ArrayList<EFAClient.Stop> result) {
			super.onPostExecute(result);
			stops.clear();
			if (result != null) {
				stops.addAll(result);
			}
			listView.invalidateViews();
		}
	}
	
	
	private class StopListAdapter extends ArrayAdapter<EFAClient.Stop> {
		StopListAdapter(Context context, ArrayList<EFAClient.Stop> stops) {
			super(context, R.layout.stop_list_item, stops);
		}
		@Override public View getView(int position, View convertView, ViewGroup parent) {
			EFAClient.Stop stop = getItem(position);
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(getContext());
				convertView = inflater.inflate(R.layout.stop_list_item, parent, false);
				viewHolder.stopNameLabel = convertView.findViewById(R.id.stopNameLabel);
				viewHolder.distanceLabel = convertView.findViewById(R.id.distanceLabel);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.stopNameLabel.setText(stop.name);
			viewHolder.distanceLabel.setText(stop.distance + " m");
			return convertView;
		}
	}
	
	
	private static class ViewHolder {
		TextView stopNameLabel, distanceLabel;
	}
	
}

