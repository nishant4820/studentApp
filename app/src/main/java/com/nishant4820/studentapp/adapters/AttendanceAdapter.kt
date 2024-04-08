package com.nishant4820.studentapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nishant4820.studentapp.data.models.AttendanceItem
import com.nishant4820.studentapp.data.models.AttendanceResponse
import com.nishant4820.studentapp.databinding.ItemAttendanceBinding
import com.nishant4820.studentapp.utils.MyDiffUtil

class AttendanceAdapter :
    RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    private var attendanceItemList = emptyList<AttendanceItem>()

    inner class ViewHolder(private val binding: ItemAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(attendanceItem: AttendanceItem) {
            binding.tvSubjectName.text = attendanceItem.subjectName
            binding.tvStatus.text = String.format(
                "Classes attended: %d out of %d",
                attendanceItem.present,
                attendanceItem.total
            )
            val attendancePercentage = if (attendanceItem.total != 0) {
                (attendanceItem.present!!.toDouble() / attendanceItem.total!!.toDouble()) * 100.0
            } else 0.0
            binding.tvPercentage.text = String.format("Percentage: %.2f%%", attendancePercentage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAttendanceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = attendanceItemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attendanceItem = attendanceItemList[position]
        holder.bind(attendanceItem)
    }

    fun setData(newData: AttendanceResponse) {
        val myDiffUtil = MyDiffUtil(attendanceItemList, newData)
        val diffUtilResult = DiffUtil.calculateDiff(myDiffUtil)
        attendanceItemList = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    fun clearData() {
        val size = attendanceItemList.size
        attendanceItemList = emptyList()
        notifyItemRangeRemoved(1, size)
    }
}