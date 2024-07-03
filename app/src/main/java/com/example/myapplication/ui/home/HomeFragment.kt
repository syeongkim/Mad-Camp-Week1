package com.example.myapplication.ui.home

import ContactsAdapter
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        val contacts = loadContactsFromSharedPreferences().toMutableList()

        if (contacts.isEmpty()) {
            contacts.addAll(loadContactsFromJson())
            saveContactsToSharedPreferences(contacts)
        }

        // contactsAdapter 초기화 시 클릭 이벤트 핸들러 추가
        contactsAdapter = ContactsAdapter(contacts, ::onContactClicked) { updatedContacts ->
            saveContactsToSharedPreferences(updatedContacts)
        }
        binding.contactsLayout.layoutManager = LinearLayoutManager(requireContext())
        binding.contactsLayout.adapter = contactsAdapter

        binding.recommendButton.setOnClickListener {
            contactsAdapter.sortByLastContactedDate()
        }

        binding.sortButton.setOnClickListener {
            contactsAdapter.sortByName()
        }

        binding.addContactButton.setOnClickListener {
            showAddContactDialog()
        }

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
                    val newContact = Contact(name, phone, currentDate, currentDate, null, null)
                    addContact(newContact)
                }
            }
            setNegativeButton("취소", null)
            show()
        }
    }

    // 연락처 수정 다이얼로그를 표시하는 함수
    private fun showEditContactDialog(contact: Contact) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_edit_contact, null)

        // 연락처 정보를 입력 필드에 설정하고 초기에는 비활성화 상태로 설정
        val nameEditText = dialogLayout.findViewById<EditText>(R.id.nameEditText).apply {
            setText(contact.name)
            setTextColor(Color.parseColor("#000000"))
            isEnabled = false
        }
        val phoneEditText = dialogLayout.findViewById<EditText>(R.id.phoneEditText).apply {
            setText(contact.phoneNumber)
            setTextColor(Color.parseColor("#000000"))
            isEnabled = false
        }
        val birthdayEditText = dialogLayout.findViewById<EditText>(R.id.birthdayEditText).apply {
            setText(contact.birthday ?: "")
            setTextColor(Color.parseColor("#000000"))
            isEnabled = false
        }
        val firstMetDateEditText = dialogLayout.findViewById<EditText>(R.id.firstMetDateEditText).apply {
            setText(contact.savedDate)
            setTextColor(Color.parseColor("#000000"))
            isEnabled = false
        }
        val lastContactedDateEditText = dialogLayout.findViewById<EditText>(R.id.lastContactedDateEditText).apply {
            setText(contact.lastContactedDate)
            setTextColor(Color.parseColor("#000000"))
            isEnabled = false
        }
        val saveButton = dialogLayout.findViewById<Button>(R.id.saveButton).apply {
            visibility = View.GONE
        }
        val editButton = dialogLayout.findViewById<Button>(R.id.editButton)

        builder.setTitle("편지지기")
        builder.setView(dialogLayout)

        val dialog = builder.create()
        dialog.show()

        // 수정 버튼 클릭 시 처리
        editButton.setOnClickListener {
            // 입력 필드를 활성화
            nameEditText.isEnabled = true
            nameEditText.setTextColor(Color.parseColor("#808080"))
            phoneEditText.isEnabled = true
            phoneEditText.setTextColor(Color.parseColor("#808080"))
            birthdayEditText.isEnabled = true
            birthdayEditText.setTextColor(Color.parseColor("#808080"))
            firstMetDateEditText.isEnabled = true
            firstMetDateEditText.setTextColor(Color.parseColor("#808080"))
            lastContactedDateEditText.isEnabled = true
            lastContactedDateEditText.setTextColor(Color.parseColor("#808080"))

            // 저장 버튼을 표시
            saveButton.visibility = View.VISIBLE
            editButton.visibility = View.GONE
        }

        saveButton.setOnClickListener {
            // 수정된 데이터를 저장
            val name = nameEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val birthday = birthdayEditText.text.toString()
            val firstMetDate = firstMetDateEditText.text.toString()
            val lastContactedDate = lastContactedDateEditText.text.toString()

            if (name.isNotEmpty() && phone.isNotEmpty()) {
                val updatedContact = contact.copy(
                    name = name,
                    phoneNumber = phone,
                    birthday = birthday,
                    savedDate = firstMetDate,
                    lastContactedDate = lastContactedDate
                )

                // 변경 사항을 SharedPreferences에 저장
                val updatedContacts = contactsAdapter.getContacts().map {
                    if (it.name == contact.name && it.phoneNumber == contact.phoneNumber) updatedContact else it
                }
                saveContactsToSharedPreferences(updatedContacts)
                contactsAdapter.updateContacts(updatedContacts)
            }

            // 다이얼로그를 닫음
            dialog.dismiss()
        }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
            dialog.dismiss()
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
        saveContactsToSharedPreferences(updatedContacts)
    }

    // 연락처 정보를 업데이트하는 함수
    private fun updateContact(contact: Contact) {
        val updatedContacts = contactsAdapter.getContacts().map {
            if (it.name == contact.name && it.phoneNumber == contact.phoneNumber) contact else it
        }
        contactsAdapter.updateContacts(updatedContacts)
        saveContactsToSharedPreferences(updatedContacts)
    }

    // 연락처 클릭 시 호출되는 함수, 수정 다이얼로그를 표시
    private fun onContactClicked(contact: Contact) {
        showEditContactDialog(contact)
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

    private fun saveContactsToSharedPreferences(contacts: List<Contact>) {
        val sharedPreferences = requireContext().getSharedPreferences("contacts_pref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(contacts)
        editor.putString("contacts_list", json)
        editor.apply()
    }

    private fun loadContactsFromSharedPreferences(): List<Contact> {
        val sharedPreferences = requireContext().getSharedPreferences("contacts_pref", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("contacts_list", null)
        val type = object : TypeToken<List<Contact>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}
