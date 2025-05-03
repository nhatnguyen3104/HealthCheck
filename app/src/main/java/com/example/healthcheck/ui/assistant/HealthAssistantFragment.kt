package com.example.healthcheck.ui.assistant

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.healthcheck.databinding.FragmentHealthAssistantBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class HealthAssistantFragment : Fragment() {

    private lateinit var binding: FragmentHealthAssistantBinding
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHealthAssistantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAskAI.setOnClickListener {
            val question = binding.etUserQuestion.text.toString().trim()
            if (question.isNotEmpty()) {
                askAI(question)
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập câu hỏi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun askAI(question: String) {
        val requestBody = """
            {
              "model": "openai/gpt-3.5-turbo",
              "messages": [
                {"role": "system", "content": "Bạn là một bác sĩ tư vấn sức khỏe."},
                {"role": "user", "content": "$question"}
              ]
            }
        """.trimIndent().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://openrouter.ai/api/v1/chat/completions")
            .addHeader("Authorization", "Bearer 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")
            .addHeader("Content-Type", "application/json")
            .addHeader("HTTP-Referer", "yourapp://")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HealthAI", "Request failed: ${e.message}")
                activity?.runOnUiThread {
                    binding.tvAIResponse.text = "Lỗi kết nối. Vui lòng thử lại sau."
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { body ->
                    try {
                        val json = JSONObject(body)
                        val reply = json.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")

                        activity?.runOnUiThread {
                            binding.tvAIResponse.text = reply
                        }
                    } catch (e: Exception) {
                        activity?.runOnUiThread {
                            binding.tvAIResponse.text = "Không thể xử lý phản hồi từ AI."
                        }
                    }
                }
            }
        })
    }
}
