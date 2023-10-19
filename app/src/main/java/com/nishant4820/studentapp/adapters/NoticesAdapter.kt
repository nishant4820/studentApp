package com.nishant4820.studentapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.nishant4820.studentapp.R
import com.nishant4820.studentapp.data.models.NoticeData
import com.nishant4820.studentapp.data.models.NoticeResponse
import com.nishant4820.studentapp.databinding.ItemNoticeBinding
import com.nishant4820.studentapp.utils.Constants.LOG_TAG
import com.nishant4820.studentapp.utils.MyDiffUtil

class NoticesAdapter : RecyclerView.Adapter<NoticesAdapter.ViewHolder>() {

    private var notices = emptyList<NoticeData>()

    class ViewHolder(private val binding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notice: NoticeData) {
            binding.tvTitle.text = notice.name
            binding.tvDescription.text = notice.description
            Log.d(LOG_TAG, "NoticesAdapter: bind, fileUrl: ${notice.noticeFile.fileUrl}")
            if (notice.noticeType == "img") {
                binding.ivBanner.load(notice.noticeFile.fileUrl) {
                    crossfade(true)
                    placeholder(R.drawable.iv_default_image)
                    error(R.drawable.ic_error_placeholder)
//                transformations(CircleCropTransformation())
                    transformations(RoundedCornersTransformation(12f, 12f, 12f, 12f))
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNoticeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = notices.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentNotice = notices[position]
        holder.bind(currentNotice)
    }

    fun setData(newData: NoticeResponse) {
        val myDiffUtil = MyDiffUtil(notices, newData.data)
        val diffUtilResult = DiffUtil.calculateDiff(myDiffUtil)
        notices = newData.data
        diffUtilResult.dispatchUpdatesTo(this)
    }

    fun clearData() {
        val size = notices.size
        notices = emptyList()
        notifyItemRangeRemoved(1, size)
    }
}