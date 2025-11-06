package com.example.sai

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.sai.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply animations
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in)

        binding.logoContainer.startAnimation(fadeIn)
        binding.logo.startAnimation(scaleIn)
        
        // Animate background glows
        binding.glow1?.startAnimation(fadeIn)
        binding.glow2?.startAnimation(fadeIn)

        binding.logoContainer.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2500)
    }
}
