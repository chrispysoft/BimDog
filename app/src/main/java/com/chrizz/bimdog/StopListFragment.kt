package com.chrizz.bimdog

import android.content.Context
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import java.util.ArrayList
import com.chrizz.bimdog.com.chrizz.bimdog.efa.EFAClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import android.util.Log


class StopListFragment : Fragment(), GPSTracker.GPSTrackerListener, AdapterView.OnItemClickListener, OnMapReadyCallback {

    private var gpsTracker: GPSTracker? = null
    private val stopList = ArrayList<EFAClient.Stop>()
    private var listView: ListView? = null
    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null
    private var currentLocation: Location? = null
    private var logView: TextView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.stop_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = getView()!!.findViewById(R.id.stopListView)
        listView!!.adapter = StopListAdapter(context!!, stopList)
        listView!!.onItemClickListener = this
        mapView = getView()!!.findViewById(R.id.mapView)
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)
        logView = getView()!!.findViewById(R.id.logTextView)
        logView!!.text = ""
        logView!!.movementMethod = ScrollingMovementMethod()
        gpsTracker = GPSTracker(activity!!, this)
    }

    override fun onResume() {
        super.onResume()
        Log.i("StopListFragment", "onResume")
        gpsTracker!!.start()
        mapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.i("StopListFragment", "onPause")
        gpsTracker!!.stop()
        mapView!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    override fun locationUpdated(location: Location) {
        Log.i("StopListFragment", "locationUpdated")
        currentLocation = location
        updateMap()

        if (EFAClient.canAccessNetwork(activity!!)) {
            EFAClientStopRequest().execute(location)
        } else {
            AlertDialog.Builder(context!!)
                    .setTitle(R.string.nonetwork_message)
                    .setPositiveButton("OK", null)
                    .show()
        }
    }

    override fun logMessage(message: String) {
        logView!!.append(message + "\n")
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val stop = parent.adapter.getItem(position) as EFAClient.Stop
        val bundle = Bundle()
        bundle.putInt("stopID", stop.id)
        bundle.putString("stopName", stop.name)
        Navigation.findNavController(view).navigate(R.id.departureListFragment, bundle)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i("StopListFragment", "onMapReady")
        googleMap.setMinZoomPreference(17f)
        this.googleMap = googleMap
        updateMap()
    }

    private fun updateMap() {
        if (googleMap != null && currentLocation != null) {
            val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            val marker = MarkerOptions()
            marker.position(latLng)
            googleMap!!.addMarker(marker)
        }
    }

    private inner class EFAClientStopRequest : AsyncTask<Location, Void, ArrayList<EFAClient.Stop>>() {
        override fun doInBackground(vararg locations: Location): ArrayList<EFAClient.Stop>? {
            return EFAClient().loadStops(locations[0])
        }

        override fun onPostExecute(result: ArrayList<EFAClient.Stop>?) {
            super.onPostExecute(result)
            stopList.clear()
            if (result != null) {
                stopList.addAll(result)
            }
            listView!!.invalidateViews()
        }
    }


    private inner class StopListAdapter internal constructor(context: Context, stops: ArrayList<EFAClient.Stop>) : ArrayAdapter<EFAClient.Stop>(context, R.layout.stop_list_item, stops) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val stop = getItem(position)
            val viewHolder: ViewHolder
            if (convertView == null) {
                viewHolder = ViewHolder()
                val inflater = LayoutInflater.from(context)
                convertView = inflater.inflate(R.layout.stop_list_item, parent, false)
                viewHolder.stopNameLabel = convertView!!.findViewById(R.id.stopNameLabel)
                viewHolder.distanceLabel = convertView.findViewById(R.id.distanceLabel)
                convertView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ViewHolder
            }
            viewHolder.stopNameLabel!!.text = stop!!.name
            viewHolder.distanceLabel!!.text = stop.distance.toString() + " m"
            return convertView
        }
    }


    private class ViewHolder {
        internal var stopNameLabel: TextView? = null
        internal var distanceLabel: TextView? = null
    }

}

