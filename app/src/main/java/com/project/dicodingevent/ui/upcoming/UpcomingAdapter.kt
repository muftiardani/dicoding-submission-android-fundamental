package com.project.dicodingevent.ui.upcoming

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.dicodingevent.R
import com.project.dicodingevent.data.response.ListEventsItem
import com.project.dicodingevent.databinding.ItemUpcomingEventBinding

class UpcomingAdapter : ListAdapter<ListEventsItem, UpcomingAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUpcomingEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
        setupItemClickListener(holder, event)
    }

    private fun setupItemClickListener(holder: ViewHolder, event: ListEventsItem) {
        holder.itemView.setOnClickListener {
            val action = UpcomingFragmentDirections
                .actionNavigationUpcomingToDetailFragment(event.id.toString())
            holder.itemView.findNavController().navigate(action)
        }
    }

    class ViewHolder(private val binding: ItemUpcomingEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: ListEventsItem) {
            with(binding) {
                tvItemName.text = event.name
                tvLocation.text = event.cityName
                tvQuota.text = root.context.getString(
                    R.string.registrants_quota,
                    event.registrants,
                    event.quota
                )

                Glide.with(root.context)
                    .load(event.imageLogo)
                    .into(imgItemPhoto)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean =
                oldItem == newItem
        }
    }
}