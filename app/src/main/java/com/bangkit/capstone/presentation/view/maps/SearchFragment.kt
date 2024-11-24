package com.bangkit.capstone.presentation.view.maps

import com.bangkit.capstone.presentation.view.adapter.HistoryAdapter
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstone.R
import com.bangkit.capstone.databinding.FragmentSearchBinding
import com.bangkit.capstone.presentation.view.adapter.SuggestionsAdapter
import com.bangkit.capstone.presentation.viewmodel.SearchHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var suggestionsAdapter: SuggestionsAdapter
    private lateinit var historyAdapter: HistoryAdapter
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

        binding.searchViewSearch.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_mapsFragment)
        }

        binding.searchViewSearch.setOnCloseListener {
            binding.rvSearch.visibility = View.GONE
            binding.rvHistory.visibility = View.VISIBLE
            false
        }


        binding.btnClearHistory.setOnClickListener {
            searchHistoryViewModel.clearSearchHistory()
            Toast.makeText(context, "Riwayat pencarian dihapus", Toast.LENGTH_SHORT).show()
        }

        binding.searchViewSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        historyAdapter = HistoryAdapter { searchHistory ->
            val action = SearchFragmentDirections.actionSearchFragmentToMapsFragment(
                latitude = searchHistory.latitude.toFloat(),
                longitude = searchHistory.longitude.toFloat()
            )
            findNavController().navigate(action)
        }

        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            searchHistoryViewModel.searchHistoryState.collectLatest { searchHistory ->
                historyAdapter.submitList(searchHistory)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            searchHistoryViewModel.searchHistoryState.collectLatest { searchHistory ->
                if (searchHistory.isEmpty()) {
                    binding.btnClearHistory.isEnabled = false
                    binding.btnClearHistory.alpha = 0.5f
                } else {
                    binding.btnClearHistory.isEnabled = true
                    binding.btnClearHistory.alpha = 1f
                }
            }
        }

        suggestionsAdapter = SuggestionsAdapter { address ->
            viewLifecycleOwner.lifecycleScope.launch {
                val title = address.thoroughfare ?: address.getAddressLine(0)
                val subtitle = address.countryName ?: address.locality ?: address.subLocality ?: "Tidak tersedia"

                if (title != null) {
                    searchHistoryViewModel.insertSearchHistory(
                        title = title.toString(),
                        subtitle = subtitle,
                        latitude = address.latitude,
                        longitude = address.longitude,
                        timestamp = System.currentTimeMillis()
                    )

                    val action = SearchFragmentDirections.actionSearchFragmentToMapsFragment(
                        latitude = address.latitude.toFloat(),
                        longitude = address.longitude.toFloat()
                    )
                    findNavController().navigate(action)
                } else {
                    Toast.makeText(context, "Data alamat tidak lengkap", Toast.LENGTH_SHORT).show()
                }
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
                binding.rvHistory.visibility = View.GONE
                suggestionsAdapter.submitList(addresses)
            } else {
                binding.rvSearch.visibility = View.GONE
                binding.rvHistory.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.rvSearch.visibility = View.GONE
            binding.rvHistory.visibility = View.VISIBLE
        }
    }
}
