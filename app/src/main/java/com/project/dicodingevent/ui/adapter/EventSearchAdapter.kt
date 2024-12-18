package com.project.dicodingevent.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.dicodingevent.R
import com.project.dicodingevent.data.remote.response.ListEventsItem
import com.project.dicodingevent.databinding.ItemEventLargeBinding

class EventSearchAdapter(
    private val onItemClick: (Int) -> Unit
) : ListAdapter<ListEventsItem, EventSearchAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(
        private val binding: ItemEventLargeBinding,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(event: ListEventsItem) {
            with(binding) {
                Glide.with(root.context)
                    .load(event.mediaCover)
                    .into(imgEvent)

                tvEvent.text = event.name
                tvCategory.text = event.category
                tvOwner.text = "Oleh : ${event.ownerName}"

                root.setOnClickListener {
                    onItemClick(event.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding = ItemEventLargeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)

        // Hide favorite icon
        holder.itemView.findViewById<ImageView>(R.id.ivFav).visibility = View.GONE
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(
                oldItem: ListEventsItem,
                newItem: ListEventsItem
            ): Boolean = oldItem == newItem

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: ListEventsItem,
                newItem: ListEventsItem
            ): Boolean = oldItem == newItem
        }
    }
}