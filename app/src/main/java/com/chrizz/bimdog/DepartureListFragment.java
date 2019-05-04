package com.chrizz.bimdog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DepartureListFragment extends Fragment {
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.departure_list_fragment, container, false);
	}
	
	@Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		String stopID = getArguments().getString("stopID");
		System.out.println("stopID="+stopID);
		super.onViewCreated(view, savedInstanceState);
	}
}
