package com.bangkit.capstone.presentation.view.maps

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstone.databinding.FragmentSearchBinding
import com.bangkit.capstone.presentation.view.adapter.SuggestionsAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var suggestionsAdapter: SuggestionsAdapter

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

        suggestionsAdapter = SuggestionsAdapter { address ->
            val action = SearchFragmentDirections.actionSearchFragmentToMapsFragment(
                address.latitude.toFloat(),
                address.longitude.toFloat()
            )
            findNavController().navigate(action)
        }

        binding.rvSearch.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = suggestionsAdapter
        }

        binding.searchView.requestFocus()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    getAddressSuggestions(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.length > 2) {
                    getAddressSuggestions(newText)
                }
                return false
            }
        })
    }

    private fun getAddressSuggestions(query: String) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(query, 10)
            if (!addresses.isNullOrEmpty()) {
                Log.d("SearchFragment", "Jumlah alamat yang diterima: ${addresses.size}")
                binding.rvSearch.visibility = View.VISIBLE
                suggestionsAdapter.submitList(addresses)
            } else {
                Log.d("SearchFragment", "Tidak ada alamat yang ditemukan untuk query: $query")
                binding.rvSearch.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.rvSearch.visibility = View.GONE
        }
    }

}
