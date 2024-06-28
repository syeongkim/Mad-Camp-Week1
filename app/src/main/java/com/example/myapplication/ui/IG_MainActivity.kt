package com.example.myapplication_imagegallary

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.IgActivityMainBinding


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "list view exam"

        var binding = IgActivityMainBinding.inflate(layoutInflater)

        val gv: GridView = binding.gridView
        val gAdapter = MyGridAdapter(
            this
        )

        gv.adapter = gAdapter
    }

    inner class MyGridAdapter(private var context: Context) : BaseAdapter() {

        private var picNames: Array<String> = arrayOf(
            "pic1", "pic2", "pic3", "pic4", "pic5",
            "pic6", "pic7", "pic8", "pic9", "pic10",
            "pic1", "pic2", "pic3", "pic4", "pic5",
            "pic6", "pic7", "pic8", "pic9", "pic10",
            "pic1", "pic2", "pic3", "pic4", "pic5",
            "pic6", "pic7", "pic8", "pic9", "pic10"
        )

        override fun getCount(): Int {
            return picNames.size
        }

        override fun getItem(i: Int): Any {
            return picNames[i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        @SuppressLint("DiscouragedApi", "ViewHolder")
        override fun getView(i: Int, view: View, viewGroup: ViewGroup): View {
            val imageView = ImageView(context)
            imageView.layoutParams = ViewGroup.LayoutParams(200, 300)
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            imageView.setPadding(5, 5, 5, 5)

            // 리소스 ID 가져오기
            val resId =
                context.resources.getIdentifier(picNames[i], "drawable", context.packageName)
            if (resId != 0) {
                imageView.setImageResource(resId)
            }

            // 갤러리의 이미지뷰를 눌렀을 때
            // 다이얼로그뷰를 팝업하여 큰 이미지를 출력합니다.
            imageView.setOnClickListener {
                val dialogView = View.inflate(
                    context,
                    R.layout.ig_dialog,
                    null
                )
                val dlg = AlertDialog.Builder(context)
                val ivPic = dialogView.findViewById<ImageView>(R.id.ivPic)
                ivPic.setImageResource(resId)
                dlg.setTitle("큰 이미지")
                dlg.setIcon(R.drawable.ic_launcher_background)
                dlg.setView(dialogView)
                dlg.setNegativeButton("닫기", null)
                dlg.show()
            }

            return imageView
        }
    }
}