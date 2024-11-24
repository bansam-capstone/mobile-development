package com.bangkit.capstone.presentation.view.maps

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bangkit.capstone.R
import com.bangkit.capstone.common.Resource
import com.bangkit.capstone.data.remote.response.LocationResponse
import com.bangkit.capstone.databinding.FragmentMapsBinding
import com.bangkit.capstone.domain.model.LocationInfo
import com.bangkit.capstone.presentation.viewmodel.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null

    private val args: MapsFragmentArgs by navArgs()
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var identifier: String? = null

    private val initialLocation = LatLng(-0.502106, 117.153709)

    private val mapsViewModel: MapsViewModel by viewModels()

    private lateinit var currentMarkerPosition: LatLng
    private var bottomSheetDialog: BottomSheetDialog? = null

    private val locations = listOf(
        LocationInfo("slamet-riyadi", LatLng(-0.5098581857545632, 117.1178542019155), "slamet-riyadi", null),
        LocationInfo("antasari", LatLng(-0.49186601488572806, 117.12722378180521), "antasari", "https://diskominfo.samarindakota.go.id/api/cctv/simpang-antasari-siradj-salman"),
        LocationInfo("simpang-agus-salim", LatLng(-0.4957041096360274, 117.14971318603816), "simpang-agus-salim", "https://diskominfo.samarindakota.go.id/api/cctv/simpang-4-agus-salim"),
        LocationInfo("simpang-lembuswana", LatLng(-0.4754107332727611, 117.14615018774853), "simpang-lembuswana", "https://diskominfo.samarindakota.go.id/api/cctv/simpang-lembuswana-m-yamin"),
        LocationInfo("mugirejo", LatLng(-0.4687086559524597, 117.19277093628588), "mugirejo", "https://diskominfo.samarindakota.go.id/api/cctv/simpang-mugirejo"),
        LocationInfo("kapten-sudjono", LatLng(-0.5259576904539937, 117.16653946879711), "kapten-sudjono", null),
        LocationInfo("brigjend-katamso", LatLng(-0.4821629316468126, 117.16130648629576), "brigjend-katamso", null),
        LocationInfo("gatot-subroto", LatLng(-0.484634868556901, 117.15525241253552), "gatot-subroto", null),
        LocationInfo("cendana", LatLng(-0.500252081801295, 117.11931456511012), "cendana", null),
        LocationInfo("di-panjaitan", LatLng(-0.4616283811244264, 117.18572338299191), "di-panjaitan", null),
        LocationInfo("damanhuri", LatLng(-0.4726480049586589, 117.18089748709794), "damanhuri", null),
        LocationInfo("pertigaan-pramuka-perjuangan", LatLng(-0.4648328326253432, 117.15584721398068), "pertigaan-pramuka-perjuangan", "https://diskominfo.samarindakota.go.id/api/cctv/tps-jalan-pramuka"),
        LocationInfo("padat-karya-sempaja-simpang-wanyi", LatLng(-0.424829289116985, 117.15882745064134), "padat-karya-sempaja-simpang-wanyi", null),
        LocationInfo("simpang-sempaja", LatLng(-0.4500742226015745, 117.15303878168255), "simpang-sempaja", "https://diskominfo.samarindakota.go.id/api/cctv/simpang-sempaja"),
        LocationInfo("ir-h-juanda", LatLng(-0.472740909178976, 117.13824418741677), "ir-h-juanda", "https://diskominfo.samarindakota.go.id/api/cctv/fly-over-sisi-juanda"),
        LocationInfo("tengkawang", LatLng(-0.5016990420031888, 117.11437249596959), "tengkawang", null),
        LocationInfo("sukorejo", LatLng(-0.4317621005498969, 117.19535493819562), "sukorejo", null),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latitude = it.getDouble(ARG_LATITUDE, Double.NaN)
            longitude = it.getDouble(ARG_LONGITUDE, Double.NaN)
            identifier = it.getString(ARG_IDENTIFIER) ?: "default"

            if (latitude.isNaN() || longitude.isNaN()) {
                latitude = 0.0
                longitude = 0.0
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        setupSearchViewNavigation()
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setupMapUI(mMap)
        addMarkers()
        setMapStyle()
        locationFromPrediction()
        locationFromSearch()
//        loadGeoJsonLayer()

        mMap.setOnMarkerClickListener { marker ->
            val selectedIdentifier  = marker.title
            if (selectedIdentifier  != null) {
                currentMarkerPosition = marker.position
                identifier = selectedIdentifier
                showBottomSheetDialog()
                mapsViewModel.getWeatherTodayByIdentifier(selectedIdentifier )
                mapsViewModel.getWeatherTommorowByIdentifier(selectedIdentifier )
            } else {
                Toast.makeText(requireContext(), "Lokasi yang dipilih!", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun locationFromSearch(){
        val lat = args.latitude
        val long = args.longitude

        if (lat != 0.0f && long != 0.0f) {
            val selectedLocation = LatLng(lat.toDouble(), long.toDouble())
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 13f))
            mMap.addMarker(
                MarkerOptions()
                    .position(selectedLocation))
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(initialLocation))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 13f))
        }
    }

    private fun locationFromPrediction(){
        if (identifier != null && this.latitude != 0.0 && this.longitude != 0.0) {
            val selectedLocation = LatLng(this.latitude, this.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 13f))
            mMap.addMarker(
                MarkerOptions()
                    .position(selectedLocation)
                    .title(identifier)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )

            currentMarkerPosition = selectedLocation
            showBottomSheetDialog()
            mapsViewModel.getWeatherTodayByIdentifier(identifier!!)
            mapsViewModel.getWeatherTommorowByIdentifier(identifier!!)
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(initialLocation))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 13f))
        }
    }

    private fun loadGeoJsonLayer() {
        try {
            val geoJsonLayer = GeoJsonLayer(mMap, R.raw.kota_samarinda, requireContext())

            val polygonStyle: GeoJsonPolygonStyle = geoJsonLayer.defaultPolygonStyle
            polygonStyle.fillColor = ContextCompat.getColor(requireContext(), R.color.polygon_fill_color)
            polygonStyle.strokeColor = ContextCompat.getColor(requireContext(), R.color.polygon_stroke_color)
            polygonStyle.strokeWidth = 2f

            geoJsonLayer.addLayerToMap()

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Gagal memuat batas wilayah.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeTommorowWeather(
        statusIcon: ImageView,
        tvFloodStatus: TextView,
        tvWindSpeed: TextView,
        tvHumidity: TextView,
        tvPressure: TextView,
        tvLocation: TextView,
        tvLocationDescription: TextView
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mapsViewModel.weatherTommorowData.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            showLoadingContent()
                        }
                        is Resource.Success -> {
                            val weatherResponse = resource.data
                            val address = getAddressFromLatLng(currentMarkerPosition)
                            if (address != null && weatherResponse != null && weatherResponse.isNotEmpty()) {
                                updateBottomSheet(
                                    address = address,
                                    weather = weatherResponse[0],
                                    statusIcon = statusIcon,
                                    tvFloodStatus = tvFloodStatus,
                                    tvWindSpeed = tvWindSpeed,
                                    tvHumidity = tvHumidity,
                                    tvPressure = tvPressure,
                                    tvLocation = tvLocation,
                                    tvLocationDescription = tvLocationDescription
                                )
                                moveCameraToPosition(currentMarkerPosition)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Gagal mengambil data dari koordinat.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dismissBottomSheet()
                            }
                        }
                        is Resource.Error -> {
                            Toast.makeText(
                                requireContext(),
                                resource.message ?: "Terjadi kesalahan saat mengambil data cuaca.",
                                Toast.LENGTH_SHORT
                            ).show()
                            dismissBottomSheet()
                        }
                    }
                }
            }
        }
    }


    private fun observeTodayWeather(
        statusIcon: ImageView,
        tvFloodStatus: TextView,
        tvWindSpeed: TextView,
        tvHumidity: TextView,
        tvPressure: TextView,
        tvLocation: TextView,
        tvLocationDescription: TextView
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mapsViewModel.weatherTodayData.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            showLoadingContent()
                        }
                        is Resource.Success -> {
                            val weatherResponse = resource.data
                            val address = getAddressFromLatLng(currentMarkerPosition)
                            if (address != null && weatherResponse != null && weatherResponse.isNotEmpty()) {
                                updateBottomSheet(
                                    address = address,
                                    weather = weatherResponse[0],
                                    statusIcon = statusIcon,
                                    tvFloodStatus = tvFloodStatus,
                                    tvWindSpeed = tvWindSpeed,
                                    tvHumidity = tvHumidity,
                                    tvPressure = tvPressure,
                                    tvLocation = tvLocation,
                                    tvLocationDescription = tvLocationDescription
                                )
                                moveCameraToPosition(currentMarkerPosition)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Gagal mengambil data dari koordinat.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dismissBottomSheet()
                            }
                        }
                        is Resource.Error -> {
                            Toast.makeText(
                                requireContext(),
                                resource.message ?: "Terjadi kesalahan saat mengambil data cuaca.",
                                Toast.LENGTH_SHORT
                            ).show()
                            dismissBottomSheet()
                        }
                    }
                }
            }
        }
    }

    private fun showLoadingContent(sheetView: View) {
        sheetView.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        sheetView.findViewById<TextView>(R.id.tvLocation).visibility = View.GONE
        sheetView.findViewById<TextView>(R.id.tvLocationDescription).visibility = View.GONE
        sheetView.findViewById<TextView>(R.id.tvWindSpeed).visibility = View.GONE
        sheetView.findViewById<TextView>(R.id.tvHumidity).visibility = View.GONE
        sheetView.findViewById<TextView>(R.id.tvPressure).visibility = View.GONE
        sheetView.findViewById<ImageView>(R.id.statusIcon).visibility = View.GONE
        sheetView.findViewById<TextView>(R.id.tvFloodStatus).visibility = View.GONE
    }

    private fun showLoadingContent() {
        bottomSheetDialog?.findViewById<ProgressBar>(R.id.progressBar)?.visibility = View.VISIBLE
        bottomSheetDialog?.findViewById<TextView>(R.id.tvLocation)?.visibility = View.GONE
        bottomSheetDialog?.findViewById<TextView>(R.id.tvLocationDescription)?.visibility = View.GONE
        bottomSheetDialog?.findViewById<TextView>(R.id.tvWindSpeed)?.visibility = View.GONE
        bottomSheetDialog?.findViewById<TextView>(R.id.tvHumidity)?.visibility = View.GONE
        bottomSheetDialog?.findViewById<TextView>(R.id.tvPressure)?.visibility = View.GONE
        bottomSheetDialog?.findViewById<ImageView>(R.id.statusIcon)?.visibility = View.GONE
        bottomSheetDialog?.findViewById<TextView>(R.id.tvFloodStatus)?.visibility = View.GONE
    }

    private fun showBottomSheetDialog() {
        bottomSheetDialog?.dismiss()

        bottomSheetDialog = BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        bottomSheetDialog?.setContentView(sheetView)

        val tabLayout = sheetView.findViewById<TabLayout>(R.id.tabLayout)
        val statusIcon = sheetView.findViewById<ImageView>(R.id.statusIcon)
        val tvFloodStatus = sheetView.findViewById<TextView>(R.id.tvFloodStatus)
        val tvWindSpeed = sheetView.findViewById<TextView>(R.id.tvWindSpeed)
        val tvHumidity = sheetView.findViewById<TextView>(R.id.tvHumidity)
        val tvPressure = sheetView.findViewById<TextView>(R.id.tvPressure)
        val tvLocation = sheetView.findViewById<TextView>(R.id.tvLocation)
        val tvLocationDescription = sheetView.findViewById<TextView>(R.id.tvLocationDescription)
        val btnViewCCTV = sheetView.findViewById<View>(R.id.btnViewCCTV)

        showLoadingContent(sheetView)

        setupTabLayout(tabLayout)

        val defaultTab = tabLayout.getTabAt(0)
        defaultTab?.select()
        defaultTab?.view?.setBackgroundResource(R.drawable.bg_tab_active)

        observeTodayWeather(
            statusIcon = statusIcon,
            tvFloodStatus = tvFloodStatus,
            tvWindSpeed = tvWindSpeed,
            tvHumidity = tvHumidity,
            tvPressure = tvPressure,
            tvLocation = tvLocation,
            tvLocationDescription = tvLocationDescription
        )

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.view?.setBackgroundResource(R.drawable.bg_tab_active)
                when (tab?.position) {
                    0 -> {
                        observeTodayWeather(
                            statusIcon = statusIcon,
                            tvFloodStatus = tvFloodStatus,
                            tvWindSpeed = tvWindSpeed,
                            tvHumidity = tvHumidity,
                            tvPressure = tvPressure,
                            tvLocation = tvLocation,
                            tvLocationDescription = tvLocationDescription
                        )
                    }
                    1 -> {
                        observeTommorowWeather(
                            statusIcon = statusIcon,
                            tvFloodStatus = tvFloodStatus,
                            tvWindSpeed = tvWindSpeed,
                            tvHumidity = tvHumidity,
                            tvPressure = tvPressure,
                            tvLocation = tvLocation,
                            tvLocationDescription = tvLocationDescription
                        )
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view?.setBackgroundResource(0)
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        bottomSheetDialog?.show()

        val location = locations.find { it.identifier == identifier }
        if (location?.cctvLink != null) {
            btnViewCCTV?.isEnabled = true
            btnViewCCTV?.alpha = 1f
            btnViewCCTV?.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(location.cctvLink))
                startActivity(intent)
            }
        } else {
            btnViewCCTV?.isEnabled = false
            btnViewCCTV?.alpha = 0.5f
            btnViewCCTV?.setOnClickListener(null)
        }

    }

    private fun setupTabLayout(tabLayout: TabLayout) {
        tabLayout.removeAllTabs()

        val tabTitles = listOf("Hari Ini", "Besok")
        tabTitles.forEach { title ->
            val tab = tabLayout.newTab().setText(title)
            tabLayout.addTab(tab)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.view?.setBackgroundResource(R.drawable.bg_tab_active)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view?.setBackgroundResource(0)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    @SuppressLint("CutPasteId")
    private fun updateBottomSheet(
        address: Address,
        weather: LocationResponse,
        statusIcon: ImageView,
        tvFloodStatus: TextView,
        tvWindSpeed: TextView,
        tvHumidity: TextView,
        tvPressure: TextView,
        tvLocation: TextView,
        tvLocationDescription: TextView
    ) {
        bottomSheetDialog?.findViewById<ProgressBar>(R.id.progressBar)?.visibility = View.GONE

        tvLocation.visibility = View.VISIBLE
        tvLocationDescription.visibility = View.VISIBLE
        tvWindSpeed.visibility = View.VISIBLE
        tvHumidity.visibility = View.VISIBLE
        tvPressure.visibility = View.VISIBLE
        statusIcon.visibility = View.VISIBLE
        tvFloodStatus.visibility = View.VISIBLE

        val streetName = address.thoroughfare ?: address.getAddressLine(0) ?: "Nama Jalan Tidak Tersedia"
        val city = address.locality ?: "Kota Tidak Diketahui"

        tvLocation.text = streetName
        tvLocationDescription.text = city

        tvWindSpeed.text = weather.windSpeed?.let { "$it m/s" } ?: "N/A"
        tvHumidity.text = weather.humidity?.let { "$it%" } ?: "N/A"
        tvPressure.text = weather.pressure?.let { "$it hPa" } ?: "N/A"

        weather.riskLevel?.let {
            updateRiskLevelIcon(it, statusIcon, tvFloodStatus)
        }
    }

    private fun moveCameraToPosition(latLng: LatLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    private fun dismissBottomSheet() {
        bottomSheetDialog?.dismiss()
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
            isTiltGesturesEnabled = true
        }
    }

    private fun addMarkers() {
        for (location in locations) {
            val markerOptions = MarkerOptions()
                .position(location.latLng)
                .title(location.identifier)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))

            val marker = mMap.addMarker(markerOptions)
            marker?.tag = location.identifier
        }
    }

    private fun updateRiskLevelIcon(riskLevel: String, imageView: ImageView, textView: TextView) {
        when (riskLevel.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }) {
            "Aman" -> {
                imageView.setImageResource(R.drawable.ic_prediction_safe)
                textView.text = "Prediksi Banjir: Aman"
            }
            "Waspada" -> {
                imageView.setImageResource(R.drawable.ic_prediction_warning)
                textView.text = "Prediksi Banjir: Waspada"
            }
            "Bahaya" -> {
                imageView.setImageResource(R.drawable.ic_prediction_danger)
                textView.text = "Prediksi Banjir: Bahaya"
            }
            else -> {
                imageView.setImageResource(R.drawable.ic_prediction_warning)
                textView.text = "Prediksi Banjir: Tidak Diketahui"
            }
        }
    }

    private suspend fun getAddressFromLatLng(latLng: LatLng): Address? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    addresses[0]
                } else {
                    null
                }
            } catch (e: IOException) {
                null
            } catch (e: IllegalArgumentException) {
                null
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bottomSheetDialog?.dismiss()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    companion object {
        private const val ARG_LATITUDE = "latitude"
        private const val ARG_LONGITUDE = "longitude"
        private const val ARG_IDENTIFIER = "identifier"

        fun newInstance(latitude: Double, longitude: Double, identifier: String?): MapsFragment {
            val fragment = MapsFragment()
            val args = Bundle()
            args.putDouble(ARG_LATITUDE, latitude)
            args.putDouble(ARG_LONGITUDE, longitude)
            identifier?.let { args.putString(ARG_IDENTIFIER, it) }
            fragment.arguments = args
            return fragment
        }
    }
}
