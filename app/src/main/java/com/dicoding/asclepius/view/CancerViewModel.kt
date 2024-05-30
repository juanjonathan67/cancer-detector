package com.dicoding.asclepius.view

import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.CancerRepository

class CancerViewModel (private val cancerRepository: CancerRepository) : ViewModel() {
    fun getCancer() = cancerRepository.getCancer()

    fun insertCancer(imageUri: String, result: String) = cancerRepository.insertCancer(imageUri, result)
}