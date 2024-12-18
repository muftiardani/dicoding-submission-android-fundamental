package com.project.dicodingevent.ui

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.dicodingevent.data.remote.response.ListEventsItem
import com.project.dicodingevent.databinding.ActivitySearchBinding
import com.project.dicodingevent.ui.adapter.EventSearchAdapter
import com.project.dicodingevent.ui.factory.SearchViewModelFactory
import com.project.dicodingevent.ui.model.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private companion object {
        const val EVENT_ID_KEY = "EVENT_ID"
    }

    private lateinit var binding: ActivitySearchBinding
    private lateinit var eventAdapter: EventSearchAdapter

    private val viewModel by viewModels<SearchViewModel> {
        SearchViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupRecyclerView()
        setupSearchField()
        setupObservers()
    }

    private fun setupView() {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
    }

    private fun setupRecyclerView() {
        eventAdapter = EventSearchAdapter(onItemClick = ::navigateToDetail)
        binding.rvEventSearch.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = eventAdapter
        }
    }

    private fun setupSearchField() {
        binding.tfSearch.editText?.setOnEditorActionListener { _, actionId, event ->
            handleSearchAction(actionId, event)
        }
    }

    private fun handleSearchAction(actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
            event?.keyCode == KeyEvent.KEYCODE_ENTER) {
            binding.tfSearch.editText?.text?.toString()?.let { query ->
                viewModel.searchEvents(query)
            }
            return true
        }
        return false
    }

    private fun setupObservers() {
        with(viewModel) {
            uiState.observe(this@SearchActivity) { state ->
                setEventDataSearch(state.searchResults)
            }

            errorMessage.observe(this@SearchActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@SearchActivity, message, Toast.LENGTH_SHORT).show()
                }
            }

            isLoading.observe(this@SearchActivity, ::showLoading)
        }
    }

    private fun setEventDataSearch(events: List<ListEventsItem>) {
        eventAdapter.submitList(events)
    }

    private fun navigateToDetail(eventId: Int) {
        Intent(this, DetailActivity::class.java).apply {
            putExtra(EVENT_ID_KEY, eventId)
            startActivity(this)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}