package com.bangkit.capstone.presentation.view.maps

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstone.databinding.FragmentSearchBinding
import com.bangkit.capstone.presentation.view.adapter.SuggestionsAdapter
import com.bangkit.capstone.presentation.viewmodel.SearchHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var suggestionsAdapter: SuggestionsAdapter
    private val searchHistoryViewModel: SearchHistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { getAddressSuggestions(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { getAddressSuggestions(it) }
                return false
            }
        })
    }

    private fun setupAdapters() {
        suggestionsAdapter = SuggestionsAdapter { address ->
            val timestamp = System.currentTimeMillis()
            viewLifecycleOwner.lifecycleScope.launch {
//                searchHistoryViewModel.insertSearchHistory(
//                    address.getAddressLine(0).toString(),
//                    address.getAddressLine(1).toString(),
//                    address.latitude,
//                    address.longitude,
//                    timestamp
//                )

                val action = SearchFragmentDirections.actionSearchFragmentToMapsFragment(
                    latitude = address.latitude.toFloat(),
                    longitude = address.longitude.toFloat()
                )
                findNavController().navigate(action)
            }
        }

        binding.rvSearch.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = suggestionsAdapter
        }
    }

    private fun getAddressSuggestions(query: String) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(query, 10)
            if (!addresses.isNullOrEmpty()) {
                binding.rvSearch.visibility = View.VISIBLE
                suggestionsAdapter.submitList(addresses)
            } else {
                binding.rvSearch.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.rvSearch.visibility = View.GONE
        }
    }
}
