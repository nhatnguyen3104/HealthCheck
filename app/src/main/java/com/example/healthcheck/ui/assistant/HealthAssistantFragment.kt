package com.example.healthcheck.ui.assistant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcheck.databinding.FragmentHealthAssistantBinding
import com.example.healthcheck.model.ChatMessage
import com.example.healthcheck.viewmodel.AIViewModel

class HealthAssistantFragment : Fragment() {

    private var _binding: FragmentHealthAssistantBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AIViewModel
    private lateinit var adapter: ChatAdapter
    private val chatList = mutableListOf<ChatMessage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthAssistantBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[AIViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(requireContext())

        adapter = ChatAdapter(chatList)
        binding.recyclerViewChat.adapter = adapter

        binding.btnAskAI.setOnClickListener {
            val question = binding.etUserQuestion.text.toString().trim()
            if (question.isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
                viewModel.sendQueryToWit(question)
                binding.etUserQuestion.text.clear()
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập câu hỏi", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.aiMessages.observe(viewLifecycleOwner) { messages ->
            binding.progressBar.visibility = View.GONE
            adapter.updateMessages(messages)
            binding.recyclerViewChat.scrollToPosition(messages.size - 1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
