package com.nishant4820.studentapp.ui.home.fragments.noticefragment.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.nishant4820.studentapp.databinding.BottomSheetNoticesBinding
import com.nishant4820.studentapp.utils.Constants.ARG_PARAM1
import com.nishant4820.studentapp.utils.Constants.ARG_PARAM2
import com.nishant4820.studentapp.viewmodels.NoticesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticesBottomSheetFragment : BottomSheetDialogFragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var society: String? = null
    private var societyChipId: Int? = null
    private var isUploadedByMe: Boolean? = null
    private val noticesViewModel: NoticesViewModel by viewModels(ownerProducer = { requireActivity() })
    private var _binding: BottomSheetNoticesBinding? = null
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
    ): View? {
        // Inflate the layout for this fragment
        _binding = BottomSheetNoticesBinding.inflate(inflater, container, false)

        noticesViewModel.readSelectedSociety.asLiveData().observe(viewLifecycleOwner) { society ->
            this.society = society.society
            this.societyChipId = society.societyChipId
            if (this.societyChipId != null) {
                try {
                    binding.societyChipGroup.findViewById<Chip>(societyChipId!!).isChecked = true
                } catch (_: Exception) {
                }
            }
        }

        noticesViewModel.readIsUploadedByMe.asLiveData()
            .observe(viewLifecycleOwner) { isUploadedByMe ->
                this.isUploadedByMe = isUploadedByMe
                if (this.isUploadedByMe != null) {
                    try {
                        binding.uploadedByMeSwitch.isChecked = isUploadedByMe!!
                    } catch (_: Exception) {
                    }
                }
            }

        binding.societyChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.size > 0) {
                society = group.findViewById<Chip>(checkedIds[0]).text.toString()
                societyChipId = checkedIds[0]
            } else {
                society = null
                societyChipId = null
            }
        }

        binding.uploadedByMeSwitch.setOnCheckedChangeListener { switch, isChecked ->
            isUploadedByMe = if (isChecked) true
            else null
        }

        binding.applyBtn.setOnClickListener {
            if (society != null && societyChipId != null) {
                noticesViewModel.saveSelectedSociety(society!!, societyChipId!!)
            } else {
                noticesViewModel.deleteSelectedSociety()
            }

            if (isUploadedByMe != null) {
                noticesViewModel.saveIsUploadedByMe(isUploadedByMe!!)
            } else {
                noticesViewModel.deleteIsUploadedByMe()
            }
            val action =
                NoticesBottomSheetFragmentDirections.actionNoticesBottomSheetFragmentToNoticesFragment(
                    true
                )
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NoticesBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}