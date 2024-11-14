package com.bangkit.capstone.presentation.view.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.capstone.R
import com.bangkit.capstone.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}