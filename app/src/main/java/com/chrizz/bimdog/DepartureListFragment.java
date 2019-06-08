package com.chrizz.bimdog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
	
	private Integer stopID;
	private ListView listView;
	private DepartureListAdapter listAdapter;
	private ProgressBar progressBar;
	private final Handler updateHandler = new Handler();
	private static final int UPDATE_INTERVAL = 10000;
	
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.departure_list_fragment, container, false);
	}
	
	@Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		stopID = getArguments().getInt("stopID");
		listAdapter = new DepartureListAdapter(view.getContext());
		listView = getView().findViewById(R.id.departureListView);
		listView.setAdapter(listAdapter);
		progressBar = getView().findViewById(R.id.progressBar);
	}
	
	@Override public void onResume() {
		super.onResume();
		updateHandler.post(updateCode);
	}
	
	@Override public void onPause() {
		super.onPause();
		updateHandler.removeCallbacks(updateCode);
	}
	
	private final Runnable updateCode = new Runnable() {
		@Override public void run() {
			new EFAClientDepartureRequest().execute(stopID);
			updateHandler.postDelayed(this, UPDATE_INTERVAL);
		}
	};
	
	private class EFAClientDepartureRequest extends AsyncTask<Integer, Void, ArrayList<EFAClient.Departure>> {
		@Override protected ArrayList<EFAClient.Departure> doInBackground(Integer... stopIDs) {
			return new EFAClient().loadDepartures(stopIDs[0]);
		}
		@Override protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
		}
		@Override protected void onPostExecute(ArrayList<EFAClient.Departure> result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);
			listAdapter.setDepartures(result);
		}
	}
}
