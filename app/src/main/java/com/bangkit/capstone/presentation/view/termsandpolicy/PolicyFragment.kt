package com.bangkit.capstone.presentation.view.termsandpolicy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bangkit.capstone.R
import com.bangkit.capstone.databinding.FragmentPolicyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PolicyFragment : Fragment() {
    private var _binding: FragmentPolicyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.titleTextView.text = getString(R.string.policy_title)
        binding.subtitleTextView.text = getString(R.string.policy_subtitle)
        binding.descriptionTextView.text = getString(R.string.policy_description)
        binding.highlightedTextView.text = getString(R.string.policy_highlighted)

        val contentArray = resources.getStringArray(R.array.policy_content)
        val content = contentArray.joinToString("\n\n")
        binding.contentTextView.text = content
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
