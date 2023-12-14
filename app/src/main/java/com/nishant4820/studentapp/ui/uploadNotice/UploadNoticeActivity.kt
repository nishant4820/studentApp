package com.nishant4820.studentapp.ui.uploadNotice

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.storage.component1
import com.google.firebase.storage.component2
import com.google.firebase.storage.storage
import com.nishant4820.studentapp.R
import com.nishant4820.studentapp.data.models.NoticeData
import com.nishant4820.studentapp.data.models.NoticeFile
import com.nishant4820.studentapp.databinding.ActivityUploadNoticeBinding
import com.nishant4820.studentapp.utils.Constants.LOG_TAG
import com.nishant4820.studentapp.utils.Constants.MESSAGE_FILE_UPLOADED
import com.nishant4820.studentapp.utils.Constants.MESSAGE_NOTICE_UPLOADED
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_UNKNOWN
import com.nishant4820.studentapp.utils.NetworkResult
import com.nishant4820.studentapp.viewmodels.UploadNoticeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UploadNoticeActivity : AppCompatActivity() {

    private val uploadNoticeViewModel: UploadNoticeViewModel by viewModels()
    private lateinit var binding: ActivityUploadNoticeBinding
    private lateinit var uploadButton: Button
    private lateinit var pdfPickerLauncher: ActivityResultLauncher<Intent>
    private var fileUri: Uri? = null
    private var noticeFile: NoticeFile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUploadNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        pdfPickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                uploadButton.text = resources.getString(R.string.upload)
                if (activityResult.resultCode == Activity.RESULT_OK) {
                    fileUri = activityResult.data?.data
                    uploadButton.isEnabled = true
                } else {
                    fileUri = null
                    uploadButton.isEnabled = false
                }
            }

        binding.btnChoose.setOnClickListener {
            selectPdf()
        }

        uploadButton = binding.btnUpload

        uploadButton.setOnClickListener {
            fileUri?.let { uploadFile(it) }
        }

        binding.btnUploadNotice.setOnClickListener {
            if (noticeFile == null) return@setOnClickListener
            val noticeDataRequestBody = NoticeData(
                name = binding.name.text.toString(),
                description = binding.description.text.toString(),
                noticeType = "pdf",
                noticeFile = noticeFile!!,
                date = "29/11/2023"
            )
            uploadNoticeViewModel.postNotice(noticeDataRequestBody)
        }

        uploadNoticeViewModel.postNoticeResponse.observe(this) { response ->
            Log.d(
                LOG_TAG,
                "Upload Notice Activity: Upload Notice response observer, response code: ${response.statusCode}"
            )
            when (response) {
                is NetworkResult.Success -> {
                    Log.d(
                        LOG_TAG,
                        "Upload Notice Activity: Upload Notice response observer, response state: success"
                    )
                    Toast.makeText(this, MESSAGE_NOTICE_UPLOADED, Toast.LENGTH_SHORT).show()
                    finish()
                }

                is NetworkResult.Error -> {
                    val message = response.message ?: NETWORK_RESULT_MESSAGE_UNKNOWN
                    Log.d(
                        LOG_TAG,
                        "Upload Notice Activity: Upload Notice response observer, response state: error, message: $message"
                    )
                    Snackbar.make(binding.btnUploadNotice, message, Snackbar.LENGTH_SHORT)
                        .setAction("OK", null).show()
                }

                is NetworkResult.Loading -> {
                    Log.d(
                        LOG_TAG,
                        "Upload Notice Activity: Upload Notice response observer, response state: loading"
                    )
                }
            }
        }

    }

    private fun selectPdf() {
        val pdfIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        pdfIntent.type = "application/pdf"
        pdfPickerLauncher.launch(pdfIntent)
    }

    private fun uploadFile(file: Uri) {
        val storage = Firebase.storage
        val storageRef = storage.reference.child("notices/${file.lastPathSegment}")
        val uploadTask = storageRef.putFile(file)
        uploadButton.text = resources.getString(R.string.uploading)
        uploadButton.isEnabled = false
        uploadTask.addOnSuccessListener {
            Log.d(LOG_TAG, "File Uploaded: Name: ${storageRef.name}")
            Log.d(LOG_TAG, "File Uploaded: Path: ${storageRef.path}")
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                Log.d(LOG_TAG, "File Uploaded: Download url: $downloadUri")
                noticeFile = NoticeFile(storageRef.name, downloadUri.toString(), storageRef.path)
                Snackbar.make(uploadButton, MESSAGE_FILE_UPLOADED, Snackbar.LENGTH_SHORT).show()
                uploadButton.text = resources.getString(R.string.uploaded)
                uploadButton.isEnabled = false
            }
        }
        uploadTask.addOnProgressListener { (bytesTransferred, totalByteCount) ->
            val progress = (100.0 * bytesTransferred) / totalByteCount
            Log.d(LOG_TAG, "Upload is $progress% done")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}