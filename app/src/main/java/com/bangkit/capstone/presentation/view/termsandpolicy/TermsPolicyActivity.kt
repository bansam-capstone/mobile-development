package com.bangkit.capstone.presentation.view.termsandpolicy

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bangkit.capstone.R
import com.bangkit.capstone.databinding.ActivityTermsPolicyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermsPolicyActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FRAGMENT_TYPE = "extra_fragment_type"
        const val FRAGMENT_TERMS = "terms"
        const val FRAGMENT_POLICY = "policy"
    }

    private lateinit var binding: ActivityTermsPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentType = intent.getStringExtra(EXTRA_FRAGMENT_TYPE)
        val fragment = when (fragmentType) {
            FRAGMENT_TERMS -> TermsFragment()
            FRAGMENT_POLICY -> PolicyFragment()
            else -> throw IllegalArgumentException("Invalid fragment type")
        }

        showFragment(fragment)
    }

    private fun showFragment(fragment: Fragment) {
        val title = when (fragment) {
            is TermsFragment -> getString(R.string.title_terms)
            is PolicyFragment -> getString(R.string.title_policy)
            else -> getString(R.string.app_name)
        }
        supportActionBar?.title = title

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
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
