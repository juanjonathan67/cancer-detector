package com.dicoding.asclepius.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.data.CancerRepository
import com.dicoding.asclepius.di.Injection
import com.dicoding.asclepius.view.CancerViewModel
import com.dicoding.asclepius.view.NewsViewModel

class ViewModelFactory private constructor (private val cancerRepository: CancerRepository)
    : ViewModelProvider.NewInstanceFactory() {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        NewsViewModel::class.java -> NewsViewModel(cancerRepository)
        CancerViewModel::class.java -> CancerViewModel(cancerRepository)
        else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    } as T

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }

}