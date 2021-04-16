package com.example.friends_app.GUI

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.friends_app.Model.BEFriend
import com.example.friends_app.R
import com.example.friends_app.data.FriendRepository

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlin.properties.Delegates

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var location: LatLng? = null
    private val LOCATION_PERMISSION_REQUEST = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        FriendRepository.initialize(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLocationPermission()
        val extras: Bundle = intent.extras!!
        var newMarker = extras.getBoolean("newMarker")
        var showLocation = extras.getBoolean("showLocation")
        if (newMarker) {
            if (location != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
            }
            mMap.setOnMapClickListener { pos ->
                mMap.clear()
                location = pos
                mMap.addMarker(MarkerOptions().position(location!!))
            }
        }

        if (!newMarker) {
            Log.e("xyz", "Opened global map")
            btnSaveLocation.visibility = View.INVISIBLE
            val mRep = FriendRepository.get()
            var allFriends = mRep.getAll()
            allFriends.observe(this, Observer {
                for (friend in it) {
                    var convertedLocation = LatLng(friend.latitude, friend.longitude)
                    mMap.addMarker(MarkerOptions().position(convertedLocation).title(friend.name))
                }
            })
        }

        if (showLocation) {
            var f = extras.getSerializable("selectedFriend") as BEFriend
            var location = LatLng(f.latitude, f.longitude)
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(location).title(f.name))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f))
        }
    }

    fun onSaveLocation(view: View) {
        if (location != null) {
            val intent = Intent()
            intent.putExtra("newLocation", location)
            setResult(50, intent)
            finish()
            Log.e("xyz", location.toString())
        } else if(location == null){
            Log.e("xyz", "location is null???")
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode === LOCATION_PERMISSION_REQUEST) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
                mMap.isMyLocationEnabled = true
            } else {
                Log.e("xyz", "Permission denied")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}