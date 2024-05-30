package com.dicoding.asclepius.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.asclepius.data.local.entity.CancerEntity
import com.dicoding.asclepius.data.local.entity.NewsEntity
import com.dicoding.asclepius.data.local.room.CancerDao
import com.dicoding.asclepius.data.remote.response.NewsResponse
import com.dicoding.asclepius.data.remote.retrofit.ApiService
import com.dicoding.asclepius.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CancerRepository private constructor(
    private val apiService: ApiService,
    private val cancerDao: CancerDao,
    private val appExecutors: AppExecutors
){
    private val _cancerResult: MutableLiveData<Result<List<CancerEntity>>> by lazy { MutableLiveData<Result<List<CancerEntity>>>() }
    private val cancerResult: LiveData<Result<List<CancerEntity>>> = _cancerResult
    private val _newsResult: MutableLiveData<Result<List<NewsEntity>>> by lazy { MutableLiveData<Result<List<NewsEntity>>>() }
    private val newsResult: LiveData<Result<List<NewsEntity>>> = _newsResult

    fun getAllNews() : LiveData<Result<List<NewsEntity>>> {
        _newsResult.postValue(Result.Loading)
        val client = apiService.getCancerNews()
        client.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    if(response.body() != null) {
                        val articles = response.body()!!.articles
                        if (articles != null) {
                            appExecutors.diskIO.execute {
                                val listArticle: ArrayList<NewsEntity> = arrayListOf()
                                for (article in articles) {
                                    val newArticle = NewsEntity (
                                        title = article?.title,
                                        publisher = article?.source?.name,
                                        url = article?.url,
                                        imageUrl = article?.urlToImage
                                    )
                                    listArticle.add(newArticle)
                                }
                                _newsResult.postValue(Result.Success(listArticle))
                            }
                        } else {
                            _newsResult.postValue(Result.Error("No News"))
                        }
                    } else {
                        _newsResult.postValue(Result.Error("Response is null"))
                    }
                } else {
                    _newsResult.postValue(Result.Error(response.message()))
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                _newsResult.postValue(Result.Error(t.message.toString()))
            }
        })
        return newsResult
    }

    fun insertCancer(imageUri: String, result: String){
        val newCancer = CancerEntity(
            imageUri = imageUri,
            result = result
        )
        appExecutors.diskIO.execute {
            cancerDao.insertCancer(newCancer)
            _cancerResult.postValue(Result.Success(cancerDao.getAllCancer()))
        }
    }

    fun getCancer(): LiveData<Result<List<CancerEntity>>> {
        appExecutors.diskIO.execute {
            _cancerResult.postValue(Result.Success(cancerDao.getAllCancer()))
        }
        return cancerResult
    }

    companion object {
        @Volatile
        private var instance: CancerRepository? = null
        fun getInstance(
            apiService: ApiService,
            cancerDao: CancerDao,
            appExecutors: AppExecutors
        ): CancerRepository =
            instance ?: synchronized(this) {
                instance ?: CancerRepository(apiService, cancerDao, appExecutors)
            }.also { instance = it }
    }
}