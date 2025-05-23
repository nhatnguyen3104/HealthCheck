package com.example.healthcheck.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthcheck.databinding.ActivityLoginBinding
import com.example.healthcheck.ui.MainActivity
import com.example.healthcheck.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel = AuthViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {

            binding.tilEmail.error = null
            binding.tilPassword.error = null

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            var isValid = true

            if (email.isEmpty()) {
                binding.tilEmail.error = "Vui lòng nhập email"
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // Kiểm tra định dạng email cơ bản phía client
                binding.tilEmail.error = "Định dạng email không hợp lệ"
                isValid = false
            }

            if (password.isEmpty()) {
                binding.tilPassword.error = "Vui lòng nhập mật khẩu"
                isValid = false
            }


            if (isValid) {
                authViewModel.login(email, password) { success, error ->
                    if (success) {
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null && user.isEmailVerified) {
                            saveLoginTime()
                            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                            navigateToMain()
                        } else {
                            FirebaseAuth.getInstance().signOut()
                            Toast.makeText(this, "Vui lòng xác minh email trước khi đăng nhập", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, error ?: "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            }



        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.tvForgotPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email trước khi khôi phục mật khẩu", Toast.LENGTH_SHORT).show()
            } else {
                authViewModel.resetPassword(email) { success, error ->
                    if (success) {
                        showResetEmailSentDialog(email)
                        Log.d("LoginActivity", "Email khôi phục mật khẩu đã được gửi đến: $email")
                    } else {
                        Toast.makeText(this, error ?: "Gửi email thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun saveLoginTime() {
        val sharedPreferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val currentTime = System.currentTimeMillis() // thời gian hiện tại tính bằng mili giây
        editor.putLong("last_login_time", currentTime)
        editor.apply()
    }

    // Điều hướng tới màn hình chính
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Kết thúc LoginActivity
    }
    private fun showResetEmailSentDialog(email: String) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Đã gửi email khôi phục")
        builder.setMessage("Một email khôi phục mật khẩu đã được gửi đến:\n$email\n\nVui lòng kiểm tra hộp thư đến hoặc thư rác.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.show()
    }


}
