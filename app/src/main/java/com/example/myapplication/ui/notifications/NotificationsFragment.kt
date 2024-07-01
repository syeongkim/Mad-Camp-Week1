package com.example.myapplication.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentNotificationsBinding
import com.example.myapplication.ui.dashboard.DashboardFragment
import com.example.myapplication.ui.home.Contact
import com.example.myapplication.ui.home.ContactsData
import com.google.gson.Gson
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView1: TextView = binding.textMemory
        val textView2: TextView = binding.textMessage

        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        //val formattedDate = today.format(formatter)
        val formattedDate = "2024-09-09"

        val dashboardFragment = DashboardFragment()
        val imageDataList = dashboardFragment.imageDataList

        var foundMatchingImage = false
        imageDataList?.forEach { imageData ->
            val date = imageData.date
            if (date != null) {
                val parts = date.split("-", limit = 2)
                if (parts.size > 1 && parts[1] == formattedDate.split("-", limit = 2)[1]) {
                    val person = imageData.person
                    val pastYearNum = formattedDate.split("-", limit = 2)[0].toInt() - parts[0].toInt()
                    notificationsViewModel.text.observe(viewLifecycleOwner) {
                        textView1.text = "${pastYearNum}년 전 오늘 생성된 추억이 있어요.\n${person}님께 지금 바로 연락해보세요!"
                        textView2.text = "자니..? 우리 그때 즐거웠는데.. 잘 지내?" // Adjust as needed, e.g., different text for textView2
                    }
                    foundMatchingImage = true

                }
            }
        }

        if (!foundMatchingImage) {
            val oldestContact = findOldestContact(loadContactsFromJson())
            if (oldestContact != null) {
                val lastContactedDate = LocalDate.parse(oldestContact.lastContactedDate)
                val daysSinceLastContact = ChronoUnit.DAYS.between(lastContactedDate, LocalDate.now())

                textView1.text = "${oldestContact.name}님과 연락한 지 벌써 ${daysSinceLastContact}일이 지났어요.\n ${oldestContact.name} 님께 지금 바로 연락해보세요!"
                textView2.text = "자니..? 오랜만이다. \n잘 지내고 있어? 조만간 한 번 보자~"
            } else { // 에러 방지를 위한 랜덤 처리
                textView1.text = "No matching data found"
                textView2.text = "" // Adjust as needed
            }
        }

        // Add button click listeners if needed
        binding.button1.setOnClickListener {
            // Handle button 1 click
        }
        binding.button2.setOnClickListener {
            // Handle button 2 click
        }

        return root
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

    private fun findOldestContact(contacts: List<Contact>): Contact? {
        return contacts.minByOrNull { LocalDate.parse(it.lastContactedDate) }
    }
}
