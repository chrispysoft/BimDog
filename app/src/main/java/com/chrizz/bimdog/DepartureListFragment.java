package com.chrizz.bimdog;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.chrizz.bimdog.com.chrizz.bimdog.efa.EFAClient;
import java.util.ArrayList;


public class DepartureListFragment extends Fragment {
	
	private String stopID;
	private ArrayList departures = new ArrayList<EFAClient.Departure>();
	private ListView listView;
	
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.departure_list_fragment, container, false);
	}
	
	@Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		stopID = getArguments().getString("stopID");
		DepartureListAdapter departureListAdapter = new DepartureListAdapter(view.getContext(), departures);
		listView = getView().findViewById(R.id.departureListView);
		listView.setAdapter(departureListAdapter);
		updateDepartures();
	}
	
	private void updateDepartures() {
		new EFAClientDepartureRequest().execute(stopID);
	}
	
	private class EFAClientDepartureRequest extends AsyncTask<String, Void, ArrayList<EFAClient.Departure>> {
		@Override protected ArrayList<EFAClient.Departure> doInBackground(String... stopIDs) {
			Log.i("DepartureListFragment", "EFAClientDepartureRequest.doInBackground");
			return new EFAClient().loadDepartures(stopIDs[0]);
		}
		
		@Override protected void onPostExecute(ArrayList<EFAClient.Departure> result) {
			super.onPostExecute(result);
			departures.clear();
			departures.addAll(result);
			listView.invalidateViews();
		}
	}
	
	
	
	private class DepartureListAdapter extends ArrayAdapter<EFAClient.Departure> {
		public DepartureListAdapter(Context context, ArrayList<EFAClient.Departure> departures) {
			super(context, R.layout.departure_item, departures);
		}
		@Override public View getView(int position, View convertView, ViewGroup parent) {
			EFAClient.Departure departure = getItem(position);
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(getContext());
				convertView = inflater.inflate(R.layout.departure_item, parent, false);
				viewHolder.numberLabel = convertView.findViewById(R.id.numberLabel);
				viewHolder.directionLabel = convertView.findViewById(R.id.directionLabel);
				viewHolder.countdownLabel = convertView.findViewById(R.id.countdownLabel);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.numberLabel.setText(departure.number);
			viewHolder.directionLabel.setText(departure.direction);
			viewHolder.countdownLabel.setText(departure.countdown);
			return convertView;
		}
	}
	
	
	private static class ViewHolder {
		TextView numberLabel, directionLabel, countdownLabel;
	}
}
