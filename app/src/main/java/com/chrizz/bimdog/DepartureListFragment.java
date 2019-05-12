package com.chrizz.bimdog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.chrizz.bimdog.com.chrizz.bimdog.efa.EFAClient;
import java.util.ArrayList;


public class DepartureListFragment extends Fragment {
	
	private String stopID;
	private ListView listView;
	private DepartureListAdapter listAdapter;
	private ProgressBar progressBar;
	
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.departure_list_fragment, container, false);
	}
	
	@Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		stopID = getArguments().getString("stopID");
		
		listAdapter = new DepartureListAdapter(view.getContext());
		listView = getView().findViewById(R.id.departureListView);
		listView.setAdapter(listAdapter);
		
		progressBar = getView().findViewById(R.id.progressBar);
		
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
		@Override protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
		}
		@Override protected void onPostExecute(ArrayList<EFAClient.Departure> result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);
			listAdapter.setDepartures(result);
			//listView.invalidateViews();
		}
	}
}
