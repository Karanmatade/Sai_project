package com.example.sai

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sai.data.entity.BookingWithGuest
import com.example.sai.databinding.ItemBookingCardBinding
import java.text.NumberFormat
import java.util.Locale

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
            
            // Display total price with formatting
            binding.tvTotalPrice.text = if (booking.totalPrice > 0) {
                "Total Price: ₹${NumberFormat.getNumberInstance(Locale.getDefault()).format(booking.totalPrice)}"
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

            // Update status chip with light green theme
            val status = booking.status.lowercase()
            when {
                status.contains("active", ignoreCase = true) -> {
                    binding.tvStatus.background = ContextCompat.getDrawable(
                        binding.root.context, R.drawable.bg_badge_light_available
                    )
                    binding.tvStatus.setTextColor(ContextCompat.getColor(
                        binding.root.context, R.color.success_green
                    ))
                    binding.tvStatus.setChipStrokeColorResource(R.color.success_green)
                    binding.tvStatus.setChipStrokeWidth(1.5f)
                }
                status.contains("completed", ignoreCase = true) || status.contains("finished", ignoreCase = true) -> {
                    binding.tvStatus.background = ContextCompat.getDrawable(
                        binding.root.context, R.drawable.bg_chip_light_inactive
                    )
                    binding.tvStatus.setTextColor(ContextCompat.getColor(
                        binding.root.context, R.color.text_secondary
                    ))
                    binding.tvStatus.setChipStrokeColorResource(R.color.text_tertiary)
                    binding.tvStatus.setChipStrokeWidth(1f)
                }
                status.contains("cancelled", ignoreCase = true) || status.contains("canceled", ignoreCase = true) -> {
                    binding.tvStatus.background = ContextCompat.getDrawable(
                        binding.root.context, R.drawable.bg_badge_light_booked
                    )
                    binding.tvStatus.setTextColor(ContextCompat.getColor(
                        binding.root.context, R.color.neon_pink
                    ))
                    binding.tvStatus.setChipStrokeColorResource(R.color.neon_pink)
                    binding.tvStatus.setChipStrokeWidth(1.5f)
                }
                else -> {
                    // Default to green for other statuses
                    binding.tvStatus.background = ContextCompat.getDrawable(
                        binding.root.context, R.drawable.bg_badge_light_available
                    )
                    binding.tvStatus.setTextColor(ContextCompat.getColor(
                        binding.root.context, R.color.accent_green
                    ))
                    binding.tvStatus.setChipStrokeColorResource(R.color.accent_green)
                    binding.tvStatus.setChipStrokeWidth(1.5f)
                }
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
