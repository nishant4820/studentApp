package com.nishant4820.studentapp.ui.home.fragments.attendanceFragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nishant4820.studentapp.adapters.AttendanceAdapter
import com.nishant4820.studentapp.databinding.FragmentAttendanceBinding
import com.nishant4820.studentapp.utils.Constants
import com.nishant4820.studentapp.utils.NetworkListener
import com.nishant4820.studentapp.utils.NetworkResult
import com.nishant4820.studentapp.utils.NetworkUtils
import com.nishant4820.studentapp.viewmodels.AttendanceViewModel
import kotlinx.coroutines.launch

class AttendanceFragment : Fragment() {
    private val mAdapter by lazy { AttendanceAdapter() }
    private val attendanceViewModel: AttendanceViewModel by viewModels(ownerProducer = { requireActivity() })
    private var isFirstNetworkCallback = true
    private lateinit var networkListener: NetworkListener
    private var _binding: FragmentAttendanceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        binding.shimmerViewContainer.startShimmer()
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                requestStudentAttendance()
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
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            networkListener = NetworkListener(requireContext())
            networkListener.networkAvailability.collect { networkStatus ->
                Log.d(
                    Constants.LOG_TAG,
                    "Attendance Fragment: Network Status Observer, network status: $networkStatus"
                )
                if (networkStatus) {
                    requestStudentAttendance()
                } else if (isFirstNetworkCallback) {
                    binding.tvError.text = Constants.NETWORK_RESULT_MESSAGE_NO_INTERNET
                    binding.llNoInternet.visibility = View.VISIBLE
                }
                isFirstNetworkCallback = false
            }
        }

        attendanceViewModel.attendanceResponse.observe(viewLifecycleOwner) { response ->
            Log.d(
                Constants.LOG_TAG,
                "Attendance Fragment: Student Attendance response observer, response code: ${response.statusCode}"
            )
            binding.llNoInternet.visibility = View.GONE
            when (response) {
                is NetworkResult.Success -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    response.data?.let { mAdapter.setData(it) }
                    hideShimmerEffect()
                    if (response.message == Constants.NETWORK_RESULT_MESSAGE_NO_RESULTS) {
                        Snackbar.make(
                            binding.root,
                            Constants.NETWORK_RESULT_MESSAGE_NO_RESULTS,
                            Snackbar.LENGTH_SHORT
                        ).show()

                        binding.tvError.text = Constants.NETWORK_RESULT_MESSAGE_NO_RESULTS
                        binding.llNoInternet.visibility = View.VISIBLE
                    }
                }

                is NetworkResult.Error -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.tvError.text = response.message.toString()
                    hideShimmerEffect()
                    Snackbar.make(
                        binding.root,
                        response.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loading -> {
                    Log.d(
                        Constants.LOG_TAG,
                        "Attendance Fragment: shimmer effect from is loading state"
                    )
                    showShimmerEffect()
                }
            }
        }

        return binding.root
    }

    private fun requestStudentAttendance() {
        Log.d(Constants.LOG_TAG, "Attendance Fragment: requestStudentAttendance")
        showShimmerEffect()
        attendanceViewModel.getStudentAttendance(attendanceViewModel.applyQueries())
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showShimmerEffect() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        binding.shimmerViewContainer.showShimmer(true)
    }

    private fun hideShimmerEffect() {
        binding.shimmerViewContainer.hideShimmer()
        binding.shimmerViewContainer.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}