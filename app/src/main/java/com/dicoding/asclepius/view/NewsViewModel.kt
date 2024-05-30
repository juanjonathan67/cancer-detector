package com.dicoding.asclepius.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.CancerRepository
import com.dicoding.asclepius.data.Result
import com.dicoding.asclepius.data.local.entity.NewsEntity

class NewsViewModel (private val cancerRepository: CancerRepository) : ViewModel() {
    fun getNews() : LiveData<Result<List<NewsEntity>>> = cancerRepository.getAllNews()
}