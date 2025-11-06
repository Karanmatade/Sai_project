package com.example.sai

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sai.data.entity.BookingWithGuest
import com.example.sai.databinding.ItemGuestCardBinding

class GuestsAdapter(
    private val onItemClick: (BookingWithGuest) -> Unit
) : ListAdapter<BookingWithGuest, GuestsAdapter.GuestViewHolder>(GuestDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuestViewHolder {
        val binding = ItemGuestCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GuestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GuestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GuestViewHolder(
        private val binding: ItemGuestCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bookingWithGuest: BookingWithGuest) {
            val booking = bookingWithGuest.booking
            val guest = bookingWithGuest.guest

            if (guest != null) {
                // Guest Name
                binding.tvGuestName.text = "${guest.firstName} ${guest.lastName}"

                // Check-in Date
                binding.tvCheckInDate.text = booking.checkInDate

                // Folio Number
                binding.tvFolioNumber.text = "Folio: ${guest.folioNumber}"

                // Room Number
                binding.tvRoomNumber.text = "Room #${booking.roomNumber}"

                // ID Info
                binding.tvIdInfo.text = "${guest.idType}: ${guest.idNumber}"

                // Address
                binding.tvAddress.text = guest.address

                // Click listener
                binding.root.setOnClickListener {
                    onItemClick(bookingWithGuest)
                }
            }
        }
    }

    class GuestDiffCallback : DiffUtil.ItemCallback<BookingWithGuest>() {
        override fun areItemsTheSame(oldItem: BookingWithGuest, newItem: BookingWithGuest): Boolean {
            return oldItem.booking.id == newItem.booking.id
        }

        override fun areContentsTheSame(oldItem: BookingWithGuest, newItem: BookingWithGuest): Boolean {
            return oldItem == newItem
        }
    }
}

