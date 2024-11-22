package com.bangkit.capstone.presentation.view.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.bangkit.capstone.R
import com.bangkit.capstone.databinding.ActivitySettingsBinding
import com.bangkit.capstone.presentation.view.about.AboutActivity
import com.bangkit.capstone.presentation.view.termsandpolicy.TermsPolicyActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.llNotification.setOnClickListener {
        }

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