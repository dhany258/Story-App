package com.dicoding.storyapp.ui.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.response.ListStoryItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.dicoding.storyapp.databinding.ActivityMapsBinding
import com.dicoding.storyapp.utilis.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mapViewModel: MapViewModel by viewModels {
        ViewModelFactory(this@MapsActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.hide()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        mapViewModel.getStoriesWithLocation().observe(this) {
            if (it != null) {
                when(it) {
                    is Result.Success -> {
                        addManyMarker(it.data.listStory)
                    }
                    is Result.Loading -> {

                    }
                    is Result.Error -> {
                        Toast.makeText(this, it.error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private val boundsBuilder = LatLngBounds.Builder()

    private fun addManyMarker(stories: List<ListStoryItem>) {
        stories.forEach { tourism ->
            val latLng = LatLng(tourism.lat ?: 0.0, tourism.lon ?: 0.0)
            mMap.addMarker(MarkerOptions().position(latLng).title(tourism.name).snippet(tourism.description))
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()

        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                30
            )
        )
    }

}