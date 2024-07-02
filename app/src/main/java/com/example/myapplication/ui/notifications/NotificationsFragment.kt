package com.example.myapplication.ui.notifications

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentNotificationsBinding
import com.example.myapplication.ui.dashboard.DashboardFragment
import com.example.myapplication.ui.dashboard.ImageData
import com.example.myapplication.ui.home.Contact
import com.example.myapplication.ui.home.ContactsData
import com.google.gson.Gson
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Random


class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private var phoneNumber = "010-0000-0000"
    private var messageContent = "자니..?"
    private var imageResource = R.drawable.pic1
    private var person = "김서영"
    private var memoryDate = "2024-07-01"
    private var memoryComment = ""

    private var hasMatchingMemory = false
    private var hasMatchingBirthday = false
    private var hasOldestContact = false
    private var hasRandom = false

    @RequiresApi(Build.VERSION_CODES.O)
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
        val datePicker: DatePicker = binding.datePicker
        //datePicker.visibility = View.GONE
        val button1: Button = binding.button1
        val button2: Button = binding.button2
        val randomButton: Button = binding.randomButton


        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        var formattedDate = today.format(formatter)
        textDate.text = "오늘은 $formattedDate 입니다!"


        val dashboardFragment = DashboardFragment()
        val imageDataList = dashboardFragment.imageDataList
        val contacts = loadContactsFromJson()

        checkMatchingMemory(imageDataList, formattedDate, notificationsViewModel, textView1, textView2, memoryPic)
        checkMatchingBirthday(contacts, formattedDate, notificationsViewModel, textView1, textView2, memoryPic)
        checkOldestContact(contacts, textView1, textView2, memoryPic)

        textDate.setOnClickListener {
            datePicker.visibility = View.VISIBLE
            textDate.visibility = View.GONE
            textView1.isEnabled = false
            textView2.isEnabled = false
            memoryPic.visibility = View.GONE
            button1.isEnabled = false
            button2.isEnabled = false
            randomButton.isEnabled = false
        }
        datePicker.setOnDateChangedListener { _, year, month, dayOfMonth ->
            hasMatchingMemory = false
            hasMatchingBirthday = false
            hasOldestContact = false
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            val formattedSelectedDate = selectedDate.format(formatter)
            formattedDate = formattedSelectedDate
            datePicker.visibility = View.GONE
            textDate.visibility = View.VISIBLE
            textView1.isEnabled = true
            textView2.isEnabled = true
            memoryPic.visibility = View.VISIBLE
            button1.isEnabled = true
            button2.isEnabled = true
            randomButton.isEnabled = true
            textDate.text = "오늘은 $formattedDate 입니다!"

            checkMatchingMemory(imageDataList, formattedDate, notificationsViewModel, textView1, textView2, memoryPic)
            checkMatchingBirthday(contacts, formattedDate, notificationsViewModel, textView1, textView2, memoryPic)
            checkOldestContact(contacts, textView1, textView2, memoryPic)
        }
        randomButton.setOnClickListener {
            checkRandomContact(contacts, textView1, textView2, memoryPic)
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

            dlg?.setNegativeButton("닫기", null)
            dlg?.setView(dialogView)
            dlg?.show()
        }

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


    private fun checkMatchingMemory(
        imageDataList: List<ImageData>,
        formattedDate: String,
        notificationsViewModel: NotificationsViewModel,
        textView1: TextView,
        textView2: TextView,
        memoryPic: ImageView
    ) {
        imageDataList.forEach { imageData ->
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
                        imageResource = imageData.imageResId!!
                        memoryPic.setImageResource(imageResource)
                    }
                    val contacts = loadContactsFromJson()
                    phoneNumber = contacts.find { it.name == person }?.phoneNumber.toString()
                    hasMatchingMemory = true
                    Log.d("hasMatchingMemory", "$person $memoryDate $pastYearNum")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkMatchingBirthday(
        contacts: List<Contact>,
        formattedDate: String,
        notificationsViewModel: NotificationsViewModel,
        textView1: TextView,
        textView2: TextView,
        memoryPic: ImageView
    ) {
        if (!hasMatchingMemory) {
            contacts.forEach { contact ->
                val birthday = contact.birthday
                val birthdayparts = birthday.split("-", limit = 2)
                if (birthday != null && birthdayparts[1] == formattedDate.split("-", limit = 2)[1]) {
                    val name = contact.name
                    val age = calculateAge(birthday)
                    val birthdayMessage = "오늘은 ${name} 님의 ${age+1} 번째 생일입니다! \n 생일 축하 메세지를 전송해보세요!"

                    notificationsViewModel.text.observe(viewLifecycleOwner) {
                        textView1.text = birthdayMessage
                        textView2.text = "자니..? ${name}의 ${age+1} 번째 생일을 진심으로 축하해!"
                    }
                    val imageResource = context?.resources?.getIdentifier(contact.picture, "drawable", context?.packageName) ?: R.drawable.madcamp
                    memoryPic.setImageResource(imageResource)
                    hasMatchingBirthday = true
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkOldestContact(
        contacts: List<Contact>,
        textView1: TextView,
        textView2: TextView,
        memoryPic: ImageView
    ) {
        if (!hasMatchingMemory && !hasMatchingBirthday) {
            val oldestContact = findOldestContact(contacts)
            if (oldestContact != null) {
                val lastContactedDate = LocalDate.parse(oldestContact.lastContactedDate)
                val daysSinceLastContact = ChronoUnit.DAYS.between(lastContactedDate, LocalDate.now())

                person = oldestContact.name
                textView1.text = "${person}님과 연락한 지 벌써 ${daysSinceLastContact}일이 지났어요.\n ${oldestContact.name} 님께 지금 바로 연락해보세요!"
                messageContent = "자니..? 오랜만이다. \n잘 지내고 있어? 조만간 한 번 보자~"
                textView2.text = messageContent
                phoneNumber = oldestContact.phoneNumber
                imageResource = context?.resources?.getIdentifier(oldestContact.picture, "drawable", context?.packageName) ?: R.drawable.madcamp
                memoryPic.setImageResource(imageResource)
                hasOldestContact = true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkRandomContact(
        contacts: List<Contact>,
        textView1: TextView,
        textView2: TextView,
        memoryPic: ImageView
    ) {
        val random = Random()
        val randomIndex = random.nextInt(contacts.size)
        val randomContact = contacts[randomIndex]

        val lastContactedDate = LocalDate.parse(randomContact.lastContactedDate)
        val daysSinceLastContact = ChronoUnit.DAYS.between(lastContactedDate, LocalDate.now())

        person = randomContact.name
        textView1.text = "${person}님과 연락한 지 벌써 ${daysSinceLastContact}일이 지났어요.\n ${randomContact.name} 님께 지금 바로 연락해보세요!"
        messageContent = "자니..? 오랜만이다. \n잘 지내고 있어? 조만간 한 번 보자~"
        textView2.text = messageContent
        phoneNumber = randomContact.phoneNumber
        imageResource = context?.resources?.getIdentifier(randomContact.picture, "drawable", context?.packageName) ?: R.drawable.madcamp
        memoryPic.setImageResource(imageResource)
        hasRandom = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateAge(birthday: String): Int {
        val birthDate = LocalDate.parse(birthday)
        val today = LocalDate.now()
        return ChronoUnit.YEARS.between(birthDate, today).toInt()
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