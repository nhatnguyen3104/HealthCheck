package com.example.healthcheck.ui.intro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.healthcheck.ui.MainActivity

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hiển thị dialog điều khoản
        AlertDialog.Builder(this)
            .setTitle("Điều khoản sử dụng")
            .setMessage("""
                Chào mừng! [Kiểm tra sức khỏe]. Theo dõi sức khỏe, hiểu cơ thể. Bắt đầu ngay!
                
                Ứng dụng cần sử dụng Internet để kết nối AI và chức năng rung để thông báo sức khỏe.
                
                Bằng cách tiếp tục, bạn đồng ý với điều khoản này.
            """.trimIndent())
            .setCancelable(false)
            .setPositiveButton("Tôi đồng ý") { _, _ ->
                // Lưu vào SharedPreferences
                getSharedPreferences("app_prefs", MODE_PRIVATE)
                    .edit()
                    .putBoolean("user_agreed", true)
                    .apply()

                // Chuyển vào MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .setNegativeButton("Thoát") { _, _ ->
                finish()
            }
            .show()
    }
}
