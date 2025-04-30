package com.example.healthcheck.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.healthcheck.R
import com.example.healthcheck.ui.MainActivity
import com.example.healthcheck.ui.terms.TermsActivity
import com.example.healthcheck.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        val splashLogo = findViewById<ImageView>(R.id.splashLogo)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        splashLogo.startAnimation(fadeIn)


        // Tạo độ trễ để hiển thị Splash
        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
            val agreed = prefs.getBoolean("user_agreed", false)

            if (!agreed) {
                startActivity(Intent(this, TermsActivity::class.java))
            } else if (shouldRedirectToLogin() || auth.currentUser == null) {
                navigateToLogin()
            } else {
                navigateToMain()
            }

            finish()
        }, 2000)

    }

    private fun shouldRedirectToLogin(): Boolean {
        val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
        val lastLoginTime = sharedPreferences.getLong("last_login_time", 0)
        if (lastLoginTime == 0L) return true

        val currentTime = System.currentTimeMillis()
        val thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000
        return (currentTime - lastLoginTime > thirtyDaysInMillis)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}