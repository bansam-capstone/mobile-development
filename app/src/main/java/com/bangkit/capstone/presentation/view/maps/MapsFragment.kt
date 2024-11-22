package com.bangkit.capstone.presentation.view.maps

import android.annotation.SuppressLint
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TableLayout
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
//import com.google.maps.android.data.geojson.GeoJsonLayer
//import com.google.maps.android.data.geojson.GeoJsonPolygonStyle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
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
        LocationInfo("slamet-riyadi", LatLng(-0.5098581857545632, 117.1178542019155), "slamet-riyadi"),
        LocationInfo("antasari", LatLng(-0.49186601488572806, 117.12722378180521), "antasari"),
        LocationInfo("simpang-agus-salim", LatLng(-0.4957041096360274, 117.14971318603816), "simpang-agus-salim"),
        LocationInfo("simpang-lembuswana", LatLng(-0.4754107332727611, 117.14615018774853), "simpang-lembuswana"),
        LocationInfo("mugirejo", LatLng(-0.4687086559524597, 117.19277093628588), "mugirejo"),
        LocationInfo("kapten-sudjono", LatLng(-0.5259576904539937, 117.16653946879711), "kapten-sudjono"),
        LocationInfo("brigjend-katamso", LatLng(-0.4821629316468126, 117.16130648629576), "brigjend-katamso"),
        LocationInfo("gatot-subroto", LatLng(-0.484634868556901, 117.15525241253552), "gatot-subroto"),
        LocationInfo("cendana", LatLng(-0.500252081801295, 117.11931456511012), "cendana"),
        LocationInfo("di-panjaitan", LatLng(-0.4616283811244264, 117.18572338299191), "di-panjaitan"),
        LocationInfo("damanhuri", LatLng(-0.4726480049586589, 117.18089748709794), "damanhuri"),
        LocationInfo("pertigaan-pramuka-perjuangan", LatLng(-0.4648328326253432, 117.15584721398068), "pertigaan-pramuka-perjuangan"),
        LocationInfo("padat-karya-sempaja-simpang-wanyi", LatLng(-0.424829289116985, 117.15882745064134), "padat-karya-sempaja-simpang-wanyi"),
        LocationInfo("simpang-sempaja", LatLng(-0.4500742226015745, 117.15303878168255), "simpang-sempaja"),
        LocationInfo("ir-h-juanda", LatLng(-0.472740909178976, 117.13824418741677), "ir-h-juanda"),
        LocationInfo("tengkawang", LatLng(-0.5016990420031888, 117.11437249596959), "tengkawang"),
        LocationInfo("sukorejo", LatLng(-0.4317621005498969, 117.19535493819562), "sukorejo")
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
        observeWeatherViewModel()
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
            val identifier = marker.title
            if (identifier != null) {
                currentMarkerPosition = marker.position
                showBottomSheetDialog()

                mapsViewModel.getWeatherByIdentifier(identifier)
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
            mapsViewModel.getWeatherByIdentifier(identifier!!)
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(initialLocation))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 13f))
        }
    }

//    private fun loadGeoJsonLayer() {
//        try {
//            val geoJsonLayer = GeoJsonLayer(mMap, R.raw.kota_samarinda, requireContext())
//
//            val polygonStyle: GeoJsonPolygonStyle = geoJsonLayer.defaultPolygonStyle
//            polygonStyle.fillColor = ContextCompat.getColor(requireContext(), R.color.polygon_fill_color)
//            polygonStyle.strokeColor = ContextCompat.getColor(requireContext(), R.color.polygon_stroke_color)
//            polygonStyle.strokeWidth = 2f
//
//            geoJsonLayer.addLayerToMap()
//
//        } catch (e: Exception) {
//            Toast.makeText(requireContext(), "Gagal memuat batas wilayah.", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun observeWeatherViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mapsViewModel.weatherData.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            val weatherResponse = resource.data
                            val address = getAddressFromLatLng(currentMarkerPosition)
                            if (address != null && weatherResponse != null && weatherResponse.isNotEmpty()) {
                                updateBottomSheet(address, weatherResponse[0])
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

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val tabLayout = bottomSheetView.findViewById<TabLayout>(R.id.tabLayout)
        val statusIcon = bottomSheetView.findViewById<ImageView>(R.id.statusIcon)
        val tvFloodStatus = bottomSheetView.findViewById<TextView>(R.id.tvFloodStatus)
        val tvWindSpeed = bottomSheetView.findViewById<TextView>(R.id.tvWindSpeed)
        val tvHumidity = bottomSheetView.findViewById<TextView>(R.id.tvHumidity)
        val tvPressure = bottomSheetView.findViewById<TextView>(R.id.tvPressure)

        setupTabLayout(tabLayout)

        updateContent(
            statusIcon = statusIcon,
            tvFloodStatus = tvFloodStatus,
            tvWindSpeed = tvWindSpeed,
            tvHumidity = tvHumidity,
            tvPressure = tvPressure,
            isToday = true
        )

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> updateContent(
                        statusIcon = statusIcon,
                        tvFloodStatus = tvFloodStatus,
                        tvWindSpeed = tvWindSpeed,
                        tvHumidity = tvHumidity,
                        tvPressure = tvPressure,
                        isToday = true
                    )
                    1 -> updateContent(
                        statusIcon = statusIcon,
                        tvFloodStatus = tvFloodStatus,
                        tvWindSpeed = tvWindSpeed,
                        tvHumidity = tvHumidity,
                        tvPressure = tvPressure,
                        isToday = false
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        bottomSheetDialog.show()
    }

    private fun updateContent(
        statusIcon: ImageView,
        tvFloodStatus: TextView,
        tvWindSpeed: TextView,
        tvHumidity: TextView,
        tvPressure: TextView,
        isToday: Boolean
    ) {
        if (isToday) {
            // Update untuk tab "Hari Ini"
            statusIcon.setImageResource(R.drawable.ic_prediction_safe)
            tvFloodStatus.text = "Status Banjir: Aman"
            tvWindSpeed.text = "1.54 m/s"
            tvHumidity.text = "90%"
            tvPressure.text = "1010 hPa"
        } else {
            // Update untuk tab "Besok"
            statusIcon.setImageResource(R.drawable.ic_prediction_warning)
            tvFloodStatus.text = "Status Banjir: Waspada"
            tvWindSpeed.text = "1.78 m/s"
            tvHumidity.text = "85%"
            tvPressure.text = "1008 hPa"
        }
    }

    private fun setupTabLayout(tabLayout: TabLayout) {
        val tabTitles = listOf("Hari Ini", "Besok")
        tabTitles.forEach { title ->
            val tab = tabLayout.newTab().setText(title)
            tabLayout.addTab(tab)
        }

        tabLayout.getTabAt(0)?.view?.setBackgroundResource(R.drawable.bg_tab_active)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.view?.setBackgroundResource(R.drawable.bg_tab_active)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view?.setBackgroundResource(0)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    @SuppressLint("CutPasteId")
    private fun updateBottomSheet(address: Address, weather: LocationResponse) {
        bottomSheetDialog?.let { dialog ->
            val sheetView = dialog.findViewById<LinearLayout>(R.id.bottomSheetLayout)
            sheetView?.let {
                with(it) {
                    findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    findViewById<TextView>(R.id.tvLocation).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.tvLocationDescription).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.tvWindSpeed).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.tvHumidity).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.tvPressure).visibility = View.VISIBLE
                    findViewById<ImageView>(R.id.statusIcon).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.tvFloodStatus).visibility = View.VISIBLE

                    val streetName = address.thoroughfare ?: address.getAddressLine(0) ?: "Nama Jalan Tidak Tersedia"
                    val city = address.locality ?: "Kota Tidak Tersedia"

                    findViewById<TextView>(R.id.tvLocation).text = streetName
                    findViewById<TextView>(R.id.tvLocationDescription).text = city

                    findViewById<TextView>(R.id.tvWindSpeed).text = weather.windSpeed?.let { "${it} m/s" } ?: "N/A"
                    findViewById<TextView>(R.id.tvHumidity).text = weather.humidity?.let { "${it}%" } ?: "N/A"
                    findViewById<TextView>(R.id.tvPressure).text = weather.pressure?.let { "${it} hPa" } ?: "N/A"

                    weather.riskLevel?.let { updateRiskLevelIcon(it, findViewById(R.id.statusIcon), findViewById(R.id.tvFloodStatus)) }
                }
            }
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
                Log.e("MapsFragment", "Geocoder IO Exception: ${e.localizedMessage}")
                null
            } catch (e: IllegalArgumentException) {
                Log.e("MapsFragment", "Geocoder Illegal Argument: ${e.localizedMessage}")
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
