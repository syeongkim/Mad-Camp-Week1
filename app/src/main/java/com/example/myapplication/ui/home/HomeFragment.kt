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

    private lateinit var contactsAdapter: ContactsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 연락처 정보 가져오기
        val contacts = loadContactsFromJson()

        // RecyclerView 설정
        contactsAdapter = ContactsAdapter(contacts)
        binding.contactsLayout.layoutManager = LinearLayoutManager(requireContext())
        binding.contactsLayout.adapter = contactsAdapter

        // 추천순 보기 버튼 클릭 이벤트 처리
        binding.recommendButton.setOnClickListener {
            contactsAdapter.sortByLastContactedDate()
        }

        // 가나다순 보기 버튼 클릭 이벤트 처리
        binding.sortButton.setOnClickListener{
            contactsAdapter.sortByName()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadContactsFromJson(): List<Contact> {
        val jsonString = context?.assets?.open("contacts.json")?.bufferedReader().use { it?.readText() }
        val contactsData = Gson().fromJson(jsonString, ContactsData::class.java)
        return contactsData?.data ?: emptyList()
    }
}

data class Contact(
    val name: String,
    val phoneNumber: String,
    val savedDate: String,
    val lastContactedDate: String
)

data class ContactsData(
    val data: List<Contact>
)