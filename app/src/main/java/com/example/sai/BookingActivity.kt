package com.example.sai

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sai.data.entity.BookingEntity
import com.example.sai.databinding.ActivityBookingBinding
import com.example.sai.viewmodel.BookingViewModel
import com.example.sai.viewmodel.RoomViewModel
import com.example.sai.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

class BookingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookingBinding
    private val roomVm: RoomViewModel by viewModels { ViewModelFactory(this) }
    private val bookingVm: BookingViewModel by viewModels { ViewModelFactory(this) }

    private var selectedRoom: Int? = null
    private var selectedRoomPrice: Int = 0
    private var checkIn: String? = null
    private var checkOut: String? = null
    private val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var latestRooms: List<com.example.sai.data.entity.RoomEntity> = emptyList()
    private var currentBookingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.title = "Book Room"
        binding.topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val roomsAdapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line)
        binding.spRooms.setAdapter(roomsAdapter)

        binding.spRooms.setOnItemClickListener { _, _, position, _ ->
            val selectedRoomEntity = latestRooms.getOrNull(position)
            selectedRoom = selectedRoomEntity?.roomNumber
            selectedRoomPrice = selectedRoomEntity?.price ?: 0
            updatePriceDisplay()
        }

        lifecycleScope.launch {
            roomVm.availableRooms.collectLatest { rooms ->
                latestRooms = rooms
                roomsAdapter.clear()
                roomsAdapter.addAll(rooms.map { "${it.roomNumber} - ${it.type} (₹${it.price})" })
                if (rooms.isNotEmpty()) {
                    selectedRoom = rooms[0].roomNumber
                    selectedRoomPrice = rooms[0].price
                    updatePriceDisplay()
                }
            }
        }

        // Initially disable the Add Guest Details button
        binding.btnAddGuestDetails.isEnabled = false

        binding.btnCheckIn.setOnClickListener { pickDate { date -> checkIn = date; binding.tvCheckIn.text = date; updatePriceDisplay() } }
        binding.btnCheckOut.setOnClickListener { pickDate { date -> checkOut = date; binding.tvCheckOut.text = date; updatePriceDisplay() } }

        binding.btnBookNow.setOnClickListener {
            val roomNo = selectedRoom
            val inD = checkIn
            val outD = checkOut
            if (roomNo == null || inD.isNullOrBlank() || outD.isNullOrBlank()) {
                Toast.makeText(this, "Select room and dates", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            // validate: out > in
            val inDate = df.parse(inD)!!
            val outDate = df.parse(outD)!!
            if (!outDate.after(inDate)) {
                Toast.makeText(this, "Checkout must be after Checkin", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            val totalPrice = calculateTotalPrice()
            val booking = BookingEntity(roomNumber = roomNo, checkInDate = inD, checkOutDate = outD, status = "Active", totalPrice = totalPrice)
            bookingVm.addBooking(booking) { bookingId ->
                currentBookingId = bookingId
                roomVm.setBooked(roomNo, true)
                Toast.makeText(this, "Booking successful!", Toast.LENGTH_SHORT).show()
                binding.btnAddGuestDetails.isEnabled = true
            }
        }

        binding.btnAddGuestDetails.setOnClickListener {
            val bookingId = currentBookingId
            if (bookingId != null && bookingId > 0) {
                val intent = Intent(this, GuestDetailsActivity::class.java)
                intent.putExtra(GuestDetailsActivity.EXTRA_BOOKING_ID, bookingId.toInt())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please create a booking first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickDate(onPicked: (String) -> Unit) {
        val c = Calendar.getInstance()
        val dlg = DatePickerDialog(this, { _, y, m, d ->
            val cal = Calendar.getInstance().apply { set(y, m, d, 0, 0, 0); set(Calendar.MILLISECOND, 0) }
            onPicked(df.format(cal.time))
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        dlg.datePicker.minDate = System.currentTimeMillis()
        dlg.show()
    }

    private fun updatePriceDisplay() {
        binding.tvPricePerDay.text = if (selectedRoomPrice > 0) "₹$selectedRoomPrice" else "₹--"
        
        val days = calculateNumberOfDays()
        binding.tvNumberOfDays.text = if (days > 0) days.toString() else "--"
        
        val totalPrice = calculateTotalPrice()
        binding.tvTotalPrice.text = if (totalPrice > 0) "₹$totalPrice" else "₹--"
    }

    private fun calculateNumberOfDays(): Long {
        val inDate = checkIn
        val outDate = checkOut
        
        return if (!inDate.isNullOrBlank() && !outDate.isNullOrBlank()) {
            try {
                val checkInLocalDate = LocalDate.parse(inDate)
                val checkOutLocalDate = LocalDate.parse(outDate)
                ChronoUnit.DAYS.between(checkInLocalDate, checkOutLocalDate)
            } catch (e: Exception) {
                0L
            }
        } else {
            0L
        }
    }

    private fun calculateTotalPrice(): Int {
        val days = calculateNumberOfDays()
        return if (days > 0 && selectedRoomPrice > 0) {
            (days * selectedRoomPrice).toInt()
        } else {
            0
        }
    }
}
