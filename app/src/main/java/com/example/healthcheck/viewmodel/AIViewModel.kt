package com.example.healthcheck.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcheck.model.ChatMessage
import com.example.healthcheck.repository.WitRepository
import kotlinx.coroutines.launch
import java.text.Normalizer

class AIViewModel : ViewModel() {
    private val witRepository = WitRepository()
    val aiMessages = MutableLiveData<List<ChatMessage>>()
    private val messageList = mutableListOf<ChatMessage>()

    fun sendQueryToWit(question: String) {
        addMessage(ChatMessage(question, isFromUser = true))
        viewModelScope.launch {
            try {
                val danhSachChat = arrayOf(
                    "Chào bạn!",
                    "Hôm nay bạn thế nào?",
                    "Bạn có gì cần hỏi tôi sao.",
                    "Hello.",
                    "Rất vui được trò chuyện cùng bạn."
                )
                val response = witRepository.askWit(question)
                Log.d("WitResponse", "Full Response: ${response.entities}")

                val intentList = response.intents ?: emptyList()
                val topIntent = intentList.maxByOrNull { it.confidence }

                val chiSoList = response.entities?.get("chi_so:chi_so")
                val chiSoEntityRaw = chiSoList?.getOrNull(0)?.value

                Log.d("WitResponse", "Entity value: $chiSoEntityRaw")

                // Normalize entity value
                val chiSoNormalized = chiSoEntityRaw
                    ?.lowercase()
                    ?.removeVietnameseDiacritics()
                    ?.replace(" ", "")
                Log.d("WitResponse", "Normalized chi_so: $chiSoNormalized")
                Log.d("WitIntent", "Top intent name: ${topIntent?.name}, confidence: ${topIntent?.confidence}")

                val message = when {
                    topIntent == null || topIntent.confidence <= 0.6 -> {
                        "Tôi chưa rõ bạn muốn hỏi gì, thử lại nhé."
                    }

                    topIntent.name == "canh_bao_suc_khoe" && chiSoNormalized != null -> {
                        when (chiSoNormalized) {
                            "spo2" -> """
                            ▪️ Nếu nồng độ oxy trong máu (SpO₂) dưới 94%, có thể là dấu hiệu thiếu oxy.
                            ▪️ Nếu dưới 90%, bạn cần can thiệp y tế khẩn cấp.
                            ▪️ Hãy tham khảo bác sĩ nếu cảm thấy mệt mỏi, khó thở.
                        """.trimIndent()
                            "nhiptim" -> """
                            ▪️ Nếu nhịp tim của bạn trên 120 nhịp/phút hoặc dưới 50 nhịp/phút khi nghỉ ngơi, cần kiểm tra sức khỏe ngay.
                            ▪️ Nhịp tim bất thường có thể là dấu hiệu của các vấn đề tim mạch.
                        """.trimIndent()
                            "nhietdo" -> """
                            ▪️ Nhiệt độ cơ thể trên 38°C có thể là dấu hiệu của sốt.
                            ▪️ Nếu dưới 35°C, đó có thể là hạ thân nhiệt – bạn cần tìm cách làm ấm cơ thể ngay.
                            ▪️ Tuy nhiên nếu bạn đo ở đầu ngón tay thì nhiệt độ bình thường sẽ dao động từ 31°C đến 35°C
                        """.trimIndent()
                            else -> "Không có thông tin cụ thể về chỉ số này."
                        }
                    }
                    topIntent.name == "muc_binh_thuong" && chiSoNormalized != null -> {
                        when (chiSoNormalized) {
                            "spo2" -> """
                            ▪️ SpO₂ bình thường từ 95% đến 100%.
                            ▪️ Dưới 94% nên theo dõi và tham khảo ý kiến bác sĩ.
                        """.trimIndent()
                            "nhiptim" -> """
                            ▪️ Nhịp tim bình thường là 60–100 nhịp/phút khi nghỉ ngơi.
                            ▪️ Vận động viên có thể có nhịp tim thấp hơn.
                        """.trimIndent()
                            "nhietdo" -> """
                            ▪️ Nhiệt độ cơ thể bình thường từ 36.1°C đến 37.2°C.
                        """.trimIndent()
                            else -> "Không có thông tin cụ thể về chỉ số này."
                        }
                    }

                    topIntent.name == "tu_van_suc_khoe" && chiSoNormalized != null -> {
                        when (chiSoNormalized) {
                            "spo2" -> """
                            ▪️ Nồng độ oxy trong máu bình thường là từ 93% đến 100%.
                            ▪️ Nếu dưới 94%, bạn cần theo dõi và tham khảo ý kiến bác sĩ.
                        """.trimIndent()
                            "nhiptim" -> """
                            ▪️ Nhịp tim bình thường của người trưởng thành khi nghỉ ngơi là từ 60 đến 100 nhịp/phút.
                            ▪️ Vận động viên có thể có nhịp tim thấp hơn.
                            ▪️ Nếu nhịp tim quá cao hoặc thấp bất thường kèm theo triệu chứng (mệt, chóng mặt), nên đi khám.
                        """.trimIndent()
                            "nhietdo" -> """
                            ▪️ Nhiệt độ cơ thể bình thường dao động trong khoảng từ 36.1°C đến 37.2°C.
                            ▪️ Nếu trên 38°C là sốt, và dưới 35°C là hạ thân nhiệt.
                        """.trimIndent()
                            else -> "Không có thông tin cụ thể về chỉ số này."
                        }
                    }
                    topIntent.name == "chao"-> {
                        danhSachChat.random()
                    }

                    else -> "Tôi chưa có thông tin cụ thể về câu hỏi này, bạn có thể diễn đạt lại nhé."
                }

                Log.d("WitIntent", "Top intent: ${topIntent?.name} - Confidence: ${topIntent?.confidence}, chi_so: $chiSoNormalized")
                addMessage(ChatMessage(message, isFromUser = false))
            } catch (e: Exception) {
                addMessage(ChatMessage("Đã xảy ra lỗi, vui lòng thử lại sau.", isFromUser = false))
                Log.e("AIViewModel", "Error: ${e.message}")
            }
        }
    }
    fun String.removeVietnameseDiacritics(): String {
        val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
        return normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            .replace("đ", "d")
            .replace("Đ", "D")
    }





    private fun addMessage(message: ChatMessage) {
        messageList.add(message)
        aiMessages.postValue(messageList.toList())
    }
}
