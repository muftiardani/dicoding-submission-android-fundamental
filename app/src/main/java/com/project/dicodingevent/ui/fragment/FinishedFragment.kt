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
import androidx.recyclerview.widget.GridLayoutManager
import com.project.dicodingevent.data.local.entity.EventEntity
import com.project.dicodingevent.databinding.FragmentFinishedBinding
import com.project.dicodingevent.ui.DetailActivity
import com.project.dicodingevent.ui.adapter.EventSmallAdapter
import com.project.dicodingevent.ui.factory.FinishedViewModelFactory
import com.project.dicodingevent.ui.model.FinishedViewModel
import kotlinx.coroutines.launch

class FinishedFragment : Fragment() {

    private companion object {
        const val EVENT_ID_KEY = "EVENT_ID"
        const val GRID_SPAN_COUNT = 2
    }

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<FinishedViewModel> {
        FinishedViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var eventsAdapter: EventSmallAdapter

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
        setupObservers()
    }

    private fun setupRecyclerView() {
        eventsAdapter = EventSmallAdapter(
            onItemClick = ::navigateToDetail,
            onFavoriteClick = ::handleFavoriteClick
        )

        binding.rvEvent.apply {
            layoutManager = GridLayoutManager(requireContext(), GRID_SPAN_COUNT)
            setHasFixedSize(true)
            adapter = eventsAdapter
        }
    }

    private fun setupObservers() {
        with(viewModel) {
            uiState.observe(viewLifecycleOwner) { state ->
                eventsAdapter.submitList(state.events)
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