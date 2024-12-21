package com.project.dicodingevent.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.dicodingevent.data.response.ListEventsItem
import com.project.dicodingevent.databinding.ItemFinishedEventBinding
import com.project.dicodingevent.util.FormatTime

class HomeFinishedAdapter : ListAdapter<ListEventsItem, HomeFinishedAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFinishedEventBinding.inflate(
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

    override fun submitList(list: List<ListEventsItem>?) {
        val limitedList = list?.take(MAX_ITEMS)
        super.submitList(limitedList)
    }

    private fun setupItemClickListener(holder: ViewHolder, event: ListEventsItem) {
        holder.itemView.setOnClickListener {
            val action = HomeFragmentDirections
                .actionNavigationHomeToDetailFragment(event.id.toString())
            holder.itemView.findNavController().navigate(action)
        }
    }

    class ViewHolder(private val binding: ItemFinishedEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: ListEventsItem) {
            with(binding) {
                tvItemName.text = event.name
                tvEndTime.text = event.endTime?.let { FormatTime.formatDateOnly(it) }

                Glide.with(root.context)
                    .load(event.imageLogo)
                    .into(imgItemPhoto)
            }
        }
    }

    companion object {
        private const val MAX_ITEMS = 5

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean =
                oldItem == newItem
        }
    }
}