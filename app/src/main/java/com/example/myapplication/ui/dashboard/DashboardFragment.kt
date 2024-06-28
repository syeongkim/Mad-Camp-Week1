package com.example.myapplication.ui.dashboard

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridView: GridView = binding.gridView
        val gridAdapter = MyGridAdapter(requireContext())

        gridView.adapter = gridAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class MyGridAdapter(private val context: Context) : BaseAdapter() {

        private val picID = arrayOf(
            R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4, R.drawable.pic5,
            R.drawable.pic6, R.drawable.pic7, R.drawable.pic8, R.drawable.pic9, R.drawable.pic10,
            R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4, R.drawable.pic5,
            R.drawable.pic6, R.drawable.pic7, R.drawable.pic8, R.drawable.pic9, R.drawable.pic10,
            R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4, R.drawable.pic5,
            R.drawable.pic6, R.drawable.pic7, R.drawable.pic8, R.drawable.pic9, R.drawable.pic10
        )

        override fun getCount(): Int {
            return picID.size
        }

        override fun getItem(position: Int): Any {
            return picID[position]
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

            // Load and resize the image
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                BitmapFactory.decodeResource(context.resources, picID[position], this)
                inSampleSize = calculateInSampleSize(this, 200, 300)
                inJustDecodeBounds = false
            }
            val bitmap = BitmapFactory.decodeResource(context.resources, picID[position], options)
            imageView.setImageBitmap(bitmap)

            imageView.setOnClickListener {
                val dialogView = View.inflate(context, R.layout.dialog, null)
                val dlg = AlertDialog.Builder(context)
                val ivPic: ImageView = dialogView.findViewById(R.id.ivPic)
                ivPic.setImageResource(picID[position])
                dlg.setTitle("큰 이미지")
                dlg.setNegativeButton("닫기", null)
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