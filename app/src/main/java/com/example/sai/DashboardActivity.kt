package com.example.sai

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.sai.databinding.ActivityDashboardBinding
import com.example.sai.viewmodel.BookingViewModel
import com.example.sai.viewmodel.RoomViewModel
import com.example.sai.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private val roomVm: RoomViewModel by viewModels { ViewModelFactory(this) }
    private val bookingVm: BookingViewModel by viewModels { ViewModelFactory(this) }
    private var currentNavItem: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize UI
        setupBottomNavigation()
        setupCardAnimations()
        setupCardHoverEffects()
        
        // Load statistics
        loadStatistics()

        // Setup card click listeners
        binding.cardAddRoom.setOnClickListener { 
            animateCardClick(it)
            startActivity(Intent(this, AddRoomActivity::class.java))
        }
        binding.cardAllRooms.setOnClickListener { 
            animateCardClick(it)
            startActivity(Intent(this, AllRoomsActivity::class.java))
        }
        binding.cardBookRoom.setOnClickListener { 
            animateCardClick(it)
            startActivity(Intent(this, BookingActivity::class.java))
        }
        binding.cardAllBookings.setOnClickListener { 
            animateCardClick(it)
            startActivity(Intent(this, AllBookingsActivity::class.java))
        }
        binding.cardModifyBooking.setOnClickListener { 
            animateCardClick(it)
            startActivity(Intent(this, ModifyBookingActivity::class.java))
        }
        
        // FAB click listener
        binding.fabAddRoom.setOnClickListener { view ->
            animateFabClick(view)
            startActivity(Intent(this, AddRoomActivity::class.java))
        }
    }

    private fun setupBottomNavigation() {
        // Navigation items are now directly in the layout
        val navDashboard = binding.navDashboard
        val navRooms = binding.navRooms
        val navBookings = binding.navBookings
        val navGuests = binding.root.findViewById<android.view.View>(R.id.navGuests)
        val navMore = binding.navMore
        
        // Set Dashboard as active initially
        setActiveNavItem(navDashboard)
        
        navDashboard.setOnClickListener {
            setActiveNavItem(navDashboard)
            // Already on dashboard, just animate
        }
        
        navRooms.setOnClickListener {
            setActiveNavItem(navRooms)
            startActivity(Intent(this, AllRoomsActivity::class.java))
        }
        
        navBookings.setOnClickListener {
            setActiveNavItem(navBookings)
            startActivity(Intent(this, AllBookingsActivity::class.java))
        }
        
        navGuests.setOnClickListener {
            setActiveNavItem(navGuests)
            startActivity(Intent(this, AllGuestsActivity::class.java))
        }
        
        navMore.setOnClickListener {
            setActiveNavItem(navMore)
            // Could open a settings/profile dialog or activity
        }
    }

    private fun setActiveNavItem(item: View) {
        // Reset previous active item
        currentNavItem?.let { prevItem ->
            prevItem.background = ContextCompat.getDrawable(this, R.drawable.bg_nav_item_light_inactive)
            when (prevItem.id) {
                R.id.navDashboard -> {
                    binding.navDashboardIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_tertiary))
                    binding.navDashboardText.setTextColor(ContextCompat.getColor(this, R.color.text_tertiary))
                    binding.navDashboardText.setTypeface(null, android.graphics.Typeface.NORMAL)
                }
                R.id.navRooms -> {
                    binding.navRoomsIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_tertiary))
                    binding.navRoomsText.setTextColor(ContextCompat.getColor(this, R.color.text_tertiary))
                    binding.navRoomsText.setTypeface(null, android.graphics.Typeface.NORMAL)
                }
                R.id.navBookings -> {
                    binding.navBookingsIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_tertiary))
                    binding.navBookingsText.setTextColor(ContextCompat.getColor(this, R.color.text_tertiary))
                    binding.navBookingsText.setTypeface(null, android.graphics.Typeface.NORMAL)
                }
                R.id.navGuests -> {
                    val navGuestsIcon = binding.root.findViewById<android.widget.ImageView>(R.id.navGuestsIcon)
                    val navGuestsText = binding.root.findViewById<android.widget.TextView>(R.id.navGuestsText)
                    navGuestsIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_tertiary))
                    navGuestsText.setTextColor(ContextCompat.getColor(this, R.color.text_tertiary))
                    navGuestsText.setTypeface(null, android.graphics.Typeface.NORMAL)
                }
                R.id.navMore -> {
                    binding.navMoreIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_tertiary))
                    binding.navMoreText.setTextColor(ContextCompat.getColor(this, R.color.text_tertiary))
                    binding.navMoreText.setTypeface(null, android.graphics.Typeface.NORMAL)
                }
            }
        }

        // Set new active item
        item.background = ContextCompat.getDrawable(this, R.drawable.bg_nav_item_light_active)
        when (item.id) {
            R.id.navDashboard -> {
                binding.navDashboardIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_green))
                binding.navDashboardText.setTextColor(ContextCompat.getColor(this, R.color.accent_green))
                binding.navDashboardText.setTypeface(null, android.graphics.Typeface.BOLD)
            }
            R.id.navRooms -> {
                binding.navRoomsIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_green))
                binding.navRoomsText.setTextColor(ContextCompat.getColor(this, R.color.accent_green))
                binding.navRoomsText.setTypeface(null, android.graphics.Typeface.BOLD)
            }
            R.id.navBookings -> {
                binding.navBookingsIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_green))
                binding.navBookingsText.setTextColor(ContextCompat.getColor(this, R.color.accent_green))
                binding.navBookingsText.setTypeface(null, android.graphics.Typeface.BOLD)
            }
            R.id.navGuests -> {
                val navGuestsIcon = binding.root.findViewById<android.widget.ImageView>(R.id.navGuestsIcon)
                val navGuestsText = binding.root.findViewById<android.widget.TextView>(R.id.navGuestsText)
                navGuestsIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_green))
                navGuestsText.setTextColor(ContextCompat.getColor(this, R.color.accent_green))
                navGuestsText.setTypeface(null, android.graphics.Typeface.BOLD)
            }
            R.id.navMore -> {
                binding.navMoreIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_green))
                binding.navMoreText.setTextColor(ContextCompat.getColor(this, R.color.accent_green))
                binding.navMoreText.setTypeface(null, android.graphics.Typeface.BOLD)
            }
        }
        
        // Animate the active item
        animateNavItemActivation(item)
        currentNavItem = item
    }

    private fun animateNavItemActivation(view: View) {
        view.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(200)
            .setInterpolator(OvershootInterpolator())
            .withEndAction {
                view.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    private fun setupCardHoverEffects() {
        val actionCards = listOf(
            binding.cardAddRoom,
            binding.cardAllRooms,
            binding.cardBookRoom,
            binding.cardAllBookings,
            binding.cardModifyBooking
        )
        
        actionCards.forEach { card ->
            card.setOnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        view.animate()
                            .scaleX(0.98f)
                            .scaleY(0.98f)
                            .setDuration(100)
                            .start()
                    }
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL -> {
                        view.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start()
                    }
                }
                false
            }
        }
    }

    private fun setupCardAnimations() {
        // Animate cards on appear
        val cards = listOf(
            binding.statCardRooms,
            binding.statCardBookings,
            binding.cardAddRoom,
            binding.cardAllRooms,
            binding.cardBookRoom,
            binding.cardAllBookings,
            binding.cardModifyBooking
        )

        cards.forEachIndexed { index, card ->
            card.alpha = 0f
            card.translationY = 50f
            
            card.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay((index * 50).toLong())
                .setDuration(400)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }

    private fun animateCardClick(view: View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    private fun animateFabClick(view: View) {
        view.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .rotation(45f)
            .setDuration(150)
            .withEndAction {
                view.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .rotation(0f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }


    private fun loadStatistics() {
        lifecycleScope.launch {
            roomVm.rooms.collectLatest { rooms ->
                animateNumberChange(binding.tvTotalRooms, rooms.size)
            }
        }

        lifecycleScope.launch {
            bookingVm.bookings.collectLatest { bookings ->
                val activeBookings = bookings.count { it.status == "Active" }
                animateNumberChange(binding.tvTotalBookings, activeBookings)
            }
        }
    }

    private fun animateNumberChange(textView: android.widget.TextView, newValue: Int) {
        val oldValue = textView.text.toString().toIntOrNull() ?: 0
        if (oldValue == newValue) return

        val animator = ObjectAnimator.ofInt(oldValue, newValue)
        animator.duration = 500
        animator.addUpdateListener { animation ->
            textView.text = animation.animatedValue.toString()
        }
        animator.start()
    }
}
