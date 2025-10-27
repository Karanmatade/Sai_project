package com.example.sai

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sai.databinding.ActivityBookingDetailsBinding
import com.example.sai.viewmodel.BookingViewModel
import com.example.sai.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class BookingDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookingDetailsBinding
    private val bookingVm: BookingViewModel by viewModels { ViewModelFactory(this) }
    
    companion object {
        const val EXTRA_BOOKING_ID = "booking_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.title = "Booking Details"
        binding.topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val bookingId = intent.getIntExtra(EXTRA_BOOKING_ID, -1)
        if (bookingId == -1) {
            Toast.makeText(this, "Invalid booking ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadBookingDetails(bookingId)
    }

    private fun loadBookingDetails(bookingId: Int) {
        lifecycleScope.launch {
            val bookingWithGuest = bookingVm.getBookingWithGuest(bookingId)
            if (bookingWithGuest != null) {
                displayBookingDetails(bookingWithGuest)
            } else {
                Toast.makeText(this@BookingDetailsActivity, "Booking not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun displayBookingDetails(bookingWithGuest: com.example.sai.data.entity.BookingWithGuest) {
        val booking = bookingWithGuest.booking
        val guest = bookingWithGuest.guest

        // Room Information
        binding.tvRoomNumber.text = "Room #${booking.roomNumber}"
        binding.tvCheckInDate.text = booking.checkInDate
        binding.tvCheckOutDate.text = booking.checkOutDate
        binding.tvBookingStatus.text = booking.status

        if (guest != null) {
            // Guest Information
            binding.tvFolioNumber.text = guest.folioNumber
            binding.tvGuestName.text = "${guest.firstName} ${guest.lastName}"
            binding.tvAddress.text = guest.address
            binding.tvCompany.text = if (guest.company.isNotEmpty()) guest.company else "N/A"

            // Identification Information
            binding.tvIdType.text = guest.idType
            binding.tvIdNumber.text = guest.idNumber

            // Vehicle Information
            binding.tvVehicle.text = if (guest.vehicle.isNotEmpty()) guest.vehicle else "N/A"
            binding.tvVehicleModel.text = if (guest.vehicleModel.isNotEmpty()) guest.vehicleModel else "N/A"
            binding.tvPlateNumber.text = if (guest.plateNumber.isNotEmpty()) guest.plateNumber else "N/A"

            // Show guest details cards
            binding.cardGuestInfo.visibility = android.view.View.VISIBLE
            binding.cardIdInfo.visibility = android.view.View.VISIBLE
            binding.cardVehicleInfo.visibility = android.view.View.VISIBLE
        } else {
            // Hide guest details cards if no guest info
            binding.cardGuestInfo.visibility = android.view.View.GONE
            binding.cardIdInfo.visibility = android.view.View.GONE
            binding.cardVehicleInfo.visibility = android.view.View.GONE
        }
    }
}
