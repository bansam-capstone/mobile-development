package com.bangkit.capstone.presentation.view.settings

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.bangkit.capstone.R
import com.bangkit.capstone.common.Resource
import com.bangkit.capstone.data.remote.response.LocationResponse
import com.bangkit.capstone.databinding.ActivitySettingsBinding
import com.bangkit.capstone.domain.model.FloodCategories
import com.bangkit.capstone.presentation.view.about.AboutActivity
import com.bangkit.capstone.presentation.view.termsandpolicy.TermsPolicyActivity
import com.bangkit.capstone.presentation.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.llAbout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        binding.llTerms.setOnClickListener {
            val intent = Intent(this, TermsPolicyActivity::class.java)
            intent.putExtra(TermsPolicyActivity.EXTRA_FRAGMENT_TYPE, TermsPolicyActivity.FRAGMENT_TERMS)
            startActivity(intent)
        }

        binding.llPrivacy.setOnClickListener {
            val intent = Intent(this, TermsPolicyActivity::class.java)
            intent.putExtra(TermsPolicyActivity.EXTRA_FRAGMENT_TYPE, TermsPolicyActivity.FRAGMENT_POLICY)
            startActivity(intent)
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
