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
    private var _binding: FragmentUpcomingDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var detailViewModel: DetailViewModel

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
        val eventId = args.eventId

        val factory = DetailViewModelFactory(eventId, requireActivity().application)
        detailViewModel =
            ViewModelProvider(this, factory)[DetailViewModel::class.java]

        detailViewModel.event.observe(viewLifecycleOwner) { eventData ->
            setEventData(eventData)
        }

        detailViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        detailViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        detailViewModel.favoriteEvent.observe(viewLifecycleOwner) { favoriteEvent ->
            if (favoriteEvent != null) {
                binding.fabFavorite.setImageResource(R.drawable.ic_favorite_active)
                binding.fabFavorite.setOnClickListener {
                    detailViewModel.deleteFavorite(favoriteEvent)
                }
            } else {
                binding.fabFavorite.setImageResource(R.drawable.ic_favorite_unactive)
                binding.fabFavorite.setOnClickListener {
                    detailViewModel.event.value?.let { event ->
                        addFavorite(event)
                    }
                }
            }
        }
    }

    private fun addFavorite(event: Event) {
        val favoriteEvent = FavoriteEvent(
            id = event.id.toString(),
            name = event.name.toString(),
            description = event.description.toString(),
            link = event.link.toString(),
            ownerName = event.ownerName.toString(),
            cityName = event.cityName.toString(),
            beginTime = FormatTime.formatDateOnly(event.beginTime),
            imageLogo = event.imageLogo.toString(),
            mediaCover = event.mediaCover.toString(),
        )

        detailViewModel.insert(favoriteEvent)
    }

    private fun setEventData(event: Event) {
        val remainingQuota = event.quota?.minus(event.registrants!!)
        val eventTime ="${FormatTime.getHour(event.beginTime)} - ${FormatTime.formatWithHour(event.endTime)}"
        binding.apply {
            tvEventName.text = event.name
            tvEventAdmin.text = event.ownerName
            tvEventLocation.text = event.cityName
            tvEventDate.text = eventTime
            if (remainingQuota == 0) {
                binding.tvEventQuota.text = context?.getString(R.string.quota_full)
            } else {
                val remainingQuotaText =
                    context?.getString(R.string.remaining_quota, remainingQuota)
                binding.tvEventQuota.text = remainingQuotaText
            }
        }

        binding.tvEventDescription.text = HtmlCompat.fromHtml(
            event.description.toString(),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        Glide.with(this)
            .load(event.mediaCover)
            .into(binding.ivCover)

        binding.btnRegistration.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(event.link)
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}