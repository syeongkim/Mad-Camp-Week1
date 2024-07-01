package com.example.myapplication.ui.dashboard

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentDashboardBinding

// 각 이미지에 대한 데이터를 저장할 data class
data class ImageData(
    val imageResId: Int,
    var person: String? = null,
    var date: String? = null,
    var memory: String? = null
)

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // 각 이미지에 대한 데이터 리스트를 초기화
    private val imageDataList = mutableListOf(
        ImageData(R.drawable.pic1), ImageData(R.drawable.pic2), ImageData(R.drawable.pic3),
        ImageData(R.drawable.pic4), ImageData(R.drawable.pic5), ImageData(R.drawable.pic6),
        ImageData(R.drawable.pic7), ImageData(R.drawable.pic8), ImageData(R.drawable.pic9),
        ImageData(R.drawable.pic10), ImageData(R.drawable.pic1), ImageData(R.drawable.pic2),
        ImageData(R.drawable.pic3), ImageData(R.drawable.pic4), ImageData(R.drawable.pic5),
        ImageData(R.drawable.pic6), ImageData(R.drawable.pic7), ImageData(R.drawable.pic8),
        ImageData(R.drawable.pic9), ImageData(R.drawable.pic10), ImageData(R.drawable.pic1),
        ImageData(R.drawable.pic2), ImageData(R.drawable.pic3), ImageData(R.drawable.pic4),
        ImageData(R.drawable.pic5), ImageData(R.drawable.pic6), ImageData(R.drawable.pic7),
        ImageData(R.drawable.pic8), ImageData(R.drawable.pic9), ImageData(R.drawable.pic10)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridView: GridView = binding.gridView
        // 어댑터를 생성하여 그리드뷰에 설정
        val gridAdapter = MyGridAdapter(requireContext(), imageDataList)
        gridView.adapter = gridAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                BitmapFactory.decodeResource(context.resources, imageData.imageResId, this)
                inSampleSize = calculateInSampleSize(this, 200, 300)
                inJustDecodeBounds = false
            }
            val bitmap = BitmapFactory.decodeResource(context.resources, imageData.imageResId, options)
            imageView.setImageBitmap(bitmap)

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

                ivPic.setImageResource(imageData.imageResId)

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

        private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            val (height: Int, width: Int) = options.run { outHeight to outWidth }
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                val halfHeight: Int = height / 2
                val halfWidth: Int = width / 2

                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }
    }
}
