package com.nishant4820.studentapp.ui.home.fragments.noticefragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.nishant4820.studentapp.R
import com.nishant4820.studentapp.adapters.NoticesAdapter
import com.nishant4820.studentapp.databinding.FragmentNoticesBinding
import com.nishant4820.studentapp.utils.Constants.LOG_TAG
import com.nishant4820.studentapp.utils.NetworkListener
import com.nishant4820.studentapp.utils.NetworkResult
import com.nishant4820.studentapp.utils.observeOnce
import com.nishant4820.studentapp.viewmodels.MainViewModel
import com.nishant4820.studentapp.viewmodels.NoticesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoticesFragment : Fragment() {
    private val args: NoticesFragmentArgs by navArgs()
    private val mAdapter by lazy { NoticesAdapter() }
    private val mainViewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() })
    private val noticesViewModel: NoticesViewModel by viewModels(ownerProducer = { requireActivity() })
    private var _binding: FragmentNoticesBinding? = null
    private val binding get() = _binding!!
    private lateinit var networkListener: NetworkListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNoticesBinding.inflate(inflater, container, false)
        binding.shimmerViewContainer.startShimmer()
        binding.swipeRefreshLayout.setOnRefreshListener {
            requestNotices()
        }
        binding.fabNoticeFilter.setOnClickListener {
            if (noticesViewModel.networkStatus) {
                findNavController().navigate(R.id.action_noticesFragment_to_noticesBottomSheetFragment)
            } else {
                noticesViewModel.showNetworkStatus()
            }
        }

        setupRecyclerView()

        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                Log.d(LOG_TAG, "NoticesFragment: nestedScrollView end detected")
            }
        })

        noticesViewModel.readBackOnline.asLiveData().observe(viewLifecycleOwner) {
            noticesViewModel.backOnline = it
        }

        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext()).collect { status ->
                Log.d(LOG_TAG, "Notices Fragment: Network Status Observer, network status: $status")
                noticesViewModel.networkStatus = status
                noticesViewModel.showNetworkStatus()
                readDatabase()
            }
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        showShimmerEffect()
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.readNotices.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
                    Log.d(
                        LOG_TAG,
                        "Notices Fragment: Database is not empty, back from bottom sheet: ${args.backFromBottomSheet}"
                    )
                    mAdapter.setData(database[0].notices)
                    hideShimmerEffect()
                } else {
                    Log.d(
                        LOG_TAG,
                        "Notices Fragment: Database is empty or back from bottom sheet: ${args.backFromBottomSheet}"
                    )
                    requestNotices()
                }
            }
        }
    }

    private fun requestNotices() {
        Log.d(LOG_TAG, "Notices Fragment: requestNotices")
        mainViewModel.getAllNotices(noticesViewModel.applyQueries())
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
                }

                is NetworkResult.Error -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    loadDataFromCache()
                    hideShimmerEffect()
                    binding.tvError.text = response.message.toString()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loading -> {
                    Log.d(LOG_TAG, "Notices Fragment: shimmer effect from is loading state")
                    showShimmerEffect()
                    binding.llNoInternet.visibility = View.GONE
                }
            }
        }
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

}