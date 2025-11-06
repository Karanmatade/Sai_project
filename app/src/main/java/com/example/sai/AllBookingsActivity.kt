package com.example.sai

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.OvershootInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private var allBookings = listOf<com.example.sai.data.entity.BookingWithGuest>()
    private var currentFilter: String = "All" // "All", "Active", "Completed"

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

        // Setup filter chips with animations
        binding.chipFilterAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateChipBackgrounds(binding.chipFilterAll, true)
                updateChipBackgrounds(binding.chipFilterActive, false)
                updateChipBackgrounds(binding.chipFilterCompleted, false)
                currentFilter = "All"
                filterBookings(binding.etSearchBookings.text.toString())
            }
        }

        binding.chipFilterActive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateChipBackgrounds(binding.chipFilterAll, false)
                updateChipBackgrounds(binding.chipFilterActive, true)
                updateChipBackgrounds(binding.chipFilterCompleted, false)
                currentFilter = "Active"
                filterBookings(binding.etSearchBookings.text.toString())
            }
        }

        binding.chipFilterCompleted.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateChipBackgrounds(binding.chipFilterAll, false)
                updateChipBackgrounds(binding.chipFilterActive, false)
                updateChipBackgrounds(binding.chipFilterCompleted, true)
                currentFilter = "Completed"
                filterBookings(binding.etSearchBookings.text.toString())
            }
        }

        // Setup search functionality
        binding.etSearchBookings.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterBookings(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        lifecycleScope.launch {
            bookingVm.bookingsWithGuests.collectLatest { bookings ->
                allBookings = bookings
                filterBookings(binding.etSearchBookings.text.toString())
            }
        }
    }

    private fun filterBookings(query: String) {
        val filtered = if (query.isBlank()) {
            allBookings
        } else {
            allBookings.filter { bookingWithGuest ->
                val booking = bookingWithGuest.booking
                val guest = bookingWithGuest.guest
                booking.roomNumber.toString().contains(query, ignoreCase = true) ||
                booking.checkInDate.contains(query, ignoreCase = true) ||
                booking.checkOutDate.contains(query, ignoreCase = true) ||
                booking.status.contains(query, ignoreCase = true) ||
                (guest?.firstName?.contains(query, ignoreCase = true) == true) ||
                (guest?.lastName?.contains(query, ignoreCase = true) == true)
            }
        }
        
        // Apply status filter
        val statusFiltered = if (currentFilter == "All") {
            filtered
        } else {
            filtered.filter { bookingWithGuest ->
                bookingWithGuest.booking.status.equals(currentFilter, ignoreCase = true)
            }
        }
        
        adapterBookings.submitList(statusFiltered)
    }
    
    private fun updateChipBackgrounds(chip: com.google.android.material.chip.Chip, isActive: Boolean) {
        if (isActive) {
            chip.background = ContextCompat.getDrawable(this, R.drawable.bg_chip_light_active)
            chip.elevation = 2f
            chip.animate()
                .scaleX(1.08f)
                .scaleY(1.08f)
                .setDuration(250)
                .setInterpolator(OvershootInterpolator(1.2f))
                .withEndAction {
                    chip.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(150)
                        .start()
                }
                .start()
        } else {
            chip.background = ContextCompat.getDrawable(this, R.drawable.bg_chip_light_inactive)
            chip.elevation = 0f
            chip.scaleX = 1.0f
            chip.scaleY = 1.0f
        }
    }
}
