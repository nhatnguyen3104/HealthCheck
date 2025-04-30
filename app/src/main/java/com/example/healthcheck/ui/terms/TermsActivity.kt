package com.example.healthcheck.ui.terms

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.healthcheck.R
import com.example.healthcheck.ui.MainActivity

class TermsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_terms)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        AlertDialog.Builder(this)
            .setTitle("Điều khoản sử dụng")
            .setMessage("Ứng dụng cần quyền truy cập Internet và rung để hoạt động đúng. Bằng cách tiếp tục, bạn đồng ý với điều này.")
            .setPositiveButton("Đồng ý") { _, _ ->
                // Lưu vào SharedPreferences là đã đồng ý
                val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                prefs.edit().putBoolean("user_agreed", true).apply()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .setNegativeButton("Thoát") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()

    }
}