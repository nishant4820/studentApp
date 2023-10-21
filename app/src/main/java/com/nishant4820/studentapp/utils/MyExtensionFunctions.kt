package com.nishant4820.studentapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import coil.load
import coil.transform.RoundedCornersTransformation
import com.github.satoshun.coroutine.autodispose.view.autoDisposeScope
import com.nishant4820.studentapp.R
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.ImageType
import com.tom_roush.pdfbox.rendering.PDFRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.min

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer.onChanged(value)
        }
    })
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.PREFERENCES_NAME)


fun loadPdf(imageView: ImageView, pdfUrl: String) {
    imageView.autoDisposeScope.launch(Dispatchers.IO) {
        val urlConnection = URL(pdfUrl).openConnection() as HttpURLConnection
        urlConnection.connect()
        val pdDocument = PDDocument.load(urlConnection.inputStream)
        var pageView = PDFRenderer(pdDocument).renderImage(0, 1F, ImageType.RGB)
        val height = min(pageView.height, pageView.width * 9 / 16)
        pageView = Bitmap.createBitmap(pageView, 0, 0, pageView.width, height)
        pdDocument.close()
        urlConnection.disconnect()
        withContext(Dispatchers.Main) {
            imageView.load(pageView) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
                transformations(RoundedCornersTransformation(12f, 12f, 12f, 12f))
            }
        }
    }
}
