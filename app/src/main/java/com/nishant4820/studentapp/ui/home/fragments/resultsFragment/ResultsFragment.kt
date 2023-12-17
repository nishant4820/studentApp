package com.nishant4820.studentapp.ui.home.fragments.resultsFragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.snackbar.Snackbar
import com.nishant4820.studentapp.R
import com.nishant4820.studentapp.data.models.Result
import com.nishant4820.studentapp.data.models.SemesterResult
import com.nishant4820.studentapp.data.models.StudentResultResponse
import com.nishant4820.studentapp.databinding.FragmentResultsBinding
import com.nishant4820.studentapp.utils.Constants
import com.nishant4820.studentapp.utils.Constants.LOG_TAG
import com.nishant4820.studentapp.utils.NetworkListener
import com.nishant4820.studentapp.utils.NetworkResult
import com.nishant4820.studentapp.utils.NetworkUtils
import com.nishant4820.studentapp.viewmodels.ResultsViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt


class ResultsFragment : Fragment() {

    private val resultsViewModel: ResultsViewModel by viewModels(ownerProducer = { requireActivity() })
    private var isFirstNetworkCallback = true
    private lateinit var networkListener: NetworkListener
    private lateinit var resultResponse: StudentResultResponse
    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResultsBinding.inflate(inflater, container, false)

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                requestStudentResult()
            } else {
                binding.swipeRefreshLayout.isRefreshing = false
                Snackbar.make(
                    binding.root,
                    Constants.NETWORK_RESULT_MESSAGE_NO_INTERNET,
                    Snackbar.LENGTH_SHORT
                ).setAction("Settings") {
                    startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                }.show()
            }
        }

        lifecycleScope.launch {
            networkListener = NetworkListener(requireContext())
            networkListener.networkAvailability.collect { networkStatus ->
                Log.d(
                    LOG_TAG,
                    "Notices Fragment: Network Status Observer, network status: $networkStatus"
                )
                resultsViewModel.networkStatus = networkStatus
//                resultsViewModel.showNetworkStatus()
                if (networkStatus) {
                    requestStudentResult()
                } else if (isFirstNetworkCallback) {
                    binding.tvError.text = Constants.NETWORK_RESULT_MESSAGE_NO_INTERNET
//                    loadDataFromCache()
                }
                isFirstNetworkCallback = false
            }
        }

        resultsViewModel.resultResponse.observe(viewLifecycleOwner) { response ->
            Log.d(
                LOG_TAG,
                "Results Fragment: Student Result response observer, response code: ${response.statusCode}"
            )
            when (response) {
                is NetworkResult.Success -> {
                    binding.llNoInternet.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    response.data?.let {
                        resultResponse = it
                        renderData()
                    }
                }

                is NetworkResult.Error -> {
                    binding.llNoInternet.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.tvError.text = response.message.toString()
                    Snackbar.make(
                        binding.root,
                        response.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loading -> {
                    binding.llNoInternet.visibility = View.GONE
                }
            }
        }


        return binding.root
    }

    private fun requestStudentResult() {
        Log.d(LOG_TAG, "Results Fragment: requestStudentResult")
        resultsViewModel.getStudentResult(resultsViewModel.applyQueries())
    }

    private fun renderData() {
        binding.apply {
            clContent.visibility = View.VISIBLE
            tvEnrollment.text = resultResponse.enrollmentNumber
            tvName.text = resultResponse.studentName
            tvBatch.text = resultResponse.batch.toString()
            tvCollege.text = resultResponse.college
            tvBranch.text = resultResponse.branch
        }

        // Selecting Overall Result by Default
        binding.tvSemester.setText(resources.getStringArray(R.array.semester_array)[0])
        renderOverallData()

        val dropdownItems = resources.getStringArray(R.array.semester_array)
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            dropdownItems
        )
        binding.tvSemester.setAdapter(arrayAdapter)

        // Listener for Semester Selection
        binding.tvSemester.setOnItemClickListener { _, _, position, _ ->
            if (position == 0) {
                // Overall Selected
                renderOverallData()
            } else {
                // Semester Selected
                renderSemesterData(position)
            }
        }
    }

    private fun renderOverallData() {
        binding.apply {
            chartOverall.visibility = View.VISIBLE
            chartOverallPercentage.visibility = View.VISIBLE
            chart.visibility = View.GONE
            llHeaderSemester.visibility = View.GONE
            llHeaderOverall.visibility = View.VISIBLE
            tvResultTitle.text = String.format("%s Result", "Overall")
            tvGpaTitle.text = getString(R.string.cgpa)
            tvGpa.text = String.format("%.3f", resultResponse.overallCgpa)
            tvPercentage.text = String.format("%.2f%%", resultResponse.overallPercentage)
            tvCredits.text = resultResponse.totalCredits.toString()
            val maxMarks = resultResponse.subjectCount * 100
            tvMarks.text = String.format("%d/%d", resultResponse.totalMarks, maxMarks)

        }

        binding.llSubjectsContainer.removeAllViews()
        for (index in 1..8) {
            val semesterResult = getSemesterResultHelper(resultResponse.result, index) ?: continue
            val semesterLayout = layoutInflater.inflate(
                R.layout.result_breakdown_overall_item_layout,
                binding.llSubjectsContainer,
                false
            ) as LinearLayout
            if (index % 2 == 0) semesterLayout.setBackgroundColor(Color.LTGRAY)

            semesterLayout.findViewById<TextView>(R.id.tv_semester).text =
                String.format("Semester %d", index)

            val maxMarks = semesterResult.subjects.size * 100
            val obtainedMarks =
                (semesterResult.percentage * semesterResult.subjects.size).roundToInt()
            semesterLayout.findViewById<TextView>(R.id.tv_marks).text =
                String.format("%d/%d", obtainedMarks, maxMarks)

            semesterLayout.findViewById<TextView>(R.id.tv_percentage).text =
                String.format("%.2f%%", semesterResult.percentage)

            semesterLayout.findViewById<TextView>(R.id.tv_cgpa).text =
                String.format("%.3f", semesterResult.cgpa)
            binding.llSubjectsContainer.addView(semesterLayout)

        }

        setupOverallChartView()
    }

    private fun setupOverallChartView() {

        val formatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return if (arrayOf(1F, 2f, 3f, 4f, 5f, 6f, 7f, 8f).contains(value)) {
                    String.format("Sem %d", value.toInt())
                } else ""
            }
        }

        binding.chartOverall.apply {
            setScaleEnabled(false)
            setPinchZoom(false)
            setTouchEnabled(false)
            setDrawGridBackground(false)
            extraBottomOffset = 20F
            description.isEnabled = false
            legend.isEnabled = true

            axisLeft.setDrawAxisLine(false)
            axisLeft.spaceBottom = 20F

            axisRight.isEnabled = false

            xAxis.setDrawAxisLine(false)
            xAxis.setDrawGridLines(false)
            xAxis.setDrawLabels(true)
            xAxis.position = XAxisPosition.BOTTOM
            xAxis.labelRotationAngle = -25F
            xAxis.axisMinimum = 0.8F
            xAxis.axisMaximum = 8F
            xAxis.valueFormatter = formatter
        }
        binding.chartOverallPercentage.apply {
            setScaleEnabled(false)
            setPinchZoom(false)
            setTouchEnabled(false)
            setDrawGridBackground(false)
            extraBottomOffset = 20F
            description.isEnabled = false
            legend.isEnabled = true

            axisLeft.setDrawAxisLine(false)
            axisLeft.spaceBottom = 20F

            axisRight.isEnabled = false

            xAxis.setDrawAxisLine(false)
            xAxis.setDrawGridLines(false)
            xAxis.setDrawLabels(true)
            xAxis.position = XAxisPosition.BOTTOM
            xAxis.labelRotationAngle = -25F
            xAxis.axisMinimum = 0.8F
            xAxis.axisMaximum = 8F
            xAxis.valueFormatter = formatter
        }
        setOverallDataInGraph()
        binding.chartOverall.invalidate()
        binding.chartOverallPercentage.invalidate()
    }

    private fun setOverallDataInGraph() {
        val values = ArrayList<Entry>()
        val valuesPercentage = ArrayList<Entry>()
        for (index in 1..8) {
            val semesterResult = getSemesterResultHelper(resultResponse.result, index) ?: continue
            values.add(Entry(index.toFloat(), semesterResult.cgpa.toFloat()))
            valuesPercentage.add(Entry(index.toFloat(), semesterResult.percentage.toFloat()))
        }

        val lineDataSet: LineDataSet
        val lineDataSetPercentage: LineDataSet

        if (binding.chartOverall.data != null && binding.chartOverall.data.dataSetCount > 0) {
            lineDataSet = binding.chartOverall.data.getDataSetByIndex(0) as LineDataSet
            lineDataSet.values = values
            binding.chartOverall.data.notifyDataChanged()
            binding.chartOverall.notifyDataSetChanged()

            lineDataSetPercentage =
                binding.chartOverallPercentage.data.getDataSetByIndex(0) as LineDataSet
            lineDataSetPercentage.values = valuesPercentage
            binding.chartOverallPercentage.data.notifyDataChanged()
            binding.chartOverallPercentage.notifyDataSetChanged()
        } else {
            val formatter = object : ValueFormatter() {
                override fun getPointLabel(entry: Entry?): String {
                    return String.format("%.2f", entry?.y)
                }
            }
            lineDataSet = LineDataSet(values, "")
            lineDataSet.label = "Semester GPA"
            lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            lineDataSet.circleColors = listOf(Color.parseColor("#FF444444"))
//            lineDataSet.circleRadius = convertPxToDp(requireContext(), 12F)
//            lineDataSet.circleHoleRadius = convertPxToDp(requireContext(), 6F)
            lineDataSet.fillDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(Color.parseColor("#B3888888"), Color.parseColor("#B3EEEEEE"))
            )
            lineDataSet.setDrawIcons(false)
            lineDataSet.setColors(Color.parseColor("#FF666666"))
            lineDataSet.setDrawFilled(true)

            val data = LineData(lineDataSet)
            data.setValueTextColor(Color.BLACK)
            data.setValueTextSize(10F)
            data.setValueFormatter(formatter)

            binding.chartOverall.data = data

            lineDataSetPercentage = lineDataSet.copy() as LineDataSet
            lineDataSetPercentage.values = valuesPercentage
            lineDataSetPercentage.label = "Percentage"

            val dataPercentage = LineData(lineDataSetPercentage)
            dataPercentage.setValueTextColor(Color.BLACK)
            dataPercentage.setValueTextSize(10F)
            dataPercentage.setValueFormatter(formatter)

            binding.chartOverallPercentage.data = dataPercentage
        }
    }

    private fun renderSemesterData(position: Int) {
        val semesterResult = getSemesterResultHelper(resultResponse.result, position)
        if (semesterResult == null) {
            Snackbar.make(binding.tvSemester, "No Data Available", Snackbar.LENGTH_SHORT).show()
            return
        }
        binding.apply {
            chartOverall.visibility = View.GONE
            chartOverallPercentage.visibility = View.GONE
            chart.visibility = View.VISIBLE
            llHeaderOverall.visibility = View.GONE
            llHeaderSemester.visibility = View.VISIBLE
            tvResultTitle.text = String.format("Sem %d Result", position)
            tvGpaTitle.text = getString(R.string.sgpa)
            tvGpa.text = String.format("%.3f", semesterResult.cgpa)
            tvPercentage.text = String.format("%.2f%%", semesterResult.percentage)
            tvCredits.text = semesterResult.totalCredits.toString()
            val maxMarks = semesterResult.subjects.size * 100
            val obtainedMarks =
                (semesterResult.percentage * semesterResult.subjects.size).roundToInt()
            tvMarks.text = String.format("%d/%d", obtainedMarks, maxMarks)
        }

        binding.llSubjectsContainer.removeAllViews()

        for ((index, subject) in semesterResult.subjects.withIndex()) {
            val subjectLayout = layoutInflater.inflate(
                R.layout.result_breakdown_item_layout,
                binding.llSubjectsContainer,
                false
            ) as LinearLayout
            if (index % 2 != 0) subjectLayout.setBackgroundColor(Color.LTGRAY)
            subjectLayout.findViewById<TextView>(R.id.tv_subject_credit).text =
                String.format("%s (%d)", subject.subjectName, subject.subjectCredit)
            subjectLayout.findViewById<TextView>(R.id.tv_int_ext).text =
                String.format("%d | %d", subject.internalMarks, subject.externalMarks)
            subjectLayout.findViewById<TextView>(R.id.tv_marks_grade).text =
                String.format("%d %s", subject.totalMarks, subject.grade)
            binding.llSubjectsContainer.addView(subjectLayout)
        }

        setupSemesterChartView(semesterResult)
    }

    private fun setupSemesterChartView(semesterResult: SemesterResult) {

        binding.chart.apply {
            setScaleEnabled(false)
            setPinchZoom(false)
            setTouchEnabled(false)
            setDrawGridBackground(false)
            setDrawValueAboveBar(false)
            isHighlightFullBarEnabled = false
            extraBottomOffset = 20F
            description.isEnabled = false
            legend.isEnabled = true

            axisLeft.isEnabled = false

            val limitLine = LimitLine(100F, "Max")
            limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
            limitLine.lineColor = Color.GRAY
            limitLine.lineWidth = 1F
            axisRight.addLimitLine(limitLine)
            axisRight.labelCount = 4
            axisRight.setDrawAxisLine(false)

            xAxis.position = XAxisPosition.BOTTOM
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawGridLines(false)
            xAxis.labelRotationAngle = -25F
            xAxis.labelCount = semesterResult.subjects.size
            xAxis.setDrawLabels(true)
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    val index = value.toInt() - 1
                    return if (index >= 0 && index < semesterResult.subjects.size)
                        semesterResult.subjects[abs(index - semesterResult.subjects.size + 1)].subjectCode
                    else ""
                }
            }
        }
        setSemesterDataInGraph(semesterResult)
        binding.chart.animateY(500)
        binding.chart.invalidate()
    }

    private fun setSemesterDataInGraph(semesterResult: SemesterResult) {
        val values = ArrayList<BarEntry>()
        for ((index, subject) in semesterResult.subjects.withIndex()) {
            values.add(
                BarEntry(
                    (abs(index - semesterResult.subjects.size)).toFloat(),
                    floatArrayOf(subject.internalMarks.toFloat(), subject.externalMarks.toFloat())
                )
            )
        }

        val barDataSet: BarDataSet

        if (binding.chart.data != null && binding.chart.data.dataSetCount > 0) {
            barDataSet = binding.chart.data.getDataSetByIndex(0) as BarDataSet
            barDataSet.values = values
            binding.chart.data.notifyDataChanged()
            binding.chart.notifyDataSetChanged()
        } else {
            barDataSet = BarDataSet(values, "")
            barDataSet.setDrawIcons(false)
            barDataSet.setColors(Color.parseColor("#80444444"), Color.LTGRAY)
            barDataSet.stackLabels = arrayOf("Internal Marks", "External Marks")

            val data = BarData(barDataSet)
            data.setValueFormatter(object : ValueFormatter() {
                override fun getBarStackedLabel(value: Float, stackedEntry: BarEntry?): String {
                    return String.format("%d", value.toInt())
                }
            })
            data.setValueTextColor(Color.BLACK)
            data.setValueTextSize(10F)

            binding.chart.data = data
        }

    }

    private fun getSemesterResultHelper(result: Result, semester: Int): SemesterResult? {
        return when (semester) {
            1 -> result.sem1
            2 -> result.sem2
            3 -> result.sem3
            4 -> result.sem4
            5 -> result.sem5
            6 -> result.sem6
            7 -> result.sem7
            else -> result.sem8
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}