package com.chrizz.bimdog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.ArrayList;


public class StopListFragment extends Fragment {
	
	private ArrayList stops = new ArrayList<Stop>();
	
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		for (int i=0; i<5; i++) {
			Stop stop = new Stop();
			stop.name = "Stop!";
			stops.add(stop);
		}
		
		return inflater.inflate(R.layout.stop_list_fragment, container, false);
	}
	
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		StopListAdapter stopListAdapter = new StopListAdapter(view.getContext(), stops);
		ListView listView = getView().findViewById(R.id.stopListView);
		listView.setAdapter(stopListAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Stop stop = (Stop) parent.getAdapter().getItem(position);
				Navigation.findNavController(view).navigate(R.id.departureListFragment);
			}
		});
	}
}
