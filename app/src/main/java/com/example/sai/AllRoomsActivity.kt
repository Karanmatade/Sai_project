package com.example.sai

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.OvershootInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sai.databinding.ActivityAllRoomsBinding
import com.example.sai.viewmodel.RoomViewModel
import com.example.sai.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class AllRoomsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllRoomsBinding
    private val roomVm: RoomViewModel by viewModels { ViewModelFactory(this) }
    private lateinit var adapterRooms: RoomsAdapter
    private var allRooms = listOf<com.example.sai.data.entity.RoomEntity>()
    private var currentFilter: String = "All" // "All", "Available", "Booked"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllRoomsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.title = "All Rooms"
        binding.topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        adapterRooms = RoomsAdapter(
            onEdit = { room -> /* Could open AddRoom prefilled; for now, no-op */ },
            onDelete = { room -> roomVm.deleteRoom(room) }
        )
        binding.recyclerRooms.layoutManager = LinearLayoutManager(this)
        binding.recyclerRooms.adapter = adapterRooms

        // Setup filter chips with animations
        binding.chipFilterAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateChipBackgrounds(binding.chipFilterAll, true)
                updateChipBackgrounds(binding.chipFilterAvailable, false)
                updateChipBackgrounds(binding.chipFilterBooked, false)
                currentFilter = "All"
                applyFilters()
            }
        }

        binding.chipFilterAvailable.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateChipBackgrounds(binding.chipFilterAll, false)
                updateChipBackgrounds(binding.chipFilterAvailable, true)
                updateChipBackgrounds(binding.chipFilterBooked, false)
                currentFilter = "Available"
                applyFilters()
            }
        }

        binding.chipFilterBooked.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateChipBackgrounds(binding.chipFilterAll, false)
                updateChipBackgrounds(binding.chipFilterAvailable, false)
                updateChipBackgrounds(binding.chipFilterBooked, true)
                currentFilter = "Booked"
                applyFilters()
            }
        }
        
        // Set initial state
        updateChipBackgrounds(binding.chipFilterAll, true)

        // Setup search functionality
        binding.etSearchRooms.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        lifecycleScope.launch {
            roomVm.rooms.collectLatest { rooms ->
                allRooms = rooms
                applyFilters()
            }
        }
        
        // Setup animations
        animateSearchBarEntry()
    }
    
    private fun animateSearchBarEntry() {
        binding.searchBarCard.alpha = 0f
        binding.searchBarCard.translationY = -30f
        binding.searchBarCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setInterpolator(OvershootInterpolator(1.1f))
            .start()
    }

    private fun applyFilters() {
        if (!::adapterRooms.isInitialized) return
        
        val query = binding.etSearchRooms.text?.toString() ?: ""
        var filtered = allRooms

        // Apply status filter
        filtered = when (currentFilter) {
            "Available" -> filtered.filter { !it.isBooked }
            "Booked" -> filtered.filter { it.isBooked }
            else -> filtered
        }

        // Apply search query
        if (query.isNotBlank()) {
            filtered = filtered.filter { room ->
                room.roomNumber.toString().contains(query, ignoreCase = true) ||
                room.type.contains(query, ignoreCase = true) ||
                room.price.toString().contains(query, ignoreCase = true)
            }
        }

        adapterRooms.submitList(filtered)
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
