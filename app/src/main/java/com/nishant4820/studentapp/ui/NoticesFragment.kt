package com.nishant4820.studentapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nishant4820.studentapp.adapters.NoticesAdapter
import com.nishant4820.studentapp.databinding.FragmentNoticesBinding
import com.nishant4820.studentapp.utils.Constants.ARG_PARAM1
import com.nishant4820.studentapp.utils.Constants.ARG_PARAM2
import com.nishant4820.studentapp.utils.NetworkResult
import com.nishant4820.studentapp.utils.observeOnce
import com.nishant4820.studentapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoticesFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val mAdapter by lazy { NoticesAdapter() }
    private val mainViewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() })
    private var _binding: FragmentNoticesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNoticesBinding.inflate(inflater, container, false)
        setupRecyclerView()
        // Used to read local database to set up data if available
        readDatabase()
        // Used to fetch data from remote even if data is available to update results
        requestNotices()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.shimmerViewContainer.startShimmer()
        binding.swipeRefreshLayout.setOnRefreshListener {
            requestNotices()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        showShimmerEffect()
    }

    private fun readDatabase() {
        Log.d("Nishant", "NoticesFragment: readDatabase() called")
        lifecycleScope.launch {
            mainViewModel.readNotices.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    Log.d("Nishant", "NoticesFragment: database is not empty")
                    mAdapter.setData(database[0].notices)
                    hideShimmerEffect()
                }
            }
        }
    }

    private fun requestNotices() {
        mainViewModel.getAllNotices()
        mainViewModel.noticesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    binding.llNoInternet.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    response.data?.let { mAdapter.setData(it) }
                }

                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    binding.swipeRefreshLayout.isRefreshing = false
                    loadDataFromCache()
//                    binding.tvError.text = response.message.toString()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loading -> {
//                    showShimmerEffect()
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

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NoticesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}