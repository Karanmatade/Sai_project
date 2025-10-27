package com.example.sai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sai.data.entity.RoomEntity
import com.google.android.material.chip.Chip

class RoomsAdapter(
    private val onEdit: (RoomEntity) -> Unit,
    private val onDelete: (RoomEntity) -> Unit
) : ListAdapter<RoomEntity, RoomsAdapter.VH>(DIFF) {

    object DIFF : DiffUtil.ItemCallback<RoomEntity>() {
        override fun areItemsTheSame(oldItem: RoomEntity, newItem: RoomEntity) = oldItem.roomNumber == newItem.roomNumber
        override fun areContentsTheSame(oldItem: RoomEntity, newItem: RoomEntity) = oldItem == newItem
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvRoomNumber: TextView = v.findViewById(R.id.tvRoomNumber)
        val tvType: TextView = v.findViewById(R.id.tvType)
        val tvPrice: TextView = v.findViewById(R.id.tvPrice)
        val chipStatus: Chip = v.findViewById(R.id.chipStatus)
        val btnEdit: ImageButton = v.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = v.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_room, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.tvRoomNumber.text = "Room #${item.roomNumber}"
        holder.tvType.text = item.type
        holder.tvPrice.text = "₹${item.price}"
        if (item.isBooked) {
            holder.chipStatus.text = "Booked"
            holder.chipStatus.setTextColor(holder.itemView.resources.getColor(R.color.red_booked, null))
        } else {
            holder.chipStatus.text = "Available"
            holder.chipStatus.setTextColor(holder.itemView.resources.getColor(R.color.green_available, null))
        }
        holder.btnEdit.setOnClickListener { onEdit(item) }
        holder.btnDelete.setOnClickListener { onDelete(item) }
    }
}
