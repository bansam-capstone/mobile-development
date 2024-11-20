package com.bangkit.capstone.presentation.view.maps

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.R
import com.bangkit.capstone.databinding.ActivityMapsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        if (savedInstanceState == null){
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            val identifier = intent.getStringExtra("identifier")

            val mapsFragment = MapsFragment.newInstance(latitude, longitude, identifier)

            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, mapsFragment)
                .commit()
        }
    }
}
