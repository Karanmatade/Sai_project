package com.example.sai

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import com.example.sai.data.entity.BookingWithGuest
import com.example.sai.databinding.ActivityAllGuestsBinding
import com.example.sai.viewmodel.BookingViewModel
import com.example.sai.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class AllGuestsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllGuestsBinding
    private val bookingVm: BookingViewModel by viewModels { ViewModelFactory(this) }
    private lateinit var adapterGuests: GuestsAdapter
    private var allGuests: List<BookingWithGuest> = emptyList()
    private var selectedDate: String? = null
    private var currentFilter: String = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllGuestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.title = "Guest Details"
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupRecyclerView()
        setupSearch()
        setupDateFilter()
        setupFilterChips()
        observeGuests()
    }

    private fun setupRecyclerView() {
        adapterGuests = GuestsAdapter { bookingWithGuest ->
            // Navigate to guest info screen
            val intent = Intent(this, GuestInfoActivity::class.java).apply {
                putExtra(GuestInfoActivity.EXTRA_BOOKING_ID, bookingWithGuest.booking.id)
            }
            startActivity(intent)
        }
        binding.recyclerGuests.layoutManager = LinearLayoutManager(this)
        binding.recyclerGuests.adapter = adapterGuests
    }

    private fun setupSearch() {
        binding.etSearchGuests.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterGuests()
            }
        })
    }

    private fun setupDateFilter() {
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnClearDate.setOnClickListener {
            selectedDate = null
            binding.tvSelectedDate.text = "All Dates"
            filterGuests()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedDate = dateFormat.format(calendar.time)
                binding.tvSelectedDate.text = selectedDate
                filterGuests()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun setupFilterChips() {
        binding.chipFilterAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentFilter = "All"
                updateChipBackgrounds()
                filterGuests()
            }
        }

        binding.chipFilterToday.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentFilter = "Today"
                updateChipBackgrounds()
                filterGuests()
            }
        }

        binding.chipFilterThisWeek.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentFilter = "This Week"
                updateChipBackgrounds()
                filterGuests()
            }
        }
    }

    private fun updateChipBackgrounds() {
        val allChips = listOf(
            binding.chipFilterAll,
            binding.chipFilterToday,
            binding.chipFilterThisWeek
        )

        allChips.forEach { chip ->
            val isActive = when (chip.id) {
                binding.chipFilterAll.id -> currentFilter == "All"
                binding.chipFilterToday.id -> currentFilter == "Today"
                binding.chipFilterThisWeek.id -> currentFilter == "This Week"
                else -> false
            }

            chip.background = if (isActive) {
                getDrawable(com.example.sai.R.drawable.bg_chip_light_active)
            } else {
                getDrawable(com.example.sai.R.drawable.bg_chip_light_inactive)
            }

            chip.setTextColor(
                if (isActive) {
                    getColor(com.example.sai.R.color.accent_green)
                } else {
                    getColor(com.example.sai.R.color.text_secondary)
                }
            )
        }
    }

    private fun observeGuests() {
        lifecycleScope.launch {
            bookingVm.bookingsWithGuests.collect { bookings ->
                allGuests = bookings.filter { it.guest != null }
                filterGuests()
            }
        }
    }

    private fun filterGuests() {
        if (!::adapterGuests.isInitialized) return

        val searchQuery = binding.etSearchGuests.text.toString().lowercase()
        var filtered = allGuests

        // Filter by search query
        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter { bookingWithGuest ->
                val guest = bookingWithGuest.guest
                guest != null && (
                    guest.firstName.lowercase().contains(searchQuery) ||
                    guest.lastName.lowercase().contains(searchQuery) ||
                    guest.folioNumber.lowercase().contains(searchQuery) ||
                    guest.idNumber.lowercase().contains(searchQuery) ||
                    guest.address.lowercase().contains(searchQuery)
                )
            }
        }

        // Filter by date
        if (selectedDate != null) {
            filtered = filtered.filter { it.booking.checkInDate == selectedDate }
        } else {
            // Apply quick filter
            when (currentFilter) {
                "Today" -> {
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    filtered = filtered.filter { it.booking.checkInDate == today }
                }
                "This Week" -> {
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    val today = calendar.time
                    calendar.add(Calendar.DAY_OF_YEAR, 7)
                    val nextWeek = calendar.time
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    
                    filtered = filtered.filter { bookingWithGuest ->
                        try {
                            val checkInDate = dateFormat.parse(bookingWithGuest.booking.checkInDate)
                            checkInDate != null && checkInDate >= today && checkInDate <= nextWeek
                        } catch (e: Exception) {
                            false
                        }
                    }
                }
            }
        }

        adapterGuests.submitList(filtered)
    }
}

