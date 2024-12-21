package com.project.dicodingevent.ui.finished

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.dicodingevent.data.response.ListEventsItem
import com.project.dicodingevent.databinding.FragmentFinishedBinding

class FinishedFragment : Fragment() {
    // View Binding
    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val finishedViewModel by viewModels<FinishedViewModel>()

    // Adapter
    private lateinit var finishedAdapter: FinishedAdapter

    // Lifecycle Methods
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Setup Methods
    private fun setupRecyclerView() {
        finishedAdapter = FinishedAdapter()
        binding.rvListEvent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = finishedAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { finishedViewModel.searchEvents(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    finishedViewModel.showFinishedEvents()
                }
                return true
            }
        })
    }

    private fun setupObservers() {
        with(finishedViewModel) {
            listEvent.observe(viewLifecycleOwner) { events ->
                finishedAdapter.submitList(events)
            }

            isLoading.observe(viewLifecycleOwner) { isLoading ->
                showLoading(isLoading)
            }

            errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                errorMessage?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // UI Methods
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }
}