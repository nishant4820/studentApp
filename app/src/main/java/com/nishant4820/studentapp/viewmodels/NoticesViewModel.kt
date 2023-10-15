package com.nishant4820.studentapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nishant4820.studentapp.data.DataStoreRepository
import com.nishant4820.studentapp.utils.Constants.DEFAULT_LIMIT
import com.nishant4820.studentapp.utils.Constants.DEFAULT_OFFSET
import com.nishant4820.studentapp.utils.Constants.QUERY_LIMIT
import com.nishant4820.studentapp.utils.Constants.QUERY_OFFSET
import com.nishant4820.studentapp.utils.Constants.QUERY_SOCIETY
import com.nishant4820.studentapp.utils.Constants.QUERY_STUDENT_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticesViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    private var selectedSociety: String? = null
    private var studentId: String? = null
    private var isUploadedByMe: Boolean? = null

    val readSelectedSociety = dataStoreRepository.readSelectedSociety
    val readIsUploadedByMe = dataStoreRepository.readUploadedByMe

    fun saveSelectedSociety(selectedSociety: String, selectedSocietyChipId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveSelectedSociety(selectedSociety, selectedSocietyChipId)
        }
    }

    fun saveIsUploadedByMe(isUploadedByMe: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveIsUploadedByMe(isUploadedByMe)
        }
    }

    fun deleteSelectedSociety() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.deleteSelectedSociety()
        }
    }

    fun deleteIsUploadedByMe() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.deleteIsUploadedByMe()
        }
    }

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        viewModelScope.launch {
            readSelectedSociety.collect { society ->
                this@NoticesViewModel.selectedSociety = society.society
            }
        }
        viewModelScope.launch {
            readIsUploadedByMe.collect { isUploadedByMe ->
                this@NoticesViewModel.isUploadedByMe = isUploadedByMe
            }
        }

        if (selectedSociety != null) {
            queries[QUERY_SOCIETY] = selectedSociety!!
        }

        if (isUploadedByMe != null) {
//            queries[QUERY_STUDENT_ID] = studentId!!
            queries[QUERY_STUDENT_ID] = "65219da05ba04b4d50b19b0d"
        }

        queries[QUERY_OFFSET] = DEFAULT_OFFSET
        queries[QUERY_LIMIT] = DEFAULT_LIMIT

        return queries
    }

}