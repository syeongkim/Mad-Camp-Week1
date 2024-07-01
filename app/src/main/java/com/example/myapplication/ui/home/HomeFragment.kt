package com.example.myapplication.ui.home

import ContactsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.R
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

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

        // 연락처 추가 버튼 클릭 이벤트 처리
        binding.addContactButton.setOnClickListener {
            showAddContactDialog()
        }

        // 검색 기능 이벤트 처리
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                contactsAdapter.filter.filter(newText)
                return false
            }
        })
    }

    private fun showAddContactDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_add_contact, null)
        val nameEditText = dialogLayout.findViewById<EditText>(R.id.nameEditText)
        val phoneEditText = dialogLayout.findViewById<EditText>(R.id.phoneEditText)

        with(builder) {
            setTitle("연락처 추가")
            setView(dialogLayout)
            setPositiveButton("추가") { _, _ ->
                val name = nameEditText.text.toString()
                val phone = phoneEditText.text.toString()
                val currentDate = getCurrentDate()

                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    val newContact = Contact(name, phone, currentDate, currentDate)
                    addContact(newContact)
                }
            }
            setNegativeButton("취소", null)
            show()
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun addContact(contact: Contact) {
        val updatedContacts = contactsAdapter.getContacts().toMutableList()
        updatedContacts.add(contact)
        contactsAdapter.updateContacts(updatedContacts)
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
