package com.project.dicodingevent.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.dicodingevent.data.response.ListEventsItem
import com.project.dicodingevent.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    // View Binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val homeViewModel by viewModels<HomeViewModel>()

    // Adapters
    private lateinit var carouselAdapter: HomeCarouselAdapter
    private lateinit var finishedAdapter: HomeFinishedAdapter

    // Lifecycle Methods
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Setup Methods
    private fun setupRecyclerViews() {
        setupCarouselRecyclerView()
        setupFinishedRecyclerView()
    }

    private fun setupCarouselRecyclerView() {
        carouselAdapter = HomeCarouselAdapter()
        binding.rvCarousel.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = carouselAdapter
        }
    }

    private fun setupFinishedRecyclerView() {
        finishedAdapter = HomeFinishedAdapter()
        binding.rvFinished.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = finishedAdapter
        }
    }

    private fun setupObservers() {
        with(homeViewModel) {
            listEvent.observe(viewLifecycleOwner) { events ->
                carouselAdapter.submitList(events)
            }

            finishedEvent.observe(viewLifecycleOwner) { events ->
                finishedAdapter.submitList(events)
            }

            isLoading.observe(viewLifecycleOwner) { isLoading ->
                showLoading(isLoading)
            }

            isRvLoading.observe(viewLifecycleOwner) { isLoading ->
                showRvLoading(isLoading)
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

    private fun showRvLoading(isLoading: Boolean) {
        binding.progressBarRv.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }
}