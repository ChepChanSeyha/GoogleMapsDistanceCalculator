package com.androidtutorialpoint.googlemapsdistancecalculator

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView

import com.androidtutorialpoint.googlemapsdistancecalculator.POJO.Example
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

import java.util.ArrayList

import retrofit.Call
import retrofit.Callback
import retrofit.GsonConverterFactory
import retrofit.Response
import retrofit.Retrofit

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    internal var origin: LatLng
    internal var dest: LatLng
    internal var MarkerPoints: ArrayList<LatLng>
    internal var ShowDistanceDuration: TextView
    internal var line: Polyline? = null

    // Checking if Google Play Services Available or not
    private val isGooglePlayServicesAvailable: Boolean
        get() {
            val googleAPI = GoogleApiAvailability.getInstance()
            val result = googleAPI.isGooglePlayServicesAvailable(this)
            if (result != ConnectionResult.SUCCESS) {
                if (googleAPI.isUserResolvableError(result)) {
                    googleAPI.getErrorDialog(this, result,
                            0).show()
                }
                return false
            }
            return true
        }

    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        ShowDistanceDuration = findViewById(R.id.show_distance_time) as TextView

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
        }

        // Initializing
        MarkerPoints = ArrayList<LatLng>()

        //show error dialog if Google Play Services not available
        if (!isGooglePlayServicesAvailable) {
            Log.d("onCreate", "Google Play Services not available. Ending Test case.")
            finish()
        } else {
            Log.d("onCreate", "Google Play Services available. Continuing.")
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = getSupportFragmentManager()
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val Model_Town = LatLng(28.7158727, 77.1910738)
        mMap!!.addMarker(MarkerOptions().position(Model_Town).title("Marker in Sydney"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(Model_Town))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11))

        // Setting onclick event listener for the map
        mMap!!.setOnMapClickListener(object : GoogleMap.OnMapClickListener() {

            fun onMapClick(point: LatLng) {

                // clearing map and generating new marker points if user clicks on map more than two times
                if (MarkerPoints.size > 1) {
                    mMap!!.clear()
                    MarkerPoints.clear()
                    MarkerPoints = ArrayList<LatLng>()
                    ShowDistanceDuration.text = ""
                }

                // Adding new item to the ArrayList
                MarkerPoints.add(point)

                // Creating MarkerOptions
                val options = MarkerOptions()

                // Setting the position of the marker
                options.position(point)

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if (MarkerPoints.size == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                } else if (MarkerPoints.size == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                }


                // Add new marker to the Google Map Android API V2
                mMap!!.addMarker(options)

                // Checks, whether start and end locations are captured
                if (MarkerPoints.size >= 2) {
                    origin = MarkerPoints[0]
                    dest = MarkerPoints[1]
                }

            }
        })

        val btnDriving = findViewById(R.id.btnDriving) as Button
        btnDriving.setOnClickListener { build_retrofit_and_get_response("driving") }

        val btnWalk = findViewById(R.id.btnWalk) as Button
        btnWalk.setOnClickListener { build_retrofit_and_get_response("walking") }
    }

    private fun build_retrofit_and_get_response(type: String) {

        val url = "https://maps.googleapis.com/maps/"

        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service = retrofit.create<RetrofitMaps>(RetrofitMaps::class.java!!)

        val call = service.getDistanceDuration("metric", origin.latitude + "," + origin.longitude, dest.latitude + "," + dest.longitude, type)

        call.enqueue(object : Callback<Example> {
            override fun onResponse(response: Response<Example>, retrofit: Retrofit) {

                try {
                    //Remove previous line from map
                    if (line != null) {
                        line!!.remove()
                    }
                    // This loop will go through all the results and add marker on each location.
                    for (i in 0 until response.body().routes.size) {
                        val distance = response.body().routes[i].legs[i].distance.text
                        val time = response.body().routes[i].legs[i].duration.text
                        ShowDistanceDuration.text = "Distance:$distance, Duration:$time"
                        val encodedString = response.body().routes[0].overviewPolyline.points
                        val list = decodePoly(encodedString)
                        line = mMap!!.addPolyline(PolylineOptions()
                                .addAll(list)
                                .width(20)
                                .color(Color.RED)
                                .geodesic(true)
                        )
                    }
                } catch (e: Exception) {
                    Log.d("onResponse", "There is an error")
                    e.printStackTrace()
                }

            }

            override fun onFailure(t: Throwable) {
                Log.d("onFailure", t.toString())
            }
        })

    }

    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                    lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }

    fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION)


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION)
            }
            return false
        } else {
            return true
        }
    }

    companion object {

        val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }

}
