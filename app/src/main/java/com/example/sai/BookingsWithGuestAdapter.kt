package com.example.sai

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sai.data.entity.BookingWithGuest
import com.example.sai.databinding.ItemBookingCardBinding

class BookingsWithGuestAdapter(
    private val onItemClick: (BookingWithGuest) -> Unit
) : ListAdapter<BookingWithGuest, BookingsWithGuestAdapter.BookingViewHolder>(BookingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookingViewHolder(
        private val binding: ItemBookingCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bookingWithGuest: BookingWithGuest) {
            val booking = bookingWithGuest.booking
            val guest = bookingWithGuest.guest

            binding.tvRoomNumber.text = "Room #${booking.roomNumber}"
            binding.tvDates.text = "${booking.checkInDate} → ${booking.checkOutDate}"
            binding.tvStatus.text = booking.status
            
            // Display total price
            binding.tvTotalPrice.text = if (booking.totalPrice > 0) {
                "Total Price: ₹${booking.totalPrice}"
            } else {
                "Total Price: ₹--"
            }
            
            // Display payment method
            binding.tvPaymentMethod.text = "Payment: ${booking.paymentMethod}"
            
            // Show guest name if available
            if (guest != null) {
                binding.tvGuestName.text = "${guest.firstName} ${guest.lastName}"
                binding.tvGuestName.visibility = android.view.View.VISIBLE
            } else {
                binding.tvGuestName.visibility = android.view.View.GONE
            }

            binding.root.setOnClickListener {
                onItemClick(bookingWithGuest)
            }
        }
    }

    class BookingDiffCallback : DiffUtil.ItemCallback<BookingWithGuest>() {
        override fun areItemsTheSame(oldItem: BookingWithGuest, newItem: BookingWithGuest): Boolean {
            return oldItem.booking.id == newItem.booking.id
        }

        override fun areContentsTheSame(oldItem: BookingWithGuest, newItem: BookingWithGuest): Boolean {
            return oldItem == newItem
        }
    }
}
