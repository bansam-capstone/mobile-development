package com.bangkit.capstone.presentation.view.help

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.R
import com.bangkit.capstone.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val faqQuestions = resources.getStringArray(R.array.faq_questions)
        val faqAnswers = resources.getStringArray(R.array.faq_answers)

        for (i in faqQuestions.indices) {
            val faqItemView = layoutInflater.inflate(R.layout.item_list_help, null)

            val questionTextView: TextView = faqItemView.findViewById(R.id.question_text)
            questionTextView.text = faqQuestions[i]

            faqItemView.setOnClickListener {
                val intent = Intent(this, DetailHelpActivity::class.java)
                intent.putExtra("EXTRA_QUESTION", faqQuestions[i])
                intent.putExtra("EXTRA_ANSWER", faqAnswers[i])
                startActivity(intent)
            }

            binding.faqContainer.addView(faqItemView)
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
