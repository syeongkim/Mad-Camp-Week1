package com.example.myapplication.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.R
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
        bindContacts()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadContactsFromJson() {
        val jsonString = context?.assets?.open("contacts.json")?.bufferedReader().use { it?.readText() }
        Log.d("HomeFragment", "jsonString: $jsonString")

        val contactsData = Gson().fromJson(jsonString, ContactsData::class.java)
        contacts = contactsData.data

        Log.d("HomeFragment", "contactsString: $contacts")
    }

    private fun bindContacts() {
        binding.contactsLayout.removeAllViews() // 기존 뷰들을 제거

        contacts.forEach { contact ->
            // 새로운 뷰를 inflate하여 추가
            val contactView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_contact, binding.contactsLayout, false)

            contactView.findViewById<TextView>(R.id.nameTextView).text = contact.name
            contactView.findViewById<TextView>(R.id.phoneTextView).text = contact.phoneNumber

            binding.contactsLayout.addView(contactView)
        }
    }
}

data class Contact(
    val name: String,
    val phoneNumber: String
)

data class ContactsData(
    val data: List<Contact>
)