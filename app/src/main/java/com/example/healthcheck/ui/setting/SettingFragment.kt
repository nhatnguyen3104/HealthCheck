package com.example.healthcheck.ui.setting

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.healthcheck.databinding.FragmentSettingBinding
import com.example.healthcheck.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = auth.currentUser
        val sharedPrefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)
        binding.switchDarkMode.isChecked = isDarkMode

        // Đặt theme khi fragment được tạo
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        // Xử lý sự kiện chuyển chế độ
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            requireActivity().recreate() // Áp dụng lại theme ngay
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        user?.let {
            binding.tvUserEmail.text = it.email ?: "Không có email"
        }

        binding.btnPrivacy.setOnClickListener {
            val policyText = """
                Chính sách và Bảo mật

                1. Ứng dụng cam kết bảo mật thông tin cá nhân của người dùng.
                2. Dữ liệu thu thập chỉ phục vụ mục đích cải thiện dịch vụ.
                3. Chúng tôi không chia sẻ dữ liệu cho bên thứ ba khi chưa được sự đồng ý.
                4. Người dùng có quyền yêu cầu xóa dữ liệu bất kỳ lúc nào.

                Vui lòng liên hệ mail nlnhat3104@gmail.com nếu bạn có bất kỳ câu hỏi nào.
            """.trimIndent()

            AlertDialog.Builder(requireContext())
                .setTitle("Chính sách và Bảo mật")
                .setMessage(policyText)
                .setPositiveButton("Tôi đã hiểu") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        binding.btnHelp.setOnClickListener {
            val helpText = """
        Trợ giúp sử dụng ứng dụng:

        - Vào mục "Dữ liệu sức khỏe" để xem các chỉ số đo.
        - Chuyển đổi giữa Chế độ sáng và tối ở phần "Giao diện".
        - Nếu gặp lỗi đăng nhập, vui lòng đăng xuất và thử lại.
        - Để bảo vệ dữ liệu, hãy đăng xuất sau khi sử dụng xong.
        
        Mọi thắc mắc khác, xin liên hệ mail nlnhat3104@gmail.com
    """.trimIndent()

            AlertDialog.Builder(requireContext())
                .setTitle("Trợ giúp")
                .setMessage(helpText)
                .setPositiveButton("Đóng") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        binding.btnFeedback.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("nlnhat3104@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Phản hồi từ người dùng ứng dụng HealthCheck")
                putExtra(Intent.EXTRA_TEXT, "Xin chào,\n\nTôi muốn góp ý như sau:\n")
            }
            try {
                startActivity(Intent.createChooser(intent, "Chọn ứng dụng email"))
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(requireContext(), "Không tìm thấy ứng dụng email.", Toast.LENGTH_SHORT).show()
                Log.e("FeedbackFragment", "Không tìm thấy ứng dụng email.");
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
