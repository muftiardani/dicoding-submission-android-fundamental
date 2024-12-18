package com.project.dicodingevent.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.dicodingevent.R
import com.project.dicodingevent.data.local.entity.EventEntity
import com.project.dicodingevent.databinding.ItemEventSmallBinding

class EventSmallAdapter(
    private val onItemClick: (Int) -> Unit,
    private val onFavoriteClick: (EventEntity) -> Unit
) : ListAdapter<EventEntity, EventSmallAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(
        private val binding: ItemEventSmallBinding,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(event: EventEntity) {
            with(binding) {
                Glide.with(root.context)
                    .load(event.mediaCover)
                    .into(imgEvent)

                tvEvent.text = event.title
                tvCategory.text = event.category
                tvOwner.text = "Oleh : ${event.owner}"

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
        val binding = ItemEventSmallBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)

        val ivFav = holder.itemView.findViewById<ImageView>(R.id.iv_fav)

        // Set favorite icon based on event status
        val favoriteIcon = if (event.isFavorite) {
            R.drawable.baseline_favorite_24
        } else {
            R.drawable.baseline_favorite_border_24
        }

        ivFav.setImageDrawable(
            ContextCompat.getDrawable(ivFav.context, favoriteIcon)
        )

        ivFav.setOnClickListener {
            onFavoriteClick(event)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventEntity>() {
            override fun areItemsTheSame(
                oldItem: EventEntity,
                newItem: EventEntity
            ): Boolean = oldItem == newItem

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: EventEntity,
                newItem: EventEntity
            ): Boolean = oldItem == newItem
        }
    }
}