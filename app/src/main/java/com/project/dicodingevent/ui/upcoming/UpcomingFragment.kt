package com.project.dicodingevent.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.dicodingevent.databinding.FragmentUpcomingBinding

class UpcomingFragment : Fragment() {
    // View Binding
    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val upcomingViewModel by viewModels<UpcomingViewModel>()

    // Adapter
    private lateinit var upcomingAdapter: UpcomingAdapter

    // Lifecycle Methods
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Setup Methods
    private fun setupRecyclerView() {
        upcomingAdapter = UpcomingAdapter()
        binding.rvListEvent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = upcomingAdapter
        }
    }

    private fun setupObservers() {
        with(upcomingViewModel) {
            listEvent.observe(viewLifecycleOwner) { events ->
                upcomingAdapter.submitList(events)
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