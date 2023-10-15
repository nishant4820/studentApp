package com.nishant4820.studentapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object: Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer.onChanged(value)
        }
    })
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.PREFERENCES_NAME)
