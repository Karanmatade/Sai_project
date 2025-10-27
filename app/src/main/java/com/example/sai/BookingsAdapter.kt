package com.example.sai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sai.data.entity.BookingEntity

class BookingsAdapter : ListAdapter<BookingEntity, BookingsAdapter.VH>(DIFF) {

    object DIFF : DiffUtil.ItemCallback<BookingEntity>() {
        override fun areItemsTheSame(oldItem: BookingEntity, newItem: BookingEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: BookingEntity, newItem: BookingEntity) = oldItem == newItem
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvRoom: TextView = v.findViewById(R.id.tvBookingRoom)
        val tvDates: TextView = v.findViewById(R.id.tvDates)
        val chipStatus: com.google.android.material.chip.Chip = v.findViewById(R.id.chipStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.tvRoom.text = "Room #${item.roomNumber}"
        holder.tvDates.text = "${item.checkInDate} → ${item.checkOutDate}"
        holder.chipStatus.text = item.status
        if (item.status == "Active") {
            holder.chipStatus.setChipBackgroundColorResource(R.color.chip_green)
            holder.chipStatus.setTextColor(holder.itemView.resources.getColor(R.color.green_available, null))
        } else {
            holder.chipStatus.setChipBackgroundColorResource(R.color.chip_red)
            holder.chipStatus.setTextColor(holder.itemView.resources.getColor(R.color.red_booked, null))
        }
    }
}

private fun View.setCardBackgroundColorCompat(colorRes: Int) {
    val color = resources.getColor(colorRes, null)
    if (this is com.google.android.material.card.MaterialCardView) {
        this.setCardBackgroundColor(color)
    } else {
        this.setBackgroundColor(color)
    }
}
