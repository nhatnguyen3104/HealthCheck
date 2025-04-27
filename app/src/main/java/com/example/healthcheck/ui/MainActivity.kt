package com.example.healthcheck.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.healthcheck.R
import com.example.healthcheck.databinding.ActivityMainBinding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.healthcheck.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLoginExpiration()

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setupActionBarWithNavController(navController)
        NavigationUI.setupWithNavController(binding.bottomNav, navController)

        binding.bottomNav.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }
    private fun checkLoginExpiration() {
        val sharedPreferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val lastLoginTime = sharedPreferences.getLong("last_login_time", 0)

        if (lastLoginTime == 0L) {
            // Nếu không có thông tin đăng nhập, yêu cầu đăng nhập
            navigateToLogin()
            return
        }

        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - lastLoginTime
        val thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000 // 30 ngày tính bằng mili giây

        if (timeDifference > thirtyDaysInMillis) {
            // Nếu thời gian đăng nhập đã quá 30 ngày, yêu cầu đăng nhập lại
            navigateToLogin()
        } else {
            // Nếu chưa quá 30 ngày, người dùng vẫn còn đăng nhập
        }
    }
    // Điều hướng tới màn hình đăng nhập
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Kết thúc MainActivity nếu không cần quay lại
    }

}
