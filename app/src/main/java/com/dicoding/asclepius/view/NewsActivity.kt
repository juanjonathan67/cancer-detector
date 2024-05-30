package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.Result
import com.dicoding.asclepius.data.local.entity.NewsEntity
import com.dicoding.asclepius.databinding.ActivityNewsBinding
import com.dicoding.asclepius.utils.ViewModelFactory

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private val newsViewModel by viewModels<NewsViewModel> { ViewModelFactory.getInstance(this@NewsActivity) }
    private val listNewsAdapter = ListNewsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.addImage -> {
                    val mainActivityIntent = Intent(this@NewsActivity, MainActivity::class.java)
                    mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(mainActivityIntent)
                    true
                }
                R.id.history -> {
                    startActivity(Intent(this@NewsActivity, HistoryActivity::class.java))
                    true
                }
                R.id.news -> {
                    true
                }
                else -> false
            }
        }

        binding.rvNews.layoutManager = LinearLayoutManager(this@NewsActivity)

        newsViewModel.getNews().observe(this@NewsActivity) {result ->
            if (result != null) {
                when (result) {
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@NewsActivity, "Inference History Error", Toast.LENGTH_SHORT).show()
                        Log.e("History Activity", result.error)
                    }
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        listNewsAdapter.submitList(result.data)
                        binding.rvNews.adapter = listNewsAdapter

                        listNewsAdapter.setOnItemClickCallback (object : ListNewsAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: NewsEntity) {
                                val webpage = Uri.parse(data.url)
                                val webIntent = Intent(Intent.ACTION_VIEW, webpage)
                                startActivity(webIntent)
                            }
                        })

                    }
                }
            }
        }
    }
}