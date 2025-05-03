package com.example.healthcheck.repository

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.healthcheck.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()


    fun register(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                onResult(true, "Vui lòng kiểm tra email để xác nhận")
                            } else {
                                onResult(false, "Đã xảy ra lỗi, không thể gửi email xác thực")
                                Log.e(
                                    "AuthRepository",
                                    "Email verification failed: ${verifyTask.exception?.message}"
                                )
                            }
                        }
                } else {
                    onResult(false, "Đã xảy ra lỗi, vui lòng thử lại sau")
                    Log.e("AuthRepository", "Registration failed: ${task.exception?.message}")
                }
            }
    }


    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, "Email hoặc mật khẩu không chính xác!")
                    Log.e("AuthRepository", "Login failed: ${task.exception?.message}")
                }
            }
    }

    fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    fun resetPassword(email: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }
    fun waitForEmailVerification(context: Context, user: FirebaseUser, onVerified: () -> Unit) {
        val handler = Handler(Looper.getMainLooper())
        val checkInterval = 3000L

        val checkRunnable = object : Runnable {
            override fun run() {
                user.reload().addOnSuccessListener {
                    if (user.isEmailVerified) {
                        Toast.makeText(context, "Xác minh thành công!", Toast.LENGTH_SHORT).show()
                        onVerified()
                    } else {
                        handler.postDelayed(this, checkInterval)
                    }
                }
            }
        }

        handler.post(checkRunnable)
    }
    fun logoutAndNavigateToLogin(context: Context) {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }


}
