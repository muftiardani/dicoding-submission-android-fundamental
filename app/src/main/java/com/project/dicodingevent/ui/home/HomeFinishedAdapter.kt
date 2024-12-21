package com.project.dicodingevent.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.dicodingevent.data.response.ListEventsItem
import com.project.dicodingevent.databinding.ItemFinishedEventBinding
import com.project.dicodingevent.util.FormatTime
import com.bumptech.glide.Glide

class HomeFinishedAdapter : ListAdapter<ListEventsItem, HomeFinishedAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(private val binding: ItemFinishedEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(eventsItem: ListEventsItem) {
            binding.apply {
                tvItemName.text = eventsItem.name
                tvEndTime.text = FormatTime.formatDateOnly(eventsItem.endTime)
            }

            Glide.with(binding.root.context)
                .load(eventsItem.imageLogo)
                .into(binding.imgItemPhoto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFinishedEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)

        holder.itemView.setOnClickListener {
            val eventId = event.id.toString()
            val action = HomeFragmentDirections
                .actionNavigationHomeToDetailFragment(eventId)

            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun submitList(list: List<ListEventsItem>?) {
        if (list != null) {
            val limitedList = if (list.size > 5) list.take(5) else list
            super.submitList(limitedList)
        } else {
            super.submitList(null)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(
                oldItem: ListEventsItem,
                newItem: ListEventsItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListEventsItem,
                newItem: ListEventsItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}