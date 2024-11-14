package com.bangkit.capstone.presentation.view.prediction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.bangkit.capstone.presentation.view.adapter.PredictionAdapter
import com.bangkit.capstone.presentation.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PredictionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPredictionBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private lateinit var adapter: PredictionAdapter

    private val locationAddressMap = mapOf(
        "slamet-riyadi" to "Jl. Slamet Riyadi,Karang Asam Ilir, Kec. Sungai Kunjang, Kota Samarinda, Kalimantan Timur",
        "antasari" to "Jl. P Antasari,Air Putih, Kec. Samarinda Ulu, Kota Samarinda, Kalimantan Timur",
        "simpang-agus-salim" to "Jl. KH. Agus Salim,Sungai Pinang Luar, Kec. Samarinda Kota, Kota Samarinda, Kalimantan Timur",
        "mugirejo" to "Jl. Mugirejo, Kec. Sungai Pinang, Kota Samarinda, Kalimantan Timur",
        "simpang-lembuswana" to "Jl. M. Yamin,Gn. Kelua, Kec. Samarinda Ulu, Kota Samarinda, Kalimantan Timur",
        "kapten-sudjono" to "Jl. Kapten Soedjono Aj,Sungai Kapih, Kec. Sambutan, Kota Samarinda, Kalimantan Timur",
        "brigjend-katamso" to "Jl. Brigjend Katamso No.207-71, Sungai Pinang Dalam, Kec. Sungai Pinang, Kota Samarinda, Kalimantan Timur",
        "gatot-subroto" to "Jl. Gatot Subroto 71-75,Bandara, Kec. Sungai Pinang, Kota Samarinda, Kalimantan Timur ",
        "cendana" to "Jl. Cendana,Tlk. Lerong Ulu, Kec. Sungai Kunjang, Kota Samarinda, Kalimantan Timur",
        "di-panjaitan" to "Jl. D.I. Panjaitan,Mugirejo, Kec. Sungai Pinang, Kota Samarinda, Kalimantan Timur",
        "damanhuri" to "Jl. Damanhuri,Mugirejo, Kec. Sungai Pinang, Kota Samarinda, Kalimantan Timur ",
        "pertigaan-pramuka-perjuangan" to "Pertigaan Pramuka Perjuangan",
        "padat-karya-sempaja-simpang-wanyi" to "Jl. Padat Karya 33,Sempaja Utara, Kec. Samarinda Utara, Kota Samarinda, Kalimantan Timur",
        "simpang-sempaja" to "Simpang Sempaja",
        "ir-h-juanda" to "Simpang Juanda Fly Over",
        "tengkawang" to "Jl. Tengkawang,Karang Anyar, Kec. Sungai Kunjang, Kota Samarinda, Kalimantan Timur ",
        "sukorejo" to "Jl. Sukorejo,Lempake, Kec. Samarinda Utara, Kota Samarinda, Kalimantan Timur "
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()

        setupRecyclerView()
        observeWeatherData()

        val locations = listOf("slamet-riyadi", "antasari", "simpang-agus-salim", "mugirejo", "simpang-lembuswana",
            "kapten-sudjono", "brigjend-katamso", "gatot-subroto", "cendana", "di-panjaitan",
            "damanhuri", "pertigaan-pramuka-perjuangan", "padat-karya-sempaja-simpang-wanyi",
            "simpang-sempaja", "ir-h-juanda", "tengkawang","sukorejo")
        weatherViewModel.getWeatherByLocation(locations)


    }

    private fun setupUI(){
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupRecyclerView() {
        adapter = PredictionAdapter(locationAddressMap) { prediction ->
            Toast.makeText(this, prediction.description, Toast.LENGTH_SHORT).show()
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