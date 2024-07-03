package com.example.myapplication.ui.notifications

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
        val editMessage: TextView = binding.editMessage



        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        var formattedDate = today.format(formatter).toString()
        textDate.text = "오늘은 $formattedDate 입니다!"


        val dashboardFragment = DashboardFragment()
        val imageDataList = dashboardFragment.imageDataList
        val contacts = loadContactsFromJson()

        checkMatchingMemory(imageDataList, formattedDate, notificationsViewModel, textView1, textView2, memoryPic)
        checkMatchingBirthday(contacts, formattedDate, notificationsViewModel, textView1, textView2, memoryPic)
        checkOldestContact(contacts, formattedDate, notificationsViewModel, textView1, textView2, memoryPic)

        textDate.setOnClickListener {
            datePicker.visibility = View.VISIBLE
            textDate.visibility = View.GONE
            textView1.visibility = View.GONE
            textView2.visibility = View.GONE
            memoryPic.visibility = View.GONE
            button1.visibility = View.GONE
            button2.visibility = View.GONE
            randomButton.visibility = View.GONE
            editMessage.visibility = View.GONE
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
            textView1.visibility = View.VISIBLE
            textView2.visibility = View.VISIBLE
            memoryPic.visibility = View.VISIBLE
            button1.visibility = View.VISIBLE
            button2.visibility = View.VISIBLE
            randomButton.visibility = View.VISIBLE
            editMessage.visibility = View.GONE
            textDate.text = "오늘은 $formattedDate 입니다!"

            checkMatchingMemory(imageDataList, formattedDate, notificationsViewModel, textView1, textView2, memoryPic)
            checkMatchingBirthday(contacts, formattedDate, notificationsViewModel, textView1, textView2, memoryPic)
            checkOldestContact(contacts, formattedDate, notificationsViewModel, textView1, textView2, memoryPic)
        }
        randomButton.setOnClickListener {
            checkRandomContact(contacts, notificationsViewModel, textView1, textView2, memoryPic)
            textView2.isEnabled = true
            editMessage.visibility = View.GONE
            hasRandom = true
        }

        textView2.setOnClickListener {
            editMessage.visibility = View.VISIBLE
            textView2.visibility = View.GONE
            editMessage.text = textView2.text
            editMessage.requestFocus()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editMessage, InputMethodManager.SHOW_IMPLICIT)
        }

        editMessage.setOnFocusChangeListener {_, hasFocus ->
            if (!hasFocus) {
                textView2.text = editMessage.text
                textView2.visibility = View.VISIBLE
                textView2.isEnabled = true
                editMessage.visibility = View.GONE
            }
        }

        root.setOnClickListener {
            editMessage.clearFocus()
        }

        root.isFocusableInTouchMode = true
        root.requestFocus()

        binding.button1.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            editMessage.visibility = View.GONE
            textView2.isEnabled = true
            startActivity(intent)
        }

        binding.button2.setOnClickListener {
            textView2.text = editMessage.text
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phoneNumber")
                putExtra("sms_body", textView2.text.toString()) // 메시지 내용
            }
            editMessage.visibility = View.GONE
            textView2.isEnabled = true
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
        var matchingMemories: MutableList<ImageData> = mutableListOf()
        imageDataList.forEach { imageData ->
            val date = imageData.date
            if (date != null) {
                val parts = date.split("-", limit = 2)
                if (parts.size > 1 && parts[1] == formattedDate.split("-", limit = 2)[1]) {
                    matchingMemories.add(imageData)
                }
            }
        }
        if (matchingMemories.size > 0) {
            val matchingMemoryData = matchingMemories.random()
            person = matchingMemoryData.person.toString()
            memoryDate = matchingMemoryData.date.toString()
            memoryComment = matchingMemoryData.memory.toString()
            val pastYearNum = formattedDate.split("-", limit = 2)[0].toInt() - memoryDate.split("-", limit = 2)[0].toInt()
            val messageContents: List<String> = listOf(
                "자니...? 며칠 전에 옛날 사진첩을 보다가 우리가 함께했던 순간들이 떠올랐어. 그때 참 행복했었는데, 지금도 가끔 그리워져. 잘 지내고 있는지 궁금해서 연락해봤어.",
                "자니...? 우연히 예전 사진들을 보다가 우리 추억이 생각나서 연락해봤어. 그때 참 많은 추억을 쌓았는데, 지금도 그 시절이 가끔 그리워. 잘 지내고 있지?",
                "자니...? 얼마 전 사진첩을 정리하다가 우리가 함께했던 소중한 순간들이 떠올랐어. 그때의 기억들이 아직도 내 마음에 남아 있어. 잘 지내고 있는지 궁금해서 문득 연락해봤어.",
                "자니...? 오랜만에 사진첩을 보다가 우리 함께했던 그때가 떠오르더라. 네가 잘 지내고 있는지 궁금해져서 이렇게 연락해봐. 요즘 어떻게 지내?",
                "자니...? 옛날 사진을 보다가 우리가 함께 웃고 떠들던 그 순간들이 생각났어. 그 시절이 가끔 그리울 때가 많아. 시간 괜찮으면 오랜만에 만나서 이야기 좀 나눌래?"
            )
            messageContent = messageContents.random()
            notificationsViewModel.text.observe(viewLifecycleOwner) {
                textView1.text = "${pastYearNum}년 전 오늘 생성된 추억이 있어요.\n${person}님께 지금 바로 연락해보세요!"
                textView2.text = messageContent
                imageResource = matchingMemoryData.imageResId!!
                memoryPic.setImageResource(imageResource)
            }
            memoryPic.setOnClickListener {
                val dialogView = View.inflate(context, R.layout.dialog, null)
                val dlg = context?.let { it1 -> AlertDialog.Builder(it1).create() }

                // 다이얼로그의 뷰 요소들 참조
                val ivPic: ImageView = dialogView.findViewById(R.id.ivPic)
                val personEditText: EditText = dialogView.findViewById(R.id.personEditText)
                val dateEditText: EditText = dialogView.findViewById(R.id.dateEditText)
                val memoryEditText: EditText = dialogView.findViewById(R.id.memoryEditText)
                val saveButton: Button = dialogView.findViewById(R.id.saveButton)
                val editButton: Button = dialogView.findViewById(R.id.editButton)

                ivPic.setImageResource(imageResource)

                // 입력된 데이터가 있는 경우
                personEditText.setText(person)
                personEditText.setTextColor(Color.parseColor("#000000"))
                dateEditText.setText(memoryDate)
                dateEditText.setTextColor(Color.parseColor("#000000"))
                memoryEditText.setText(memoryComment)
                memoryEditText.setTextColor(Color.parseColor("#000000"))

                // 입력 필드를 비활성화하고 저장 버튼을 숨김
                personEditText.isEnabled = false
                dateEditText.isEnabled = false
                memoryEditText.isEnabled = false
                saveButton.text = "닫기"
                editButton.visibility = View.GONE

                saveButton.setOnClickListener {
                    dlg?.dismiss()
                }
                dlg?.setView(dialogView)
                dlg?.show()
            }
            val contacts = loadContactsFromJson()
            phoneNumber = contacts.find { it.name == person }?.phoneNumber.toString()
            hasMatchingMemory = true
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
        var matchingBirthdays: MutableList<Contact> = mutableListOf()
        if (!hasMatchingMemory) {
            contacts.forEach { contact ->
                val birthday = contact.birthday
                if (birthday != null) {
                    if (birthday.split("-", limit = 2)[1] == formattedDate.split("-", limit = 2)[1]) {
                        matchingBirthdays.add(contact)
                    }
                }
            }
            if (matchingBirthdays.size > 0) {
                val matchingBirthday = matchingBirthdays.random()
                val name = matchingBirthday.name
                val age = matchingBirthday.birthday?.let { calculateAge(it) }
                val birthdayNotifyMessage = "오늘은 $name 님의 ${age?.plus(1)} 번째 생일입니다! \n 생일 축하 메세지를 전송해보세요!"
                val messageContents: List<String> = listOf(
                    "자니...? 오랜만에 연락해서 미안해. 오늘 네 생일인 거 잊지 않았어. 생일 진심으로 축하해! 잘 지내고 있지? 시간이 너무 빨리 지나갔네. 조만간 얼굴 보고 이야기 나누자!",
                    "자니...? 요즘 어떻게 지내? 오늘 네 생일이라는 걸 생각하니 그냥 지나칠 수가 없었어. 생일 정말 축하해! 오랜만에 네 목소리도 듣고 싶은데, 언제 한번 연락할게.",
                    "자니...? 오랜만에 연락해서 놀랐지? 오늘 네 생일이라 생각이 나서 이렇게 연락해. 생일 축하하고, 네가 있어 항상 고마워. 시간이 되면 만나서 축하해주고 싶어.",
                    "자니...? 요즘 잘 지내고 있지? 오늘 네 생일이라 그냥 넘어갈 수 없어서 이렇게 연락해. 생일 축하해! 항상 건강하고 행복하길 바래. 조만간 얼굴 보자!",
                    "자니...? 오늘 네 생일이라 생각나서 연락했어. 생일 진심으로 축하해! 요즘 어떻게 지내고 있어? 오랜만에 만나서 이야기 나누고 싶어. 답장 기다릴게!"
                )
                notificationsViewModel.text.observe(viewLifecycleOwner) {
                    textView1.text = birthdayNotifyMessage
                    textView2.text = messageContents.random()
                }
                val imageResource = context?.resources?.getIdentifier(matchingBirthday.picture, "drawable", context?.packageName) ?: R.drawable.logo
                memoryPic.setImageResource(imageResource)
                memoryPic.setOnClickListener {
                    val dialogView = View.inflate(context, R.layout.dialog, null)
                    val dlg = context?.let { it1 -> AlertDialog.Builder(it1).create() }

                    // 다이얼로그의 뷰 요소들 참조
                    val ivPic: ImageView = dialogView.findViewById(R.id.ivPic)
                    val personEditText: EditText = dialogView.findViewById(R.id.personEditText)
                    val dateEditText: EditText = dialogView.findViewById(R.id.dateEditText)
                    val memoryEditText: EditText = dialogView.findViewById(R.id.memoryEditText)
                    val saveButton: Button = dialogView.findViewById(R.id.saveButton)
                    val editButton: Button = dialogView.findViewById(R.id.editButton)
                    val text1: TextView = dialogView.findViewById(R.id.text1)
                    val text2: TextView = dialogView.findViewById(R.id.text2)
                    val text3: TextView = dialogView.findViewById(R.id.text3)

                    ivPic.setImageResource(imageResource)

                    // 입력된 데이터가 있는 경우
                    text1.text = "생일인 사람:"
                    personEditText.setText(matchingBirthday.name)
                    personEditText.setTextColor(Color.parseColor("#000000"))
                    text2.text = "생일:"
                    dateEditText.setText(matchingBirthday.birthday)
                    dateEditText.setTextColor(Color.parseColor("#000000"))
                    text3.text = "마지막 인사 기록"
                    memoryEditText.setText(matchingBirthday.lastContactedDate)
                    memoryEditText.setTextColor(Color.parseColor("#000000"))

                    // 입력 필드를 비활성화하고 저장 버튼을 숨김
                    personEditText.isEnabled = false
                    dateEditText.isEnabled = false
                    memoryEditText.isEnabled = false
                    saveButton.text = "닫기"
                    editButton.visibility = View.GONE

                    saveButton.setOnClickListener {
                        dlg?.dismiss()
                    }
                    dlg?.setView(dialogView)
                    dlg?.show()
                }
                hasMatchingBirthday = true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkOldestContact(
        contacts: List<Contact>,
        formattedDate: String,
        notificationsViewModel: NotificationsViewModel,
        textView1: TextView,
        textView2: TextView,
        memoryPic: ImageView
    ) {
        if (!hasMatchingMemory && !hasMatchingBirthday) {
            var matchingOldestContacts: MutableList<Contact> = mutableListOf()
            contacts.forEach { contact ->
                if (ChronoUnit.DAYS.between(LocalDate.parse(contact.lastContactedDate), LocalDate.parse(formattedDate)) >= 730) {
                    matchingOldestContacts.add(contact)
                }
            }
            if (matchingOldestContacts.size > 0) {
                val oldestContact = matchingOldestContacts.random()
                person = oldestContact.name
                val messageContents: List<String> = listOf(
                    "자니...? 정말 오랜만이야. 요즘 어떻게 지내고 있어? 문득 네 생각이 나서 연락해봤어. 잘 지내고 있지? 시간 되면 오랜만에 만나서 이야기 나누자!",
                    "자니...? 오랜만에 연락해서 놀랐지? 요즘 어떻게 지내? 예전 사진첩을 보다가 네가 생각나서 연락했어. 오랜만에 만나서 옛날 이야기 나누면 좋겠다.",
                    "자니...? 정말 오랜만이야. 네가 잘 지내고 있는지 궁금해서 연락해봤어. 요즘 어떻게 지내? 조만간 만나서 근황도 나누고 좋은 시간 보내고 싶어.",
                    "자니...? 너무 오랜만이야! 요즘 어떻게 지내? 나도 바쁘게 지냈는데, 네 생각이 나서 연락했어. 시간 되면 오랜만에 만나서 근황도 나누고 이야기도 좀 하자.",
                    "자니...? 오랜만에 네가 생각나서 연락해봤어. 요즘 어떻게 지내? 잘 지내고 있지? 오랜만에 만나서 그동안 못 나눈 이야기들 나눠보자!"
                )
                messageContent = messageContents.random()
                notificationsViewModel.text.observe(viewLifecycleOwner) {
                    val daysSinceLastContact = ChronoUnit.DAYS.between(LocalDate.parse(oldestContact.lastContactedDate), LocalDate.parse(formattedDate))
                    textView1.text = "${person}님과 연락한 지 벌써 ${daysSinceLastContact}일이 지났어요.\n ${person} 님께 지금 바로 연락해보세요!"
                    textView2.text = messageContent
                    imageResource = context?.resources?.getIdentifier(oldestContact.picture, "drawable", context?.packageName) ?: R.drawable.logo
                    memoryPic.setImageResource(imageResource)
                }
                memoryPic.setOnClickListener {
                    val dialogView = View.inflate(context, R.layout.dialog, null)
                    val dlg = context?.let { it1 -> AlertDialog.Builder(it1).create() }

                    // 다이얼로그의 뷰 요소들 참조
                    val ivPic: ImageView = dialogView.findViewById(R.id.ivPic)
                    val personEditText: EditText = dialogView.findViewById(R.id.personEditText)
                    val dateEditText: EditText = dialogView.findViewById(R.id.dateEditText)
                    val memoryEditText: EditText = dialogView.findViewById(R.id.memoryEditText)
                    val saveButton: Button = dialogView.findViewById(R.id.saveButton)
                    val editButton: Button = dialogView.findViewById(R.id.editButton)
                    val text1: TextView = dialogView.findViewById(R.id.text1)
                    val text2: TextView = dialogView.findViewById(R.id.text2)
                    val text3: TextView = dialogView.findViewById(R.id.text3)

                    ivPic.setImageResource(imageResource)

                    // 입력된 데이터가 있는 경우
                    text1.text = "이름:"
                    personEditText.setText(oldestContact.name)
                    personEditText.setTextColor(Color.parseColor("#000000"))
                    text2.text = "첫 인사 기록:"
                    dateEditText.setText(oldestContact.savedDate)
                    dateEditText.setTextColor(Color.parseColor("#000000"))
                    text3.text = "마지막 인사 기록"
                    memoryEditText.setText(oldestContact.lastContactedDate)
                    memoryEditText.setTextColor(Color.parseColor("#000000"))

                    // 입력 필드를 비활성화하고 저장 버튼을 숨김
                    personEditText.isEnabled = false
                    dateEditText.isEnabled = false
                    memoryEditText.isEnabled = false
                    saveButton.text = "닫기"
                    editButton.visibility = View.GONE

                    saveButton.setOnClickListener {
                        dlg?.dismiss()
                    }
                    dlg?.setView(dialogView)
                    dlg?.show()
                }
                phoneNumber = oldestContact.phoneNumber
                hasOldestContact = true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkRandomContact(
        contacts: List<Contact>,
        notificationsViewModel: NotificationsViewModel,
        textView1: TextView,
        textView2: TextView,
        memoryPic: ImageView
    ) {
        if (hasRandom) {
            val random = Random()
            val randomIndex = random.nextInt(contacts.size)
            val randomContact = contacts[randomIndex]

            val lastContactedDate = LocalDate.parse(randomContact.lastContactedDate)
            val daysSinceLastContact = ChronoUnit.DAYS.between(lastContactedDate, LocalDate.now())

            val person = randomContact.name
            val messageContents: List<String> = listOf(
                "자니...? 정말 오랜만이야. 요즘 어떻게 지내고 있어? 문득 네 생각이 나서 연락해봤어. 잘 지내고 있지? 시간 되면 오랜만에 만나서 이야기 나누자!",
                "자니...? 오랜만에 연락해서 놀랐지? 요즘 어떻게 지내? 예전 사진첩을 보다가 네가 생각나서 연락했어. 오랜만에 만나서 옛날 이야기 나누면 좋겠다.",
                "자니...? 정말 오랜만이야. 네가 잘 지내고 있는지 궁금해서 연락해봤어. 요즘 어떻게 지내? 조만간 만나서 근황도 나누고 좋은 시간 보내고 싶어.",
                "자니...? 너무 오랜만이야! 요즘 어떻게 지내? 나도 바쁘게 지냈는데, 네 생각이 나서 연락했어. 시간 되면 오랜만에 만나서 근황도 나누고 이야기도 좀 하자.",
                "자니...? 오랜만에 네가 생각나서 연락해봤어. 요즘 어떻게 지내? 잘 지내고 있지? 오랜만에 만나서 그동안 못 나눈 이야기들 나눠보자!"
            )
            val messageContent = messageContents.random()
            notificationsViewModel.text.observe(viewLifecycleOwner) {
                textView1.text = "${person}님과 연락한 지 벌써 ${daysSinceLastContact}일이 지났어요.\n ${person} 님께 지금 바로 연락해보세요!"
                textView2.text = messageContent
                imageResource = context?.resources?.getIdentifier(randomContact.picture, "drawable", context?.packageName) ?: R.drawable.logo
                memoryPic.setImageResource(imageResource)
            }
            memoryPic.setOnClickListener {
                val dialogView = View.inflate(context, R.layout.dialog, null)
                val dlg = context?.let { it1 -> AlertDialog.Builder(it1).create() }

                // 다이얼로그의 뷰 요소들 참조
                val ivPic: ImageView = dialogView.findViewById(R.id.ivPic)
                val personEditText: EditText = dialogView.findViewById(R.id.personEditText)
                val dateEditText: EditText = dialogView.findViewById(R.id.dateEditText)
                val memoryEditText: EditText = dialogView.findViewById(R.id.memoryEditText)
                val saveButton: Button = dialogView.findViewById(R.id.saveButton)
                val editButton: Button = dialogView.findViewById(R.id.editButton)
                val text1: TextView = dialogView.findViewById(R.id.text1)
                val text2: TextView = dialogView.findViewById(R.id.text2)
                val text3: TextView = dialogView.findViewById(R.id.text3)

                ivPic.setImageResource(imageResource)

                // 입력된 데이터가 있는 경우
                text1.text = "이름:"
                personEditText.setText(randomContact.name)
                personEditText.setTextColor(Color.parseColor("#000000"))
                text2.text = "첫 인사 기록"
                dateEditText.setText(randomContact.savedDate)
                dateEditText.setTextColor(Color.parseColor("#000000"))
                text3.text = "마지막 인사 기록"
                memoryEditText.setText(randomContact.lastContactedDate)
                memoryEditText.setTextColor(Color.parseColor("#000000"))

                // 입력 필드를 비활성화하고 저장 버튼을 숨김
                personEditText.isEnabled = false
                dateEditText.isEnabled = false
                memoryEditText.isEnabled = false
                saveButton.text = "닫기"
                editButton.visibility = View.GONE

                saveButton.setOnClickListener {
                    dlg?.dismiss()
                }
                dlg?.setView(dialogView)
                dlg?.show()
            }
            phoneNumber = randomContact.phoneNumber
        }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun findOldestContact(contacts: List<Contact>): Contact? {
        return contacts.minByOrNull { LocalDate.parse(it.lastContactedDate) }
    }

}
