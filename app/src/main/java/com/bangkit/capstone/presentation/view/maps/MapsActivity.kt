package com.bangkit.capstone.presentation.view.maps

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.R
import com.bangkit.capstone.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isScrollGesturesEnabled = true
        mMap.uiSettings.isRotateGesturesEnabled = true
        mMap.uiSettings.isTiltGesturesEnabled = true

        addMarkers()

        mMap.setOnMarkerClickListener { marker ->
            marker.title?.let {
                showBottomSheet(it, "Deskripsi untuk ${marker.title}")
            }
            true
        }

        val initialLocation = LatLng(-0.502106, 117.153709)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(initialLocation))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12f))
    }

    private fun addMarkers() {
        val locations = listOf(
            Pair("Jalan Slamet Riyadi", LatLng(-0.5098581857545632, 117.1178542019155)),
            Pair("Jalan Antasari", LatLng(-0.49186601488572806, 117.12722378180521)),
            Pair("Simpang Agus Salim", LatLng(-0.4957041096360274, 117.14971318603816)),
            Pair("Simpang Lembuswana", LatLng(-0.4754107332727611, 117.14615018774853)),
            Pair("Jalan Mugirejo", LatLng(-0.4687086559524597, 117.19277093628588)),
            Pair("Jalan Kapten Sudjono", LatLng(-0.5259576904539937, 117.16653946879711)),
            Pair("Jalan Brigjend Katamso", LatLng(-0.4821629316468126, 117.16130648629576)),
            Pair("Jalan Gatot Subroto", LatLng(-0.484634868556901, 117.15525241253552)),
            Pair("Jalan Cendana", LatLng(-0.4987184574034962, 117.12151672396949)),
            Pair("Jalan DI Panjaitan", LatLng(-0.4616283811244264, 117.18572338299191)),
            Pair("Jalan Damanhuri", LatLng(-0.4726480049586589, 117.18089748709794)),
            Pair("Jalan Pertigaan Pramuka Perjuangan", LatLng(-0.4648328326253432, 117.15584721398068)),
            Pair("Jalan Padat Karya Sempaja-Simpang Wanyi", LatLng(-0.424829289116985, 117.15882745064134)),
            Pair("Simpang Sempaja", LatLng(-0.4500742226015745, 117.15303878168255)),
            Pair("Jalan Ir H Juanda (Fly Over)", LatLng(-0.472740909178976, 117.13824418741677)),
            Pair("Jalan Tengkawang", LatLng(-0.5016990420031888, 117.11437249596959)),
            Pair("Jalan Sukorejo", LatLng(-0.4317621005498969, 117.19535493819562))
        )

        for (location in locations) {
            mMap.addMarker(MarkerOptions().position(location.second).title(location.first))
        }
    }

    private fun showBottomSheet(title: String, description: String) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)

        val titleTextView = sheetView.findViewById<TextView>(R.id.title)
        val descriptionTextView = sheetView.findViewById<TextView>(R.id.description)

        titleTextView.text = title
        descriptionTextView.text = description

        bottomSheetDialog.setContentView(sheetView)
        bottomSheetDialog.show()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}
