package com.bangkit.capstone.presentation.view.prediction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstone.R
import com.bangkit.capstone.common.Resource
import com.bangkit.capstone.databinding.ActivityPredictionBinding
import com.bangkit.capstone.domain.model.LocationInfo
import com.bangkit.capstone.presentation.view.adapter.PredictionAdapter
import com.bangkit.capstone.presentation.view.maps.MapsActivity
import com.bangkit.capstone.presentation.viewmodel.WeatherViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PredictionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPredictionBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private lateinit var adapter: PredictionAdapter

    private val locationAddressMap = mapOf(
        "slamet-riyadi" to "Jl. Slamet Riyadi,Karang Asam Ilir, Kec. Sungai Kunjang",
        "antasari" to "Jl. P Antasari, Air Putih, Kec. Samarinda Ulu",
        "simpang-agus-salim" to "Jl. KH. Agus Salim, Sungai Pinang Luar, Kec. Samarinda Kota",
        "mugirejo" to "Jl. Mugirejo, Kec. Sungai Pinang",
        "simpang-lembuswana" to "Simpang Lembuswana",
        "kapten-sudjono" to "Jl. Kapten Soedjono Aj, Sungai Kapih, Kec. Sambutan",
        "brigjend-katamso" to "Jl. Brigjend Katamso No.207-71, Sungai Pinang Dalam, Kec. Sungai Pinang",
        "gatot-subroto" to "Jl. Gatot Subroto 71-75, Bandara, Kec. Sungai Pinang",
        "cendana" to "Jl. Cendana,Tlk. Lerong Ulu, Kec. Sungai Kunjang",
        "di-panjaitan" to "Jl. D.I. Panjaitan, Mugirejo, Kec. Sungai Pinang",
        "damanhuri" to "Jl. Damanhuri, Mugirejo, Kec. Sungai Pinang",
        "pertigaan-pramuka-perjuangan" to "Pertigaan Pramuka Perjuangan",
        "padat-karya-sempaja-simpang-wanyi" to "Jl. Padat Karya 33, Sempaja Utara, Kec. Samarinda Utara",
        "simpang-sempaja" to "Simpang Sempaja",
        "ir-h-juanda" to "Simpang Juanda Fly Over",
        "tengkawang" to "Jl. Tengkawang, Karang Anyar, Kec. Sungai Kunjang",
        "sukorejo" to "Jl. Sukorejo, Lempake, Kec. Samarinda Utara"
    )

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()

        setupRecyclerView()
        observeWeatherData()

        val locationIdentifiers = listOf(
            "slamet-riyadi", "antasari", "simpang-agus-salim", "mugirejo", "simpang-lembuswana",
            "kapten-sudjono", "brigjend-katamso", "gatot-subroto", "cendana", "di-panjaitan",
            "damanhuri", "pertigaan-pramuka-perjuangan", "padat-karya-sempaja-simpang-wanyi",
            "simpang-sempaja", "ir-h-juanda", "tengkawang", "sukorejo"
        )
        weatherViewModel.getWeatherByLocation(locationIdentifiers)
    }


    private fun setupUI(){
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupRecyclerView() {
        adapter = PredictionAdapter(locationAddressMap) { prediction ->

            val selectedLocation = locations.find { it.identifier == prediction.location }
            if (selectedLocation != null) {

                val intent = Intent(this, MapsActivity::class.java).apply {
                    val bundle = Bundle()
                    bundle.putDouble("latitude", selectedLocation.latLng.latitude)
                    bundle.putDouble("longitude", selectedLocation.latLng.longitude)
                    bundle.putString("identifier", selectedLocation.identifier)

                    putExtra("latitude", selectedLocation.latLng.latitude)
                    putExtra("longitude", selectedLocation.latLng.longitude)
                    putExtra("identifier", selectedLocation.identifier)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }

        binding.predictionRecycler.layoutManager = LinearLayoutManager(this)
        binding.predictionRecycler.adapter = adapter
    }



    private fun observeWeatherData() {
        lifecycleScope.launch {
            weatherViewModel.weatherData.collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        binding.shimmerLayout.startShimmer()
                        binding.shimmerLayout.visibility = View.VISIBLE
                        binding.predictionRecycler.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.visibility = View.GONE
                        binding.predictionRecycler.visibility = View.VISIBLE
                        result.data?.let { predictions ->
                            adapter.setData(predictions)
                        }
                    }
                    is Resource.Error -> {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.visibility = View.GONE
                        binding.predictionRecycler.visibility = View.GONE
                        Toast.makeText(this@PredictionActivity, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}