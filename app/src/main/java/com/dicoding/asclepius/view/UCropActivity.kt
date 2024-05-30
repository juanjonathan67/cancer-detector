package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityUcropBinding
import com.yalantis.ucrop.UCrop
import java.io.File
import java.lang.StringBuilder
import java.util.UUID

class UCropActivity : AppCompatActivity() {
    private lateinit var sourceUri : String
    private lateinit var destinationUri : String
    private lateinit var uri: Uri

    private lateinit var binding: ActivityUcropBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUcropBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.extras != null) {
            sourceUri = intent.getStringExtra(SEND_IMAGE_DATA) ?: "NA"
            uri = Uri.parse(sourceUri)
        }

        destinationUri = StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString()

        val ucropOptions = UCrop.Options()

        UCrop.of(uri, Uri.fromFile(File(cacheDir, destinationUri)))
            .withOptions(ucropOptions)
            .withAspectRatio(1F, 1F)
            .withMaxResultSize(2000, 2000)
            .start(this@UCropActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = data?.let { UCrop.getOutput(it) }

            val resultIntent = Intent()
            resultIntent.putExtra(CROP_RESULT, "$resultUri")
            setResult(101, resultIntent)
            finish()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = data?.let { UCrop.getError(it) }
        }
    }

    companion object {
        const val SEND_IMAGE_DATA = "SendImageData"
        const val CROP_RESULT = "CropResult"
    }
}