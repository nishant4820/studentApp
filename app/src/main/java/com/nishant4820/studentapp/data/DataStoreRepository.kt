package com.nishant4820.studentapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_BACK_ONLINE
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_IS_UPLOADED_BY_ME
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_SOCIETY
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_SOCIETY_CHIP_ID
import com.nishant4820.studentapp.utils.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

@ActivityRetainedScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferenceKeys {
        val selectedSocietyId = stringPreferencesKey(PREFERENCES_SOCIETY)
        val selectedSocietyChipId = intPreferencesKey(PREFERENCES_SOCIETY_CHIP_ID)
        val isUploadedByMe = booleanPreferencesKey(PREFERENCES_IS_UPLOADED_BY_ME)
        val backOnline = booleanPreferencesKey(PREFERENCES_BACK_ONLINE)
    }

    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun saveSelectedSociety(
        societyID: String,
        societyChipId: Int
    ) {
        dataStore.edit { settings ->
            settings[PreferenceKeys.selectedSocietyId] = societyID
            settings[PreferenceKeys.selectedSocietyChipId] = societyChipId
        }
    }

    suspend fun saveIsUploadedByMe(
        isUploadedByMe: Boolean
    ) {
        dataStore.edit { settings ->
            settings[PreferenceKeys.isUploadedByMe] = isUploadedByMe
        }
    }

    suspend fun saveBackOnline(
        backOnline: Boolean
    ) {
        dataStore.edit { settings ->
            settings[PreferenceKeys.backOnline] = backOnline
        }
    }


    val readSelectedSocietyLocal: Flow<SocietyLocal> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val society = preferences[PreferenceKeys.selectedSocietyId]
            val societyChipId = preferences[PreferenceKeys.selectedSocietyChipId]
            SocietyLocal(society, societyChipId)
        }

    val readUploadedByMe: Flow<Boolean?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferenceKeys.isUploadedByMe]
        }

    val readBackOnline: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferenceKeys.backOnline] ?: false
        }

    suspend fun deleteSelectedSociety() {
        dataStore.edit { settings ->
            settings.remove(PreferenceKeys.selectedSocietyId)
            settings.remove(PreferenceKeys.selectedSocietyChipId)
        }
    }

    suspend fun deleteIsUploadedByMe() {
        dataStore.edit { settings ->
            settings.remove(PreferenceKeys.isUploadedByMe)
        }
    }

}


data class SocietyLocal(
    val societyId: String?,
    val societyChipId: Int?
)