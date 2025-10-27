package com.example.sai

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sai.data.entity.GuestEntity
import com.example.sai.databinding.ActivityGuestDetailsBinding
import com.example.sai.viewmodel.BookingViewModel
import com.example.sai.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class GuestDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGuestDetailsBinding
    private val bookingVm: BookingViewModel by viewModels { ViewModelFactory(this) }
    
    companion object {
        const val EXTRA_BOOKING_ID = "booking_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuestDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.title = "Guest Details"
        binding.topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val bookingId = intent.getIntExtra(EXTRA_BOOKING_ID, -1)
        if (bookingId == -1) {
            Toast.makeText(this, "Invalid booking ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupIdTypeSpinner()
        setupClickListeners(bookingId)
    }

    private fun setupIdTypeSpinner() {
        val idTypes = arrayOf("Aadhar Card", "Passport", "Driving License", "Voter ID")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, idTypes)
        binding.spIdType.setAdapter(adapter)
    }

    private fun setupClickListeners(bookingId: Int) {
        binding.btnSaveDetails.setOnClickListener {
            saveGuestDetails(bookingId)
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveGuestDetails(bookingId: Int) {
        val folioNumber = binding.etFolioNumber.text?.toString()?.trim() ?: ""
        val lastName = binding.etLastName.text?.toString()?.trim() ?: ""
        val firstName = binding.etFirstName.text?.toString()?.trim() ?: ""
        val address = binding.etAddress.text?.toString()?.trim() ?: ""
        val company = binding.etCompany.text?.toString()?.trim() ?: ""
        val idType = binding.spIdType.text?.toString()?.trim() ?: ""
        val idNumber = binding.etIdNumber.text?.toString()?.trim() ?: ""
        val vehicle = binding.etVehicle.text?.toString()?.trim() ?: ""
        val vehicleModel = binding.etVehicleModel.text?.toString()?.trim() ?: ""
        val plateNumber = binding.etPlateNumber.text?.toString()?.trim() ?: ""
        
        // Get selected payment method
        val paymentMethod = if (binding.rbCash.isChecked) "Cash" else "Online"

        // Validate required fields
        if (folioNumber.isEmpty() || lastName.isEmpty() || firstName.isEmpty() || 
            address.isEmpty() || idType.isEmpty() || idNumber.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val guest = GuestEntity(
            bookingId = bookingId,
            folioNumber = folioNumber,
            lastName = lastName,
            firstName = firstName,
            address = address,
            company = company,
            idType = idType,
            idNumber = idNumber,
            vehicle = vehicle,
            vehicleModel = vehicleModel,
            plateNumber = plateNumber
        )

        bookingVm.addGuest(guest) { guestId ->
            if (guestId > 0) {
                // Update booking with payment method
                bookingVm.updateBookingPaymentMethod(bookingId, paymentMethod) {
                    Snackbar.make(binding.root, "Guest Details Saved Successfully", Snackbar.LENGTH_LONG).show()
                    finish()
                }
            } else {
                Toast.makeText(this, "Failed to save guest details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
