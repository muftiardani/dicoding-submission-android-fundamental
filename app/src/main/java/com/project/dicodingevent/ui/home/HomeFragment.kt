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

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel by viewModels<HomeViewModel>()


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

        homeViewModel.listEvent.observe(viewLifecycleOwner) { upcomingEvents ->
            setCarouselData(upcomingEvents)
        }

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCarousel.layoutManager = layoutManager

        homeViewModel.finishedEvent.observe(viewLifecycleOwner) { finishedEvents ->
            setFinishedEventData(finishedEvents)
        }

        val finishedLayoutManager = LinearLayoutManager(requireContext())
        binding.rvFinished.layoutManager = finishedLayoutManager

        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        homeViewModel.isRvLoading.observe(viewLifecycleOwner) {
            showRvLoading(it)
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setCarouselData(eventsItem: List<ListEventsItem>) {
        val adapter = HomeCarouselAdapter()
        adapter.submitList(eventsItem)
        binding.rvCarousel.adapter = adapter
    }

    private fun setFinishedEventData(eventsItem: List<ListEventsItem>) {
        val adapter = HomeFinishedAdapter()
        adapter.submitList(eventsItem)
        binding.rvFinished.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun showRvLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarRv.visibility = View.VISIBLE
        } else {
            binding.progressBarRv.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}