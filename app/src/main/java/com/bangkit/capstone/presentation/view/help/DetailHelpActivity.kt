package com.bangkit.capstone.presentation.view.help

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.R
import com.bangkit.capstone.databinding.ActivityDetailHelpBinding

class DetailHelpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val question = intent.getStringExtra("EXTRA_QUESTION")
        val answer = intent.getStringExtra("EXTRA_ANSWER")

        binding.questionTitle.text = question
        binding.answerText.text = answer
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
