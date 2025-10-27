package com.example.sai

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sai.databinding.ActivityAllBookingsBinding
import com.example.sai.viewmodel.BookingViewModel
import com.example.sai.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AllBookingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllBookingsBinding
    private val bookingVm: BookingViewModel by viewModels { ViewModelFactory(this) }
    private lateinit var adapterBookings: BookingsWithGuestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllBookingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.title = "All Bookings"
        binding.topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        adapterBookings = BookingsWithGuestAdapter { bookingWithGuest ->
            val intent = Intent(this, BookingDetailsActivity::class.java)
            intent.putExtra(BookingDetailsActivity.EXTRA_BOOKING_ID, bookingWithGuest.booking.id)
            startActivity(intent)
        }
        binding.recyclerBookings.layoutManager = LinearLayoutManager(this)
        binding.recyclerBookings.adapter = adapterBookings

        lifecycleScope.launch {
            bookingVm.bookingsWithGuests.collectLatest { adapterBookings.submitList(it) }
        }
    }
}
