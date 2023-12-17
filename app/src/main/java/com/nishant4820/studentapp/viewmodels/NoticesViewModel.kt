package com.nishant4820.studentapp.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nishant4820.studentapp.data.DataStoreRepository
import com.nishant4820.studentapp.utils.Constants.DEFAULT_LIMIT
import com.nishant4820.studentapp.utils.Constants.DEFAULT_OFFSET
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_NO_INTERNET
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_ID
import com.nishant4820.studentapp.utils.Constants.QUERY_LIMIT
import com.nishant4820.studentapp.utils.Constants.QUERY_OFFSET
import com.nishant4820.studentapp.utils.Constants.QUERY_SOCIETY
import com.nishant4820.studentapp.utils.Constants.QUERY_STUDENT_ID
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticesViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    private var selectedSocietyId: String? = null
    private var isUploadedByMe: Boolean? = null

    var networkStatus = false

    // Back Online is used to check if user if coming back to online state from offline state or the user was originally online.
    var backOnline = false

    val readSelectedSociety = dataStoreRepository.readSelectedSocietyLocal
    val readIsUploadedByMe = dataStoreRepository.readUploadedByMe
    val readBackOnline = dataStoreRepository.readBackOnline

    fun saveSelectedSociety(selectedSocietyId: String, selectedSocietyChipId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveSelectedSociety(selectedSocietyId, selectedSocietyChipId)
        }
    }

    fun saveIsUploadedByMe(isUploadedByMe: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveIsUploadedByMe(isUploadedByMe)
        }
    }

    private fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
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
                this@NoticesViewModel.selectedSocietyId = society.societyId
            }
        }
        viewModelScope.launch {
            readIsUploadedByMe.collect { isUploadedByMe ->
                this@NoticesViewModel.isUploadedByMe = isUploadedByMe
            }
        }

        if (selectedSocietyId != null) {
            queries[QUERY_SOCIETY] = selectedSocietyId!!
        }

        if (isUploadedByMe != null) {
            val studentId = Hawk.get<String>(PREFERENCES_ID)
            queries[QUERY_STUDENT_ID] = studentId
        }

//        queries[QUERY_OFFSET] = DEFAULT_OFFSET
//        queries[QUERY_LIMIT] = DEFAULT_LIMIT

        return queries
    }

    fun showNetworkStatus() {
        if (networkStatus) {
            if (backOnline) {
                Toast.makeText(getApplication(), "We're back online.", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        } else {
            Toast.makeText(getApplication(), NETWORK_RESULT_MESSAGE_NO_INTERNET, Toast.LENGTH_SHORT)
                .show()
            saveBackOnline(true)
        }
    }

}