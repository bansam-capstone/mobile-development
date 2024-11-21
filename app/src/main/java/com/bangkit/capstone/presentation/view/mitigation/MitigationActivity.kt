package com.bangkit.capstone.presentation.view.mitigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import com.bangkit.capstone.R
import com.bangkit.capstone.databinding.ActivityMitigationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MitigationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMitigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMitigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutSebelumBanjir = findViewById<LinearLayout>(R.id.layoutSebelumBanjir)
        val layoutKetikaPeringatanBanjir = findViewById<LinearLayout>(R.id.layoutKetikaPeringatanBanjir)
        val layoutSaatBanjirTerjadi = findViewById<LinearLayout>(R.id.layoutSaatBanjirTerjadi)
        val layoutSetelahBanjirSurut = findViewById<LinearLayout>(R.id.layoutSetelahBanjirSurut)

        val listSebelumBanjir = resources.getStringArray(R.array.list_sebelum_banjir)
        val listKetikaPeringatanBanjir = resources.getStringArray(R.array.list_ketika_peringatan_banjir)
        val listSaatBanjirTerjadi = resources.getStringArray(R.array.list_saat_banjir_terjadi)
        val listSetelahBanjirSurut = resources.getStringArray(R.array.list_setelah_banjir_surut)

        fun addItemsToLayout(list: Array<String>, layout: LinearLayout) {
            list.forEach { item ->
                val textView = TextView(this).apply {
                    text = "• $item"
                    textSize = 14f
                    setPadding(0, 2, 0, 2)
                }
                layout.addView(textView)
            }
        }

        addItemsToLayout(listSebelumBanjir, layoutSebelumBanjir)
        addItemsToLayout(listKetikaPeringatanBanjir, layoutKetikaPeringatanBanjir)
        addItemsToLayout(listSaatBanjirTerjadi, layoutSaatBanjirTerjadi)
        addItemsToLayout(listSetelahBanjirSurut, layoutSetelahBanjirSurut)
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
