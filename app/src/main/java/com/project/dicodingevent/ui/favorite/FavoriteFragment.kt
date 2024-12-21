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

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var adapter: FavoriteEventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = FavoriteViewModelFactory(requireActivity().application)
        favoriteViewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        adapter = FavoriteEventAdapter()
        binding.rvListEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvListEvent.adapter = adapter

        favoriteViewModel.getFavoriteEvents().observe(viewLifecycleOwner) { favoriteEvents ->
            if (favoriteEvents.isNullOrEmpty()) {
                // Show empty state message and hide RecyclerView
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvListEvent.visibility = View.GONE
            } else {
                // Show RecyclerView and hide empty state message
                binding.tvEmptyState.visibility = View.GONE
                binding.rvListEvent.visibility = View.VISIBLE
                adapter.submitList(favoriteEvents)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}