package com.example.sai

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

        lifecycleScope.launch {
            roomVm.rooms.collectLatest { adapterRooms.submitList(it) }
        }
    }
}
