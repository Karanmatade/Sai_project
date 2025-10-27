package com.example.sai

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.sai.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.title = "Admin Dashboard"

        binding.cardAddRoom.setOnClickListener { startActivity(Intent(this, AddRoomActivity::class.java)) }
        binding.cardAllRooms.setOnClickListener { startActivity(Intent(this, AllRoomsActivity::class.java)) }
        binding.cardBookRoom.setOnClickListener { startActivity(Intent(this, BookingActivity::class.java)) }
        binding.cardAllBookings.setOnClickListener { startActivity(Intent(this, AllBookingsActivity::class.java)) }
        binding.cardModifyBooking.setOnClickListener { startActivity(Intent(this, ModifyBookingActivity::class.java)) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
