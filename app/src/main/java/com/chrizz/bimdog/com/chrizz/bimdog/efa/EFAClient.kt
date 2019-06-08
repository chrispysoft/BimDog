package com.chrizz.bimdog.com.chrizz.bimdog.efa

import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.MalformedURLException
import java.net.URL
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.io.BufferedReader
import java.io.IOException
import java.util.ArrayList
import java.util.Locale
import javax.net.ssl.HttpsURLConnection


class EFAClient {

    private val baseURL = "https://www.linzag.at/static/"


    fun loadStops(location: Location): ArrayList<Stop>? {
        val coordinate = String.format(Locale.ENGLISH, "%.6f:%.6f:WGS84", location.longitude, location.latitude)
        val builder = Uri.parse(baseURL).buildUpon()
        builder.appendPath("XML_STOPFINDER_REQUEST")
        builder.appendQueryParameter("locationServerActive", "1")
        builder.appendQueryParameter("stateless", "1")
        builder.appendQueryParameter("outputFormat", "JSON")
        builder.appendQueryParameter("type_sf", "coord")
        builder.appendQueryParameter("name_sf", coordinate)
        builder.appendQueryParameter("coordOutputFormat", "WGS84%%5BDD.ddddd%%5D")
        val uri = builder.build()
        var stops: ArrayList<Stop>? = null
        try {
            val url = URL(uri.toString())
            val rootObject = downloadJSONObject(url)
            val stopFinderObject = rootObject!!.getJSONObject("stopFinder")
            val jsonArray = stopFinderObject.getJSONArray("itdOdvAssignedStops")
            stops = ArrayList()
            for (obj in jsonArray) {
                val stop = Stop(obj)
                stops.add(stop)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return stops
    }

    fun loadDepartures(stopID: Int): ArrayList<Departure>? {
        val builder = Uri.parse(baseURL).buildUpon()
        builder.appendPath("XML_DM_REQUEST")
        builder.appendQueryParameter("locationServerActive", "1")
        builder.appendQueryParameter("stateless", "1")
        builder.appendQueryParameter("outputFormat", "JSON")
        builder.appendQueryParameter("useRealtime", "1")
        builder.appendQueryParameter("mode", "direct")
        builder.appendQueryParameter("type_dm", "any")
        builder.appendQueryParameter("name_dm", stopID.toString())
        builder.appendQueryParameter("limit", "10")
        val uri = builder.build()
        var departures: ArrayList<Departure>? = null
        try {
            val url = URL(uri.toString())
            val rootObject = downloadJSONObject(url)
            val jsonArray = rootObject!!.getJSONArray("departureList")
            departures = ArrayList()
            for (obj in jsonArray) {
                val departure = Departure(obj)
                departures.add(departure)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return departures
    }


    private fun downloadJSONObject(url: URL): JSONObject? {
        var jsonObject: JSONObject? = null
        try {
            val connection = url.openConnection() as HttpsURLConnection
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inStr = url.openStream()
                val reader = BufferedReader(InputStreamReader(inStr))
                val builder = StringBuilder()
                for (line in reader.lines()) {
                    builder.append(line)
                }
                jsonObject = JSONObject(builder.toString())
                inStr.close()
                reader.close()
            } else {

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return jsonObject
    }


    class Stop {
        val id: Int
        val name: String
        val distance: Int

        constructor(jsonObject: JSONObject) {
            id = jsonObject.getInt("stopID")
            name = jsonObject.getString("name")
            distance = jsonObject.getInt("distance")
        }
    }

    class Departure {
        val platform: Int
        val number: String
        val direction: String
        val countdown: String

        constructor(jsonObject: JSONObject) {
            val servingLineObject = jsonObject.getJSONObject("servingLine")
            platform = jsonObject.getInt("platform")
            number = servingLineObject.getString("number")
            direction = servingLineObject.getString("direction")
            countdown = jsonObject.getString("countdown")
        }
    }


    companion object {
        fun canAccessNetwork(context: Context): Boolean {
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = manager.activeNetworkInfo
            return info != null && info.isConnected
        }

        operator fun JSONArray.iterator(): Iterator<JSONObject>
                = (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()
    }




}
