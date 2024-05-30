package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toUri
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.entity.CancerEntity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.utils.ViewModelFactory
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.NumberFormat

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var imageUri: Uri
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var result: String? = null
    private val cancerViewModel by viewModels<CancerViewModel> { ViewModelFactory.getInstance(this@ResultActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.addImage -> {
                    val mainActivityIntent = Intent(this@ResultActivity, MainActivity::class.java)
                    mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(mainActivityIntent)
                    true
                }
                R.id.history -> {
                    startActivity(Intent(this@ResultActivity, HistoryActivity::class.java))
                    true
                }
                R.id.news -> {
                    startActivity(Intent(this@ResultActivity, NewsActivity::class.java))
                    true
                }
                else -> false
            }
        }

        if (intent.extras != null) {
            imageUri = intent.getStringExtra(SEND_IMAGE_DATA)?.toUri() ?: "NA".toUri()
            result = intent.getStringExtra(SEND_RESULT)
        }

        binding.resultImage.setImageURI(imageUri)

        if (result == null) {
            imageClassifierHelper = ImageClassifierHelper(
                context = this@ResultActivity,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        runOnUiThread {
                            Toast.makeText(this@ResultActivity, error, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onResults(results: List<Classifications>?) {
                        runOnUiThread {
                            results?.let { it ->
                                Log.d("Result Activity", it.toString())
                                if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                    val result = NumberFormat.getPercentInstance().format(it[0].categories[0].score).toString().trim()
                                    binding.resultText.text = "Cancer : $result"
                                    cancerViewModel.insertCancer(imageUri = imageUri.toString(), result = result)
                                } else {
                                    binding.resultText.text = ""
                                }
                            }
                        }
                    }
                }
            )

            imageClassifierHelper.classifyStaticImage(imageUri)
        } else {
            binding.resultText.text = "Cancer : $result"
        }
    }

    companion object {
        const val SEND_IMAGE_DATA = "SendImageData"
        const val SEND_RESULT = "SendResult"
    }
}