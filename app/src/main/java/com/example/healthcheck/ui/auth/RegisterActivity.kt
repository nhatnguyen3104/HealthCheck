package com.example.healthcheck.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthcheck.databinding.ActivityRegisterBinding
import com.example.healthcheck.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel = AuthViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {

            binding.tiEmail.error = null
            binding.tilPassword.error = null
            binding.ticonfirmlPassword.error = null

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            var isValid = true

            if (email.isEmpty()) {
                binding.tiEmail.error = "Vui lòng nhập email"
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tiEmail.error = "Định dạng email không hợp lệ"
                isValid = false
            }

            if (password.isEmpty()) {
                binding.tilPassword.error = "Vui lòng nhập mật khẩu"
                isValid = false
            } else if (password.length < 6) { // Ví dụ: kiểm tra độ dài tối thiểu
                binding.tilPassword.error = "Mật khẩu phải có ít nhất 6 ký tự"
                isValid = false
            }

            if (confirmPassword.isEmpty()) {
                binding.ticonfirmlPassword.error = "Vui lòng xác nhận mật khẩu"
                isValid = false
            } else if (password != confirmPassword) {
                binding.ticonfirmlPassword.error = "Mật khẩu xác nhận không khớp"
                // Có thể đặt lỗi cho cả 2 ô nếu muốn
                // binding.tilPassword.error = "Mật khẩu xác nhận không khớp"
                isValid = false
            }

            if (isValid) {
                authViewModel.register(email, password) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, error ?: "Đăng ký thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
        binding.tvAlreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
