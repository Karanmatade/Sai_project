package com.example.sai

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sai.data.entity.RoomEntity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import java.util.Locale

class RoomsAdapter(
    private val onEdit: (RoomEntity) -> Unit,
    private val onDelete: (RoomEntity) -> Unit
) : ListAdapter<RoomEntity, RoomsAdapter.VH>(DIFF) {

    object DIFF : DiffUtil.ItemCallback<RoomEntity>() {
        override fun areItemsTheSame(oldItem: RoomEntity, newItem: RoomEntity) = oldItem.roomNumber == newItem.roomNumber
        override fun areContentsTheSame(oldItem: RoomEntity, newItem: RoomEntity) = oldItem == newItem
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val cardRoom: MaterialCardView = v.findViewById(R.id.cardRoom)
        val borderGlow: View = v.findViewById(R.id.borderGlow)
        val tvRoomNumber: TextView = v.findViewById(R.id.tvRoomNumber)
        val tvType: TextView = v.findViewById(R.id.tvType)
        val tvPrice: TextView = v.findViewById(R.id.tvPrice)
        val chipStatus: Chip = v.findViewById(R.id.chipStatus)
        val btnEdit: MaterialCardView = v.findViewById(R.id.btnEdit)
        val btnDelete: MaterialCardView = v.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_room, parent, false)
        val holder = VH(v)
        
        // Entry animation
        animateEntry(holder.itemView)
        
        return holder
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        
        // Set room data
        holder.tvRoomNumber.text = "#${item.roomNumber}"
        holder.tvType.text = item.type
        holder.tvPrice.text = "₹${String.format(Locale.getDefault(), "%,d", item.price)}"
        
        // Apply gradient text colors for light theme
        applyGradientText(holder.tvRoomNumber, 
            ContextCompat.getColor(holder.itemView.context, R.color.accent_green),
            ContextCompat.getColor(holder.itemView.context, R.color.accent_green_dark))
        
        applyGradientText(holder.tvPrice,
            ContextCompat.getColor(holder.itemView.context, R.color.accent_purple),
            ContextCompat.getColor(holder.itemView.context, R.color.neon_pink))
        
        // Update status chip and border with light theme effects
        if (item.isBooked) {
            holder.chipStatus.text = "Booked"
            holder.chipStatus.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_badge_light_booked)
            holder.chipStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.neon_pink))
            holder.chipStatus.setChipStrokeColorResource(R.color.neon_pink)
            holder.chipStatus.setChipStrokeWidth(1.5f)
            holder.borderGlow.setBackgroundResource(R.drawable.bg_border_light_pink)
            
            // Animate status change
            animateStatusChange(holder.chipStatus, false)
        } else {
            holder.chipStatus.text = "Available"
            holder.chipStatus.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_badge_light_available)
            holder.chipStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.success_green))
            holder.chipStatus.setChipStrokeColorResource(R.color.success_green)
            holder.chipStatus.setChipStrokeWidth(1.5f)
            holder.borderGlow.setBackgroundResource(R.drawable.bg_border_light_cyan)
            
            // Animate status change
            animateStatusChange(holder.chipStatus, true)
        }
        
        // Button click listeners with animations
        holder.btnEdit.setOnClickListener { view ->
            animateButtonClick(view) {
                onEdit(item)
            }
        }
        
        holder.btnDelete.setOnClickListener { view ->
            animateButtonClick(view) {
                onDelete(item)
            }
        }
        
        // Card hover effect (optional - can be triggered on focus)
        holder.cardRoom.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                animateCardHover(holder.cardRoom, true)
            } else {
                animateCardHover(holder.cardRoom, false)
            }
        }
    }

    private fun applyGradientText(textView: TextView, startColor: Int, endColor: Int) {
        val paint = textView.paint
        val width = paint.measureText(textView.text.toString())
        val textShader = LinearGradient(
            0f, 0f, width, textView.textSize,
            startColor, endColor,
            Shader.TileMode.CLAMP
        )
        textView.paint.shader = textShader
        textView.invalidate()
    }

    private fun animateEntry(view: View) {
        view.alpha = 0f
        view.scaleX = 0.9f
        view.scaleY = 0.9f
        view.translationY = 40f
        view.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .translationY(0f)
            .setDuration(450)
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()
    }

    private fun animateStatusChange(chip: Chip, isAvailable: Boolean) {
        chip.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(200)
            .setInterpolator(OvershootInterpolator())
            .withEndAction {
                chip.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    private fun animateButtonClick(view: View, action: () -> Unit) {
        view.animate()
            .scaleX(0.85f)
            .scaleY(0.85f)
            .setDuration(120)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                view.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(100)
                    .withEndAction {
                        view.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(80)
                            .setInterpolator(OvershootInterpolator())
                            .withEndAction {
                                action()
                            }
                            .start()
                    }
                    .start()
            }
            .start()
    }

    private fun animateCardHover(card: MaterialCardView, isHovered: Boolean) {
        if (isHovered) {
            card.animate()
                .scaleX(1.02f)
                .scaleY(1.02f)
                .setDuration(200)
                .start()
            card.cardElevation = 24f
        } else {
            card.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(200)
                .start()
            card.cardElevation = 20f
        }
    }
}
