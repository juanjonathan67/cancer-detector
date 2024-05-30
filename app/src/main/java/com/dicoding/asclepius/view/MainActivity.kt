package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null

    private lateinit var cropImage: ActivityResultLauncher<String>

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == 101) {
            val cropResult = result.data?.getStringExtra(UCropActivity.CROP_RESULT)
            currentImageUri = result.data?.data

            if (cropResult != null) {
                currentImageUri = Uri.parse(cropResult)
            }

            showImage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cropImage = registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            val cropIntent = Intent(this@MainActivity, UCropActivity::class.java)
            cropIntent.putExtra(UCropActivity.SEND_IMAGE_DATA, result.toString())
            startForResult.launch(cropIntent)
        }

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.analyzeButton.setOnClickListener {
            analyzeImage()
        }

        binding.topAppBar.navigationIcon = null

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.addImage -> {
                    true
                }
                R.id.history -> {
                    startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
                    true
                }
                R.id.news -> {
                    startActivity(Intent(this@MainActivity, NewsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun startGallery() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        Dexter.withContext(this@MainActivity)
            .withPermission(permission)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    cropImage.launch("image/*")
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    showToast("Permission Denied")
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissionRequest: PermissionRequest?,
                    permissionToken: PermissionToken?
                ) {
                    permissionToken?.continuePermissionRequest()
                }

            }).check()
    }

    private fun showImage() {
        binding.previewImageView.setImageURI(currentImageUri)
    }

    private fun analyzeImage() {
        moveToResult()
    }

    private fun moveToResult() {
        val resultIntent = Intent(this@MainActivity, ResultActivity::class.java)
        resultIntent.putExtra(ResultActivity.SEND_IMAGE_DATA, currentImageUri.toString())
        startActivity(resultIntent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
