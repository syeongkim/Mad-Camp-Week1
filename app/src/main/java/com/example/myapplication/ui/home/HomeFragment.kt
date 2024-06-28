package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.gson.Gson

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var contacts: List<Contact>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        loadContactsFromJson()

        // 이름과 연락처를 TextView에 표시
        binding.nameTextView.text = contacts[0].name
        binding.phoneTextView.text = contacts[0].phoneNumber

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadContactsFromJson() {
        val jsonString = context?.assets?.open("contacts.json")?.bufferedReader().use { it?.readText() }
        contacts = Gson().fromJson(jsonString, Array<Contact>::class.java).toList()
    }
}

data class Contact(
    val name: String,
    val phoneNumber: String
)