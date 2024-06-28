package com.example.myapplication.ui.home

import ContactsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.gson.Gson

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 샘플 연락처 데이터
        val contacts = loadContactsFromJson()
//        val contacts = listOf(
//            Contact("John Doe", "123-456-7890"),
//            Contact("Jane Smith", "987-654-3210"),
//            Contact("Bob Johnson", "555-555-5555")
//        )

        // RecyclerView 설정
        binding.contactsLayout.layoutManager = LinearLayoutManager(requireContext())
        binding.contactsLayout.adapter = ContactsAdapter(contacts)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadContactsFromJson(): List<Contact> {
        val jsonString = context?.assets?.open("contacts.json")?.bufferedReader().use { it?.readText() }
        val contactsData = Gson().fromJson(jsonString, ContactsData::class.java)
        return contactsData.data

    }
}

data class Contact(
    val name: String,
    val phoneNumber: String
)

data class ContactsData(
    val data: List<Contact>
)