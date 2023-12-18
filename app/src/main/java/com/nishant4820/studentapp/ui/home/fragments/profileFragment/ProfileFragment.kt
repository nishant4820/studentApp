package com.nishant4820.studentapp.ui.home.fragments.profileFragment

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
import com.google.android.material.snackbar.Snackbar
import com.nishant4820.studentapp.data.models.StudentProfileResponse
import com.nishant4820.studentapp.databinding.FragmentProfileBinding
import com.nishant4820.studentapp.ui.login.LoginActivity
import com.nishant4820.studentapp.utils.Constants
import com.nishant4820.studentapp.utils.NetworkListener
import com.nishant4820.studentapp.utils.NetworkResult
import com.nishant4820.studentapp.utils.NetworkUtils
import com.nishant4820.studentapp.utils.openActivity
import com.nishant4820.studentapp.viewmodels.ProfileViewModel
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModels(ownerProducer = { requireActivity() })
    private var isFirstNetworkCallback = true
    private lateinit var networkListener: NetworkListener
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                requestStudentProfile()
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
                    Constants.LOG_TAG,
                    "Profile Fragment: Network Status Observer, network status: $networkStatus"
                )
                profileViewModel.networkStatus = networkStatus
//                resultsViewModel.showNetworkStatus()
                if (networkStatus) {
                    requestStudentProfile()
                } else if (isFirstNetworkCallback) {
                    binding.tvError.text = Constants.NETWORK_RESULT_MESSAGE_NO_INTERNET
//                    loadDataFromCache()
                }
                isFirstNetworkCallback = false
            }
        }

        profileViewModel.profileResponse.observe(viewLifecycleOwner) { response ->
            Log.d(
                Constants.LOG_TAG,
                "Profile Fragment: Student Profile response observer, response code: ${response.statusCode}"
            )
            when (response) {
                is NetworkResult.Success -> {
                    binding.llNoInternet.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    response.data?.let {
                        renderData(it)
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

        binding.apply {
            val packageInfo =
                requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            val versionName = packageInfo.versionName
            tvBuildVersion.text = versionName

            btnLogout.setOnClickListener {
                Hawk.deleteAll()
                requireActivity().openActivity<LoginActivity>()
                requireActivity().finishAffinity()
                profileViewModel.clearAllTables()
                profileViewModel.clearAllPreferences()
            }
        }

        return binding.root
    }

    private fun requestStudentProfile() {
        Log.d(Constants.LOG_TAG, "Profile Fragment: requestStudentProfile")
        profileViewModel.getStudentProfile()
    }

    private fun renderData(studentProfileResponse: StudentProfileResponse) {
        binding.apply {
            with(studentProfileResponse) {
                clContent.visibility = View.VISIBLE
                tvTitleName.text = name
                tvTitleBatch.text = String.format("Batch of %d", batch)
                tvEnrollment.text = enrollment.toString()
                tvName.text = name
                tvBatch.text = batch.toString()
                tvBranchSection.text = String.format("%s-%s", branch, section)
                if (isSocietyAdmin && society != null) {
                    llSocietyContainer.visibility = View.VISIBLE
                    tvSociety.text = society.name
                } else llSocietyContainer.visibility = View.GONE
                tvPhone.text = phone
                tvAddress.text = address
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}