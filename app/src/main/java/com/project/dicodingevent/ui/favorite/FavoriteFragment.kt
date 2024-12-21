package com.project.dicodingevent.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.dicodingevent.databinding.FragmentFavoriteBinding

class FavoriteFragment : Fragment() {
    // View Binding
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    // ViewModel and Adapter
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var adapter: FavoriteEventAdapter

    // Lifecycle Methods
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
        setupViewModel()
        setupRecyclerView()
        observeFavorites()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Setup Methods
    private fun setupViewModel() {
        val factory = FavoriteViewModelFactory(requireActivity().application)
        favoriteViewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = FavoriteEventAdapter()
        binding.rvListEvent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoriteFragment.adapter
        }
    }

    private fun observeFavorites() {
        favoriteViewModel.getFavoriteEvents().observe(viewLifecycleOwner) { favoriteEvents ->
            updateUI(favoriteEvents.isNullOrEmpty())
            favoriteEvents?.let { adapter.submitList(it) }
        }
    }

    // UI Updates
    private fun updateUI(isEmpty: Boolean) {
        binding.apply {
            tvEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
            rvListEvent.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }
    }
}