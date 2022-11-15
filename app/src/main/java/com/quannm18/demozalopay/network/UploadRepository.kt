package com.quannm18.demozalopay.network

import android.app.Application
import com.quannm18.demozalopay.network.api.APIConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadRepository(application: Application) {
    companion object{
        var instance : UploadRepository? = null
        fun newInstance(application: Application): UploadRepository {
            if (instance==null){
                instance = UploadRepository(application)
            }
            return instance!!
        }
    }

    suspend fun upload(image: MultipartBody.Part, key: RequestBody) = APIConfig.apiService.uploadImage(image)
}