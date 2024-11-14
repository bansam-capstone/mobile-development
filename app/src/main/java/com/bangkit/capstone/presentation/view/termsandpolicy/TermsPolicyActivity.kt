package com.bangkit.capstone.presentation.view.termsandpolicy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.capstone.R
import com.bangkit.capstone.databinding.ActivityTermsPolicyBinding

class TermsPolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTermsPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}