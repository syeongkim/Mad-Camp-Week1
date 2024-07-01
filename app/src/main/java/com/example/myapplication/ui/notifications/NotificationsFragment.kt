package com.example.myapplication.ui.notifications

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
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
        val memoryPic: ImageView = binding.memoryPic
        val textDate: TextView = binding.textDate

        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = today.format(formatter)
        //val formattedDate = "2024-09-09"

        val dashboardFragment = DashboardFragment()
        val imageDataList = dashboardFragment.imageDataList

        textDate.text = "오늘 날짜는 ${formattedDate}"

        var foundMatchingImage = false
        var phoneNumber = "010-0000-0000"
        var messageContent = "자니..?"
        var imageResource = R.drawable.pic1
        var person = "김서영"
        var memoryDate = "2024-07-01"
        var memoryComment = ""
        imageDataList?.forEach { imageData ->
            val date = imageData.date
            if (date != null) {
                val parts = date.split("-", limit = 2)
                if (parts.size > 1 && parts[1] == formattedDate.split("-", limit = 2)[1]) {
                    person = imageData.person.toString()
                    memoryDate = date
                    memoryComment = imageData.memory.toString()
                    val pastYearNum = formattedDate.split("-", limit = 2)[0].toInt() - parts[0].toInt()
                    notificationsViewModel.text.observe(viewLifecycleOwner) {
                        textView1.text = "${pastYearNum}년 전 오늘 생성된 추억이 있어요.\n${person}님께 지금 바로 연락해보세요!"
                        messageContent = "자니..? 우리 그때 즐거웠는데.. 잘 지내?"
                        textView2.text = messageContent
                        imageResource = imageData.imageResId
                        memoryPic.setImageResource(imageData.imageResId)
                    }
                    val contacts = loadContactsFromJson()
                    phoneNumber = contacts.find { it.name == person }?.phoneNumber.toString()
                    foundMatchingImage = true

                }
            }
        }

        if (!foundMatchingImage) {
            val oldestContact = findOldestContact(loadContactsFromJson())
            if (oldestContact != null) {
                val lastContactedDate = LocalDate.parse(oldestContact.lastContactedDate)
                val daysSinceLastContact = ChronoUnit.DAYS.between(lastContactedDate, LocalDate.now())

                person = oldestContact.name
                textView1.text = "${person}님과 연락한 지 벌써 ${daysSinceLastContact}일이 지났어요.\n ${oldestContact.name} 님께 지금 바로 연락해보세요!"
                messageContent = "자니..? 오랜만이다. \n잘 지내고 있어? 조만간 한 번 보자~"
                textView2.text = messageContent
                phoneNumber = oldestContact.phoneNumber
            } else { // TODO: 에러 방지를 위한 랜덤 처리
                textView1.text = "No matching data found"
                textView2.text = "" // Adjust as needed
            }
        }

        memoryPic.setOnClickListener {
            val dialogView = View.inflate(context, R.layout.dialog, null)
            val dlg = context?.let { it1 -> AlertDialog.Builder(it1) }

            // 다이얼로그의 뷰 요소들 참조
            val ivPic: ImageView = dialogView.findViewById(R.id.ivPic)
            val personEditText: EditText = dialogView.findViewById(R.id.personEditText)
            val dateEditText: EditText = dialogView.findViewById(R.id.dateEditText)
            val memoryEditText: EditText = dialogView.findViewById(R.id.memoryEditText)
            val saveButton: Button = dialogView.findViewById(R.id.saveButton)

            ivPic.setImageResource(imageResource)

            // 입력된 데이터가 있는 경우
            personEditText.setText(person)
            dateEditText.setText(memoryDate)
            memoryEditText.setText(memoryComment)

            // 입력 필드를 비활성화하고 저장 버튼을 숨김
            personEditText.isEnabled = false
            dateEditText.isEnabled = false
            memoryEditText.isEnabled = false
            saveButton.visibility = View.GONE

            if (dlg != null) {
                dlg.setNegativeButton("닫기", null)
                dlg.setView(dialogView)
                dlg.show()
            }
        }
        // Add button click listeners if needed
        binding.button1.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(intent)
        }
        binding.button2.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phoneNumber")
                putExtra("sms_body", messageContent) // 메시지 내용
            }
            startActivity(intent)
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
