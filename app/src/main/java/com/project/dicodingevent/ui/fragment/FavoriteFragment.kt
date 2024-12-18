package com.project.dicodingevent.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.dicodingevent.data.local.entity.EventEntity
import com.project.dicodingevent.databinding.FragmentFavoriteBinding
import com.project.dicodingevent.ui.DetailActivity
import com.project.dicodingevent.ui.adapter.EventLargeAdapter
import com.project.dicodingevent.ui.factory.FavoriteViewModelFactory
import com.project.dicodingevent.ui.model.FavoriteViewModel
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {

    private companion object {
        const val EVENT_ID_KEY = "EVENT_ID"
    }

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<FavoriteViewModel> {
        FavoriteViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var eventsAdapter: EventLargeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeEvents()
    }

    private fun setupRecyclerView() {
        eventsAdapter = EventLargeAdapter(
            onFavoriteClick = ::handleFavoriteClick,
            onItemClick = ::navigateToDetail
        )

        binding.rvFavorite.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventsAdapter
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getFavoriteEvents().observe(viewLifecycleOwner) { events ->
                eventsAdapter.submitList(events)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner, ::showLoading)
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