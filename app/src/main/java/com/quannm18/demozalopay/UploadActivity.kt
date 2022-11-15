package com.quannm18.demozalopay

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.quannm18.demozalopay.databinding.ActivityUploadBinding
import com.quannm18.demozalopay.utils.Status
import com.quannm18.demozalopay.viewmodel.UploadViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class UploadActivity : AppCompatActivity() {
    lateinit var binding: ActivityUploadBinding
    lateinit var imgUri: Uri
    private val mViewModel: UploadViewModel by viewModels()
    private val contract = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imgUri = it!!
        binding.imgPick.setImageURI(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_upload)
        initData()
        initView()
        listenerLiveData()
        listener()
    }

    private fun initData() {

    }

    private fun initView() {

    }

    private fun listenerLiveData() {

    }

    private fun listener() {
        binding.imgPick.setOnClickListener {
            contract.launch("image/*")
        }

        binding.btnUpload.setOnClickListener {
            upload()
        }
    }

    fun upload() {
        val filesDir = applicationContext.filesDir
        val file = File(filesDir, "image.png")

        val inputStream = contentResolver.openInputStream(imgUri)
        val outputStream = FileOutputStream(file)
        inputStream!!.copyTo(outputStream)

        val requesrBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("upload", file.name, requesrBody)
        val key: RequestBody =
            getString(R.string.key).toRequestBody("text/plain".toMediaTypeOrNull())
        lifecycleScope.launch {
            mViewModel.upload(image = part, key).observe(this@UploadActivity) {
                when (it.status) {
                    Status.SUCCESS -> {
                        Glide.with(this@UploadActivity).load(it.data!!.data.url)
                            .into(binding.imgPick)
                        Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
                        binding.btnUpload.setText("Success")
                        Log.e(javaClass.simpleName, "upload: ${it.message}")
                    }
                    Status.ERROR -> {
                        Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                        Log.e(javaClass.simpleName, "upload: ${it.message}")
                    }
                    Status.LOADING -> {
                        Toast.makeText(applicationContext, "Loading", Toast.LENGTH_SHORT).show()
                        Log.e(javaClass.simpleName, "upload: ${it.message}")
                    }
                }
            }
        }
    }
}