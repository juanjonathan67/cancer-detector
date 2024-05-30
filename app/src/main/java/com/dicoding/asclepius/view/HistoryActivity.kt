package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.Result
import com.dicoding.asclepius.data.local.entity.CancerEntity
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.utils.ViewModelFactory

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private val cancerViewModel by viewModels<CancerViewModel> { ViewModelFactory.getInstance(this@HistoryActivity) }
    private val listCancerAdapter = ListCancerAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.addImage -> {
                    val mainActivityIntent = Intent(this@HistoryActivity, MainActivity::class.java)
                    mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(mainActivityIntent)
                    true
                }
                R.id.history -> {
                    true
                }
                R.id.news -> {
                    startActivity(Intent(this@HistoryActivity, NewsActivity::class.java))
                    true
                }
                else -> false
            }
        }

        binding.rvHistory.layoutManager = GridLayoutManager(this@HistoryActivity, 2)

        cancerViewModel.getCancer().observe(this@HistoryActivity) { result ->
            if (result != null) {
                when(result) {
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@HistoryActivity, "Inference History Error", Toast.LENGTH_SHORT).show()
                        Log.e("History Activity", result.error)
                    }
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        listCancerAdapter.submitList(result.data)
                        binding.rvHistory.adapter = listCancerAdapter

                        listCancerAdapter.setOnItemClickCallback (object : ListCancerAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: CancerEntity) {
                                val resultIntent = Intent(this@HistoryActivity, ResultActivity::class.java)
                                resultIntent.putExtra(ResultActivity.SEND_IMAGE_DATA, data.imageUri)
                                resultIntent.putExtra(ResultActivity.SEND_RESULT, data.result)
                                startActivity(resultIntent)
                            }
                        })
                    }
                }
            }
        }

    }
}