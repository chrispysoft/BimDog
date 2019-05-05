package com.chrizz.bimdog;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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


public class StopListFragment extends Fragment implements AdapterView.OnItemClickListener {
	
	private ArrayList stops = new ArrayList<EFAClient.Stop>();
	private ListView listView;
	
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.stop_list_fragment, container, false);
	}
	
	
	@Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		StopListAdapter stopListAdapter = new StopListAdapter(view.getContext(), stops);
		listView = getView().findViewById(R.id.stopListView);
		listView.setAdapter(stopListAdapter);
		listView.setOnItemClickListener(this);
	}
	
	public void updateStops(Location location) {
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
			Log.i("StopListFragment", "EFAClientStopRequest.doInBackground");
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
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.stopNameLabel.setText(stop.name);
			return convertView;
		}
	}
	
	
	private static class ViewHolder {
		TextView stopNameLabel;
	}
	
}

