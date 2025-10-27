package com.example.sai

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sai.data.entity.RoomEntity
import com.example.sai.databinding.ActivityAddRoomBinding
import com.example.sai.viewmodel.RoomViewModel
import com.example.sai.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddRoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddRoomBinding
    private val roomVm: RoomViewModel by viewModels { ViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.title = "Add Room"
        binding.topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val types = listOf("Deluxe", "Suite", "Standard", "Multi Deluxe")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, types)
        binding.spType.setAdapter(adapter)

        binding.btnAdd.setOnClickListener {
            val number = binding.etRoomNumber.text?.toString()?.toIntOrNull()
            val price = binding.etPrice.text?.toString()?.toIntOrNull()
            val type = binding.spType.text?.toString() ?: ""
            if (number == null || price == null || type.isBlank()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            CoroutineScope(Dispatchers.Main).launch {
                val existing = roomVm.getRoom(number)
                if (existing != null) {
                    Toast.makeText(this@AddRoomActivity, "Room already exists", Toast.LENGTH_SHORT).show()
                } else {
                    roomVm.addRoom(RoomEntity(number, type, price)) { err ->
                        Toast.makeText(this@AddRoomActivity, err.message ?: "Error", Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(this@AddRoomActivity, "Room added successfully", Toast.LENGTH_SHORT).show()
                    binding.etRoomNumber.text?.clear()
                    binding.etPrice.text?.clear()
                }
            }
        }

        binding.btnUpdate.setOnClickListener {
            val number = binding.etRoomNumber.text?.toString()?.toIntOrNull()
            val price = binding.etPrice.text?.toString()?.toIntOrNull()
            val type = binding.spType.text?.toString() ?: ""
            if (number == null || price == null || type.isBlank()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            CoroutineScope(Dispatchers.Main).launch {
                val existing = roomVm.getRoom(number)
                if (existing == null) {
                    Toast.makeText(this@AddRoomActivity, "Room not found", Toast.LENGTH_SHORT).show()
                } else {
                    roomVm.updateRoom(existing.copy(type = type, price = price))
                    Toast.makeText(this@AddRoomActivity, "Room updated", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            val number = binding.etRoomNumber.text?.toString()?.toIntOrNull()
            if (number == null) {
                Toast.makeText(this, "Enter room number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            CoroutineScope(Dispatchers.Main).launch {
                val existing = roomVm.getRoom(number)
                if (existing == null) {
                    Toast.makeText(this@AddRoomActivity, "Room not found", Toast.LENGTH_SHORT).show()
                } else {
                    roomVm.deleteRoom(existing)
                    Toast.makeText(this@AddRoomActivity, "Room deleted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
