package com.bangkit.capstone.presentation.view.maps

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.bangkit.capstone.util.GeofenceBroadcastReceiver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
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

    private var isGeoJsonActive = false
    private var geoJsonPolygonList = mutableListOf<Polygon>()

    private lateinit var geofencingClient: GeofencingClient
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
    }

    private val geofenceList = mutableListOf<Geofence>()

    private val mapsViewModel: MapsViewModel by viewModels()

    private var currentMarkerPosition: LatLng? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Izin lokasi diberikan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Izin lokasi tidak diberikan", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestNotificationLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Izin notifikasi diberikan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Izin notifikasi tidak diberikan", Toast.LENGTH_SHORT).show()
            }
        }

    private val locations = listOf(
        LocationInfo("Jl. Slamet Riyadi", LatLng(-0.5098581857545632, 117.1178542019155), "slamet-riyadi", null),
        LocationInfo("Jl. Antasari", LatLng(-0.49186601488572806, 117.12722378180521), "antasari", "https://diskominfo.samarindakota.go.id/api/cctv/simpang-antasari-siradj-salman"),
        LocationInfo("Simpang Agus Salim", LatLng(-0.4957041096360274, 117.14971318603816), "simpang-agus-salim", "https://diskominfo.samarindakota.go.id/api/cctv/simpang-4-agus-salim"),
        LocationInfo("Simpang Lembuswana", LatLng(-0.4754107332727611, 117.14615018774853), "simpang-lembuswana", "https://diskominfo.samarindakota.go.id/api/cctv/simpang-lembuswana-m-yamin"),
        LocationInfo("Jl. Mugirejo", LatLng(-0.4687086559524597, 117.19277093628588), "mugirejo", "https://diskominfo.samarindakota.go.id/api/cctv/simpang-mugirejo"),
        LocationInfo("Jl. Kapten Soedjono Aj", LatLng(-0.5259576904539937, 117.16653946879711), "kapten-sudjono", null),
        LocationInfo("Jl. Brigjend Katamso", LatLng(-0.4821629316468126, 117.16130648629576), "brigjend-katamso", null),
        LocationInfo("Jl. Gatot Subroto", LatLng(-0.484634868556901, 117.15525241253552), "gatot-subroto", null),
        LocationInfo("Jl. Cendana", LatLng(-0.500252081801295, 117.11931456511012), "cendana", null),
        LocationInfo("Jl. D.I. Panjaitan", LatLng(-0.4616283811244264, 117.18572338299191), "di-panjaitan", null),
        LocationInfo("Jl. Mugirejo", LatLng(-0.4726480049586589, 117.18089748709794), "damanhuri", null),
        LocationInfo("Pertigaan Pramuka Perjuangan", LatLng(-0.4648328326253432, 117.15584721398068), "pertigaan-pramuka-perjuangan", "https://diskominfo.samarindakota.go.id/api/cctv/tps-jalan-pramuka"),
        LocationInfo("Jl. Padat Karya", LatLng(-0.424829289116985, 117.15882745064134), "padat-karya-sempaja-simpang-wanyi", null),
        LocationInfo("Simpang Sempaja", LatLng(-0.4500742226015745, 117.15303878168255), "simpang-sempaja", "https://diskominfo.samarindakota.go.id/api/cctv/simpang-sempaja"),
        LocationInfo("Simpang Juanda Fly Over", LatLng(-0.472740909178976, 117.13824418741677), "ir-h-juanda", "https://diskominfo.samarindakota.go.id/api/cctv/fly-over-sisi-juanda"),
        LocationInfo("Jl. Tengkawang", LatLng(-0.5016990420031888, 117.11437249596959), "tengkawang", null),
        LocationInfo("Jl. Sukorejo", LatLng(-0.4317621005498969, 117.19535493819562), "sukorejo", null),
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        setupSearchViewNavigation()
        setupGeofencing()

        binding.btnLocation.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                if (isLocationEnabled) {
                    getMyLocation()
                } else {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        binding.btnActiveGeoJson.setOnClickListener {
            val actionText = if (isGeoJsonActive) "menonaktifkan" else "mengaktifkan"
            val confirmationMessage = "Apakah Anda yakin ingin $actionText tampilan peta Samarinda?"

            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi")
                .setMessage(confirmationMessage)
                .setPositiveButton("Ya") { dialog, _ ->
                    if (isGeoJsonActive) {
                        removeGeoJson()
                    } else {
                        loadGeoJson()
                    }
                    isGeoJsonActive = !isGeoJsonActive
                    dialog.dismiss()
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            alertDialog.setOnShowListener {
                val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

                val buttonColor = if (isDarkMode) android.R.color.white else android.R.color.black

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), buttonColor))
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), buttonColor))
            }

            alertDialog.show()
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setupMapUI(mMap)
        addMarkers()
        setMapStyle()
        locationFromPrediction()
        locationFromSearch()

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

    private fun setupGeofencing() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    setupGeofencingClient()
                }
            } else {
                setupGeofencingClient()
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setupGeofencingClient() {
        try {
            geofencingClient = LocationServices.getGeofencingClient(requireContext())

            geofenceList.clear()

            locations.forEach { location ->
                val geofence = Geofence.Builder()
                    .setRequestId(location.identifier)
                    .setCircularRegion(
                        location.latLng.latitude,
                        location.latLng.longitude,
                        700f
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER or
                                Geofence.GEOFENCE_TRANSITION_EXIT
                    )
                    .build()

                geofenceList.add(geofence)
            }

            val geofencingRequest = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geofenceList)
                .build()

            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)

        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), "Izin diperlukan: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    enableLocationFeatures()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
            } else {
                enableLocationFeatures()
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun enableLocationFeatures() {
        try {
            mMap.isMyLocationEnabled = true

            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {

                    val currentLocation = LatLng(it.latitude, it.longitude)
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 17f)
                    mMap.animateCamera(cameraUpdate)
                }
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Gagal mendapatkan lokasi: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), "Izin lokasi diperlukan untuk mengakses lokasi pengguna", Toast.LENGTH_SHORT).show()
        }
    }

    private fun locationFromSearch(){
        val lat = args.latitude
        val long = args.longitude

        if (lat != 0.0f && long != 0.0f) {
            val selectedLocation = LatLng(lat.toDouble(), long.toDouble())
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 13f))
            mMap.addMarker(
                MarkerOptions()
                    .position(selectedLocation))
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12f))
        }
    }

    private fun locationFromPrediction(){
        if (identifier != null && this.latitude != 0.0 && this.longitude != 0.0) {
            val selectedLocation = LatLng(this.latitude, this.longitude)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 13f))
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
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12f))
        }
    }

    private fun loadGeoJson() {
        try {
            val geoJsonStream = resources.openRawResource(R.raw.kota_samarinda)
            val geoJsonString = geoJsonStream.bufferedReader().use { it.readText() }

            val jsonObject = JSONObject(geoJsonString)
            val features = jsonObject.getJSONArray("features")

            for (i in 0 until features.length()) {
                val feature = features.getJSONObject(i)
                val geometry = feature.getJSONObject("geometry")
                val type = geometry.getString("type")
                val coordinates = geometry.getJSONArray("coordinates")

                if (type == "Polygon") {
                    val polygonOptions = PolygonOptions()
                        .zIndex(0f)
                        .fillColor(ContextCompat.getColor(requireContext(), R.color.polygon_fill_color))
                        .strokeColor(ContextCompat.getColor(requireContext(), R.color.polygon_stroke_color))
                        .strokeWidth(0.5f)

                    for (j in 0 until coordinates.length()) {
                        val coordArray = coordinates.getJSONArray(j)
                        for (k in 0 until coordArray.length()) {
                            val point = coordArray.getJSONArray(k)
                            val lat = point.getDouble(1)
                            val lng = point.getDouble(0)
                            polygonOptions.add(LatLng(lat, lng))
                        }
                    }

                    val polygon = mMap.addPolygon(polygonOptions)
                    geoJsonPolygonList.add(polygon)
                } else if (type == "MultiPolygon") {
                    for (j in 0 until coordinates.length()) {
                        val polygonArray = coordinates.getJSONArray(j)
                        val polygonOptions = PolygonOptions()
                            .zIndex(0f)
                            .fillColor(ContextCompat.getColor(requireContext(), R.color.polygon_fill_color))
                            .strokeColor(ContextCompat.getColor(requireContext(), R.color.polygon_stroke_color))
                            .strokeWidth(0.5f)

                        for (k in 0 until polygonArray.length()) {
                            val coordArray = polygonArray.getJSONArray(k)
                            for (l in 0 until coordArray.length()) {
                                val point = coordArray.getJSONArray(l)
                                val lat = point.getDouble(1)
                                val lng = point.getDouble(0)
                                polygonOptions.add(LatLng(lat, lng))
                            }
                        }

                        val polygon = mMap.addPolygon(polygonOptions)
                        geoJsonPolygonList.add(polygon)
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Gagal memuat GeoJSON.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeGeoJson() {
        for (polygon in geoJsonPolygonList) {
            polygon.remove()
        }
        geoJsonPolygonList.clear()
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
                            val address = currentMarkerPosition?.let { getAddressFromLatLng(it) }
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
                                currentMarkerPosition?.let { moveCameraToPosition(it) }
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
                            val address = currentMarkerPosition?.let { getAddressFromLatLng(it) }
                            if (address != null && !weatherResponse.isNullOrEmpty()) {
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
                                currentMarkerPosition?.let { moveCameraToPosition(it) }
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
                .zIndex(1.0f)

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
