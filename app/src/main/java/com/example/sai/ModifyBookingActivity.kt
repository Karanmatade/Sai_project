package com.example.sai

import android.app.DatePickerDialog
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sai.data.entity.BookingEntity
import com.example.sai.databinding.ActivityModifyBookingBinding
import com.example.sai.viewmodel.BookingViewModel
import com.example.sai.viewmodel.RoomViewModel
import com.example.sai.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ModifyBookingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityModifyBookingBinding
    private val bookingVm: BookingViewModel by viewModels { ViewModelFactory(this) }
    private val roomVm: RoomViewModel by viewModels { ViewModelFactory(this) }
    private var selectedBooking: BookingEntity? = null
    private val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var latest: List<BookingEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.title = "Modify Booking"
        binding.topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line)
        binding.spBookings.setAdapter(adapter)

        binding.spBookings.setOnItemClickListener { _, _, position, _ ->
            selectedBooking = latest.getOrNull(position)
        }

        animateCardsEntry()
        setupButtonAnimations()

        lifecycleScope.launch {
            bookingVm.bookings.collectLatest { list ->
                latest = list
                adapter.clear()
                adapter.addAll(list.map { "#${it.id} • Room ${it.roomNumber} • ${it.status}" })
                if (list.isNotEmpty()) {
                    selectedBooking = list[0]
                }
            }
        }

        binding.btnExtend.setOnClickListener { view ->
            val b = selectedBooking
            if (b == null) {
                Toast.makeText(this, "Please select a booking", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val currentOut = df.parse(b.checkOutDate)
            if (currentOut == null) {
                Toast.makeText(this, "Invalid checkout date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            animateButtonPress(view) {
                pickDate(minDate = currentOut.time + 24 * 60 * 60 * 1000) { newDate ->
                    val updated = b.copy(checkOutDate = newDate)
                    bookingVm.updateBooking(updated)
                    Toast.makeText(this@ModifyBookingActivity, "Booking extended", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnCancel.setOnClickListener { view ->
            val b = selectedBooking
            if (b == null) {
                Toast.makeText(this, "Please select a booking", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (b.status == "Cancelled") { 
                Toast.makeText(this, "Already cancelled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener 
            }
            animateButtonPress(view) {
                val updated = b.copy(status = "Cancelled")
                bookingVm.updateBooking(updated)
                roomVm.setBooked(b.roomNumber, false)
                Toast.makeText(this@ModifyBookingActivity, "Booking cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun animateCardsEntry() {
        val card = binding.searchBarCard
        card.alpha = 0f
        card.translationY = 50f
        
        Handler(Looper.getMainLooper()).postDelayed({
            card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .start()
        }, 100)
    }

    private fun setupButtonAnimations() {
        binding.spBookings.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.animate()
                    .scaleX(1.02f)
                    .scaleY(1.02f)
                    .setDuration(200)
                    .start()
            } else {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start()
            }
        }
    }

    private fun animateButtonPress(view: View, action: () -> Unit) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .setListener(null)
                        .start()
                    action()
                }
            })
            .start()
    }

    private fun pickDate(minDate: Long? = null, onPicked: (String) -> Unit) {
        val c = Calendar.getInstance()
        val dlg = DatePickerDialog(this, { _, y, m, d ->
            val cal = Calendar.getInstance().apply { set(y, m, d, 0, 0, 0); set(Calendar.MILLISECOND, 0) }
            onPicked(df.format(cal.time))
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        if (minDate != null) dlg.datePicker.minDate = minDate
        dlg.show()
    }
}
