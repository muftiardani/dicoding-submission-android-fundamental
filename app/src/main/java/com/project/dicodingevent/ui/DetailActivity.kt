package com.project.dicodingevent.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.project.dicodingevent.data.remote.response.EventDetailResponse
import com.project.dicodingevent.databinding.ActivityDetailBinding
import com.project.dicodingevent.ui.factory.DetailViewModelFactory
import com.project.dicodingevent.ui.model.DetailViewModel

class DetailActivity : AppCompatActivity() {

    private companion object {
        const val EVENT_ID_KEY = "EVENT_ID"
        const val DEFAULT_EVENT_ID = 1
    }

    private lateinit var binding: ActivityDetailBinding

    private val viewModel by viewModels<DetailViewModel> {
        DetailViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupObservers()
        loadEventDetail()
    }

    private fun setupView() {
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupObservers() {
        with(viewModel) {
            uiState.observe(this@DetailActivity) { state ->
                state.eventDetail?.let { setEventData(it) }
            }

            errorMessage.observe(this@DetailActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@DetailActivity, message, Toast.LENGTH_SHORT).show()
                }
            }

            isLoading.observe(this@DetailActivity, ::showLoading)
        }
    }

    private fun loadEventDetail() {
        val eventId = intent.getIntExtra(EVENT_ID_KEY, DEFAULT_EVENT_ID)
        viewModel.fetchEventDetail(eventId)
    }

    @SuppressLint("SetTextI18n")
    private fun setEventData(eventDetail: EventDetailResponse) {
        val event = eventDetail.event

        with(binding) {
            supportActionBar?.title = event.name

            Glide.with(root.context)
                .load(event.imageLogo)
                .into(imgEvent)

            tvTitleEvent.text = event.name
            tvSummaryEvent.text = event.summary
            tvBeginTime.text = event.beginTime
            tvRemainingQuota.text = "Sisa Kuota: ${event.quota - event.registrants}"
            tvCategory.text = event.category
            tvOwner.text = "Oleh : ${event.ownerName}"
            tvDescription.text = HtmlCompat.fromHtml(
                event.description,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            btnRegister.setOnClickListener {
                openRegistrationLink(event.link)
            }
        }
    }

    private fun openRegistrationLink(link: String) {
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(link)
            startActivity(this)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnRegister.visibility = if (isLoading) View.GONE else View.VISIBLE
            tvInformation.visibility = if (isLoading) View.GONE else View.VISIBLE
            cardCategory.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}