package com.example.sai

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.example.sai.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val loginCard = findViewById<androidx.cardview.widget.CardView>(R.id.loginCard)
        val appTitle = findViewById<android.widget.TextView>(R.id.appTitle)

        // Apply animations
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        
        loginCard?.startAnimation(slideUp)
        appTitle?.startAnimation(fadeIn)

        btnLogin.setOnClickListener {
            val user = etUsername.text?.toString()?.trim() ?: ""
            val pass = etPassword.text?.toString()?.trim() ?: ""
            if (user == "admin" && pass == "admin123") {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}