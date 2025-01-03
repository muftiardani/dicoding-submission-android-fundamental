package com.project.dicodingevent.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.project.dicodingevent.R
import com.project.dicodingevent.data.database.FavoriteEvent
import com.project.dicodingevent.data.response.Event
import com.project.dicodingevent.databinding.FragmentUpcomingDetailBinding
import com.project.dicodingevent.util.FormatTime
import com.bumptech.glide.Glide

class DetailFragment : Fragment() {
    // View Binding
    private var _binding: FragmentUpcomingDetailBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private lateinit var detailViewModel: DetailViewModel

    // Lifecycle Methods
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: DetailFragmentArgs by navArgs()
        val factory = DetailViewModelFactory(args.eventId, requireActivity().application)
        detailViewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Properly clean up binding reference
    }

    // Setup Methods
    private fun setupObservers() {
        with(detailViewModel) {
            event.observe(viewLifecycleOwner) { eventData ->
                eventData?.let { setEventData(it) }
            }

            errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                errorMessage?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }

            isLoading.observe(viewLifecycleOwner) {
                showLoading(it)
            }

            favoriteEvent.observe(viewLifecycleOwner) { favoriteEvent ->
                setupFavoriteButton(favoriteEvent)
            }
        }
    }

    private fun setupFavoriteButton(favoriteEvent: FavoriteEvent?) {
        binding.fabFavorite.apply {
            if (favoriteEvent != null) {
                setImageResource(R.drawable.ic_favorite_active)
                setOnClickListener {
                    detailViewModel.deleteFavorite(favoriteEvent)
                }
            } else {
                setImageResource(R.drawable.ic_favorite_unactive)
                setOnClickListener {
                    detailViewModel.event.value?.let { event ->
                        addFavorite(event)
                    }
                }
            }
        }
    }

    // UI Methods
    private fun setEventData(event: Event) {
        val remainingQuota = event.quota?.minus(event.registrants ?: 0)
        val eventTime = "${FormatTime.getHour(event.beginTime)} - ${FormatTime.formatWithHour(event.endTime)}"

        with(binding) {
            tvEventName.text = event.name.orEmpty()
            tvEventAdmin.text = event.ownerName.orEmpty()
            tvEventLocation.text = event.cityName.orEmpty()
            tvEventDate.text = eventTime
            tvEventQuota.text = when {
                remainingQuota == 0 -> context?.getString(R.string.quota_full)
                else -> context?.getString(R.string.remaining_quota, remainingQuota)
            }

            tvEventDescription.text = HtmlCompat.fromHtml(
                event.description ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            btnRegistration.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                startActivity(intent)
            }
        }

        Glide.with(this)
            .load(event.mediaCover)
            .into(binding.ivCover)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    // Business Logic
    private fun addFavorite(event: Event) {
        val favoriteEvent = FavoriteEvent(
            id = event.id?.toString() ?: "",
            name = event.name.orEmpty(),
            description = event.description.orEmpty(),
            link = event.link.orEmpty(),
            ownerName = event.ownerName.orEmpty(),
            cityName = event.cityName.orEmpty(),
            beginTime = FormatTime.formatDateOnly(event.beginTime),
            imageLogo = event.imageLogo.orEmpty(),
            mediaCover = event.mediaCover.orEmpty()
        )
        detailViewModel.insert(favoriteEvent)
    }
}
