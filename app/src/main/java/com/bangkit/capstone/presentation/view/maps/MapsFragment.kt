package com.bangkit.capstone.presentation.view.maps

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bangkit.capstone.R
import com.bangkit.capstone.common.Resource
import com.bangkit.capstone.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapsBinding
    private lateinit var mMap: GoogleMap

    private val args: MapsFragmentArgs by navArgs()

    private val initialLocation = LatLng(-0.502106, 117.153709)

    private val locations = listOf(
        "Jalan Slamet Riyadi" to LatLng(-0.5098581857545632, 117.1178542019155),
        "Jalan Antasari" to LatLng(-0.49186601488572806, 117.12722378180521),
        "Simpang Agus Salim" to LatLng(-0.4957041096360274, 117.14971318603816),
        "Simpang Lembuswana" to LatLng(-0.4754107332727611, 117.14615018774853),
        "Jalan Mugirejo" to LatLng(-0.4687086559524597, 117.19277093628588),
        "Jalan Kapten Sudjono" to LatLng(-0.5259576904539937, 117.16653946879711),
        "Jalan Brigjend Katamso" to LatLng(-0.4821629316468126, 117.16130648629576),
        "Jalan Gatot Subroto" to LatLng(-0.484634868556901, 117.15525241253552),
        "Jalan Cendana" to LatLng(-0.4987184574034962, 117.12151672396949),
        "Jalan DI Panjaitan" to LatLng(-0.4616283811244264, 117.18572338299191),
        "Jalan Damanhuri" to LatLng(-0.4726480049586589, 117.18089748709794),
        "Jalan Pertigaan Pramuka Perjuangan" to LatLng(-0.4648328326253432, 117.15584721398068),
        "Jalan Padat Karya Sempaja-Simpang Wanyi" to LatLng(-0.424829289116985, 117.15882745064134),
        "Simpang Sempaja" to LatLng(-0.4500742226015745, 117.15303878168255),
        "Jalan Ir H Juanda (Fly Over)" to LatLng(-0.472740909178976, 117.13824418741677),
        "Jalan Tengkawang" to LatLng(-0.5016990420031888, 117.11437249596959),
        "Jalan Sukorejo" to LatLng(-0.4317621005498969, 117.19535493819562)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        setupSearchViewNavigation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setupMapUI(mMap)
        addMarkers()
        setMapStyle()

        val latitude = args.latitude
        val longitude = args.longitude

        if (latitude != 0.0f && longitude != 0.0f) {
            val selectedLocation = LatLng(latitude.toDouble(), longitude.toDouble())
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 12f))
            mMap.addMarker(MarkerOptions().position(selectedLocation).title("Lokasi yang dipilih!"))
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(initialLocation))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12f))
        }

        mMap.setOnMarkerClickListener { marker ->
            marker.title?.let {
                showBottomSheet(it, "Deskripsi untuk ${marker.title}")
            }
            true
        }
    }

    private fun setupSearchViewNavigation() {
        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                findNavController().navigate(R.id.action_mapsFragment_to_searchFragment)
            }
        }

        binding.searchView.setOnClickListener {
            findNavController().navigate(R.id.action_mapsFragment_to_searchFragment)
        }
    }

    private fun setupMapUI(map: GoogleMap) {
        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
            isScrollGesturesEnabled = true
            isRotateGesturesEnabled = true
            isTiltGesturesEnabled = true
        }
    }

    private fun addMarkers() {
        for ((title, latLng) in locations) {
            val bitmapDescriptor = getBitmapFromVectorDrawable(R.drawable.ic_marker_warning)

            val markerOptions = MarkerOptions()
                .position(latLng)
                .title(title)

            bitmapDescriptor?.let {
                markerOptions.icon(it)
            }

            mMap.addMarker(markerOptions)
        }
    }

    private fun getBitmapFromVectorDrawable(drawableId: Int): BitmapDescriptor? {
        val drawable = ContextCompat.getDrawable(requireContext(), drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun showBottomSheet(title: String, description: String) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)

        val titleTextView = sheetView.findViewById<TextView>(R.id.title)
        val descriptionTextView = sheetView.findViewById<TextView>(R.id.description)

        titleTextView.text = title
        descriptionTextView.text = description

        bottomSheetDialog.setContentView(sheetView)
        bottomSheetDialog.show()
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
            )
            if (!success) {
                Toast.makeText(requireContext(), "Gagal mengatur style map", Toast.LENGTH_SHORT).show()
            }
        } catch (exception: Resources.NotFoundException) {
           Toast.makeText(requireContext(), "Tidak dapat menemukan style map", Toast.LENGTH_SHORT).show()
        }
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
