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
import com.project.dicodingevent.databinding.FragmentUpcomingBinding
import com.project.dicodingevent.ui.DetailActivity
import com.project.dicodingevent.ui.adapter.EventLargeAdapter
import com.project.dicodingevent.ui.factory.UpcomingModelFactory
import com.project.dicodingevent.ui.model.UpcomingViewModel
import kotlinx.coroutines.launch

class UpcomingFragment : Fragment() {

    private companion object {
        const val EVENT_ID_KEY = "EVENT_ID"
    }

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<UpcomingViewModel> {
        UpcomingModelFactory.getInstance(requireActivity())
    }

    private lateinit var eventsAdapter: EventLargeAdapter

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

    private fun setupRecyclerView() {
        eventsAdapter = EventLargeAdapter(
            onItemClick = ::navigateToDetail,
            onFavoriteClick = ::handleFavoriteClick
        )

        binding.rvEvent.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            setHasFixedSize(true)
            adapter = eventsAdapter
        }
    }

    private fun setupObservers() {
        with(viewModel) {
            uiState.observe(viewLifecycleOwner) { state ->
                eventsAdapter.submitList(state.upcomingEvents)
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
            viewModel.toggleEventFavorite(event, !event.isFavorite)
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