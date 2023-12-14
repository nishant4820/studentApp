package com.nishant4820.studentapp.ui.home.fragments.noticefragment.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.nishant4820.studentapp.R
import com.nishant4820.studentapp.data.models.Society
import com.nishant4820.studentapp.data.models.SocietyDetails
import com.nishant4820.studentapp.databinding.BottomSheetNoticesBinding
import com.nishant4820.studentapp.utils.Constants.LOG_TAG
import com.nishant4820.studentapp.viewmodels.MainViewModel
import com.nishant4820.studentapp.viewmodels.NoticesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class NoticesBottomSheetFragment : BottomSheetDialogFragment() {
    private var societyId: String? = null
    private var societyChipId: Int? = null
    private var isUploadedByMe: Boolean? = null
    private var societyList: List<Society>? = null
    private var societyAdminDetails: SocietyDetails? = null
    private val noticesViewModel: NoticesViewModel by viewModels(ownerProducer = { requireActivity() })
    private val mainViewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() })
    private var _binding: BottomSheetNoticesBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = BottomSheetNoticesBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            mainViewModel.readSettings.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    val settingsResponse = database[0].settings
                    societyList = settingsResponse.societyList
                    societyAdminDetails = settingsResponse.societyDetails
                    Log.d(
                        LOG_TAG,
                        "NoticesBottomSheet: society list fetched, size = ${societyList!!.size}"
                    )
                    addChips(settingsResponse.societyList)
                    markSelectedSociety()
                    markIsUploadedByMe()
                }
            }
        }

        binding.societyChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.size > 0) {
                val selectedSocietyObject = group.findViewById<Chip>(checkedIds[0]).tag as Society
                societyId = selectedSocietyObject.id
                societyChipId = checkedIds[0]
                binding.uploadedByMeSwitch.isChecked = false
            } else {
                societyId = null
                societyChipId = null
            }
        }

        binding.uploadedByMeSwitch.setOnCheckedChangeListener { _, isChecked ->
            isUploadedByMe = if (isChecked) {
                binding.societyChipGroup.clearCheck()
                true
            } else null
        }

        binding.applyBtn.setOnClickListener {
            if (societyId != null && societyChipId != null) {
                noticesViewModel.saveSelectedSociety(societyId!!, societyChipId!!)
            } else {
                noticesViewModel.deleteSelectedSociety()
            }

            if (isUploadedByMe != null) {
                noticesViewModel.saveIsUploadedByMe(isUploadedByMe!!)
            } else {
                noticesViewModel.deleteIsUploadedByMe()
            }
            findNavController().navigate(R.id.action_noticesBottomSheetFragment_to_noticesFragment)
        }

        binding.uploadBtn.setOnClickListener {
            findNavController().navigate(R.id.action_noticesBottomSheetFragment_to_uploadNoticeActivity)
        }

        return binding.root
    }

    private fun addChips(societyList: List<Society>) {
        // Remove old chips added to ChipGroup
        binding.societyChipGroup.removeAllViews()
        // Add Chips to ChipGroup
        for (society in societyList) {
            addChip(society, binding.societyChipGroup)
        }
    }

    private fun addChip(society: Society, chipGroup: ChipGroup) {
        val chip = layoutInflater.inflate(R.layout.single_chip_layout, chipGroup, false) as Chip
        chip.text = society.name
        chip.tag = society
        chip.id = abs(society.id.hashCode())
        Log.d(
            LOG_TAG,
            "NoticesBottomSheet: addChip, society: ${chip.text}, id generated: ${chip.id}"
        )
        chipGroup.addView(chip)
    }

    private fun markSelectedSociety() {
        noticesViewModel.readSelectedSociety.asLiveData().observe(viewLifecycleOwner) { society ->
            this.societyId = society.societyId
            this.societyChipId = society.societyChipId
            if (this.societyChipId != null) {
                try {
                    binding.societyChipGroup.findViewById<Chip>(societyChipId!!).isChecked = true
                } catch (_: Exception) {
                }
            }
        }
    }

    private fun markIsUploadedByMe() {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}