package com.chrizz.bimdog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import com.chrizz.bimdog.com.chrizz.bimdog.efa.EFAClient.Departure;


public class DepartureListAdapter extends BaseAdapter {
	
	private static final int TYPE_ITEM = 0;
	private static final int TYPE_HEADER = 1;
	
	private LayoutInflater layoutInflater;
	private ArrayList<DataContainer> dataContainers = new ArrayList<>();
	
	
	DepartureListAdapter(Context context) {
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setDepartures(ArrayList<Departure> departures) {
		dataContainers.clear();
		
		departures.sort(new Comparator<Departure>() {
			@Override public int compare(Departure d1, Departure d2) {
				return d1.getPlatformID().compareTo(d2.getPlatformID());
			}
		});
		
		Integer lastPlatformID = null;
		for (Departure departure : departures) {
			int currPlatformID = departure.getPlatformID();
			if (lastPlatformID == null || lastPlatformID != currPlatformID) {
				addHeader(currPlatformID);
				lastPlatformID = currPlatformID;
			}
			addItem(departure);
		}
		
		notifyDataSetChanged();
	}
	
	private void addItem(Departure departure) {
		DataContainer container = new DataContainer(TYPE_ITEM);
		container.number = departure.number;
		container.direction = departure.direction;
		container.countdown = departure.countdown;
		dataContainers.add(container);
	}
	
	private void addHeader(int platform) {
		DataContainer container = new DataContainer(TYPE_HEADER);
		container.platform = "Platform " + (platform+1);
		dataContainers.add(container);
	}
	
	
	@Override public int getItemViewType(int position) {
		return dataContainers.get(position).type;
	}
	
	@Override public int getViewTypeCount() {
		return 2;
	}
	
	@Override public int getCount() {
		return dataContainers.size();
	}
	
	@Override public DataContainer getItem(int position) {
		return dataContainers.get(position);
	}
	
	@Override public long getItemId(int position) {
		return TYPE_ITEM;
	}
	
	@Override public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		int rowType = getItemViewType(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			switch (rowType) {
				case TYPE_ITEM:
					convertView = layoutInflater.inflate(R.layout.departure_list_item, parent, false);
					viewHolder.numberLabel = convertView.findViewById(R.id.numberLabel);
					viewHolder.directionLabel = convertView.findViewById(R.id.directionLabel);
					viewHolder.countdownLabel = convertView.findViewById(R.id.countdownLabel);
					convertView.setTag(viewHolder);
					break;
				case TYPE_HEADER:
					convertView = layoutInflater.inflate(R.layout.departure_list_header, parent, false);
					viewHolder.platformLabel = convertView.findViewById(R.id.platformLabel);
					convertView.setTag(viewHolder);
					break;
			}
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		DataContainer container = getItem(position);
		switch (rowType) {
			case TYPE_ITEM:
				viewHolder.numberLabel.setText(container.number);
				viewHolder.directionLabel.setText(container.direction);
				viewHolder.countdownLabel.setText(container.countdown);
				break;
			case TYPE_HEADER:
				viewHolder.platformLabel.setText(container.platform);
				break;
		}
		
		return convertView;
	}
	
	
	private static class ViewHolder {
		TextView numberLabel, directionLabel, countdownLabel, platformLabel;
	}
	
	
	private class DataContainer {
		int type;
		String platform, number, direction, countdown;
		DataContainer(int type) {
			this.type = type;
		}
	}
}
