package com.chrizz.bimdog;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import java.util.ArrayList;
import com.chrizz.bimdog.com.chrizz.bimdog.efa.EFAClient;
import android.util.Log;
import androidx.core.app.ActivityCompat;


public class StopListFragment extends Fragment implements GPSTracker.GPSTrackerListener, AdapterView.OnItemClickListener {
	
	private final int REQUEST_CODE_PERMISSION = 2;
	private final String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
	private GPSTracker gpsTracker;
	private ArrayList stops = new ArrayList<EFAClient.Stop>();
	private ListView listView;
	
	
	@Override public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("StopListFragment", "onCreate");
		gpsTracker = new GPSTracker(getActivity());
		gpsTracker.listener = this;
	}
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i("StopListFragment", "onCreateView");
		return inflater.inflate(R.layout.stop_list_fragment, container, false);
	}
	
	
	@Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		Log.i("StopListFragment", "onViewCreated");
		
		listView = getView().findViewById(R.id.stopListView);
		listView.setAdapter(new StopListAdapter(getContext(), stops));
		listView.setOnItemClickListener(this);
		
		try {
			ActivityCompat.requestPermissions(getActivity(), new String[]{mPermission}, REQUEST_CODE_PERMISSION);
			if (gpsTracker.canGetLocation()) {
				gpsTracker.startUpdatingLocation();
			} else {
				gpsTracker.showSettingsAlert();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override public void locationUpdated(Location location) {
		Log.i("StopListFragment", "locationUpdated");
		new EFAClientStopRequest().execute(location);
	}
	
	@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		EFAClient.Stop stop = (EFAClient.Stop) parent.getAdapter().getItem(position);
		Bundle bundle = new Bundle();
		bundle.putString("stopID", stop.id);
		Navigation.findNavController(view).navigate(R.id.departureListFragment, bundle);
	}
	
	
	private class EFAClientStopRequest extends AsyncTask<Location, Void, ArrayList<EFAClient.Stop>> {
		@Override protected ArrayList<EFAClient.Stop> doInBackground(Location... locations) {
			return new EFAClient().loadStops(locations[0]);
		}
		@Override protected void onPostExecute(ArrayList<EFAClient.Stop> result) {
			super.onPostExecute(result);
			stops.clear();
			stops.addAll(result);
			listView.invalidateViews();
		}
	}
	
	
	private class StopListAdapter extends ArrayAdapter<EFAClient.Stop> {
		public StopListAdapter(Context context, ArrayList<EFAClient.Stop> stops) {
			super(context, R.layout.stop_item, stops);
		}
		@Override public View getView(int position, View convertView, ViewGroup parent) {
			EFAClient.Stop stop = getItem(position);
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(getContext());
				convertView = inflater.inflate(R.layout.stop_item, parent, false);
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

