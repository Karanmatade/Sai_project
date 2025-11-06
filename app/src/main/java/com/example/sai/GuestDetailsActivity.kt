package com.example.sai

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
        animateCardsEntry()
        setupInputFieldAnimations()
    }

    private fun setupIdTypeSpinner() {
        val idTypes = arrayOf("Aadhar Card", "Passport", "Driving License", "Voter ID")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, idTypes)
        binding.spIdType.setAdapter(adapter)
    }

    private fun setupClickListeners(bookingId: Int) {
        binding.btnSaveDetails.setOnClickListener { view ->
            animateButtonPress(view) {
                saveGuestDetails(bookingId)
            }
        }

        binding.btnCancel.setOnClickListener { view ->
            animateButtonPress(view) {
                finish()
            }
        }
    }

    private fun animateCardsEntry() {
        val cards = listOf(
            binding.cardGuestInfo,
            binding.cardIdInfo,
            binding.cardVehicleInfo,
            binding.cardPaymentInfo
        )

        cards.forEachIndexed { index, card ->
            card.alpha = 0f
            card.translationY = 50f
            
            Handler(Looper.getMainLooper()).postDelayed({
                card.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(400)
                    .setStartDelay((index * 100).toLong())
                    .setInterpolator(android.view.animation.DecelerateInterpolator())
                    .start()
            }, (index * 100).toLong())
        }

        // Animate buttons
        Handler(Looper.getMainLooper()).postDelayed({
            val buttonContainer = binding.btnCancel.parent as View
            buttonContainer.alpha = 0f
            buttonContainer.translationY = 30f
            buttonContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .start()
        }, 500)
    }

    private fun setupInputFieldAnimations() {
        val inputFields = listOf(
            binding.etFolioNumber,
            binding.etLastName,
            binding.etFirstName,
            binding.etAddress,
            binding.etCompany,
            binding.spIdType,
            binding.etIdNumber,
            binding.etVehicle,
            binding.etVehicleModel,
            binding.etPlateNumber
        )

        inputFields.forEach { field ->
            field.setOnFocusChangeListener { view, hasFocus ->
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

    private fun animateSuccessFeedback(action: () -> Unit) {
        val cards = listOf(
            binding.cardGuestInfo,
            binding.cardIdInfo,
            binding.cardVehicleInfo,
            binding.cardPaymentInfo
        )

        cards.forEachIndexed { index, card ->
            Handler(Looper.getMainLooper()).postDelayed({
                card.animate()
                    .scaleX(1.05f)
                    .scaleY(1.05f)
                    .setDuration(150)
                    .withEndAction {
                        card.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150)
                            .start()
                    }
                    .start()
            }, (index * 50).toLong())
        }

        Handler(Looper.getMainLooper()).postDelayed({
            action()
        }, 300)
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
                    // Show success animation
                    animateSuccessFeedback {
                        Snackbar.make(binding.root, "Guest Details Saved Successfully", Snackbar.LENGTH_LONG).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            finish()
                        }, 500)
                    }
                }
            } else {
                Toast.makeText(this, "Failed to save guest details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
