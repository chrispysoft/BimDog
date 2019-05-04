package com.chrizz.bimdog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class StopListAdapter extends ArrayAdapter<Stop> {
	private static class ViewHolder {
		TextView stopNameLabel;
	}
	
	
	public StopListAdapter(Context context, ArrayList<Stop> stops) {
		super(context, R.layout.stop_item, stops);
	}
	
	
	@Override public View getView(int position, View convertView, ViewGroup parent) {
		Stop stop = getItem(position);
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
