package com.project.dicodingevent.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.dicodingevent.data.local.entity.EventEntity
import com.project.dicodingevent.databinding.FragmentHomeBinding
import com.project.dicodingevent.ui.DetailActivity
import com.project.dicodingevent.ui.adapter.EventLargeAdapter
import com.project.dicodingevent.ui.adapter.EventSmallAdapter
import com.project.dicodingevent.ui.factory.HomeModelFactory
import com.project.dicodingevent.ui.model.HomeViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private companion object {
        const val EVENT_ID_KEY = "EVENT_ID"
        const val MAX_DISPLAYED_EVENTS = 5
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel> {
        HomeModelFactory.getInstance(requireActivity())
    }

    private lateinit var upcomingEventsAdapter: EventSmallAdapter
    private lateinit var finishedEventsAdapter: EventLargeAdapter

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

    private fun setupRecyclerViews() {
        setupUpcomingEventsRecyclerView()
        setupFinishedEventsRecyclerView()
    }

    private fun setupUpcomingEventsRecyclerView() {
        upcomingEventsAdapter = EventSmallAdapter(
            onItemClick = ::navigateToDetail,
            onFavoriteClick = ::handleFavoriteClick
        )

        binding.rvEventUpcoming.apply {
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = upcomingEventsAdapter
        }
    }

    private fun setupFinishedEventsRecyclerView() {
        finishedEventsAdapter = EventLargeAdapter(
            onItemClick = ::navigateToDetail,
            onFavoriteClick = ::handleFavoriteClick
        )

        binding.rvEventFinished.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            setHasFixedSize(true)
            adapter = finishedEventsAdapter
        }
    }

    private fun setupObservers() {
        with(viewModel) {
            uiState.observe(viewLifecycleOwner) { state ->
                upcomingEventsAdapter.submitList(state.upcomingEvents.take(MAX_DISPLAYED_EVENTS))
                finishedEventsAdapter.submitList(state.finishedEvents.take(MAX_DISPLAYED_EVENTS))
            }

            errorMessage.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
                }
            }

            isLoading.observe(viewLifecycleOwner, ::showLoading)
        }
    }

    private fun handleFavoriteClick(event: EventEntity) {
        viewLifecycleOwner.lifecycleScope.launch {
            if (event.isFavorite) {
                viewModel.toggleEventFavorite(event, false)
            } else {
                viewModel.toggleEventFavorite(event, true)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun navigateToDetail(eventId: Int) {
        Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(EVENT_ID_KEY, eventId)
            startActivity(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}