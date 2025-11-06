package com.example.sai

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sai.databinding.ActivityGuestInfoBinding
import com.example.sai.viewmodel.BookingViewModel
import com.example.sai.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class GuestInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGuestInfoBinding
    private val bookingVm: BookingViewModel by viewModels { ViewModelFactory(this) }
    
    companion object {
        const val EXTRA_BOOKING_ID = "booking_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuestInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.title = "Guest Information"
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val bookingId = intent.getIntExtra(EXTRA_BOOKING_ID, -1)
        if (bookingId == -1) {
            finish()
            return
        }

        loadGuestInfo(bookingId)
    }

    private fun loadGuestInfo(bookingId: Int) {
        lifecycleScope.launch {
            val bookingWithGuest = bookingVm.getBookingWithGuest(bookingId)
            bookingWithGuest?.let {
                displayGuestInfo(it)
            }
        }
    }

    private fun displayGuestInfo(bookingWithGuest: com.example.sai.data.entity.BookingWithGuest) {
        val booking = bookingWithGuest.booking
        val guest = bookingWithGuest.guest

        if (guest != null) {
            // Guest Information
            binding.tvGuestName.text = "${guest.firstName} ${guest.lastName}"
            binding.tvFolioNumber.text = guest.folioNumber
            binding.tvAddress.text = guest.address
            binding.tvCompany.text = if (guest.company.isNotEmpty()) guest.company else "N/A"

            // Identification Information
            binding.tvIdType.text = guest.idType
            binding.tvIdNumber.text = guest.idNumber

            // Vehicle Information
            binding.tvVehicle.text = if (guest.vehicle.isNotEmpty()) guest.vehicle else "N/A"
            binding.tvVehicleModel.text = if (guest.vehicleModel.isNotEmpty()) guest.vehicleModel else "N/A"
            binding.tvPlateNumber.text = if (guest.plateNumber.isNotEmpty()) guest.plateNumber else "N/A"

            // Booking Information
            binding.tvRoomNumber.text = "Room #${booking.roomNumber}"
            binding.tvCheckInDate.text = booking.checkInDate
            binding.tvCheckOutDate.text = booking.checkOutDate
            
            // Status with color
            binding.tvStatus.text = booking.status
            val statusColor = when (booking.status.lowercase()) {
                "active" -> getColor(com.example.sai.R.color.accent_green)
                "cancelled", "canceled" -> getColor(com.example.sai.R.color.error_red)
                else -> getColor(com.example.sai.R.color.text_secondary)
            }
            binding.tvStatus.setTextColor(statusColor)

            // Show all cards
            binding.cardGuestInfo.visibility = android.view.View.VISIBLE
            binding.cardIdInfo.visibility = android.view.View.VISIBLE
            binding.cardVehicleInfo.visibility = android.view.View.VISIBLE
            binding.cardBookingInfo.visibility = android.view.View.VISIBLE
        } else {
            // Hide guest details if no guest info
            binding.cardGuestInfo.visibility = android.view.View.GONE
            binding.cardIdInfo.visibility = android.view.View.GONE
            binding.cardVehicleInfo.visibility = android.view.View.GONE
            binding.cardBookingInfo.visibility = android.view.View.GONE
        }
    }
}

