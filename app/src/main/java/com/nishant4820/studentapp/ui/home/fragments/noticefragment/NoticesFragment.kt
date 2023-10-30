package com.nishant4820.studentapp.ui.home.fragments.noticefragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nishant4820.studentapp.R
import com.nishant4820.studentapp.adapters.NoticesAdapter
import com.nishant4820.studentapp.data.models.NoticeData
import com.nishant4820.studentapp.databinding.FragmentNoticesBinding
import com.nishant4820.studentapp.utils.Constants.LOG_TAG
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_NO_INTERNET
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_NO_RESULTS
import com.nishant4820.studentapp.utils.NetworkListener
import com.nishant4820.studentapp.utils.NetworkResult
import com.nishant4820.studentapp.utils.NetworkUtils
import com.nishant4820.studentapp.utils.OnListItemClickListener
import com.nishant4820.studentapp.viewmodels.MainViewModel
import com.nishant4820.studentapp.viewmodels.NoticesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoticesFragment : Fragment(), OnListItemClickListener {
    private val mAdapter by lazy { NoticesAdapter(this) }
    private val mainViewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() })
    private val noticesViewModel: NoticesViewModel by viewModels(ownerProducer = { requireActivity() })
    private var isFirstNetworkCallback = true
    private lateinit var networkListener: NetworkListener
    private var _binding: FragmentNoticesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNoticesBinding.inflate(inflater, container, false)
        binding.shimmerViewContainer.startShimmer()
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                requestNotices()
            } else {
                binding.swipeRefreshLayout.isRefreshing = false
                Snackbar.make(
                    binding.root,
                    NETWORK_RESULT_MESSAGE_NO_INTERNET,
                    Snackbar.LENGTH_SHORT
                ).setAction("Settings") {
                    startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                }.show()
            }
        }
        binding.fabNoticeFilter.setOnClickListener {
            if (noticesViewModel.networkStatus) {
                findNavController().navigate(R.id.action_noticesFragment_to_noticesBottomSheetFragment)
            } else {
                noticesViewModel.showNetworkStatus()
            }
        }

        setupRecyclerView()

        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                Log.d(LOG_TAG, "NoticesFragment: nestedScrollView end detected")
            }
        })

        noticesViewModel.readBackOnline.asLiveData().observe(viewLifecycleOwner) {
            noticesViewModel.backOnline = it
        }

        lifecycleScope.launch {
            networkListener = NetworkListener(requireContext())
            networkListener.networkAvailability.collect { networkStatus ->
                Log.d(
                    LOG_TAG,
                    "Notices Fragment: Network Status Observer, network status: $networkStatus"
                )
                noticesViewModel.networkStatus = networkStatus
                noticesViewModel.showNetworkStatus()
                if (networkStatus) {
                    requestNotices()
                } else if (isFirstNetworkCallback) {
                    binding.tvError.text = NETWORK_RESULT_MESSAGE_NO_INTERNET
                    loadDataFromCache()
                }
                isFirstNetworkCallback = false
            }
        }

        mainViewModel.noticesResponse.observe(viewLifecycleOwner) { response ->
            Log.d(
                LOG_TAG,
                "Notices Fragment: Notices response observer, response code: ${response.statusCode}"
            )
            when (response) {
                is NetworkResult.Success -> {
                    binding.llNoInternet.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    response.data?.let { mAdapter.setData(it) }
                    hideShimmerEffect()
                    if (response.message == NETWORK_RESULT_MESSAGE_NO_RESULTS) {
                        Snackbar.make(
                            binding.root,
                            NETWORK_RESULT_MESSAGE_NO_RESULTS,
                            Snackbar.LENGTH_SHORT
                        ).setAction("Filters") {
                            binding.fabNoticeFilter.performClick()
                        }.show()

                        binding.tvError.text = NETWORK_RESULT_MESSAGE_NO_RESULTS
                        binding.llNoInternet.visibility = View.VISIBLE
                    }
                }

                is NetworkResult.Error -> {
                    binding.llNoInternet.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.tvError.text = response.message.toString()
                    loadDataFromCache()
                    hideShimmerEffect()
                    Snackbar.make(
                        binding.root,
                        response.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loading -> {
                    Log.d(LOG_TAG, "Notices Fragment: shimmer effect from is loading state")
                    showShimmerEffect()
                    binding.llNoInternet.visibility = View.GONE
                }
            }
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun requestNotices() {
        Log.d(LOG_TAG, "Notices Fragment: requestNotices")
        showShimmerEffect()
        mainViewModel.getAllNotices(noticesViewModel.applyQueries())
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readNotices.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    mAdapter.setData(database[0].notices)
                } else {
                    binding.llNoInternet.visibility = View.VISIBLE
                }
            }
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

    override fun onItemClick(object1: Any?, object2: Any?, object3: Any?, position: Int) {
        if (object1 is NoticeData) {
            Log.d(LOG_TAG, "NoticesFragment: onItemClick, Notice Card clicked")
            try {
                val action = NoticesFragmentDirections.actionNoticesFragmentToNoticeDetailActivity(
                    object1
                )
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.d(LOG_TAG, "NoticesFragment: onItemClick, exception in navigating to Notice Detail Activity, exception message: ${e.message}")
            }
        }
    }

}