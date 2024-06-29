package com.example.myapplication.ui.home

import ContactsAdapter
import android.content.ContentResolver
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 연락처 정보 가져오기
        val contacts = getContacts()

        // RecyclerView 설정
        binding.contactsLayout.layoutManager = LinearLayoutManager(requireContext())
        binding.contactsLayout.adapter = ContactsAdapter(contacts)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val contentResolver: ContentResolver = requireContext().contentResolver

        // 연락처 관리
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use { c ->
            while (c.moveToNext()) {
                val name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
                // 연락처에 대한 추가적인 정보 가져오기 (전화번호, 등록일 등)
                val phoneCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(c.getString(c.getColumnIndex(ContactsContract.Contacts._ID))),
                    null
                )

                phoneCursor?.use { pc ->
                    if (pc.moveToNext()) {
                        val phoneNumber = pc.getString(pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                        // 연락처 정보를 Contact 객체로 추가
                        val contact = Contact(
                            name = name,
                            phoneNumber = phoneNumber,
                            savedDate = "", // 여기서 저장 날짜 설정
                            lastContactedDate = "" // 여기서 최근 연락한 날짜 설정
                        )
                        contacts.add(contact)
                    }
                }
            }
        }
        return contacts
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