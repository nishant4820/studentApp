package com.nishant4820.studentapp.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import coil.load
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
        try {
            val urlConnection = URL(pdfUrl).openConnection() as HttpURLConnection
            urlConnection.connect()
            val pdDocument = PDDocument.load(urlConnection.inputStream)
            var pageView = PDFRenderer(pdDocument).renderImage(0, 1F, ImageType.RGB)
            val height = min(pageView.height, pageView.width * 9 / 16)
            pageView = Bitmap.createBitmap(pageView, 0, 0, pageView.width, height)
            pdDocument.close()
            urlConnection.disconnect()
            withContext(Dispatchers.Main) {
                imageView.load(pageView)
            }
        } catch (_: Exception) {
            imageView.load(R.drawable.ic_pdf)
        }
    }
}

fun convertPxToDp(context: Context, px: Float): Float {
    return px / context.resources.displayMetrics.density
}

fun convertDpToPx(context: Context, dp: Float): Float {
    return dp * context.resources.displayMetrics.density
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

/**
 * Inline function to open activity without parameter
 */
inline fun <reified T : AppCompatActivity> Context.openActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

/**
 * Inline function to open activity with parameter
 */
inline fun <reified T : AppCompatActivity> Context.openActivity(bundle: Bundle) {
    val intent = Intent(this, T::class.java).apply {
        putExtra("bundle", bundle)
    }
    startActivity(intent)
}
