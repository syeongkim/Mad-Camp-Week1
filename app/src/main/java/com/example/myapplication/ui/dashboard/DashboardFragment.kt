package com.example.myapplication.ui.dashboard

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentDashboardBinding
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

// 각 이미지에 대한 데이터를 저장할 data class
data class ImageData(
    val imageResId: Int? = null,
    val imageUri: Uri? = null,
    var person: String? = null,
    var date: String? = null,
    var memory: String? = null
)

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // 각 이미지에 대한 데이터 리스트를 초기화 (반복 없이 10개만)
    private val imageDataList = mutableListOf(
        ImageData(R.drawable.pic1, person = "진유하", date = "2023-07-01", memory = "즐거운 하루"),
        ImageData(R.drawable.pic2, person = "조승완", date = "2023-07-02", memory = "멋진 순간"),
        ImageData(R.drawable.pic3, person = "정민규", date = "2023-07-03", memory = "행복한 시간"),
        ImageData(R.drawable.pic4, person = "장세일", date = "2023-07-04", memory = "기억에 남는 날"),
        ImageData(R.drawable.pic5, person = "윤우성", date = "2023-07-05", memory = "환상적인 경험"),
        ImageData(R.drawable.pic6, person = "안세혁", date = "2023-07-06", memory = "소중한 추억"),
        ImageData(R.drawable.pic7, person = "안규찬", date = "2023-07-07", memory = "즐거운 기억"),
        ImageData(R.drawable.pic8), ImageData(R.drawable.pic9), ImageData(R.drawable.pic10)
    )

    // 이미지 선택 결과를 처리하기 위한 ActivityResultLauncher 등록
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // 선택된 이미지 URI가 있으면 ImageData 객체를 생성하여 리스트의 처음에 추가
            imageDataList.add(0, ImageData(imageUri = it))
            saveImageDataList()
            gridAdapter.notifyDataSetChanged()
        }
    }

    private lateinit var gridAdapter: MyGridAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridView: GridView = binding.gridView
        loadImageDataList()
        gridAdapter = MyGridAdapter(requireContext(), imageDataList)

        gridView.adapter = gridAdapter

        // 이미지 추가 버튼 클릭 시 이미지 선택기 실행
        binding.addImageButton.setOnClickListener {
            pickImage.launch("image/*")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveImageDataList() {
        val sharedPreferences = requireContext().getSharedPreferences("image_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val jsonArray = JSONArray()

        imageDataList.forEach { imageData ->
            val jsonObject = JSONObject().apply {
                put("imageResId", imageData.imageResId)
                put("imageUri", imageData.imageUri?.toString())
                put("person", imageData.person)
                put("date", imageData.date)
                put("memory", imageData.memory)
            }
            jsonArray.put(jsonObject)
        }

        editor.putString("imageDataList", jsonArray.toString())
        editor.apply()
    }

    private fun loadImageDataList() {
        val sharedPreferences = requireContext().getSharedPreferences("image_data", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString("imageDataList", null)

        if (!jsonString.isNullOrEmpty()) {
            val jsonArray = JSONArray(jsonString)
            imageDataList.clear()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val imageResId = if (jsonObject.isNull("imageResId")) null else jsonObject.getInt("imageResId")
                val imageUri = if (jsonObject.isNull("imageUri")) null else Uri.parse(jsonObject.getString("imageUri"))
                val person = if (jsonObject.isNull("person")) null else jsonObject.getString("person")
                val date = if (jsonObject.isNull("date")) null else jsonObject.getString("date")
                val memory = if (jsonObject.isNull("memory")) null else jsonObject.getString("memory")

                imageDataList.add(ImageData(imageResId, imageUri, person, date, memory))
            }
        }
    }

    inner class MyGridAdapter(private val context: Context, private val dataList: List<ImageData>) : BaseAdapter() {

        override fun getCount(): Int {
            return dataList.size
        }

        override fun getItem(position: Int): Any {
            return dataList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val imageView: ImageView = convertView as? ImageView ?: ImageView(context).apply {
                layoutParams = ViewGroup.LayoutParams(200, 300)
                scaleType = ImageView.ScaleType.FIT_CENTER
                setPadding(5, 5, 5, 5)
            }

            val imageData = dataList[position]

            // 이미지를 로드하고 리사이즈
            try {
                val bitmap = if (imageData.imageResId != null) {
                    BitmapFactory.decodeResource(context.resources, imageData.imageResId)
                } else {
                    val inputStream = context.contentResolver.openInputStream(imageData.imageUri!!)
                    BitmapFactory.decodeStream(inputStream)
                }
                imageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                Log.e("DashboardFragment", "Error loading image", e)
            }

            // 클릭 리스너: 입력된 데이터가 있는 경우와 없는 경우를 구분
            imageView.setOnClickListener {
                val dialogView = View.inflate(context, R.layout.dialog, null)
                val dlg = AlertDialog.Builder(context)

                // 다이얼로그의 뷰 요소들 참조
                val ivPic: ImageView = dialogView.findViewById(R.id.ivPic)
                val personEditText: EditText = dialogView.findViewById(R.id.personEditText)
                val dateEditText: EditText = dialogView.findViewById(R.id.dateEditText)
                val memoryEditText: EditText = dialogView.findViewById(R.id.memoryEditText)
                val saveButton: Button = dialogView.findViewById(R.id.saveButton)

                if (imageData.imageResId != null) {
                    ivPic.setImageResource(imageData.imageResId)
                } else {
                    ivPic.setImageURI(imageData.imageUri)
                }

                // 입력된 데이터가 있는 경우
                if (imageData.person != null && imageData.date != null && imageData.memory != null) {
                    personEditText.setText(imageData.person)
                    dateEditText.setText(imageData.date)
                    memoryEditText.setText(imageData.memory)

                    // 입력 필드를 비활성화하고 저장 버튼을 숨김
                    personEditText.isEnabled = false
                    dateEditText.isEnabled = false
                    memoryEditText.isEnabled = false
                    saveButton.visibility = View.GONE

                    dlg.setNegativeButton("닫기", null)
                } else {
                    // 입력된 데이터가 없는 경우
                    saveButton.setOnClickListener {
                        // 입력된 데이터를 저장
                        imageData.person = personEditText.text.toString()
                        imageData.date = dateEditText.text.toString()
                        imageData.memory = memoryEditText.text.toString()

                        // 변경 사항을 SharedPreferences에 저장
                        saveImageDataList()

                        // 다이얼로그를 닫음
                        dlg.create().dismiss()
                    }

                    dlg.setNegativeButton("닫기", null)
                }

                dlg.setView(dialogView)
                dlg.show()
            }

            return imageView
        }
    }
}










