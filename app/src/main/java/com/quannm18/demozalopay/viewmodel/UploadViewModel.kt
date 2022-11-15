package com.quannm18.demozalopay.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.quannm18.demozalopay.network.UploadRepository
import com.quannm18.demozalopay.utils.Resource
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import kotlinx.coroutines.launch
import okhttp3.RequestBody

class UploadViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository: UploadRepository by lazy {
        UploadRepository.newInstance(application)
    }
    private val listener: MutableLiveData<Any> = MutableLiveData()
    val event: LiveData<Any> by lazy {
        listener
    }

    suspend fun upload(image: MultipartBody.Part, key: RequestBody) = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mRepository.upload(image,key)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message ?: "Error"))
        }
    }
}