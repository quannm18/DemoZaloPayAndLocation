package com.quannm18.demozalopay.model.network

data class UploadResponse(
    val `data`: Data,
    val status: Int,
    val success: Boolean
)